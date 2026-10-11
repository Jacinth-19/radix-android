package com.radix.app

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

private data class NetPoint(val x:Float,val y:Float,val gate:LogicEngine.Gate?=null)
private data class DiagramLayout(val width:Float,val height:Float,val points:Map<String,NetPoint>)
private fun layout(c:LogicEngine.Circuit):DiagramLayout {
    val depth=c.inputs.associateWith{0}.toMutableMap();c.nodes.forEach { depth[it.id]=1+it.refs.maxOf{r->depth.getValue(r)} }
    val counts=c.nodes.groupingBy{depth.getValue(it.id)}.eachCount();val h=maxOf(240,c.inputs.size*42+30,(counts.values.maxOrNull()?:1)*88+30).toFloat()
    val used=mutableMapOf<Int,Int>();val points=c.inputs.mapIndexed{i,n->n to NetPoint(50f,30+(i+.5f)*(h-60)/c.inputs.size)}.toMap().toMutableMap()
    c.nodes.forEach{n->val d=depth.getValue(n.id);val i=used[d]?:0;used[d]=i+1;points[n.id]=NetPoint(50+d*150f,30+(i+.5f)*(h-60)/counts.getValue(d),n.gate)}
    return DiagramLayout((depth.values.max()+1)*150f+80,h,points)
}
private data class DiagramPaint(val high:Color,val low:Color,val ink:Color,val panel:Color,val unknown:Color,val language:String) {
    fun tone(v:Int?):Color=if(v==null)unknown else if(v==1)high else low
    fun text(source:String):String=AppStrings.translate(source,language)
}
private fun DrawScope.label(p:DiagramPaint,x:Float,y:Float,s:String,color:Color=p.low,size:Float=11f,center:Boolean=false){
    val paint=Paint(Paint.ANTI_ALIAS_FLAG).apply{textSize=size;typeface=Typeface.MONOSPACE;this.color=android.graphics.Color.argb((color.alpha*255).toInt(),(color.red*255).toInt(),(color.green*255).toInt(),(color.blue*255).toInt());textAlign=if(center)Paint.Align.CENTER else Paint.Align.LEFT}
    drawContext.canvas.nativeCanvas.drawText(s,x,y,paint)
}
private fun DrawScope.wire(p:DiagramPaint,x:Float,y:Float,toX:Float,toY:Float,v:Int?){
    val path=Path().apply{moveTo(x,y);lineTo((x+toX)/2,y);lineTo((x+toX)/2,toY);lineTo(toX,toY)}
    if(v==1)drawPath(path,p.high.copy(alpha=.18f),style=Stroke(7f))
    drawPath(path,p.tone(v),style=Stroke(2f))
}
private fun DrawScope.track(p:DiagramPaint,pts:List<Pair<Float,Float>>,v:Int?,w:Float=2f){
    if(v==1)for(i in 0 until pts.size-1)drawLine(p.high.copy(alpha=.18f),Offset(pts[i].first,pts[i].second),Offset(pts[i+1].first,pts[i+1].second),w+5f)
    for(i in 0 until pts.size-1)drawLine(p.tone(v),Offset(pts[i].first,pts[i].second),Offset(pts[i+1].first,pts[i+1].second),w)
}
private fun DrawScope.pin(p:DiagramPaint,x:Float,y:Float,v:Int?){drawCircle(p.tone(v),3.2f,Offset(x,y))}
private fun outputX(g:LogicEngine.Gate):Float=when(g){LogicEngine.Gate.NOR,LogicEngine.Gate.XNOR->70f;LogicEngine.Gate.NOT,LogicEngine.Gate.NAND->62f;LogicEngine.Gate.AND,LogicEngine.Gate.BUFFER->52f;else->60f}
private fun isOr(g:LogicEngine.Gate)=g in listOf(LogicEngine.Gate.OR,LogicEngine.Gate.NOR,LogicEngine.Gate.XOR,LogicEngine.Gate.XNOR)
private fun DrawScope.symbol(p:DiagramPaint,g:LogicEngine.Gate,x:Float,y:Float){
    val d=if(g in listOf(LogicEngine.Gate.AND,LogicEngine.Gate.NAND))"M0 0 H26 A26 26 0 0 1 26 52 H0 Z" else if(isOr(g))"M0 0 Q18 26 0 52 Q38 52 60 26 Q38 0 0 0 Z" else "M0 0 L52 26 L0 52 Z"
    withTransform({translate(x,y)}) {
        val path=PathParser().parsePathString(d).toPath()
        withTransform({translate(2f,2.5f)}){drawPath(path,p.ink.copy(alpha=.14f))}
        drawPath(path,p.panel);drawPath(path,p.ink,style=Stroke(2f))
        if(g in listOf(LogicEngine.Gate.XOR,LogicEngine.Gate.XNOR))drawPath(PathParser().parsePathString("M-6 0 Q12 26 -6 52").toPath(),p.ink,style=Stroke(1.6f))
        if(g in listOf(LogicEngine.Gate.NOT,LogicEngine.Gate.NAND,LogicEngine.Gate.NOR,LogicEngine.Gate.XNOR)){val cx=if(isOr(g))66f else 58f;drawLine(p.ink,Offset(cx-6,26f),Offset(cx-4,26f),1.6f);drawCircle(p.panel,4f,Offset(cx,26f));drawCircle(p.ink,4f,Offset(cx,26f),style=Stroke(1.6f))}
    }
}
@Composable private fun DiagramSurface(width:Float,height:Float,title:String,wide:Boolean=false,draw:DrawScope.(DiagramPaint)->Unit){
    val strings=LocalStrings.current
    val p=DiagramPaint(accent(),muted(),MaterialTheme.colorScheme.onSurface,MaterialTheme.colorScheme.surfaceVariant,MaterialTheme.colorScheme.secondary,strings.languageCode)
    BoxWithConstraints(Modifier.fillMaxWidth().semantics{contentDescription=strings.text(title)}) {
        // Compact block/single-gate views fit phones. Expanded netlists scroll locally at readable scale.
        val actual=if(wide)maxOf(maxWidth.value,width) else minOf(maxWidth.value,width)
        Box(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
        Canvas(Modifier.width(actual.dp).height((height*actual/width).dp)) {
            val factor=size.width/width
            withTransform({scale(factor,factor,Offset.Zero)}) {
                var gx=12f;while(gx<width){var gy=12f;while(gy<height){drawCircle(p.ink.copy(alpha=.055f),1.1f,Offset(gx,gy));gy+=26f};gx+=26f}
                draw(p)
            }
        }
        }
    }
}
@Composable internal fun GateDiagram(g:LogicEngine.Gate,inputs:List<Boolean>,out:Boolean){
    DiagramSurface(320f,190f,"${g.name} gate. Inputs ${inputs.map{if(it)1 else 0}}; output ${if(out)1 else 0}.") {p->
        val x=140f;val y=68f
        inputs.forEachIndexed {i,v->val iy=if(inputs.size==1)94f else 35+i*120f/(inputs.size-1);val py=y+(i+1)*52f/(inputs.size+1);label(p,14f,iy+4,('A'+i).toString());label(p,48f,iy+4,if(v)"1"else"0",p.tone(if(v)1 else 0),14f);wire(p,63f,iy,x+if(isOr(g))7f else 0f,py,if(v)1 else 0)}
        symbol(p,g,x,y);label(p,x+24,y+76,g.name,center=true,size=10f)
        wire(p,x+outputX(g),y+26,260f,y+26,if(out)1 else 0);label(p,270f,y+31,"Y=${if(out)1 else 0}",p.tone(if(out)1 else 0),16f)
    }
}
@Composable internal fun CircuitDiagram(c:LogicEngine.Circuit,r:LogicEngine.CircuitResult,expanded:Boolean){
    if(!expanded){BlockDiagram(c.title,c.inputs,c.outputs.keys.toList(),c.inputs.map{if(r.signals.getValue(it))1 else 0},r.outputs.values.map{if(it)1 else 0},false,false);return}
    val language=LocalStrings.current.languageCode
    val outputNames=c.outputs.keys.map{LogicCatalogTranslations.translateOutputName(it,language)}
    val l=layout(c)
    DiagramSurface(l.width,l.height,"${c.title} gate-level schematic. Crossings connect only at pin junction dots.",true){p->
        c.nodes.forEach {n->val point=l.points.getValue(n.id);n.refs.forEachIndexed{i,ref->val from=l.points.getValue(ref);val fx=from.x+(from.gate?.let{outputX(it)}?:0f);wire(p,fx,from.y,point.x+if(isOr(n.gate))7f else 0f,point.y-26+(i+1)*52f/(n.refs.size+1),if(r.signals.getValue(ref))1 else 0);drawCircle(p.tone(if(r.signals.getValue(ref))1 else 0),3f,Offset(fx,from.y))}}
        c.nodes.forEach {n->val point=l.points.getValue(n.id);symbol(p,n.gate,point.x,point.y-26);label(p,point.x+26,point.y+45,"${n.gate} · ${n.id}",size=10f,center=true)}
        c.inputs.forEach {n->val point=l.points.getValue(n);label(p,8f,point.y+4,n);label(p,48f,point.y-9,if(r.signals.getValue(n))"1"else"0",p.tone(if(r.signals.getValue(n))1 else 0))}
        c.outputs.entries.forEachIndexed{i,(name,ref)->val from=l.points.getValue(ref);val y=35+(i+.5f)*(l.height-70)/c.outputs.size;val v=if(r.outputs.getValue(name))1 else 0;wire(p,from.x+(from.gate?.let{outputX(it)}?:0f),from.y,l.width-60,y,v);label(p,l.width-52,y+4,"${outputNames[i]}=$v",p.tone(v))}
    }
}
@Composable internal fun BlockDiagram(title:String,inputs:List<String>,outputs:List<String>,ivals:List<Int?>,ovals:List<Int?>,clocked:Boolean,clock:Boolean){
    val language=LocalStrings.current.languageCode
    val visualTitle=LogicCatalogTranslations.translate(title,language)?:title
    val visualOutputs=outputs.map{LogicCatalogTranslations.translateOutputName(it,language)}
    val h=maxOf(200,maxOf(inputs.size,outputs.size+(if(clocked)1 else 0))*32+60).toFloat()
    DiagramSurface(320f,h,"$title block. ${outputs.zip(ovals).joinToString{(name,v)->"$name=${v?:"unknown"}}}"){p->
        drawRoundRect(p.panel,Offset(122f,24f),Size(91f,h-49),androidx.compose.ui.geometry.CornerRadius(12f))
        drawRoundRect(p.low,Offset(122f,24f),Size(91f,h-49),androidx.compose.ui.geometry.CornerRadius(12f),style=Stroke(1.2f))
        val words=visualTitle.substringBefore(" · ").split(' ')
        words.forEachIndexed{i,word->label(p,166f,maxOf(55f,h/2-words.size*7)+15*i,word,p.ink,10f,true)}
        inputs.forEachIndexed{i,n->val y=38+(i+.5f)*(h-if(clocked)108 else 68)/inputs.size;label(p,10f,y+4,n,size=10f);label(p,65f,y-7,ivals[i]?.toString()?:"X",p.tone(ivals[i]),10f);wire(p,73f,y,122f,y,ivals[i])}
        visualOutputs.forEachIndexed{i,n->val y=38+(i+.5f)*(h-68)/visualOutputs.size;wire(p,213f,y,252f,y,ovals[i]);label(p,260f,y+4,"$n=${ovals[i]?:"X"}",p.tone(ovals[i]),10f)}
        if(clocked){val cy=h-49;wire(p,78f,cy,122f,cy,if(clock)1 else 0);drawPath(PathParser().parsePathString("M122 ${h-55} l9 6 -9 6").toPath(),p.ink,style=Stroke(1.5f));label(p,43f,cy+4,"CLK",size=9f)}
    }
}
@Composable internal fun SequentialDiagram(s:LogicEngine.Sequential,state:LogicEngine.State,inputs:List<Boolean>,outputs:List<Int?>,expanded:Boolean){
    if(!expanded){BlockDiagram(s.title,s.inputs,if(s.width==1)listOf("Q","Q′")else(s.width-1 downTo 0).map{"Q$it"},inputs.map{if(it)1 else 0},outputs,s.clocked,state.clock);return}
    SequentialInternals(s,state,inputs,outputs)
}
// ---------- Inside sequential circuits: illustrative gate-level structure ----------
private fun bit(v:Boolean)=if(v)1 else 0
private fun DrawScope.stageCell(p:DiagramPaint,x:Float,y:Float,w:Float,h:Float,title:String,q:Int?){
    withTransform({translate(2f,2.5f)}){drawRoundRect(p.ink.copy(alpha=.12f),Offset(x,y),Size(w,h),androidx.compose.ui.geometry.CornerRadius(12f))}
    drawRoundRect(p.panel,Offset(x,y),Size(w,h),androidx.compose.ui.geometry.CornerRadius(12f))
    drawRoundRect(p.ink,Offset(x,y),Size(w,h),androidx.compose.ui.geometry.CornerRadius(12f),style=Stroke(2f))
    label(p,x+w/2,y+22,p.text(title),p.ink,11f,true)
    label(p,x+w/2,y+h-12,"Q=${q?:"X"}",p.tone(q),13f,true)
}
private fun DrawScope.clockWedge(x:Float,y:Float){drawPath(PathParser().parsePathString("M$x $y l10 7 -10 7").toPath(),androidx.compose.ui.graphics.Color.Transparent);drawPath(PathParser().parsePathString("M$x $y l10 7 -10 7 Z").toPath(),pInk(),style=Stroke(1.6f))}
private fun pInk():androidx.compose.ui.graphics.Color=androidx.compose.ui.graphics.Color(0xFF888888)
@Composable private fun SequentialInternals(s:LogicEngine.Sequential,state:LogicEngine.State,inputs:List<Boolean>,outputs:List<Int?>){
    when(s.id){
        "SR_NOR","SR_NAND"->{
            val nand=s.id=="SR_NAND";val kind=if(nand)LogicEngine.Gate.NAND else LogicEngine.Gate.NOR;val px=145f+if(nand)0f else 7f
            DiagramSurface(330f,250f,"Cross-coupled ${kind.name} latch. ${if(nand)"nS drives the Q gate"else"R drives the Q gate"}.",true){p->
                val upper=bit(inputs[if(nand)0 else 1]);val lower=bit(inputs[if(nand)1 else 0])
                wire(p,65f,52f,px,48+52f/3,upper);wire(p,65f,164f,px,160+52f/3,lower)
                pin(p,px,48+52f/3,upper);pin(p,px,160+52f/3,lower)
                label(p,10f,56f,if(nand)"nS"else"R");label(p,10f,168f,if(nand)"nR"else"S")
                symbol(p,kind,145f,48f);symbol(p,kind,145f,160f);val ox=145+outputX(kind)
                track(p,listOf(ox to 74f,290f to 74f,290f to 160+104f/3,px to 160+104f/3),outputs[0])
                track(p,listOf(ox to 186f,310f to 186f,310f to 48+104f/3,px to 48+104f/3),outputs[1])
                pin(p,ox,74f,outputs[0]);pin(p,ox,186f,outputs[1]);pin(p,px,160+104f/3,outputs[0]);pin(p,px,48+104f/3,outputs[1])
                label(p,240f,59f,"Q=${outputs[0]?:"X"}",p.tone(outputs[0]));label(p,240f,213f,"Q′=${outputs[1]?:"X"}",p.tone(outputs[1]))
                label(p,145f,238f,p.text("cross-coupled ${kind.name.lowercase()} gates"),size=10f)
            }
        }
        "D_LATCH"->{
            // D ingrediente: S = D·EN, R = D′·EN into a NOR SR core.
            DiagramSurface(500f,250f,"D latch internals: steering gates plus a cross-coupled NOR core.",true){p->
                val d=bit(inputs[0]);val en=bit(inputs[1]);val nd=1-d;val set=d and en;val rst=nd and en
                label(p,10f,64f,"D");label(p,10f,176f,"EN")
                wire(p,40f,60f,150f,60f,d);pin(p,150f,60f,d)
                symbol(p,LogicEngine.Gate.NOT,150f,34f);label(p,150f,108f,"D′=$nd",p.tone(nd),10f)
                wire(p,40f,60f,250f,51f,d);pin(p,250f,51f,d)
                wire(p,40f,172f,250f,69f,en);pin(p,250f,69f,en)
                symbol(p,LogicEngine.Gate.AND,250f,34f);label(p,250f,108f,"S=D·EN=$set",p.tone(set),10f)
                symbol(p,LogicEngine.Gate.AND,250f,140f);label(p,250f,214f,"R=D′·EN=$rst",p.tone(rst),10f)
                wire(p,212f,60f,250f,157f,nd);pin(p,250f,157f,nd)
                wire(p,40f,172f,250f,175f,en);pin(p,250f,175f,en)
                symbol(p,LogicEngine.Gate.NOR,350f,34f);symbol(p,LogicEngine.Gate.NOR,350f,140f)
                wire(p,302f,60f,357f,157f,set);pin(p,357f,157f,set)
                wire(p,302f,166f,357f,51f,rst);pin(p,357f,51f,rst)
                track(p,listOf(420f to 60f,448f to 60f,448f to 175f,357f to 175f),outputs[0])
                track(p,listOf(420f to 166f,466f to 166f,466f to 69f,357f to 69f),outputs[1])
                pin(p,420f,60f,outputs[0]);pin(p,420f,166f,outputs[1])
                label(p,424f,45f,"Q=${outputs[0]?:"X"}",p.tone(outputs[0]));label(p,424f,193f,"Q′=${outputs[1]?:"X"}",p.tone(outputs[1]))
            }
        }
        "SR_FF"->{
            DiagramSurface(500f,260f,"SR flip-flop internals: clocked AND gates feeding an SR NOR core.",true){p->
                val sIn=bit(inputs[0]);val rIn=bit(inputs[1]);val clk=bit(state.clock)
                label(p,10f,64f,"S");label(p,10f,176f,"R");label(p,10f,232f,"CLK=$clk",p.tone(clk))
                wire(p,40f,60f,190f,51f,sIn);wire(p,40f,172f,190f,157f,rIn)
                wire(p,60f,228f,190f,69f,clk);wire(p,60f,228f,190f,175f,clk)
                symbol(p,LogicEngine.Gate.AND,190f,34f);symbol(p,LogicEngine.Gate.AND,190f,140f)
                val s2=sIn and clk;val r2=rIn and clk
                label(p,190f,108f,"S·CLK=$s2",p.tone(s2),10f);label(p,190f,214f,"R·CLK=$r2",p.tone(r2),10f)
                symbol(p,LogicEngine.Gate.NOR,320f,34f);symbol(p,LogicEngine.Gate.NOR,320f,140f)
                wire(p,242f,60f,327f,157f,s2);wire(p,242f,166f,327f,51f,r2)
                track(p,listOf(390f to 60f,418f to 60f,418f to 175f,327f to 175f),outputs[0])
                track(p,listOf(390f to 166f,436f to 166f,436f to 69f,327f to 69f),outputs[1])
                pin(p,390f,60f,outputs[0]);pin(p,390f,166f,outputs[1])
                label(p,394f,45f,"Q=${outputs[0]?:"X"}",p.tone(outputs[0]));label(p,394f,193f,"Q′=${outputs[1]?:"X"}",p.tone(outputs[1]))
            }
        }
        "D_FF"->{
            // Master-slave: master follows D while CLK=0; slave copies the master on rising edges.
            DiagramSurface(560f,395f,"D flip-flop internals: master latch and slave latch with a clock inverter.",true){p->
                val d=bit(inputs[0]);val clk=bit(state.clock);val nclk=1-clk
                label(p,10f,70f,"D");label(p,10f,199f,"CLK=$clk",p.tone(clk))
                label(p,150f,18f,p.text("MASTER · follows D while CLK=0"),size=10f)
                label(p,150f,190f,p.text("SLAVE · copies master on ↑CLK"),size=10f)
                track(p,listOf(30f to 195f,480f to 195f),clk);pin(p,30f,195f,clk)
                track(p,listOf(440f to 66f,505f to 66f,505f to 207f,40f to 207f,40f to 251f),null)
                label(p,300f,203f,p.text("master Q → slave D"),size=9f)
                // Master steering.
                wire(p,40f,66f,140f,66f,d);pin(p,140f,66f,d)
                symbol(p,LogicEngine.Gate.NOT,140f,40f)
                wire(p,215f,66f,215f,57f,d);wire(p,215f,57f,260f,57f,d);pin(p,215f,66f,d);pin(p,260f,57f,d)
                track(p,listOf(202f to 66f,202f to 100f,231f to 100f,231f to 147f,260f to 147f),1-d)
                pin(p,260f,147f,1-d)
                symbol(p,LogicEngine.Gate.AND,260f,40f);symbol(p,LogicEngine.Gate.AND,260f,130f)
                // Master clock inverter → nclk taps.
                track(p,listOf(222f to 195f,222f to 134f),clk);pin(p,222f,195f,clk);pin(p,222f,134f,clk)
                symbol(p,LogicEngine.Gate.NOT,222f,108f);label(p,228f,102f,"CLK′=$nclk",p.tone(nclk),9f)
                track(p,listOf(284f to 134f,300f to 134f,300f to 75f,260f to 75f),nclk)
                track(p,listOf(284f to 134f,300f to 134f,300f to 165f,260f to 165f),nclk)
                symbol(p,LogicEngine.Gate.NOR,370f,40f);symbol(p,LogicEngine.Gate.NOR,370f,130f)
                wire(p,312f,66f,377f,147f,d and nclk);wire(p,312f,156f,377f,57f,(1-d) and nclk)
                track(p,listOf(440f to 66f,465f to 66f,465f to 165f,377f to 165f),null)
                track(p,listOf(440f to 156f,485f to 156f,485f to 75f,377f to 75f),null)
                label(p,444f,58f,"Qm",size=10f)
                // Slave steering: same gates, shifted down.
                wire(p,40f,251f,140f,251f,null);pin(p,140f,251f,null)
                symbol(p,LogicEngine.Gate.NOT,140f,225f)
                wire(p,225f,251f,225f,242f,null);wire(p,225f,242f,260f,242f,null);pin(p,225f,251f,null)
                track(p,listOf(202f to 251f,202f to 285f,231f to 285f,231f to 332f,260f to 332f),null)
                symbol(p,LogicEngine.Gate.AND,260f,225f);symbol(p,LogicEngine.Gate.AND,260f,315f)
                track(p,listOf(330f to 195f,330f to 350f),clk);pin(p,330f,195f,clk)
                wire(p,330f,260f,260f,260f,clk);wire(p,330f,350f,260f,350f,clk)
                symbol(p,LogicEngine.Gate.NOR,370f,225f);symbol(p,LogicEngine.Gate.NOR,370f,315f)
                wire(p,312f,251f,377f,332f,null);wire(p,312f,341f,377f,242f,null)
                track(p,listOf(440f to 251f,465f to 251f,465f to 350f,377f to 350f),outputs[0])
                track(p,listOf(440f to 341f,485f to 341f,485f to 260f,377f to 260f),outputs[1])
                pin(p,440f,251f,outputs[0]);pin(p,440f,341f,outputs[1])
                label(p,444f,243f,"Q=${outputs[0]?:"X"}",p.tone(outputs[0]));label(p,444f,368f,"Q′=${outputs[1]?:"X"}",p.tone(outputs[1]),10f)
            }
        }
        "JK_FF","T_FF"->{
            val jk=s.id=="JK_FF"
            DiagramSurface(540f,280f,"${if(jk)"JK"else"T"} flip-flop internals: input steering with Q feedback into a clocked SR core.",true){p->
                val jIn=bit(inputs[0]);val kIn=bit(if(jk)inputs[1]else inputs[0]);val clk=bit(state.clock)
                val q=outputs[0];val qb=outputs[1]
                label(p,8f,54f,if(jk)"J"else"T");if(jk)label(p,8f,164f,"K")else label(p,8f,164f,"T (again)")
                label(p,8f,248f,"CLK=$clk",p.tone(clk))
                symbol(p,LogicEngine.Gate.AND,120f,24f);symbol(p,LogicEngine.Gate.AND,120f,134f)
                label(p,120f,96f,if(jk)"J·Q′"else"T·Q′",size=10f);label(p,120f,206f,if(jk)"K·Q"else"T·Q",size=10f)
                wire(p,40f,50f,120f,41f,jIn);wire(p,40f,160f,120f,151f,kIn)
                pin(p,120f,41f,jIn);pin(p,120f,151f,kIn)
                // Feedback rails: Q′ over the top, Q along the bottom.
                track(p,listOf(452f to 160f,492f to 160f,492f to 20f,108f to 20f,108f to 59f,120f to 59f),qb)
                track(p,listOf(452f to 50f,474f to 50f,474f to 250f,120f to 250f,120f to 169f),q)
                pin(p,120f,59f,qb);pin(p,120f,169f,q)
                symbol(p,LogicEngine.Gate.AND,230f,24f);symbol(p,LogicEngine.Gate.AND,230f,134f)
                label(p,230f,96f,"·CLK",size=10f);label(p,230f,206f,"·CLK",size=10f)
                val js=if(qb==null)null else jIn and qb;val ks=if(q==null)null else kIn and q
                wire(p,172f,50f,230f,41f,js);wire(p,172f,160f,230f,151f,ks)
                wire(p,60f,244f,230f,59f,clk);wire(p,60f,244f,230f,169f,clk)
                symbol(p,LogicEngine.Gate.NOR,340f,24f);symbol(p,LogicEngine.Gate.NOR,340f,134f)
                wire(p,282f,50f,347f,151f,js?.and(clk));wire(p,282f,160f,347f,41f,ks?.and(clk))
                track(p,listOf(410f to 50f,452f to 50f),q);track(p,listOf(410f to 160f,452f to 160f),qb)
                pin(p,452f,50f,q);pin(p,452f,160f,qb)
                track(p,listOf(410f to 50f,436f to 50f,436f to 169f,347f to 169f),q)
                track(p,listOf(410f to 160f,436f to 160f,436f to 59f,347f to 59f),qb)
                pin(p,410f,50f,q);pin(p,410f,160f,qb)
                label(p,414f,42f,"Q=${q?:"X"}",p.tone(q));label(p,414f,187f,"Q′=${qb?:"X"}",p.tone(qb))
                if(!jk)label(p,120f,228f,p.text("J and K are tied together: T=0 holds, T=1 toggles"),size=10f)
            }
        }
        "COUNTER_3"->{
            DiagramSurface(560f,300f,"3-bit counter internals: ideal T stages with EN, clock and synchronous reset rails.",true){p->
                val en=bit(inputs[0]);val rst=bit(inputs[1]);val clk=bit(state.clock)
                val q=List(3){outputs[it]}
                label(p,8f,40f,"EN=$en",p.tone(en));label(p,8f,270f,"CLK=$clk",p.tone(clk));label(p,8f,292f,"RESET=$rst",p.tone(rst))
                track(p,listOf(60f to 36f,520f to 36f),en)
                track(p,listOf(60f to 266f,520f to 266f),clk)
                track(p,listOf(60f to 288f,520f to 288f),rst)
                // Stages left→right Q2 Q1 Q0; T0=EN, T1=EN·Q0, T2=EN·Q0·Q1.
                val xs=listOf(90f,230f,370f)
                xs.forEachIndexed{i,x->
                    stageCell(p,x,120f,110f,100f,"T stage Q${2-i}",q[i])
                    wire(p,x+55,266f,x+55,220f,clk)
                    wire(p,x+55,288f,x+75,220f,rst)
                    label(p,x+55,244f,"R",size=9f)
                }
                wire(p,425f,36f,425f,120f,en);pin(p,425f,120f,en)
                label(p,429f,112f,"T0",size=9f)
                symbol(p,LogicEngine.Gate.AND,270f,50f)
                wire(p,300f,36f,270f,67f,en);pin(p,270f,67f,en)
                track(p,listOf(435f to 120f,435f to 85f,270f to 85f),q[2])
                pin(p,435f,120f,q[2]);pin(p,270f,85f,q[2])
                wire(p,322f,76f,285f,120f,null);pin(p,285f,120f,null)
                label(p,270f,44f,"T1=EN·Q0",size=9f)
                symbol(p,LogicEngine.Gate.AND,130f,50f)
                wire(p,160f,36f,130f,63f,en);pin(p,130f,63f,en)
                track(p,listOf(435f to 110f,250f to 110f,250f to 76f,130f to 76f),q[2])
                pin(p,435f,110f,q[2])
                track(p,listOf(240f to 120f,240f to 89f,130f to 89f),q[1])
                pin(p,240f,120f,q[1])
                wire(p,182f,76f,145f,120f,null);pin(p,145f,120f,null)
                label(p,130f,44f,"T2=EN·Q0·Q1",size=9f)
                label(p,90f,252f,p.text("synchronous: every stage shares one clock edge"),size=10f)
            }
        }
        "SHIFT_4","RING_4","JOHNSON_4"->{
            val kind=when(s.id){"SHIFT_4"->"shift register";"RING_4"->"ring counter";else->"Johnson counter"}
            DiagramSurface(600f,280f,"4-bit $kind internals: ideal D stages with shared clock, enable and reset.",true){p->
                val hasD=s.id=="SHIFT_4"
                val dIn=if(hasD)bit(inputs[0])else 0;val en=bit(inputs[if(hasD)1 else 0]);val rst=bit(inputs[if(hasD)2 else 1]);val clk=bit(state.clock)
                val q=List(4){outputs[it]}
                if(hasD)label(p,8f,80f,"D=$dIn",p.tone(dIn))
                label(p,8f,230f,"EN=$en",p.tone(en));label(p,8f,252f,"CLK=$clk",p.tone(clk));label(p,8f,272f,"RESET=$rst",p.tone(rst))
                val xs=listOf(80f,200f,320f,440f)
                track(p,listOf(60f to 226f,580f to 226f),en)
                track(p,listOf(60f to 248f,580f to 248f),clk)
                track(p,listOf(60f to 268f,580f to 268f),rst)
                xs.forEachIndexed{i,x->
                    stageCell(p,x,110f,100f,90f,"D stage Q$i",q[3-i])
                    wire(p,x+30,226f,x+30,200f,en);wire(p,x+50,248f,x+50,200f,clk);wire(p,x+70,268f,x+70,200f,rst)
                }
                // Bits flow left→right toward the MSB: D→Q0→Q1→Q2→Q3.
                if(hasD){wire(p,50f,76f,80f,140f,dIn);pin(p,80f,140f,dIn);label(p,52f,66f,p.text("D in"),size=9f)}
                for(i in 0 until 3){val v=q[3-i];wire(p,xs[i]+100f,150f,xs[i+1],150f,v);pin(p,xs[i]+100f,150f,v);pin(p,xs[i+1],150f,v)}
                if(s.id=="RING_4"){track(p,listOf(540f to 150f,575f to 150f,575f to 60f,60f to 60f,60f to 150f,80f to 150f),q[0]);pin(p,80f,150f,q[0]);label(p,280f,52f,p.text("Q3 feeds back to D0"),size=10f)}
                if(s.id=="JOHNSON_4"){
                    track(p,listOf(540f to 150f,575f to 150f,575f to 60f,60f to 60f,60f to 150f,94f to 150f),q[0])
                    drawCircle(p.panel,5f,Offset(88f,150f));drawCircle(p.ink,5f,Offset(88f,150f),style=Stroke(1.6f))
                    pin(p,80f,150f,q[0])
                    label(p,280f,52f,p.text("inverted Q3 feeds back to D0"),size=10f)
                }
                if(hasD)label(p,80f,100f,p.text("serial in → Q0 ··· Q3 out"),size=10f)
            }
        }
    }
}
@Composable internal fun TimingDiagram(s:LogicEngine.Sequential,history:List<LogicEngine.Sample>){
    val labels=(if(s.clocked)listOf("CLK")else emptyList())+s.inputs+(if(s.width==1)listOf("Q","Q′")else(s.width-1 downTo 0).map{"Q$it"})
    val w=maxOf(320,60+history.size*42).toFloat();val h=40+labels.size*42f
    DiagramSurface(w,h,"Discrete action timing trace, not real time. Unknown states are marked X.",true){p->
        labels.forEachIndexed {r,name->val base=40+r*42f;label(p,8f,base,name,size=10f);var previous:Float?=null
            history.forEachIndexed{i,sample->val v=when{ name=="CLK"->if(sample.clock)1 else 0;name in s.inputs->if(sample.inputs[s.inputs.indexOf(name)])1 else 0;else->sample.outputs[if(name=="Q′")1 else if(name=="Q")0 else s.width-1-name.drop(1).toInt()] }
                val x=53+i*42f;val y=if(v==null)base-5 else if(v==1)base-15 else base+4
                if(previous!=null&&v!=null)drawLine(p.tone(v),Offset(x,previous!!),Offset(x,y),1.6f)
                drawLine(p.tone(v),Offset(x,y),Offset(x+42,y),1.8f);if(v==null)label(p,x+15,y-3,"X",p.unknown,9f)
                previous=if(v==null)null else y;if(r==0)label(p,x+19,15f,sample.tick.toString(),size=9f,center=true)
            }
        }
    }
}
