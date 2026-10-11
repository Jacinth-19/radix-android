package com.radix.app

/** Long-form labels whose mathematical notation should remain identical in every locale. */
internal object UiTranslationsTechnical {
    private val fr = mapOf(
        "Binary gates take 2 inputs here; chain them for wider functions. NOT and BUFFER take 1." to "Ici, les portes binaires ont 2 entrées ; mettez-les en cascade pour des fonctions plus larges. NOT et BUFFER en ont 1.",
        "Complete the wiring to generate the table." to "Terminez le câblage pour générer la table.",
        "Includes saved conversions, circuit sessions, mastered practice cards, the current K-map/lab drafts and comfort preferences. Theme, reminder time and inbox stay on this device; the app follows your system language and does not include it in backups. Circuit traces and signing keys/passwords are never included. JSON is NOT encrypted; store it privately." to "Inclut les conversions enregistrées, les sessions de circuits, les questions maîtrisées, les brouillons de cartes/labos et les préférences de confort. Le thème, l’heure des rappels et la boîte de réception restent sur l’appareil ; la langue système n’est pas sauvegardée. Les chronologies et clés/mots de passe ne sont jamais inclus. Le JSON n’est PAS chiffré : conservez-le en lieu sûr.",
        "Why 0.1 + 0.2 ≠ 0.3: most decimal fractions repeat forever in binary, so the nearest float is stored, not the exact value." to "Pourquoi 0,1 + 0,2 ≠ 0,3 : la plupart des fractions décimales ont une période infinie en binaire ; le flottant le plus proche est stocké, pas la valeur exacte.",
        "Wire an external input first." to "Connectez d’abord une entrée externe.",
        "XP: Easy 10, Intermediate 20, Advanced 30 per mastered card, plus up to +14 for long streaks. Levels grow quadratically — early wins come fast." to "XP : 10 en Facile, 20 en Intermédiaire et 30 en Avancé par question maîtrisée, plus jusqu’à 14 pour les longues séries. Les niveaux progressent de façon quadratique : les premiers arrivent vite.",
        """1. Gray order is 00, 01, 11, 10.
2. Group powers of two, not 3 or 6.
3. Use rectangles, not diagonal-only pairs.
4. Opposite edges touch; four corners can wrap.
5. Overlap is allowed; Xs are optional.""" to """1. L’ordre Gray est 00, 01, 11, 10.
2. Regroupez par puissances de deux, jamais par 3 ou 6.
3. Utilisez des rectangles, pas des paires uniquement diagonales.
4. Les bords opposés sont voisins ; les quatre coins peuvent se rejoindre.
5. Les groupes peuvent se chevaucher ; les X sont facultatifs.""",
        "↶ Undo" to "↶ Annuler",
        "↷ Redo" to "↷ Rétablir",
        "Allow haptic feedback" to "Autoriser le retour haptique",
        "Arithmetic base from 2 to 36" to "Base arithmétique de 2 à 36",
        "A′B + AB′  ≡  A XOR B\n3 inputs → 8 rows (0–7)\n4 inputs → 16 rows (0–15)" to "A′B + AB′  ≡  A XOR B\n3 entrées → 8 lignes (0–7)\n4 entrées → 16 lignes (0–15)",
        "SOP: fixed 0 → A', fixed 1 → A.\nPOS: fixed 0 → A, fixed 1 → A'.\n+ is OR, adjacency is AND, ' is NOT.\nSwitching SOP/POS preserves the truth table." to "SOP : 0 fixe → A', 1 fixe → A.\nPOS : 0 fixe → A, 1 fixe → A'.\n+ signifie OU, juxtaposition ET, ' NON.\nPasser de SOP à POS conserve la table de vérité.",
        "Save keeps the minimized formula and form; Copy working includes the groups, rules and full table." to "Enregistrer conserve la formule minimale et sa forme ; Copier le calcul inclut les groupes, les règles et toute la table.",
        "Share solution image" to "Partager l’image de la solution",
        "Toggle wiring mode" to "Activer ou désactiver le mode câblage",
        "Use larger learning controls" to "Utiliser des commandes d’apprentissage plus grandes",
        "Use signed two’s-complement interpretation" to "Utiliser l’interprétation signée en complément à deux",
        "Copy working" to "Copier le calcul",
        "Skip" to "Passer",
        "Back" to "Retour",
        "13 ÷ 2 = 6, r1\n 6 ÷ 2 = 3, r0\n 3 ÷ 2 = 1, r1\n 1 ÷ 2 = 0, r1\n↑ read upward → 1101₂" to "13 ÷ 2 = 6, r1\n 6 ÷ 2 = 3, r0\n 3 ÷ 2 = 1, r1\n 1 ÷ 2 = 0, r1\n↑ lire de bas en haut → 1101₂",
        "−13 at 8 bits\n00001101 → invert → +1\n11110011 = unsigned 243" to "−13 sur 8 bits\n00001101 → inverser → +1\n11110011 = 243 non signé"
    )
    private val es = mapOf(
        "Binary gates take 2 inputs here; chain them for wider functions. NOT and BUFFER take 1." to "Aquí las puertas binarias tienen 2 entradas; encadénalas para funciones más amplias. NOT y BUFFER tienen 1.",
        "Complete the wiring to generate the table." to "Completa el cableado para generar la tabla.",
        "Includes saved conversions, circuit sessions, mastered practice cards, the current K-map/lab drafts and comfort preferences. Theme, reminder time and inbox stay on this device; the app follows your system language and does not include it in backups. Circuit traces and signing keys/passwords are never included. JSON is NOT encrypted; store it privately." to "Incluye conversiones guardadas, sesiones de circuitos, preguntas dominadas, borradores de mapas/laboratorio y preferencias. El tema, la hora de recordatorio y la bandeja permanecen en el dispositivo; el idioma del sistema no se incluye en la copia. Nunca se incluyen trazas ni claves/contraseñas. El JSON NO está cifrado: guárdalo en privado.",
        "Why 0.1 + 0.2 ≠ 0.3: most decimal fractions repeat forever in binary, so the nearest float is stored, not the exact value." to "Por qué 0,1 + 0,2 ≠ 0,3: muchas fracciones decimales se repiten infinitamente en binario, por lo que se guarda el valor flotante más cercano, no el exacto.",
        "Wire an external input first." to "Conecta primero una entrada externa.",
        "XP: Easy 10, Intermediate 20, Advanced 30 per mastered card, plus up to +14 for long streaks. Levels grow quadratically — early wins come fast." to "XP: 10 en Fácil, 20 en Intermedio y 30 en Avanzado por pregunta dominada, más hasta 14 por rachas largas. Los niveles crecen de forma cuadrática: los primeros llegan pronto.",
        """1. Gray order is 00, 01, 11, 10.
2. Group powers of two, not 3 or 6.
3. Use rectangles, not diagonal-only pairs.
4. Opposite edges touch; four corners can wrap.
5. Overlap is allowed; Xs are optional.""" to """1. El orden Gray es 00, 01, 11, 10.
2. Agrupa potencias de dos, no grupos de 3 o 6.
3. Usa rectángulos, no pares solo diagonales.
4. Los bordes opuestos son adyacentes; las cuatro esquinas pueden agruparse.
5. Se permite solapar grupos; las X son opcionales.""",
        "Allow haptic feedback" to "Permitir respuesta háptica",
        "Arithmetic base from 2 to 36" to "Base aritmética de 2 a 36",
        "A′B + AB′  ≡  A XOR B\n3 inputs → 8 rows (0–7)\n4 inputs → 16 rows (0–15)" to "A′B + AB′  ≡  A XOR B\n3 entradas → 8 filas (0–7)\n4 entradas → 16 filas (0–15)",
        "SOP: fixed 0 → A', fixed 1 → A.\nPOS: fixed 0 → A, fixed 1 → A'.\n+ is OR, adjacency is AND, ' is NOT.\nSwitching SOP/POS preserves the truth table." to "SOP: 0 fijo → A', 1 fijo → A.\nPOS: 0 fijo → A, 1 fijo → A'.\n+ es OR, la yuxtaposición es AND y ' es NOT.\nCambiar entre SOP/POS conserva la tabla de verdad.",
        "Save keeps the minimized formula and form; Copy working includes the groups, rules and full table." to "Guardar conserva la fórmula mínima y su forma; Copiar procedimiento incluye los grupos, las reglas y toda la tabla.",
        "Share solution image" to "Compartir imagen de la solución",
        "Toggle wiring mode" to "Activar o desactivar modo de cableado",
        "Use larger learning controls" to "Usar controles de aprendizaje más grandes",
        "Use signed two’s-complement interpretation" to "Usar interpretación con signo en complemento a dos",
        "Copy working" to "Copiar procedimiento",
        "Skip" to "Omitir",
        "Back" to "Atrás",
        "13 ÷ 2 = 6, r1\n 6 ÷ 2 = 3, r0\n 3 ÷ 2 = 1, r1\n 1 ÷ 2 = 0, r1\n↑ read upward → 1101₂" to "13 ÷ 2 = 6, r1\n 6 ÷ 2 = 3, r0\n 3 ÷ 2 = 1, r1\n 1 ÷ 2 = 0, r1\n↑ lee de abajo arriba → 1101₂",
        "−13 at 8 bits\n00001101 → invert → +1\n11110011 = unsigned 243" to "−13 en 8 bits\n00001101 → invertir → +1\n11110011 = 243 sin signo"
    )
    private val pt = mapOf(
        "Binary gates take 2 inputs here; chain them for wider functions. NOT and BUFFER take 1." to "Aqui, portas binárias têm 2 entradas; encadeie-as para funções mais amplas. NOT e BUFFER têm 1.",
        "Complete the wiring to generate the table." to "Conclua a fiação para gerar a tabela.",
        "Includes saved conversions, circuit sessions, mastered practice cards, the current K-map/lab drafts and comfort preferences. Theme, reminder time and inbox stay on this device; the app follows your system language and does not include it in backups. Circuit traces and signing keys/passwords are never included. JSON is NOT encrypted; store it privately." to "Inclui conversões salvas, sessões de circuitos, questões dominadas, rascunhos de mapas/laboratório e preferências. Tema, horário dos lembretes e caixa de entrada ficam no dispositivo; o idioma do sistema não entra no backup. Rastros e chaves/senhas nunca são incluídos. O JSON NÃO é criptografado: guarde-o em local privado.",
        "Why 0.1 + 0.2 ≠ 0.3: most decimal fractions repeat forever in binary, so the nearest float is stored, not the exact value." to "Por que 0,1 + 0,2 ≠ 0,3: muitas frações decimais se repetem infinitamente em binário, então o valor de ponto flutuante mais próximo é armazenado, não o exato.",
        "Wire an external input first." to "Conecte primeiro uma entrada externa.",
        "XP: Easy 10, Intermediate 20, Advanced 30 per mastered card, plus up to +14 for long streaks. Levels grow quadratically — early wins come fast." to "XP: 10 no Fácil, 20 no Intermediário e 30 no Avançado por questão dominada, mais até 14 por sequências longas. Os níveis crescem quadraticamente: os primeiros chegam rápido.",
        """1. Gray order is 00, 01, 11, 10.
2. Group powers of two, not 3 or 6.
3. Use rectangles, not diagonal-only pairs.
4. Opposite edges touch; four corners can wrap.
5. Overlap is allowed; Xs are optional.""" to """1. A ordem Gray é 00, 01, 11, 10.
2. Agrupe potências de dois, não grupos de 3 ou 6.
3. Use retângulos, não pares apenas diagonais.
4. Bordas opostas são adjacentes; os quatro cantos podem se agrupar.
5. É permitido sobrepor grupos; X é opcional.""",
        "Allow haptic feedback" to "Permitir resposta háptica",
        "Arithmetic base from 2 to 36" to "Base aritmética de 2 a 36",
        "A′B + AB′  ≡  A XOR B\n3 inputs → 8 rows (0–7)\n4 inputs → 16 rows (0–15)" to "A′B + AB′  ≡  A XOR B\n3 entradas → 8 linhas (0–7)\n4 entradas → 16 linhas (0–15)",
        "SOP: fixed 0 → A', fixed 1 → A.\nPOS: fixed 0 → A, fixed 1 → A'.\n+ is OR, adjacency is AND, ' is NOT.\nSwitching SOP/POS preserves the truth table." to "SOP: 0 fixo → A', 1 fixo → A.\nPOS: 0 fixo → A, 1 fixo → A'.\n+ é OR, justaposição é AND e ' é NOT.\nAlternar SOP/POS mantém a tabela-verdade.",
        "Save keeps the minimized formula and form; Copy working includes the groups, rules and full table." to "Salvar mantém a fórmula mínima e sua forma; Copiar cálculo inclui os grupos, as regras e a tabela completa.",
        "Share solution image" to "Compartilhar imagem da solução",
        "Toggle wiring mode" to "Ativar ou desativar modo de fiação",
        "Use larger learning controls" to "Usar controles de aprendizagem maiores",
        "Use signed two’s-complement interpretation" to "Usar interpretação com sinal em complemento de dois",
        "Copy working" to "Copiar cálculo",
        "Skip" to "Pular",
        "Back" to "Voltar",
        "13 ÷ 2 = 6, r1\n 6 ÷ 2 = 3, r0\n 3 ÷ 2 = 1, r1\n 1 ÷ 2 = 0, r1\n↑ read upward → 1101₂" to "13 ÷ 2 = 6, r1\n 6 ÷ 2 = 3, r0\n 3 ÷ 2 = 1, r1\n 1 ÷ 2 = 0, r1\n↑ leia de baixo para cima → 1101₂",
        "−13 at 8 bits\n00001101 → invert → +1\n11110011 = unsigned 243" to "−13 em 8 bits\n00001101 → inverter → +1\n11110011 = 243 sem sinal"
    )

    fun translate(source: String, language: String): String? = when (language.lowercase()) {
        "fr" -> fr[source]
        "es" -> es[source]
        "pt" -> pt[source]
        else -> null
    }
}
