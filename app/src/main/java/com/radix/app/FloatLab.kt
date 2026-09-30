package com.radix.app

/** IEEE-754 single/double lab. Pure; java.lang.Float/Double are the oracle, fields are taught. */
internal object FloatLab {
    data class Report(val width:Int,val bits:String,val hex:String,val sign:Int,val expBits:String,val expRaw:Int,val bias:Int,val expValue:Int?,val fracBits:String,val kind:String,val value:Double,val valueText:String,val steps:List<String>)
    fun bias(width:Int):Int{require(width==32||width==64){"Width must be 32 or 64."};return if(width==32)127 else 1023}
    fun expLen(width:Int):Int{require(width==32||width==64){"Width must be 32 or 64."};return if(width==32)8 else 11}
    private fun padBits(word:Long,width:Int):String = if(width==32)(word and 0xFFFFFFFFL).toString(2).padStart(32,'0') else word.toString(2).let{if(it.startsWith('-'))java.math.BigInteger.valueOf(word).add(java.math.BigInteger.ONE.shiftLeft(64)).toString(2).padStart(64,'0') else it.padStart(64,'0')}
    fun decode(word:Long,width:Int):Report{
        require(width==32||width==64){"Width must be 32 or 64."}
        val e=expLen(width);val f=width-1-e;val b=bias(width);val bits=padBits(word,width)
        val sign=bits[0]-'0';val expBits=bits.substring(1,1+e);val fracBits=bits.substring(1+e)
        val expRaw=expBits.toInt(2);val max=(1 shl e)-1
        val value=if(width==32)java.lang.Float.intBitsToFloat((word and 0xFFFFFFFFL).toInt()).toDouble() else java.lang.Double.longBitsToDouble(word)
        val kind:String;val expValue:Int?;val valueText:String;val steps=mutableListOf<String>()
        steps+=if(sign==0)"Sign bit is 0, so the value is positive (or +0)." else "Sign bit is 1, so the value is negative (or −0)."
        when{
            expRaw==0&&fracBits.all{it=='0'}->{kind=if(sign==0)"+0 (positive zero)"else"−0 (negative zero)";expValue=null;valueText=if(sign==0)"+0.0"else"−0.0";steps+="Exponent field is all 0s and the fraction is all 0s: this is the special zero pattern."}
            expRaw==0->{kind="Subnormal (denormal)";expValue=1-b;valueText=value.toString();steps+="Exponent all 0s with a nonzero fraction means subnormal: no hidden leading 1.";steps+="Value = (−1)^$sign × 0.${fracBits.trimEnd('0').ifEmpty{"0"}}₂ × 2^${1-b}."}
            expRaw==max&&fracBits.all{it=='0'}->{kind=if(sign==0)"+Infinity"else"−Infinity";expValue=null;valueText=value.toString();steps+="Exponent all 1s with zero fraction is the infinity pattern (overflow, or division like 1.0/0.0)."}
            expRaw==max->{kind="NaN (Not a Number)";expValue=null;valueText="NaN";steps+="Exponent all 1s with a nonzero fraction is NaN: the result of 0.0/0.0 or ∞−∞."}
            else->{kind="Normal";expValue=expRaw-b;valueText=value.toString();steps+="Exponent field ${expBits}₂ = $expRaw; unbiased exponent = $expRaw − $b = ${expRaw-b}.";steps+="Hidden leading 1: value = (−1)^$sign × 1.${fracBits.trimEnd('0').ifEmpty{"0"}}₂ × 2^${expRaw-b}."}
        }
        val hex=if(width==32)(word and 0xFFFFFFFFL).toString(16).uppercase().padStart(8,'0') else padBits(word,64).chunked(4).joinToString(""){it.toInt(2).toString(16).uppercase()}
        return Report(width,bits,hex,sign,expBits,expRaw,b,expValue,fracBits,kind,value,valueText,steps)
    }
    fun parseBits(text:String,width:Int):Long{
        val clean=text.filter{it=='0'||it=='1'}
        require(clean.length==width){"Type exactly $width binary digits."}
        return if(width==32)clean.toLong(2) else java.math.BigInteger(clean,2).toLong()
    }
    fun parseHex(text:String,width:Int):Long{
        val clean=text.trim().removePrefix("0x").removePrefix("0X").uppercase()
        require(clean.length==width/4&&clean.all{it in '0'..'9'||it in 'A'..'F'}){"Type exactly ${width/4} hex digits."}
        return if(width==32)clean.toLong(16) else java.math.BigInteger(clean,16).toLong()
    }
    fun encode(text:String,width:Int):Report{
        require(width==32||width==64){"Width must be 32 or 64."}
        val clean=text.trim().lowercase()
        val value=when(clean){"+inf","inf","infinity","+infinity"->Double.POSITIVE_INFINITY;"-inf","-infinity"->Double.NEGATIVE_INFINITY;"nan","+nan","-nan"->Double.NaN;else->clean.toDoubleOrNull()?:throw IllegalArgumentException("Type a decimal number, or inf / −inf / nan.")}
        require(value.isFinite()||!value.isNaN()||true){"Unreachable."}
        val word=if(width==32)java.lang.Float.floatToRawIntBits(value.toFloat()).toLong() and 0xFFFFFFFFL else java.lang.Double.doubleToRawLongBits(value)
        val r=decode(word,width)
        val note=when{value.isNaN()->"NaN has many bit patterns; this is the canonical quiet NaN the platform produces.";width==32&&value.isFinite()&&value.toFloat().isInfinite()->"The magnitude overflows single precision, so it rounds to infinity.";width==32&&value.isFinite()&&value.toFloat().toDouble()!=value->"Nearest single-precision neighbor: ${value.toFloat()}. The missing tail is rounding error.";else->"Exactly representable at this width (within the parsed decimal)."}
        return r.copy(steps=listOf("Parsed “$text” as ${if(value.isNaN())"NaN" else value}.",note)+r.steps)
    }
}
