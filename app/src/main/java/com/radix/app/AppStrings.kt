package com.radix.app

import androidx.compose.runtime.staticCompositionLocalOf

/** Core chrome, onboarding, and reminder copy; catalog text resolves through the shared locale translator. */
internal data class Strings(
    val tabConvert:String,val tabLogic:String,val tabKmaps:String,val tabMore:String,
    val tagline:String,
    val skip:String,val back:String,val next:String,val startLearning:String,
    val tourConvertTitle:String,val tourConvertBody:String,
    val tourLogicTitle:String,val tourLogicBody:String,
    val tourKmapTitle:String,val tourKmapBody:String,
    val tourPracticeTitle:String,val tourPracticeBody:String,
    val tourRemindTitle:String,val tourRemindBody:String,
    val whatsNewTitle:String,val whatsNewIntro:String,val whatsNewItems:List<String>,val whatsNewKeep:String,
    val keepExploring:String,val setReminder:String,
    val themeTitle:String,val themeDark:String,val themeLight:String,val themeSystem:String,
    val reminderTitle:String,val reminderIntro:String,val reminderDaily:String,val reminderDailyNote:String,
    val reminderStreak:String,val reminderTips:String,val pickTime:String,val save:String,val cancel:String,
    val reminderOnToast:String,val reminderBlockedToast:String,
    val reminderPreview:String,val reminderSettings:String,val reminderPreviewToast:String,
    val landscapeKmapHint:String,val landscapeCircuitHint:String,val landscapeBuilderHint:String,val convertRandomize:String,
    val inboxTitle:String,val inboxClear:String,val inboxEmpty:String,
    val updateTitle:String,val updateHow:String,val updateNotes:String,val updateCopy:String,val updateCopied:String,
    val reminderTime:(Int,Int)->String,val streakLine:(Int)->String,val updateCurrent:(String,Int)->String,
    val languageCode:String="en",
) {
    fun text(source:String):String=AppStrings.translate(source,languageCode)
}

private val enStrings=Strings(
    tabConvert="Convert",tabLogic="Logic",tabKmaps="K-maps",tabMore="More",
    tagline="Numbers, finally obvious.",
    skip="Skip",back="Back",next="Next",startLearning="Start learning",
    tourConvertTitle="One number. Every base.",
    tourConvertBody="Type 255 once and watch it become FF, 377 and 11111111. Open History to revisit conversions; the arithmetic lab adds, subtracts, multiplies and divides in binary, denary and more.",
    tourLogicTitle="Tap an input. Watch the wire.",
    tourLogicBody="Gates, adders, flip-flops and counters respond live. Try the 4-input, 7-segment BCD decoder; rotate to landscape for wide diagrams and circuit work.",
    tourKmapTitle="Circle 1s. Shrink expressions.",
    tourKmapBody="SOP groups the 1s; POS groups the 0s. Toggle group overlays and cell indices, inspect each term, and follow its explanation. Tables contain 2ⁿ rows: 8 for 3 inputs, 16 for 4.",
    tourPracticeTitle="Easy first. Exams later.",
    tourPracticeBody="108 objective cards across Easy, Intermediate and Advanced, plus written subjective self-checks. Follow your seven-step learning path and track mastery; hints come before answers.",
    tourRemindTitle="A nudge, not a nag.",
    tourRemindBody="One gentle daily reminder at your hour, with an optional bundled chime and streak celebrations you can switch off. Silence or change its sound in Android notification settings. Everything stays offline.",
    whatsNewTitle="What’s new in Radix 7.0",
    whatsNewIntro="Your work stays on this device. Explore the clearer toolbox, live BCD display and cross-base arithmetic:",
    whatsNewItems=listOf(
        "More groups the seven-step learning path, conversion history, progress, objective quizzes and written self-checks",
        "The 4-input BCD decoder now has a live seven-segment display preview; landscape gives wide circuit diagrams more room",
        "Exact addition, subtraction, multiplication and division in binary, denary and other bases, with results shown in both binary and denary",
        "Still included: Boolean expression lab, circuit builder, IEEE-754 lab, 108 practice cards and local progress",
        "English is the fallback; French, Spanish and Portuguese remain fully localized for supported system locales",
        "No account or internet connection is needed; your study data stays on this device"),
    whatsNewKeep="Your saved conversions, sessions, mastered cards and backups are untouched.",
    keepExploring="Keep exploring",setReminder="Set a reminder",
    themeTitle="Theme",themeDark="Dark",themeLight="Light",themeSystem="System",
    reminderTitle="Study reminders",reminderIntro="One optional reminder a day, scheduled on this device only. Includes a gentle bundled chime; preview it or silence/change it in Android notification settings. No account or network.",
    reminderDaily="Daily reminder",reminderDailyNote="Fires once at your chosen time",
    reminderStreak="Streak celebrations",reminderTips="Tips inside reminders",
    pickTime="Pick a time",save="Save",cancel="Cancel",
    reminderOnToast="Reminder set — see you then.",
    reminderBlockedToast="System blocked notifications — reminders stay in your inbox.",
    reminderPreview="Preview reminder",reminderSettings="Sound & notification settings",reminderPreviewToast="Preview notification sent. Check the sound or change it in system settings.",
    landscapeKmapHint="For 5–6 variables, swipe horizontally or rotate your device to landscape to inspect the full map.",landscapeCircuitHint="Wide input bank: rotate your device to landscape for more room. The truth table is generated 16 rows per page.",landscapeBuilderHint="Portrait uses a swipeable wide canvas; rotate to landscape to see and arrange more at once.",convertRandomize="Randomize value",
    inboxTitle="Notification inbox",inboxClear="Clear",
    inboxEmpty="Nothing here yet. Reminders and streak news land here even if system notifications are off.",
    updateTitle="Updates & version",updateHow="Radix updates like any app: install the newer version and it replaces this one, keeping your saved study data. Backups from older versions still restore.",
    updateNotes="What’s new",updateCopy="Copy version",updateCopied="Version info copied.",
    reminderTime={h,m->"Reminder at %d:%02d".format(h,m)},
    streakLine={d->if(d<=0)"No streak yet — one card starts it." else "🔥 $d-day practice streak"},
    updateCurrent={name,code->"Radix $name · code $code · offline build"},
)

