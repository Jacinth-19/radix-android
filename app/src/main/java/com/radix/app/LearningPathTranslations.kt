package com.radix.app

/** Titles and explanations in the guided path, onboarding, and Boolean-law catalog. */
internal object LearningPathTranslations {
    private data class Text3(val fr:String,val es:String,val pt:String)
    private val entries=mapOf(
        "Number systems & base conversion" to Text3("Systèmes numériques et conversion de base","Sistemas numéricos y conversión de base","Sistemas numéricos e conversão de base"),
        "Signed numbers & representations" to Text3("Nombres signés et représentations","Números con signo y representaciones","Números com sinal e representações"),
        "Logic gates & Boolean laws" to Text3("Portes logiques et lois booléennes","Puertas lógicas y leyes booleanas","Portas lógicas e leis booleanas"),
        "Expressions, operators & truth tables" to Text3("Expressions, opérateurs et tables de vérité","Expresiones, operadores y tablas de verdad","Expressões, operadores e tabelas-verdade"),
        "Karnaugh maps & minimization" to Text3("Cartes de Karnaugh et simplification","Mapas de Karnaugh y minimización","Mapas de Karnaugh e minimização"),
        "Combinational circuits" to Text3("Circuits combinatoires","Circuitos combinacionales","Circuitos combinacionais"),
        "Sequential logic & memory" to Text3("Logique séquentielle et mémoire","Lógica secuencial y memoria","Lógica sequencial e memória"),
        "Easy — first wins" to Text3("Facile — les premiers points comptent","Fácil — cada acierto cuenta","Fácil — cada acerto conta"),
        "Intermediate — exam drills" to Text3("Intermédiaire — entraînement aux examens","Intermedio — ejercicios de examen","Intermediário — exercícios para exames"),
        "Advanced — prove it" to Text3("Avancé — démontrez-le","Avanzado — demuéstralo","Avançado — prove"),
        "Identity & null" to Text3("Identité et élément nul","Identidad y elemento nulo","Identidade e elemento nulo"),
        "Exam favorite: kill 0s and 1s before you draw anything." to Text3("Grand classique des examens : éliminez les 0 et les 1 avant de dessiner quoi que ce soit.","Típico de examen: elimina los 0 y los 1 antes de dibujar.","Clássico de prova: elimine os 0 e os 1 antes de desenhar."),
        "Idempotent, complement & negation" to Text3("Idempotence, complément et négation","Idempotencia, complemento y negación","Idempotência, complemento e negação"),
        "A term ORed/ANDed with itself changes nothing; a variable plus its complement covers everything." to Text3("Un terme combiné par OR ou AND avec lui-même ne change pas ; une variable associée à son complément couvre tous les cas.","Combinar un término consigo mismo con OR o AND no lo cambia; una variable junto con su complemento cubre todos los casos.","Combinar um termo com ele mesmo usando OR ou AND não o altera; uma variável com seu complemento cobre todos os casos."),
        "Commutative & associative" to Text3("Commutativité et associativité","Conmutatividad y asociatividad","Comutatividade e associatividade"),
        "Reorder and regroup freely — then spot cancellations." to Text3("Réordonnez et regroupez librement, puis repérez les simplifications.","Reordena y agrupa libremente; después, detecta las simplificaciones.","Reordene e reagrupe livremente; depois, encontre as simplificações."),
        "Distributive" to Text3("Distributivité","Distributiva","Distributiva"),
        "Factor like ordinary algebra — then watch terms vanish." to Text3("Factorisez comme en algèbre ordinaire, puis observez les termes disparaître.","Factoriza como en álgebra y observa cómo desaparecen términos.","Fatore como na álgebra comum e veja os termos desaparecerem."),
        "Absorption" to Text3("Absorption","Absorción","Absorção"),
        "The disappearing term: the longer clause is redundant." to Text3("Le terme qui disparaît : la proposition la plus longue est redondante.","El término que desaparece: la cláusula más larga es redundante.","O termo que desaparece: a expressão mais longa é redundante."),
        "De Morgan" to Text3("De Morgan","De Morgan","De Morgan"),
        "Break the bar, flip the sign: OR becomes AND and back." to Text3("Le complément inverse l’opérateur : OR devient AND, et inversement.","Al negar, cambia el operador: OR se vuelve AND y viceversa.","Ao negar, troque o operador: OR vira AND e vice-versa."),
        "XOR & XNOR" to Text3("XOR et XNOR","XOR y XNOR","XOR e XNOR"),
        "⊕ detects difference; ⊙ detects equality. Both appear in adders and comparators." to Text3("⊕ détecte une différence ; ⊙ détecte une égalité. Les deux apparaissent dans les additionneurs et les comparateurs.","⊕ detecta diferencias; ⊙ detecta igualdad. Ambos aparecen en sumadores y comparadores.","⊕ detecta diferenças; ⊙ detecta igualdade. Ambos aparecem em somadores e comparadores."),
        "IDENTITY OR" to Text3("IDENTITÉ OU","IDENTIDAD OR","IDENTIDADE OR"),
        "IDENTITY AND" to Text3("IDENTITÉ ET","IDENTIDAD AND","IDENTIDADE AND"),
        "NULL OR" to Text3("ÉLÉMENT NUL OR","ELEMENTO NULO OR","ELEMENTO NULO OR"),
        "NULL AND" to Text3("ÉLÉMENT NUL AND","ELEMENTO NULO AND","ELEMENTO NULO AND"),
        "IDEMPOTENT OR" to Text3("IDEMPOTENCE OR","IDEMPOTENCIA OR","IDEMPOTÊNCIA OR"),
        "IDEMPOTENT AND" to Text3("IDEMPOTENCE AND","IDEMPOTENCIA AND","IDEMPOTÊNCIA AND"),
        "COMPLEMENT OR" to Text3("COMPLÉMENT OR","COMPLEMENTO OR","COMPLEMENTO OR"),
        "COMPLEMENT AND" to Text3("COMPLÉMENT AND","COMPLEMENTO AND","COMPLEMENTO AND"),
        "DOUBLE NEGATION" to Text3("DOUBLE NÉGATION","DOBLE NEGACIÓN","DUPLA NEGAÇÃO"),
        "COMMUTATIVE OR" to Text3("COMMUTATIVITÉ OR","CONMUTATIVA OR","COMUTATIVA OR"),
        "COMMUTATIVE AND" to Text3("COMMUTATIVITÉ AND","CONMUTATIVA AND","COMUTATIVA AND"),
        "ASSOCIATIVE OR" to Text3("ASSOCIATIVITÉ OR","ASOCIATIVA OR","ASSOCIATIVA OR"),
        "ASSOCIATIVE AND" to Text3("ASSOCIATIVITÉ AND","ASOCIATIVA AND","ASSOCIATIVA AND"),
        "AND OVER OR" to Text3("AND SUR OR","AND SOBRE OR","AND SOBRE OR"),
        "OR OVER AND" to Text3("OR SUR AND","OR SOBRE AND","OR SOBRE AND"),
        "ABSORPTION OR" to Text3("ABSORPTION OR","ABSORCIÓN OR","ABSORÇÃO OR"),
        "ABSORPTION AND" to Text3("ABSORPTION AND","ABSORCIÓN AND","ABSORÇÃO AND"),
        "DE MORGAN OR" to Text3("DE MORGAN OR","DE MORGAN OR","DE MORGAN OR"),
        "DE MORGAN AND" to Text3("DE MORGAN AND","DE MORGAN AND","DE MORGAN AND"),
        "XOR EXPANSION" to Text3("DÉVELOPPEMENT DE XOR","DESARROLLO DE XOR","EXPANSÃO DE XOR"),
        "XNOR AS INVERTED XOR" to Text3("XNOR COMME XOR INVERSÉ","XNOR COMO XOR INVERTIDO","XNOR COMO XOR INVERTIDO")
    )
    fun translate(source:String,language:String):String? {
        val lang=language.lowercase()
        val stepCard=Regex("^([✓]|\\d+\\.)\\s{2}(.+)$").matchEntire(source)
        if(stepCard!=null){
            val title=entries[stepCard.groupValues[2]]?:return null
            val translated=when(lang){"fr"->title.fr;"es"->title.es;"pt"->title.pt;else->return null}
            return "${stepCard.groupValues[1]}  $translated"
        }
        return entries[source]?.let { text -> when(lang) {
            "fr"->text.fr
            "es"->text.es
            "pt"->text.pt
            else->null
        } }
    }
}
