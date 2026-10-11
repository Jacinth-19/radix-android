package com.radix.app

import android.app.Application
import androidx.core.content.edit
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import org.json.JSONArray
import org.json.JSONObject

data class SavedNumber(val id: Long, val input: String, val base: Int)

class RadixViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("radix_local", 0)
    var number by mutableStateOf(prefs.getString("number", "255") ?: "255")
        private set
    var sourceBase by mutableIntStateOf(prefs.getInt("source_base", 10).coerceIn(2, 36))
        private set
    var dark by mutableStateOf(prefs.getBoolean("dark", true))
        private set
    var tab by mutableIntStateOf(0)
    var customBase by mutableStateOf<Int?>(prefs.getInt("custom_base", 0).takeIf { it in 2..36 })
        private set
    var bitValue by mutableIntStateOf(prefs.getInt("bit_value",42).coerceIn(0,255))
        private set
    var saved by mutableStateOf(loadSaved())
        private set

    internal var logicSection by mutableIntStateOf(prefs.getInt("logic_section", 0).coerceIn(0, 2))
    internal var gateId by mutableStateOf(prefs.getString("logic_gate", "AND")?.takeIf { value -> LogicEngine.Gate.entries.any {it.name==value} } ?: "AND")
        private set
    internal var gateWidth by mutableIntStateOf(2)
        private set
    internal var gateInputs by mutableStateOf(listOf(true,true))
        private set
    internal var circuitId by mutableStateOf(prefs.getString("logic_circuit", "full-adder")?.takeIf { value->LogicCatalog.circuits.any{it.id==value} } ?: "full-adder")
        private set
    internal var circuitInputs by mutableStateOf(List(LogicCatalog.circuits.first{it.id==circuitId}.inputs.size){false})
        private set
    internal var sequentialId by mutableStateOf(prefs.getString("logic_sequential", "D_FF")?.takeIf{value->LogicCatalog.sequential.any{it.id==value}} ?: "D_FF")
        private set
    internal var sequentialInputs by mutableStateOf(LogicEngine.defaultInputs(LogicCatalog.sequential.first{it.id==sequentialId}))
        private set
    internal var sequenceState by mutableStateOf(LogicEngine.initial(LogicCatalog.sequential.first{it.id==sequentialId}))
        private set
    internal var sequenceHistory by mutableStateOf(listOf(LogicEngine.Sample(0,false,sequentialInputs,LogicEngine.outputs(LogicCatalog.sequential.first{it.id==sequentialId},sequenceState,sequentialInputs))))
        private set
    internal var tourSeen by mutableStateOf(prefs.getBoolean("tour_v3_seen",false))
        private set
    private var sequenceTick=0
    init {
        if (prefs.contains("app_language")) prefs.edit { remove("app_language") }
        if(gateId in listOf("NOT","BUFFER")){gateWidth=1;gateInputs=listOf(true)}
    }
    internal fun chooseGate(id:String)=editLogic {require(LogicEngine.Gate.entries.any{it.name==id});gateId=id;gateWidth=if(id in listOf("NOT","BUFFER"))1 else gateWidth.coerceIn(2,4);gateInputs=List(gateWidth){true};persistLogic()}
    internal fun chooseGateWidth(value:Int)=editLogic {require(value in 2..4 && gateId !in listOf("NOT","BUFFER"));gateWidth=value;gateInputs=List(value){false}}
    internal fun toggleGateInput(index:Int)=editLogic {gateInputs=gateInputs.mapIndexed{i,v->if(i==index)!v else v}}
    internal fun chooseCircuit(id:String)=editLogic {val c=LogicCatalog.circuits.first{it.id==id};circuitId=id;circuitInputs=List(c.inputs.size){false};persistLogic()}
    internal fun toggleCircuitInput(index:Int)=editLogic {circuitInputs=circuitInputs.mapIndexed{i,v->if(i==index)!v else v}}
    internal fun chooseSequential(id:String)=editLogic {require(LogicCatalog.sequential.any{it.id==id});sequentialId=id;resetSequenceRaw();persistLogic()}
    internal fun resetSequence()=editLogic {resetSequenceRaw()}
    private fun resetSequenceRaw(){val s=LogicCatalog.sequential.first{it.id==sequentialId};sequentialInputs=LogicEngine.defaultInputs(s);sequenceState=LogicEngine.initial(s);sequenceTick=0;sequenceHistory=listOf(LogicEngine.Sample(0,false,sequentialInputs,LogicEngine.outputs(s,sequenceState,sequentialInputs)))}
    internal fun toggleSequentialInput(index:Int)=editLogic {val s=LogicCatalog.sequential.first{it.id==sequentialId};sequentialInputs=sequentialInputs.mapIndexed{i,v->if(i==index)!v else v};sequenceState=LogicEngine.update(s,sequenceState,sequentialInputs,sequenceState.clock);recordSequence()}
    internal fun releaseLatchInputs()=editLogic {val s=LogicCatalog.sequential.first{it.id==sequentialId};require(s.id in listOf("SR_NOR","SR_NAND"));sequentialInputs=List(2){s.id=="SR_NAND"};sequenceState=LogicEngine.update(s,sequenceState,sequentialInputs,false);recordSequence()}
    internal fun setSequenceClock(clock:Boolean)=editLogic {clockRaw(clock)}
    private fun clockRaw(clock:Boolean){val s=LogicCatalog.sequential.first{it.id==sequentialId};require(s.clocked);sequenceState=LogicEngine.update(s,sequenceState,sequentialInputs,clock);recordSequence()}
    internal fun pulseSequence()=editLogic {if(sequenceState.clock)clockRaw(false);clockRaw(true);clockRaw(false)}
    private fun recordSequence(){val s=LogicCatalog.sequential.first{it.id==sequentialId};sequenceHistory=(sequenceHistory+LogicEngine.Sample(++sequenceTick,sequenceState.clock,sequentialInputs.toList(),LogicEngine.outputs(s,sequenceState,sequentialInputs))).takeLast(12)}
    internal fun markTourSeen(){tourSeen=true;prefs.edit{putBoolean("tour_v3_seen",true)}}
    internal fun persistLogic(){prefs.edit{putInt("logic_section",logicSection);putString("logic_gate",gateId);putString("logic_circuit",circuitId);putString("logic_sequential",sequentialId)}}
    internal fun logicToKMap(n:Int,cells:List<Int>,form:KMapEngine.Form=KMapEngine.Form.SOP,names:String=KMapEngine.names(n))=editMap {
        require(n in 2..6 && cells.size==(1 shl n));require(names.length==n&&names.all{it in 'A'..'Z'}&&names.toSet().size==n)
        kVariables=n;kNames=names;kCells=cells;kForm=form;syncKInputs();tab=3
    }

    var kVariables by mutableIntStateOf(prefs.getInt("k_variables", 2).coerceIn(2, 6))
        private set
    var kNames by mutableStateOf(runCatching {
        val value = prefs.getString("k_names", KMapEngine.names(kVariables)) ?: KMapEngine.names(kVariables)
        require(value.length == kVariables && value.all { it in 'A'..'Z' } && value.toSet().size == kVariables)
        value
    }.getOrDefault(KMapEngine.names(kVariables)))
        private set
    var kCells by mutableStateOf(runCatching {
        val array = JSONArray(prefs.getString("k_cells", if (kVariables == 2) "[0,1,1,1]" else "[]"))
        List(1 shl kVariables) { array.getInt(it).also { value -> require(value in 0..2) } }
    }.getOrDefault(List(1 shl kVariables) { if (kVariables == 2 && it != 0) 1 else 0 }))
        private set
    var kForm by mutableStateOf(runCatching { KMapEngine.Form.valueOf(prefs.getString("k_form", "SOP") ?: "SOP") }.getOrDefault(KMapEngine.Form.SOP))
        private set
    var kTerms by mutableStateOf(KMapEngine.indicesText(kCells, if (kForm == KMapEngine.Form.SOP) 1 else 0))
        private set
    var kDontCare by mutableStateOf(KMapEngine.indicesText(kCells, 2))
        private set
    var kError by mutableStateOf<String?>(null)
        private set
    fun selectKVariables(value: Int)=editMap {
        require(value in 2..6)
        kVariables = value; kNames = KMapEngine.names(value); kCells = List(1 shl value) { 0 }; syncKInputs()
    }
    fun selectKForm(value: KMapEngine.Form)=editMap { kForm = value; syncKInputs() }
    fun cycleKCell(index: Int)=editMap { kCells = kCells.mapIndexed { i, value -> if (i == index) (value + 1) % 3 else value }; syncKInputs() }
    fun resetK()=editMap { kCells = List(1 shl kVariables) { 0 }; syncKInputs() }
    fun updateKTerms(value: String)=editMap { kTerms = value.take(256); parseKInputs() }
    fun updateKDontCare(value: String)=editMap { kDontCare = value.take(256); parseKInputs() }
    fun loadKExercise(exercise: KMapEngine.Exercise)=editMap {
        kVariables = exercise.variables; kNames = KMapEngine.names(exercise.variables); kCells = exercise.cells(); kForm = exercise.form; syncKInputs()
    }
    private fun parseKInputs() {
        when (val result = KMapEngine.fromLists(kVariables, kTerms, kDontCare, kForm)) {
            is KMapEngine.Input.Invalid -> kError = result.message
            is KMapEngine.Input.Valid -> { kError = null; kCells = result.cells; persistK() }
        }
    }
    private fun syncKInputs() {
        kTerms = KMapEngine.indicesText(kCells, if (kForm == KMapEngine.Form.SOP) 1 else 0)
        kDontCare = KMapEngine.indicesText(kCells, 2); kError = null; persistK()
    }
    private fun persistK() {
        prefs.edit {
            putInt("k_variables", kVariables); putString("k_names", kNames); putString("k_form", kForm.name)
            putString("k_cells", JSONArray(kCells).toString())
        }
    }

    fun updateNumber(value: String) {
        // Keep intermediate editing states; validation lives in the pure engine.
        number = value.take(600)
        prefs.edit { putString("number", number) }
    }
    fun selectSource(base: Int) {
        require(base in 2..36)
        sourceBase = base
        prefs.edit { putInt("source_base", base) }
    }
    fun setCustom(base: Int?) {
        require(base == null || base in 2..36)
        customBase = base
        prefs.edit { putInt("custom_base", base ?: 0) }
    }
    fun toggleTheme() {
        dark = !dark;themeMode=if(dark)0 else 1
        prefs.edit { putBoolean("dark", dark);putInt("theme_mode",themeMode) }
    }
    fun save(): Boolean {
        if (BaseConverter.parse(number, sourceBase) !is BaseConverter.Parsed.Valid) return false
        val input = number.trim()
        val old = saved.firstOrNull { it.input == input && it.base == sourceBase }
        val item = SavedNumber(old?.id ?: System.currentTimeMillis(), input, sourceBase)
        saved = (listOf(item) + saved.filterNot { it.id == item.id }).take(50)
        persistSaved()
        return true
    }
    fun delete(id: Long) {
        saved = saved.filterNot { it.id == id }
        persistSaved()
    }
    fun restore(item: SavedNumber) {
        selectSource(item.base)
        updateNumber(item.input)
        tab = 0
    }
    fun openLab() {
        val value = (BaseConverter.parse(number, sourceBase) as? BaseConverter.Parsed.Valid)?.value
        if (value != null && value.isInteger && value.numerator.signum() >= 0 && value.numerator.bitLength() <= 8) {
            setBits(value.numerator.toInt())
        }
        tab = 2
    }
    fun toggleBit(index: Int) { require(index in 0..7); setBits(bitValue xor (1 shl index)) }
    fun setBits(value: Int) { bitValue = value.coerceIn(0, 255); prefs.edit { putInt("bit_value",bitValue) } }
    fun useBits() {
        selectSource(10)
        updateNumber(bitValue.toString())
        tab = 0
    }
    fun surprise() {
        val candidate = listOf(42, 255, 1024, 1337, 65535).filter { it.toString(sourceBase).uppercase() != number }.random()
        updateNumber(candidate.toString(sourceBase).uppercase())
    }
    private fun persistSaved() {
        val array = JSONArray()
        saved.forEach { array.put(JSONObject().put("id", it.id).put("input", it.input).put("base", it.base)) }
        prefs.edit { putString("saved", array.toString()) }
    }
    private fun loadSaved(): List<SavedNumber> = runCatching {
        val array = JSONArray(prefs.getString("saved", "[]"))
        (0 until minOf(array.length(), 50)).mapNotNull { index ->
            val item = array.getJSONObject(index)
            val input = item.getString("input")
            val base = item.getInt("base")
            if (BaseConverter.parse(input, base) is BaseConverter.Parsed.Valid)
                SavedNumber(item.getLong("id"), input, base) else null
        }
    }.getOrDefault(emptyList())
    internal var expression by mutableStateOf(runCatching {val o=StudyJson.parse(prefs.getString("study_expression","{}")?:"{}") as Map<*,*>;val form=(o["form"] as? String?:prefs.getString("study_expression_form","SOP")?:"SOP");require(form in listOf("SOP","POS"));StudyExpression((o["variables"] as Number).toInt().coerceIn(2,6),(o["left"] as String).take(256),(o["right"] as String).take(256),form)}.getOrDefault(StudyExpression()))
        private set
    internal var expressionResultForm by mutableStateOf(runCatching{KMapEngine.Form.valueOf(expression.form)}.getOrDefault(KMapEngine.Form.SOP))
        private set
    internal var representation by mutableStateOf(runCatching {val o=StudyJson.parse(prefs.getString("study_representation","{}")?:"{}") as Map<*,*>;val w=(o["width"] as Number).toInt();require(w in RepresentationEngine.widths);val mode=o["mode"] as String;require(mode in listOf("decimal","bits","gray"));StudyRepresentation(w,o["signed"] as Boolean,mode,(o["input"] as String).take(128))}.getOrDefault(StudyRepresentation()))
        private set
    internal var sessions by mutableStateOf(runCatching{StudyData.sessions(prefs.getString("study_sessions","[]")?:"[]")}.getOrDefault(emptyList()))
        private set
    internal var savedExpressions by mutableStateOf(runCatching{StudyData.savedExpressions(prefs.getString("study_saved_expressions","[]")?:"[]")}.getOrDefault(emptyList()))
        private set
    internal var conversionHistory by mutableStateOf(runCatching{StudyData.conversionHistory(prefs.getString("conversion_history","[]")?:"[]")}.getOrDefault(emptyList()))
        private set
    internal var practiceProgress by mutableStateOf(runCatching{StudyData.progress(prefs.getString("study_progress","[]")?:"[]")}.getOrDefault(emptySet()))
        private set
    internal var practiceIndex by mutableIntStateOf(prefs.getInt("study_question",0).coerceIn(0,PracticeCatalog.questions.lastIndex))
        private set
    internal var haptics by mutableStateOf(prefs.getBoolean("study_haptics",true))
        private set
    internal var largeControls by mutableStateOf(prefs.getBoolean("study_large_controls",false))
        private set
    internal fun setExpression(value:StudyExpression){require(value.variables in 2..6){"Choose 2 to 6 variables."};require(value.form in listOf("SOP","POS")){"Choose SOP or POS."};expression=value.copy(left=value.left.take(256),right=value.right.take(256));expressionResultForm=KMapEngine.Form.valueOf(expression.form);prefs.edit{putString("study_expression",StudyJson.write(mapOf("variables" to expression.variables,"left" to expression.left,"right" to expression.right,"form" to expression.form)));putString("study_expression_form",expression.form)}}
    internal fun setExpressionResultForm(value:KMapEngine.Form){setExpression(expression.copy(form=value.name))}
    internal fun setRepresentation(value:StudyRepresentation){representation=value.copy(input=value.input.take(128));prefs.edit{putString("study_representation",StudyJson.write(mapOf("width" to value.width,"signed" to value.signed,"mode" to value.mode,"input" to representation.input)))}}
    internal fun saveExpressionSolution(value:StudyExpression,form:KMapEngine.Form):Boolean {
        val source=value.left.trim();require(source.isNotEmpty()){"Enter an expression before saving."};ExpressionEngine.report(source,value.variables)
        val same=savedExpressions.firstOrNull{it.variables==value.variables&&it.expression==source&&it.form==form.name}
        if(same!=null){savedExpressions=listOf(same)+savedExpressions.filterNot{it.id==same.id};persistStudy();return false}
        require(savedExpressions.size<50){"Saved Boolean-solution library is full (50). Delete an older item first."}
        val id=maxOf(System.currentTimeMillis(),(savedExpressions.maxOfOrNull{it.id}?:0L)+1L)
        savedExpressions=(listOf(StudySavedExpression(id,value.variables,source,form.name))+savedExpressions).take(50);persistStudy();return true
    }
    internal fun deleteSavedExpression(id:Long){savedExpressions=savedExpressions.filterNot{it.id==id};persistStudy()}
    internal fun openSavedExpression(item:StudySavedExpression){setExpression(StudyExpression(item.variables,item.expression,""));setExpressionResultForm(KMapEngine.Form.valueOf(item.form));tab=6}
    internal fun recordConversionHistory(input:String,base:Int){
        val clean=input.trim();if(base !in 2..36||BaseConverter.parse(clean,base)!is BaseConverter.Parsed.Valid)return
        if(conversionHistory.firstOrNull()?.let{it.input==clean&&it.base==base}==true)return
        val id=maxOf(System.currentTimeMillis(),(conversionHistory.maxOfOrNull{it.id}?:0L)+1L)
        conversionHistory=(listOf(StudyConversionHistory(id,clean,base))+conversionHistory).take(100)
        prefs.edit{putString("conversion_history",StudyData.conversionHistoryText(conversionHistory))}
    }
    internal fun deleteHistoryEntry(id:Long){conversionHistory=conversionHistory.filterNot{it.id==id};prefs.edit{putString("conversion_history",StudyData.conversionHistoryText(conversionHistory))}}
    internal fun clearConversionHistory(){conversionHistory=emptyList();prefs.edit{putString("conversion_history","[]")}}
    internal fun restoreHistoryEntry(item:StudyConversionHistory){selectSource(item.base);updateNumber(item.input);tab=0}
    internal fun selectQuestion(index:Int){practiceIndex=index.coerceIn(0,PracticeCatalog.questions.lastIndex);prefs.edit{putInt("study_question",practiceIndex)}}
    internal fun completeQuestion(id:String){val q=PracticeCatalog.questions.firstOrNull{it.id==id}?:throw IllegalArgumentException("Unknown question.");practiceProgress=practiceProgress+id;persistStudy();recordPracticeDay();awardGameXp(q.tier);GameWidget.refreshAll(getApplication())}
    private fun recordPracticeDay(){
        val today=java.time.LocalDate.now().toString();val last=prefs.getString("last_practice_day","")?:""
        val yesterday=java.time.LocalDate.now().minusDays(1).toString()
        streakDays=when{last==today->streakDays;last==yesterday->streakDays+1;else->1}
        prefs.edit{putString("last_practice_day",today);putInt("streak_days",streakDays)}
        if(streakDays in StudyReminder.milestones)StudyReminder.milestone(getApplication(),streakDays)
        inbox=StudyReminder.loadInbox(getApplication())
    }
    internal fun markOpened(){
        val today=java.time.LocalDate.now();val last=runCatching{java.time.LocalDate.parse(prefs.getString("last_open_day","")?:"")}.getOrNull()
        prefs.edit{putString("last_open_day",today.toString())}
        GameWidget.refreshAll(getApplication())
        if(last!=null&&last.until(today,java.time.temporal.ChronoUnit.DAYS)>=3){
            pushInbox(when(effectiveLanguage){"fr"->"Bon retour !";"es"->"¡Bienvenido de nuevo!";"pt"->"Boas-vindas de volta!";else->"Welcome back!"},when(effectiveLanguage){"fr"->"Reprends avec une carte facile — ta progression est intacte.";"es"->"Retoma con una tarjeta fácil — tu progreso está intacto.";"pt"->"Retome com uma questão fácil — seu progresso está intacto.";else->"Pick up with one easy card — your progress is intact."})
        }
    }
    internal fun resetProgress(){practiceProgress=emptySet();persistStudy()}
    internal fun setHaptics(value:Boolean){haptics=value;persistStudy()}
    internal fun setLargeControls(value:Boolean){largeControls=value;persistStudy()}
    private fun persistStudy(){prefs.edit{putString("study_sessions",StudyData.sessionText(sessions));putString("study_saved_expressions",StudyData.savedExpressionText(savedExpressions));putString("conversion_history",StudyData.conversionHistoryText(conversionHistory));putString("study_progress",StudyJson.write(practiceProgress.sorted()));putBoolean("study_haptics",haptics);putBoolean("study_large_controls",largeControls)}}
    internal fun saveCircuitSession(name:String){
        require(sessions.size<24){"Session library is full. Delete or back up an older checkpoint first."};val clean=name.trim().take(40);val id=java.util.UUID.randomUUID().toString()
        val session=when(logicSection){
            0->CircuitSession(id,clean,"gates",gateId,gateInputs.size,gateInputs.toList())
            1->CircuitSession(id,clean,"combinational",circuitId,circuitInputs.size,circuitInputs.toList())
            else->{val c=LogicCatalog.sequential.first{it.id==sequentialId};CircuitSession(id,clean,"sequential",sequentialId,c.width,sequentialInputs.toList(),sequenceState.bits,sequenceState.clock)}
        };StudyData.session(StudyData.sessionMap(session));sessions=(listOf(session)+sessions).take(24);persistStudy()
    }
    internal fun deleteSession(id:String){sessions=sessions.filterNot{it.id==id};persistStudy()}
    internal fun restoreSession(session:CircuitSession)=editLogic {
        StudyData.session(StudyData.sessionMap(session))
        when(session.kind){
            "gates"->{logicSection=0;gateId=session.model;gateWidth=session.width;gateInputs=session.inputs.toList()}
            "combinational"->{logicSection=1;circuitId=session.model;circuitInputs=session.inputs.toList()}
            else->{logicSection=2;sequentialId=session.model;sequentialInputs=session.inputs.toList();sequenceState=LogicEngine.State(session.bits,session.clock);sequenceTick=0;val c=LogicCatalog.sequential.first{it.id==sequentialId};sequenceHistory=listOf(LogicEngine.Sample(0,session.clock,sequentialInputs,LogicEngine.outputs(c,sequenceState,sequentialInputs)))}
        };persistLogic();tab=4
    }
    private data class LogicSnapshot(val section:Int,val gate:String,val width:Int,val gateInputs:List<Boolean>,val circuit:String,val circuitInputs:List<Boolean>,val seq:String,val seqInputs:List<Boolean>,val state:LogicEngine.State,val history:List<LogicEngine.Sample>,val tick:Int)
    private val logicHistory=StudyHistory<LogicSnapshot>()
    internal var logicCanUndo by mutableStateOf(false);private set
    internal var logicCanRedo by mutableStateOf(false);private set
    private fun logicSnapshot()=LogicSnapshot(logicSection,gateId,gateWidth,gateInputs,circuitId,circuitInputs,sequentialId,sequentialInputs,sequenceState,sequenceHistory,sequenceTick)
    private fun applyLogic(s:LogicSnapshot){logicSection=s.section;gateId=s.gate;gateWidth=s.width;gateInputs=s.gateInputs;circuitId=s.circuit;circuitInputs=s.circuitInputs;sequentialId=s.seq;sequentialInputs=s.seqInputs;sequenceState=s.state;sequenceHistory=s.history;sequenceTick=s.tick;persistLogic()}
    private fun syncLogicHistory(){logicCanUndo=logicHistory.canUndo;logicCanRedo=logicHistory.canRedo}
    private fun editLogic(action:()->Unit){val before=logicSnapshot();action();logicHistory.record(before,logicSnapshot());syncLogicHistory()}
    internal fun undoLogic(){logicHistory.undo(logicSnapshot())?.let(::applyLogic);syncLogicHistory()}
    internal fun redoLogic(){logicHistory.redo(logicSnapshot())?.let(::applyLogic);syncLogicHistory()}
    private data class MapSnapshot(val variables:Int,val names:String,val cells:List<Int>,val form:KMapEngine.Form,val terms:String,val dc:String,val error:String?)
    private val mapHistory=StudyHistory<MapSnapshot>()
    internal var mapCanUndo by mutableStateOf(false);private set
    internal var mapCanRedo by mutableStateOf(false);private set
    private fun mapSnapshot()=MapSnapshot(kVariables,kNames,kCells,kForm,kTerms,kDontCare,kError)
    private fun applyMap(s:MapSnapshot){kVariables=s.variables;kNames=s.names;kCells=s.cells;kForm=s.form;kTerms=s.terms;kDontCare=s.dc;kError=s.error;persistK()}
    private fun syncMapHistory(){mapCanUndo=mapHistory.canUndo;mapCanRedo=mapHistory.canRedo}
    private fun editMap(action:()->Unit){val before=mapSnapshot();action();mapHistory.record(before,mapSnapshot());syncMapHistory()}
    internal fun undoMap(){mapHistory.undo(mapSnapshot())?.let(::applyMap);syncMapHistory()}
    internal fun redoMap(){mapHistory.redo(mapSnapshot())?.let(::applyMap);syncMapHistory()}
    internal fun backupText()=StudyData.encode(StudyBackup(saved.map{StudySaved(it.id,it.input,it.base)},sessions,practiceProgress,StudyMap(kVariables,kCells,kForm.name,kNames),expression,representation,StudySettings(haptics,largeControls),StudyConverter(number,sourceBase,customBase,bitValue,dark),savedExpressions,conversionHistory))
    internal fun restoreBackup(b:StudyBackup){
        // Validate once more before any preference/state mutation; draft expressions may remain invalid editing states.
        val checked=StudyData.decode(StudyData.encode(b))
        saved=checked.saved.map{SavedNumber(it.id,it.input,it.base)};sessions=checked.sessions;savedExpressions=checked.savedExpressions;conversionHistory=checked.conversionHistory;practiceProgress=checked.progress
        kVariables=checked.kmap.variables;kNames=checked.kmap.names;kCells=checked.kmap.cells;kForm=KMapEngine.Form.valueOf(checked.kmap.form);syncKInputs()
        setExpression(checked.expression);setRepresentation(checked.representation);haptics=checked.settings.haptics;largeControls=checked.settings.largeControls
        number=checked.converter.number;sourceBase=checked.converter.base;customBase=checked.converter.custom;bitValue=checked.converter.bits;dark=checked.converter.dark;themeMode=if(dark)0 else 1
        prefs.edit{putString("number",number);putInt("source_base",sourceBase);putInt("custom_base",customBase?:0);putBoolean("dark",dark);putInt("bit_value",bitValue)}
        persistSaved();persistStudy();mapHistory.clear();logicHistory.clear();syncMapHistory();syncLogicHistory()
    }

    // ---- Radix 5: first run, theme, language, reminders, streaks, inbox, tiers ----
    internal val isUpgrader: Boolean get()=prefs.contains("tour_v3_seen")||prefs.contains("saved")||prefs.contains("study_sessions")||prefs.contains("study_progress")||prefs.contains("dark")
    internal var onboardingDone by mutableStateOf(prefs.getBoolean("onboarding_done",false))
        private set
    internal var lastSeenCode by mutableIntStateOf(prefs.getInt("last_seen_code",0))
        private set
    internal fun completeOnboarding(){onboardingDone=true;markVersionSeen();prefs.edit{putBoolean("onboarding_done",true)}}
    internal fun markVersionSeen(){lastSeenCode=APP_VERSION_CODE;prefs.edit{putInt("last_seen_code",APP_VERSION_CODE)}}
    internal var themeMode by mutableIntStateOf(prefs.getInt("theme_mode",0).coerceIn(0,2))
        private set
    internal fun setThemeMode(mode:Int,systemDark:Boolean){themeMode=mode.coerceIn(0,2);dark=when(themeMode){0->true;1->false;else->systemDark};prefs.edit{putInt("theme_mode",themeMode);putBoolean("dark",dark)}}
    internal fun applySystemTheme(systemDark:Boolean){if(themeMode==2&&dark!=systemDark){dark=systemDark;prefs.edit{putBoolean("dark",dark)}}}
    internal val effectiveLanguage:String get()=AppStrings.systemLanguage()
    internal var notifEnabled by mutableStateOf(prefs.getBoolean("notif_enabled",false))
        private set
    internal var notifHour by mutableIntStateOf(prefs.getInt("notif_hour",19).coerceIn(0,23))
        private set
    internal var notifMinute by mutableIntStateOf(prefs.getInt("notif_minute",0).coerceIn(0,59))
        private set
    internal var notifStreakTopic by mutableStateOf(prefs.getBoolean("notif_streak",true))
        private set
    internal var notifTipsTopic by mutableStateOf(prefs.getBoolean("notif_tips",true))
        private set
    internal fun setNotifEnabled(value:Boolean){notifEnabled=value;prefs.edit{putBoolean("notif_enabled",value)};StudyReminder.schedule(getApplication())}
    internal fun setNotifTime(hour:Int,minute:Int){notifHour=hour.coerceIn(0,23);notifMinute=minute.coerceIn(0,59);prefs.edit{putInt("notif_hour",notifHour);putInt("notif_minute",notifMinute)};StudyReminder.schedule(getApplication())}
    internal fun setNotifTopics(streak:Boolean,tips:Boolean){notifStreakTopic=streak;notifTipsTopic=tips;prefs.edit{putBoolean("notif_streak",streak);putBoolean("notif_tips",tips)}}
    internal var streakDays by mutableIntStateOf(prefs.getInt("streak_days",0))
        private set
    internal var inbox by mutableStateOf(StudyReminder.loadInbox(application))
        private set
    internal fun pushInbox(title:String,body:String){StudyReminder.pushInbox(getApplication(),title,body);inbox=StudyReminder.loadInbox(getApplication())}
    internal fun clearInbox(){StudyReminder.clearInbox(getApplication());inbox=emptyList()}
    internal var practiceTier by mutableIntStateOf(prefs.getInt("practice_tier",0).coerceIn(0,3))
        private set
    internal fun setPracticeTier(value:Int){practiceTier=value.coerceIn(0,3);prefs.edit{putInt("practice_tier",practiceTier)}}

    // ---- Radix 6: XP/leaderboard, circuit builder, float lab, live sim ----
    internal var gameProfiles by mutableStateOf(loadGameProfiles())
        private set
    internal var gameActive by mutableIntStateOf(prefs.getInt("game_active",0).coerceAtLeast(0))
        private set
    internal var lastAward by mutableStateOf<String?>(null)
        private set
    internal val gameXp:Int get()=gameProfiles.getOrNull(gameActive)?.xp?:0
    internal val gameLevel:Int get()=GameEngine.level(gameXp)
    private fun loadGameProfiles():List<GameEngine.Profile> = runCatching{
        val array=JSONArray(prefs.getString("game_profiles","[]")?:"[]")
        val rows=(0 until minOf(array.length(),8)).map{GameEngine.Profile(GameEngine.cleanName(array.getJSONObject(it).getString("name")),array.getJSONObject(it).getInt("xp").coerceAtLeast(0))}
        rows.ifEmpty{listOf(GameEngine.Profile("Player 1",0))}
    }.getOrDefault(listOf(GameEngine.Profile("Player 1",0)))
    private fun persistGame(){val array=JSONArray();gameProfiles.forEach{array.put(JSONObject().put("name",it.name).put("xp",it.xp))};prefs.edit{putString("game_profiles",array.toString());putInt("game_active",gameActive.coerceIn(gameProfiles.indices))};gameActive=gameActive.coerceIn(gameProfiles.indices)}
    private fun awardGameXp(tier:Int){
        val before=gameLevel;val gain=GameEngine.xpAward(tier,streakDays)
        gameProfiles=gameProfiles.mapIndexed{i,p->if(i==gameActive)p.copy(xp=p.xp+gain)else p};persistGame()
        val after=gameLevel
        lastAward=if(after>before)"Level $after ${GameEngine.levelName(after)}! +$gain XP" else "+$gain XP"
        if(after>before)pushInbox(when(effectiveLanguage){"fr"->"Niveau $after !";"es"->"¡Nivel $after!";"pt"->"Nível $after!";else->"Level $after!"},when(effectiveLanguage){"fr"->"${GameEngine.levelName(after)} : +$gain XP pour cette carte.";"es"->"${GameEngine.levelName(after)}: +$gain XP por esta tarjeta.";"pt"->"${GameEngine.levelName(after)}: +$gain XP por esta questão.";else->"${GameEngine.levelName(after)} — +$gain XP for that card."})
    }
    internal fun clearAward(){lastAward=null}
    internal fun addProfile(name:String){val clean=GameEngine.cleanName(name);require(gameProfiles.size<8){"Leaderboard holds 8 players."};require(gameProfiles.none{it.name.equals(clean,true)}){"That name is taken."};gameProfiles=gameProfiles+GameEngine.Profile(clean,0);gameActive=gameProfiles.lastIndex;persistGame()}
    internal fun selectProfile(index:Int){require(index in gameProfiles.indices){"Unknown player."};gameActive=index;persistGame()}
    internal fun deleteProfile(index:Int){require(gameProfiles.size>1){"Keep at least one player."};require(index in gameProfiles.indices){"Unknown player."};gameProfiles=gameProfiles.filterIndexed{i,_->i!=index};gameActive=0;persistGame()}
    internal var builderBoard by mutableStateOf(loadBuilder())
        private set
    internal var builderWiring by mutableStateOf(false)
        private set
    internal var builderPending by mutableStateOf<Pair<Int,Int>?>(null)
        private set
    internal var builderPreset by mutableIntStateOf(-1)
        private set
    private fun loadBuilder():BuilderEngine.Board = runCatching{
        val o=JSONObject(prefs.getString("builder_board","{}")?:"{}")
        val gates=(0 until o.getJSONArray("gates").length()).map{val g=o.getJSONArray("gates").getJSONObject(it);BuilderEngine.Placed(g.getInt("id"),LogicEngine.Gate.valueOf(g.getString("gate")),g.getDouble("x").toFloat().coerceIn(0f,1f),g.getDouble("y").toFloat().coerceIn(0f,1f))}
        val wires=(0 until o.getJSONArray("wires").length()).map{val w=o.getJSONArray("wires").getJSONObject(it);BuilderEngine.Wire(w.getInt("from"),w.getInt("fromPin"),w.getInt("to"),w.getInt("toPin"))}
        val ext=(0 until 4).map{o.getJSONArray("ext").getBoolean(it)}
        BuilderEngine.Board(gates,wires,ext).also{require(BuilderEngine.validate(it).size<=6)}
    }.getOrDefault(BuilderEngine.presetHalfAdder())
    private fun persistBuilder(){val o=JSONObject();val g=JSONArray();builderBoard.gates.forEach{g.put(JSONObject().put("id",it.id).put("gate",it.gate.name).put("x",it.x.toDouble()).put("y",it.y.toDouble()))};val w=JSONArray();builderBoard.wires.forEach{w.put(JSONObject().put("from",it.fromId).put("fromPin",it.fromPin).put("to",it.toId).put("toPin",it.toPin))};val e=JSONArray();builderBoard.ext.forEach{e.put(it)};o.put("gates",g);o.put("wires",w);o.put("ext",e);prefs.edit{putString("builder_board",o.toString())}}
    internal fun builderAdd(gate:LogicEngine.Gate){val next=(builderBoard.gates.maxOfOrNull{it.id}?:-1)+1;builderBoard=builderBoard.copy(gates=builderBoard.gates+BuilderEngine.Placed(next,gate,0.08f+0.1f*(next%5),0.08f+0.12f*((next/5)%4)));builderPreset=-1;persistBuilder()}
    internal fun builderMove(id:Int,x:Float,y:Float){builderBoard=builderBoard.copy(gates=builderBoard.gates.map{if(it.id==id)it.copy(x=x.coerceIn(0f,1f),y=y.coerceIn(0f,1f))else it});persistBuilder()}
    internal fun builderDelete(id:Int){builderBoard=builderBoard.copy(gates=builderBoard.gates.filterNot{it.id==id},wires=builderBoard.wires.filterNot{it.fromId==id||it.toId==id});builderPreset=-1;if(builderPending?.first==id)builderPending=null;persistBuilder()}
    internal fun builderToggleExt(index:Int){require(index in 0..3);builderBoard=builderBoard.copy(ext=builderBoard.ext.mapIndexed{i,v->if(i==index)!v else v});persistBuilder()}
    internal fun builderSetWiring(value:Boolean){builderWiring=value;builderPending=null}
    internal fun builderTapSource(id:Int,pin:Int){builderPending=id to pin}
    internal fun builderTapTarget(id:Int,pin:Int){
        val src=builderPending?:return
        if(src.first==id&&id>=0){builderPending=null;return}
        builderBoard=builderBoard.copy(wires=builderBoard.wires.filterNot{it.toId==id&&it.toPin==pin}+BuilderEngine.Wire(src.first,src.second,id,pin))
        builderPreset=-1;builderPending=null;persistBuilder()
    }
    internal fun builderDeleteWireTo(id:Int,pin:Int){builderBoard=builderBoard.copy(wires=builderBoard.wires.filterNot{it.toId==id&&it.toPin==pin});builderPreset=-1;persistBuilder()}
    internal fun builderLoadPreset(index:Int){val presets=BuilderEngine.presets();require(index in presets.indices);builderBoard=presets[index].second;builderPreset=index;builderPending=null;persistBuilder()}
    internal fun builderClear(){builderBoard=BuilderEngine.Board(emptyList(),emptyList(),List(4){false});builderPreset=-1;builderPending=null;persistBuilder()}
    internal var floatWidth by mutableIntStateOf(prefs.getInt("float_width",32).let{if(it==64)64 else 32})
        private set
    internal var floatMode by mutableStateOf(prefs.getString("float_mode","dec")?.takeIf{it in listOf("dec","bits","hex")}?:"dec")
        private set
    internal var floatInput by mutableStateOf(prefs.getString("float_input","0.1")?:"0.1")
        private set
    internal fun setFloat(width:Int,mode:String,input:String){floatWidth=if(width==64)64 else 32;floatMode=if(mode in listOf("dec","bits","hex"))mode else "dec";floatInput=input.take(128);prefs.edit{putInt("float_width",floatWidth);putString("float_mode",floatMode);putString("float_input",floatInput)}}
    internal var livePlaying by mutableStateOf(false)
    internal var liveSpeed by mutableIntStateOf(prefs.getInt("live_speed",1).coerceIn(1,4))
    internal var livePeriodNs by mutableIntStateOf(prefs.getInt("live_period",200).coerceIn(10,10000))
    internal fun chooseLiveSpeed(value:Int){liveSpeed=value.coerceIn(1,4);prefs.edit{putInt("live_speed",liveSpeed)}}
    internal fun setLivePeriod(value:Int){livePeriodNs=value.coerceIn(10,10000);prefs.edit{putInt("live_period",livePeriodNs)}}
}