private val frStrings=enStrings.copy(
    tabConvert="Convertir",tabLogic="Logique",tabKmaps="Karnaugh",tabMore="Plus",
    tagline="Les nombres, enfin évidents.",
    skip="Passer",back="Retour",next="Suivant",startLearning="Commencer",
    tourConvertTitle="Un nombre. Toutes les bases.",
    tourConvertBody="Saisissez 255 une fois : FF, 377 et 11111111. L’historique permet de reprendre vos conversions ; le labo calcule +, −, × et ÷ en binaire, décimal et autres bases.",
    tourLogicTitle="Touchez une entrée. Suivez le fil.",
    tourLogicBody="Portes, additionneurs, bascules et compteurs réagissent en direct. Essayez le décodeur BCD à 4 entrées et 7 segments ; le mode paysage offre plus d’espace aux schémas.",
    tourKmapTitle="Entourez les 1. Réduisez tout.",
    tourKmapBody="En SOP, regroupez les 1 ; en POS, les 0. Affichez les groupes et indices, puis lisez chaque terme et son explication. Les tables ont 2ⁿ lignes : 8 pour 3 entrées, 16 pour 4.",
    tourPracticeTitle="Facile d’abord. Examens ensuite.",
    tourPracticeBody="108 questions objectives sur trois niveaux, plus des autoévaluations rédigées. Suivez le parcours en sept étapes et votre maîtrise ; les indices précèdent toujours les réponses.",
    tourRemindTitle="Un rappel, pas un reproche.",
    tourRemindBody="Un rappel quotidien à votre heure, avec une sonnerie intégrée facultative et des séries désactivables. Réglez ou coupez le son dans les paramètres de notifications Android. Tout reste hors ligne.",
    whatsNewTitle="Nouveautés de Radix 7.0",
    whatsNewIntro="Vos données restent sur l’appareil. Découvrez les outils mieux organisés, l’afficheur BCD et l’arithmétique multibase :",
    whatsNewItems=listOf(
        "Plus regroupe le parcours en sept étapes, l’historique, la progression, les quiz objectifs et les autoévaluations rédigées",
        "Le décodeur BCD à 4 entrées dispose d’un aperçu dynamique à 7 segments ; le paysage agrandit les schémas de circuits",
        "Addition, soustraction, multiplication et division exactes en binaire, décimal et autres bases, avec résultats dans les deux bases",
        "Toujours inclus : labo d’expressions booléennes, constructeur de circuits, labo IEEE-754 et 108 questions",
        "L’anglais reste la langue de repli ; le français, l’espagnol et le portugais restent entièrement traduits pour les langues système prises en charge",
        "Sans compte ni connexion Internet ; vos données d’étude restent sur cet appareil"),
    whatsNewKeep="Vos conversions, sessions, cartes maîtrisées et sauvegardes sont intactes.",
    keepExploring="Continuer",setReminder="Créer un rappel",
    themeTitle="Thème",themeDark="Sombre",themeLight="Clair",themeSystem="Système",
    reminderTitle="Rappels d’étude",reminderIntro="Un rappel facultatif par jour, planifié sur cet appareil. Sonnerie douce intégrée : écoutez l’aperçu ou réglez/coupez le son dans les paramètres Android. Sans compte ni réseau.",
    reminderDaily="Rappel quotidien",reminderDailyNote="Une fois par jour, à votre heure",
    reminderStreak="Fêter les séries",reminderTips="Astuces dans les rappels",
    pickTime="Choisir l’heure",save="Enregistrer",cancel="Annuler",
    reminderOnToast="Rappel programmé — à tout à l’heure.",
    reminderBlockedToast="Notifications bloquées — les rappels restent dans la boîte de réception.",
    reminderPreview="Écouter un aperçu",reminderSettings="Son et notifications",reminderPreviewToast="Notification test envoyée. Réglez le son dans les paramètres système.",
    landscapeKmapHint="Pour 5–6 variables, faites défiler horizontalement ou passez en paysage pour voir toute la carte.",landscapeCircuitHint="Entrées nombreuses : passez en paysage. La table est générée par pages de 16 lignes.",landscapeBuilderHint="En portrait, faites défiler un canevas large ; passez en paysage pour voir et organiser davantage à la fois.",convertRandomize="Valeur aléatoire",
    inboxTitle="Boîte de réception",inboxClear="Effacer",
    inboxEmpty="Rien pour l’instant. Rappels et séries arrivent ici même si les notifications système sont coupées.",
    updateTitle="Mises à jour",updateHow="Radix se met à jour comme toute appli : installez la version plus récente, elle remplace celle-ci en gardant vos données. Les sauvegardes anciennes se restaurent toujours.",
    updateNotes="Nouveautés",updateCopy="Copier la version",updateCopied="Version copiée.",
    reminderTime={h,m->"Rappel : %d:%02d".format(h,m)},
    streakLine={d->if(d<=0)"Pas encore de série — une carte suffit." else "🔥 Série de $d jours"},
    updateCurrent={name,code->"Radix $name · code $code · version hors ligne"},
    languageCode="fr",
)

