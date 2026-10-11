package com.radix.app

/** Localization for generated validation feedback and numeric-lab labels. */
internal object CoreEngineTranslations {
    private data class Text3(val fr:String,val es:String,val pt:String)
    private val entries=mapOf(
        "Choose a base between 2 and 36." to Text3("Choisissez une base entre 2 et 36.","Elige una base entre 2 y 36.","Escolha uma base entre 2 e 36."),
        "Add a number after the sign." to Text3("Ajoutez un nombre après le signe.","Añade un número después del signo.","Adicione um número após o sinal."),
        "Add digits after the base prefix." to Text3("Ajoutez des chiffres après le préfixe de base.","Añade dígitos después del prefijo de base.","Adicione dígitos após o prefixo da base."),
        "Use digits, one point, and (parentheses) for repeating digits." to Text3("Utilisez des chiffres, un point et des parenthèses pour les chiffres périodiques.","Usa dígitos, un punto y paréntesis para indicar dígitos periódicos.","Use dígitos, um ponto e parênteses para indicar dígitos periódicos."),
        "Enter at least one digit." to Text3("Saisissez au moins un chiffre.","Introduce al menos un dígito.","Digite pelo menos um dígito."),
        "Enter a whole decimal integer, not a fraction or base prefix." to Text3("Saisissez un entier décimal, pas une fraction ni un préfixe de base.","Introduce un entero decimal, no una fracción ni un prefijo de base.","Digite um número inteiro decimal, não uma fração ou prefixo de base."),
        "Use only binary 0/1; spaces and underscores may group digits." to Text3("Utilisez uniquement 0 et 1 ; les espaces et les tirets bas peuvent séparer les chiffres.","Usa solo 0 y 1; puedes separar los dígitos con espacios o guiones bajos.","Use apenas 0 e 1; espaços e sublinhados podem separar os dígitos."),
        "Choose Decimal, Binary word or Gray code." to Text3("Choisissez Décimal, mot binaire ou code Gray.","Elige decimal, palabra binaria o código Gray.","Escolha decimal, palavra binária ou código Gray."),
        "Choose 4, 8, 16, 32 or 64 bits." to Text3("Choisissez 4, 8, 16, 32 ou 64 bits.","Elige 4, 8, 16, 32 o 64 bits.","Escolha 4, 8, 16, 32 ou 64 bits."),
        "Keep input within 128 characters." to Text3("Limitez la saisie à 128 caractères.","Limita la entrada a 128 caracteres.","Mantenha a entrada em até 128 caracteres."),
        "Choose +, −, × or ÷." to Text3("Choisissez +, −, × ou ÷.","Elige +, −, × o ÷.","Escolha +, −, × ou ÷."),
        "Column subtraction needs the larger magnitude first." to Text3("Pour la soustraction par colonnes, placez d’abord la plus grande valeur absolue.","En la resta por columnas, coloca primero la magnitud mayor.","Na subtração por colunas, use primeiro a maior magnitude."),
        "Division by zero is undefined." to Text3("La division par zéro n’est pas définie.","La división por cero no está definida.","A divisão por zero não é definida."),
        "Internal column check failed." to Text3("La vérification interne des colonnes a échoué.","Falló la comprobación interna de columnas.","A verificação interna das colunas falhou."),
        "Internal multiplication check failed." to Text3("La vérification interne de la multiplication a échoué.","Falló la comprobación interna de la multiplicación.","A verificação interna da multiplicação falhou."),
        "Internal division check failed." to Text3("La vérification interne de la division a échoué.","Falló la comprobación interna de la división.","A verificação interna da divisão falhou."),
        "Keep expressions within 256 characters." to Text3("Limitez les expressions à 256 caractères.","Limita las expresiones a 256 caracteres.","Mantenha as expressões em até 256 caracteres."),
        "Enter an expression using A–Z and 0/1." to Text3("Saisissez une expression avec A–Z et 0/1.","Introduce una expresión con A–Z y 0/1.","Digite uma expressão usando A–Z e 0/1."),
        "Use A–Z, 0/1, parentheses and Boolean operators" to Text3("Utilisez A–Z, 0/1, des parenthèses et des opérateurs booléens","Usa A–Z, 0/1, paréntesis y operadores booleanos","Use A–Z, 0/1, parênteses e operadores booleanos"),
        "Nesting is too deep; use at most 32 levels" to Text3("L’imbrication est trop profonde ; utilisez au plus 32 niveaux","La expresión está demasiado anidada; usa como máximo 32 niveles","O aninhamento é profundo demais; use no máximo 32 níveis"),
        "Close the parenthesis" to Text3("Fermez la parenthèse","Cierra el paréntesis","Feche o parêntese"),
        "Expected a variable, 0/1 or parenthesis" to Text3("Une variable, 0/1 ou une parenthèse était attendu","Se esperaba una variable, 0/1 o un paréntesis","Era esperada uma variável, 0/1 ou parêntese"),
        "Check the expression." to Text3("Vérifiez l’expression.","Comprueba la expresión.","Confira a expressão."),
        "Check input." to Text3("Vérifiez la saisie.","Comprueba la entrada.","Confira a entrada."),
        "Check the input." to Text3("Vérifiez la saisie.","Comprueba la entrada.","Confira a entrada."),
        "Check function notation." to Text3("Vérifiez la notation de la fonction.","Comprueba la notación de la función.","Confira a notação da função."),
        "Check expression G." to Text3("Vérifiez l’expression G.","Comprueba la expresión G.","Confira a expressão G."),
        "Correct the expression, then solve again." to Text3("Corrigez l’expression, puis relancez la résolution.","Corrige la expresión y vuelve a resolver.","Corrija a expressão e resolva novamente."),
        "Enter an expression before saving." to Text3("Saisissez une expression avant de l’enregistrer.","Introduce una expresión antes de guardarla.","Digite uma expressão antes de salvar."),
        "Choose SOP or POS." to Text3("Choisissez SOP ou POS.","Elige SOP o POS.","Escolha SOP ou POS."),
        "Saved Boolean-solution library is full (50). Delete an older item first." to Text3("La bibliothèque de solutions booléennes est pleine (50). Supprimez d’abord un ancien élément.","La biblioteca de soluciones booleanas está llena (50). Elimina primero un elemento anterior.","A biblioteca de soluções booleanas está cheia (50). Exclua um item antigo primeiro."),
        "Could not save this solution." to Text3("Impossible d’enregistrer cette solution.","No se pudo guardar esta solución.","Não foi possível salvar esta solução."),
        "Choose a target base from 2 to 36." to Text3("Choisissez une base cible de 2 à 36.","Elige una base de destino entre 2 y 36.","Escolha uma base de destino entre 2 e 36."),
        "Choose a base from 2 to 36." to Text3("Choisissez une base de 2 à 36.","Elige una base entre 2 y 36.","Escolha uma base de 2 a 36."),
        "Width must be 32 or 64." to Text3("La largeur doit être de 32 ou 64 bits.","El ancho debe ser de 32 o 64 bits.","A largura deve ser 32 ou 64 bits."),
        "Keep function notation within 512 characters." to Text3("Limitez la notation de la fonction à 512 caractères.","Limita la notación de la función a 512 caracteres.","Mantenha a notação da função em até 512 caracteres."),
        "Enter a number first." to Text3("Saisissez d’abord un nombre.","Introduce primero un número.","Digite um número primeiro."),
        "Keep each number within 64 digits." to Text3("Limitez chaque nombre à 64 chiffres.","Limita cada número a 64 dígitos.","Mantenha cada número em até 64 dígitos."),
        "Use only base-" to Text3("Utilisez uniquement les chiffres de la base ","Usa solo dígitos de base ","Use apenas dígitos da base "),
        "No points or prefixes." to Text3("Aucun point ni préfixe.","Sin puntos ni prefijos.","Sem pontos ou prefixos."),
        "Too many gates (maximum 12)." to Text3("Trop de portes (maximum : 12).","Demasiadas puertas (máximo: 12).","Portas demais (máximo: 12)."),
        "Too many wires (maximum 28)." to Text3("Trop de fils (maximum : 28).","Demasiados cables (máximo: 28).","Fios demais (máximo: 28)."),
        "Four external inputs A–D are required." to Text3("Quatre entrées externes A–D sont nécessaires.","Se requieren cuatro entradas externas A–D.","São necessárias quatro entradas externas A–D."),
        "Duplicate gate placement." to Text3("Une porte est placée plusieurs fois.","Una puerta está colocada más de una vez.","Há uma porta posicionada mais de uma vez."),
        "Gate IDs must be non-negative." to Text3("Les identifiants de porte doivent être positifs ou nuls.","Los identificadores de puerta deben ser positivos o cero.","Os IDs das portas devem ser positivos ou zero."),
        "Wire one signal into the probe (Y) to observe an output." to Text3("Reliez un signal à la sonde (Y) pour observer une sortie.","Conecta una señal a la sonda (Y) para observar una salida.","Conecte um sinal à sonda (Y) para observar uma saída."),
        "Feedback loop detected — latches and flip-flops live in the Sequential tab." to Text3("Boucle de rétroaction détectée — les verrous et bascules se trouvent dans l’onglet Séquentiel.","Se detectó un bucle de realimentación; los latches y flip-flops están en la pestaña Secuencial.","Loop de realimentação detectado — latches e flip-flops ficam na aba Sequencial."),
        "Wire the probe first." to Text3("Reliez d’abord la sonde.","Conecta primero la sonda.","Conecte a sonda primeiro."),
        "Wire one signal into the probe (Y) to observe an output." to Text3("Reliez un signal à la sonde (Y) pour observer une sortie.","Conecta una señal a la sonda (Y) para observar una salida.","Conecte um sinal à sonda (Y) para observar uma saída."),
        "XOR from 4 NANDs" to Text3("XOR avec 4 NAND","XOR con 4 NAND","XOR com 4 NAND"),
        "Half adder (probe = Sum)" to Text3("Demi-additionneur (sonde = somme)","Semisumador (sonda = suma)","Meio somador (sonda = soma)"),
        "2:1 MUX from gates" to Text3("Multiplexeur 2:1 avec des portes","Multiplexor 2:1 con puertas","Multiplexador 2:1 com portas"),
        "Type a decimal number, or inf / −inf / nan." to Text3("Saisissez un nombre décimal, ou inf / −inf / nan.","Introduce un número decimal o inf / −inf / nan.","Digite um número decimal ou inf / −inf / nan."),
        "Invalid representation width/mode." to Text3("Largeur ou mode de représentation invalide.","Ancho o modo de representación no válido.","Largura ou modo de representação inválido."),
        "Invalid K-map dimensions, form or variable labels." to Text3("Dimensions, forme ou noms de variables de la carte de Karnaugh invalides.","Dimensiones, forma o etiquetas de variables del mapa K no válidas.","Dimensões, forma ou rótulos de variáveis do mapa K inválidos."),
        "Unsupported backup format/version. Use a Radix study backup version 1." to Text3("Format ou version de sauvegarde non pris en charge. Utilisez une sauvegarde d’étude Radix de version 1.","Formato o versión de copia no compatible. Usa una copia de estudio Radix versión 1.","Formato ou versão de backup não compatível. Use um backup de estudo Radix versão 1.")
    )

