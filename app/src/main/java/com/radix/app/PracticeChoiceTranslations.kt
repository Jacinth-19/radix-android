package com.radix.app

/** Short prose answer choices; equations and bit patterns remain unchanged. */
internal object PracticeChoiceTranslations {
    private data class Text3(val fr:String,val es:String,val pt:String)
    private val entries=mapOf(
        "Q=1, Q′=0" to Text3("Q=1, Q′=0","Q=1, Q′=0","Q=1, Q′=0"),
        "Q=0, Q′=1" to Text3("Q=0, Q′=1","Q=0, Q′=1","Q=0, Q′=1"),
        "Q=0, Q′=0; memory X" to Text3("Q=0, Q′=0 ; mémoire X","Q=0, Q′=0; memoria X","Q=0, Q′=0; memória X"),
        "Q=1, Q′=1; memory X" to Text3("Q=1, Q′=1 ; mémoire X","Q=1, Q′=1; memoria X","Q=1, Q′=1; memória X"),
        "First evaluate OR." to Text3("Calculez d’abord OR.","Primero evalúa OR.","Primeiro calcule OR."),
        "A′ AND B′" to Text3("A′ ET B′","A′ AND B′","A′ AND B′"),
        "A′ OR B′" to Text3("A′ OU B′","A′ OR B′","A′ OR B′"),
        "A OR B" to Text3("A OU B","A OR B","A OR B"),
        "A AND B" to Text3("A ET B","A AND B","A AND B"),
        "B AND C" to Text3("B ET C","B AND C","B AND C"),
        "All are left-to-right" to Text3("Toutes de gauche à droite","Todas de izquierda a derecha","Todas da esquerda para a direita"),
        "Any even count" to Text3("N’importe quel nombre pair","Cualquier cantidad par","Qualquer quantidade par"),
        "Multiples of 3" to Text3("Multiples de 3","Múltiplos de 3","Múltiplos de 3"),
        "Any count" to Text3("N’importe quelle quantité","Cualquier cantidad","Qualquer quantidade"),
        "touch (wrap around)" to Text3("se touchent (par raccordement)","se tocan (con envoltura)","se conectam (com contorno)"),
        "never touch" to Text3("ne se touchent jamais","nunca se tocan","nunca se conectam"),
        "touch only in POS" to Text3("se touchent uniquement en POS","solo se tocan en POS","só se conectam em POS"),
        "touch only in 4-var" to Text3("se touchent uniquement sur 4 variables","solo se tocan en mapas de 4 variables","só se conectam em mapas de 4 variáveis")
    )
    fun translate(source:String,language:String):String? {
        val lang=language.lowercase()
        if(lang !in setOf("fr","es","pt"))return null
        entries[source]?.let { text -> return when(lang) {"fr"->text.fr;"es"->text.es;else->text.pt} }
        val variables=Regex("^(\\d+) variables$").matchEntire(source)?.groupValues?.get(1)?:return null
        return when(lang) {"fr"->"$variables variables";"es"->"$variables variables";else->"$variables variáveis"}
    }
}
