package com.radix.app

import org.junit.Assert.assertEquals
import org.junit.Test

class LocaleTextTest {
    @Test fun systemLanguagePrefersFirstSupportedDeviceLocaleAndFallsBackToEnglish() {
        assertEquals("fr", AppStrings.systemLanguage(listOf("fr-FR", "en-US")))
        assertEquals("es", AppStrings.systemLanguage(listOf("de-DE", "es-MX")))
        assertEquals("pt", AppStrings.systemLanguage(listOf("pt_BR", "en")))
        assertEquals("en", AppStrings.systemLanguage(listOf("de-DE", "ja-JP")))
    }

    @Test fun systemLanguageCatalogTranslatesScreensAndKeepsEnglishFallback() {
        assertEquals("Write logic.", AppStrings.forLang("en").text("Write logic."))
        assertEquals("Écrivez la logique.", AppStrings.forLang("fr").text("Write logic."))
        assertEquals("Escribe lógica.", AppStrings.forLang("es").text("Write logic."))
        assertEquals("Escreva lógica.", AppStrings.forLang("pt").text("Write logic."))
        assertEquals("Write logic.", AppStrings.forLang("de").text("Write logic."))
        assertEquals("Écrivez la logique.", AppStrings.forLang("fr-CA").text("Write logic."))
        assertEquals("¿Borrar el historial de conversiones?", AppStrings.forLang("es").text("Clear conversion history?"))
        assertEquals("Une table pour les nombres de 0 à 15. Lisez une ligne pour convertir instantanément.", AppStrings.forLang("fr").text("One table for the numbers 0–15. Read a row across to convert instantly."))
        assertEquals("Salve o estado.", AppStrings.forLang("pt").text("Save the state."))
        assertEquals("Escreva lógica e comprove", AppStrings.forLang("pt").text("Write logic, then prove it"))
        assertEquals("Intermedio · minimización booleana", AppStrings.forLang("es").text("Intermediate · Boolean minimization"))
        assertEquals("Avancé · logique séquentielle", AppStrings.forLang("fr").text("Advanced · sequential logic"))
        assertEquals("MÉMOIRE · AVANCÉ", AppStrings.forLang("fr").text("MEMORY · ADVANCED"))
        assertEquals("Au moins une", AppStrings.forLang("fr").text("At least one"))
        assertEquals("AND necesita que todas las entradas estén en 1.", AppStrings.forLang("es").text("AND needs all inputs high."))
        assertEquals("D deve permanecer estável durante o tempo de setup antes da borda de captura.", AppStrings.forLang("pt").text("D must hold still for the setup window before the capturing edge."))
        assertEquals("Une porte AND ne produit 1 que si toutes ses entrées valent 1.", AppStrings.forLang("fr").text("An AND gate only outputs 1 when every input is 1."))
        assertEquals("La salida no seleccionada es 0. No es un multiplexor con las etiquetas invertidas.", AppStrings.forLang("es").text("The unselected output is 0. It is not a multiplexer with reversed labels."))
        assertEquals("Na borda de subida, Q(próximo) = T ⊕ Q.", AppStrings.forLang("pt").text("Q(next) = T ⊕ Q on a rising edge."))
        assertEquals("Systèmes numériques et conversion de base", AppStrings.forLang("fr").text("Number systems & base conversion"))
        assertEquals("IDENTIDAD OR", AppStrings.forLang("es").text("IDENTITY OR"))
        assertEquals("Paires qui se chevauchent", AppStrings.forLang("fr").text("Overlapping pairs"))
        assertEquals("A permanece en 0 → conserva A'.", AppStrings.forLang("es").text("A stays 0 → A'."))
        assertEquals("Grupo válido de 2 células → A. As bordas opostas são adjacentes. Ele já é um grupo maximal.", AppStrings.forLang("pt").text("Valid 2-cell group → A. Opposite edges are adjacent. It is already a maximal group."))
        assertEquals("2 variáveis · SOP · condições indiferentes", AppStrings.forLang("pt").text("2 variables · SOP · don't-cares"))
        assertEquals("Niveau 3 Apprenti", AppStrings.forLang("fr").text("Level 3 Apprentice"))
        assertEquals("3/10 tarjetas dominadas", AppStrings.forLang("es").text("3/10 practice cards mastered"))
        assertEquals("Nível 2 · 100 XP · 4 jogadores", AppStrings.forLang("pt").text("Level 2 · 100 XP · 4 players"))
        assertEquals("G no es un dígito en base 16. Usa 0–9 y A–F.", AppStrings.forLang("es").text("G isn't a base-16 digit. Use 0–9 and A–F."))
        assertEquals("Décimal", AppStrings.forLang("fr").text("Decimal"))
        assertEquals("Unexpected END; check operators/parentheses · position 4", AppStrings.forLang("en").text("Unexpected END; check operators/parentheses · position 4"))
        assertEquals("Saisissez exactement 32 chiffres binaires.", AppStrings.forLang("fr").text("Type exactly 32 binary digits."))
        assertEquals("Colonne 0 : 1 + 1 + retenue d’entrée 0 = 2 → écrivez 0, retenue de sortie 1.", AppStrings.forLang("fr").text("Column 0: 1 + 1 + carry-in 0 = 2 → write 0, carry-out 1."))
        assertEquals("2. Divide la parte entera entre 16. Lee los restos DE ABAJO ARRIBA.", AppStrings.forLang("es").text("2. Divide the integer part by 16. Read remainders BOTTOM TO TOP."))
        assertEquals("Permitir respuesta háptica", AppStrings.forLang("es").text("Allow haptic feedback"))
        assertEquals(
            "1. L’ordre Gray est 00, 01, 11, 10.\n2. Regroupez par puissances de deux, jamais par 3 ou 6.\n3. Utilisez des rectangles, pas des paires uniquement diagonales.\n4. Les bords opposés sont voisins ; les quatre coins peuvent se rejoindre.\n5. Les groupes peuvent se chevaucher ; les X sont facultatifs.",
            AppStrings.forLang("fr").text("1. Gray order is 00, 01, 11, 10.\n2. Group powers of two, not 3 or 6.\n3. Use rectangles, not diagonal-only pairs.\n4. Opposite edges touch; four corners can wrap.\n5. Overlap is allowed; Xs are optional.")
        )
    }

