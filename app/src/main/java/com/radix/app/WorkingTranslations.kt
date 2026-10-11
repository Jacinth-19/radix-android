package com.radix.app

/** Generated step-by-step explanations from the numeric, arithmetic, and IEEE-754 labs. */
internal object WorkingTranslations {
    private data class Text3(val fr:String,val es:String,val pt:String)
    private val entries=mapOf(
        "1. Expand the source place values (work on the magnitude)." to Text3("1. Développez les valeurs de position de la base source (travaillez sur la valeur absolue).","1. Desarrolla los valores posicionales de la base de origen (trabaja con la magnitud).","1. Expanda os valores posicionais da base de origem (trabalhe com a magnitude)."),
        "2. Divide the integer part by the target base. Read remainders BOTTOM TO TOP." to Text3("2. Divisez la partie entière par la base cible. Lisez les restes DE BAS EN HAUT.","2. Divide la parte entera entre la base de destino. Lee los restos DE ABAJO ARRIBA.","2. Divida a parte inteira pela base de destino. Leia os restos DE BAIXO PARA CIMA."),
        "Read the integer remainders from bottom to top." to Text3("Lisez les restes entiers de bas en haut.","Lee los restos enteros de abajo arriba.","Leia os restos inteiros de baixo para cima."),
        "Read the fractional digits from top to bottom." to Text3("Lisez les chiffres fractionnaires de haut en bas.","Lee los dígitos fraccionarios de arriba abajo.","Leia os dígitos fracionários de cima para baixo."),
        "3. Multiply the fractional remainder by the target base. Read digits TOP TO BOTTOM." to Text3("3. Multipliez le reste fractionnaire par la base cible. Lisez les chiffres DE HAUT EN BAS.","3. Multiplica el resto fraccionario por la base de destino. Lee los dígitos DE ARRIBA ABAJO.","3. Multiplique o resto fracionário pela base de destino. Leia os dígitos DE CIMA PARA BAIXO."),
        "Stopped at 24 fractional places. The ellipsis means truncated/approximate, not exact." to Text3("Arrêt après 24 chiffres fractionnaires. Les points de suspension signalent une troncature/approximation, pas un résultat exact.","Se detuvo tras 24 posiciones fraccionarias. Los puntos suspensivos indican truncamiento/aproximación, no exactitud.","Parou após 24 casas fracionárias. As reticências indicam truncamento/aproximação, não um resultado exato."),
        "The remainder is zero: the fraction terminates exactly." to Text3("Le reste est nul : la fraction se termine exactement.","El resto es cero: la fracción termina exactamente.","O resto é zero: a fração termina exatamente."),
        "Restore the minus sign. This is signed-magnitude notation, NOT two's complement." to Text3("Rétablissez le signe moins. Il s’agit d’une notation signe-valeur absolue, PAS du complément à deux.","Restablece el signo menos. Es una representación de signo y magnitud, NO complemento a dos.","Recoloque o sinal de menos. Esta é uma representação sinal-magnitude, NÃO complemento de dois."),
        "Restore the minus sign. This is signed magnitude, not fixed-width two's complement." to Text3("Rétablissez le signe moins. Il s’agit d’une représentation signe-magnitude, pas d’un complément à deux sur largeur fixe.","Restablece el signo menos. Es signo-magnitud, no complemento a dos de ancho fijo.","Recoloque o sinal de menos. É sinal-magnitude, não complemento de dois de largura fixa."),
        "Binary shortcut: group 3 bits at a time around the point. Pad the INTEGER on the LEFT; pad the FRACTION on the RIGHT." to Text3("Astuce binaire : regroupez les bits par 3 autour du point. Complétez la partie ENTIÈRE à GAUCHE et la FRACTION à DROITE.","Atajo binario: agrupa 3 bits alrededor del punto. Completa la parte ENTERA por la IZQUIERDA y la FRACCIÓN por la DERECHA.","Atalho binário: agrupe 3 bits ao redor do ponto. Complete a parte INTEIRA à ESQUERDA e a FRAÇÃO à DIREITA."),
        "Binary shortcut: group 4 bits at a time around the point. Pad the INTEGER on the LEFT; pad the FRACTION on the RIGHT." to Text3("Astuce binaire : regroupez les bits par 4 autour du point. Complétez la partie ENTIÈRE à GAUCHE et la FRACTION à DROITE.","Atajo binario: agrupa 4 bits alrededor del punto. Completa la parte ENTERA por la IZQUIERDA y la FRACCIÓN por la DERECHA.","Atalho binário: agrupe 4 bits ao redor do ponto. Complete a parte INTEIRA à ESQUERDA e a FRAÇÃO à DIREITA."),
        "The remainder repeats: the cycle starts at fractional step " to Text3("Le reste se répète : le cycle commence à l’étape fractionnaire ","El resto se repite: el ciclo empieza en el paso fraccionario ","O resto se repete: o ciclo começa na etapa fracionária "),
        "The remainder is zero: the fraction terminates exactly." to Text3("Le reste est nul : la fraction se termine exactement.","El resto es cero: la fracción termina exactamente.","O resto é zero: a fração termina exatamente."),
        "Binary shortcut: each base-" to Text3("Astuce binaire : chaque chiffre en base ","Atajo binario: cada dígito en base ","Atalho binário: cada dígito na base "),
        "Keep the point in place." to Text3("Gardez le point à la même position.","Mantén el punto en su lugar.","Mantenha o ponto na mesma posição."),
        "Exact value" to Text3("Valeur exacte","Valor exacto","Valor exato"),
        "Remainder zero: the fraction terminates exactly." to Text3("Reste nul : la fraction se termine exactement.","Resto cero: la fracción termina exactamente.","Resto zero: a fração termina exatamente."),
        "Magnitude" to Text3("Valeur absolue","Magnitud","Magnitude"),
        "Invert every bit" to Text3("Inversez chaque bit","Invierte cada bit","Inverta cada bit"),
        "Add 1 at this width" to Text3("Ajoutez 1 sur cette largeur","Suma 1 con este ancho","Some 1 nesta largura"),
        "Unsigned" to Text3("Sans signe","Sin signo","Sem sinal"),
        "Two’s complement" to Text3("Complément à deux","Complemento a dos","Complemento de dois"),
        "One’s complement" to Text3("Complément à un","Complemento a uno","Complemento de um"),
        "Gray code" to Text3("Code Gray","Código Gray","Código Gray"),
        "Gray code = binary XOR (binary shifted right by one)." to Text3("Code Gray = binaire XOR (binaire décalé d’un bit vers la droite).","Código Gray = binario XOR (binario desplazado un bit a la derecha).","Código Gray = binário XOR (binário deslocado um bit à direita)."),
        "BCD here represents non-negative decimal digits only; a negative value has no BCD word in this model." to Text3("Ici, le BCD représente uniquement des chiffres décimaux non négatifs ; ce modèle ne définit pas de mot BCD pour une valeur négative.","Aquí, BCD solo representa dígitos decimales no negativos; este modelo no define una palabra BCD para valores negativos.","Aqui, o BCD representa apenas dígitos decimais não negativos; este modelo não define uma palavra BCD para valores negativos."),
        "BCD gives EACH decimal digit its own 4-bit nibble; it is not ordinary binary." to Text3("En BCD, CHAQUE chiffre décimal possède son propre quartet de 4 bits ; ce n’est pas du binaire ordinaire.","En BCD, CADA dígito decimal tiene su propio grupo de 4 bits; no es binario común.","Em BCD, CADA dígito decimal tem seu próprio grupo de 4 bits; não é binário comum."),
        "Short words are ZERO-padded on the left, not sign-extended. Choose the original width when reading a negative word." to Text3("Les mots courts sont complétés par des ZÉROS à gauche, sans extension de signe. Pour lire un mot négatif, choisissez sa largeur d’origine.","Las palabras cortas se rellenan con CEROS a la izquierda, no se extienden con el signo. Al leer una palabra negativa, elige su ancho original.","Palavras curtas recebem ZEROS à esquerda, sem extensão de sinal. Ao ler uma palavra negativa, escolha a largura original."),
        "Decode Gray with cumulative XOR from the most significant bit. Gray is a code, not positional binary." to Text3("Décodez Gray avec un XOR cumulatif à partir du bit de poids fort. Gray est un code, pas un binaire positionnel.","Decodifica Gray con XOR acumulativo desde el bit más significativo. Gray es un código, no un sistema binario posicional.","Decodifique Gray com XOR acumulado a partir do bit mais significativo. Gray é um código, não binário posicional."),
        "Exponent field is all 0s and the fraction is all 0s: this is the special zero pattern." to Text3("Le champ exposant et la fraction sont entièrement à 0 : c’est le motif spécial du zéro.","El campo de exponente y la fracción son todo 0: es el patrón especial del cero.","O campo do expoente e a fração são todos 0: este é o padrão especial de zero."),
        "Exponent all 0s with a nonzero fraction means subnormal: no hidden leading 1." to Text3("Un exposant entièrement à 0 avec une fraction non nulle indique un nombre subnormal : il n’y a pas de 1 initial implicite.","Un exponente todo 0 con una fracción distinta de cero indica un subnormal: no hay un 1 inicial oculto.","Expoente todo em 0 com fração não nula indica subnormal: não há 1 inicial oculto."),
        "Exponent all 1s with zero fraction is the infinity pattern (overflow, or division like 1.0/0.0)." to Text3("Un exposant entièrement à 1 avec une fraction nulle représente l’infini (débordement ou division telle que 1.0/0.0).","Un exponente todo 1 con fracción cero representa infinito (desbordamiento o una división como 1.0/0.0).","Expoente todo em 1 com fração zero representa infinito (overflow ou divisão como 1.0/0.0)."),
        "Exponent all 1s with a nonzero fraction is NaN: the result of 0.0/0.0 or ∞−∞." to Text3("Un exposant entièrement à 1 avec une fraction non nulle représente NaN, résultat de 0.0/0.0 ou ∞−∞.","Un exponente todo 1 con fracción no nula es NaN: resultado de 0.0/0.0 o ∞−∞.","Expoente todo em 1 com fração não nula é NaN: resultado de 0.0/0.0 ou ∞−∞."),
        "The magnitude overflows single precision, so it rounds to infinity." to Text3("La valeur dépasse la précision simple et est donc arrondie à l’infini.","La magnitud excede la precisión simple y se redondea a infinito.","A magnitude excede a precisão simples e é arredondada para infinito."),
        "Exactly representable at this width (within the parsed decimal)." to Text3("Représentable exactement sur cette largeur (dans la limite du décimal saisi).","Representable exactamente con este ancho (dentro del decimal introducido).","Representável exatamente nesta largura (dentro do decimal informado)."),
        "The nearest single-precision neighbor: " to Text3("Valeur simple précision la plus proche : ","Valor de precisión simple más cercano: ","Valor de precisão simples mais próximo: "),
        "NaN has many bit patterns; this is the canonical quiet NaN the platform produces." to Text3("NaN possède plusieurs motifs de bits ; voici le NaN silencieux canonique produit par la plateforme.","NaN tiene varios patrones de bits; este es el NaN silencioso canónico que produce la plataforma.","NaN tem vários padrões de bits; este é o NaN silencioso canônico produzido pela plataforma."),
        "Feedback loop detected — latches and flip-flops live in the Sequential tab." to Text3("Boucle de rétroaction détectée — les verrous et bascules se trouvent dans l’onglet Séquentiel.","Se detectó un bucle de realimentación; los latches y flip-flops están en la pestaña Secuencial.","Loop de realimentação detectado — latches e flip-flops ficam na aba Sequencial."),
        "Wire the probe first." to Text3("Reliez d’abord la sonde.","Conecta primero la sonda.","Conecte a sonda primeiro."),
        "Wire one signal into the probe (Y) to observe an output." to Text3("Reliez un signal à la sonde (Y) pour observer une sortie.","Conecta una señal a la sonda (Y) para observar una salida.","Conecte um sinal à sonda (Y) para observar uma saída."),
        "XOR from 4 NANDs" to Text3("XOR avec 4 NAND","XOR con 4 NAND","XOR com 4 NAND"),
        "Half adder (probe = Sum)" to Text3("Demi-additionneur (sonde = somme)","Semisumador (sonda = suma)","Meio somador (sonda = soma)"),
        "2:1 MUX from gates" to Text3("Multiplexeur 2:1 avec des portes","Multiplexor 2:1 con puertas","Multiplexador 2:1 com portas"),
        "Little aha moments." to Text3("Petites révélations.","Pequeños descubrimientos.","Pequenas descobertas."),
        "24 beginner cases. Try the groups, then reveal the worked solution." to Text3("24 cas pour débuter. Essayez les groupes, puis affichez la solution détaillée.","24 casos para principiantes. Prueba los grupos y revela la solución explicada.","24 casos para iniciantes. Experimente os grupos e revele a solução explicada."),
        "Use only base-" to Text3("Utilisez uniquement les chiffres de la base ","Usa solo dígitos de base ","Use apenas dígitos da base "),
        "Builder truth table" to Text3("Table de vérité du circuit","Tabla de verdad del constructor","Tabela-verdade do construtor")
    )

