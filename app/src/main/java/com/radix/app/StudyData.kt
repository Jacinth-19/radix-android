package com.radix.app

import kotlin.math.floor

internal data class CircuitSession(val id:String,val name:String,val kind:String,val model:String,val width:Int,val inputs:List<Boolean>,val bits:Int?=null,val clock:Boolean=false)
internal data class StudySaved(val id:Long,val input:String,val base:Int)
internal data class StudyMap(val variables:Int,val cells:List<Int>,val form:String)
internal data class StudyExpression(val variables:Int=2,val left:String="A'B + AB'",val right:String="A XOR B")
internal data class StudyRepresentation(val width:Int=8,val signed:Boolean=true,val mode:String="decimal",val input:String="-13")
internal data class StudySettings(val haptics:Boolean=true,val largeControls:Boolean=false)
internal data class StudyConverter(val number:String,val base:Int,val custom:Int?,val bits:Int,val dark:Boolean)
internal data class StudyBackup(val saved:List<StudySaved>,val sessions:List<CircuitSession>,val progress:Set<String>,val kmap:StudyMap,val expression:StudyExpression,val representation:StudyRepresentation,val settings:StudySettings,val converter:StudyConverter)

/** Portable version 1 envelope used by Android and the browser. All import checks precede mutation. */
internal object StudyData {
    private fun obj(value:Any?,name:String):Map<*,*> = value as? Map<*,*> ?: throw IllegalArgumentException("$name must be an object.")
    private fun list(value:Any?,name:String,max:Int):List<*> {val v=value as? List<*>?:throw IllegalArgumentException("$name must be a list.");require(v.size<=max){"$name has too many entries (maximum $max)."};return v}
    private fun string(value:Any?,name:String,max:Int):String {val s=value as? String?:throw IllegalArgumentException("$name must be text.");require(s.length<=max){"$name is too long."};return s}
    private fun bool(value:Any?,name:String)=value as? Boolean?:throw IllegalArgumentException("$name must be true or false.")
    private fun integer(value:Any?,name:String,min:Long,max:Long):Long {
        val number=value as? Number?:throw IllegalArgumentException("$name must be an integer.");val d=number.toDouble()
        require(d.isFinite()&&d==floor(d)&&d>=min&&d<=max&&d in -9007199254740991.0..9007199254740991.0){"$name is out of range or not an exact integer."};return number.toLong()
    }
    fun session(value:Any?):CircuitSession {
        val o=obj(value,"Session");val id=string(o["id"],"Session ID",64);require(id.matches(Regex("[A-Za-z0-9_-]{1,64}"))){"Invalid session ID."}
        val name=string(o["name"],"Session name",40);require(name.isNotBlank()&&name.none{it.code<32}){"Give the session a name without control characters."}
        val kind=string(o["kind"],"Session kind",16);val model=string(o["model"],"Session model",32);val width=integer(o["width"],"Session width",1,20).toInt()
        val inputs=list(o["inputs"],"Session inputs",20).map{bool(it,"Input")}
        val bits=if(o["bits"]==null)null else integer(o["bits"],"Stored state",0,15).toInt();val clock=bool(o["clock"],"Clock")
        when(kind){
            "gates"->{val gate=LogicEngine.Gate.entries.firstOrNull{it.name==model}?:throw IllegalArgumentException("Unknown gate.");require(if(gate in listOf(LogicEngine.Gate.NOT,LogicEngine.Gate.BUFFER))width==1 else width in 2..4){"Invalid gate width."};require(inputs.size==width&&bits==null&&!clock){"Invalid gate session state."}}
            "combinational"->{val c=LogicCatalog.circuits.firstOrNull{it.id==model}?:throw IllegalArgumentException("Unknown circuit.");require(width==c.inputs.size&&inputs.size==c.inputs.size&&bits==null&&!clock){"Invalid combinational session state."}}
            "sequential"->{val s=LogicCatalog.sequential.firstOrNull{it.id==model}?:throw IllegalArgumentException("Unknown sequential model.");require(width==s.width&&inputs.size==s.inputs.size&&(bits==null||bits<(1 shl width))&&(s.clocked||!clock)){"Invalid sequential session state."};require(s.clocked||!LogicEngine.forbidden(s,inputs)||bits==null){"Forbidden latch inputs must have unknown stored memory."}}
            else->throw IllegalArgumentException("Unknown session kind.")
        }
        return CircuitSession(id,name,kind,model,width,inputs,bits,clock)
    }
    fun sessionMap(s:CircuitSession):Map<String,Any?> = linkedMapOf("id" to s.id,"name" to s.name,"kind" to s.kind,"model" to s.model,"width" to s.width,"inputs" to s.inputs,"bits" to s.bits,"clock" to s.clock)
    fun sessions(text:String):List<CircuitSession>{val rows=list(StudyJson.parse(text),"Sessions",24).map{session(it)};require(rows.map{it.id}.distinct().size==rows.size){"Duplicate session IDs."};return rows}
    fun sessionText(rows:List<CircuitSession>)=StudyJson.write(rows.map(::sessionMap))
    fun progress(text:String):Set<String>{val ids=list(StudyJson.parse(text),"Progress",PracticeCatalog.questions.size).map{string(it,"Question ID",64)};val allowed=PracticeCatalog.questions.map{it.id}.toSet();require(ids.all{it in allowed}&&ids.distinct().size==ids.size){"Unknown or duplicate practice question ID."};return ids.toSet()}
    fun decode(text:String):StudyBackup {
        val o=obj(StudyJson.parse(text),"Backup")
        require(o["format"]=="radix-study"&&integer(o["version"],"Backup version",1,100)==1L){"Unsupported backup format/version. Use a Radix study backup version 1."}
        val saves=list(o["saved"],"Saved conversions",50).map {row->val x=obj(row,"Saved conversion");val id=integer(x["id"],"Save ID",1,9007199254740991);val input=string(x["input"],"Saved input",600);val base=integer(x["base"],"Saved base",2,36).toInt();require(BaseConverter.parse(input,base) is BaseConverter.Parsed.Valid){"A saved conversion is invalid for its source base."};StudySaved(id,input,base)}
        require(saves.map{it.id}.distinct().size==saves.size){"Duplicate conversion IDs."}
        val sessions=list(o["sessions"],"Sessions",24).map(::session);require(sessions.map{it.id}.distinct().size==sessions.size){"Duplicate session IDs."}
        val progress=progress(StudyJson.write(o["progress"]))
        val k=obj(o["kmap"],"K-map");val n=integer(k["variables"],"Variables",2,6).toInt();val cells=list(k["cells"],"Cells",64).map{integer(it,"Cell",0,2).toInt()};val form=string(k["form"],"Form",3);require(cells.size==(1 shl n)&&form in listOf("SOP","POS")){"Invalid K-map dimensions/form."}
        val ex=obj(o["expression"],"Expression lab");val expression=StudyExpression(integer(ex["variables"],"Expression variables",2,6).toInt(),string(ex["left"],"Expression",256),string(ex["right"],"Comparison expression",256))
        val rep=obj(o["representation"],"Representation lab");val width=integer(rep["width"],"Bit width",4,64).toInt();val mode=string(rep["mode"],"Input mode",16);require(width in RepresentationEngine.widths&&mode in listOf("decimal","bits","gray")){"Invalid representation width/mode."}
        val representation=StudyRepresentation(width,bool(rep["signed"],"Signed mode"),mode,string(rep["input"],"Representation input",128))
        val settings=obj(o["settings"],"Settings");val prefs=StudySettings(bool(settings["haptics"],"Haptics"),bool(settings["largeControls"],"Large controls"))
        val cv=obj(o["converter"],"Converter");val base=integer(cv["base"],"Source base",2,36).toInt();val custom=if(cv["custom"]==null)null else integer(cv["custom"],"Custom base",2,36).toInt()
        val converter=StudyConverter(string(cv["number"],"Current number",600),base,custom,integer(cv["bits"],"Bit lab value",0,255).toInt(),bool(cv["dark"],"Theme"))
        return StudyBackup(saves,sessions,progress,StudyMap(n,cells,form),expression,representation,prefs,converter)
    }
    fun encode(b:StudyBackup):String = StudyJson.write(linkedMapOf(
        "format" to "radix-study","version" to 1,"appVersion" to "7.0.0",
        "saved" to b.saved.map{mapOf("id" to it.id,"input" to it.input,"base" to it.base)},"sessions" to b.sessions.map(::sessionMap),"progress" to b.progress.sorted(),
        "kmap" to mapOf("variables" to b.kmap.variables,"cells" to b.kmap.cells,"form" to b.kmap.form),
        "expression" to mapOf("variables" to b.expression.variables,"left" to b.expression.left,"right" to b.expression.right),
        "representation" to mapOf("width" to b.representation.width,"signed" to b.representation.signed,"mode" to b.representation.mode,"input" to b.representation.input),
        "settings" to mapOf("haptics" to b.settings.haptics,"largeControls" to b.settings.largeControls),
        "converter" to mapOf("number" to b.converter.number,"base" to b.converter.base,"custom" to b.converter.custom,"bits" to b.converter.bits,"dark" to b.converter.dark)
    ))
}