    @Test fun localizedLandingAndMoreCopyCoversAllLocales() {
        assertEquals("STUDIO DE LOGIQUE NUMÉRIQUE",AppStrings.forLang("fr").text("THE DIGITAL LOGIC STUDIO"))
        assertEquals("Cada ley se verifica en directo con todas las combinaciones de entrada.",AppStrings.forLang("es").text("Each law is verified live against every input row."))
        assertEquals("LABORATÓRIO DE LEIS BOOLEANAS",AppStrings.forLang("pt").text("THE BOOLEAN LAWS LAB"))
        assertEquals("Glissez, reliez, simulez — sans retour matériel, avec des résultats toujours explicables.",AppStrings.forLang("fr").text("Drag, connect, simulate — feedback-free, always explainable."))
        assertEquals("Arrastra, conecta y simula: cada resultado se explica paso a paso.",AppStrings.forLang("es").text("Drag, connect, simulate — feedback-free, always explainable."))
        assertEquals("Arraste, conecte e simule — cada resultado pode ser explicado.",AppStrings.forLang("pt").text("Drag, connect, simulate — feedback-free, always explainable."))
        assertEquals("Parcours d’apprentissage et progression",AppStrings.forLang("fr").text("Learning path & progress"))
        assertEquals("Laboratorio de coma flotante IEEE-754",AppStrings.forLang("es").text("IEEE-754 float lab"))
        assertEquals("Construtor de circuitos",AppStrings.forLang("pt").text("Circuit builder"))
        assertEquals("4 de 108 verificações dominadas · 7 etapas, dos sistemas numéricos à lógica sequencial",AppStrings.forLang("pt").text("4 of 108 checks mastered · 7 steps from number systems to sequential logic"))
        assertEquals("7 conversiones locales recientes",AppStrings.forLang("es").text("7 recent local conversions"))
    }

