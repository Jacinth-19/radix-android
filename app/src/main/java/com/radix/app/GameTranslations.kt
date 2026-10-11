package com.radix.app

/** Localized level names, score summaries, and dynamic game/progress labels. */
internal object GameTranslations {
    private val levels=mapOf(
        "Novice" to Triple("Débutant","Novato","Iniciante"),
        "Apprentice" to Triple("Apprenti","Aprendiz","Aprendiz"),
        "Builder" to Triple("Constructeur","Constructor","Construtor"),
        "Thinker" to Triple("Penseur","Pensador","Pensador"),
        "Solver" to Triple("Résolveur","Solucionador","Solucionador"),
        "Designer" to Triple("Concepteur","Diseñador","Projetista"),
        "Architect" to Triple("Architecte","Arquitecto","Arquiteto"),
        "Master" to Triple("Maître","Maestro","Mestre")
    )
    fun translate(source:String,language:String):String? {
        val lang=language.lowercase()
        if(lang !in setOf("fr","es","pt"))return null
        fun levelName(raw:String):String {
            val match=Regex("^(.+) \\+(\\d+)$").matchEntire(raw)
            val base=match?.groupValues?.get(1)?:raw
            val translated=levels[base]?.let { when(lang) {"fr"->it.first;"es"->it.second;else->it.third} }?:base
            return if(match==null)translated else "$translated +${match.groupValues[2]}"
        }
        levels[source]?.let {return when(lang) {"fr"->it.first;"es"->it.second;else->it.third}}
        val level=Regex("^Level (\\d+) (.+)$").matchEntire(source)
        if(level!=null)return when(lang) {"fr"->"Niveau ${level.groupValues[1]} ${levelName(level.groupValues[2])}";"es"->"Nivel ${level.groupValues[1]} ${levelName(level.groupValues[2])}";else->"Nível ${level.groupValues[1]} ${levelName(level.groupValues[2])}"}
        val award=Regex("^Level (\\d+)! \\+(\\d+) XP$").matchEntire(source)
        if(award!=null)return when(lang) {"fr"->"Niveau ${award.groupValues[1]} ! +${award.groupValues[2]} XP";"es"->"¡Nivel ${award.groupValues[1]}! +${award.groupValues[2]} XP";else->"Nível ${award.groupValues[1]}! +${award.groupValues[2]} XP"}
        val namedAward=Regex("^Level (\\d+) (.+)! \\+(\\d+) XP$").matchEntire(source)
        if(namedAward!=null)return when(lang) {"fr"->"Niveau ${namedAward.groupValues[1]} ${levelName(namedAward.groupValues[2])} ! +${namedAward.groupValues[3]} XP";"es"->"¡Nivel ${namedAward.groupValues[1]} ${levelName(namedAward.groupValues[2])}! +${namedAward.groupValues[3]} XP";else->"Nível ${namedAward.groupValues[1]} ${levelName(namedAward.groupValues[2])}! +${namedAward.groupValues[3]} XP"}
        val nameLine=Regex("^(.+) · Level (\\d+) (.+)$").matchEntire(source)
        if(nameLine!=null)return when(lang) {"fr"->"${nameLine.groupValues[1]} · Niveau ${nameLine.groupValues[2]} ${levelName(nameLine.groupValues[3])}";"es"->"${nameLine.groupValues[1]} · Nivel ${nameLine.groupValues[2]} ${levelName(nameLine.groupValues[3])}";else->"${nameLine.groupValues[1]} · Nível ${nameLine.groupValues[2]} ${levelName(nameLine.groupValues[3])}"}
        val xpNext=Regex("^(\\d+) XP \\((\\d+)/(\\d+) to Level (\\d+)\\)$").matchEntire(source)
        if(xpNext!=null)return when(lang) {"fr"->"${xpNext.groupValues[1]} XP (${xpNext.groupValues[2]}/${xpNext.groupValues[3]} avant le niveau ${xpNext.groupValues[4]})";"es"->"${xpNext.groupValues[1]} XP (${xpNext.groupValues[2]}/${xpNext.groupValues[3]} para el nivel ${xpNext.groupValues[4]})";else->"${xpNext.groupValues[1]} XP (${xpNext.groupValues[2]}/${xpNext.groupValues[3]} para o nível ${xpNext.groupValues[4]})"}
        val practiceProgress=Regex("^(\\d+)/(\\d+) practice cards mastered$").matchEntire(source)
        if(practiceProgress!=null)return when(lang) {"fr"->"${practiceProgress.groupValues[1]}/${practiceProgress.groupValues[2]} questions maîtrisées";"es"->"${practiceProgress.groupValues[1]}/${practiceProgress.groupValues[2]} tarjetas dominadas";else->"${practiceProgress.groupValues[1]}/${practiceProgress.groupValues[2]} questões dominadas"}
        if(source=="Streak: warming up")return when(lang) {"fr"->"Série : en cours";"es"->"Racha: empezando";else->"Sequência: começando"}
        val streak=Regex("^Streak: (\\d+) days? 🔥$").matchEntire(source)
        if(streak!=null)return when(lang) {"fr"->"Série : ${streak.groupValues[1]} jours 🔥";"es"->"Racha: ${streak.groupValues[1]} días 🔥";else->"Sequência: ${streak.groupValues[1]} dias 🔥"}
        val xpTotal=Regex("^(\\d+) XP · (\\d+)/(\\d+) to Level (\\d+)$").matchEntire(source)
        if(xpTotal!=null)return when(lang) {"fr"->"${xpTotal.groupValues[1]} XP · ${xpTotal.groupValues[2]}/${xpTotal.groupValues[3]} avant le niveau ${xpTotal.groupValues[4]}";"es"->"${xpTotal.groupValues[1]} XP · ${xpTotal.groupValues[2]}/${xpTotal.groupValues[3]} para el nivel ${xpTotal.groupValues[4]}";else->"${xpTotal.groupValues[1]} XP · ${xpTotal.groupValues[2]}/${xpTotal.groupValues[3]} para o nível ${xpTotal.groupValues[4]}"}
        val levelRow=Regex("^Level (\\d+) (.+) · (\\d+) XP$").matchEntire(source)
        if(levelRow!=null)return when(lang) {"fr"->"Niveau ${levelRow.groupValues[1]} ${levelName(levelRow.groupValues[2])} · ${levelRow.groupValues[3]} XP";"es"->"Nivel ${levelRow.groupValues[1]} ${levelName(levelRow.groupValues[2])} · ${levelRow.groupValues[3]} XP";else->"Nível ${levelRow.groupValues[1]} ${levelName(levelRow.groupValues[2])} · ${levelRow.groupValues[3]} XP"}
        val levelPlayers=Regex("^Level (\\d+) · (\\d+) XP · (\\d+) players$").matchEntire(source)
        if(levelPlayers!=null)return when(lang) {"fr"->"Niveau ${levelPlayers.groupValues[1]} · ${levelPlayers.groupValues[2]} XP · ${levelPlayers.groupValues[3]} joueurs";"es"->"Nivel ${levelPlayers.groupValues[1]} · ${levelPlayers.groupValues[2]} XP · ${levelPlayers.groupValues[3]} jugadores";else->"Nível ${levelPlayers.groupValues[1]} · ${levelPlayers.groupValues[2]} XP · ${levelPlayers.groupValues[3]} jogadores"}
        val activeProgress=Regex("^(\\d+) of (\\d+) mastered · Level (\\d+)$").matchEntire(source)
        if(activeProgress!=null)return when(lang) {"fr"->"${activeProgress.groupValues[1]} sur ${activeProgress.groupValues[2]} maîtrisées · niveau ${activeProgress.groupValues[3]}";"es"->"${activeProgress.groupValues[1]} de ${activeProgress.groupValues[2]} dominadas · nivel ${activeProgress.groupValues[3]}";else->"${activeProgress.groupValues[1]} de ${activeProgress.groupValues[2]} dominadas · nível ${activeProgress.groupValues[3]}"}
        val xpAs=Regex("^(\\d+) XP as (.+) · Easy 10, Intermediate 20, Advanced 30 per card \\+ streak bonus\\.$").matchEntire(source)
        if(xpAs!=null)return when(lang) {"fr"->"${xpAs.groupValues[1]} XP pour ${xpAs.groupValues[2]} · Facile 10, Intermédiaire 20, Avancé 30 par question + bonus de série.";"es"->"${xpAs.groupValues[1]} XP de ${xpAs.groupValues[2]} · Fácil 10, Intermedio 20, Avanzado 30 por tarjeta + bonificación por racha.";else->"${xpAs.groupValues[1]} XP de ${xpAs.groupValues[2]} · Fácil 10, Intermediário 20, Avançado 30 por questão + bônus de sequência."}
        val levelCards=Regex("^(\\d+) cards at this level · (\\d+) mastered here$").matchEntire(source)
        if(levelCards!=null)return when(lang) {"fr"->"${levelCards.groupValues[1]} questions à ce niveau · ${levelCards.groupValues[2]} maîtrisées ici";"es"->"${levelCards.groupValues[1]} tarjetas en este nivel · ${levelCards.groupValues[2]} dominadas aquí";else->"${levelCards.groupValues[1]} questões neste nível · ${levelCards.groupValues[2]} dominadas aqui"}
        val pathSummary=Regex("^(\\d+) of (\\d+) checks mastered · 7 steps from number systems to sequential logic$").matchEntire(source)
        if(pathSummary!=null)return when(lang) {"fr"->"${pathSummary.groupValues[1]} contrôles maîtrisés sur ${pathSummary.groupValues[2]} · 7 étapes, des systèmes numériques à la logique séquentielle";"es"->"${pathSummary.groupValues[1]} de ${pathSummary.groupValues[2]} controles dominados · 7 pasos, de los sistemas numéricos a la lógica secuencial";else->"${pathSummary.groupValues[1]} de ${pathSummary.groupValues[2]} verificações dominadas · 7 etapas, dos sistemas numéricos à lógica sequencial"}
        val recent=Regex("^(\\d+) recent local conversions$").matchEntire(source)
        if(recent!=null)return when(lang) {"fr"->"${recent.groupValues[1]} conversions locales récentes";"es"->"${recent.groupValues[1]} conversiones locales recientes";else->"${recent.groupValues[1]} conversões locais recentes"}
        val saved=Regex("^(\\d+) saved numbers$").matchEntire(source)
        if(saved!=null)return when(lang) {"fr"->"${saved.groupValues[1]} nombres enregistrés";"es"->"${saved.groupValues[1]} números guardados";else->"${saved.groupValues[1]} números salvos"}
        val cards=Regex("^(\\d+) leveled objective cards \\+ written self-checks$").matchEntire(source)
        if(cards!=null)return when(lang) {"fr"->"${cards.groupValues[1]} questions à choix multiples par niveau + autoévaluations écrites";"es"->"${cards.groupValues[1]} preguntas por niveles + autoevaluaciones escritas";else->"${cards.groupValues[1]} questões por nível + autoavaliações escritas"}
        val sessions=Regex("^(\\d+) named inputs/clock/memory sessions$").matchEntire(source)
        if(sessions!=null)return when(lang) {"fr"->"${sessions.groupValues[1]} sessions nommées avec entrées, horloge et mémoire";"es"->"${sessions.groupValues[1]} sesiones con nombre, entradas, reloj y memoria";else->"${sessions.groupValues[1]} sessões nomeadas com entradas, clock e memória"}
        if(source=="That name is taken.")return when(lang) {"fr"->"Ce nom est déjà utilisé.";"es"->"Ese nombre ya está en uso.";else->"Esse nome já está em uso."}
        if(source=="Leaderboard holds 8 players.")return when(lang) {"fr"->"Le classement est limité à 8 joueurs.";"es"->"La clasificación admite 8 jugadores.";else->"O placar aceita até 8 jogadores."}
        if(source=="Name needs 1–24 printable characters.")return when(lang) {"fr"->"Le nom doit contenir de 1 à 24 caractères imprimables.";"es"->"El nombre debe tener de 1 a 24 caracteres imprimibles.";else->"O nome precisa ter de 1 a 24 caracteres imprimíveis."}
        if(source=="Keep at least one player.")return when(lang) {"fr"->"Gardez au moins un joueur.";"es"->"Debe quedar al menos un jugador.";else->"Mantenha pelo menos um jogador."}
        if(source=="Unknown player.")return when(lang) {"fr"->"Joueur inconnu.";"es"->"Jugador desconocido.";else->"Jogador desconhecido."}
        if(source=="Cannot delete.")return when(lang) {"fr"->"Impossible de supprimer.";"es"->"No se puede eliminar.";else->"Não foi possível excluir."}
        if(source=="Player joined the table.")return when(lang) {"fr"->"Le joueur a rejoint la partie.";"es"->"El jugador se unió a la partida.";else->"O jogador entrou na partida."}
        if(source=="Score card shared as an image.")return when(lang) {"fr"->"La carte de score a été partagée en image.";"es"->"Se compartió la tarjeta de puntuación como imagen.";else->"O cartão de pontuação foi compartilhado como imagem."}
        return null
    }
}
