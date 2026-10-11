package com.radix.app

/** Validation messages shown while loading portable study backups. */
internal object BackupTranslations {
    private data class Text3(val fr:String,val es:String,val pt:String)
    private val entries=mapOf(
        "Invalid session ID." to Text3("Identifiant de session invalide.","ID de sesión no válido.","ID de sessão inválido."),
        "Give the session a name without control characters." to Text3("Donnez à la session un nom sans caractères de contrôle.","Asigna a la sesión un nombre sin caracteres de control.","Dê à sessão um nome sem caracteres de controle."),
        "Unknown gate." to Text3("Porte inconnue.","Puerta desconocida.","Porta desconhecida."),
        "Invalid gate width." to Text3("Largeur de porte invalide.","Ancho de puerta no válido.","Largura de porta inválida."),
        "Invalid gate session state." to Text3("État de session de porte invalide.","Estado de sesión de puerta no válido.","Estado da sessão de porta inválido."),
        "Unknown circuit." to Text3("Circuit inconnu.","Circuito desconocido.","Circuito desconhecido."),
        "Invalid combinational session state." to Text3("État de session combinatoire invalide.","Estado de sesión combinacional no válido.","Estado da sessão combinacional inválido."),
        "Unknown sequential model." to Text3("Modèle séquentiel inconnu.","Modelo secuencial desconocido.","Modelo sequencial desconhecido."),
        "Invalid sequential session state." to Text3("État de session séquentielle invalide.","Estado de sesión secuencial no válido.","Estado da sessão sequencial inválido."),
        "Forbidden latch inputs must have unknown stored memory." to Text3("Des entrées de verrou interdites doivent avoir une mémoire stockée indéterminée.","Las entradas prohibidas del latch deben tener memoria almacenada desconocida.","Entradas proibidas do latch devem ter memória armazenada desconhecida."),
        "Unknown session kind." to Text3("Type de session inconnu.","Tipo de sesión desconocido.","Tipo de sessão desconhecido."),
        "Duplicate session IDs." to Text3("Identifiants de session en double.","Hay IDs de sesión duplicados.","IDs de sessão duplicados."),
        "Unknown solution form." to Text3("Forme de solution inconnue.","Forma de solución desconocida.","Forma de solução desconhecida."),
        "Duplicate solution IDs." to Text3("Identifiants de solution en double.","Hay IDs de solución duplicados.","IDs de solução duplicados."),
        "A history entry is invalid for its source base." to Text3("Une entrée d’historique n’est pas valide pour sa base source.","Una entrada del historial no es válida para su base de origen.","Uma entrada do histórico é inválida para sua base de origem."),
        "Duplicate history IDs." to Text3("Identifiants d’historique en double.","Hay IDs de historial duplicados.","IDs de histórico duplicados."),
        "Unknown or duplicate practice question ID." to Text3("Identifiant de question inconnu ou en double.","ID de pregunta desconocido o duplicado.","ID de questão desconhecido ou duplicado."),
        "A saved conversion is invalid for its source base." to Text3("Une conversion enregistrée n’est pas valide pour sa base source.","Una conversión guardada no es válida para su base de origen.","Uma conversão salva é inválida para sua base de origem."),
        "Duplicate conversion IDs." to Text3("Identifiants de conversion en double.","Hay IDs de conversión duplicados.","IDs de conversão duplicados."),
        "Invalid representation width/mode." to Text3("Largeur ou mode de représentation invalide.","Ancho o modo de representación no válido.","Largura ou modo de representação inválido."),
        "A history entry is invalid for its source base." to Text3("Une entrée d’historique n’est pas valide pour sa base source.","Una entrada del historial no es válida para su base de origen.","Uma entrada do histórico é inválida para sua base de origem."),
        "A saved conversion is invalid for its source base." to Text3("Une conversion enregistrée n’est pas valide pour sa base source.","Una conversión guardada no es válida para su base de origen.","Uma conversão salva é inválida para sua base de origem."),
        "Unsupported backup format/version. Use a Radix study backup version 1." to Text3("Format ou version de sauvegarde non pris en charge. Utilisez une sauvegarde d’étude Radix de version 1.","Formato o versión de copia no compatible. Usa una copia de estudio Radix versión 1.","Formato ou versão de backup não compatível. Use um backup de estudo Radix versão 1."),
        "Duplicate conversion IDs." to Text3("Identifiants de conversion en double.","Hay IDs de conversión duplicados.","IDs de conversão duplicados."),
        "Unknown or duplicate practice question ID." to Text3("Identifiant de question inconnu ou en double.","ID de pregunta desconocido o duplicado.","ID de questão desconhecido ou duplicado."),
        "A history entry is invalid for its source base." to Text3("Une entrée d’historique n’est pas valide pour sa base source.","Una entrada del historial no es válida para su base de origen.","Uma entrada do histórico é inválida para sua base de origem."),
        "A saved conversion is invalid for its source base." to Text3("Une conversion enregistrée n’est pas valide pour sa base source.","Una conversión guardada no es válida para su base de origen.","Uma conversão salva é inválida para sua base de origem."),
        "Backup exceeds 256 KiB." to Text3("La sauvegarde dépasse 256 Kio.","La copia supera 256 KiB.","O backup excede 256 KiB."),
        "Backup JSON is nested too deeply." to Text3("Le JSON de sauvegarde est trop profondément imbriqué.","El JSON de la copia está demasiado anidado.","O JSON do backup está aninhado profundamente demais."),
        "Backup JSON has too many values." to Text3("Le JSON de sauvegarde contient trop de valeurs.","El JSON de la copia contiene demasiados valores.","O JSON do backup contém valores demais."),
        "Unsupported JSON value" to Text3("Valeur JSON non prise en charge","Valor JSON no compatible","Valor JSON não compatível"),
        "Cannot open backup" to Text3("Impossible d’ouvrir la sauvegarde","No se puede abrir la copia","Não foi possível abrir o backup"),
        "Cannot open output file" to Text3("Impossible d’ouvrir le fichier de sortie","No se puede abrir el archivo de salida","Não foi possível abrir o arquivo de saída")
    )
    private val fields=mapOf(
        "Session" to Text3("Session","Sesión","Sessão"),
        "Session ID" to Text3("Identifiant de session","ID de sesión","ID da sessão"),
        "Session name" to Text3("Nom de session","Nombre de sesión","Nome da sessão"),
        "Sessions" to Text3("Sessions","Sesiones","Sessões"),
        "Saved conversions" to Text3("Conversions enregistrées","Conversiones guardadas","Conversões salvas"),
        "Saved conversion" to Text3("Conversion enregistrée","Conversión guardada","Conversão salva"),
        "Save ID" to Text3("Identifiant d’enregistrement","ID guardado","ID do salvamento"),
        "Saved input" to Text3("Valeur enregistrée","Entrada guardada","Entrada salva"),
        "Saved base" to Text3("Base enregistrée","Base guardada","Base salva"),
        "Saved Boolean solutions" to Text3("Solutions booléennes enregistrées","Soluciones booleanas guardadas","Soluções booleanas salvas"),
        "Boolean solution" to Text3("Solution booléenne","Solución booleana","Solução booleana"),
        "Conversion history" to Text3("Historique des conversions","Historial de conversiones","Histórico de conversões"),
        "History entry" to Text3("Entrée d’historique","Entrada del historial","Entrada do histórico"),
        "Question ID" to Text3("Identifiant de question","ID de pregunta","ID da questão"),
        "Progress" to Text3("Progression","Progreso","Progresso"),
        "Backup version" to Text3("Version de sauvegarde","Versión de la copia","Versão do backup"),
        "K-map" to Text3("Carte de Karnaugh","Mapa de Karnaugh","Mapa de Karnaugh"),
        "Variables" to Text3("Variables","Variables","Variáveis"),
        "Cells" to Text3("Cases","Celdas","Células"),
        "Cell" to Text3("Case","Celda","Célula"),
        "Form" to Text3("Forme","Forma","Forma"),
        "K-map variable labels" to Text3("Noms des variables de la carte","Etiquetas de variables del mapa","Rótulos das variáveis do mapa"),
        "Expression lab" to Text3("Laboratoire d’expressions","Laboratorio de expresiones","Laboratório de expressões"),
        "Expression form" to Text3("Forme d’expression","Forma de expresión","Forma da expressão"),
        "Expression variables" to Text3("Variables de l’expression","Variables de la expresión","Variáveis da expressão"),
        "Expression" to Text3("Expression","Expresión","Expressão"),
        "Comparison expression" to Text3("Expression de comparaison","Expresión de comparación","Expressão de comparação"),
        "Representation lab" to Text3("Laboratoire de représentation","Laboratorio de representación","Laboratório de representação"),
        "Bit width" to Text3("Largeur en bits","Ancho en bits","Largura em bits"),
        "Input mode" to Text3("Mode de saisie","Modo de entrada","Modo de entrada"),
        "Inputs" to Text3("Entrées","Entradas","Entradas"),
        "Bits" to Text3("Bits","Bits","Bits"),
        "Width" to Text3("Largeur","Ancho","Largura"),
        "Clock" to Text3("Horloge","Reloj","Clock"),
        "Kind" to Text3("Type","Tipo","Tipo"),
        "Model" to Text3("Modèle","Modelo","Modelo"),
        "Session kind" to Text3("Type de session","Tipo de sesión","Tipo de sessão"),
        "Session model" to Text3("Modèle de session","Modelo de sesión","Modelo da sessão"),
        "Session width" to Text3("Largeur de session","Ancho de sesión","Largura da sessão"),
        "Session inputs" to Text3("Entrées de session","Entradas de sesión","Entradas da sessão"),
        "Input" to Text3("Entrée","Entrada","Entrada"),
        "Stored state" to Text3("État mémorisé","Estado almacenado","Estado armazenado"),
        "Saved Boolean solution" to Text3("Solution booléenne enregistrée","Solución booleana guardada","Solução booleana salva"),
        "Solution ID" to Text3("Identifiant de solution","ID de solución","ID da solução"),
        "Solution variables" to Text3("Variables de la solution","Variables de la solución","Variáveis da solução"),
        "Saved expression" to Text3("Expression enregistrée","Expresión guardada","Expressão salva"),
        "Solution form" to Text3("Forme de la solution","Forma de la solución","Forma da solução"),
        "History ID" to Text3("Identifiant d’historique","ID del historial","ID do histórico"),
        "History input" to Text3("Entrée d’historique","Entrada del historial","Entrada do histórico"),
        "History base" to Text3("Base de l’historique","Base del historial","Base do histórico"),
        "Signed mode" to Text3("Mode signé","Modo con signo","Modo com sinal"),
        "Representation input" to Text3("Entrée de représentation","Entrada de representación","Entrada da representação"),
        "Haptics" to Text3("Retour haptique","Respuesta háptica","Resposta háptica"),
        "Large controls" to Text3("Commandes agrandies","Controles grandes","Controles maiores"),
        "Settings" to Text3("Paramètres","Ajustes","Configurações"),
        "Converter" to Text3("Convertisseur","Conversor","Conversor"),
        "Source base" to Text3("Base source","Base de origen","Base de origem"),
        "Custom base" to Text3("Base personnalisée","Base personalizada","Base personalizada"),
        "Bit lab value" to Text3("Valeur du laboratoire de bits","Valor del laboratorio de bits","Valor do laboratório de bits"),
        "Current number" to Text3("Nombre actuel","Número actual","Número atual"),
        "Theme" to Text3("Thème","Tema","Tema"),
        "Backup" to Text3("Sauvegarde","Copia","Backup")
    )
    fun translate(source:String,language:String):String? {
        val lang=language.lowercase();if(lang !in setOf("fr","es","pt"))return null
        fun local(text:Text3)=when(lang) {"fr"->text.fr;"es"->text.es;else->text.pt}
        entries[source]?.let(::local)?.let{return it}
        fun field(name:String)=fields[name]?.let(::local)?:name
        val tooMany=Regex("^(.+) has too many entries \\(maximum (\\d+)\\)\\.$").matchEntire(source)
        if(tooMany!=null)return when(lang) {"fr"->"Le champ « ${field(tooMany.groupValues[1])} » contient trop d’éléments (maximum ${tooMany.groupValues[2]}).";"es"->"El campo « ${field(tooMany.groupValues[1])} » tiene demasiadas entradas (máximo ${tooMany.groupValues[2]}).";else->"O campo « ${field(tooMany.groupValues[1])} » tem entradas demais (máximo ${tooMany.groupValues[2]})."}
        val fieldError=Regex("^(.+) (must be an object|must be a list|must be text|is too long|must be true or false|must be an integer|is out of range or not an exact integer)\\.$").matchEntire(source)
        if(fieldError!=null){
            val name=field(fieldError.groupValues[1]);return when(fieldError.groupValues[2]) {
                "must be an object"->when(lang){"fr"->"Le champ « $name » doit être un objet.";"es"->"El campo « $name » debe ser un objeto.";else->"O campo « $name » precisa ser um objeto."}
                "must be a list"->when(lang){"fr"->"Le champ « $name » doit être une liste.";"es"->"El campo « $name » debe ser una lista.";else->"O campo « $name » precisa ser uma lista."}
                "must be text"->when(lang){"fr"->"Le champ « $name » doit contenir du texte.";"es"->"El campo « $name » debe contener texto.";else->"O campo « $name » precisa conter texto."}
                "is too long"->when(lang){"fr"->"Le champ « $name » est trop long.";"es"->"El campo « $name » es demasiado largo.";else->"O campo « $name » é longo demais."}
                "must be true or false"->when(lang){"fr"->"Le champ « $name » doit valoir vrai ou faux.";"es"->"El campo « $name » debe ser verdadero o falso.";else->"O campo « $name » deve ser verdadeiro ou falso."}
                "must be an integer"->when(lang){"fr"->"Le champ « $name » doit être un entier.";"es"->"El campo « $name » debe ser un entero.";else->"O campo « $name » precisa ser um número inteiro."}
                else->when(lang){"fr"->"Le champ « $name » est hors limites ou n’est pas un entier exact.";"es"->"El campo « $name » está fuera de rango o no es un entero exacto.";else->"O campo « $name » está fora do intervalo ou não é um inteiro exato."}
            }
        }
        val invalidJson=Regex("^Invalid backup JSON: (.+) at (\\d+)\\.$").matchEntire(source)
        if(invalidJson!=null){
            val error=invalidJson.groupValues[1]
            val expected=Regex("^expected (true|false|null)$").matchEntire(error)?.groupValues?.get(1)
            val reason=when {
                expected!=null->when(lang) {"fr"->"valeur attendue « $expected »";"es"->"se esperaba « $expected »";else->"esperava « $expected »"}
                else->when(error) {
                    "unexpected trailing text"->when(lang){"fr"->"texte superflu";"es"->"texto sobrante";else->"texto excedente"}
                    "unexpected end"->when(lang){"fr"->"fin inattendue";"es"->"fin inesperado";else->"fim inesperado"}
                    "unexpected character"->when(lang){"fr"->"caractère inattendu";"es"->"carácter inesperado";else->"caractere inesperado"}
                    "control character in string"->when(lang){"fr"->"caractère de contrôle dans une chaîne";"es"->"carácter de control en una cadena";else->"caractere de controle em uma string"}
                    "incomplete escape"->when(lang){"fr"->"séquence d’échappement incomplète";"es"->"secuencia de escape incompleta";else->"sequência de escape incompleta"}
                    "incomplete Unicode escape"->when(lang){"fr"->"séquence d’échappement Unicode incomplète";"es"->"secuencia de escape Unicode incompleta";else->"sequência de escape Unicode incompleta"}
                    "invalid Unicode escape"->when(lang){"fr"->"séquence d’échappement Unicode invalide";"es"->"secuencia de escape Unicode no válida";else->"sequência de escape Unicode inválida"}
                    "invalid escape"->when(lang){"fr"->"séquence d’échappement invalide";"es"->"secuencia de escape no válida";else->"sequência de escape inválida"}
                    "unclosed string"->when(lang){"fr"->"chaîne non terminée";"es"->"cadena sin cerrar";else->"string não encerrada"}
                    "expected object key"->when(lang){"fr"->"clé d’objet attendue";"es"->"se esperaba una clave de objeto";else->"esperava uma chave de objeto"}
                    "expected colon"->when(lang){"fr"->"deux-points attendu";"es"->"se esperaba dos puntos";else->"esperava dois-pontos"}
                    "duplicate key"->when(lang){"fr"->"clé en double";"es"->"clave duplicada";else->"chave duplicada"}
                    "unclosed object"->when(lang){"fr"->"objet non fermé";"es"->"objeto sin cerrar";else->"objeto não encerrado"}
                    "expected comma or closing brace"->when(lang){"fr"->"virgule ou accolade fermante attendue";"es"->"se esperaba una coma o una llave de cierre";else->"esperava vírgula ou chave de fechamento"}
                    "unclosed array"->when(lang){"fr"->"tableau non fermé";"es"->"lista sin cerrar";else->"lista não encerrada"}
                    "expected comma or closing bracket"->when(lang){"fr"->"virgule ou crochet fermant attendu";"es"->"se esperaba una coma o un corchete de cierre";else->"esperava vírgula ou colchete de fechamento"}
                    "incomplete number"->when(lang){"fr"->"nombre incomplet";"es"->"número incompleto";else->"número incompleto"}
                    "invalid number"->when(lang){"fr"->"nombre invalide";"es"->"número no válido";else->"número inválido"}
                    "empty fraction"->when(lang){"fr"->"fraction vide";"es"->"fracción vacía";else->"fração vazia"}
                    "empty exponent"->when(lang){"fr"->"exposant vide";"es"->"exponente vacío";else->"expoente vazio"}
                    "number out of range"->when(lang){"fr"->"nombre hors limites";"es"->"número fuera de rango";else->"número fora do intervalo"}
                    else->return null
                }
            }
            return when(lang) {"fr"->"JSON de sauvegarde invalide à la position ${invalidJson.groupValues[2]} : $reason.";"es"->"JSON de la copia no válido en la posición ${invalidJson.groupValues[2]}: $reason.";else->"JSON do backup inválido na posição ${invalidJson.groupValues[2]}: $reason."}
        }
        val save=Regex("^Could not save backup: (.+)$").matchEntire(source)
        if(save!=null){val detail=translate(save.groupValues[1],lang)?:save.groupValues[1];return when(lang) {"fr"->"Impossible d’enregistrer la sauvegarde : $detail";"es"->"No se pudo guardar la copia: $detail";else->"Não foi possível salvar o backup: $detail"}}
        val valid=Regex("^✓ Valid: (\\d+) conversions · (\\d+) sessions · (\\d+) mastered cards · (\\d+)-variable (SOP|POS) K-map\\.$").matchEntire(source)
        if(valid!=null)return when(lang) {"fr"->"✓ Valide : ${valid.groupValues[1]} conversions · ${valid.groupValues[2]} sessions · ${valid.groupValues[3]} cartes maîtrisées · carte de Karnaugh à ${valid.groupValues[4]} variables (${valid.groupValues[5]}).";"es"->"✓ Válida: ${valid.groupValues[1]} conversiones · ${valid.groupValues[2]} sesiones · ${valid.groupValues[3]} tarjetas dominadas · mapa de Karnaugh de ${valid.groupValues[4]} variables (${valid.groupValues[5]}).";else->"✓ Válido: ${valid.groupValues[1]} conversões · ${valid.groupValues[2]} sessões · ${valid.groupValues[3]} cartões dominados · mapa de Karnaugh de ${valid.groupValues[4]} variáveis (${valid.groupValues[5]})."}
        return null
    }
}