private val esStrings=enStrings.copy(
    tabConvert="Convertir",tabLogic="Lógica",tabKmaps="Karnaugh",tabMore="Más",
    tagline="Los números, por fin evidentes.",
    skip="Omitir",back="Atrás",next="Siguiente",startLearning="Empezar",
    tourConvertTitle="Un número. Todas las bases.",
    tourConvertBody="Escribe 255 una vez: obtén FF, 377 y 11111111. Consulta el historial para recuperar conversiones; el laboratorio suma, resta, multiplica y divide en binario, decimal y otras bases.",
    tourLogicTitle="Toca una entrada. Sigue el cable.",
    tourLogicBody="Puertas, sumadores, flip-flops y contadores responden en vivo. Prueba el decodificador BCD de 4 entradas y 7 segmentos; el modo horizontal amplía los diagramas.",
    tourKmapTitle="Rodea los 1. Reduce todo.",
    tourKmapBody="En SOP, agrupa los 1; en POS, los 0. Activa los grupos e índices, y lee cada término con su explicación. Las tablas tienen 2ⁿ filas: 8 con 3 entradas y 16 con 4.",
    tourPracticeTitle="Fácil primero. Exámenes después.",
    tourPracticeBody="108 cuestionarios objetivos en tres niveles, además de autoevaluaciones escritas. Sigue la ruta de siete pasos y revisa tu dominio; las pistas aparecen antes de las respuestas.",
    tourRemindTitle="Un recordatorio, no un regaño.",
    tourRemindBody="Un recordatorio diario a tu hora, con tono integrado opcional y rachas desactivables. Cambia o silencia el sonido en los ajustes de notificaciones de Android. Todo sin conexión.",
    whatsNewTitle="Novedades de Radix 7.0",
    whatsNewIntro="Tus datos siguen en este dispositivo. Descubre las herramientas mejor organizadas, la pantalla BCD y la aritmética entre bases:",
    whatsNewItems=listOf(
        "Más reúne la ruta de siete pasos, el historial, el progreso, los cuestionarios objetivos y las autoevaluaciones escritas",
        "El decodificador BCD de 4 entradas incluye una vista dinámica de 7 segmentos; el modo horizontal da más espacio a los circuitos",
        "Suma, resta, multiplicación y división exactas en binario, decimal y otras bases, con resultados en binario y decimal",
        "También incluye: laboratorio de expresiones booleanas, constructor de circuitos, laboratorio IEEE-754 y 108 preguntas",
        "El inglés sigue como idioma de reserva; francés, español y portugués mantienen la localización completa para idiomas de sistema compatibles",
        "Sin cuenta ni conexión a Internet; tus datos de estudio permanecen en este dispositivo"),
    whatsNewKeep="Tus conversiones, sesiones, tarjetas dominadas y copias están intactas.",
    keepExploring="Seguir",setReminder="Crear recordatorio",
    themeTitle="Tema",themeDark="Oscuro",themeLight="Claro",themeSystem="Sistema",
    reminderTitle="Recordatorios de estudio",reminderIntro="Un recordatorio opcional al día, solo en este dispositivo. Incluye un tono suave: escúchalo o siléncialo/cámbialo en los ajustes de notificaciones Android. Sin cuenta ni red.",
    reminderDaily="Recordatorio diario",reminderDailyNote="Una vez al día, a tu hora",
    reminderStreak="Celebrar rachas",reminderTips="Trucos en recordatorios",
    pickTime="Elegir hora",save="Guardar",cancel="Cancelar",
    reminderOnToast="Recordatorio programado — nos vemos luego.",
    reminderBlockedToast="Notificaciones bloqueadas — los recordatorios quedan en la bandeja.",
    reminderPreview="Probar recordatorio",reminderSettings="Sonido y notificaciones",reminderPreviewToast="Notificación de prueba enviada. Ajusta el sonido en el sistema.",
    landscapeKmapHint="Con 5–6 variables, desplázate horizontalmente o gira el dispositivo para ver el mapa completo.",landscapeCircuitHint="Muchas entradas: gira el dispositivo. La tabla se genera en páginas de 16 filas.",landscapeBuilderHint="En vertical, desliza el lienzo ancho; gira a horizontal para ver y organizar más elementos a la vez.",convertRandomize="Valor aleatorio",
    inboxTitle="Bandeja de entrada",inboxClear="Vaciar",
    inboxEmpty="Nada por aquí. Recordatorios y rachas llegan aquí aunque las notificaciones estén desactivadas.",
    updateTitle="Actualizaciones",updateHow="Radix se actualiza como cualquier app: instala la versión nueva, reemplaza esta y conserva tus datos. Las copias antiguas siempre se restauran.",
    updateNotes="Novedades",updateCopy="Copiar versión",updateCopied="Versión copiada.",
    reminderTime={h,m->"Recordatorio: %d:%02d".format(h,m)},
    streakLine={d->if(d<=0)"Sin racha aún — basta una tarjeta." else "🔥 Racha de $d días"},
    updateCurrent={name,code->"Radix $name · código $code · versión sin conexión"},
    languageCode="es",
)