    @Test fun localizedBasePickerAndGeneratedTeachingStepsAreComplete() {
        assertEquals("En attente d’un nombre",AppStrings.forLang("fr").text("Waiting for a number"))
        assertEquals("Una perspectiva distinta.",AppStrings.forLang("es").text("A different perspective."))
        assertEquals("Adicione qualquer base de 2 a 36 aos resultados.",AppStrings.forLang("pt").text("Add any base from 2 to 36 to your results."))
        assertEquals("Chaque bit est un interrupteur : 0 signifie désactivé et 1 activé. De droite à gauche, les positions valent 1, 2, 4, 8, 16, 32, 64 et 128. Additionnez les valeurs des positions activées.",AppStrings.forLang("fr").text("Each bit is a switch: 0 is off and 1 is on. From right to left, the positions are worth 1, 2, 4, 8, 16, 32, 64 and 128. Add the positions that are switched on."))
        assertEquals("Cada dígito hexadecimal representa cuatro bits. 1111 en binario es F en hexadecimal, así que 1111 1111 se convierte en FF. Los dígitos van del 0 al 9 y luego de la A a la F.",AppStrings.forLang("es").text("One hexadecimal digit represents four bits. 1111 in binary is F in hex, so 1111 1111 becomes FF. The digits are 0–9 followed by A–F."))
        assertEquals("Em qualquer base, cada posição vale uma potência dessa base. 10 em binário significa 2 em decimal. 10 em hexadecimal significa 16. Os mesmos dígitos, outra perspectiva.",AppStrings.forLang("pt").text("In any base, each position is worth a power of that base. 10 in binary means 2 in decimal. 10 in hexadecimal means 16. Same digits, different perspective."))
        assertEquals("Las posiciones se cuentan desde el punto: … 2, 1, 0, −1, −2 … . A–Z representan los valores 10–35. Trabaja primero con la magnitud.",AppStrings.forLang("es").text("Positions run from the point: … 2, 1, 0, −1, −2 … . A–Z represent values 10–35. Work on the magnitude first."))
        assertEquals("Conservez chaque chiffre entier et reportez le reste fractionnaire. Lisez les chiffres DE HAUT EN BAS — ne les inversez pas.",AppStrings.forLang("fr").text("Keep each integer digit; carry its fractional remainder. Read digits TOP TO BOTTOM—do not reverse them."))
        assertEquals("Alinhe os grupos ao redor do ponto. Complete a parte inteira à ESQUERDA e a fracionária à DIREITA.",AppStrings.forLang("pt").text("Align groups around the point. Integer padding goes LEFT; fractional padding goes RIGHT."))
        assertEquals("Truncado tras 24 decimales · aproximado",AppStrings.forLang("es").text("Truncated at 24 fractional places · approximate"))
    }

    @Test fun localizedCanvasShareAndVerifierCopyPreservesMeaning() {
        assertEquals("Étage D Q2",AppStrings.forLang("fr").text("D stage Q2"))
        assertEquals("puertas NOR acopladas en cruz",AppStrings.forLang("es").text("cross-coupled nor gates"))
        assertEquals("J=K: T=0 mantém, T=1 alterna",AppStrings.forLang("pt").text("J and K are tied together: T=0 holds, T=1 toggles"))
        assertEquals("Touchez pour vérifier en direct",AppStrings.forLang("fr").text("Tap to verify live"))
        assertEquals("✓ Se cumple en las 8 filas",AppStrings.forLang("es").text("✓ Holds in all 8 rows"))
        assertEquals("Resultado divergente — relate este erro",AppStrings.forLang("pt").text("Mismatch — report this bug"))
        assertEquals("TARJETA DE PUNTUACIÓN",AppStrings.forLang("es").text("SCORE CARD"))
        assertEquals("SOLUTION DE CARTE DE KARNAUGH · POS",AppStrings.forLang("fr").text("K-MAP SOLUTION · POS"))
        assertEquals("Índices 0–63 · A é o bit mais significativo · eixos em código Gray",AppStrings.forLang("pt").text("Indices 0–63 · A is MSB · Gray-coded axes"))
        assertEquals("Radix · aprendizaje sin conexión · sin anuncios ni cuentas",AppStrings.forLang("es").text("Radix · offline learning · no ads, no accounts"))
        assertEquals("350 / 400 XP para o nível 4",AppStrings.forLang("pt").text("350 / 400 XP to Level 4"))
        assertEquals("Compartilhar cartão",AppStrings.forLang("pt").text("Share card"))
        assertEquals("⚡ Transitorio: la sonda parpadea antes de estabilizarse.",AppStrings.forLang("es").text("⚡ Glitch: the probe flickers before settling."))
        assertEquals("Mudar para a visualização em blocos, ideal para celular",AppStrings.forLang("pt").text("Switch to phone-friendly block view"))
        assertEquals("Exploremos",AppStrings.forLang("es").text("Let's explore"))
        assertEquals("Q(actuel)",AppStrings.forLang("fr").text("Q(now)"))
        assertEquals("Q(siguiente)",AppStrings.forLang("es").text("Q(next)"))
        assertEquals("Estado",AppStrings.forLang("pt").text("Status"))
        assertEquals("Suivant →",AppStrings.forLang("fr").text("Next →"))
        assertEquals("Siguiente →",AppStrings.forLang("es").text("Next →"))
        assertEquals("Avançar →",AppStrings.forLang("pt").text("Next →"))
        assertEquals("El jugador se unió a la partida.",AppStrings.forLang("es").text("Player joined the table."))
        assertEquals("O cartão de pontuação foi compartilhado como imagem.",AppStrings.forLang("pt").text("Score card shared as an image."))
    }

