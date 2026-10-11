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
        assertEquals("A permanece en 0 → conserva A′.", AppStrings.forLang("es").text("A stays 0 → A'."))
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
