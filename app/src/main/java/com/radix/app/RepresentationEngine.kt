package com.radix.app

import java.math.BigInteger

/** Fixed-width representations, separate from magnitude-first base conversion. No silent wrapping. */
internal object RepresentationEngine {
    val widths=listOf(4,8,16,32,64)
    data class Report(val width:Int,val word:String,val unsigned:String,val signed:String,val hex:String,val gray:String,val bcd:String?,val ones:String,val onesSigned:String,val range:String,val steps:List<String>)
    private val ZERO=BigInteger.ZERO;private val ONE=BigInteger.ONE
    fun grayEncode(value:BigInteger):BigInteger {require(value.signum()>=0);return value.xor(value.shiftRight(1))}
    fun grayDecode(gray:BigInteger):BigInteger {require(gray.signum()>=0&&gray.bitLength()<=64);var result=ZERO;var v=gray;while(v.signum()!=0){result=result.xor(v);v=v.shiftRight(1)};return result}
    fun bcd(value:BigInteger):String {require(value.signum()>=0);return value.toString().map{(it-'0').toString(2).padStart(4,'0')}.joinToString(" ")}
    fun report(input:String,width:Int,signedMode:Boolean,mode:String="decimal"):Report {
        require(width in widths){"Choose 4, 8, 16, 32 or 64 bits."}
        require(input.length<=128){"Keep input within 128 characters."}
        val text=input.trim();val modulus=ONE.shiftLeft(width);val sign=ONE.shiftLeft(width-1)
        val min=if(signedMode)sign.negate()else ZERO;val max=if(signedMode)sign-ONE else modulus-ONE
        val range="$min to $max";val steps=mutableListOf<String>();val wordValue:BigInteger
        when(mode){
            "decimal"->{
                require(text.matches(Regex("[+-]?[0-9]+"))){"Enter a whole decimal integer, not a fraction or base prefix."}
                val v=BigInteger(text)
                require(v>=min&&v<=max){"Out of range: $width-bit ${if(signedMode)"two’s-complement"else"unsigned"} values are $range. No wrapping was applied."}
                wordValue=if(v.signum()<0)modulus+v else v
                if(v.signum()<0){
                    val magnitude=v.negate().toString(2).padStart(width,'0')
                    steps+="Magnitude: $magnitude"
                    steps+="Invert every bit: ${magnitude.map{if(it=='1')'0'else'1'}.joinToString("")}"
                    steps+="Add 1 at this width: ${wordValue.toString(2).padStart(width,'0')}"
                    steps+="Equivalent arithmetic: 2^$width + ($v) = $wordValue."
                }else steps+="Convert $v to binary and zero-pad on the left to $width bits."
            }
            "bits","gray"->{
                val digits=text.filterNot{it==' '||it=='_'}
                require(digits.isNotEmpty()&&digits.all{it=='0'||it=='1'}){"Use only binary 0/1; spaces and underscores may group digits."}
                require(digits.length<=width){"The word has ${digits.length} bits; choose a width of at least that size."}
                val v=BigInteger(digits,2);wordValue=if(mode=="gray")grayDecode(v)else v
                if(digits.length<width)steps+="Short words are ZERO-padded on the left, not sign-extended. Choose the original width when reading a negative word."
                if(mode=="gray")steps+="Decode Gray with cumulative XOR from the most significant bit. Gray is a code, not positional binary."
            }
            else->throw IllegalArgumentException("Choose Decimal, Binary word or Gray code.")
        }
        val signed=if(wordValue>=sign)wordValue-modulus else wordValue
        val binary=wordValue.toString(2).padStart(width,'0');val gray=grayEncode(wordValue).toString(2).padStart(width,'0')
        val inverted=(modulus-ONE-wordValue).toString(2).padStart(width,'0')
        val onesSigned=if(binary[0]=='0')wordValue.toString()else if(wordValue==modulus-ONE)"-0" else "-"+(modulus-ONE-wordValue).toString()
        val selected=if(signedMode)signed else wordValue
        steps+="Unsigned: sum the enabled bit weights = $wordValue."
        steps+="Two’s complement: ${if(binary[0]=='1')"MSB is 1: $wordValue − 2^$width = $signed"else"MSB is 0: $signed"}."
        steps+="One’s complement: invert every bit: $binary → $inverted, which reads as $onesSigned in one’s complement.${if(onesSigned=="-0")" One’s complement has two zeroes: +0 and −0." else ""}"
        steps+="Gray code = binary XOR (binary shifted right by one)."
        if(selected.signum()<0)steps+="BCD here represents non-negative decimal digits only; a negative value has no BCD word in this model."
        else steps+="BCD gives EACH decimal digit its own 4-bit nibble; it is not ordinary binary."
        return Report(width,binary,wordValue.toString(),signed.toString(),wordValue.toString(16).uppercase().padStart((width+3)/4,'0'),gray,if(selected.signum()>=0)bcd(selected)else null,inverted,onesSigned,range,steps)
    }
}