    @Test fun dynamicLabelsPreserveValuesAcrossLocales() {
        assertEquals("Nombre à convertir en base 16", AppStrings.forLang("fr").text("Number to convert in base 16"))
        assertEquals("Cambiar el bit 3, peso 8", AppStrings.forLang("es").text("Toggle bit 3, weight 8"))
        assertEquals("5 variáveis", AppStrings.forLang("pt").text("5 vars"))
        assertEquals(
            "Selecione um grupo adjacente com tamanho potência de dois (1–8 células) e confira.",
            AppStrings.forLang("pt").text("Select an adjacent power-of-two group (1–8 cells), then check it.")
        )
        assertEquals("Supprimer « Session A » ?", AppStrings.forLang("fr").text("Delete Session A?"))
        assertEquals("Respuesta: FF", AppStrings.forLang("es").text("Answer: FF"))
        assertEquals("Copier cette page (16 lignes)", AppStrings.forLang("fr").text("Copy this page (16 rows)"))
        assertEquals("3/108 maîtrisées", AppStrings.forLang("fr").text("3/108 mastered"))
        assertEquals("🔥 Racha de 4 días", AppStrings.forLang("es").text("🔥 4-day streak"))
        assertEquals("Visita visual do app, etapa 2 de 5", AppStrings.forLang("pt").text("Visual app tour, step 2 of 5"))
    }

