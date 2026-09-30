package com.radix.app

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toColorInt
import java.io.File

/** Score/K-map cards rendered with android.graphics and shared as PNGs. No network, no SDK. */
internal object ShareCards {
    const val AUTHORITY="com.radix.app.fileprovider"
    private val INK="#111410".toColorInt();private val PANEL="#1C2019".toColorInt();private val LIME="#D2FA69".toColorInt();private val PAPER="#F4F6EF".toColorInt();private val FAINT="#969D8F".toColorInt();private val SKY="#AACBFA".toColorInt()
    private fun face(context:Context,bold:Boolean):Typeface = runCatching{ResourcesCompat.getFont(context,if(bold)R.font.space_grotesk else R.font.jetbrains_mono)!!}.getOrDefault(if(bold)Typeface.DEFAULT_BOLD else Typeface.MONOSPACE)
    private fun paint(context:Context,color:Int,size:Float,bold:Boolean,mono:Boolean=false):Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply{this.color=color;textSize=sizeDp(context,size);typeface=face(context,bold||!mono);if(bold)isFakeBoldText=true}
    private fun sizeDp(context:Context,dp:Float):Float = dp*context.resources.displayMetrics.density/3f*3f // px on the 1080-wide card
    private fun card(w:Int,h:Int):Pair<Bitmap,Canvas>{val b=createBitmap(w,h);val c=Canvas(b);c.drawColor(INK);return b to c}
    private fun header(c:Canvas,context:Context,eyebrow:String){
        val p=paint(context,LIME,44f,true);c.drawText("rₓ  radix",72f,130f,p)
        c.drawText(eyebrow,72f,200f,paint(context,FAINT,34f,false))
        Paint().apply{color=LIME;strokeWidth=4f}.also{c.drawLine(72f,232f,1008f,232f,it)}
    }
    private fun footer(c:Canvas,context:Context,h:Int){c.drawText("Radix · offline learning · no ads, no accounts",72f,h-72f,paint(context,FAINT,30f,false))}
    fun renderScore(context:Context,name:String,xp:Int,mastered:Int,total:Int,streak:Int):Bitmap{
        val (b,c)=card(1080,1350);header(c,context,"SCORE CARD")
        val lines=GameEngine.scoreLines(name,xp,mastered,total,streak)
        c.drawText(name,72f,380f,paint(context,PAPER,84f,true))
        c.drawText(lines[0].substringAfter("· ").trim(),72f,470f,paint(context,LIME,48f,true))
        var y=620f
        lines.drop(1).forEach{line->
            Paint().apply{color=PANEL}.also{c.drawRoundRect(RectF(72f,y-64f,1008f,y+36f),28f,28f,it)}
            c.drawText(line,110f,y,paint(context,PAPER,38f,false));y+=150f
        }
        // XP progress bar to next level.
        val (into,needed)=GameEngine.progressToNext(xp)
        Paint().apply{color=PANEL}.also{c.drawRoundRect(RectF(72f,y,1008f,y+44f),22f,22f,it)}
        Paint().apply{color=LIME}.also{c.drawRoundRect(RectF(72f,y,72f+936f*into/needed,y+44f),22f,22f,it)}
        c.drawText("$into / $needed XP to Level ${GameEngine.level(xp)+1}",72f,y+110f,paint(context,FAINT,34f,false))
        footer(c,context,1350);return b
    }
    fun renderKMap(context:Context,title:String,variables:Int,cells:List<Int>,form:String,expression:String,note:String):Bitmap{
        require(variables in 2..4&&cells.size==(1 shl variables))
        val (b,c)=card(1080,1560);header(c,context,"K-MAP SOLUTION · $form")
        c.drawText(title.take(34),72f,360f,paint(context,PAPER,56f,true))
        c.drawText("F(${KMapEngine.names(variables)}) = $expression",72f,440f,paint(context,LIME,44f,false))
        val rows=KMapEngine.gray(KMapEngine.rowBits(variables));val cols=KMapEngine.gray(KMapEngine.columnBits(variables))
        val gx0=72f;val gy0=520f;val cell=936f/cols.size;val mono=paint(context,PAPER,40f,false)
        rows.forEachIndexed{r,_->
            cols.forEachIndexed{ci,_->
                val index=KMapEngine.cellAt(variables,r,ci);val v=cells[index]
                val rect=RectF(gx0+ci*cell+6f,gy0+r*cell+6f,gx0+(ci+1)*cell-6f,gy0+(r+1)*cell-6f)
                Paint().apply{color=PANEL}.also{c.drawRoundRect(rect,18f,18f,it)}
                Paint().apply{color=when(v){1->LIME;2->SKY;else->FAINT};style=Paint.Style.STROKE;strokeWidth=4f}.also{c.drawRoundRect(rect,18f,18f,it)}
                mono.color=when(v){1->LIME;2->SKY;else->PAPER}
                c.drawText(if(v==2)"X"else v.toString(),rect.centerX()-14f,rect.centerY()+16f,mono)
            }
        }
        val ny=gy0+rows.size*cell+80f
        note.chunked(44).take(4).forEachIndexed{i,line->c.drawText(line,72f,ny+i*52f,paint(context,FAINT,32f,false))}
        footer(c,context,1560);return b
    }
    fun share(context:Context,bitmap:Bitmap,fileName:String){
        val dir=File(context.cacheDir,"share").apply{mkdirs()}
        require(fileName.matches(Regex("[A-Za-z0-9_-]+\\.png"))){ "Bad file name."}
        val file=File(dir,fileName);file.outputStream().use{bitmap.compress(Bitmap.CompressFormat.PNG,100,it)}
        val uri=FileProvider.getUriForFile(context,AUTHORITY,file)
        context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply{type="image/png";putExtra(Intent.EXTRA_STREAM,uri);addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)},"Share card").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
    fun shareScore(context:Context,name:String,xp:Int,mastered:Int,total:Int,streak:Int)=share(context,renderScore(context,name,xp,mastered,total,streak),"Radix-score.png")
    fun shareKMap(context:Context,title:String,variables:Int,cells:List<Int>,form:String,expression:String,note:String)=share(context,renderKMap(context,title,variables,cells,form,expression,note),"Radix-kmap.png")
}
