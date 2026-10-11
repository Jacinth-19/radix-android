package com.radix.app

import java.math.BigInteger

/** Exact integer arithmetic in bases 2–36 with schoolbook column working. No fractions, no floats. */
internal object BaseArithmetic {
    data class Report(val a:String,val b:String,val base:Int,val op:Char,val result:String,val quotient:String?,val remainder:String?,val steps:List<String>)
    private fun digitValue(c:Char)=if(c in '0'..'9')c-'0' else c-'A'+10
    private fun digitChar(v:Int)=if(v<10)('0'+v) else ('A'+v-10)
    private fun show(v:Int)=if(v>=10)"${digitChar(v)}($v)"else v.toString()
    private data class Num(val negative:Boolean,val digits:String)
    private fun parse(raw:String,base:Int):Num{
        val t=raw.trim().uppercase(java.util.Locale.ROOT).replace(" ","").replace("_","")
        require(t.isNotEmpty()&&t.length<=65){"Keep each number within 64 digits."}
        val neg=t.startsWith("-");val d=if(neg||t.startsWith("+"))t.drop(1)else t
        require(d.isNotEmpty()&&d.length<=64&&d.all{val v=try{digitValue(it)}catch(_:Exception){-1};v in 0 until base}){"Use only base-$base digits 0–${digitChar(base-1)}; no points or prefixes."}
        return Num(neg,d.trimStart('0').ifEmpty{"0"})
    }
    private fun compareMag(x:String,y:String)=if(x.length!=y.length)x.length.compareTo(y.length) else x.compareTo(y)
    private fun addMag(x:String,y:String,base:Int):Pair<String,List<String>>{
        var carry=0;val out=StringBuilder();val steps=mutableListOf<String>()
        for(i in 0 until maxOf(x.length,y.length)){
            val dx=if(i<x.length)digitValue(x[x.length-1-i])else 0;val dy=if(i<y.length)digitValue(y[y.length-1-i])else 0
            val carryIn=carry;val total=dx+dy+carryIn;out.append(digitChar(total%base));carry=total/base
            steps+="Column $i: ${show(dx)} + ${show(dy)} + carry-in ${show(carryIn)} = $total → write ${digitChar(total%base)}, carry-out $carry."
        }
        if(carry>0){out.append(digitChar(carry));steps+="Final carry $carry becomes the new leftmost digit."}
        return out.toString().reversed().trimStart('0').ifEmpty{"0"} to steps
    }
    private fun subMag(big:String,small:String,base:Int):Pair<String,List<String>>{
        require(compareMag(big,small)>=0){"Column subtraction needs the larger magnitude first."}
        var borrow=0;val out=StringBuilder();val steps=mutableListOf<String>()
        for(i in big.indices){
            val dx=digitValue(big[big.length-1-i]);val dy=if(i<small.length)digitValue(small[small.length-1-i])else 0
            val borrowIn=borrow;var top=dx-borrowIn;borrow=0;var note=""
            if(top<dy){top+=base;borrow=1;note=", borrowing $base from the left"}
            out.append(digitChar(top-dy))
            steps+="Column $i: top ${show(dx)} minus borrow-in $borrowIn minus ${show(dy)}$note → write ${digitChar(top-dy)}, borrow-out $borrow."
        }
        return out.toString().reversed().trimStart('0').ifEmpty{"0"} to steps
    }
    private fun toBig(n:Num,base:Int)=BigInteger(n.digits,base).let{if(n.negative)it.negate()else it}
    private fun fromBig(v:BigInteger,base:Int)=v.toString(base).uppercase(java.util.Locale.ROOT)
    fun compute(rawA:String,rawB:String,base:Int,opRaw:Char):Report{
        require(base in 2..36){"Choose a base from 2 to 36."}
        val op=when(opRaw){'+','-','*','×','/','÷'->opRaw;else->throw IllegalArgumentException("Choose +, −, × or ÷.")}
        val a=parse(rawA,base);val b=parse(rawB,base)
        val normA=(if(a.negative&&a.digits!="0")"-"else "")+a.digits;val normB=(if(b.negative&&b.digits!="0")"-"else "")+b.digits
        val va=toBig(a,base);val vb=toBig(b,base);val steps=mutableListOf<String>()
        return when(op){
            '+','-'->{
                val effB=if(op=='-')b.copy(negative=!b.negative&&b.digits!="0")else b
                if(op=='-')steps+="Subtraction adds the negated value: $normA − ($normB) = $normA + (${if(effB.negative&&effB.digits!="0")"-"else ""}${effB.digits})."
                val sameSign=a.negative==effB.negative||a.digits=="0"||effB.digits=="0"
                if(sameSign){
                    steps+="Both magnitudes share a sign — add the columns right to left in base $base."
                    val (sum,col)=addMag(a.digits,effB.digits,base);steps+=col
                    val exact=fromBig(if(op=='+')va+vb else va-vb,base)
                    require(sum==exact.trimStart('-')){"Internal column check failed."}
                    Report(normA,normB,base,op,exact,null,null,steps+"Apply the shared sign → $exact.")
                }else{
                    steps+="Different signs — subtract the smaller magnitude from the larger one."
                    val cmp=compareMag(a.digits,effB.digits)
                    if(cmp==0)return Report(normA,normB,base,op,"0",null,null,steps+"Equal magnitudes cancel exactly → 0.")
                    val (big,small,bigNeg)=if(cmp>0)Triple(a.digits,effB.digits,a.negative)else Triple(effB.digits,a.digits,effB.negative)
                    val (diff,col)=subMag(big,small,base);steps+=col
                    val exact=fromBig(if(op=='+')va+vb else va-vb,base)
                    require(diff==exact.trimStart('-')){"Internal column check failed."}
                    Report(normA,normB,base,op,exact,null,null,steps+"Take the sign of the larger magnitude → $exact.")
                }
            }
            '*','×'->{
                if(a.digits=="0"||b.digits=="0")return Report(normA,normB,base,'×',"0",null,null,listOf("Anything times zero is zero → 0."))
                steps+="Multiply each digit of the second number by the whole first number, shifting left each row."
                val partials=b.digits.reversed().mapIndexed{i,ch->
                    val d=digitValue(ch);val row=BigInteger(a.digits,base).multiply(BigInteger.valueOf(d.toLong())).multiply(BigInteger.valueOf(base.toLong()).pow(i))
                    steps+="Row $i: $normA × ${show(d)}, shifted $i place${if(i==1)""else"s"} → ${fromBig(row,base)}."
                    row}
                val mag=partials.fold(BigInteger.ZERO){x,y->x+y}
                steps+="Add the ${partials.size} partial product${if(partials.size==1)""else"s"} → ${fromBig(mag,base)}."
                val neg=a.negative!=b.negative;val exact=(if(neg)mag.negate()else mag).let{fromBig(it,base)}
                steps+=if(neg)"Different signs → the product is negative: $exact." else "Same signs → the product is positive: $exact."
                require(exact==fromBig(va.multiply(vb),base)){"Internal multiplication check failed."}
                Report(normA,normB,base,'×',exact,null,null,steps)
            }
            else->{
                require(b.digits!="0"){"Division by zero is undefined."}
                steps+="Long division on magnitudes: bring digits down left to right; each trial digit is 0–${digitChar(base-1)}."
                val divisor=BigInteger(b.digits,base);var current=BigInteger.ZERO;val q=StringBuilder()
                a.digits.forEachIndexed{i,ch->
                    current=current.multiply(BigInteger.valueOf(base.toLong())).add(BigInteger.valueOf(digitValue(ch).toLong()))
                    var trial=(current.divide(divisor)).toInt().coerceAtMost(base-1)
                    // Exact single-digit trial by construction; verify bounds honestly.
                    while(divisor.multiply(BigInteger.valueOf((trial+1).toLong()))<=current&&trial<base-1)trial++
                    while(divisor.multiply(BigInteger.valueOf(trial.toLong()))>current&&trial>0)trial--
                    current-=divisor.multiply(BigInteger.valueOf(trial.toLong()));q.append(digitChar(trial))
                    steps+="Bring down ${show(digitValue(ch))} → working value ${fromBig(current.add(divisor.multiply(BigInteger.valueOf(trial.toLong()))),base)}; ÷ ${b.digits} = ${show(trial)}, remainder ${fromBig(current,base)}."
                }
                val magQ=q.toString().trimStart('0').ifEmpty{"0"}
                val neg=a.negative!=b.negative&&magQ!="0";val quotient=(if(neg)"-"else "")+magQ
                val remMag=fromBig(current,base);val remainder=(if(a.negative&&remMag!="0")"-"else "")+remMag
                steps+="Quotient $quotient remainder $remainder. Check: divisor × quotient + remainder = dividend."
                require(quotient==fromBig(va.divide(vb),base)&&remainder==fromBig(va.remainder(vb),base)){"Internal division check failed."}
                Report(normA,normB,base,'÷',quotient,quotient,remainder,steps)
            }
        }
    }
    /** Denary/octal/hex/binary side-by-side for 0–15. */
    fun referenceTable():List<List<String>> = (0..15).map{n->listOf(n.toString(),n.toString(8),n.toString(16).uppercase(),n.toString(2).padStart(4,'0'))}
}