    @Test fun dynamicBuilderStudyAndAccessibilityLabelsAreLocalized() {
        assertEquals("Chemin critique : 10 ns à travers 2 portes. Les délais sont pédagogiques (NOT 2 ns, NAND/NOR 3 ns, AND/OR 4 ns, XOR/XNOR 6 ns), pas des mesures du silicium.",AppStrings.forLang("fr").text("Critical path: 10 ns through 2 gates. Delays are teaching values (NOT 2 ns, NAND/NOR 3 ns, AND/OR 4 ns, XOR/XNOR 6 ns), not silicon measurements."))
        assertEquals("Expresión interpretada: A + B. Los interruptores controlan tanto la salida como el diagrama de puertas.",AppStrings.forLang("es").text("Parsed as A + B. The live switches drive both the output and the gate-level diagram."))
        assertEquals("Contraexemplos (ordem das entradas A B): 01, 10",AppStrings.forLang("pt").text("Counterexamples (input order A B): 01, 10"))
        assertEquals("Le vérificateur compare les 8 combinaisons d’entrées, pas seulement celles des interrupteurs affichés.",AppStrings.forLang("fr").text("The verifier compares all 8 input combinations, not only the switches shown above."))
        assertEquals("Evalúa las 4 filas en orden binario. Las salidas 1 son minterminos; las salidas 0, maxterminos.",AppStrings.forLang("es").text("Evaluate all 4 rows in binary order. Rows with output 1 become minterms; rows with output 0 become maxterms."))
        assertEquals("La ligne mise en évidence suit les interrupteurs d’entrée. Les 8 lignes restent copiables.",AppStrings.forLang("fr").text("Highlighted row follows the live input switches. All 8 rows remain available for copying."))
        assertEquals("Entrées : A=0 · B=1. Les portes se mettent à jour avec les interrupteurs.",AppStrings.forLang("fr").text("Inputs: A=0 · B=1. Group gates update as the switches change."))
        assertEquals("No se requieren celdas con 0; la constante no necesita grupos.",AppStrings.forLang("es").text("No required 0-cells; the constant answer needs no groups."))
        assertEquals("Células 1, 3 · 2 células · as bordas se conectam · essencial",AppStrings.forLang("pt").text("Cells 1, 3 · 2 cells · wraps an edge · essential"))
        assertEquals("Indices 0–15. A est le bit de poids fort. Les doublons sont ignorés ; les plages ne sont pas prises en charge.",AppStrings.forLang("fr").text("Indices 0–15. A is the MSB. Duplicates are ignored; ranges are not supported."))
        assertEquals("1 obligatoires : 1, 3 · indifférences X : aucune",AppStrings.forLang("fr").text("1s: 1, 3 · Xs: none"))
        assertEquals("ABCD   índice   entrada   simplificada",AppStrings.forLang("es").text("ABCD   index   input   simplified"))
        assertEquals("Entrada A, 1. Toca para alternar.",AppStrings.forLang("es").text("Input A 1. Tap to toggle."))
        assertEquals("Entrada externa B, 0. Toque para alternar.",AppStrings.forLang("pt").text("External input B, 0. Tap to toggle."))
        assertEquals("Entrada 2 da porta AND: conectada",AppStrings.forLang("pt").text("AND input 2, wired"))
        assertEquals("mintermino m3, A=0, B=1, salida valor indiferente. Selecciona la celda del grupo.",AppStrings.forLang("es").text("Minterm m3, A=0, B=1, output don't-care. Select group cell"))
        assertEquals("Sonde de sortie, 1",AppStrings.forLang("fr").text("Output probe, 1"))
        assertEquals("Porte AND. Entrées [1, 0] ; sortie 0.",AppStrings.forLang("fr").text("AND gate. Inputs [1, 0]; output 0."))
        assertEquals("Bloque Semisumador. Salidas: Suma=1, Acarreo=0",AppStrings.forLang("es").text("Half adder block. Sum=1, Carry=0"))
        assertEquals("Bloc Demi-additionneur. Sorties : Somme=1, Retenue=0",AppStrings.forLang("fr").text("Half adder block. Sum=1, Carry=0"))
        assertEquals("Bloco Meio somador. Saídas: Soma=1, Transporte=0",AppStrings.forLang("pt").text("Half adder block. Sum=1, Carry=0"))
        assertEquals("Bloque Sumador completo. Salidas: Suma=1, Cout=0",AppStrings.forLang("es").text("Full adder block. Sum=1, Cout=0"))
        assertEquals("Somme",AppStrings.forLang("fr").text("Sum"))
        assertEquals("Préstamo",AppStrings.forLang("es").text("Borrow"))
        assertEquals("Par",AppStrings.forLang("pt").text("Even"))
        assertEquals("Transporte",AppStrings.forLang("pt").text("Carry"))
        assertEquals("3 variables",AppStrings.forLang("fr").text("3 variables"))
        assertEquals("3 variables",AppStrings.forLang("es").text("3 variables"))
        assertEquals("3 variáveis",AppStrings.forLang("pt").text("3 variables"))
        assertEquals("Somme = A ⊕ B",AppStrings.forLang("fr").text("Sum = A ⊕ B"))
        assertEquals("Y0 = D · mintermino(S) para la selección 00",AppStrings.forLang("es").text("Y0 = D · minterm(S) for select 00"))
        assertEquals("A, B, C e D são as entradas BCD com pesos 8-4-2-1.",AppStrings.forLang("pt").text("A B C D are 8-4-2-1 BCD inputs."))
        assertEquals("a…g son los segmentos activos en alto de una pantalla de cátodo común.",AppStrings.forLang("es").text("a…g are active-high segments for a common-cathode display."))
        assertEquals("Take a guided tour of the core tools. This built-in helper works offline. It is not a chat or network service. The app follows the system language when English, French, Spanish or Portuguese is supported; otherwise it uses English.",AppStrings.forLang("en").text("Take a guided tour of the core tools. This built-in helper works offline. It is not a chat or network service. The app follows the system language when English, French, Spanish or Portuguese is supported; otherwise it uses English."))
        assertEquals("Découvrez les outils essentiels avec ce guide intégré et disponible hors ligne. Ce n’est ni un chat ni un service réseau. L’application suit la langue du système si elle est en anglais, en français, en espagnol ou en portugais ; sinon, elle utilise l’anglais.",AppStrings.forLang("fr").text("Take a guided tour of the core tools. This built-in helper works offline. It is not a chat or network service. The app follows the system language when English, French, Spanish or Portuguese is supported; otherwise it uses English."))
        assertEquals("Recorre las herramientas principales con esta guía integrada y sin conexión. No es un chat ni un servicio de red. La aplicación sigue el idioma del sistema cuando es inglés, francés, español o portugués; de lo contrario, usa inglés.",AppStrings.forLang("es").text("Take a guided tour of the core tools. This built-in helper works offline. It is not a chat or network service. The app follows the system language when English, French, Spanish or Portuguese is supported; otherwise it uses English."))
        assertEquals("Explore as ferramentas principais com esta ajuda integrada e offline. Ela não é um chat nem um serviço de rede. O app segue o idioma do sistema quando ele é inglês, francês, espanhol ou português; caso contrário, usa inglês.",AppStrings.forLang("pt").text("Take a guided tour of the core tools. This built-in helper works offline. It is not a chat or network service. The app follows the system language when English, French, Spanish or Portuguese is supported; otherwise it uses English."))
        assertEquals("Esquema de Sumador completo a nivel de puertas. Los cruces solo se conectan en los puntos de unión de los pines.",AppStrings.forLang("es").text("Full adder gate-level schematic. Crossings connect only at pin junction dots."))
        assertEquals("Traza temporal de acciones discretas, no tiempo real. Los estados desconocidos se marcan con X.",AppStrings.forLang("es").text("Discrete action timing trace, not real time. Unknown states are marked X."))
        assertEquals("Hexadécimal : aucun résultat",AppStrings.forLang("fr").text("Hexadecimal: no result"))
        assertEquals("Supprimer la conversion enregistrée 1010",AppStrings.forLang("fr").text("Delete saved conversion 1010"))
        assertEquals("Copiar expressão simplificada",AppStrings.forLang("pt").text("Copy simplified expression"))
        assertEquals("Copiar el resultado rápido Binario",AppStrings.forLang("es").text("Copy Binary quick result"))
        assertEquals("half-adder · combinational · inputs 10",AppStrings.forLang("en").text("half-adder · combinational · inputs 10"))
        assertEquals("Demi-additionneur · combinatoire · entrées 00",AppStrings.forLang("fr").text("half-adder · combinational · inputs 00"))
        assertEquals("Área de trabalho · 3/12 portas",AppStrings.forLang("pt").text("Canvas · 3/12 gates"))
        assertEquals("Plus → Entraînement logique propose 23 questions en niveaux Facile, Intermédiaire et Avancé. Choisissez une réponse, vérifiez-la, demandez un indice ou affichez l’explication. Une bonne réponse valide la maîtrise ; révéler ne compte pas. Les séries célèbrent les jours d’entraînement sans pénaliser les pauses.",AppStrings.forLang("fr").text("More → Logic practice has 23 cards across Easy, Intermediate and Advanced. Choose an answer, check it, use a hint or reveal the explanation. Correct checks save mastery; revealing does not. Streaks celebrate practice days and never punish a missed day."))
        assertEquals("Más → Práctica lógica ofrece 23 preguntas de nivel Fácil, Intermedio y Avanzado. Elige una respuesta, compruébala, usa una pista o revela la explicación. Las respuestas correctas cuentan como dominadas; revelar no. Las rachas celebran los días de práctica sin penalizar descansos.",AppStrings.forLang("es").text("More → Logic practice has 23 cards across Easy, Intermediate and Advanced. Choose an answer, check it, use a hint or reveal the explanation. Correct checks save mastery; revealing does not. Streaks celebrate practice days and never punish a missed day."))
        assertEquals("Mais → Prática lógica tem 23 questões nos níveis Fácil, Intermediário e Avançado. Escolha uma resposta, confira-a, use uma dica ou revele a explicação. Respostas corretas registram domínio; revelar não. Sequências celebram os dias de prática sem punir pausas.",AppStrings.forLang("pt").text("More → Logic practice has 23 cards across Easy, Intermediate and Advanced. Choose an answer, check it, use a hint or reveal the explanation. Correct checks save mastery; revealing does not. Streaks celebrate practice days and never punish a missed day."))
        assertEquals("Actividad reciente · 7/100",AppStrings.forLang("es").text("Recent activity · 7/100"))
        assertEquals("En cours · Amina",AppStrings.forLang("fr").text("Now playing · Amina"))
        assertEquals("Terminez le câblage",AppStrings.forLang("fr").text("Finish the wiring"))
        assertEquals("Tabla de verdad de tu circuito",AppStrings.forLang("es").text("Your circuit's truth table"))
        assertEquals("12 FILAS · SE MUESTRAN 16 A LA VEZ",AppStrings.forLang("es").text("12 ROWS · SHOWING 16 AT A TIME"))
        assertEquals("AIDE VISUELLE · 2/5",AppStrings.forLang("fr").text("VISUAL HELPER · 2/5"))
        assertEquals("saída = 1",AppStrings.forLang("pt").text("out = 1"))
        assertEquals("Non équivalentes · 1 ligne de contre-exemple",AppStrings.forLang("fr").text("Not equivalent · 1 counterexample row"))
        assertEquals("No equivalentes · 3 filas de contraejemplo",AppStrings.forLang("es").text("Not equivalent · 3 counterexample rows"))
        assertEquals("Não equivalentes · 2 linhas de contraexemplo",AppStrings.forLang("pt").text("Not equivalent · 2 counterexample rows"))
        assertEquals("32 chiffres binaires",AppStrings.forLang("fr").text("32 binary digits"))
        assertEquals("16 dígitos hexadecimales",AppStrings.forLang("es").text("16 hex digits"))
        assertEquals("32 dígitos binários",AppStrings.forLang("pt").text("32 binary digits"))
        assertEquals("MOT BINAIRE SUR 8 BITS",AppStrings.forLang("fr").text("8-BIT BINARY WORD"))
        assertEquals("PALABRA BINARIA DE 16 BITS",AppStrings.forLang("es").text("16-BIT BINARY WORD"))
        assertEquals("PALAVRA BINÁRIA DE 32 BITS",AppStrings.forLang("pt").text("32-BIT BINARY WORD"))
        assertEquals("Chiffres de 0 à 1 · fractions acceptées",AppStrings.forLang("fr").text("Digits 0–1 · fractions welcome"))
        assertEquals("Dígitos 0–9, A–F · se aceptan fracciones",AppStrings.forLang("es").text("Digits 0–9, A–F · fractions welcome"))
        assertEquals("Dígitos de 0 a 9, A a Z · frações permitidas",AppStrings.forLang("pt").text("Digits 0–9, A–Z · fractions welcome"))
    }

