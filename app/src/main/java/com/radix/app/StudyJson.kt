package com.radix.app

/** Small, bounded JSON codec for portable offline backups; independent of Android org.json. */
internal object StudyJson {
    fun parse(text:String):Any? {require(text.toByteArray(Charsets.UTF_8).size<=262144){"Backup exceeds 256 KiB."};return Reader(text).read()}
    fun write(value:Any?):String=when(value){
        null->"null";is Boolean->value.toString();is Int,is Long->value.toString()
        is String->buildString{append('"');value.forEach{c->when(c){'"'->append("\\\"");'\\'->append("\\\\");'\n'->append("\\n");'\r'->append("\\r");'\t'->append("\\t");else->if(c.code<32)append("\\u"+c.code.toString(16).padStart(4,'0'))else append(c)}};append('"')}
        is List<*>->value.joinToString(",","[","]"){write(it)}
        is Map<*,*>->value.entries.joinToString(",","{","}"){require(it.key is String);write(it.key)+":"+write(it.value)}
        else->throw IllegalArgumentException("Unsupported JSON value")
    }
    private class Reader(private val s:String){
        private var i=0;private var nodes=0
        private fun error(message:String):Nothing=throw IllegalArgumentException("Invalid backup JSON: $message at ${i+1}.")
        private fun ws(){while(i<s.length&&s[i] in listOf(' ','\n','\r','\t'))i++}
        fun read():Any?{ws();val value=value(0);ws();if(i!=s.length)error("unexpected trailing text");return value}
        private fun value(depth:Int):Any?{
            require(depth<=16){"Backup JSON is nested too deeply."};require(++nodes<=20000){"Backup JSON has too many values."};ws();if(i>=s.length)error("unexpected end")
            return when(s[i]){'"'->string();'{'->obj(depth+1);'['->array(depth+1);'t'->{literal("true");true};'f'->{literal("false");false};'n'->{literal("null");null};'-',in '0'..'9'->number();else->error("unexpected character")}
        }
        private fun literal(word:String){if(!s.startsWith(word,i))error("expected $word");i+=word.length}
        private fun string():String {
            i++;val b=StringBuilder()
            while(i<s.length){val c=s[i++];if(c=='"')return b.toString();if(c.code<32)error("control character in string")
                if(c!='\\'){b.append(c);continue};if(i>=s.length)error("incomplete escape")
                when(val e=s[i++]){'"','\\','/'->b.append(e);'b'->b.append('\b');'f'->b.append('\u000c');'n'->b.append('\n');'r'->b.append('\r');'t'->b.append('\t');'u'->{if(i+4>s.length)error("incomplete Unicode escape");val n=s.substring(i,i+4).toIntOrNull(16)?:error("invalid Unicode escape");b.append(n.toChar());i+=4};else->error("invalid escape")}
            };error("unclosed string")
        }
        private fun obj(depth:Int):Map<String,Any?>{
            i++;ws();val result=linkedMapOf<String,Any?>();if(i<s.length&&s[i]=='}'){i++;return result}
            while(true){ws();if(i>=s.length||s[i]!='"')error("expected object key");val key=string();ws();if(i>=s.length||s[i++]!=':')error("expected colon");if(result.containsKey(key))error("duplicate key");result[key]=value(depth);ws();if(i>=s.length)error("unclosed object");when(s[i++]){'}'->return result;','->Unit;else->error("expected comma or closing brace")}}
        }
        private fun array(depth:Int):List<Any?>{
            i++;ws();val result=mutableListOf<Any?>();if(i<s.length&&s[i]==']'){i++;return result}
            while(true){result+=value(depth);ws();if(i>=s.length)error("unclosed array");when(s[i++]){']'->return result;','->Unit;else->error("expected comma or closing bracket")}}
        }
        private fun number():Number{
            val start=i;if(s[i]=='-')i++;if(i>=s.length)error("incomplete number")
            if(s[i]=='0')i++ else {if(s[i] !in '1'..'9')error("invalid number");while(i<s.length&&s[i] in '0'..'9')i++}
            if(i<s.length&&s[i]=='.'){i++;val p=i;while(i<s.length&&s[i] in '0'..'9')i++;if(i==p)error("empty fraction")}
            if(i<s.length&&s[i] in listOf('e','E')){i++;if(i<s.length&&s[i] in listOf('+','-'))i++;val p=i;while(i<s.length&&s[i] in '0'..'9')i++;if(i==p)error("empty exponent")}
            val text=s.substring(start,i)
            return text.toLongOrNull()?:text.toDoubleOrNull()?.takeIf{it.isFinite()}?:error("number out of range")
        }
    }
}
