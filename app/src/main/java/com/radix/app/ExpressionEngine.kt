package com.radix.app

/** Bounded Boolean parser. No scripting/eval; A is the most significant input. */
internal object ExpressionEngine {
    sealed interface Expr {
        fun evaluate(n:Int, code:Int):Boolean
        fun display():String
    }
    data class Constant(val value:Boolean):Expr {
        override fun evaluate(n:Int,code:Int)=value
        override fun display()=if(value)"1"else"0"
    }
    data class Variable(val index:Int):Expr {
        override fun evaluate(n:Int,code:Int)=(code and (1 shl(n-index-1)))!=0
        override fun display()=('A'+index).toString()
    }
    data class Not(val child:Expr):Expr {
        override fun evaluate(n:Int,code:Int)=!child.evaluate(n,code)
        override fun display()=when(child){is Variable,is Constant->child.display()+"′";else->"(${child.display()})′"}
    }
    data class Binary(val op:String,val left:Expr,val right:Expr):Expr {
        override fun evaluate(n:Int,code:Int):Boolean {
            val a=left.evaluate(n,code);val b=right.evaluate(n,code)
            return when(op){"AND"->a&&b;"NAND"->!(a&&b);"OR"->a||b;"NOR"->!(a||b);"XOR"->a!=b;"XNOR"->a==b;else->error(op)}
        }
        // Proper notation students must learn: · + ⊕ ⊙ ′
        override fun display()=when(op){
            "AND"->"(${left.display()}·${right.display()})";"OR"->"(${left.display()}+${right.display()})"
            "XOR"->"(${left.display()}⊕${right.display()})";"XNOR"->"(${left.display()}⊙${right.display()})"
            "NAND"->"(${left.display()}·${right.display()})′";"NOR"->"(${left.display()}+${right.display()})′";else->error(op)}
    }
    data class Report(val normalized:String,val cells:List<Int>,val sop:String,val pos:String)
    data class Comparison(val equivalent:Boolean,val mismatches:List<Int>)
    private data class Token(val type:String,val position:Int)
    private val words=listOf("XNOR","NAND","XOR","NOR","NOT","AND","OR")
    private fun fail(message:String,at:Int):Nothing=throw IllegalArgumentException("$message · position ${at+1}")
    private fun tokens(source:String):List<Token>{
        require(source.length<=256){"Keep expressions within 256 characters."}
        require(source.isNotBlank()){ "Enter an expression using A–D and 0/1." }
        val s=source.uppercase(java.util.Locale.ROOT);val result=mutableListOf<Token>();var i=0
        while(i<s.length){val ch=s[i];if(ch.isWhitespace()){i++;continue}
            val word=words.firstOrNull{s.startsWith(it,i)}
            val type=when {
                word!=null->word
                ch in 'A'..'D'->ch.toString()
                ch in '0'..'1'->ch.toString()
                ch in listOf('′','’')->"'"
                ch in listOf('(',')','\'')->ch.toString()
                ch=='!'||ch=='~'||ch=='¬'->"NOT"
                ch=='+'||ch=='|'||ch=='∨'->"OR"
                ch=='*'||ch=='.'||ch=='·'||ch=='&'||ch=='∧'->"AND"
                ch=='^'||ch=='⊕'->"XOR"
                ch=='⊙'->"XNOR"
                else->fail("Use A–D, 0/1, parentheses and Boolean operators",i)
            }
            result+=Token(type,i)
            i+=word?.length?:if((ch=='&'||ch=='|')&&i+1<s.length&&s[i+1]==ch)2 else 1
        }
        result+=Token("END",s.length);return result
    }
    private class Parser(source:String,val n:Int){
        private val ts=tokens(source);private var i=0;private var depth=0
        private fun token()=ts[i]
        private fun take()=ts[i++].type
        private fun atomStarts()=token().type in listOf("A","B","C","D","0","1","(","NOT")
        private fun bounded(block:()->Expr):Expr{depth++;if(depth>32)fail("Nesting is too deep; use at most 32 levels",token().position);val value=block();depth--;return value}
        fun parse():Expr {val e=or();if(token().type!="END")fail("Unexpected ${token().type}; check operators/parentheses",token().position);return e}
        private fun or():Expr {var e=xor();while(token().type in listOf("OR","NOR")){val op=take();e=Binary(op,e,xor())};return e}
        private fun xor():Expr {var e=and();while(token().type in listOf("XOR","XNOR")){val op=take();e=Binary(op,e,and())};return e}
        private fun and():Expr {var e=unary();while(token().type in listOf("AND","NAND")||atomStarts()){val op=if(token().type in listOf("AND","NAND"))take()else"AND";e=Binary(op,e,unary())};return e}
        private fun unary():Expr=bounded {
            var e=if(token().type=="NOT"){take();Not(unary())}else primary()
            while(token().type=="'"){take();e=Not(e)};e
        }
        private fun primary():Expr {
            val t=token()
            return when(t.type){
                "0","1"->{take();Constant(t.type=="1")}
                "A","B","C","D"->{take();val index=t.type[0]-'A';if(index>=n)fail("${t.type} needs at least ${index+1} variables",t.position);Variable(index)}
                "("->{take();val e=or();if(token().type!=")")fail("Close the parenthesis",token().position);take();e}
                else->fail("Expected a variable, 0/1 or parenthesis",t.position)
            }
        }
    }
    fun parse(source:String,n:Int):Expr {require(n in 2..4){"Choose 2, 3 or 4 variables."};return Parser(source,n).parse()}
    fun report(source:String,n:Int):Report {
        val e=parse(source,n);val cells=List(1 shl n){if(e.evaluate(n,it))1 else 0}
        return Report(e.display(),cells,KMapEngine.solve(n,cells,KMapEngine.Form.SOP).expression,KMapEngine.solve(n,cells,KMapEngine.Form.POS).expression)
    }
    fun compare(left:String,right:String,n:Int):Comparison {
        val a=parse(left,n);val b=parse(right,n);val mismatches=(0 until(1 shl n)).filter{a.evaluate(n,it)!=b.evaluate(n,it)}
        return Comparison(mismatches.isEmpty(),mismatches)
    }
}