    @Test fun builderLimitAndSwipeableCanvasCopyStayLocalized() {
        assertEquals("12 portes maximum ; supprimez-en une pour faire de la place.",AppStrings.forLang("fr").text("Maximum 12 gates; remove one to make room."))
        assertEquals("Máximo: 12 puertas; elimina una para dejar sitio.",AppStrings.forLang("es").text("Maximum 12 gates; remove one to make room."))
        assertEquals("Máximo de 12 portas; remova uma para liberar espaço.",AppStrings.forLang("pt").text("Maximum 12 gates; remove one to make room."))
        assertEquals("En portrait, faites défiler un canevas large ; passez en paysage pour voir et organiser davantage à la fois.",AppStrings.forLang("fr").landscapeBuilderHint)
        assertEquals("En vertical, desliza el lienzo ancho; gira a horizontal para ver y organizar más elementos a la vez.",AppStrings.forLang("es").landscapeBuilderHint)
        assertEquals("No modo retrato, deslize horizontalmente a área de trabalho; gire para paisagem para ver e organizar mais elementos de uma vez.",AppStrings.forLang("pt").landscapeBuilderHint)
    }

    @Test fun generatedValidationTeachingAndClipboardCopyAreLocalized() {
        assertEquals(
            "Fuera de rango: los valores sin signo de 8 bits van de 0 a 255. No se aplicó ningún desbordamiento circular.",
            AppStrings.forLang("es").text("Out of range: 8-bit unsigned values are 0 to 255. No wrapping was applied.")
        )
        assertEquals(
            "Saisissez exactement 8 chiffres hexadécimaux.",
            AppStrings.forLang("fr").text("Type exactly 8 hex digits.")
        )
        assertEquals(
            "Division longue des valeurs absolues : abaissez les chiffres de gauche à droite ; chaque chiffre d’essai va de 0 à F.",
            AppStrings.forLang("fr").text("Long division on magnitudes: bring digits down left to right; each trial digit is 0–F.")
        )
        assertEquals(
            "Baixe A(10) → valor de trabalho 13; ÷ 3 = 4, resto 1.",
            AppStrings.forLang("pt").text("Bring down A(10) → working value 13; ÷ 3 = 4, remainder 1.")
        )
        assertEquals(
            "SIGNE 1  ·  EXPOSANT 10000000₂ = 128 (biais 127, réel 1)  ·  FRACTION 23 bits",
            AppStrings.forLang("fr").text("SIGN 1  ·  EXPONENT 10000000₂ = 128 (bias 127, true 1)  ·  FRACTION 23 bits")
        )
        assertEquals(
            "Champ exposant 10000000₂ = 128 ; exposant non biaisé = 128 − 127 = 1.",
            AppStrings.forLang("fr").text("Exponent field 10000000₂ = 128; unbiased exponent = 128 − 127 = 1.")
        )
        assertEquals(
            "Complemento a dos: MSB=1: 255 − 2^8 = −1.",
            AppStrings.forLang("es").text("Two’s complement: MSB is 1: 255 − 2^8 = −1.")
        )
        assertEquals(
            "G1 : cases 1, 3 → AB (les bords opposés se rejoignent) · essentiel",
            AppStrings.forLang("fr").text("G1: cells 1, 3 → AB (opposite edges wrap) · essential")
        )
        assertEquals(
            "• La variable A change dans le groupe → éliminez-la.",
            AppStrings.forLang("fr").text("• A changes inside the group → remove it.")
        )
        assertEquals(
            "Table de vérité du circuit",
            AppStrings.forLang("fr").text("Builder truth table")
        )
        assertEquals("toutes les 4 lignes", AppStrings.forLang("fr").text("all 4 rows"))
        assertEquals(
            "Le champ « Identifiant de session » doit contenir du texte.",
            AppStrings.forLang("fr").text("Session ID must be text.")
        )
        assertEquals(
            "JSON de sauvegarde invalide à la position 6 : séquence d’échappement invalide.",
            AppStrings.forLang("fr").text("Invalid backup JSON: invalid escape at 6.")
        )
        assertEquals(
            "Não foi possível salvar o backup: Não foi possível abrir o arquivo de saída",
            AppStrings.forLang("pt").text("Could not save backup: Cannot open output file")
        )
        assertEquals(
            "✓ Válida: 2 conversiones · 1 sesiones · 5 tarjetas dominadas · mapa de Karnaugh de 3 variables (SOP).",
            AppStrings.forLang("es").text("✓ Valid: 2 conversions · 1 sessions · 5 mastered cards · 3-variable SOP K-map.")
        )
        assertEquals("Expression résolue sur les 8 lignes d’entrée.",AppStrings.forLang("fr").text("Expression solved across all 8 input rows."))
        assertEquals("Equivalencia comprobada en las 4 filas de entrada.",AppStrings.forLang("es").text("Equivalence checked across all 4 input rows."))
        assertEquals(
            "Corrija as listas antes de resolver: Use índices de células separados por vírgulas, não intervalos ou expressões.",
            AppStrings.forLang("pt").text("Fix the lists before solving: Use comma-separated cell indices, not ranges or expressions.")
        )
        assertEquals("• Trop de portes (maximum : 12).",AppStrings.forLang("fr").text("• Too many gates (maximum 12)."))
        assertEquals("Derivación copiada.",AppStrings.forLang("es").text("derivation copied."))
        assertEquals("Tabela-verdade copiada.",AppStrings.forLang("pt").text("Truth table copied."))
        assertEquals("Procedimiento del mapa de Karnaugh copiado.",AppStrings.forLang("es").text("K-map working copied."))
        assertEquals("SOP function opened in K-maps.",AppStrings.forLang("en").text("SOP function opened in K-maps."))
        assertEquals("Função POS aberta nos mapas de Karnaugh.",AppStrings.forLang("pt").text("POS function opened in K-maps."))
        assertEquals("Binaire copié.",AppStrings.forLang("fr").text("Binary copied."))
        assertEquals("Base 7 copiada.",AppStrings.forLang("es").text("Base 7 copied."))
        assertEquals("Porta XOR removida.",AppStrings.forLang("pt").text("XOR removed."))
        assertEquals("Se quitó la entrada del historial.",AppStrings.forLang("es").text("History entry removed."))
        assertEquals("Todas as leis booleanas foram copiadas.",AppStrings.forLang("pt").text("All Boolean laws copied."))
        assertEquals("Activa el modo de cableado y toca esta salida.",AppStrings.forLang("es").text("Turn wiring mode ON, then tap this output."))
    }
}