private val ptStrings=enStrings.copy(
    tabConvert="Converter",tabLogic="Lógica",tabKmaps="Mapas K",tabMore="Mais",
    tagline="Números, enfim, claros.",
    skip="Pular",back="Voltar",next="Avançar",startLearning="Começar a aprender",
    tourConvertTitle="Um número. Todas as bases.",
    tourConvertBody="Digite 255 uma vez: veja FF, 377 e 11111111. O histórico recupera conversões; o laboratório soma, subtrai, multiplica e divide em binário, decimal e outras bases.",
    tourLogicTitle="Toque em uma entrada. Siga o fio.",
    tourLogicBody="Portas, somadores, flip-flops e contadores respondem ao vivo. Experimente o decodificador BCD de 4 entradas e 7 segmentos; paisagem dá mais espaço aos circuitos.",
    tourKmapTitle="Agrupe os 1. Simplifique a lógica.",
    tourKmapBody="No SOP, agrupe os 1; no POS, os 0. Ative os grupos e índices, e leia cada termo e explicação. As tabelas têm 2ⁿ linhas: 8 para 3 entradas e 16 para 4.",
    tourPracticeTitle="Comece pelo fácil. Avance até os exames.",
    tourPracticeBody="108 testes objetivos em três níveis e autoavaliações escritas. Acompanhe seu domínio no percurso de sete etapas; as dicas vêm antes das respostas.",
    tourRemindTitle="Um lembrete, sem pressão.",
    tourRemindBody="Um lembrete diário no horário escolhido, com toque integrado opcional e comemorações de sequência que podem ser desativadas. Ajuste ou silencie o som nas configurações de notificações do Android. Tudo funciona offline.",
    whatsNewTitle="Novidades do Radix 7.0",
    whatsNewIntro="Seus dados continuam neste dispositivo. Explore as ferramentas organizadas, o display BCD e a aritmética entre bases:",
    whatsNewItems=listOf(
        "Mais reúne o percurso de sete etapas, histórico, progresso, testes objetivos e autoavaliações escritas",
        "O decodificador BCD de 4 entradas agora tem prévia dinâmica de 7 segmentos; paisagem amplia os circuitos",
        "Adição, subtração, multiplicação e divisão exatas em binário, decimal e outras bases, com resultados nas duas bases",
        "Também inclui: laboratório de expressões booleanas, construtor de circuitos, laboratório IEEE-754 e 108 questões",
        "O inglês continua como idioma padrão de reserva; francês, espanhol e português seguem totalmente localizados para idiomas do sistema compatíveis",
        "Sem conta ou conexão à Internet; seus dados de estudo permanecem neste dispositivo"),
    whatsNewKeep="Conversões, sessões, cartões dominados e backups continuam intactos.",
    keepExploring="Continuar explorando",setReminder="Definir lembrete",
    themeTitle="Tema",themeDark="Escuro",themeLight="Claro",themeSystem="Sistema",
    reminderTitle="Lembretes de estudo",reminderIntro="Um lembrete opcional por dia, agendado apenas neste dispositivo. Inclui um toque suave integrado; ouça a prévia ou silencie/altere-o nas configurações de notificações do Android. Sem conta ou rede.",
    reminderDaily="Lembrete diário",reminderDailyNote="Uma vez por dia, no horário escolhido",
    reminderStreak="Comemorar sequências",reminderTips="Dicas nos lembretes",
    pickTime="Escolher horário",save="Salvar",cancel="Cancelar",
    reminderOnToast="Lembrete agendado — até lá.",
    reminderBlockedToast="Notificações bloqueadas pelo sistema — os lembretes continuam na caixa de entrada.",
    reminderPreview="Prévia do lembrete",reminderSettings="Som e notificações",reminderPreviewToast="Notificação de teste enviada. Confira ou altere o som nas configurações do sistema.",
    landscapeKmapHint="Para 5–6 variáveis, deslize na horizontal ou gire o dispositivo para visualizar o mapa completo.",landscapeCircuitHint="Muitas entradas: gire o dispositivo para usar o modo paisagem. A tabela é gerada em páginas de 16 linhas.",landscapeBuilderHint="No modo retrato, deslize horizontalmente a área de trabalho; gire para paisagem para ver e organizar mais elementos de uma vez.",convertRandomize="Sortear valor",
    inboxTitle="Caixa de notificações",inboxClear="Limpar",
    inboxEmpty="Ainda não há nada aqui. Lembretes e avisos de sequência aparecem mesmo quando as notificações do sistema estão desativadas.",
    updateTitle="Atualizações e versão",updateHow="O Radix é atualizado como qualquer app: instale a versão mais recente para substituir esta, mantendo seus dados de estudo. Backups antigos continuam podendo ser restaurados.",
    updateNotes="Novidades",updateCopy="Copiar versão",updateCopied="Informações da versão copiadas.",
    reminderTime={h,m->"Lembrete às %d:%02d".format(h,m)},
    streakLine={d->if(d<=0)"Ainda sem sequência — uma questão já começa." else "🔥 $d dias seguidos de prática"},
    updateCurrent={name,code->"Radix $name · código $code · versão offline"},
    languageCode="pt",
)

internal object AppStrings {
    private val supportedLanguages=setOf("en","fr","es","pt")
    fun systemLanguage(locales:android.os.LocaleList=android.os.LocaleList.getDefault()):String =
        systemLanguage((0 until locales.size()).map { locales[it].toLanguageTag() })

    /** Select the first supported language in the device's ordered locale list, else English. */
    fun systemLanguage(languageTags:List<String>):String = languageTags
        .asSequence()
        .map { it.substringBefore('-').substringBefore('_').lowercase(java.util.Locale.ROOT) }
        .firstOrNull { it in supportedLanguages } ?: "en"

    fun forLang(code:String):Strings {
        val language=if(code.equals("system",ignoreCase=true))systemLanguage()
            else code.substringBefore('-').substringBefore('_').lowercase(java.util.Locale.ROOT)
        return when(language){"fr"->frStrings;"es"->esStrings;"pt"->ptStrings;else->enStrings}
    }
    fun translate(source:String,language:String):String=UiTranslations.translate(source,language)
    fun translateBlock(source:String,language:String):String=source.split('\n').joinToString("\n"){translate(it,language)}
}
internal val LocalStrings=staticCompositionLocalOf{enStrings}