    fun translate(source:String,language:String):String? {
        val lang=language.lowercase()
        if(lang !in setOf("fr","es","pt"))return null
        entries[source]?.let {return it.forLanguage(lang)}
        fun tr(fr:String,es:String,pt:String)=when(lang) {"fr"->fr;"es"->es;else->pt}
        val baseName=when(source) {"Binary"->"binary";"Octal"->"octal";"Decimal"->"decimal";"Hexadecimal"->"hexadecimal";else->null}
        if(baseName!=null)return when(baseName) {"binary"->tr("Binaire","Binario","Binário");"octal"->"Octal";"decimal"->tr("Décimal","Decimal","Decimal");else->tr("Hexadécimal","Hexadecimal","Hexadecimal")}
        val genericBase=Regex("^Base (\\d+)$").matchEntire(source)
        if(genericBase!=null)return "Base ${genericBase.groupValues[1]}"
        val digits=Regex("^Digits 0–([0-9A-Z])$").matchEntire(source)
        if(digits!=null)return when(lang) {"fr"->"Chiffres de 0 à ${digits.groupValues[1]}";"es"->"Dígitos 0–${digits.groupValues[1]}";else->"Dígitos de 0 a ${digits.groupValues[1]}"}
        val wideDigits=Regex("^Digits 0–9, A–([0-9A-Z])$").matchEntire(source)
        if(wideDigits!=null)return when(lang) {"fr"->"Chiffres de 0 à 9, A à ${wideDigits.groupValues[1]}";"es"->"Dígitos 0–9, A–${wideDigits.groupValues[1]}";else->"Dígitos de 0 a 9, A a ${wideDigits.groupValues[1]}"}
        val digitError=Regex("^(.+) isn't a base-(\\d+) digit\\. Use (.+)\\.$").matchEntire(source)
        if(digitError!=null){
            val allowed=digitError.groupValues[3].replace(" and ",when(lang){"fr"->" et ";"es"->" y ";else->" e "})
            return when(lang) {"fr"->"${digitError.groupValues[1]} n’est pas un chiffre en base ${digitError.groupValues[2]}. Utilisez $allowed.";"es"->"${digitError.groupValues[1]} no es un dígito en base ${digitError.groupValues[2]}. Usa $allowed.";else->"${digitError.groupValues[1]} não é um dígito na base ${digitError.groupValues[2]}. Use $allowed."}
        }
        val arithmeticDigits=Regex("^Use only base-(\\d+) digits 0–([0-9A-Z]); no points or prefixes\\.$").matchEntire(source)
        if(arithmeticDigits!=null)return when(lang) {"fr"->"Utilisez uniquement les chiffres de la base ${arithmeticDigits.groupValues[1]}, de 0 à ${arithmeticDigits.groupValues[2]} ; aucun point ni préfixe.";"es"->"Usa solo dígitos de base ${arithmeticDigits.groupValues[1]}, de 0 a ${arithmeticDigits.groupValues[2]}; sin puntos ni prefijos.";else->"Use apenas dígitos da base ${arithmeticDigits.groupValues[1]}, de 0 a ${arithmeticDigits.groupValues[2]}; sem pontos ou prefixos."}
        val targetBase=Regex("^Target base (\\d+) · (Binary|Octal|Decimal|Hexadecimal|Base \\d+)$").matchEntire(source)
        if(targetBase!=null){val name=translate(targetBase.groupValues[2],lang)?:targetBase.groupValues[2];return when(lang) {"fr"->"Base cible ${targetBase.groupValues[1]} · $name";"es"->"Base de destino ${targetBase.groupValues[1]} · $name";else->"Base de destino ${targetBase.groupValues[1]} · $name"}}
        if(source=="External inputs are A–D.")return when(lang) {"fr"->"Les entrées externes sont A–D.";"es"->"Las entradas externas son A–D.";else->"As entradas externas são A–D."}
        if(source=="NOT/BUFFER need one input; other gates need 2–4.")return when(lang) {"fr"->"NOT/BUFFER nécessitent une entrée ; les autres portes en nécessitent 2 à 4.";"es"->"NOT/BUFFER necesitan una entrada; las demás puertas necesitan de 2 a 4.";else->"NOT/BUFFER precisam de uma entrada; as demais portas precisam de 2 a 4."}
        val wire=Regex("^Wire (\\d+): (.+)\\.$").matchEntire(source)
        if(wire!=null){
            val detail=wire.groupValues[2]
            val translated=when {
                detail=="external input must be A–D"->tr("l’entrée externe doit être A–D","la entrada externa debe ser A–D","a entrada externa deve ser A–D")
                detail=="the probe is an output — it cannot drive a wire"->tr("la sonde est une sortie et ne peut pas alimenter un fil","la sonda es una salida y no puede alimentar un cable","a sonda é uma saída e não pode alimentar um fio")
                detail=="unknown source gate"->tr("porte source inconnue","puerta de origen desconocida","porta de origem desconhecida")
                detail=="gates have one output pin"->tr("les portes ont une seule broche de sortie","las puertas tienen un pin de salida","as portas têm um pino de saída")
                detail=="the probe has one input"->tr("la sonde a une seule entrée","la sonda tiene una entrada","a sonda tem uma entrada")
                detail=="external inputs cannot be driven"->tr("les entrées externes ne peuvent pas être pilotées","las entradas externas no se pueden controlar desde el circuito","entradas externas não podem ser acionadas")
                detail=="unknown target gate"->tr("porte cible inconnue","puerta de destino desconocida","porta de destino desconhecida")
                detail.startsWith("pin out of range for ")->tr("broche hors plage pour ${detail.substringAfter("for ")}","pin fuera de rango para ${detail.substringAfter("for ")}","pino fora do intervalo para ${detail.substringAfter("for ")}")
                else->detail
            }
            return "${tr("Fil","Cable","Fio")} ${wire.groupValues[1]} : $translated."
        }
        val probeDrivers=Regex("^The probe has (\\d+) drivers; keep exactly one\\.$").matchEntire(source)
        if(probeDrivers!=null)return when(lang) {"fr"->"La sonde a ${probeDrivers.groupValues[1]} pilotes ; gardez-en exactement un.";"es"->"La sonda tiene ${probeDrivers.groupValues[1]} conexiones; deja exactamente una.";else->"A sonda tem ${probeDrivers.groupValues[1]} acionadores; mantenha exatamente um."}
        val gateDrivers=Regex("^Gate (\\d+) pin (\\d+) has (\\d+) drivers; keep exactly one\\.$").matchEntire(source)
        if(gateDrivers!=null)return when(lang) {"fr"->"La broche ${gateDrivers.groupValues[2]} de la porte ${gateDrivers.groupValues[1]} a ${gateDrivers.groupValues[3]} pilotes ; gardez-en exactement un.";"es"->"El pin ${gateDrivers.groupValues[2]} de la puerta ${gateDrivers.groupValues[1]} tiene ${gateDrivers.groupValues[3]} conexiones; deja solo una.";else->"O pino ${gateDrivers.groupValues[2]} da porta ${gateDrivers.groupValues[1]} tem ${gateDrivers.groupValues[3]} acionadores; mantenha apenas um."}
        val unwired=Regex("^([A-Z]+) gate (\\d+) pin (\\d+) is unwired\\.$").matchEntire(source)
        if(unwired!=null)return when(lang) {"fr"->"La broche ${unwired.groupValues[3]} de la porte ${unwired.groupValues[1]} ${unwired.groupValues[2]} n’est pas câblée.";"es"->"El pin ${unwired.groupValues[3]} de la puerta ${unwired.groupValues[1]} ${unwired.groupValues[2]} no está conectado.";else->"O pino ${unwired.groupValues[3]} da porta ${unwired.groupValues[1]} ${unwired.groupValues[2]} está desconectado."}
        val chars=Regex("^Keep your input to (\\d+) digits or fewer\\.$").matchEntire(source)
        if(chars!=null)return when(lang) {"fr"->"Limitez la saisie à ${chars.groupValues[1]} chiffres.";"es"->"Limita la entrada a ${chars.groupValues[1]} dígitos.";else->"Mantenha a entrada em até ${chars.groupValues[1]} dígitos."}
        val count=Regex("^This expression uses (\\d+) different variables; select at least that many inputs \\(maximum 6\\)\\.$").matchEntire(source)
        if(count!=null)return when(lang) {"fr"->"Cette expression utilise ${count.groupValues[1]} variables différentes ; sélectionnez au moins autant d’entrées (maximum 6).";"es"->"Esta expresión usa ${count.groupValues[1]} variables distintas; selecciona al menos esa cantidad de entradas (máximo 6).";else->"Esta expressão usa ${count.groupValues[1]} variáveis diferentes; selecione pelo menos esse número de entradas (máximo 6)."}
        val position=Regex("^(Use A–Z, 0/1, parentheses and Boolean operators|Nesting is too deep; use at most 32 levels|Close the parenthesis|Expected a variable, 0/1 or parenthesis|Variable [A-Z] is outside the selected input set|Unexpected .+?; check operators/parentheses) · position (\\d+)$").matchEntire(source)
        if(position!=null){
            val p=position.groupValues[2]
            val base=position.groupValues[1]
            val translated=when {
                base.startsWith("Use A–Z") -> tr("Utilisez A–Z, 0/1, des parenthèses et des opérateurs booléens","Usa A–Z, 0/1, paréntesis y operadores booleanos","Use A–Z, 0/1, parênteses e operadores booleanos")
                base.startsWith("Nesting") -> tr("L’imbrication est trop profonde ; utilisez au plus 32 niveaux","La expresión está demasiado anidada; usa como máximo 32 niveles","O aninhamento é profundo demais; use no máximo 32 níveis")
                base=="Close the parenthesis" -> tr("Fermez la parenthèse","Cierra el paréntesis","Feche o parêntese")
                base.startsWith("Expected") -> tr("Une variable, 0/1 ou une parenthèse était attendu","Se esperaba una variable, 0/1 o un paréntesis","Era esperada uma variável, 0/1 ou parêntese")
                base.startsWith("Variable") -> {val v=base.substringAfter("Variable ").substringBefore(' ');tr("La variable $v ne fait pas partie des entrées sélectionnées","La variable $v no está en el conjunto de entradas seleccionado","A variável $v não faz parte do conjunto de entradas selecionado")}
                else -> {val token=base.substringAfter("Unexpected ").substringBefore(';');tr("Opérateur $token inattendu ; vérifiez les opérateurs et les parenthèses","$token inesperado; comprueba los operadores y paréntesis","$token inesperado; confira os operadores e parênteses")}
            }
            return "$translated · ${when(lang){"fr"->"position";"es"->"posición";else->"posição"}} $p"
        }
        val word=Regex("^The word has (\\d+) bits; choose a width of at least that size\\.$").matchEntire(source)
        if(word!=null)return when(lang) {"fr"->"Le mot contient ${word.groupValues[1]} bits ; choisissez une largeur au moins égale.";"es"->"La palabra tiene ${word.groupValues[1]} bits; elige un ancho de al menos ese tamaño.";else->"A palavra tem ${word.groupValues[1]} bits; escolha uma largura de pelo menos esse tamanho."}
        val outOfRange=Regex("^Out of range: (\\d+)-bit (two’s-complement|unsigned) values are (.+) to (.+)\\. No wrapping was applied\\.$").matchEntire(source)
        if(outOfRange!=null){
            val mode=when(outOfRange.groupValues[2]) {"two’s-complement"->tr("en complément à deux","en complemento a dos","em complemento de dois");else->tr("non signées","sin signo","sem sinal")}
            return when(lang) {"fr"->"Hors plage : les valeurs $mode sur ${outOfRange.groupValues[1]} bits vont de ${outOfRange.groupValues[3]} à ${outOfRange.groupValues[4]}. Aucun bouclage n’a été appliqué.";"es"->"Fuera de rango: los valores $mode de ${outOfRange.groupValues[1]} bits van de ${outOfRange.groupValues[3]} a ${outOfRange.groupValues[4]}. No se aplicó ningún desbordamiento circular.";else->"Fora do intervalo: valores $mode de ${outOfRange.groupValues[1]} bits vão de ${outOfRange.groupValues[3]} a ${outOfRange.groupValues[4]}. Nenhum valor foi ajustado por retorno."}
        }
        val range=Regex("^Selected decimal range: (.+) to (.+)\\. No overflowing value is wrapped or silently truncated\\.$").matchEntire(source)
        if(range!=null)return when(lang) {"fr"->"Plage décimale sélectionnée : de ${range.groupValues[1]} à ${range.groupValues[2]}. Aucun dépassement n’est bouclé ou tronqué silencieusement.";"es"->"Rango decimal seleccionado: de ${range.groupValues[1]} a ${range.groupValues[2]}. Ningún valor fuera de rango se ajusta ni se trunca en silencio.";else->"Faixa decimal selecionada: de ${range.groupValues[1]} a ${range.groupValues[2]}. Nenhum valor excedente é ajustado ou truncado silenciosamente."}
        val plainRange=Regex("^(-?\\d+) to (-?\\d+)$").matchEntire(source)
        if(plainRange!=null)return when(lang) {"fr"->"${plainRange.groupValues[1]} à ${plainRange.groupValues[2]}";"es"->"${plainRange.groupValues[1]} a ${plainRange.groupValues[2]}";else->"${plainRange.groupValues[1]} a ${plainRange.groupValues[2]}"}
        val preset=Regex("^Type exactly (\\d+) (binary digits|hex digits)\\.$").matchEntire(source)
        if(preset!=null)return when(lang) {"fr"->"Saisissez exactement ${preset.groupValues[1]} ${if(preset.groupValues[2]=="binary digits")"chiffres binaires"else"chiffres hexadécimaux"}.";"es"->"Escribe exactamente ${preset.groupValues[1]} ${if(preset.groupValues[2]=="binary digits")"dígitos binarios"else"dígitos hexadecimales"}.";else->"Digite exatamente ${preset.groupValues[1]} ${if(preset.groupValues[2]=="binary digits")"dígitos binários"else"dígitos hexadecimais"}."}
        return null
    }
    private fun Text3.forLanguage(lang:String)=when(lang) {"fr"->fr;"es"->es;"pt"->pt;else->error("Unsupported language")}
}