    fun translate(source:String,language:String):String? {
        val lang=language.lowercase()
        if(lang !in setOf("fr","es","pt"))return null
        fun tr(fr:String,es:String,pt:String)=when(lang) {"fr"->fr;"es"->es;else->pt}
        entries[source]?.let {return when(lang) {"fr"->it.fr;"es"->it.es;else->it.pt}}
        val numbered=Regex("^(\\d+)\\. (.+)$").matchEntire(source)
        if(numbered!=null){val body=translate(numbered.groupValues[2],lang)?:return null;return "${numbered.groupValues[1]}. $body"}
        val header=Regex("^(.+) \\(base (\\d+)\\) → (.+) \\(base (\\d+)\\)$").matchEntire(source)
        if(header!=null)return "${header.groupValues[1]} (base ${header.groupValues[2]}) → ${header.groupValues[3]} (base ${header.groupValues[4]})"
        val targetBase=Regex("^Target base (\\d+) · (.+)$").matchEntire(source)
        if(targetBase!=null){val name=CoreEngineTranslations.translate(targetBase.groupValues[2],lang)?:targetBase.groupValues[2];return when(lang) {"fr"->"Base cible ${targetBase.groupValues[1]} · $name";"es"->"Base de destino ${targetBase.groupValues[1]} · $name";else->"Base de destino ${targetBase.groupValues[1]} · $name"}}
        val repeatTail=Regex("^Repeating tail: (.+)$").matchEntire(source)
        if(repeatTail!=null)return "${tr("Partie périodique","Parte periódica","Parte periódica")}: ${repeatTail.groupValues[1]}"
        val exact=Regex("^Exact value: (.+); decimal: (.+)$").matchEntire(source)
        if(exact!=null)return "${tr("Valeur exacte","Valor exacto","Valor exato")}: ${exact.groupValues[1]}; ${tr("décimal","decimal","decimal")}: ${exact.groupValues[2]}"
        val integerDigits=Regex("^Integer digits: (.+)$").matchEntire(source)
        if(integerDigits!=null)return "${tr("Chiffres entiers","Dígitos enteros","Dígitos inteiros")}: ${integerDigits.groupValues[1]}"
        val remainderStep=Regex("^The remainder repeats: the cycle starts at fractional step (\\d+)\\. Put those digits in parentheses\\.$").matchEntire(source)
        if(remainderStep!=null)return when(lang) {"fr"->"Le reste se répète : le cycle commence à l’étape fractionnaire ${remainderStep.groupValues[1]}. Mettez ces chiffres entre parenthèses.";"es"->"El resto se repite: el ciclo empieza en el paso fraccionario ${remainderStep.groupValues[1]}. Escribe esos dígitos entre paréntesis.";else->"O resto se repete: o ciclo começa na etapa fracionária ${remainderStep.groupValues[1]}. Coloque esses dígitos entre parênteses."}
        val answer=Regex("^Answer: (.+) \\(base (\\d+)\\) — (approximate|exact)$").matchEntire(source)
        if(answer!=null)return when(lang) {"fr"->"Réponse : ${answer.groupValues[1]} (base ${answer.groupValues[2]}) — ${if(answer.groupValues[3]=="exact")"exacte"else"approximative"}";"es"->"Respuesta: ${answer.groupValues[1]} (base ${answer.groupValues[2]}) — ${if(answer.groupValues[3]=="exact")"exacta"else"aproximada"}";else->"Resposta: ${answer.groupValues[1]} (base ${answer.groupValues[2]}) — ${if(answer.groupValues[3]=="exact")"exata"else"aproximada"}"}
        val division=Regex("^(?:\\d+\\. )?(.+) ÷ (\\d+) = (.+), remainder (-?\\d+) \\((.)\\)$").matchEntire(source)
        if(division!=null){val s="${division.groupValues[1]} ÷ ${division.groupValues[2]} = ${division.groupValues[3]}";return "$s, ${tr("reste","resto","resto")} ${division.groupValues[4]} (${division.groupValues[5]})"}
        val sectionDivide=Regex("^((?:\\d+)\\. )?Divide the integer part by (\\d+)\\. Read remainders BOTTOM TO TOP\\.$").matchEntire(source)
        if(sectionDivide!=null)return "${sectionDivide.groupValues[1]}${tr("Divisez la partie entière par","Divide la parte entera entre","Divida a parte inteira pela")} ${sectionDivide.groupValues[2]}. ${tr("Lisez les restes DE BAS EN HAUT.","Lee los restos DE ABAJO ARRIBA.","Leia os restos DE BAIXO PARA CIMA.")}"
        val sectionMultiply=Regex("^((?:\\d+)\\. )?Multiply the fractional remainder by (\\d+)\\. Read digits TOP TO BOTTOM\\.$").matchEntire(source)
        if(sectionMultiply!=null)return "${sectionMultiply.groupValues[1]}${tr("Multipliez le reste fractionnaire par","Multiplica el resto fraccionario por","Multiplique o resto fracionário pela")} ${sectionMultiply.groupValues[2]}. ${tr("Lisez les chiffres DE HAUT EN BAS.","Lee los dígitos DE ARRIBA ABAJO.","Leia os dígitos DE CIMA PARA BAIXO.")}"
        val unsigned=Regex("^Unsigned: sum the enabled bit weights = (.+)\\.$").matchEntire(source)
        if(unsigned!=null)return "${tr("Sans signe : additionnez les poids des bits activés","Sin signo: suma los pesos de los bits activos","Sem sinal: some os pesos dos bits ativos")}: ${unsigned.groupValues[1]}."
        val unsignedValue=Regex("^Unsigned: (.+)$").matchEntire(source)
        if(unsignedValue!=null)return "${tr("Sans signe","Sin signo","Sem sinal")}: ${unsignedValue.groupValues[1]}"
        val magnitude=Regex("^Magnitude: (.+)$").matchEntire(source)
        if(magnitude!=null)return "${tr("Valeur absolue","Magnitud","Magnitude")}: ${magnitude.groupValues[1]}"
        val bitTransform=Regex("^(Invert every bit|Add 1 at this width): (.+)$").matchEntire(source)
        if(bitTransform!=null)return "${tr(if(bitTransform.groupValues[1].startsWith("Invert"))"Inversez chaque bit"else"Ajoutez 1 sur cette largeur",if(bitTransform.groupValues[1].startsWith("Invert"))"Invierte cada bit"else"Suma 1 con este ancho",if(bitTransform.groupValues[1].startsWith("Invert"))"Inverta cada bit"else"Some 1 nesta largura")}: ${bitTransform.groupValues[2]}"
        val arithmetic=Regex("^Equivalent arithmetic: 2\\^(\\d+) \\+ \\((.+)\\) = (.+)\\.$").matchEntire(source)
        if(arithmetic!=null)return "${tr("Calcul équivalent","Cálculo equivalente","Cálculo equivalente")}: 2^${arithmetic.groupValues[1]} + (${arithmetic.groupValues[2]}) = ${arithmetic.groupValues[3]}."
        val convertBinary=Regex("^Convert (.+) to binary and zero-pad on the left to (\\d+) bits\\.$").matchEntire(source)
        if(convertBinary!=null)return when(lang) {"fr"->"Convertissez ${convertBinary.groupValues[1]} en binaire et complétez à gauche avec des zéros jusqu’à ${convertBinary.groupValues[2]} bits.";"es"->"Convierte ${convertBinary.groupValues[1]} a binario y completa con ceros a la izquierda hasta ${convertBinary.groupValues[2]} bits.";else->"Converta ${convertBinary.groupValues[1]} para binário e complete com zeros à esquerda até ${convertBinary.groupValues[2]} bits."}
        val baseHeader=Regex("^(.+) \\(base (\\d+)\\) → (.+) \\(base (\\d+)\\)$").matchEntire(source)
        if(baseHeader!=null)return source
        val decoded=Regex("^Decoded · (.+)$").matchEntire(source)
        if(decoded!=null){val kind=translate(decoded.groupValues[1],lang)?:decoded.groupValues[1];return "${tr("Décodé","Decodificado","Decodificado")} · $kind"}
        val kind=when(source) {
            "Normal"->tr("Normal","Normal","Normal")
            "Subnormal (denormal)"->tr("Sous-normal (dénormalisé)","Subnormal (denormal)","Subnormal (denormalizado)")
            "NaN (Not a Number)"->"NaN (Not a Number)"
            "+Infinity"->tr("+Infini","+Infinito","+Infinito")
            "−Infinity"->tr("−Infini","−Infinito","−Infinito")
            "+0 (positive zero)"->tr("+0 (zéro positif)","+0 (cero positivo)","+0 (zero positivo)")
            "−0 (negative zero)"->tr("−0 (zéro négatif)","−0 (cero negativo)","−0 (zero negativo)")
            else->null
        }
        if(kind!=null)return kind
        val sign=Regex("^Sign bit is ([01]), so the value is (positive|negative) \\(or ([+−]0)\\)\\.$").matchEntire(source)
        if(sign!=null)return when(lang) {"fr"->"Le bit de signe vaut ${sign.groupValues[1]} : la valeur est ${if(sign.groupValues[2]=="positive")"positive"else"négative"} (ou ${sign.groupValues[3]}).";"es"->"El bit de signo es ${sign.groupValues[1]}, así que el valor es ${if(sign.groupValues[2]=="positive")"positivo"else"negativo"} (o ${sign.groupValues[3]}).";else->"O bit de sinal é ${sign.groupValues[1]}, então o valor é ${if(sign.groupValues[2]=="positive")"positivo"else"negativo"} (ou ${sign.groupValues[3]})."}
        val exponent=Regex("^Exponent field (.+)₂ = (\\d+); unbiased exponent = (\\d+) − (\\d+) = (-?\\d+)\\.$").matchEntire(source)
        if(exponent!=null)return when(lang) {"fr"->"Champ exposant ${exponent.groupValues[1]}₂ = ${exponent.groupValues[2]} ; exposant non biaisé = ${exponent.groupValues[3]} − ${exponent.groupValues[4]} = ${exponent.groupValues[5]}.";"es"->"Campo de exponente ${exponent.groupValues[1]}₂ = ${exponent.groupValues[2]}; exponente sin sesgo = ${exponent.groupValues[3]} − ${exponent.groupValues[4]} = ${exponent.groupValues[5]}.";else->"Campo do expoente ${exponent.groupValues[1]}₂ = ${exponent.groupValues[2]}; expoente sem viés = ${exponent.groupValues[3]} − ${exponent.groupValues[4]} = ${exponent.groupValues[5]}."}
        val graySteps=Regex("^Value = \\(−1\\)\\^(\\d+) × 0\\.(.+)₂ × 2\\^(-?\\d+)\\.$").matchEntire(source)
        if(graySteps!=null)return "${tr("Valeur","Valor","Valor")} = (−1)^${graySteps.groupValues[1]} × 0.${graySteps.groupValues[2]}₂ × 2^${graySteps.groupValues[3]}."
        val hidden=Regex("^Hidden leading 1: value = \\(−1\\)\\^(\\d+) × 1\\.(.+)₂ × 2\\^(-?\\d+)\\.$").matchEntire(source)
        if(hidden!=null)return "${tr("1 initial implicite","1 inicial oculto","1 inicial oculto")} : ${tr("valeur","valor","valor")} = (−1)^${hidden.groupValues[1]} × 1.${hidden.groupValues[2]}₂ × 2^${hidden.groupValues[3]}."
        val carry=Regex("^(?:\\d+\\. )?Column (\\d+): (.+) \\+ (.+) \\+ carry-in (\\d+) = (\\d+) → write (.), carry-out (\\d+)\\.$").matchEntire(source)
        if(carry!=null)return "${tr("Colonne","Columna","Coluna")} ${carry.groupValues[1]} : ${carry.groupValues[2]} + ${carry.groupValues[3]} + ${tr("retenue d’entrée","acarreo de entrada","carry de entrada")} ${carry.groupValues[4]} = ${carry.groupValues[5]} → ${tr("écrivez","escribe","escreva")} ${carry.groupValues[6]}, ${tr("retenue de sortie","acarreo de salida","carry de saída")} ${carry.groupValues[7]}."
        val borrow=Regex("^(?:\\d+\\. )?Column (\\d+): top (.+) minus borrow-in (\\d+) minus (.+?)(, borrowing (\\d+) from the left)? → write (.), borrow-out (\\d+)\\.$").matchEntire(source)
        if(borrow!=null)return "${tr("Colonne","Columna","Coluna")} ${borrow.groupValues[1]} : ${tr("chiffre supérieur","dígito superior","dígito superior")} ${borrow.groupValues[2]} − ${tr("emprunt entrant","préstamo entrante","empréstimo recebido")} ${borrow.groupValues[3]} − ${borrow.groupValues[4]}${if(borrow.groupValues[6].isNotEmpty())tr(", emprunt de ${borrow.groupValues[6]} à gauche",", tomando ${borrow.groupValues[6]} de la izquierda",", emprestando ${borrow.groupValues[6]} da esquerda")else""} → ${tr("écrivez","escribe","escreva")} ${borrow.groupValues[7]}, ${tr("emprunt sortant","préstamo saliente","empréstimo de saída")} ${borrow.groupValues[8]}."
        val carryFinal=Regex("^(?:\\d+\\. )?Final carry (\\d+) becomes the new leftmost digit\\.$").matchEntire(source)
        if(carryFinal!=null)return tr("La retenue finale ${carryFinal.groupValues[1]} devient le nouveau chiffre le plus à gauche.","El acarreo final ${carryFinal.groupValues[1]} se convierte en el nuevo dígito de la izquierda.","O carry final ${carryFinal.groupValues[1]} torna-se o novo dígito mais à esquerda.")
        val mathStep=Regex("^(?:\\d+\\. )?(Apply the shared sign|Take the sign of the larger magnitude) → (.+)\\.$").matchEntire(source)
        if(mathStep!=null)return "${tr(if(mathStep.groupValues[1].startsWith("Apply"))"Appliquez le signe commun"else"Prenez le signe de la plus grande valeur absolue",if(mathStep.groupValues[1].startsWith("Apply"))"Aplica el signo común"else"Usa el signo de la magnitud mayor",if(mathStep.groupValues[1].startsWith("Apply"))"Aplique o sinal comum"else"Use o sinal da maior magnitude")} → ${mathStep.groupValues[2]}."
        val equal=Regex("^(?:\\d+\\. )?Equal magnitudes cancel exactly → (.+)\\.$").matchEntire(source)
        if(equal!=null)return "${tr("Les valeurs absolues égales s’annulent exactement","Las magnitudes iguales se cancelan exactamente","Magnitudes iguais se cancelam exatamente")} → ${equal.groupValues[1]}."
        val subtraction=Regex("^(?:\\d+\\. )?Subtraction adds the negated value: (.+) − \\((.+)\\) = (.+) \\+ \\((.+)\\)\\.$").matchEntire(source)
        if(subtraction!=null)return "${tr("La soustraction ajoute la valeur opposée","La resta suma el valor opuesto","A subtração soma o valor oposto")} : ${subtraction.groupValues[1]} − (${subtraction.groupValues[2]}) = ${subtraction.groupValues[3]} + (${subtraction.groupValues[4]})."
        val signs=Regex("^(?:\\d+\\. )?Both magnitudes share a sign — add the columns right to left in base (\\d+)\\.$").matchEntire(source)
        if(signs!=null)return "${tr("Les valeurs absolues ont le même signe — additionnez les colonnes de droite à gauche en base","Las magnitudes tienen el mismo signo: suma las columnas de derecha a izquierda en base","As magnitudes têm o mesmo sinal — some as colunas da direita para a esquerda na base")} ${signs.groupValues[1]}."
        if(source.contains("Different signs — subtract the smaller magnitude from the larger one"))return tr("Signes différents — soustrayez la plus petite valeur absolue de la plus grande.","Signos distintos: resta la magnitud menor de la mayor.","Sinais diferentes — subtraia a menor magnitude da maior.")
        val zero=Regex("^(?:\\d+\\. )?Anything times zero is zero → (.+)\\.$").matchEntire(source)
        if(zero!=null)return "${tr("Tout nombre multiplié par zéro donne zéro","Todo número multiplicado por cero da cero","Qualquer número vezes zero é zero")} → ${zero.groupValues[1]}."
        if(source.contains("Multiply each digit of the second number by the whole first number"))return tr("Multipliez chaque chiffre du second nombre par le premier nombre entier, en décalant chaque ligne vers la gauche.","Multiplica cada dígito del segundo número por el primer número completo y desplaza cada fila a la izquierda.","Multiplique cada dígito do segundo número pelo primeiro número inteiro, deslocando cada linha para a esquerda.")
        val row=Regex("^(?:\\d+\\. )?Row (\\d+): (.+) × (.+), shifted (\\d+) places? → (.+)\\.$").matchEntire(source)
        if(row!=null)return "${tr("Ligne","Fila","Linha")} ${row.groupValues[1]} : ${row.groupValues[2]} × ${row.groupValues[3]}, ${tr("décalée de","desplazada","deslocada")} ${row.groupValues[4]} ${tr("position(s)","posición(es)","casa(s)")} → ${row.groupValues[5]}."
        val partial=Regex("^(?:\\d+\\. )?Add the (\\d+) partial products? → (.+)\\.$").matchEntire(source)
        if(partial!=null)return "${tr("Additionnez les","Suma los","Some os")} ${partial.groupValues[1]} ${tr("produits partiels","productos parciales","produtos parciais")} → ${partial.groupValues[2]}."
        val productSign=Regex("^(?:\\d+\\. )?(Different|Same) signs → the product is (negative|positive): (.+)\\.$").matchEntire(source)
        if(productSign!=null)return "${tr(if(productSign.groupValues[1]=="Different")"Signes différents"else"Même signe",if(productSign.groupValues[1]=="Different")"Signos distintos"else"Mismo signo",if(productSign.groupValues[1]=="Different")"Sinais diferentes"else"Mesmo sinal")} → ${tr("le produit est","el producto es","o produto é")} ${tr(if(productSign.groupValues[2]=="negative")"négatif"else"positif",if(productSign.groupValues[2]=="negative")"negativo"else"positivo",if(productSign.groupValues[2]=="negative")"negativo"else"positivo")} : ${productSign.groupValues[3]}."
        val quotient=Regex("^(?:\\d+\\. )?Quotient (.+) remainder (.+)\\. Check: divisor × quotient \\+ remainder = dividend\\.$").matchEntire(source)
        if(quotient!=null)return when(lang) {"fr"->"Quotient ${quotient.groupValues[1]}, reste ${quotient.groupValues[2]}. Vérification : diviseur × quotient + reste = dividende.";"es"->"Cociente ${quotient.groupValues[1]}, resto ${quotient.groupValues[2]}. Comprobación: divisor × cociente + resto = dividendo.";else->"Quociente ${quotient.groupValues[1]}, resto ${quotient.groupValues[2]}. Verificação: divisor × quociente + resto = dividendo."}
        val nearest=Regex("^Nearest single-precision neighbor: (.+)\\. The missing tail is rounding error\\.$").matchEntire(source)
        if(nearest!=null)return when(lang) {"fr"->"Valeur simple précision la plus proche : ${nearest.groupValues[1]}. L’écart restant est une erreur d’arrondi.";"es"->"Valor de precisión simple más cercano: ${nearest.groupValues[1]}. La diferencia restante es error de redondeo.";else->"Valor de precisão simples mais próximo: ${nearest.groupValues[1]}. A diferença restante é erro de arredondamento."}
        val parsed=Regex("^Parsed “(.+)” as (.+)\\.$").matchEntire(source)
        if(parsed!=null)return when(lang) {"fr"->"« ${parsed.groupValues[1]} » interprété comme ${parsed.groupValues[2]}.";"es"->"«${parsed.groupValues[1]}» se interpretó como ${parsed.groupValues[2]}.";else->"“${parsed.groupValues[1]}” interpretado como ${parsed.groupValues[2]}."}
        if(source=="Exponent field is all 0s and the fraction is all 0s: this is the special zero pattern.")return tr("Le champ exposant et la fraction sont entièrement à 0 : c’est le motif spécial du zéro.","El campo de exponente y la fracción son todo 0: es el patrón especial del cero.","O campo do expoente e a fração são todos 0: este é o padrão especial de zero.")
        if(source=="Exponent all 0s with a nonzero fraction means subnormal: no hidden leading 1.")return tr("Un exposant entièrement à 0 avec une fraction non nulle indique un nombre subnormal : il n’y a pas de 1 initial implicite.","Un exponente todo 0 con una fracción distinta de cero indica un subnormal: no hay un 1 inicial oculto.","Expoente todo em 0 com fração não nula indica subnormal: não há 1 inicial oculto.")
        if(source=="Exponent all 1s with zero fraction is the infinity pattern (overflow, or division like 1.0/0.0).")return tr("Un exposant entièrement à 1 avec une fraction nulle représente l’infini (débordement ou division telle que 1.0/0.0).","Un exponente todo 1 con fracción cero representa infinito (desbordamiento o una división como 1.0/0.0).","Expoente todo em 1 com fração zero representa infinito (overflow ou divisão como 1.0/0.0).")
        if(source=="Exponent all 1s with a nonzero fraction is NaN: the result of 0.0/0.0 or ∞−∞.")return tr("Un exposant entièrement à 1 avec une fraction non nulle représente NaN, résultat de 0.0/0.0 ou ∞−∞.","Un exponente todo 1 con fracción no nula es NaN: resultado de 0.0/0.0 o ∞−∞.","Expoente todo em 1 com fração não nula é NaN: resultado de 0.0/0.0 ou ∞−∞.")

        val decimalRange=Regex("^Range: (-?\\d+) to (-?\\d+)$").matchEntire(source)
        if(decimalRange!=null)return when(lang) {"fr"->"Plage : ${decimalRange.groupValues[1]} à ${decimalRange.groupValues[2]}";"es"->"Rango: ${decimalRange.groupValues[1]} a ${decimalRange.groupValues[2]}";else->"Faixa: ${decimalRange.groupValues[1]} a ${decimalRange.groupValues[2]}"}
        val wordLine=Regex("^(\\d+)-bit representations$").matchEntire(source)
        if(wordLine!=null)return when(lang) {"fr"->"Représentations sur ${wordLine.groupValues[1]} bits";"es"->"Representaciones de ${wordLine.groupValues[1]} bits";else->"Representações de ${wordLine.groupValues[1]} bits"}
        val inputLine=Regex("^Input: (.+) \\((decimal|bits|gray)\\)$").matchEntire(source)
        if(inputLine!=null)return when(lang) {"fr"->"Entrée : ${inputLine.groupValues[1]} (${when(inputLine.groupValues[2]){"decimal"->"décimal";"bits"->"bits";else->"Gray"}})";"es"->"Entrada: ${inputLine.groupValues[1]} (${when(inputLine.groupValues[2]){"decimal"->"decimal";"bits"->"bits";else->"Gray"}})";else->"Entrada: ${inputLine.groupValues[1]} (${when(inputLine.groupValues[2]){"decimal"->"decimal";"bits"->"bits";else->"Gray"}})"}
        val floatHeader=Regex("^(\\d+)-bit float$").matchEntire(source)
        if(floatHeader!=null)return "IEEE-754 ${floatHeader.groupValues[1]} bits"
        val decimalLine=Regex("^Decimal: (.+)$").matchEntire(source)
        if(decimalLine!=null)return "${tr("Décimal","Decimal","Decimal")}: ${decimalLine.groupValues[1]}"
        val valueLine=Regex("^Word: (.+)$").matchEntire(source)
        if(valueLine!=null)return "${tr("Mot","Palabra","Palavra")}: ${valueLine.groupValues[1]}"
        val hexLine=Regex("^Hex: (.+)$").matchEntire(source)
        if(hexLine!=null)return "Hex : ${hexLine.groupValues[1]}"
        val bitsLine=Regex("^Bits: (.+)$").matchEntire(source)
        if(bitsLine!=null)return "Bits : ${bitsLine.groupValues[1]}"
        if(source=="not defined")return tr("non défini","no definido","não definido")
        if(source=="Not defined for negative values")return tr("Non défini pour les valeurs négatives","No definido para valores negativos","Não definido para valores negativos")
        val integerShortcut=Regex("^Integer ([0-9A-Z]+) → ([0-9A-Z]+)$").matchEntire(source)
        if(integerShortcut!=null)return "${tr("Entier","Entero","Inteiro")} ${integerShortcut.groupValues[1]} → ${integerShortcut.groupValues[2]}"
        val fractionShortcut=Regex("^Fraction ([0-9A-Z]+) → ([0-9A-Z]+)$").matchEntire(source)
        if(fractionShortcut!=null)return "${tr("Fraction","Fracción","Fração")} ${fractionShortcut.groupValues[1]} → ${fractionShortcut.groupValues[2]}"
        val binaryShortcut=Regex("^Binary shortcut: each base-(\\d+) digit represents exactly (\\d+) bits\\. Keep the point in place\\.$").matchEntire(source)
        if(binaryShortcut!=null)return when(lang) {"fr"->"Astuce binaire : chaque chiffre en base ${binaryShortcut.groupValues[1]} représente exactement ${binaryShortcut.groupValues[2]} bits. Gardez le point à la même position.";"es"->"Atajo binario: cada dígito en base ${binaryShortcut.groupValues[1]} representa exactamente ${binaryShortcut.groupValues[2]} bits. Mantén el punto en su lugar.";else->"Atalho binário: cada dígito na base ${binaryShortcut.groupValues[1]} representa exatamente ${binaryShortcut.groupValues[2]} bits. Mantenha o ponto na mesma posição."}
        val divideQuotient=Regex("^Divide each quotient by (\\d+)\\. Read the remainders BOTTOM TO TOP\\.$").matchEntire(source)
        if(divideQuotient!=null)return when(lang) {"fr"->"Divisez chaque quotient par ${divideQuotient.groupValues[1]}. Lisez les restes DE BAS EN HAUT.";"es"->"Divide cada cociente entre ${divideQuotient.groupValues[1]}. Lee los restos DE ABAJO ARRIBA.";else->"Divida cada quociente por ${divideQuotient.groupValues[1]}. Leia os restos DE BAIXO PARA CIMA."}
        val stop=Regex("^Stopped at 24 places before zero or a detected cycle\\. The ellipsis marks a truncated approximation\\.$").matchEntire(source)
        if(stop!=null)return tr("Arrêt après 24 chiffres avant d’atteindre zéro ou un cycle détecté. Les points de suspension signalent une approximation tronquée.","Se detuvo tras 24 posiciones antes de llegar a cero o detectar un ciclo. Los puntos suspensivos indican una aproximación truncada.","Parou após 24 casas antes de chegar a zero ou detectar um ciclo. As reticências indicam uma aproximação truncada.")
        val cycleStep=Regex("^The remainder repeats\\. The cycle starts at fractional step (\\d+); put those digits in parentheses\\.$").matchEntire(source)
        if(cycleStep!=null)return when(lang) {"fr"->"Le reste se répète. Le cycle commence à l’étape fractionnaire ${cycleStep.groupValues[1]} ; placez ces chiffres entre parenthèses.";"es"->"El resto se repite. El ciclo empieza en el paso fraccionario ${cycleStep.groupValues[1]}; escribe esos dígitos entre paréntesis.";else->"O resto se repete. O ciclo começa na etapa fracionária ${cycleStep.groupValues[1]}; coloque esses dígitos entre parênteses."}
        val twos=Regex("^Two’s complement: MSB is (0|1): (.+?)(?: − 2\\^(\\d+) = (.+))?\\.$").matchEntire(source)
        if(twos!=null)return when(lang) {
            "fr"->if(twos.groupValues[1]=="1")"Complément à deux : MSB=1 : ${twos.groupValues[2]} − 2^${twos.groupValues[3]} = ${twos.groupValues[4]}."else"Complément à deux : MSB=0 : ${twos.groupValues[2]}."
            "es"->if(twos.groupValues[1]=="1")"Complemento a dos: MSB=1: ${twos.groupValues[2]} − 2^${twos.groupValues[3]} = ${twos.groupValues[4]}."else"Complemento a dos: MSB=0: ${twos.groupValues[2]}."
            else->if(twos.groupValues[1]=="1")"Complemento de dois: MSB=1: ${twos.groupValues[2]} − 2^${twos.groupValues[3]} = ${twos.groupValues[4]}."else"Complemento de dois: MSB=0: ${twos.groupValues[2]}."
        }
        val ones=Regex("^One’s complement: invert every bit: (.+) → (.+), which reads as (.+) in one’s complement\\.( One’s complement has two zeroes: \\+0 and −0\\.)?$").matchEntire(source)
        if(ones!=null)return when(lang) {
            "fr"->"Complément à un : inversion de chaque bit : ${ones.groupValues[1]} → ${ones.groupValues[2]}, soit ${ones.groupValues[3]} en complément à un.${if(ones.groupValues[4].isNotEmpty())" Le complément à un possède deux zéros : +0 et −0."else""}"
            "es"->"Complemento a uno: invierte cada bit: ${ones.groupValues[1]} → ${ones.groupValues[2]}, que representa ${ones.groupValues[3]} en complemento a uno.${if(ones.groupValues[4].isNotEmpty())" El complemento a uno tiene dos ceros: +0 y −0."else""}"
            else->"Complemento de um: inverta cada bit: ${ones.groupValues[1]} → ${ones.groupValues[2]}, que representa ${ones.groupValues[3]} em complemento de um.${if(ones.groupValues[4].isNotEmpty())" O complemento de um tem dois zeros: +0 e −0."else""}"
        }
        val twosSummary=Regex("^Two’s complement: (.+)$").matchEntire(source)
        if(twosSummary!=null)return "${tr("Complément à deux","Complemento a dos","Complemento de dois")}: ${twosSummary.groupValues[1]}"
        val onesSummary=Regex("^One’s complement: (.+)$").matchEntire(source)
        if(onesSummary!=null)return "${tr("Complément à un","Complemento a uno","Complemento de um")}: ${onesSummary.groupValues[1]}"
        val graySummary=Regex("^Gray: (.+)$").matchEntire(source)
        if(graySummary!=null)return "${tr("Code Gray","Código Gray","Código Gray")}: ${graySummary.groupValues[1]}"
        val bcdSummary=Regex("^BCD: (.+)$").matchEntire(source)
        if(bcdSummary!=null)return "BCD : ${if(bcdSummary.groupValues[1]=="not defined")tr("non défini","no definido","não definido")else bcdSummary.groupValues[1]}"
        val kindValue=Regex("^(Normal|Subnormal \\(denormal\\)|NaN \\(Not a Number\\)|[+−]Infinity|[+−]0 \\((?:positive|negative) zero\\)) = (.+)$").matchEntire(source)
        if(kindValue!=null){val translatedKind=translate(kindValue.groupValues[1],lang)?:kindValue.groupValues[1];return "$translatedKind = ${kindValue.groupValues[2]}"}
        val floatFields=Regex("^SIGN ([01])  ·  EXPONENT ([01]+)₂ = (\\d+) \\(bias (\\d+)(, true (-?\\d+))?\\)  ·  FRACTION (\\d+) bits$").matchEntire(source)
        if(floatFields!=null)return when(lang) {"fr"->"SIGNE ${floatFields.groupValues[1]}  ·  EXPOSANT ${floatFields.groupValues[2]}₂ = ${floatFields.groupValues[3]} (biais ${floatFields.groupValues[4]}${if(floatFields.groupValues[5].isNotEmpty())", réel ${floatFields.groupValues[6]}"else""})  ·  FRACTION ${floatFields.groupValues[7]} bits";"es"->"SIGNO ${floatFields.groupValues[1]}  ·  EXPONENTE ${floatFields.groupValues[2]}₂ = ${floatFields.groupValues[3]} (sesgo ${floatFields.groupValues[4]}${if(floatFields.groupValues[5].isNotEmpty())", real ${floatFields.groupValues[6]}"else""})  ·  FRACCIÓN ${floatFields.groupValues[7]} bits";else->"SINAL ${floatFields.groupValues[1]}  ·  EXPOENTE ${floatFields.groupValues[2]}₂ = ${floatFields.groupValues[3]} (viés ${floatFields.groupValues[4]}${if(floatFields.groupValues[5].isNotEmpty())", real ${floatFields.groupValues[6]}"else""})  ·  FRAÇÃO ${floatFields.groupValues[7]} bits"}
        val longDivision=Regex("^(?:\\d+\\. )?Long division on magnitudes: bring digits down left to right; each trial digit is 0–([0-9A-Z])\\.$").matchEntire(source)
        if(longDivision!=null)return when(lang) {"fr"->"Division longue des valeurs absolues : abaissez les chiffres de gauche à droite ; chaque chiffre d’essai va de 0 à ${longDivision.groupValues[1]}.";"es"->"División larga de magnitudes: baja los dígitos de izquierda a derecha; cada dígito de prueba va de 0 a ${longDivision.groupValues[1]}.";else->"Divisão longa das magnitudes: baixe os dígitos da esquerda para a direita; cada dígito de tentativa vai de 0 a ${longDivision.groupValues[1]}."}
        val bringDown=Regex("^(?:\\d+\\. )?Bring down (.+) → working value (.+); ÷ (.+) = (.+), remainder (.+)\\.$").matchEntire(source)
        if(bringDown!=null)return when(lang) {"fr"->"Abaissez ${bringDown.groupValues[1]} → valeur de travail ${bringDown.groupValues[2]} ; ÷ ${bringDown.groupValues[3]} = ${bringDown.groupValues[4]}, reste ${bringDown.groupValues[5]}.";"es"->"Baja ${bringDown.groupValues[1]} → valor de trabajo ${bringDown.groupValues[2]}; ÷ ${bringDown.groupValues[3]} = ${bringDown.groupValues[4]}, resto ${bringDown.groupValues[5]}.";else->"Baixe ${bringDown.groupValues[1]} → valor de trabalho ${bringDown.groupValues[2]}; ÷ ${bringDown.groupValues[3]} = ${bringDown.groupValues[4]}, resto ${bringDown.groupValues[5]}."}
        return null
    }
}
