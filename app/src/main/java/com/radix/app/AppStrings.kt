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
    val systemLanguageNote:String,
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
    tourConvertBody="Type 255 once and watch it become FF, 377 and 11111111. Every answer shows its working.",
    tourLogicTitle="Tap an input. Watch the wire.",
    tourLogicBody="Gates, adders, flip-flops and counters respond live. Open any circuit to see the gates inside.",
    tourKmapTitle="Circle 1s. Shrink expressions.",
    tourKmapBody="SOP groups the 1s; POS groups the 0s. Toggle group overlays and cell indices, inspect each term, and follow its explanation. Tables contain 2ⁿ rows: 8 for 3 inputs, 16 for 4.",
    tourPracticeTitle="Easy first. Exams later.",
    tourPracticeBody="108 objective cards across Easy, Intermediate and Advanced, plus written self-checks. The guided path runs from number systems to sequential logic; hints come before answers.",
    tourRemindTitle="A nudge, not a nag.",
    tourRemindBody="One gentle daily reminder at your hour, with an optional bundled chime and streak celebrations you can switch off. Silence or change its sound in Android notification settings. Everything stays offline.",
    whatsNewTitle="What’s new in Radix 7.0",
    whatsNewIntro="Your saved work stays on this device. This update widens the logic labs and improves reminders:",
    whatsNewItems=listOf(
        "Boolean expressions accept A–Z; expression/K-map tools cover 2–6 variables with exact 2ⁿ-row tables (8 rows for 3 inputs, 16 for 4; up to 64)",
        "23 combinational models, including the 16:1 MUX; its 1,048,576-row truth table is paged 16 rows at a time",
        "Optional bundled CC0 reminder chime, an in-app preview and direct Android notification-channel settings",
        "Still included: circuit builder, IEEE-754 lab, 108 practice cards, local streaks and up to 8 profiles",
        "Core menus, onboarding and reminders follow the device language (English, French, Spanish or Portuguese); many technical lessons remain in English",
        "No account or internet connection is needed; you control notification permission and sound"),
    whatsNewKeep="Your saved conversions, sessions, mastered cards and backups are untouched.",
    keepExploring="Keep exploring",setReminder="Set a reminder",
    themeTitle="Theme",themeDark="Dark",themeLight="Light",themeSystem="System",
    systemLanguageNote="Core menus, onboarding and reminders follow the device language (English, French, Spanish or Portuguese); other languages fall back to English. Many study, circuit and lesson screens remain in English.",
    reminderTitle="Study reminders",reminderIntro="One optional reminder a day, scheduled on this device only. Includes a gentle bundled chime; preview it or silence/change it in Android notification settings. No account or network.",
    reminderDaily="Daily reminder",reminderDailyNote="Fires once at your chosen time",
    reminderStreak="Streak celebrations",reminderTips="Tips inside reminders",
    pickTime="Pick a time",save="Save",cancel="Cancel",
    reminderOnToast="Reminder set — see you then.",
    reminderBlockedToast="System blocked notifications — reminders stay in your inbox.",
    reminderPreview="Preview reminder",reminderSettings="Sound & notification settings",reminderPreviewToast="Preview notification sent. Check the sound or change it in system settings.",
    landscapeKmapHint="For 5–6 variables, swipe horizontally or rotate your device to landscape to inspect the full map.",landscapeCircuitHint="Wide input bank: rotate your device to landscape for more room. The truth table is generated 16 rows per page.",landscapeBuilderHint="The full circuit lab is easier to use in landscape: rotate your device for more canvas space. Portrait remains available.",convertRandomize="Randomize value",
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
    tourConvertBody="Tapez 255 une fois et voyez FF, 377 et 11111111. Chaque réponse montre son raisonnement.",
    tourLogicTitle="Touchez une entrée. Suivez le fil.",
    tourLogicBody="Portes, additionneurs, bascules et compteurs réagissent en direct. Ouvrez chaque circuit pour voir les portes.",
    tourKmapTitle="Entourez les 1. Réduisez tout.",
    tourKmapBody="En SOP, regroupez les 1 ; en POS, les 0. Affichez les groupes et indices, puis lisez chaque terme et son explication. Les tables ont 2ⁿ lignes : 8 pour 3 entrées, 16 pour 4.",
    tourPracticeTitle="Facile d’abord. Examens ensuite.",
    tourPracticeBody="108 questions à choix multiples, trois niveaux, plus des réponses rédigées en autoévaluation. Le parcours va des systèmes de numération à la logique séquentielle ; les indices précèdent toujours les réponses.",
    tourRemindTitle="Un rappel, pas un reproche.",
    tourRemindBody="Un rappel quotidien à votre heure, avec une sonnerie intégrée facultative et des séries désactivables. Réglez ou coupez le son dans les paramètres de notifications Android. Tout reste hors ligne.",
    whatsNewTitle="Nouveautés de Radix 7.0",
    whatsNewIntro="Vos données restent sur l’appareil. Cette mise à jour élargit les outils logiques et améliore les rappels :",
    whatsNewItems=listOf(
        "Les expressions acceptent A–Z ; les outils couvrent 2–6 variables et calculent exactement 2ⁿ lignes (8 pour 3 entrées, 16 pour 4, jusqu’à 64)",
        "23 circuits combinatoires, dont le multiplexeur 16:1 ; table d’un million de lignes par pages de 16",
        "Sonnerie CC0 intégrée et facultative, aperçu dans l’appli et réglages de notifications Android",
        "Toujours inclus : constructeur, labo IEEE-754, 108 cartes, séries locales et 8 profils",
        "Les menus principaux, l’accueil et les rappels suivent la langue de l’appareil (anglais, français, espagnol ou portugais) ; plusieurs leçons techniques restent en anglais",
        "Sans compte ni connexion Internet ; vous contrôlez l’autorisation et le son des notifications"),
    whatsNewKeep="Vos conversions, sessions, cartes maîtrisées et sauvegardes sont intactes.",
    keepExploring="Continuer",setReminder="Créer un rappel",
    themeTitle="Thème",themeDark="Sombre",themeLight="Clair",themeSystem="Système",
    systemLanguageNote="Les menus principaux, l’accueil et les rappels suivent la langue de l’appareil (anglais, français, espagnol ou portugais) ; les autres langues utilisent l’anglais. Plusieurs écrans de cours, circuits et leçons restent en anglais.",
    reminderTitle="Rappels d’étude",reminderIntro="Un rappel facultatif par jour, planifié sur cet appareil. Sonnerie douce intégrée : écoutez l’aperçu ou réglez/coupez le son dans les paramètres Android. Sans compte ni réseau.",
    reminderDaily="Rappel quotidien",reminderDailyNote="Une fois par jour, à votre heure",
    reminderStreak="Fêter les séries",reminderTips="Astuces dans les rappels",
    pickTime="Choisir l’heure",save="Enregistrer",cancel="Annuler",
    reminderOnToast="Rappel programmé — à tout à l’heure.",
    reminderBlockedToast="Notifications bloquées — les rappels restent dans la boîte de réception.",
    reminderPreview="Écouter un aperçu",reminderSettings="Son et notifications",reminderPreviewToast="Notification test envoyée. Réglez le son dans les paramètres système.",
    landscapeKmapHint="Pour 5–6 variables, faites défiler horizontalement ou passez en paysage pour voir toute la carte.",landscapeCircuitHint="Entrées nombreuses : passez en paysage. La table est générée par pages de 16 lignes.",landscapeBuilderHint="Le labo de circuits est plus confortable en paysage : pivotez l’appareil pour agrandir le canevas. Le mode portrait reste possible.",convertRandomize="Valeur aléatoire",
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
    tourConvertBody="Escribe 255 una vez y míralo convertirse en FF, 377 y 11111111. Cada respuesta muestra su procedimiento.",
    tourLogicTitle="Toca una entrada. Sigue el cable.",
    tourLogicBody="Puertas, sumadores, flip-flops y contadores responden en vivo. Abre cualquier circuito para ver las puertas.",
    tourKmapTitle="Rodea los 1. Reduce todo.",
    tourKmapBody="En SOP, agrupa los 1; en POS, los 0. Activa los grupos e índices, y lee cada término con su explicación. Las tablas tienen 2ⁿ filas: 8 con 3 entradas y 16 con 4.",
    tourPracticeTitle="Fácil primero. Exámenes después.",
    tourPracticeBody="108 preguntas de opción múltiple en tres niveles, además de respuestas escritas para autoevaluar. La ruta va de los sistemas numéricos a la lógica secuencial; siempre hay pistas antes de responder.",
    tourRemindTitle="Un recordatorio, no un regaño.",
    tourRemindBody="Un recordatorio diario a tu hora, con tono integrado opcional y rachas desactivables. Cambia o silencia el sonido en los ajustes de notificaciones de Android. Todo sin conexión.",
    whatsNewTitle="Novedades de Radix 7.0",
    whatsNewIntro="Tus datos siguen en este dispositivo. Esta actualización amplía la lógica y mejora los recordatorios:",
    whatsNewItems=listOf(
        "Las expresiones aceptan A–Z; las herramientas cubren 2–6 variables y calculan exactamente 2ⁿ filas (8 con 3 entradas, 16 con 4, hasta 64)",
        "23 circuitos combinacionales, incluido el MUX 16:1; un millón de filas por páginas de 16",
        "Tono CC0 integrado y opcional, vista previa y ajustes de notificaciones de Android",
        "También incluye: constructor, laboratorio IEEE-754, 108 tarjetas, rachas y 8 perfiles",
        "Los menús principales, la bienvenida y los recordatorios siguen el idioma del dispositivo (inglés, francés, español o portugués); muchas lecciones técnicas siguen en inglés",
        "Sin cuenta ni conexión; tú controlas el permiso y el sonido de las notificaciones"),
    whatsNewKeep="Tus conversiones, sesiones, tarjetas dominadas y copias están intactas.",
    keepExploring="Seguir",setReminder="Crear recordatorio",
    themeTitle="Tema",themeDark="Oscuro",themeLight="Claro",themeSystem="Sistema",
    systemLanguageNote="Los menús principales, la bienvenida y los recordatorios siguen el idioma del dispositivo (inglés, francés, español o portugués); los demás usan inglés. Muchas pantallas de estudio, circuitos y lecciones siguen en inglés.",
    reminderTitle="Recordatorios de estudio",reminderIntro="Un recordatorio opcional al día, solo en este dispositivo. Incluye un tono suave: escúchalo o siléncialo/cámbialo en los ajustes de notificaciones Android. Sin cuenta ni red.",
    reminderDaily="Recordatorio diario",reminderDailyNote="Una vez al día, a tu hora",
    reminderStreak="Celebrar rachas",reminderTips="Trucos en recordatorios",
    pickTime="Elegir hora",save="Guardar",cancel="Cancelar",
    reminderOnToast="Recordatorio programado — nos vemos luego.",
    reminderBlockedToast="Notificaciones bloqueadas — los recordatorios quedan en la bandeja.",
    reminderPreview="Probar recordatorio",reminderSettings="Sonido y notificaciones",reminderPreviewToast="Notificación de prueba enviada. Ajusta el sonido en el sistema.",
    landscapeKmapHint="Con 5–6 variables, desplázate horizontalmente o gira el dispositivo para ver el mapa completo.",landscapeCircuitHint="Muchas entradas: gira el dispositivo. La tabla se genera en páginas de 16 filas.",landscapeBuilderHint="El laboratorio de circuitos es más cómodo en horizontal: gira el dispositivo para ampliar el lienzo. El modo vertical sigue disponible.",convertRandomize="Valor aleatorio",
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
    tourConvertBody="Digite 255 uma vez e veja FF, 377 e 11111111. Cada resposta mostra o cálculo.",
    tourLogicTitle="Toque em uma entrada. Siga o fio.",
    tourLogicBody="Portas, somadores, flip-flops e contadores respondem ao vivo. Abra um circuito para ver as portas internas.",
    tourKmapTitle="Agrupe os 1. Simplifique a lógica.",
    tourKmapBody="No SOP, agrupe os 1; no POS, os 0. Ative os grupos e índices, e leia cada termo e explicação. As tabelas têm 2ⁿ linhas: 8 para 3 entradas e 16 para 4.",
    tourPracticeTitle="Comece pelo fácil. Avance até os exames.",
    tourPracticeBody="108 questões objetivas em três níveis e exercícios escritos de autoavaliação. O percurso vai dos sistemas numéricos à lógica sequencial; as dicas vêm antes das respostas.",
    tourRemindTitle="Um lembrete, sem pressão.",
    tourRemindBody="Um lembrete diário no horário escolhido, com toque integrado opcional e comemorações de sequência que podem ser desativadas. Ajuste ou silencie o som nas configurações de notificações do Android. Tudo funciona offline.",
    whatsNewTitle="Novidades do Radix 7.0",
    whatsNewIntro="Seus dados continuam neste dispositivo. Esta atualização amplia os laboratórios de lógica e melhora os lembretes:",
    whatsNewItems=listOf(
        "Expressões aceitam A–Z; as ferramentas abrangem 2–6 variáveis e calculam exatamente 2ⁿ linhas (8 para 3 entradas, 16 para 4, até 64)",
        "23 modelos combinacionais, incluindo o MUX 16:1; a tabela com 1.048.576 linhas é exibida em páginas de 16",
        "Toque opcional CC0 integrado, prévia no app e acesso às configurações de notificações do Android",
        "Também inclui: construtor de circuitos, laboratório IEEE-754, 108 cartões, sequências locais e até 8 perfis",
        "Menus principais, boas-vindas e lembretes seguem o idioma do dispositivo (inglês, francês, espanhol ou português); muitas lições técnicas permanecem em inglês",
        "Sem conta ou conexão à Internet; você controla a permissão e o som das notificações"),
    whatsNewKeep="Conversões, sessões, cartões dominados e backups continuam intactos.",
    keepExploring="Continuar explorando",setReminder="Definir lembrete",
    themeTitle="Tema",themeDark="Escuro",themeLight="Claro",themeSystem="Sistema",
    systemLanguageNote="Menus principais, boas-vindas e lembretes seguem o idioma do dispositivo (inglês, francês, espanhol ou português); os demais idiomas usam inglês. Muitas telas de estudo, circuitos e lições continuam em inglês.",
    reminderTitle="Lembretes de estudo",reminderIntro="Um lembrete opcional por dia, agendado apenas neste dispositivo. Inclui um toque suave integrado; ouça a prévia ou silencie/altere-o nas configurações de notificações do Android. Sem conta ou rede.",
    reminderDaily="Lembrete diário",reminderDailyNote="Uma vez por dia, no horário escolhido",
    reminderStreak="Comemorar sequências",reminderTips="Dicas nos lembretes",
    pickTime="Escolher horário",save="Salvar",cancel="Cancelar",
    reminderOnToast="Lembrete agendado — até lá.",
    reminderBlockedToast="Notificações bloqueadas pelo sistema — os lembretes continuam na caixa de entrada.",
    reminderPreview="Prévia do lembrete",reminderSettings="Som e notificações",reminderPreviewToast="Notificação de teste enviada. Confira ou altere o som nas configurações do sistema.",
    landscapeKmapHint="Para 5–6 variáveis, deslize na horizontal ou gire o dispositivo para visualizar o mapa completo.",landscapeCircuitHint="Muitas entradas: gire o dispositivo para usar o modo paisagem. A tabela é gerada em páginas de 16 linhas.",landscapeBuilderHint="O laboratório completo de circuitos é mais fácil no modo paisagem: gire o dispositivo para ampliar a área de trabalho. O modo retrato continua disponível.",convertRandomize="Sortear valor",
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
