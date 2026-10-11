package com.radix.app

/** Additional screen and accessibility copy; technical symbols and variable names stay unchanged. */
internal object UiTranslationsExtra {
    private val fr=mapOf(
        "Clear the canvas" to "Effacer la zone de travail",
        "Copy table" to "Copier la table",
        "× remove" to "× supprimer",
        "Share score card" to "Partager la carte de score",
        "Remove" to "Supprimer",
        "New player name" to "Nom du nouveau joueur",
        "Laws, not luck." to "Les lois, pas la chance.",
        "NOTHING IS TAKEN ON FAITH HERE. Tap Verify and the app checks both sides on all 4 rows (2 variables) or all 8 rows (3 variables)." to "Ici, rien n’est laissé au hasard. Touchez Vérifier : l’application compare les deux côtés sur les 4 lignes (2 variables) ou les 8 lignes (3 variables).",
        "Copy all laws" to "Copier toutes les lois",
        "Notation: · is AND, + is OR, ⊕ is XOR, ⊙ is XNOR, ′ is NOT. Adjacency (AB) also means AND." to "Notation : · signifie ET, + OU, ⊕ XOR, ⊙ XNOR, ′ NON. La juxtaposition (AB) signifie aussi ET.",
        "Add. Subtract." to "Addition. Soustraction.",
        "First number" to "Premier nombre",
        "Second number" to "Deuxième nombre",
        "Copy full working" to "Copier tout le calcul",
        "Denary · octal · hex · binary" to "Décimal · octal · hexadécimal · binaire",
        "One table for the numbers 0–15. Read a row across to convert instantly." to "Une table pour les nombres de 0 à 15. Lisez une ligne pour convertir instantanément.",
        "Why octal and hex exist: 3 bits make one octal digit, 4 bits make one hex digit — so long binary strings stay readable." to "Pourquoi l’octal et l’hexadécimal ? Trois bits forment un chiffre octal et quatre un chiffre hexadécimal : les longues suites binaires restent lisibles.",
        "Small circuits." to "De petits circuits.",
        "Change ▾" to "Changer ▾",
        "Ideal digital models, plus an optional teaching live-timing layer (fixed gate delays, glitch replay, auto clock). No electrical voltage, setup/hold violation or metastability simulation." to "Modèles numériques idéaux, avec une chronologie pédagogique facultative (délais fixes, rejeu des parasites, horloge automatique). Aucune simulation de tension, de violation setup/hold ou de métastabilité.",
        "Choose your next aha." to "Choisissez votre prochain déclic.",
        "Show current input row" to "Afficher la ligne d’entrée actuelle",
        "Simplify this gate in K-maps" to "Simplifier cette porte avec une carte de Karnaugh",
        "No memory: outputs follow the current inputs." to "Sans mémoire : les sorties suivent les entrées actuelles.",
        "Swipe ↔ to follow all gates. Crossing wires connect only at dotted pin junctions." to "Balayez ↔ pour suivre les portes. Les fils croisés ne se connectent qu’aux jonctions pointillées.",
        "This circuit has more than six input variables; K-maps support up to six variables." to "Ce circuit a plus de six variables d’entrée ; les cartes de Karnaugh en acceptent au maximum six.",
        "Inversion bubbles mean NOT; the extra curved input line identifies XOR/XNOR." to "Les bulles d’inversion signifient NON ; la ligne d’entrée courbe supplémentaire indique XOR/XNOR.",
        "Switch to phone-friendly block view" to "Passer à la vue en blocs, adaptée au téléphone",
        "Explore the gate-level diagram" to "Explorer le schéma au niveau des portes",
        "Forbidden inputs force equal physical outputs. Stored memory is indeterminate; a valid set/reset recovers it." to "Les entrées interdites imposent des sorties physiques identiques. La mémoire stockée est indéterminée ; un état set/reset valide la rétablit.",
        "Memory is indeterminate (X). Use a valid set/reset or capture a known D value to recover." to "La mémoire est indéterminée (X). Utilisez un set/reset valide ou capturez une valeur D connue pour la rétablir.",
        "See inside the circuit" to "Voir l’intérieur du circuit",
        "Q(next) applies on a rising edge. At other times Q holds." to "Q(suivant) s’applique sur un front montant. Le reste du temps, Q conserve sa valeur.",
        "Q(now)" to "Q(actuel)",
        "Q(next)" to "Q(suivant)",
        "Status" to "État",
        "Level-sensitive: enabled inputs can change Q immediately." to "Sensible au niveau : les entrées activées peuvent changer Q immédiatement.",
        "⏸ Stop live clock" to "⏸ Arrêter l’horloge en direct",
        "▶ Live clock" to "▶ Horloge en direct",
        "↑ Rise" to "↑ Front montant",
        "↓ Fall" to "↓ Front descendant",
        "Pulse" to "Impulsion",
        "Inside view: grey nets are structure-only; colored nets carry live values. Crossing wires connect only at dotted pin junctions." to "Vue interne : les fils gris montrent la structure ; les fils colorés portent les valeurs en direct. Les croisements ne se connectent qu’aux jonctions pointillées.",
        "Release both to hold" to "Relâchez les deux pour mémoriser",
        "Models simultaneous release. Changing only one input first can recover a known set/reset state." to "Modélise le relâchement simultané. Changer d’abord une seule entrée peut rétablir un état connu.",
        "Initialize simulation" to "Initialiser la simulation",
        "Initialization sets a known teaching state; it does not model real hardware power-up." to "L’initialisation définit un état pédagogique connu ; elle ne modélise pas le démarrage réel du matériel.",
        "See memory over time" to "Observer la mémoire au fil du temps",
        "Each column is an action sample, not elapsed time. Swipe ↔ for the latest 12 samples. X means unknown." to "Chaque colonne représente un échantillon d’action, pas une durée. Balayez ↔ pour voir les 12 derniers. X signifie inconnu.",
        "Next-state table" to "Table des états suivants",
        "Period" to "Période",
        "Nanosecond timeline · Q sampled at edges" to "Chronologie en nanosecondes · Q échantillonné sur les fronts",
        "The auto clock pulses this same ideal model; the diagram above grows live. Teaching delays only." to "L’horloge automatique fait évoluer ce même modèle idéal ; le schéma ci-dessus se met à jour en direct. Les délais sont pédagogiques.",
        "A little extra." to "Un petit plus.",
        "Radix 7.0 · no ads, accounts or Internet permission" to "Radix 7.0 · sans publicité, compte ni autorisation Internet",
        "Paste" to "Coller",
        "Save" to "Enregistrer",
        "LIVE CONVERSIONS" to "CONVERSIONS EN DIRECT",
        "Show the working" to "Afficher le calcul",
        "Add a custom base" to "Ajouter une base personnalisée",
        "( ) = repeating digits · … = truncated at 24 places" to "( ) = chiffres périodiques · … = tronqué après 24 positions",
        "Tap Save on a conversion to keep it here.\nYour collection stays completely offline." to "Touchez Enregistrer sur une conversion pour la conserver ici.\nVotre collection reste entièrement hors ligne.",
        "Open conversion  " to "Ouvrir la conversion  ",
        "Surprise me" to "Surprenez-moi",
        "decimal" to "décimal",
        "Take it to the converter  " to "Ouvrir dans le convertisseur  ",
        "A AND B — tap inputs to feel it" to "A ET B — touchez les entrées pour voir le résultat",
        "Repeating tails use a geometric series:" to "Les périodes répétées utilisent une série géométrique :",
        "Restore the minus sign. This is signed magnitude, not fixed-width two's complement." to "Rétablissez le signe moins. Il s’agit d’un signe-magnitude, pas d’un complément à deux de largeur fixe.",
        "Load Σm / ΠM function" to "Charger la fonction Σm / ΠM",
        "Reset to 0" to "Réinitialiser à 0",
        "Only specified 0/1 cells must match. Xs impose no output requirement." to "Seules les cases 0/1 spécifiées doivent correspondre. Les X n’imposent aucune sortie.",
        "F · CURRENT OUTPUT" to "F · SORTIE ACTUELLE",
        "Σm · ON SET" to "Σm · ENSEMBLE DES 1",
        "ΠM · OFF SET" to "ΠM · ENSEMBLE DES 0",
        "SOP groups required 1s. A literal that changes within a group is removed; a fixed 0 is complemented and a fixed 1 is not." to "La SOP regroupe les 1 requis. Un littéral qui varie dans le groupe disparaît ; un 0 fixe est complémenté, un 1 fixe ne l’est pas.",
        "No 1-cells: the minimum SOP is the constant 0." to "Aucune case à 1 : la SOP minimale est la constante 0.",
        "POS groups required 0s. A fixed 0 stays uncomplemented; a fixed 1 is complemented in its sum clause. Opposite map edges are adjacent." to "La POS regroupe les 0 requis. Un 0 fixe reste non complémenté ; un 1 fixe est complémenté dans sa somme. Les bords opposés de la carte sont adjacents.",
        "No 0-cells: the minimum POS is the constant 1." to "Aucune case à 0 : la POS minimale est la constante 1.",
        "The exact minimizer covers every required cell, uses no forbidden cell, then minimizes term count and literal count. XNOR and NAND/NOR are already evaluated by the parser." to "Le minimiseur exact couvre chaque case requise, évite les cases interdites, puis minimise le nombre de termes et de littéraux. L’analyseur prend déjà en charge XNOR et NAND/NOR.",
        "The map is unavailable until the expression is valid." to "La carte est indisponible tant que l’expression n’est pas valide.",
        "K-map variables are shown in the expression's first-appearance order; the leftmost listed input is the most significant bit." to "Les variables de la carte suivent leur premier ordre d’apparition dans l’expression ; l’entrée la plus à gauche est le bit de poids fort.",
        "Gate-level circuit · live" to "Circuit au niveau des portes · en direct",
        "Saved on this device, restored with portable study backups, and capped at 50 items." to "Enregistrées sur cet appareil, incluses dans les sauvegardes et limitées à 50 solutions.",
        "Nothing saved yet. Solve an expression or K-map, then tap Save solution." to "Aucune solution enregistrée. Résolvez une expression ou une carte, puis touchez Enregistrer la solution.",
        "From digits to memory." to "Des chiffres à la mémoire.",
        "Every conversion." to "Chaque conversion.",
        "History records a value after you pause typing. Repeated adjacent entries are combined; this is separate from your hand-picked saved numbers." to "L’historique enregistre une valeur après une courte pause de saisie. Les entrées voisines identiques sont regroupées ; il est distinct des nombres enregistrés manuellement.",
        "Type a valid value in Convert and pause briefly. It will appear here with its source base." to "Saisissez une valeur valide dans Convertir et faites une courte pause. Elle apparaîtra ici avec sa base source.",
        "Open Convert" to "Ouvrir Convertir",
        "Clear conversion history?" to "Effacer l’historique des conversions ?",
        "This removes recent conversion entries only. Your separately saved numbers and all other study data stay unchanged." to "Seules les conversions récentes seront supprimées. Vos nombres enregistrés et toutes les autres données resteront inchangés.",
        "Clear history" to "Effacer l’historique",
        "Keep history" to "Conserver l’historique",
        "Same bits." to "Les mêmes bits.",
        "Encode or decode" to "Encoder ou décoder",
        "Two’s-complement interpretation" to "Interprétation en complément à deux",
        "Off = unsigned decimal input/range" to "Désactivé = entrée/plage décimale non signée",
        "One word, two interpretations" to "Un mot, deux interprétations",
        "UNSIGNED" to "NON SIGNÉ",
        "TWO’S COMPLEMENT" to "COMPLÉMENT À DEUX",
        "HEX · SAME WORD" to "HEX · MÊME MOT",
        "One’s complement · invert every bit" to "Complément à un · inverser chaque bit",
        "INVERTED WORD" to "MOT INVERSÉ",
        "ONE’S-COMPLEMENT VALUE" to "VALEUR EN COMPLÉMENT À UN",
        "One’s complement negates by flipping bits only — no +1. So zero has two faces: all-0s (+0) and all-1s (−0)." to "Le complément à un nie en inversant les bits, sans ajouter 1. Le zéro a donc deux formes : tous les bits à 0 (+0) et tous à 1 (−0).",
        "Codes are not the same as binary" to "Les codes ne sont pas du binaire ordinaire",
        "GRAY CODE OF THIS WORD" to "CODE GRAY DE CE MOT",
        "BCD OF THE SELECTED DECIMAL VALUE" to "BCD DE LA VALEUR DÉCIMALE CHOISIE",
        "Gray changes one bit between consecutive non-negative counts. BCD encodes each decimal digit separately; unused BCD nibbles 1010–1111 are not decimal digits." to "Le code Gray ne change qu’un bit entre deux nombres consécutifs. Le BCD code chaque chiffre décimal séparément ; les quartets 1010–1111 ne sont pas des chiffres décimaux valides.",
        "This lab is fixed-width. The original Convert tab uses signed magnitude for negative values; it does not automatically mean two’s complement." to "Ce laboratoire utilise une largeur fixe. L’onglet Convertir représente les nombres négatifs en signe-magnitude, pas automatiquement en complément à deux.",
        "Try the idea." to "Mettez l’idée à l’épreuve.",
        "Nice!" to "Bien joué !",
        "A correct check counts as mastered. Revealing an answer does not. Streaks celebrate practice days — they never punish a missed day." to "Une réponse vérifiée correctement est maîtrisée. Révéler une réponse ne compte pas. Les séries célèbrent les jours d’entraînement sans pénaliser les pauses.",
        "Check my answer" to "Vérifier ma réponse",
        "Reveal & explain" to "Révéler et expliquer",
        "No cards combine this level with this topic — loosen a filter to continue." to "Aucune question ne combine ce niveau et ce thème ; élargissez un filtre pour continuer.",
        "← Previous" to "← Précédent",
        "Next idea →" to "Idée suivante →",
        "Subjective · short-answer checkpoint" to "Autoévaluation · réponse courte",
        "Objective mode above checks a selected answer. Here, write your reasoning first, then compare it with a model solution; written responses are intentionally self-assessed, not automatically graded." to "Le mode objectif vérifie un choix. Ici, rédigez d’abord votre raisonnement puis comparez-le à une solution modèle ; les réponses écrites sont autoévaluées, sans note automatique.",
        "Your explanation or working" to "Votre explication ou votre calcul",
        "MODEL ANSWER" to "RÉPONSE MODÈLE",
        "Check both the result and the reasoning in your own words; compare the method, not just the final line." to "Vérifiez le résultat et expliquez le raisonnement avec vos mots ; comparez la méthode, pas seulement la réponse finale.",
        "Reset practice progress" to "Réinitialiser la progression d’entraînement",
        "Reset mastered cards?" to "Réinitialiser les questions maîtrisées ?",
        "Only practice progress is cleared. Circuits, conversions and K-maps stay unchanged." to "Seule la progression d’entraînement sera effacée. Les circuits, conversions et cartes resteront inchangés.",
        "Reset progress" to "Réinitialiser la progression",
        "Keep progress" to "Conserver la progression",
        "Save the state." to "Enregistrez l’état.",
        "Save a checkpoint from Logic. A saved session restores its model, inputs, clock and memory; its timing trace starts fresh. Up to 24 sessions stay on this device." to "Enregistrez un point de reprise depuis Logique. Une session restaure le modèle, les entrées, l’horloge et la mémoire ; sa chronologie redémarre. Jusqu’à 24 sessions sont conservées sur l’appareil.",
        "Your first checkpoint awaits" to "Votre premier point de reprise vous attend",
        "Open Logic, explore a circuit, then tap Save session." to "Ouvrez Logique, explorez un circuit puis touchez Enregistrer la session.",
        "Open Logic" to "Ouvrir Logique",
        "Restore" to "Restaurer",
        "This removes the saved checkpoint, not the current circuit. Export a backup first if you want a copy." to "Cela supprime le point de reprise enregistré, pas le circuit actuel. Exportez d’abord une sauvegarde si vous souhaitez en garder une copie.",
        "Keep" to "Garder",
        "Make it yours." to "Personnalisez l’application.",
        "Comfort controls" to "Commandes de confort",
        "Haptic feedback" to "Retour haptique",
        "Allow short native tap feedback" to "Autoriser un bref retour tactile à chaque appui",
        "Larger learning controls" to "Commandes d’apprentissage plus grandes",
        "More room for circuit inputs and study actions; system font scaling is respected" to "Plus d’espace pour les entrées de circuit et les actions d’étude ; la taille de police du système est respectée.",
        "Appearance" to "Apparence",
        "Reminders & inbox" to "Rappels et boîte de réception",
        "Updates & version" to "Mises à jour et version",
        "Sync via your cloud drive" to "Synchroniser avec votre espace cloud",
        "Radix runs no cloud of its own — instead, save the portable backup file straight into Google Drive, Dropbox or any folder the system picker offers. Reopen that same file on another device (or after reinstalling) to continue exactly where you stopped. Same validation, same privacy: the file is yours, unencrypted JSON, and Radix never uploads anything by itself." to "Radix ne possède pas de cloud. Enregistrez plutôt la sauvegarde portable dans Google Drive, Dropbox ou tout dossier proposé par le sélecteur système. Rouvrez ce fichier sur un autre appareil (ou après réinstallation) pour reprendre où vous en étiez. Le fichier JSON vous appartient, n’est pas chiffré, et Radix ne téléverse jamais rien.",
        "Save backup into Drive…" to "Enregistrer la sauvegarde dans Drive…",
        "Open backup from Drive…" to "Ouvrir une sauvegarde depuis Drive…",
        "Create a portable backup" to "Créer une sauvegarde portable",
        "Save backup file" to "Enregistrer le fichier de sauvegarde",
        "Copy backup text" to "Copier le texte de sauvegarde",
        "Validate, then restore" to "Valider puis restaurer",
        "Choose backup file" to "Choisir un fichier de sauvegarde",
        "Or paste backup JSON" to "Ou coller le JSON de sauvegarde",
        "Validate backup" to "Valider la sauvegarde",
        "Review replacement & restore" to "Vérifier le remplacement et restaurer",
        "Replace local study data?" to "Remplacer les données d’étude locales ?",
        "The validated backup REPLACES your saved conversions, sessions, progress, K-map, lab drafts and preferences. Export a backup of your current data first. The active circuit is not changed." to "La sauvegarde validée REMPLACERA vos conversions, sessions, progression, carte de Karnaugh, brouillons et préférences. Exportez d’abord vos données actuelles. Le circuit actif ne changera pas.",
        "Replace & restore" to "Remplacer et restaurer",
        "Save a circuit checkpoint" to "Enregistrer un point de reprise du circuit",
        "Session name · up to 40 characters" to "Nom de session · jusqu’à 40 caractères",
        "Current inputs, clock and stored memory are kept. The trace starts fresh on restore." to "Les entrées, l’horloge et la mémoire sont conservées. La chronologie repart de zéro lors de la restauration.",
        "Library is full (24). Back up/delete an older session first." to "La bibliothèque est pleine (24). Sauvegardez ou supprimez d’abord une ancienne session.",
        "Save session" to "Enregistrer la session",
        "A little help.\nA lot of aha." to "Un peu d’aide.\nBeaucoup de déclics.",
        "Take a guided tour of the core tools. This built-in helper works offline. It is not a chat or network service. The app follows the system language when English, French, Spanish or Portuguese is supported; otherwise it uses English." to "Découvrez les outils essentiels avec ce guide intégré et disponible hors ligne. Ce n’est ni un chat ni un service réseau. L’application suit la langue du système si elle est en anglais, en français, en espagnol ou en portugais ; sinon, elle utilise l’anglais.",
        "Start the visual app tour" to "Commencer la visite guidée de l’application",
        "Try" to "Essayer",
        "→ Hint" to "→ Indice",
        "→ Check" to "→ Vérifier",
        "Undo ↶  •  Save  •  Restore ↷\nExport → Validate → Confirm" to "Annuler ↶  •  Enregistrer  •  Restaurer ↷\nExporter → Valider → Confirmer",
        "Ready to explore" to "Prêt à explorer",
        "Offline backup file saved." to "Fichier de sauvegarde hors ligne enregistré.",
        "Backup loaded. Validate before restoring." to "Sauvegarde chargée. Validez-la avant de la restaurer.",
        "Backup text copied; paste it somewhere private." to "Texte de sauvegarde copié ; collez-le dans un endroit sûr.",
        "Validated study data restored." to "Données d’étude validées restaurées.",
        "Circuit checkpoint saved." to "Point de reprise du circuit enregistré.",
        "Session library is full. Delete or back up an older checkpoint first." to "La bibliothèque de sessions est pleine. Supprimez ou sauvegardez d’abord un ancien point de reprise.",
        "Complete truth/state table copied." to "Table complète de vérité ou d’états copiée.",
        "Visible truth-table page copied." to "Page visible de la table de vérité copiée.",
        "Truth table copied." to "Table de vérité copiée.",
        "Arithmetic working copied." to "Calcul arithmétique copié.",
        "Float working copied." to "Calcul IEEE-754 copié.",
        "Representation working copied." to "Calcul des représentations copié.",
        "K-map working copied." to "Calcul de la carte de Karnaugh copié.",
        "Simplified expression copied." to "Expression simplifiée copiée.",
        "Boolean solution saved on this device and included in backups." to "Solution booléenne enregistrée sur cet appareil et incluse dans les sauvegardes.",
        "That solution is already saved; it was moved to the top." to "Cette solution est déjà enregistrée ; elle a été replacée en tête.",
        "K-map formula saved in Boolean solutions and backups." to "Formule de la carte de Karnaugh enregistrée dans les solutions booléennes et les sauvegardes.",
        "That formula was already saved; it was moved to the top." to "Cette formule est déjà enregistrée ; elle a été replacée en tête.",
        "K-map solution shared as an image." to "Solution de la carte de Karnaugh partagée en image.",
        "Fix the expression before solving." to "Corrigez l’expression avant de lancer la résolution.",
        "Enter expression G to verify equivalence." to "Saisissez l’expression G pour vérifier l’équivalence.",
        "Approximate value copied · 24 fractional places." to "Valeur approximative copiée · 24 positions fractionnaires.",
        "Removed from saved numbers." to "Supprimé des nombres enregistrés.",
        "All Boolean laws copied." to "Toutes les lois booléennes ont été copiées.",
        "Pick a glowing output first." to "Sélectionnez d’abord une sortie lumineuse.",
        "Wire removed." to "Fil supprimé.",
        "Turn wiring mode ON, then tap this output." to "Activez le mode câblage, puis touchez cette sortie.",
        "Saved expression opened." to "L’expression enregistrée a été ouverte.",
        "Saved solution removed." to "Solution enregistrée supprimée.",
        "Conversion restored from history." to "Conversion restaurée depuis l’historique.",
        "History entry removed." to "Entrée de l’historique supprimée.",
        "Conversion history cleared." to "Historique des conversions effacé.",
        "Session restored; fresh timing trace." to "Session restaurée ; la chronologie repart de zéro.",
        "Start from a working build" to "Commencez par un montage qui fonctionne",
        "Gate palette · tap to place" to "Palette de portes · touchez pour placer",
        "Finish the wiring" to "Terminez le câblage",
        "Live simulation" to "Simulation en direct",
        "Pulse an input · watch for glitches" to "Impulsion d’entrée · observez les parasites",
        "Your circuit's truth table" to "Table de vérité de votre circuit",
        "Your float" to "Votre nombre flottant",
        "Household table · same device" to "Classement familial · même appareil",
        "Selected" to "Sélectionnée",
        "Not selected" to "Non sélectionnée",
        "Copy simplified expression" to "Copier l’expression simplifiée",
        "SCORE CARD" to "CARTE DE SCORE",
        "Radix · offline learning · no ads, no accounts" to "Radix · apprentissage hors ligne · sans publicité ni compte",
        "Share card" to "Partager la carte",
        "cross-coupled nor gates" to "portes NOR à rétroaction croisée",
        "cross-coupled nand gates" to "portes NAND à rétroaction croisée",
        "MASTER · follows D while CLK=0" to "MAÎTRE · suit D tant que CLK=0",
        "SLAVE · copies master on ↑CLK" to "ESCLAVE · copie le maître sur ↑CLK",
        "master Q → slave D" to "Q maître → D esclave",
        "J and K are tied together: T=0 holds, T=1 toggles" to "J=K : T=0 conserve, T=1 bascule",
        "synchronous: every stage shares one clock edge" to "synchrone : même front d’horloge pour chaque étage",
        "D in" to "Entrée D",
        "Q3 feeds back to D0" to "Q3 reboucle vers D0",
        "inverted Q3 feeds back to D0" to "Q3 inversé reboucle vers D0",
        "serial in → Q0 ··· Q3 out" to "Entrée série → Q0 ··· sortie Q3",
        "Tap a glowing output, then an input (or the probe)." to "Touchez une sortie lumineuse, puis une entrée (ou la sonde).",
        "Drag gates to arrange. Tap an input pin to unwire it." to "Faites glisser les portes pour les placer. Touchez une broche d’entrée pour la déconnecter.",
        "⚡ Glitch: the probe flickers before settling." to "⚡ Parasite : la sonde vacille avant de se stabiliser.",
        "Clean: the probe changes once, then holds." to "Signal stable : la sonde change une fois, puis reste ainsi.",
        "Single · 32-bit" to "Simple · 32 bits",
        "Double · 64-bit" to "Double précision · 64 bits",
        "Decimal, inf, −inf or nan" to "Décimal, inf, −inf ou nan",
        "Let's explore" to "Explorons !",
        "Each product term is an AND gate; their outputs feed the final OR." to "Chaque terme produit correspond à une porte ET ; leurs sorties alimentent la porte OU finale.",
        "Each sum clause is an OR gate; their outputs feed the final AND." to "Chaque clause somme correspond à une porte OU ; leurs sorties alimentent la porte ET finale.",
        "✓ Correct. Here’s why." to "✓ Correct. Voici pourquoi.",
        "Not yet. Try the hint and check again." to "Pas encore. Utilisez l’indice et vérifiez à nouveau.",
        "Hide hint" to "Masquer l’indice",
        "Show a hint" to "Afficher un indice",
        "Hide model answer" to "Masquer la réponse modèle",
        "Compare with model answer" to "Comparer à la réponse modèle",
        "Tap to select; values stay fixed." to "Touchez pour sélectionner ; les valeurs restent fixes.",
        "Tap: 0 → 1 → X → 0" to "Touchez : 0 → 1 → X → 0",
        "Exit group check" to "Quitter la vérification de groupe",
        "Check a group" to "Vérifier un groupe",
        "Tap to verify live" to "Touchez pour vérifier en direct",
        "Mismatch — report this bug" to "Résultat différent — signalez ce bogue",
        "Big ideas." to "De grandes idées.",
        "THE DIGITAL LOGIC STUDIO" to "STUDIO DE LOGIQUE NUMÉRIQUE",
        "Tap an input. Follow a wire. Understand the output." to "Touchez une entrée. Suivez un fil. Comprenez la sortie.",
        "A lot to explore." to "De quoi explorer.",
        "YOUR LEARNING TOOLBOX" to "VOTRE BOÎTE À OUTILS D’APPRENTISSAGE",
        "Everything offline. Everything within a tap." to "Tout hors ligne. Tout à portée de doigt.",
        "Prove every line." to "Prouvez chaque ligne.",
        "THE BOOLEAN LAWS LAB" to "LABORATOIRE DES LOIS BOOLÉENNES",
        "Each law is verified live against every input row." to "Chaque loi est vérifiée en direct pour toutes les combinaisons d’entrées.",
        "Multiply. Divide." to "Multipliez. Divisez.",
        "BASE ARITHMETIC, WITH WORKING" to "ARITHMÉTIQUE EN BASE, AVEC CALCUL DÉTAILLÉ",
        "Exact integer math in any base from 2 to 36." to "Calcul entier exact dans toutes les bases, de 2 à 36.",
        "Place gates." to "Placez des portes.",
        "Wire the idea." to "Reliez les portes pour donner vie à l’idée.",
        "THE CIRCUIT BUILDER" to "CONSTRUCTEUR DE CIRCUITS",
        "Drag, connect, simulate — feedback-free, always explainable." to "Glissez, reliez, simulez — sans retour matériel, avec des résultats toujours explicables.",
        "Fractions fly." to "Les fractions prennent leur envol.",
        "Bits explain." to "Les bits éclairent le calcul.",
        "THE IEEE-754 FLOAT LAB" to "LABORATOIRE IEEE-754 DES NOMBRES FLOTTANTS",
        "Sign, exponent, fraction — every float, decoded." to "Signe, exposant, fraction — chaque flottant décodé.",
        "Earn XP." to "Gagnez des XP.",
        "Top the table." to "Prenez la tête du classement.",
        "LEADERBOARD & REWARDS" to "CLASSEMENT ET RÉCOMPENSES",
        "One device, up to 8 players. Streaks never punish a missed day." to "Un appareil, jusqu’à 8 joueurs. Les séries ne pénalisent jamais un jour manqué."
    )

    private val es=mapOf(
        "Clear the canvas" to "Borrar el lienzo",
        "Copy table" to "Copiar tabla",
        "× remove" to "× quitar",
        "Share score card" to "Compartir tarjeta de puntuación",
        "Remove" to "Quitar",
        "New player name" to "Nombre del nuevo jugador",
        "Laws, not luck." to "Leyes, no suerte.",
        "NOTHING IS TAKEN ON FAITH HERE. Tap Verify and the app checks both sides on all 4 rows (2 variables) or all 8 rows (3 variables)." to "Aquí nada se da por hecho. Toca Verificar y la aplicación comprueba ambos lados en las 4 filas (2 variables) o en las 8 filas (3 variables).",
        "Copy all laws" to "Copiar todas las leyes",
        "Notation: · is AND, + is OR, ⊕ is XOR, ⊙ is XNOR, ′ is NOT. Adjacency (AB) also means AND." to "Notación: · significa AND, + OR, ⊕ XOR, ⊙ XNOR y ′ NOT. La yuxtaposición (AB) también significa AND.",
        "Add. Subtract." to "Suma. Resta.",
        "First number" to "Primer número",
        "Second number" to "Segundo número",
        "Copy full working" to "Copiar procedimiento completo",
        "Denary · octal · hex · binary" to "Decimal · octal · hexadecimal · binario",
        "One table for the numbers 0–15. Read a row across to convert instantly." to "Una tabla para los números del 0 al 15. Lee una fila para convertir al instante.",
        "Why octal and hex exist: 3 bits make one octal digit, 4 bits make one hex digit — so long binary strings stay readable." to "Por qué existen el octal y el hexadecimal: 3 bits forman un dígito octal y 4 uno hexadecimal, así las cadenas binarias largas siguen siendo legibles.",
        "Small circuits." to "Circuitos pequeños.",
        "Change ▾" to "Cambiar ▾",
        "Ideal digital models, plus an optional teaching live-timing layer (fixed gate delays, glitch replay, auto clock). No electrical voltage, setup/hold violation or metastability simulation." to "Modelos digitales ideales, con una capa temporal didáctica opcional (retardos fijos, repetición de glitches y reloj automático). No simula voltajes, violaciones de setup/hold ni metastabilidad.",
        "Choose your next aha." to "Elige tu próximo descubrimiento.",
        "Show current input row" to "Mostrar fila de entrada actual",
        "Simplify this gate in K-maps" to "Simplificar esta puerta con mapas de Karnaugh",
        "No memory: outputs follow the current inputs." to "Sin memoria: las salidas siguen las entradas actuales.",
        "Swipe ↔ to follow all gates. Crossing wires connect only at dotted pin junctions." to "Desliza ↔ para seguir todas las puertas. Los cables cruzados solo se conectan en las uniones punteadas.",
        "This circuit has more than six input variables; K-maps support up to six variables." to "Este circuito tiene más de seis variables de entrada; los mapas de Karnaugh admiten hasta seis.",
        "Inversion bubbles mean NOT; the extra curved input line identifies XOR/XNOR." to "Las burbujas de inversión significan NOT; la línea curva adicional de entrada identifica XOR/XNOR.",
        "Switch to phone-friendly block view" to "Cambiar a la vista de bloques, adaptada al móvil",
        "Explore the gate-level diagram" to "Explorar el diagrama a nivel de puertas",
        "Forbidden inputs force equal physical outputs. Stored memory is indeterminate; a valid set/reset recovers it." to "Las entradas prohibidas fuerzan salidas físicas iguales. La memoria almacenada es indeterminada; un set/reset válido la recupera.",
        "Memory is indeterminate (X). Use a valid set/reset or capture a known D value to recover." to "La memoria es indeterminada (X). Usa un set/reset válido o captura un valor D conocido para recuperarla.",
        "See inside the circuit" to "Ver el interior del circuito",
        "Q(next) applies on a rising edge. At other times Q holds." to "Q(siguiente) se aplica en un flanco ascendente. En los demás momentos, Q se mantiene.",
        "Q(now)" to "Q(actual)",
        "Q(next)" to "Q(siguiente)",
        "Status" to "Estado",
        "Level-sensitive: enabled inputs can change Q immediately." to "Sensible al nivel: las entradas habilitadas pueden cambiar Q de inmediato.",
        "⏸ Stop live clock" to "⏸ Detener reloj en vivo",
        "▶ Live clock" to "▶ Reloj en vivo",
        "↑ Rise" to "↑ Subida",
        "↓ Fall" to "↓ Bajada",
        "Pulse" to "Pulso",
        "Inside view: grey nets are structure-only; colored nets carry live values. Crossing wires connect only at dotted pin junctions." to "Vista interna: las redes grises muestran la estructura; las de color llevan valores en directo. Los cables cruzados solo se conectan en uniones punteadas.",
        "Release both to hold" to "Soltar ambos para mantener",
        "Models simultaneous release. Changing only one input first can recover a known set/reset state." to "Modela la liberación simultánea. Cambiar primero una sola entrada puede recuperar un estado conocido.",
        "Initialize simulation" to "Inicializar simulación",
        "Initialization sets a known teaching state; it does not model real hardware power-up." to "La inicialización establece un estado didáctico conocido; no modela el encendido del hardware real.",
        "See memory over time" to "Ver la memoria a lo largo del tiempo",
        "Each column is an action sample, not elapsed time. Swipe ↔ for the latest 12 samples. X means unknown." to "Cada columna es una muestra de una acción, no tiempo transcurrido. Desliza ↔ para ver las últimas 12. X significa desconocido.",
        "Next-state table" to "Tabla de estados siguientes",
        "Period" to "Período",
        "Nanosecond timeline · Q sampled at edges" to "Línea de tiempo en nanosegundos · Q se muestrea en los flancos",
        "The auto clock pulses this same ideal model; the diagram above grows live. Teaching delays only." to "El reloj automático pulsa este mismo modelo ideal; el diagrama de arriba se actualiza en directo. Los retardos son didácticos.",
        "A little extra." to "Un detalle más.",
        "Radix 7.0 · no ads, accounts or Internet permission" to "Radix 7.0 · sin anuncios, cuentas ni permiso de Internet",
        "Paste" to "Pegar",
        "Save" to "Guardar",
        "LIVE CONVERSIONS" to "CONVERSIONES EN DIRECTO",
        "Show the working" to "Mostrar procedimiento",
        "Add a custom base" to "Añadir base personalizada",
        "( ) = repeating digits · … = truncated at 24 places" to "( ) = dígitos periódicos · … = truncado tras 24 posiciones",
        "Tap Save on a conversion to keep it here.\nYour collection stays completely offline." to "Toca Guardar en una conversión para conservarla aquí.\nTu colección permanece completamente sin conexión.",
        "Open conversion  " to "Abrir conversión  ",
        "Surprise me" to "Sorpréndeme",
        "decimal" to "decimal",
        "Take it to the converter  " to "Abrir en el conversor  ",
        "A AND B — tap inputs to feel it" to "A AND B — toca las entradas y observa",
        "Repeating tails use a geometric series:" to "Las partes periódicas usan una serie geométrica:",
        "Restore the minus sign. This is signed magnitude, not fixed-width two's complement." to "Restablece el signo menos. Es signo-magnitud, no complemento a dos de ancho fijo.",
        "Load Σm / ΠM function" to "Cargar función Σm / ΠM",
        "Reset to 0" to "Restablecer a 0",
        "Only specified 0/1 cells must match. Xs impose no output requirement." to "Solo deben coincidir las celdas 0/1 especificadas. Las X no imponen una salida.",
        "F · CURRENT OUTPUT" to "F · SALIDA ACTUAL",
        "Σm · ON SET" to "Σm · CONJUNTO DE 1",
        "ΠM · OFF SET" to "ΠM · CONJUNTO DE 0",
        "SOP groups required 1s. A literal that changes within a group is removed; a fixed 0 is complemented and a fixed 1 is not." to "SOP agrupa los 1 necesarios. Se elimina el literal que cambia dentro del grupo; un 0 fijo se complementa y un 1 fijo no.",
        "No 1-cells: the minimum SOP is the constant 0." to "No hay celdas 1: la SOP mínima es la constante 0.",
        "POS groups required 0s. A fixed 0 stays uncomplemented; a fixed 1 is complemented in its sum clause. Opposite map edges are adjacent." to "POS agrupa los 0 necesarios. Un 0 fijo no se complementa; un 1 fijo se complementa en su suma. Los bordes opuestos del mapa son adyacentes.",
        "No 0-cells: the minimum POS is the constant 1." to "No hay celdas 0: la POS mínima es la constante 1.",
        "The exact minimizer covers every required cell, uses no forbidden cell, then minimizes term count and literal count. XNOR and NAND/NOR are already evaluated by the parser." to "El minimizador exacto cubre cada celda necesaria, evita las prohibidas y minimiza el número de términos y literales. El analizador ya evalúa XNOR y NAND/NOR.",
        "The map is unavailable until the expression is valid." to "El mapa no está disponible hasta que la expresión sea válida.",
        "K-map variables are shown in the expression's first-appearance order; the leftmost listed input is the most significant bit." to "Las variables del mapa siguen el orden en que aparecen por primera vez; la entrada más a la izquierda es el bit más significativo.",
        "Gate-level circuit · live" to "Circuito a nivel de puertas · en directo",
        "Saved on this device, restored with portable study backups, and capped at 50 items." to "Se guardan en este dispositivo, se incluyen en las copias de estudio y hay un límite de 50.",
        "Nothing saved yet. Solve an expression or K-map, then tap Save solution." to "Aún no hay nada guardado. Resuelve una expresión o mapa y toca Guardar solución.",
        "From digits to memory." to "De los dígitos a la memoria.",
        "Every conversion." to "Cada conversión.",
        "History records a value after you pause typing. Repeated adjacent entries are combined; this is separate from your hand-picked saved numbers." to "El historial guarda un valor cuando dejas de escribir. Las entradas repetidas consecutivas se agrupan; es distinto de los números guardados manualmente.",
        "Type a valid value in Convert and pause briefly. It will appear here with its source base." to "Escribe un valor válido en Convertir y espera un momento. Aparecerá aquí con su base de origen.",
        "Open Convert" to "Abrir Convertir",
        "Clear conversion history?" to "¿Borrar el historial de conversiones?",
        "This removes recent conversion entries only. Your separately saved numbers and all other study data stay unchanged." to "Solo se eliminarán las conversiones recientes. Los números guardados y los demás datos de estudio no cambiarán.",
        "Clear history" to "Borrar historial",
        "Keep history" to "Conservar historial",
        "Same bits." to "Los mismos bits.",
        "Encode or decode" to "Codificar o decodificar",
        "Two’s-complement interpretation" to "Interpretación en complemento a dos",
        "Off = unsigned decimal input/range" to "Desactivado = entrada/rango decimal sin signo",
        "One word, two interpretations" to "Una palabra, dos interpretaciones",
        "UNSIGNED" to "SIN SIGNO",
        "TWO’S COMPLEMENT" to "COMPLEMENTO A DOS",
        "HEX · SAME WORD" to "HEX · MISMA PALABRA",
        "One’s complement · invert every bit" to "Complemento a uno · invertir cada bit",
        "INVERTED WORD" to "PALABRA INVERTIDA",
        "ONE’S-COMPLEMENT VALUE" to "VALOR EN COMPLEMENTO A UNO",
        "One’s complement negates by flipping bits only — no +1. So zero has two faces: all-0s (+0) and all-1s (−0)." to "El complemento a uno niega invirtiendo los bits, sin sumar 1. Por eso el cero tiene dos formas: todos 0 (+0) y todos 1 (−0).",
        "Codes are not the same as binary" to "Los códigos no son lo mismo que el binario",
        "GRAY CODE OF THIS WORD" to "CÓDIGO GRAY DE ESTA PALABRA",
        "BCD OF THE SELECTED DECIMAL VALUE" to "BCD DEL VALOR DECIMAL ELEGIDO",
        "Gray changes one bit between consecutive non-negative counts. BCD encodes each decimal digit separately; unused BCD nibbles 1010–1111 are not decimal digits." to "Gray cambia un bit entre valores consecutivos no negativos. BCD codifica cada dígito decimal por separado; los nibbles 1010–1111 no son dígitos decimales válidos.",
        "This lab is fixed-width. The original Convert tab uses signed magnitude for negative values; it does not automatically mean two’s complement." to "Este laboratorio usa ancho fijo. La pestaña Convertir representa los negativos con signo y magnitud, no necesariamente en complemento a dos.",
        "Try the idea." to "Prueba la idea.",
        "Nice!" to "¡Bien!",
        "A correct check counts as mastered. Revealing an answer does not. Streaks celebrate practice days — they never punish a missed day." to "Una respuesta correcta queda dominada. Revelar una respuesta no cuenta. Las rachas celebran los días de práctica, sin castigar los descansos.",
        "Check my answer" to "Comprobar mi respuesta",
        "Reveal & explain" to "Revelar y explicar",
        "No cards combine this level with this topic — loosen a filter to continue." to "No hay preguntas para este nivel y tema; cambia un filtro para continuar.",
        "← Previous" to "← Anterior",
        "Next idea →" to "Siguiente idea →",
        "Subjective · short-answer checkpoint" to "Autoevaluación · respuesta breve",
        "Objective mode above checks a selected answer. Here, write your reasoning first, then compare it with a model solution; written responses are intentionally self-assessed, not automatically graded." to "El modo objetivo comprueba una opción. Aquí, escribe primero tu razonamiento y compáralo con una solución modelo; las respuestas escritas son autoevaluadas, no calificadas automáticamente.",
        "Your explanation or working" to "Tu explicación o procedimiento",
        "MODEL ANSWER" to "RESPUESTA MODELO",
        "Check both the result and the reasoning in your own words; compare the method, not just the final line." to "Comprueba el resultado y explica el razonamiento con tus palabras; compara el método, no solo la respuesta final.",
        "Reset practice progress" to "Restablecer progreso de práctica",
        "Reset mastered cards?" to "¿Restablecer las preguntas dominadas?",
        "Only practice progress is cleared. Circuits, conversions and K-maps stay unchanged." to "Solo se borra el progreso de práctica. Los circuitos, conversiones y mapas no cambian.",
        "Reset progress" to "Restablecer progreso",
        "Keep progress" to "Conservar progreso",
        "Save the state." to "Guarda el estado.",
        "Save a checkpoint from Logic. A saved session restores its model, inputs, clock and memory; its timing trace starts fresh. Up to 24 sessions stay on this device." to "Guarda un punto de control desde Lógica. La sesión restaura el modelo, las entradas, el reloj y la memoria; el trazado temporal comienza de nuevo. Se guardan hasta 24 sesiones.",
        "Your first checkpoint awaits" to "Tu primer punto de control te espera",
        "Open Logic, explore a circuit, then tap Save session." to "Abre Lógica, explora un circuito y toca Guardar sesión.",
        "Open Logic" to "Abrir Lógica",
        "Restore" to "Restaurar",
        "This removes the saved checkpoint, not the current circuit. Export a backup first if you want a copy." to "Esto elimina el punto guardado, no el circuito actual. Exporta una copia de seguridad si quieres conservarlo.",
        "Keep" to "Conservar",
        "Make it yours." to "Personalízalo.",
        "Comfort controls" to "Controles de comodidad",
        "Haptic feedback" to "Respuesta háptica",
        "Allow short native tap feedback" to "Permitir una breve respuesta táctil al tocar",
        "Larger learning controls" to "Controles de aprendizaje más grandes",
        "More room for circuit inputs and study actions; system font scaling is respected" to "Más espacio para entradas de circuitos y acciones de estudio; se respeta el tamaño de letra del sistema.",
        "Appearance" to "Apariencia",
        "Reminders & inbox" to "Recordatorios y bandeja",
        "Updates & version" to "Actualizaciones y versión",
        "Sync via your cloud drive" to "Sincronizar con tu unidad en la nube",
        "Radix runs no cloud of its own — instead, save the portable backup file straight into Google Drive, Dropbox or any folder the system picker offers. Reopen that same file on another device (or after reinstalling) to continue exactly where you stopped. Same validation, same privacy: the file is yours, unencrypted JSON, and Radix never uploads anything by itself." to "Radix no tiene una nube propia. Guarda la copia portátil en Google Drive, Dropbox o cualquier carpeta del selector del sistema. Abre el mismo archivo en otro dispositivo (o tras reinstalar) para continuar donde lo dejaste. El archivo JSON es tuyo, no está cifrado y Radix nunca lo sube por su cuenta.",
        "Save backup into Drive…" to "Guardar copia en Drive…",
        "Open backup from Drive…" to "Abrir copia desde Drive…",
        "Create a portable backup" to "Crear una copia portátil",
        "Save backup file" to "Guardar archivo de copia",
        "Copy backup text" to "Copiar texto de la copia",
        "Validate, then restore" to "Validar y restaurar",
        "Choose backup file" to "Elegir archivo de copia",
        "Or paste backup JSON" to "O pegar el JSON de la copia",
        "Validate backup" to "Validar copia",
        "Review replacement & restore" to "Revisar reemplazo y restaurar",
        "Replace local study data?" to "¿Reemplazar los datos de estudio locales?",
        "The validated backup REPLACES your saved conversions, sessions, progress, K-map, lab drafts and preferences. Export a backup of your current data first. The active circuit is not changed." to "La copia validada REEMPLAZA tus conversiones, sesiones, progreso, mapa, borradores y preferencias. Exporta primero una copia de los datos actuales. El circuito activo no cambia.",
        "Replace & restore" to "Reemplazar y restaurar",
        "Save a circuit checkpoint" to "Guardar punto de control del circuito",
        "Session name · up to 40 characters" to "Nombre de sesión · hasta 40 caracteres",
        "Current inputs, clock and stored memory are kept. The trace starts fresh on restore." to "Se conservan las entradas, el reloj y la memoria. El trazado comienza de nuevo al restaurar.",
        "Library is full (24). Back up/delete an older session first." to "La biblioteca está llena (24). Guarda o borra primero una sesión antigua.",
        "Save session" to "Guardar sesión",
        "A little help.\nA lot of aha." to "Un poco de ayuda.\nMuchos descubrimientos.",
        "Take a guided tour of the core tools. This built-in helper works offline. It is not a chat or network service. The app follows the system language when English, French, Spanish or Portuguese is supported; otherwise it uses English." to "Recorre las herramientas principales con esta guía integrada y sin conexión. No es un chat ni un servicio de red. La aplicación sigue el idioma del sistema cuando es inglés, francés, español o portugués; de lo contrario, usa inglés.",
        "Start the visual app tour" to "Iniciar recorrido visual",
        "Try" to "Probar",
        "→ Hint" to "→ Pista",
        "→ Check" to "→ Comprobar",
        "Undo ↶  •  Save  •  Restore ↷\nExport → Validate → Confirm" to "Deshacer ↶  •  Guardar  •  Restaurar ↷\nExportar → Validar → Confirmar",
        "Ready to explore" to "Listo para explorar",
        "Offline backup file saved." to "Archivo de copia guardado sin conexión.",
        "Backup loaded. Validate before restoring." to "Copia cargada. Valídala antes de restaurarla.",
        "Backup text copied; paste it somewhere private." to "Texto de la copia copiado; pégalo en un lugar privado.",
        "Validated study data restored." to "Datos de estudio validados restaurados.",
        "Circuit checkpoint saved." to "Punto de control del circuito guardado.",
        "Session library is full. Delete or back up an older checkpoint first." to "La biblioteca de sesiones está llena. Elimina o guarda primero un punto de control anterior.",
        "Complete truth/state table copied." to "Tabla completa de verdad y estados copiada.",
        "Visible truth-table page copied." to "Página visible de la tabla de verdad copiada.",
        "Truth table copied." to "Tabla de verdad copiada.",
        "Arithmetic working copied." to "Cálculo aritmético copiado.",
        "Float working copied." to "Cálculo IEEE-754 copiado.",
        "Representation working copied." to "Cálculo de representaciones copiado.",
        "K-map working copied." to "Procedimiento del mapa de Karnaugh copiado.",
        "Simplified expression copied." to "Expresión simplificada copiada.",
        "Boolean solution saved on this device and included in backups." to "Solución booleana guardada en este dispositivo e incluida en las copias.",
        "That solution is already saved; it was moved to the top." to "Esa solución ya está guardada; se movió al principio.",
        "K-map formula saved in Boolean solutions and backups." to "Fórmula del mapa de Karnaugh guardada en soluciones booleanas y copias de seguridad.",
        "That formula was already saved; it was moved to the top." to "Esa fórmula ya está guardada; se movió al principio.",
        "K-map solution shared as an image." to "Solución del mapa de Karnaugh compartida como imagen.",
        "Fix the expression before solving." to "Corrige la expresión antes de resolver.",
        "Enter expression G to verify equivalence." to "Escribe la expresión G para comprobar la equivalencia.",
        "Approximate value copied · 24 fractional places." to "Valor aproximado copiado · 24 posiciones decimales.",
        "Removed from saved numbers." to "Se quitó de los números guardados.",
        "All Boolean laws copied." to "Se copiaron todas las leyes booleanas.",
        "Pick a glowing output first." to "Selecciona primero una salida iluminada.",
        "Wire removed." to "Cable eliminado.",
        "Turn wiring mode ON, then tap this output." to "Activa el modo de cableado y toca esta salida.",
        "Saved expression opened." to "Se abrió la expresión guardada.",
        "Saved solution removed." to "Se quitó la solución guardada.",
        "Conversion restored from history." to "Se restauró la conversión del historial.",
        "History entry removed." to "Se quitó la entrada del historial.",
        "Conversion history cleared." to "Se borró el historial de conversiones.",
        "Session restored; fresh timing trace." to "Sesión restaurada; el registro temporal empieza de nuevo.",
        "Start from a working build" to "Empieza con un montaje funcional",
        "Gate palette · tap to place" to "Paleta de puertas · toca para colocar",
        "Finish the wiring" to "Completa el cableado",
        "Live simulation" to "Simulación en vivo",
        "Pulse an input · watch for glitches" to "Pulsa una entrada · observa los transitorios",
        "Your circuit's truth table" to "Tabla de verdad de tu circuito",
        "Your float" to "Tu número en coma flotante",
        "Household table · same device" to "Clasificación familiar · mismo dispositivo",
        "Selected" to "Seleccionada",
        "Not selected" to "No seleccionada",
        "Copy simplified expression" to "Copiar expresión simplificada",
        "SCORE CARD" to "TARJETA DE PUNTUACIÓN",
        "Radix · offline learning · no ads, no accounts" to "Radix · aprendizaje sin conexión · sin anuncios ni cuentas",
        "Share card" to "Compartir tarjeta",
        "cross-coupled nor gates" to "puertas NOR acopladas en cruz",
        "cross-coupled nand gates" to "puertas NAND acopladas en cruz",
        "MASTER · follows D while CLK=0" to "MAESTRO · sigue D mientras CLK=0",
        "SLAVE · copies master on ↑CLK" to "ESCLAVO · copia al maestro en ↑CLK",
        "master Q → slave D" to "Q maestro → D esclavo",
        "J and K are tied together: T=0 holds, T=1 toggles" to "J=K: T=0 mantiene, T=1 conmuta",
        "synchronous: every stage shares one clock edge" to "síncrono: todas las etapas comparten un flanco de reloj",
        "D in" to "Entrada D",
        "Q3 feeds back to D0" to "Q3 realimenta D0",
        "inverted Q3 feeds back to D0" to "Q3 invertido realimenta D0",
        "serial in → Q0 ··· Q3 out" to "Entrada serie → Q0 ··· salida Q3",
        "Tap a glowing output, then an input (or the probe)." to "Toca una salida iluminada y luego una entrada (o la sonda).",
        "Drag gates to arrange. Tap an input pin to unwire it." to "Arrastra las puertas para ordenarlas. Toca un pin de entrada para desconectarlo.",
        "⚡ Glitch: the probe flickers before settling." to "⚡ Transitorio: la sonda parpadea antes de estabilizarse.",
        "Clean: the probe changes once, then holds." to "Estable: la sonda cambia una vez y luego se mantiene.",
        "Single · 32-bit" to "Precisión simple · 32 bits",
        "Double · 64-bit" to "Precisión doble · 64 bits",
        "Decimal, inf, −inf or nan" to "Decimal, inf, −inf o nan",
        "Let's explore" to "Exploremos",
        "Each product term is an AND gate; their outputs feed the final OR." to "Cada término producto corresponde a una puerta AND; sus salidas alimentan la OR final.",
        "Each sum clause is an OR gate; their outputs feed the final AND." to "Cada cláusula suma corresponde a una puerta OR; sus salidas alimentan la AND final.",
        "✓ Correct. Here’s why." to "✓ Correcto. Aquí tienes la explicación.",
        "Not yet. Try the hint and check again." to "Todavía no. Prueba la pista y vuelve a comprobarlo.",
        "Hide hint" to "Ocultar pista",
        "Show a hint" to "Mostrar una pista",
        "Hide model answer" to "Ocultar respuesta modelo",
        "Compare with model answer" to "Comparar con la respuesta modelo",
        "Tap to select; values stay fixed." to "Toca para seleccionar; los valores no cambian.",
        "Tap: 0 → 1 → X → 0" to "Toca: 0 → 1 → X → 0",
        "Exit group check" to "Salir de la comprobación de grupos",
        "Check a group" to "Comprobar un grupo",
        "Tap to verify live" to "Toca para verificar en directo",
        "Mismatch — report this bug" to "No coincide: informa de este error",
        "Big ideas." to "Grandes ideas.",
        "THE DIGITAL LOGIC STUDIO" to "ESTUDIO DE LÓGICA DIGITAL",
        "Tap an input. Follow a wire. Understand the output." to "Pulsa una entrada. Sigue un cable. Comprende la salida.",
        "A lot to explore." to "Mucho por explorar.",
        "YOUR LEARNING TOOLBOX" to "TU CAJA DE HERRAMIENTAS DE APRENDIZAJE",
        "Everything offline. Everything within a tap." to "Todo sin conexión. Todo al alcance de un toque.",
        "Prove every line." to "Comprueba cada línea.",
        "THE BOOLEAN LAWS LAB" to "LABORATORIO DE LEYES BOOLEANAS",
        "Each law is verified live against every input row." to "Cada ley se verifica en directo con todas las combinaciones de entrada.",
        "Multiply. Divide." to "Multiplica. Divide.",
        "BASE ARITHMETIC, WITH WORKING" to "ARITMÉTICA EN BASE, CON PROCEDIMIENTO",
        "Exact integer math in any base from 2 to 36." to "Cálculo entero exacto en cualquier base del 2 al 36.",
        "Place gates." to "Coloca puertas.",
        "Wire the idea." to "Conecta la idea.",
        "THE CIRCUIT BUILDER" to "CONSTRUCTOR DE CIRCUITOS",
        "Drag, connect, simulate — feedback-free, always explainable." to "Arrastra, conecta y simula: cada resultado se explica paso a paso.",
        "Fractions fly." to "Las fracciones vuelan.",
        "Bits explain." to "Los bits lo explican.",
        "THE IEEE-754 FLOAT LAB" to "LABORATORIO DE PUNTO FLOTANTE IEEE-754",
        "Sign, exponent, fraction — every float, decoded." to "Signo, exponente y fracción: cada flotante, descodificado.",
        "Earn XP." to "Gana XP.",
        "Top the table." to "Llega a lo más alto.",
        "LEADERBOARD & REWARDS" to "CLASIFICACIÓN Y RECOMPENSAS",
        "One device, up to 8 players. Streaks never punish a missed day." to "Un dispositivo, hasta 8 jugadores. Las rachas no penalizan los días de descanso."
    )

    private val pt=mapOf(
        "Clear the canvas" to "Limpar a área de trabalho",
        "Copy table" to "Copiar tabela",
        "× remove" to "× remover",
        "Share score card" to "Compartilhar cartão de pontuação",
        "Remove" to "Remover",
        "New player name" to "Nome do novo jogador",
        "Laws, not luck." to "Leis, não sorte.",
        "NOTHING IS TAKEN ON FAITH HERE. Tap Verify and the app checks both sides on all 4 rows (2 variables) or all 8 rows (3 variables)." to "Aqui nada é aceito sem verificação. Toque em Verificar e o app compara os dois lados nas 4 linhas (2 variáveis) ou nas 8 linhas (3 variáveis).",
        "Copy all laws" to "Copiar todas as leis",
        "Notation: · is AND, + is OR, ⊕ is XOR, ⊙ is XNOR, ′ is NOT. Adjacency (AB) also means AND." to "Notação: · significa AND, + OR, ⊕ XOR, ⊙ XNOR e ′ NOT. A justaposição (AB) também significa AND.",
        "Add. Subtract." to "Somar. Subtrair.",
        "First number" to "Primeiro número",
        "Second number" to "Segundo número",
        "Copy full working" to "Copiar cálculo completo",
        "Denary · octal · hex · binary" to "Decimal · octal · hexadecimal · binário",
        "One table for the numbers 0–15. Read a row across to convert instantly." to "Uma tabela para os números de 0 a 15. Leia uma linha para converter na hora.",
        "Why octal and hex exist: 3 bits make one octal digit, 4 bits make one hex digit — so long binary strings stay readable." to "Por que existem o octal e o hexadecimal: 3 bits formam um dígito octal e 4 bits um dígito hexadecimal, mantendo legíveis as sequências binárias longas.",
        "Small circuits." to "Circuitos pequenos.",
        "Change ▾" to "Alterar ▾",
        "Ideal digital models, plus an optional teaching live-timing layer (fixed gate delays, glitch replay, auto clock). No electrical voltage, setup/hold violation or metastability simulation." to "Modelos digitais ideais, com uma camada temporal didática opcional (atrasos fixos, repetição de glitches e relógio automático). Não simula tensão, violação de setup/hold nem metastabilidade.",
        "Choose your next aha." to "Escolha sua próxima descoberta.",
        "Show current input row" to "Mostrar linha de entrada atual",
        "Simplify this gate in K-maps" to "Simplificar esta porta nos mapas de Karnaugh",
        "No memory: outputs follow the current inputs." to "Sem memória: as saídas acompanham as entradas atuais.",
        "Swipe ↔ to follow all gates. Crossing wires connect only at dotted pin junctions." to "Deslize ↔ para acompanhar as portas. Fios cruzados só se conectam nas junções pontilhadas.",
        "This circuit has more than six input variables; K-maps support up to six variables." to "Este circuito tem mais de seis variáveis de entrada; mapas de Karnaugh aceitam até seis.",
        "Inversion bubbles mean NOT; the extra curved input line identifies XOR/XNOR." to "As bolhas de inversão significam NOT; a linha de entrada curva extra identifica XOR/XNOR.",
        "Switch to phone-friendly block view" to "Mudar para a visualização em blocos, ideal para celular",
        "Explore the gate-level diagram" to "Explorar o diagrama em nível de portas",
        "Forbidden inputs force equal physical outputs. Stored memory is indeterminate; a valid set/reset recovers it." to "Entradas proibidas forçam saídas físicas iguais. A memória armazenada é indeterminada; um set/reset válido a recupera.",
        "Memory is indeterminate (X). Use a valid set/reset or capture a known D value to recover." to "A memória é indeterminada (X). Use um set/reset válido ou capture um valor D conhecido para recuperá-la.",
        "See inside the circuit" to "Ver por dentro do circuito",
        "Q(next) applies on a rising edge. At other times Q holds." to "Q(próximo) vale na borda de subida. Nos demais momentos, Q mantém o valor.",
        "Q(now)" to "Q(atual)",
        "Q(next)" to "Q(próximo)",
        "Status" to "Estado",
        "Level-sensitive: enabled inputs can change Q immediately." to "Sensível a nível: entradas habilitadas podem alterar Q imediatamente.",
        "⏸ Stop live clock" to "⏸ Parar relógio ao vivo",
        "▶ Live clock" to "▶ Relógio ao vivo",
        "↑ Rise" to "↑ Subida",
        "↓ Fall" to "↓ Descida",
        "Pulse" to "Pulso",
        "Inside view: grey nets are structure-only; colored nets carry live values. Crossing wires connect only at dotted pin junctions." to "Visão interna: redes cinzas mostram apenas a estrutura; redes coloridas transportam valores ao vivo. Fios cruzados só se conectam nas junções pontilhadas.",
        "Release both to hold" to "Soltar ambos para manter",
        "Models simultaneous release. Changing only one input first can recover a known set/reset state." to "Modela a liberação simultânea. Alterar primeiro apenas uma entrada pode recuperar um estado conhecido.",
        "Initialize simulation" to "Inicializar simulação",
        "Initialization sets a known teaching state; it does not model real hardware power-up." to "A inicialização define um estado didático conhecido; não representa a inicialização real do hardware.",
        "See memory over time" to "Ver a memória ao longo do tempo",
        "Each column is an action sample, not elapsed time. Swipe ↔ for the latest 12 samples. X means unknown." to "Cada coluna é uma amostra de ação, não tempo decorrido. Deslize ↔ para ver as 12 últimas. X significa desconhecido.",
        "Next-state table" to "Tabela de próximo estado",
        "Period" to "Período",
        "Nanosecond timeline · Q sampled at edges" to "Linha do tempo em nanossegundos · Q amostrado nas bordas",
        "The auto clock pulses this same ideal model; the diagram above grows live. Teaching delays only." to "O relógio automático pulsa este mesmo modelo ideal; o diagrama acima se atualiza ao vivo. Os atrasos são didáticos.",
        "A little extra." to "Um pouco mais.",
        "Radix 7.0 · no ads, accounts or Internet permission" to "Radix 7.0 · sem anúncios, contas ou permissão de Internet",
        "Paste" to "Colar",
        "Save" to "Salvar",
        "LIVE CONVERSIONS" to "CONVERSÕES AO VIVO",
        "Show the working" to "Mostrar o cálculo",
        "Add a custom base" to "Adicionar base personalizada",
        "( ) = repeating digits · … = truncated at 24 places" to "( ) = dígitos periódicos · … = truncado após 24 posições",
        "Tap Save on a conversion to keep it here.\nYour collection stays completely offline." to "Toque em Salvar numa conversão para guardá-la aqui.\nSua coleção permanece totalmente offline.",
        "Open conversion  " to "Abrir conversão  ",
        "Surprise me" to "Surpreenda-me",
        "decimal" to "decimal",
        "Take it to the converter  " to "Abrir no conversor  ",
        "A AND B — tap inputs to feel it" to "A AND B — toque nas entradas e veja",
        "Repeating tails use a geometric series:" to "As partes periódicas usam uma série geométrica:",
        "Restore the minus sign. This is signed magnitude, not fixed-width two's complement." to "Restaure o sinal de menos. Isto é sinal e magnitude, não complemento de dois de largura fixa.",
        "Load Σm / ΠM function" to "Carregar função Σm / ΠM",
        "Reset to 0" to "Redefinir para 0",
        "Only specified 0/1 cells must match. Xs impose no output requirement." to "Somente as células 0/1 especificadas precisam corresponder. X não exige uma saída.",
        "F · CURRENT OUTPUT" to "F · SAÍDA ATUAL",
        "Σm · ON SET" to "Σm · CONJUNTO DE 1",
        "ΠM · OFF SET" to "ΠM · CONJUNTO DE 0",
        "SOP groups required 1s. A literal that changes within a group is removed; a fixed 0 is complemented and a fixed 1 is not." to "SOP agrupa os 1 necessários. O literal que varia no grupo é removido; um 0 fixo é complementado e um 1 fixo não.",
        "No 1-cells: the minimum SOP is the constant 0." to "Sem células 1: a SOP mínima é a constante 0.",
        "POS groups required 0s. A fixed 0 stays uncomplemented; a fixed 1 is complemented in its sum clause. Opposite map edges are adjacent." to "POS agrupa os 0 necessários. Um 0 fixo não é complementado; um 1 fixo é complementado em sua soma. As bordas opostas do mapa são adjacentes.",
        "No 0-cells: the minimum POS is the constant 1." to "Sem células 0: a POS mínima é a constante 1.",
        "The exact minimizer covers every required cell, uses no forbidden cell, then minimizes term count and literal count. XNOR and NAND/NOR are already evaluated by the parser." to "O minimizador exato cobre todas as células necessárias, evita as proibidas e reduz o número de termos e literais. O analisador já calcula XNOR e NAND/NOR.",
        "The map is unavailable until the expression is valid." to "O mapa fica indisponível até a expressão ser válida.",
        "K-map variables are shown in the expression's first-appearance order; the leftmost listed input is the most significant bit." to "As variáveis do mapa seguem a primeira ordem de aparição na expressão; a entrada mais à esquerda é o bit mais significativo.",
        "Gate-level circuit · live" to "Circuito no nível de portas · ao vivo",
        "Saved on this device, restored with portable study backups, and capped at 50 items." to "Salvas neste dispositivo, incluídas nos backups e limitadas a 50 soluções.",
        "Nothing saved yet. Solve an expression or K-map, then tap Save solution." to "Nada salvo ainda. Resolva uma expressão ou mapa e toque em Salvar solução.",
        "From digits to memory." to "Dos dígitos à memória.",
        "Every conversion." to "Cada conversão.",
        "History records a value after you pause typing. Repeated adjacent entries are combined; this is separate from your hand-picked saved numbers." to "O histórico registra um valor após uma pausa na digitação. Entradas repetidas consecutivas são agrupadas; isso é separado dos números salvos manualmente.",
        "Type a valid value in Convert and pause briefly. It will appear here with its source base." to "Digite um valor válido em Converter e aguarde um instante. Ele aparecerá aqui com a base de origem.",
        "Open Convert" to "Abrir Converter",
        "Clear conversion history?" to "Limpar o histórico de conversões?",
        "This removes recent conversion entries only. Your separately saved numbers and all other study data stay unchanged." to "Somente as conversões recentes serão removidas. Os números salvos e os demais dados permanecerão iguais.",
        "Clear history" to "Limpar histórico",
        "Keep history" to "Manter histórico",
        "Same bits." to "Os mesmos bits.",
        "Encode or decode" to "Codificar ou decodificar",
        "Two’s-complement interpretation" to "Interpretação em complemento de dois",
        "Off = unsigned decimal input/range" to "Desativado = entrada/faixa decimal sem sinal",
        "One word, two interpretations" to "Uma palavra, duas interpretações",
        "UNSIGNED" to "SEM SINAL",
        "TWO’S COMPLEMENT" to "COMPLEMENTO DE DOIS",
        "HEX · SAME WORD" to "HEX · MESMA PALAVRA",
        "One’s complement · invert every bit" to "Complemento de um · inverter cada bit",
        "INVERTED WORD" to "PALAVRA INVERTIDA",
        "ONE’S-COMPLEMENT VALUE" to "VALOR EM COMPLEMENTO DE UM",
        "One’s complement negates by flipping bits only — no +1. So zero has two faces: all-0s (+0) and all-1s (−0)." to "O complemento de um nega invertendo apenas os bits, sem somar 1. Assim, o zero tem duas formas: todos 0 (+0) e todos 1 (−0).",
        "Codes are not the same as binary" to "Códigos não são o mesmo que binário",
        "GRAY CODE OF THIS WORD" to "CÓDIGO GRAY DESTA PALAVRA",
        "BCD OF THE SELECTED DECIMAL VALUE" to "BCD DO VALOR DECIMAL SELECIONADO",
        "Gray changes one bit between consecutive non-negative counts. BCD encodes each decimal digit separately; unused BCD nibbles 1010–1111 are not decimal digits." to "Gray muda um bit entre contagens consecutivas não negativas. BCD codifica cada dígito decimal separadamente; os nibbles 1010–1111 não são dígitos decimais válidos.",
        "This lab is fixed-width. The original Convert tab uses signed magnitude for negative values; it does not automatically mean two’s complement." to "Este laboratório usa largura fixa. A aba Converter representa negativos com sinal e magnitude, não automaticamente em complemento de dois.",
        "Try the idea." to "Experimente a ideia.",
        "Nice!" to "Boa!",
        "A correct check counts as mastered. Revealing an answer does not. Streaks celebrate practice days — they never punish a missed day." to "Uma resposta correta conta como dominada. Revelar a resposta não conta. Sequências celebram os dias de prática sem punir pausas.",
        "Check my answer" to "Conferir minha resposta",
        "Reveal & explain" to "Revelar e explicar",
        "No cards combine this level with this topic — loosen a filter to continue." to "Não há questões para este nível e tópico; ajuste um filtro para continuar.",
        "← Previous" to "← Anterior",
        "Next idea →" to "Próxima ideia →",
        "Subjective · short-answer checkpoint" to "Autoavaliação · resposta curta",
        "Objective mode above checks a selected answer. Here, write your reasoning first, then compare it with a model solution; written responses are intentionally self-assessed, not automatically graded." to "O modo objetivo confere uma opção. Aqui, escreva seu raciocínio primeiro e compare com uma solução modelo; respostas escritas são autoavaliadas, sem nota automática.",
        "Your explanation or working" to "Sua explicação ou cálculo",
        "MODEL ANSWER" to "RESPOSTA MODELO",
        "Check both the result and the reasoning in your own words; compare the method, not just the final line." to "Confira o resultado e explique o raciocínio com suas palavras; compare o método, não apenas a resposta final.",
        "Reset practice progress" to "Redefinir progresso de prática",
        "Reset mastered cards?" to "Redefinir questões dominadas?",
        "Only practice progress is cleared. Circuits, conversions and K-maps stay unchanged." to "Somente o progresso de prática será apagado. Circuitos, conversões e mapas permanecem iguais.",
        "Reset progress" to "Redefinir progresso",
        "Keep progress" to "Manter progresso",
        "Save the state." to "Salve o estado.",
        "Save a checkpoint from Logic. A saved session restores its model, inputs, clock and memory; its timing trace starts fresh. Up to 24 sessions stay on this device." to "Salve um ponto de restauração em Lógica. A sessão restaura modelo, entradas, relógio e memória; o traçado temporal recomeça. Até 24 sessões ficam neste dispositivo.",
        "Your first checkpoint awaits" to "Seu primeiro ponto de restauração está esperando",
        "Open Logic, explore a circuit, then tap Save session." to "Abra Lógica, explore um circuito e toque em Salvar sessão.",
        "Open Logic" to "Abrir Lógica",
        "Restore" to "Restaurar",
        "This removes the saved checkpoint, not the current circuit. Export a backup first if you want a copy." to "Isso remove o ponto salvo, não o circuito atual. Exporte um backup antes se quiser manter uma cópia.",
        "Keep" to "Manter",
        "Make it yours." to "Deixe do seu jeito.",
        "Comfort controls" to "Controles de conforto",
        "Haptic feedback" to "Resposta háptica",
        "Allow short native tap feedback" to "Permitir uma breve resposta tátil ao tocar",
        "Larger learning controls" to "Controles de aprendizagem maiores",
        "More room for circuit inputs and study actions; system font scaling is respected" to "Mais espaço para entradas de circuitos e ações de estudo; o tamanho da fonte do sistema é respeitado.",
        "Appearance" to "Aparência",
        "Reminders & inbox" to "Lembretes e caixa de entrada",
        "Updates & version" to "Atualizações e versão",
        "Sync via your cloud drive" to "Sincronizar pela sua nuvem",
        "Radix runs no cloud of its own — instead, save the portable backup file straight into Google Drive, Dropbox or any folder the system picker offers. Reopen that same file on another device (or after reinstalling) to continue exactly where you stopped. Same validation, same privacy: the file is yours, unencrypted JSON, and Radix never uploads anything by itself." to "O Radix não tem uma nuvem própria. Salve o backup portátil no Google Drive, Dropbox ou em qualquer pasta disponível no seletor do sistema. Abra o mesmo arquivo em outro dispositivo (ou após reinstalar) para continuar de onde parou. O JSON é seu, não é criptografado e o Radix nunca envia arquivos por conta própria.",
        "Save backup into Drive…" to "Salvar backup no Drive…",
        "Open backup from Drive…" to "Abrir backup do Drive…",
        "Create a portable backup" to "Criar um backup portátil",
        "Save backup file" to "Salvar arquivo de backup",
        "Copy backup text" to "Copiar texto do backup",
        "Validate, then restore" to "Validar e restaurar",
        "Choose backup file" to "Escolher arquivo de backup",
        "Or paste backup JSON" to "Ou colar o JSON do backup",
        "Validate backup" to "Validar backup",
        "Review replacement & restore" to "Revisar substituição e restaurar",
        "Replace local study data?" to "Substituir os dados locais de estudo?",
        "The validated backup REPLACES your saved conversions, sessions, progress, K-map, lab drafts and preferences. Export a backup of your current data first. The active circuit is not changed." to "O backup validado SUBSTITUI conversões, sessões, progresso, mapas, rascunhos e preferências salvos. Exporte primeiro um backup dos dados atuais. O circuito ativo não muda.",
        "Replace & restore" to "Substituir e restaurar",
        "Save a circuit checkpoint" to "Salvar ponto de restauração do circuito",
        "Session name · up to 40 characters" to "Nome da sessão · até 40 caracteres",
        "Current inputs, clock and stored memory are kept. The trace starts fresh on restore." to "Entradas, relógio e memória armazenada são mantidos. O traçado recomeça ao restaurar.",
        "Library is full (24). Back up/delete an older session first." to "A biblioteca está cheia (24). Faça backup ou exclua uma sessão antiga primeiro.",
        "Save session" to "Salvar sessão",
        "A little help.\nA lot of aha." to "Um pouco de ajuda.\nMuitas descobertas.",
        "Take a guided tour of the core tools. This built-in helper works offline. It is not a chat or network service. The app follows the system language when English, French, Spanish or Portuguese is supported; otherwise it uses English." to "Explore as ferramentas principais com esta ajuda integrada e offline. Ela não é um chat nem um serviço de rede. O app segue o idioma do sistema quando ele é inglês, francês, espanhol ou português; caso contrário, usa inglês.",
        "Start the visual app tour" to "Iniciar visita guiada do app",
        "Try" to "Tentar",
        "→ Hint" to "→ Dica",
        "→ Check" to "→ Conferir",
        "Undo ↶  •  Save  •  Restore ↷\nExport → Validate → Confirm" to "Desfazer ↶  •  Salvar  •  Restaurar ↷\nExportar → Validar → Confirmar",
        "Ready to explore" to "Pronto para explorar",
        "Offline backup file saved." to "Arquivo de backup offline salvo.",
        "Backup loaded. Validate before restoring." to "Backup carregado. Valide antes de restaurar.",
        "Backup text copied; paste it somewhere private." to "Texto do backup copiado; cole-o em um local privado.",
        "Validated study data restored." to "Dados de estudo validados restaurados.",
        "Circuit checkpoint saved." to "Ponto de retomada do circuito salvo.",
        "Session library is full. Delete or back up an older checkpoint first." to "A biblioteca de sessões está cheia. Exclua ou faça backup de um ponto de retomada anterior.",
        "Complete truth/state table copied." to "Tabela-verdade ou tabela de estados completa copiada.",
        "Visible truth-table page copied." to "Página visível da tabela-verdade copiada.",
        "Truth table copied." to "Tabela-verdade copiada.",
        "Arithmetic working copied." to "Cálculo aritmético copiado.",
        "Float working copied." to "Cálculo IEEE 754 copiado.",
        "Representation working copied." to "Cálculo de representações copiado.",
        "K-map working copied." to "Resolução do mapa de Karnaugh copiada.",
        "Simplified expression copied." to "Expressão simplificada copiada.",
        "Boolean solution saved on this device and included in backups." to "Solução booleana salva neste dispositivo e incluída nos backups.",
        "That solution is already saved; it was moved to the top." to "Essa solução já está salva; foi movida para o topo.",
        "K-map formula saved in Boolean solutions and backups." to "Fórmula do mapa de Karnaugh salva nas soluções booleanas e nos backups.",
        "That formula was already saved; it was moved to the top." to "Essa fórmula já está salva; foi movida para o topo.",
        "K-map solution shared as an image." to "Solução do mapa de Karnaugh compartilhada como imagem.",
        "Fix the expression before solving." to "Corrija a expressão antes de resolver.",
        "Enter expression G to verify equivalence." to "Digite a expressão G para verificar a equivalência.",
        "Approximate value copied · 24 fractional places." to "Valor aproximado copiado · 24 casas decimais.",
        "Removed from saved numbers." to "Removido dos números salvos.",
        "All Boolean laws copied." to "Todas as leis booleanas foram copiadas.",
        "Pick a glowing output first." to "Selecione primeiro uma saída iluminada.",
        "Wire removed." to "Fio removido.",
        "Turn wiring mode ON, then tap this output." to "Ative o modo de fiação e toque nesta saída.",
        "Saved expression opened." to "A expressão salva foi aberta.",
        "Saved solution removed." to "Solução salva removida.",
        "Conversion restored from history." to "Conversão restaurada do histórico.",
        "History entry removed." to "Entrada do histórico removida.",
        "Conversion history cleared." to "Histórico de conversões apagado.",
        "Session restored; fresh timing trace." to "Sessão restaurada; o registro temporal recomeça.",
        "Start from a working build" to "Comece com uma montagem funcional",
        "Gate palette · tap to place" to "Paleta de portas · toque para posicionar",
        "Finish the wiring" to "Conclua a fiação",
        "Live simulation" to "Simulação ao vivo",
        "Pulse an input · watch for glitches" to "Pulse uma entrada · observe os transitórios",
        "Your circuit's truth table" to "Tabela-verdade do seu circuito",
        "Your float" to "Seu número de ponto flutuante",
        "Household table · same device" to "Classificação familiar · mesmo dispositivo",
        "Selected" to "Selecionada",
        "Not selected" to "Não selecionada",
        "Copy simplified expression" to "Copiar expressão simplificada",
        "SCORE CARD" to "CARTÃO DE PONTUAÇÃO",
        "Radix · offline learning · no ads, no accounts" to "Radix · aprendizado offline · sem anúncios nem contas",
        "Share card" to "Compartilhar cartão",
        "cross-coupled nor gates" to "portas NOR com realimentação cruzada",
        "cross-coupled nand gates" to "portas NAND com realimentação cruzada",
        "MASTER · follows D while CLK=0" to "MESTRE · segue D enquanto CLK=0",
        "SLAVE · copies master on ↑CLK" to "ESCRAVO · copia o mestre na ↑CLK",
        "master Q → slave D" to "Q mestre → D escravo",
        "J and K are tied together: T=0 holds, T=1 toggles" to "J=K: T=0 mantém, T=1 alterna",
        "synchronous: every stage shares one clock edge" to "síncrono: todos os estágios compartilham uma borda de clock",
        "D in" to "Entrada D",
        "Q3 feeds back to D0" to "Q3 realimenta D0",
        "inverted Q3 feeds back to D0" to "Q3 invertido realimenta D0",
        "serial in → Q0 ··· Q3 out" to "Entrada serial → Q0 ··· saída Q3",
        "Tap a glowing output, then an input (or the probe)." to "Toque em uma saída iluminada e depois em uma entrada (ou na sonda).",
        "Drag gates to arrange. Tap an input pin to unwire it." to "Arraste as portas para organizá-las. Toque em um pino de entrada para desconectá-lo.",
        "⚡ Glitch: the probe flickers before settling." to "⚡ Transiente: a sonda oscila antes de estabilizar.",
        "Clean: the probe changes once, then holds." to "Estável: a sonda muda uma vez e então permanece.",
        "Single · 32-bit" to "Precisão simples · 32 bits",
        "Double · 64-bit" to "Precisão dupla · 64 bits",
        "Decimal, inf, −inf or nan" to "Decimal, inf, −inf ou nan",
        "Let's explore" to "Vamos explorar",
        "Each product term is an AND gate; their outputs feed the final OR." to "Cada termo produto corresponde a uma porta AND; as saídas alimentam a OR final.",
        "Each sum clause is an OR gate; their outputs feed the final AND." to "Cada cláusula soma corresponde a uma porta OR; as saídas alimentam a AND final.",
        "✓ Correct. Here’s why." to "✓ Correto. Veja por quê.",
        "Not yet. Try the hint and check again." to "Ainda não. Use a dica e confira novamente.",
        "Hide hint" to "Ocultar dica",
        "Show a hint" to "Mostrar uma dica",
        "Hide model answer" to "Ocultar resposta-modelo",
        "Compare with model answer" to "Comparar com a resposta-modelo",
        "Tap to select; values stay fixed." to "Toque para selecionar; os valores permanecem fixos.",
        "Tap: 0 → 1 → X → 0" to "Toque: 0 → 1 → X → 0",
        "Exit group check" to "Sair da verificação de grupo",
        "Check a group" to "Verificar um grupo",
        "Tap to verify live" to "Toque para verificar ao vivo",
        "Mismatch — report this bug" to "Resultado divergente — relate este erro",
        "Big ideas." to "Grandes ideias.",
        "THE DIGITAL LOGIC STUDIO" to "ESTÚDIO DE LÓGICA DIGITAL",
        "Tap an input. Follow a wire. Understand the output." to "Toque em uma entrada. Siga o fio. Entenda a saída.",
        "A lot to explore." to "Muito para explorar.",
        "YOUR LEARNING TOOLBOX" to "SEU KIT DE FERRAMENTAS DE APRENDIZAGEM",
        "Everything offline. Everything within a tap." to "Tudo offline. Tudo a um toque.",
        "Prove every line." to "Comprove cada linha.",
        "THE BOOLEAN LAWS LAB" to "LABORATÓRIO DE LEIS BOOLEANAS",
        "Each law is verified live against every input row." to "Cada lei é verificada ao vivo em todas as combinações de entrada.",
        "Multiply. Divide." to "Multiplique. Divida.",
        "BASE ARITHMETIC, WITH WORKING" to "ARITMÉTICA DE BASE, COM CÁLCULOS",
        "Exact integer math in any base from 2 to 36." to "Cálculo exato de inteiros em qualquer base, de 2 a 36.",
        "Place gates." to "Posicione as portas.",
        "Wire the idea." to "Conecte as ideias.",
        "THE CIRCUIT BUILDER" to "CONSTRUTOR DE CIRCUITOS",
        "Drag, connect, simulate — feedback-free, always explainable." to "Arraste, conecte e simule — cada resultado pode ser explicado.",
        "Fractions fly." to "Frações em movimento.",
        "Bits explain." to "Bits explicam.",
        "THE IEEE-754 FLOAT LAB" to "LABORATÓRIO DE PONTO FLUTUANTE IEEE-754",
        "Sign, exponent, fraction — every float, decoded." to "Sinal, expoente e fração — cada número flutuante, decodificado.",
        "Earn XP." to "Ganhe XP.",
        "Top the table." to "Suba no placar.",
        "LEADERBOARD & REWARDS" to "CLASSIFICAÇÃO E RECOMPENSAS",
        "One device, up to 8 players. Streaks never punish a missed day." to "Um dispositivo, até 8 jogadores. Sequências não punem dias de descanso."
    )

    fun translate(source:String,language:String):String? {
        val lang=language.lowercase()
        if(lang !in setOf("fr","es","pt"))return null
        val fixed=when(lang) {"fr"->fr[source];"es"->es[source];"pt"->pt[source];else->null}
        if(fixed!=null)return fixed
        val kmapHeader=Regex("^K-MAP SOLUTION · (SOP|POS)$").matchEntire(source)
        if(kmapHeader!=null)return when(lang) {"fr"->"SOLUTION DE CARTE DE KARNAUGH · ${kmapHeader.groupValues[1]}";"es"->"SOLUCIÓN DEL MAPA DE KARNAUGH · ${kmapHeader.groupValues[1]}";else->"SOLUÇÃO DO MAPA DE KARNAUGH · ${kmapHeader.groupValues[1]}"}
        val kmapIndices=Regex("^Indices 0–(\\d+) · A is MSB · Gray-coded axes$").matchEntire(source)
        if(kmapIndices!=null)return when(lang) {"fr"->"Indices 0–${kmapIndices.groupValues[1]} · A est le bit de poids fort · axes en code Gray";"es"->"Índices 0–${kmapIndices.groupValues[1]} · A es el bit más significativo · ejes en código Gray";else->"Índices 0–${kmapIndices.groupValues[1]} · A é o bit mais significativo · eixos em código Gray"}
        if(source=="start your streak")return when(lang) {"fr"->"Commencez votre série";"es"->"Empieza tu racha";"pt"->"Comece sua sequência";else->null}
        val allRows=Regex("^all (\\d+) rows$").matchEntire(source)
        if(allRows!=null)return when(lang) {"fr"->"toutes les ${allRows.groupValues[1]} lignes";"es"->"todas las ${allRows.groupValues[1]} filas";"pt"->"todas as ${allRows.groupValues[1]} linhas";else->null}
        val lawVerified=Regex("^✓ Holds in all (\\d+) rows$").matchEntire(source)
        if(lawVerified!=null)return when(lang) {"fr"->"✓ Vraie sur les ${lawVerified.groupValues[1]} lignes";"es"->"✓ Se cumple en las ${lawVerified.groupValues[1]} filas";"pt"->"✓ Válida nas ${lawVerified.groupValues[1]} linhas";else->null}
        val expressionRows=Regex("^Expression solved across all (\\d+) input rows\\.$").matchEntire(source)
        if(expressionRows!=null)return when(lang) {"fr"->"Expression résolue sur les ${expressionRows.groupValues[1]} lignes d’entrée.";"es"->"Expresión resuelta en las ${expressionRows.groupValues[1]} filas de entrada.";"pt"->"Expressão resolvida nas ${expressionRows.groupValues[1]} linhas de entrada.";else->null}
        val checkedRows=Regex("^Equivalence checked across all (\\d+) input rows\\.$").matchEntire(source)
        if(checkedRows!=null)return when(lang) {"fr"->"Équivalence vérifiée sur les ${checkedRows.groupValues[1]} lignes d’entrée.";"es"->"Equivalencia comprobada en las ${checkedRows.groupValues[1]} filas de entrada.";"pt"->"Equivalência verificada nas ${checkedRows.groupValues[1]} linhas de entrada.";else->null}
        val listError=Regex("^Fix the lists before solving: (.+)$").matchEntire(source)
        if(listError!=null){val detail=listError.groupValues[1];val translated=KMapContentTranslations.translate(detail,lang)?:detail;return when(lang) {"fr"->"Corrigez les listes avant de résoudre : $translated";"es"->"Corrige las listas antes de resolver: $translated";"pt"->"Corrija as listas antes de resolver: $translated";else->null}}
        val openedFunction=Regex("^(SOP|POS) function opened in K-maps\\.$").matchEntire(source)
        if(openedFunction!=null)return when(lang) {"fr"->"Fonction ${openedFunction.groupValues[1]} ouverte dans les cartes de Karnaugh.";"es"->"Función ${openedFunction.groupValues[1]} abierta en mapas de Karnaugh.";"pt"->"Função ${openedFunction.groupValues[1]} aberta nos mapas de Karnaugh.";else->null}
        val copiedLabel=Regex("^(.+) copied\\.$").matchEntire(source)?.groupValues?.get(1)
        if(copiedLabel!=null){
            val label=when(copiedLabel) {"derivation"->"derivation";"TSV truth table"->"table";else->null}
            if(label!=null)return when(lang) {"fr"->if(label=="derivation")"Dérivation copiée."else"Table de vérité TSV copiée.";"es"->if(label=="derivation")"Derivación copiada."else"Tabla de verdad TSV copiada.";"pt"->if(label=="derivation")"Derivação copiada."else"Tabela-verdade TSV copiada.";else->null}
        }
        val fractionHint=Regex("^(Digits 0–[0-9A-Z](?:, A–[0-9A-Z])?) · fractions welcome$").matchEntire(source)
        if(fractionHint!=null){val digits=CoreEngineTranslations.translate(fractionHint.groupValues[1],lang)?:fractionHint.groupValues[1];val tail=when(lang){"fr"->"fractions acceptées";"es"->"se aceptan fracciones";else->"frações permitidas"};return "$digits · $tail"}
        val copiedBase=Regex("^(Binary|Octal|Decimal|Hexadecimal|Base \\d+) copied\\.$").matchEntire(source)?.groupValues?.get(1)
        if(copiedBase!=null){
            if(copiedBase.startsWith("Base "))return when(lang) {"fr"->"$copiedBase copiée.";"es"->"$copiedBase copiada.";"pt"->"$copiedBase copiada.";else->null}
            val name=CoreEngineTranslations.translate(copiedBase,lang)?:copiedBase
            return when(lang) {"fr"->"$name copié.";"es"->"$name copiado.";"pt"->"$name copiado.";else->null}
        }
        val removedGate=Regex("^(AND|OR|NOT|NAND|NOR|XOR|XNOR|BUFFER) removed\\.$").matchEntire(source)
        if(removedGate!=null)return when(lang) {"fr"->"Porte ${removedGate.groupValues[1]} supprimée.";"es"->"Puerta ${removedGate.groupValues[1]} eliminada.";"pt"->"Porta ${removedGate.groupValues[1]} removida.";else->null}
        val inputCount=Regex("^(.+) · (\\d+) inputs$").matchEntire(source)
        if(inputCount!=null){val title=LogicCatalogTranslations.translate(inputCount.groupValues[1],lang)?:inputCount.groupValues[1];return when(lang) {"fr"->"$title · ${inputCount.groupValues[2]} entrées";"es"->"$title · ${inputCount.groupValues[2]} entradas";else->"$title · ${inputCount.groupValues[2]} entradas"}}
        val pageRows=Regex("^(.+) · rows (\\d+)–(\\d+)$").matchEntire(source)
        if(pageRows!=null){val title=LogicCatalogTranslations.translate(pageRows.groupValues[1],lang)?:pageRows.groupValues[1];return when(lang) {"fr"->"$title · lignes ${pageRows.groupValues[2]}–${pageRows.groupValues[3]}";"es"->"$title · filas ${pageRows.groupValues[2]}–${pageRows.groupValues[3]}";else->"$title · linhas ${pageRows.groupValues[2]}–${pageRows.groupValues[3]}"}}
        val clockMode=Regex("^(.+) · (on rising edge|level-sensitive)$").matchEntire(source)
        if(clockMode!=null){val title=LogicCatalogTranslations.translate(clockMode.groupValues[1],lang)?:clockMode.groupValues[1];val mode=when(clockMode.groupValues[2]) {"on rising edge"->when(lang){"fr"->"sur front montant";"es"->"en flanco ascendente";else->"na borda de subida"};else->when(lang){"fr"->"sensible au niveau";"es"->"sensible al nivel";else->"sensível ao nível"}};return "$title · $mode"}
        val widgetProgress=Regex("^(\\d+)/(\\d+) mastered$").matchEntire(source)
        if(widgetProgress!=null) {
            val done=widgetProgress.groupValues[1];val total=widgetProgress.groupValues[2]
            return when(lang) {"fr"->"$done/$total maîtrisées";"es"->"$done/$total dominadas";"pt"->"$done/$total dominados";else->null}
        }
        val widgetStreak=Regex("^🔥 (\\d+)-day streak$").matchEntire(source)
        if(widgetStreak!=null)return when(lang) {"fr"->"🔥 Série de ${widgetStreak.groupValues[1]} jours";"es"->"🔥 Racha de ${widgetStreak.groupValues[1]} días";"pt"->"🔥 Sequência de ${widgetStreak.groupValues[1]} dias";else->null}
        val pathProgress=Regex("^(.+) · (\\d+)/(\\d+) practice checks mastered$").matchEntire(source)
        if(pathProgress!=null){
            val categories=pathProgress.groupValues[1].split(" · ").joinToString(" · "){UiTranslationsStudy.translate(it,lang)?:it}
            val done=pathProgress.groupValues[2];val total=pathProgress.groupValues[3]
            return when(lang) {"fr"->"$categories · $done/$total exercices maîtrisés";"es"->"$categories · $done/$total ejercicios dominados";"pt"->"$categories · $done/$total verificações dominadas";else->null}
        }
        val mapExercise=Regex("^(\\d+) variables · (SOP|POS)( · don't-cares)?$").matchEntire(source)
        if(mapExercise!=null)return when(lang) {"fr"->"${mapExercise.groupValues[1]} variables · ${mapExercise.groupValues[2]}${if(mapExercise.groupValues[3].isNotEmpty())" · indifférences"else""}";"es"->"${mapExercise.groupValues[1]} variables · ${mapExercise.groupValues[2]}${if(mapExercise.groupValues[3].isNotEmpty())" · valores indiferentes"else""}";"pt"->"${mapExercise.groupValues[1]} variáveis · ${mapExercise.groupValues[2]}${if(mapExercise.groupValues[3].isNotEmpty())" · condições indiferentes"else""}";else->null}
        val groupCount=Regex("^(\\d+) (product terms|sum clauses) · (\\d+) literals · exact minimum$").matchEntire(source)
        if(groupCount!=null)return when(lang) {"fr"->"${groupCount.groupValues[1]} ${if(groupCount.groupValues[2]=="product terms")"termes produits"else"clauses sommes"} · ${groupCount.groupValues[3]} littéraux · minimum exact";"es"->"${groupCount.groupValues[1]} ${if(groupCount.groupValues[2]=="product terms")"términos producto"else"cláusulas suma"} · ${groupCount.groupValues[3]} literales · mínimo exacto";"pt"->"${groupCount.groupValues[1]} ${if(groupCount.groupValues[2]=="product terms")"termos produto"else"cláusulas soma"} · ${groupCount.groupValues[3]} literais · mínimo exato";else->null}
        val selectedCount=Regex("^(\\d+) selected$").matchEntire(source)
        if(selectedCount!=null)return when(lang) {"fr"->"${selectedCount.groupValues[1]} sélectionnées";"es"->"${selectedCount.groupValues[1]} seleccionadas";"pt"->"${selectedCount.groupValues[1]} selecionadas";else->null}
        val groupCells=Regex("^Cells (.+) · (\\d+) cells( · edge wrap)?( · essential)?$").matchEntire(source)
        if(groupCells!=null)return when(lang) {"fr"->"Cases ${groupCells.groupValues[1]} · ${groupCells.groupValues[2]} cases${if(groupCells.groupValues[3].isNotEmpty())" · bords raccordés"else""}${if(groupCells.groupValues[4].isNotEmpty())" · essentiel"else""}";"es"->"Celdas ${groupCells.groupValues[1]} · ${groupCells.groupValues[2]} celdas${if(groupCells.groupValues[3].isNotEmpty())" · bordes conectados"else""}${if(groupCells.groupValues[4].isNotEmpty())" · esencial"else""}";"pt"->"Células ${groupCells.groupValues[1]} · ${groupCells.groupValues[2]} células${if(groupCells.groupValues[3].isNotEmpty())" · bordas conectadas"else""}${if(groupCells.groupValues[4].isNotEmpty())" · essencial"else""}";else->null}
        val selectedPrime=Regex("^SELECTED PRIME (IMPLICANTS|IMPLICATES) · (\\d+)$").matchEntire(source)
        if(selectedPrime!=null)return when(lang) {"fr"->"${if(selectedPrime.groupValues[1]=="IMPLICANTS")"IMPLICANTS PREMIERS"else"IMPLICATES PREMIERS"} SÉLECTIONNÉS · ${selectedPrime.groupValues[2]}";"es"->"${if(selectedPrime.groupValues[1]=="IMPLICANTS")"IMPLICANTES PRIMOS"else"IMPLICANTES PRIMOS"} SELECCIONADOS · ${selectedPrime.groupValues[2]}";"pt"->"${if(selectedPrime.groupValues[1]=="IMPLICANTS")"IMPLICANTES PRIMOS"else"IMPLICADOS PRIMOS"} SELECIONADOS · ${selectedPrime.groupValues[2]}";else->null}
        val groupSize=Regex("^Select an adjacent power-of-two group \\(1–(\\d+) cells\\), then check it\\.$").matchEntire(source)
        if(groupSize!=null)return when(lang) {"fr"->"Sélectionnez un groupe adjacent de taille puissance de deux (1–${groupSize.groupValues[1]} cases), puis vérifiez-le.";"es"->"Selecciona un grupo adyacente de tamaño potencia de dos (1–${groupSize.groupValues[1]} celdas) y compruébalo.";"pt"->"Selecione um grupo adjacente com tamanho potência de dois (1–${groupSize.groupValues[1]} células) e confira.";else->null}
        val count=Regex("^(\\d+) of (\\d+) practice cards mastered$").matchEntire(source)
        if(count!=null) {
            val done=count.groupValues[1];val total=count.groupValues[2]
            return when(lang) {"fr"->"$done sur $total cartes d’entraînement maîtrisées";"es"->"$done de $total tarjetas de práctica dominadas";"pt"->"$done de $total cartões de prática dominados";else->null}
        }
        val answer=Regex("^Answer: (.*)$").matchEntire(source)
        if(answer!=null)return when(lang) {"fr"->"Réponse : ${answer.groupValues[1]}";"es"->"Respuesta: ${answer.groupValues[1]}";"pt"->"Resposta: ${answer.groupValues[1]}";else->null}
        val delete=Regex("^Delete (.+)\\?$").matchEntire(source)
        if(delete!=null)return when(lang) {"fr"->"Supprimer « ${delete.groupValues[1]} » ?";"es"->"¿Eliminar «${delete.groupValues[1]}»?";"pt"->"Excluir «${delete.groupValues[1]}»?";else->null}
        val deleteSaved=Regex("^Delete saved conversion (.+)$").matchEntire(source)
        if(deleteSaved!=null)return when(lang) {"fr"->"Supprimer la conversion enregistrée ${deleteSaved.groupValues[1]}";"es"->"Eliminar conversión guardada ${deleteSaved.groupValues[1]}";"pt"->"Excluir conversão salva ${deleteSaved.groupValues[1]}";else->null}
        val card=Regex("^Choose study card · (\\d+)/(\\d+)$").matchEntire(source)
        if(card!=null)return when(lang) {"fr"->"Choisir une question · ${card.groupValues[1]}/${card.groupValues[2]}";"es"->"Elegir pregunta · ${card.groupValues[1]}/${card.groupValues[2]}";"pt"->"Escolher questão · ${card.groupValues[1]}/${card.groupValues[2]}";else->null}
        val page=Regex("^Copy this page \\((\\d+) rows\\)$").matchEntire(source)
        if(page!=null)return when(lang) {"fr"->"Copier cette page (${page.groupValues[1]} lignes)";"es"->"Copiar esta página (${page.groupValues[1]} filas)";"pt"->"Copiar esta página (${page.groupValues[1]} linhas)";else->null}
        val output=Regex("^Open (.+) in K-maps$").matchEntire(source)
        if(output!=null)return when(lang) {"fr"->"Ouvrir ${output.groupValues[1]} dans les cartes de Karnaugh";"es"->"Abrir ${output.groupValues[1]} en mapas de Karnaugh";"pt"->"Abrir ${output.groupValues[1]} nos mapas de Karnaugh";else->null}
        val vars=Regex("^(\\d+) vars$").matchEntire(source)
        if(vars!=null)return when(lang) {"fr"->"${vars.groupValues[1]} variables";"es"->"${vars.groupValues[1]} variables";"pt"->"${vars.groupValues[1]} variáveis";else->null}
        val tour=Regex("^Visual app tour, step (\\d+) of (\\d+)$").matchEntire(source)
        if(tour!=null)return when(lang) {"fr"->"Visite visuelle de l’application, étape ${tour.groupValues[1]} sur ${tour.groupValues[2]}";"es"->"Recorrido visual de la aplicación, paso ${tour.groupValues[1]} de ${tour.groupValues[2]}";"pt"->"Visita visual do app, etapa ${tour.groupValues[1]} de ${tour.groupValues[2]}";else->null}
        val copyQuick=Regex("^Copy (.+) quick result$").matchEntire(source)
        if(copyQuick!=null){val base=CoreEngineTranslations.translate(copyQuick.groupValues[1],lang)?:copyQuick.groupValues[1];return when(lang) {"fr"->"Copier le résultat rapide $base";"es"->"Copiar el resultado rápido $base";"pt"->"Copiar o resultado rápido $base";else->null}}
        val copyResult=Regex("^Copy (.+) result$").matchEntire(source)
        if(copyResult!=null){val base=CoreEngineTranslations.translate(copyResult.groupValues[1],lang)?:copyResult.groupValues[1];return when(lang) {"fr"->"Copier le résultat $base";"es"->"Copiar resultado $base";"pt"->"Copiar resultado $base";else->null}}
        val wiring=Regex("^Wiring mode: (ON|OFF)$").matchEntire(source)
        if(wiring!=null)return when(lang) {"fr"->"Mode câblage : ${if(wiring.groupValues[1]=="ON")"ACTIVÉ"else"DÉSACTIVÉ"}";"es"->"Modo de cableado: ${if(wiring.groupValues[1]=="ON")"ACTIVADO"else"DESACTIVADO"}";"pt"->"Modo de fiação: ${if(wiring.groupValues[1]=="ON")"ATIVADO"else"DESATIVADO"}";else->null}
        val level=Regex("^Level (\\d+) (.+)$").matchEntire(source)
        if(level!=null)return when(lang) {"fr"->"Niveau ${level.groupValues[1]} · ${level.groupValues[2]}";"es"->"Nivel ${level.groupValues[1]} · ${level.groupValues[2]}";"pt"->"Nível ${level.groupValues[1]} · ${level.groupValues[2]}";else->null}
        val probe=Regex("^Probe Y = ([01])$").matchEntire(source)
        if(probe!=null)return when(lang) {"fr"->"Sonde Y = ${probe.groupValues[1]}";"es"->"Sonda Y = ${probe.groupValues[1]}";"pt"->"Sonda Y = ${probe.groupValues[1]}";else->null}
        val gateEvent=Regex("^([A-Z]+) #(\\d+) → ([01]) @ (\\d+) ns$").matchEntire(source)
        if(gateEvent!=null)return when(lang) {"fr"->"Porte ${gateEvent.groupValues[1]} #${gateEvent.groupValues[2]} → ${gateEvent.groupValues[3]} à ${gateEvent.groupValues[4]} ns";"es"->"Puerta ${gateEvent.groupValues[1]} #${gateEvent.groupValues[2]} → ${gateEvent.groupValues[3]} a los ${gateEvent.groupValues[4]} ns";"pt"->"Porta ${gateEvent.groupValues[1]} #${gateEvent.groupValues[2]} → ${gateEvent.groupValues[3]} a ${gateEvent.groupValues[4]} ns";else->null}
        val pulse=Regex("^Pulse ([A-D])$").matchEntire(source)
        if(pulse!=null)return when(lang) {"fr"->"Impulsion ${pulse.groupValues[1]}";"es"->"Pulso ${pulse.groupValues[1]}";"pt"->"Pulso ${pulse.groupValues[1]}";else->null}
        val settled=Regex("^Settles in (\\d+) ns · final Y = ([01])$").matchEntire(source)
        if(settled!=null)return when(lang) {"fr"->"Stabilisation en ${settled.groupValues[1]} ns · sortie finale Y = ${settled.groupValues[2]}";"es"->"Se estabiliza en ${settled.groupValues[1]} ns · salida final Y = ${settled.groupValues[2]}";"pt"->"Estabiliza em ${settled.groupValues[1]} ns · saída final Y = ${settled.groupValues[2]}";else->null}
        val event=Regex("^t=(\\d+) ns  (.+) → ([01])$").matchEntire(source)
        if(event!=null){
            val rawLabel=event.groupValues[2]
            val label=when {
                rawLabel=="Y"->when(lang) {"fr"->"sortie Y";"es"->"salida Y";else->"saída Y"}
                Regex("^(AND|OR|NOT|NAND|NOR|XOR|XNOR|BUFFER) #\\d+$").matches(rawLabel)->when(lang) {"fr"->"porte $rawLabel";"es"->"puerta $rawLabel";else->"porta $rawLabel"}
                else->return null
            }
            return when(lang) {"fr"->"t=${event.groupValues[1]} ns · $label → ${event.groupValues[3]}";"es"->"t=${event.groupValues[1]} ns · $label → ${event.groupValues[3]}";else->"t=${event.groupValues[1]} ns · $label → ${event.groupValues[3]}"}
        }
        val rowsOverInputs=Regex("^(\\d+) rows over ([A-D](?:, [A-D])*)$").matchEntire(source)
        if(rowsOverInputs!=null)return when(lang) {"fr"->"${rowsOverInputs.groupValues[1]} lignes pour les entrées ${rowsOverInputs.groupValues[2]}";"es"->"${rowsOverInputs.groupValues[1]} filas para las entradas ${rowsOverInputs.groupValues[2]}";"pt"->"${rowsOverInputs.groupValues[1]} linhas para as entradas ${rowsOverInputs.groupValues[2]}";else->null}
        val addPlayer=Regex("^Add player \\((\\d+)/(\\d+)\\)$").matchEntire(source)
        if(addPlayer!=null)return when(lang) {"fr"->"Ajouter un joueur (${addPlayer.groupValues[1]}/${addPlayer.groupValues[2]})";"es"->"Añadir jugador (${addPlayer.groupValues[1]}/${addPlayer.groupValues[2]})";"pt"->"Adicionar jogador (${addPlayer.groupValues[1]}/${addPlayer.groupValues[2]})";else->null}
        val saveSession=Regex("^Save session · (\\d+)/(\\d+)$").matchEntire(source)
        if(saveSession!=null)return when(lang) {"fr"->"Enregistrer la session · ${saveSession.groupValues[1]}/${saveSession.groupValues[2]}";"es"->"Guardar sesión · ${saveSession.groupValues[1]}/${saveSession.groupValues[2]}";"pt"->"Salvar sessão · ${saveSession.groupValues[1]}/${saveSession.groupValues[2]}";else->null}
        val inputLabel=Regex("^(\\d+) inputs$").matchEntire(source)
        if(inputLabel!=null)return when(lang) {"fr"->"${inputLabel.groupValues[1]} entrées";"es"->"${inputLabel.groupValues[1]} entradas";"pt"->"${inputLabel.groupValues[1]} entradas";else->null}
        val currentRow=Regex("^Show current input row \\((\\d+)\\)$").matchEntire(source)
        if(currentRow!=null)return when(lang) {"fr"->"Afficher la ligne d’entrée ${currentRow.groupValues[1]}";"es"->"Mostrar la fila de entrada ${currentRow.groupValues[1]}";"pt"->"Mostrar a linha de entrada ${currentRow.groupValues[1]}";else->null}
        val kVariables=Regex("^K-map variables: (.+)$").matchEntire(source)
        if(kVariables!=null)return when(lang) {"fr"->"Variables de la carte de Karnaugh : ${kVariables.groupValues[1]}";"es"->"Variables del mapa de Karnaugh: ${kVariables.groupValues[1]}";"pt"->"Variáveis do mapa de Karnaugh: ${kVariables.groupValues[1]}";else->null}
        val clockMemory=Regex("^CLK = ([01])  ·  memory (.+)$").matchEntire(source)
        if(clockMemory!=null)return when(lang) {"fr"->"HORLOGE = ${clockMemory.groupValues[1]}  ·  mémoire ${clockMemory.groupValues[2]}";"es"->"RELOJ = ${clockMemory.groupValues[1]}  ·  memoria ${clockMemory.groupValues[2]}";"pt"->"CLK = ${clockMemory.groupValues[1]}  ·  memória ${clockMemory.groupValues[2]}";else->null}
        val saveRows=Regex("^(\\d+) SAVED · UP TO (\\d+)$").matchEntire(source)
        if(saveRows!=null)return when(lang) {"fr"->"${saveRows.groupValues[1]} ENREGISTRÉS · JUSQU’À ${saveRows.groupValues[2]}";"es"->"${saveRows.groupValues[1]} GUARDADOS · HASTA ${saveRows.groupValues[2]}";"pt"->"${saveRows.groupValues[1]} SALVOS · ATÉ ${saveRows.groupValues[2]}";else->null}
        val customBase=Regex("^Custom base (\\d+) · change$").matchEntire(source)
        if(customBase!=null)return when(lang) {"fr"->"Base personnalisée ${customBase.groupValues[1]} · modifier";"es"->"Base personalizada ${customBase.groupValues[1]} · cambiar";"pt"->"Base personalizada ${customBase.groupValues[1]} · alterar";else->null}
        val baseEntry=Regex("^(.+) · base (\\d+)$").matchEntire(source)
        if(baseEntry!=null){val name=CoreEngineTranslations.translate(baseEntry.groupValues[1],lang)?:baseEntry.groupValues[1];return when(lang) {"fr"->"$name · base ${baseEntry.groupValues[2]}";"es"->"$name · base ${baseEntry.groupValues[2]}";"pt"->"$name · base ${baseEntry.groupValues[2]}";else->null}}
        val hexValue=Regex("^([0-9A-F]+) HEX$").matchEntire(source)
        if(hexValue!=null)return "${hexValue.groupValues[1]} HEX"
        val octalValue=Regex("^([0-7]+) OCT$").matchEntire(source)
        if(octalValue!=null)return "${octalValue.groupValues[1]} OCT"
        val uppercaseBase=Regex("^BASE (\\d+) · (BINARY|OCTAL|DECIMAL|HEXADECIMAL|BASE \\d+)$").matchEntire(source)
        if(uppercaseBase!=null){
            val name=when(uppercaseBase.groupValues[2]) {"BINARY"->when(lang) {"fr"->"BINAIRE";"es"->"BINARIO";else->"BINÁRIO"};"OCTAL"->"OCTAL";"DECIMAL"->when(lang) {"fr"->"DÉCIMAL";else->"DECIMAL"};"HEXADECIMAL"->when(lang) {"fr"->"HEXADÉCIMAL";else->"HEXADECIMAL"};else->uppercaseBase.groupValues[2]}
            return when(lang) {"fr"->"BASE ${uppercaseBase.groupValues[1]} · $name";"es"->"BASE ${uppercaseBase.groupValues[1]} · $name";"pt"->"BASE ${uppercaseBase.groupValues[1]} · $name";else->null}
        }
        val answerBase=Regex("^ANSWER · BASE (\\d+)$").matchEntire(source)
        if(answerBase!=null)return when(lang) {"fr"->"RÉSULTAT · BASE ${answerBase.groupValues[1]}";"es"->"RESPUESTA · BASE ${answerBase.groupValues[1]}";"pt"->"RESPOSTA · BASE ${answerBase.groupValues[1]}";else->null}
        val representationWidth=Regex("^(\\d+) bits$").matchEntire(source)
        if(representationWidth!=null)return when(lang) {"fr"->"${representationWidth.groupValues[1]} bits";"es"->"${representationWidth.groupValues[1]} bits";"pt"->"${representationWidth.groupValues[1]} bits";else->null}
        val binaryWord=Regex("^(\\d+)-BIT BINARY WORD$").matchEntire(source)
        if(binaryWord!=null)return when(lang) {"fr"->"MOT BINAIRE SUR ${binaryWord.groupValues[1]} BITS";"es"->"PALABRA BINARIA DE ${binaryWord.groupValues[1]} BITS";"pt"->"PALAVRA BINÁRIA DE ${binaryWord.groupValues[1]} BITS";else->null}
        val binaryDigits=Regex("^(\\d+) binary digits$").matchEntire(source)
        if(binaryDigits!=null)return when(lang) {"fr"->"${binaryDigits.groupValues[1]} chiffres binaires";"es"->"${binaryDigits.groupValues[1]} dígitos binarios";"pt"->"${binaryDigits.groupValues[1]} dígitos binários";else->null}
        val hexDigits=Regex("^(\\d+) hex digits$").matchEntire(source)
        if(hexDigits!=null)return when(lang) {"fr"->"${hexDigits.groupValues[1]} chiffres hexadécimaux";"es"->"${hexDigits.groupValues[1]} dígitos hexadecimales";"pt"->"${hexDigits.groupValues[1]} dígitos hexadecimais";else->null}
        val savedSolutions=Regex("^Saved solutions · (\\d+)$").matchEntire(source)
        if(savedSolutions!=null)return when(lang) {"fr"->"Solutions enregistrées · ${savedSolutions.groupValues[1]}";"es"->"Soluciones guardadas · ${savedSolutions.groupValues[1]}";"pt"->"Soluções salvas · ${savedSolutions.groupValues[1]}";else->null}
        val savedBooleanSolutions=Regex("^Saved Boolean solutions · (\\d+)$").matchEntire(source)
        if(savedBooleanSolutions!=null)return when(lang) {"fr"->"Solutions booléennes enregistrées · ${savedBooleanSolutions.groupValues[1]}";"es"->"Soluciones booleanas guardadas · ${savedBooleanSolutions.groupValues[1]}";"pt"->"Soluções booleanas salvas · ${savedBooleanSolutions.groupValues[1]}";else->null}
        val indexNote=Regex("^Indices 0–(\\d+)\\. A is the MSB\\. Duplicates are ignored; ranges are not supported\\.$").matchEntire(source)
        if(indexNote!=null)return when(lang) {"fr"->"Indices 0–${indexNote.groupValues[1]}. A est le bit de poids fort. Les doublons sont ignorés ; les plages ne sont pas prises en charge.";"es"->"Índices 0–${indexNote.groupValues[1]}. A es el bit más significativo. Se ignoran los duplicados; no se admiten rangos.";"pt"->"Índices 0–${indexNote.groupValues[1]}. A é o bit mais significativo. Duplicatas são ignoradas; intervalos não são aceitos.";else->null}
        val listSummary=Regex("^1s: (.+) · Xs: (.+)$").matchEntire(source)
        if(listSummary!=null){val ones=if(listSummary.groupValues[1]=="none")when(lang){"fr"->"aucun";"es"->"ninguno";else->"nenhum"}else listSummary.groupValues[1];val dc=if(listSummary.groupValues[2]=="none")when(lang){"fr"->"aucune";"es"->"ninguno";else->"nenhuma"}else listSummary.groupValues[2];return when(lang) {"fr"->"1 obligatoires : $ones · indifférences X : $dc";"es"->"1 obligatorios: $ones · condiciones indiferentes X: $dc";"pt"->"1 obrigatórios: $ones · condições indiferentes X: $dc";else->null}}
        val kMapHeader=Regex("^([A-Z]{2,6})   index   input   simplified$").matchEntire(source)
        if(kMapHeader!=null)return when(lang) {"fr"->"${kMapHeader.groupValues[1]}   indice   entrée   simplifiée";"es"->"${kMapHeader.groupValues[1]}   índice   entrada   simplificada";"pt"->"${kMapHeader.groupValues[1]}   índice   entrada   simplificada";else->null}
        val optionalCell=Regex("^(.*) \\(optional\\)$").matchEntire(source)
        if(optionalCell!=null)return when(lang) {"fr"->"${optionalCell.groupValues[1]} (facultatif)";"es"->"${optionalCell.groupValues[1]} (opcional)";"pt"->"${optionalCell.groupValues[1]} (opcional)";else->null}
        val canvas=Regex("^Canvas · (\\d+)/(\\d+) gates$").matchEntire(source)
        if(canvas!=null)return when(lang) {"fr"->"Zone de travail · ${canvas.groupValues[1]}/${canvas.groupValues[2]} portes";"es"->"Lienzo · ${canvas.groupValues[1]}/${canvas.groupValues[2]} puertas";"pt"->"Área de trabalho · ${canvas.groupValues[1]}/${canvas.groupValues[2]} portas";else->null}
        val recentActivity=Regex("^Recent activity · (\\d+)/100$").matchEntire(source)
        if(recentActivity!=null)return when(lang) {"fr"->"Activité récente · ${recentActivity.groupValues[1]}/100";"es"->"Actividad reciente · ${recentActivity.groupValues[1]}/100";"pt"->"Atividade recente · ${recentActivity.groupValues[1]}/100";else->null}
        val nowPlaying=Regex("^Now playing · (.*)$").matchEntire(source)
        if(nowPlaying!=null)return when(lang) {"fr"->"En cours · ${nowPlaying.groupValues[1]}";"es"->"En juego · ${nowPlaying.groupValues[1]}";"pt"->"Em jogo · ${nowPlaying.groupValues[1]}";else->null}
        val criticalPath=Regex("^Critical path: (\\d+) ns through (\\d+) gates?\\. Delays are teaching values \\(NOT 2 ns, NAND/NOR 3 ns, AND/OR 4 ns, XOR/XNOR 6 ns\\), not silicon measurements\\.$").matchEntire(source)
        if(criticalPath!=null){val count=criticalPath.groupValues[2];val plural=count!="1";return when(lang) {"fr"->"Chemin critique : ${criticalPath.groupValues[1]} ns à travers $count porte${if(plural)"s"else""}. Les délais sont pédagogiques (NOT 2 ns, NAND/NOR 3 ns, AND/OR 4 ns, XOR/XNOR 6 ns), pas des mesures du silicium.";"es"->"Ruta crítica: ${criticalPath.groupValues[1]} ns a través de $count puerta${if(plural)"s"else""}. Los retardos son didácticos (NOT 2 ns, NAND/NOR 3 ns, AND/OR 4 ns, XOR/XNOR 6 ns), no mediciones del silicio.";"pt"->"Caminho crítico: ${criticalPath.groupValues[1]} ns por $count porta${if(plural)"s"else""}. Os atrasos são didáticos (NOT 2 ns, NAND/NOR 3 ns, AND/OR 4 ns, XOR/XNOR 6 ns), não medições do silício.";else->null}}
        val parsedLive=Regex("^Parsed as (.+)\\. The live switches drive both the output and the gate-level diagram\\.$").matchEntire(source)
        if(parsedLive!=null)return when(lang) {"fr"->"Expression analysée : ${parsedLive.groupValues[1]}. Les interrupteurs agissent à la fois sur la sortie et sur le schéma des portes.";"es"->"Expresión interpretada: ${parsedLive.groupValues[1]}. Los interruptores controlan tanto la salida como el diagrama de puertas.";"pt"->"Expressão interpretada: ${parsedLive.groupValues[1]}. As chaves controlam a saída e o diagrama de portas.";else->null}
        val notEquivalent=Regex("^Not equivalent · (\\d+) counterexample rows?$").matchEntire(source)
        if(notEquivalent!=null){val count=notEquivalent.groupValues[1];val singular=count=="1";return when(lang) {"fr"->"Non équivalentes · $count ligne${if(singular)""else"s"} de contre-exemple";"es"->"No equivalentes · $count fila${if(singular)""else"s"} de contraejemplo";"pt"->"Não equivalentes · $count linha${if(singular)""else"s"} de contraexemplo";else->null}}
        val counterexamples=Regex("^Counterexamples \\(input order (.+)\\): (.*)$").matchEntire(source)
        if(counterexamples!=null)return when(lang) {"fr"->"Contre-exemples (ordre des entrées ${counterexamples.groupValues[1]}) : ${counterexamples.groupValues[2]}";"es"->"Contraejemplos (orden de entrada ${counterexamples.groupValues[1]}): ${counterexamples.groupValues[2]}";"pt"->"Contraexemplos (ordem das entradas ${counterexamples.groupValues[1]}): ${counterexamples.groupValues[2]}";else->null}
        val verifierRows=Regex("^The verifier compares all (\\d+) input combinations, not only the switches shown above\\.$").matchEntire(source)
        if(verifierRows!=null)return when(lang) {"fr"->"Le vérificateur compare les ${verifierRows.groupValues[1]} combinaisons d’entrées, pas seulement celles des interrupteurs affichés.";"es"->"El verificador compara las ${verifierRows.groupValues[1]} combinaciones de entrada, no solo las de los interruptores mostrados.";"pt"->"O verificador compara as ${verifierRows.groupValues[1]} combinações de entrada, não apenas as chaves exibidas.";else->null}
        val evaluateRows=Regex("^Evaluate all (\\d+) rows in binary order\\. Rows with output 1 become minterms; rows with output 0 become maxterms\\.$").matchEntire(source)
        if(evaluateRows!=null)return when(lang) {"fr"->"Évaluez les ${evaluateRows.groupValues[1]} lignes dans l’ordre binaire. Les sorties 1 deviennent des mintermes ; les sorties 0, des maxtermes.";"es"->"Evalúa las ${evaluateRows.groupValues[1]} filas en orden binario. Las salidas 1 son minterminos; las salidas 0, maxterminos.";"pt"->"Avalie as ${evaluateRows.groupValues[1]} linhas em ordem binária. Saídas 1 tornam-se mintermos; saídas 0, maxtermos.";else->null}
        val highlightedRows=Regex("^Highlighted row follows the live input switches\\. All (\\d+) rows remain available for copying\\.$").matchEntire(source)
        if(highlightedRows!=null)return when(lang) {"fr"->"La ligne mise en évidence suit les interrupteurs d’entrée. Les ${highlightedRows.groupValues[1]} lignes restent copiables.";"es"->"La fila resaltada sigue los interruptores de entrada. Las ${highlightedRows.groupValues[1]} filas se pueden copiar.";"pt"->"A linha destacada acompanha as chaves de entrada. As ${highlightedRows.groupValues[1]} linhas podem ser copiadas.";else->null}
        val noRequiredCells=Regex("^No required ([01])-cells; the constant answer needs no groups\\.$").matchEntire(source)
        if(noRequiredCells!=null)return when(lang) {"fr"->"Aucune case ${noRequiredCells.groupValues[1]} requise ; la constante ne nécessite aucun groupe.";"es"->"No se requieren celdas con ${noRequiredCells.groupValues[1]}; la constante no necesita grupos.";"pt"->"Nenhuma célula com ${noRequiredCells.groupValues[1]} é necessária; a constante não precisa de grupos.";else->null}
        val cellsSummary=Regex("^Cells (.+) · (\\d+) cells( · wraps an edge)?( · essential)?$").matchEntire(source)
        if(cellsSummary!=null){val wrap=cellsSummary.groupValues[3].isNotEmpty();val essential=cellsSummary.groupValues[4].isNotEmpty();return when(lang) {"fr"->"Cases ${cellsSummary.groupValues[1]} · ${cellsSummary.groupValues[2]} cases${if(wrap)" · les bords se rejoignent"else""}${if(essential)" · essentiel"else""}";"es"->"Celdas ${cellsSummary.groupValues[1]} · ${cellsSummary.groupValues[2]} celdas${if(wrap)" · los bordes se conectan"else""}${if(essential)" · esencial"else""}";"pt"->"Células ${cellsSummary.groupValues[1]} · ${cellsSummary.groupValues[2]} células${if(wrap)" · as bordas se conectam"else""}${if(essential)" · essencial"else""}";else->null}}
        val inputGateStatus=Regex("^Inputs: (.+)\\. Group gates update as the switches change\\.$").matchEntire(source)
        if(inputGateStatus!=null)return when(lang) {"fr"->"Entrées : ${inputGateStatus.groupValues[1]}. Les portes se mettent à jour avec les interrupteurs.";"es"->"Entradas: ${inputGateStatus.groupValues[1]}. Las puertas se actualizan al cambiar los interruptores.";"pt"->"Entradas: ${inputGateStatus.groupValues[1]}. As portas são atualizadas ao mudar as chaves.";else->null}
        val sessionSummary=Regex("^(.+) · (gates|combinational|sequential) · inputs ([01]+)(?: · Q=([01X]+) · CLK=([01]))?$").matchEntire(source)
        if(sessionSummary!=null){val kind=sessionSummary.groupValues[2];val rawModel=sessionSummary.groupValues[1];val model=when(kind){"gates"->LogicCatalog.gates.firstOrNull{it.gate.name.equals(rawModel,true)}?.gate?.name?:rawModel;"combinational"->LogicCatalog.circuits.firstOrNull{it.id==rawModel}?.let{LogicCatalogTranslations.translate(it.title,lang)?:it.title}?:rawModel;else->LogicCatalog.sequential.firstOrNull{it.id==rawModel}?.let{LogicCatalogTranslations.translate(it.title,lang)?:it.title}?:rawModel};val modelText=if(kind=="gates")model else model;val kindText=when(kind){"gates"->when(lang){"fr"->"portes";"es"->"puertas";else->"portas"};"combinational"->when(lang){"fr"->"combinatoire";"es"->"combinacional";else->"combinacional"};else->when(lang){"fr"->"séquentiel";"es"->"secuencial";else->"sequencial"}};val inputs=when(lang){"fr"->"entrées";"es"->"entradas";else->"entradas"};val clock=if(sessionSummary.groupValues[4].isNotEmpty())" · Q=${sessionSummary.groupValues[4]} · ${when(lang){"fr"->"HORLOGE";"es"->"RELOJ";else->"CLK"}}=${sessionSummary.groupValues[5]}"else"";return "$modelText · $kindText · $inputs ${sessionSummary.groupValues[3]}$clock"}
        val inputToggle=Regex("^Input ([A-Z]) ([01])\\. Tap to toggle\\.$").matchEntire(source)
        if(inputToggle!=null)return when(lang) {"fr"->"Entrée ${inputToggle.groupValues[1]}, ${inputToggle.groupValues[2]}. Touchez pour basculer.";"es"->"Entrada ${inputToggle.groupValues[1]}, ${inputToggle.groupValues[2]}. Toca para alternar.";"pt"->"Entrada ${inputToggle.groupValues[1]}, ${inputToggle.groupValues[2]}. Toque para alternar.";else->null}
        val externalToggle=Regex("^External input ([A-D]), ([01])\\. Tap to toggle\\.$").matchEntire(source)
        if(externalToggle!=null)return when(lang) {"fr"->"Entrée externe ${externalToggle.groupValues[1]}, ${externalToggle.groupValues[2]}. Touchez pour basculer.";"es"->"Entrada externa ${externalToggle.groupValues[1]}, ${externalToggle.groupValues[2]}. Toca para alternar.";"pt"->"Entrada externa ${externalToggle.groupValues[1]}, ${externalToggle.groupValues[2]}. Toque para alternar.";else->null}
        val externalState=Regex("^External input ([A-D]), ([01])$").matchEntire(source)
        if(externalState!=null)return when(lang) {"fr"->"Entrée externe ${externalState.groupValues[1]}, ${externalState.groupValues[2]}";"es"->"Entrada externa ${externalState.groupValues[1]}, ${externalState.groupValues[2]}";"pt"->"Entrada externa ${externalState.groupValues[1]}, ${externalState.groupValues[2]}";else->null}
        val pinInput=Regex("^([A-Z]+) input (\\d+), (wired|empty)$").matchEntire(source)
        if(pinInput!=null)return when(lang) {"fr"->"Entrée ${pinInput.groupValues[2]} de la porte ${pinInput.groupValues[1]} : ${if(pinInput.groupValues[3]=="wired")"connectée"else"libre"}";"es"->"Entrada ${pinInput.groupValues[2]} de la puerta ${pinInput.groupValues[1]}: ${if(pinInput.groupValues[3]=="wired")"conectada"else"vacía"}";"pt"->"Entrada ${pinInput.groupValues[2]} da porta ${pinInput.groupValues[1]}: ${if(pinInput.groupValues[3]=="wired")"conectada"else"vazia"}";else->null}
        val pinOutput=Regex("^([A-Z]+) output$").matchEntire(source)
        if(pinOutput!=null)return when(lang) {"fr"->"Sortie de la porte ${pinOutput.groupValues[1]}";"es"->"Salida de la puerta ${pinOutput.groupValues[1]}";"pt"->"Saída da porta ${pinOutput.groupValues[1]}";else->null}
        val outputLabel=Regex("^out = ([01])$").matchEntire(source)
        if(outputLabel!=null)return when(lang) {"fr"->"sortie = ${outputLabel.groupValues[1]}";"es"->"salida = ${outputLabel.groupValues[1]}";"pt"->"saída = ${outputLabel.groupValues[1]}";else->null}
        val guideHeading=Regex("^VISUAL HELPER · (\\d+)/(\\d+)$").matchEntire(source)
        if(guideHeading!=null)return when(lang) {"fr"->"AIDE VISUELLE · ${guideHeading.groupValues[1]}/${guideHeading.groupValues[2]}";"es"->"GUÍA VISUAL · ${guideHeading.groupValues[1]}/${guideHeading.groupValues[2]}";"pt"->"AJUDA VISUAL · ${guideHeading.groupValues[1]}/${guideHeading.groupValues[2]}";else->null}
        val shownRows=Regex("^(\\d+) ROWS · SHOWING 16 AT A TIME$").matchEntire(source)
        if(shownRows!=null)return when(lang) {"fr"->"${shownRows.groupValues[1]} LIGNES · 16 AFFICHÉES À LA FOIS";"es"->"${shownRows.groupValues[1]} FILAS · SE MUESTRAN 16 A LA VEZ";"pt"->"${shownRows.groupValues[1]} LINHAS · 16 EXIBIDAS POR VEZ";else->null}
        val outputProbe=Regex("^Output probe, ([01])$").matchEntire(source)
        if(outputProbe!=null)return when(lang) {"fr"->"Sonde de sortie, ${outputProbe.groupValues[1]}";"es"->"Sonda de salida, ${outputProbe.groupValues[1]}";"pt"->"Sonda de saída, ${outputProbe.groupValues[1]}";else->null}
        val mapCell=Regex("^(Minterm m|Maxterm M)(\\d+), (.+), output (don't-care|[01])\\. (Select group cell|Tap to cycle)$").matchEntire(source)
        if(mapCell!=null){val term=if(mapCell.groupValues[1].startsWith("Minterm"))when(lang){"fr"->"minterme m";"es"->"mintermino m";else->"mintermo m"}else when(lang){"fr"->"maxterme M";"es"->"maxtermino M";else->"maxtermo M"};val value=if(mapCell.groupValues[4]=="don't-care")when(lang){"fr"->"indifférence";"es"->"valor indiferente";else->"condição indiferente"}else mapCell.groupValues[4];val action=if(mapCell.groupValues[5]=="Select group cell")when(lang){"fr"->"Sélectionnez une case à grouper";"es"->"Selecciona la celda del grupo";else->"Selecione a célula do grupo"}else when(lang){"fr"->"Touchez pour changer la valeur";"es"->"Toca para cambiar el valor";else->"Toque para mudar o valor"};return when(lang) {"fr"->"$term${mapCell.groupValues[2]}, ${mapCell.groupValues[3]}, sortie ${value}. $action.";"es"->"$term${mapCell.groupValues[2]}, ${mapCell.groupValues[3]}, salida ${value}. $action.";"pt"->"$term${mapCell.groupValues[2]}, ${mapCell.groupValues[3]}, saída ${value}. $action.";else->null}}
        val baseResult=Regex("^(Binary|Octal|Decimal|Hexadecimal|Base \\d+): (.+)$").matchEntire(source)
        if(baseResult!=null){val rawName=baseResult.groupValues[1];val name=CoreEngineTranslations.translate(rawName,lang)?:rawName;val value=if(baseResult.groupValues[2]=="no result")when(lang){"fr"->"aucun résultat";"es"->"sin resultado";else->"sem resultado"}else baseResult.groupValues[2];return "$name : $value"}
        val gateDiagram=Regex("^([A-Z]+) gate\\. Inputs (\\[[01](?:, [01])*\\]); output ([01])\\.$").matchEntire(source)
        if(gateDiagram!=null)return when(lang) {"fr"->"Porte ${gateDiagram.groupValues[1]}. Entrées ${gateDiagram.groupValues[2]} ; sortie ${gateDiagram.groupValues[3]}.";"es"->"Puerta ${gateDiagram.groupValues[1]}. Entradas ${gateDiagram.groupValues[2]}; salida ${gateDiagram.groupValues[3]}.";"pt"->"Porta ${gateDiagram.groupValues[1]}. Entradas ${gateDiagram.groupValues[2]}; saída ${gateDiagram.groupValues[3]}.";else->null}
        val gateSchematic=Regex("^(.+) gate-level schematic\\. Crossings connect only at pin junction dots\\.$").matchEntire(source)
        if(gateSchematic!=null){val title=LogicCatalogTranslations.translate(gateSchematic.groupValues[1],lang)?:gateSchematic.groupValues[1];return when(lang) {"fr"->"Schéma de $title au niveau des portes. Les croisements ne sont reliés qu’aux points de jonction des broches.";"es"->"Esquema de $title a nivel de puertas. Los cruces solo se conectan en los puntos de unión de los pines.";"pt"->"Esquema de $title no nível das portas. Os cruzamentos só se conectam nos pontos de junção dos pinos.";else->null}}
        val blockDiagram=Regex("^(.+) block\\. (.+)$").matchEntire(source)
        if(blockDiagram!=null){val title=LogicCatalogTranslations.translate(blockDiagram.groupValues[1],lang)?:blockDiagram.groupValues[1];val unknown=when(lang){"fr"->"indéterminé";"es"->"desconocido";else->"desconhecido"};val outputs=Regex("([A-Za-z]+)(?=\\s*=)").replace(blockDiagram.groupValues[2].replace("unknown",unknown)){match->LogicCatalogTranslations.translateOutputName(match.value,lang)};return when(lang) {"fr"->"Bloc $title. Sorties : $outputs";"es"->"Bloque $title. Salidas: $outputs";"pt"->"Bloco $title. Saídas: $outputs";else->null}}
        val crossLatch=Regex("^Cross-coupled (NOR|NAND) latch\\. (nS|R) drives the Q gate\\.$").matchEntire(source)
        if(crossLatch!=null){val setInput=crossLatch.groupValues[2]=="nS";return when(lang) {"fr"->"Verrou ${crossLatch.groupValues[1]} à rétroaction croisée. ${if(setInput)"nS commande"else"R commande"} la porte Q.";"es"->"Latch ${crossLatch.groupValues[1]} acoplado en cruz. ${if(setInput)"nS controla"else"R controla"} la puerta Q.";"pt"->"Latch ${crossLatch.groupValues[1]} com realimentação cruzada. ${if(setInput)"nS aciona"else"R aciona"} a porta Q.";else->null}}
        if(source=="D latch internals: steering gates plus a cross-coupled NOR core.")return when(lang) {"fr"->"Intérieur du verrou D : portes de commande et cœur NOR à rétroaction croisée.";"es"->"Interior del latch D: puertas de control y núcleo NOR acoplado en cruz.";else->"Interior do latch D: portas de controle e núcleo NOR com realimentação cruzada."}
        if(source=="SR flip-flop internals: clocked AND gates feeding an SR NOR core.")return when(lang) {"fr"->"Intérieur de la bascule SR : des portes AND commandées par l’horloge alimentent un cœur SR à portes NOR.";"es"->"Interior del flip-flop SR: puertas AND sincronizadas alimentan un núcleo SR de puertas NOR.";else->"Interior do flip-flop SR: portas AND controladas pelo clock alimentam um núcleo SR NOR."}
        if(source=="D flip-flop internals: master latch and slave latch with a clock inverter.")return when(lang) {"fr"->"Intérieur de la bascule D : verrou maître, verrou esclave et inverseur d’horloge.";"es"->"Interior del flip-flop D: latch maestro, latch esclavo e inversor de reloj.";else->"Interior do flip-flop D: latch mestre, latch escravo e inversor de clock."}
        val jkT=Regex("^(JK|T) flip-flop internals: input steering with Q feedback into a clocked SR core\\.$").matchEntire(source)
        if(jkT!=null)return when(lang) {"fr"->"Intérieur de la bascule ${jkT.groupValues[1]} : commande des entrées avec rétroaction de Q vers un cœur SR cadencé.";"es"->"Interior del flip-flop ${jkT.groupValues[1]}: control de entradas con realimentación de Q hacia un núcleo SR sincronizado.";else->"Interior do flip-flop ${jkT.groupValues[1]}: controle das entradas com realimentação de Q para um núcleo SR controlado pelo clock."}
        if(source=="3-bit counter internals: ideal T stages with EN, clock and synchronous reset rails.")return when(lang) {"fr"->"Intérieur du compteur 3 bits : étages T idéaux avec lignes EN, horloge et remise à zéro synchrone.";"es"->"Interior del contador de 3 bits: etapas T ideales con señales EN, reloj y reinicio síncrono.";else->"Interior do contador de 3 bits: estágios T ideais com sinais EN, clock e reset síncrono."}
        val wideCounter=Regex("^4-bit (.+) internals: ideal D stages with shared clock, enable and reset\\.$").matchEntire(source)
        if(wideCounter!=null){val kind=when(wideCounter.groupValues[1]) {"ring counter"->when(lang){"fr"->"compteur en anneau";"es"->"contador en anillo";else->"contador em anel"};"Johnson counter"->"compteur Johnson";else->wideCounter.groupValues[1]};return when(lang) {"fr"->"Intérieur du $kind 4 bits : étages D idéaux avec horloge, activation et remise à zéro communes.";"es"->"Interior del $kind de 4 bits: etapas D ideales con reloj, habilitación y reinicio compartidos.";else->"Interior do $kind de 4 bits: estágios D ideais com clock, habilitação e reset compartilhados."}}
        val stage=Regex("^([TD]) stage Q(\\d+)$").matchEntire(source)
        if(stage!=null){val register=stage.groupValues[1];val index=stage.groupValues[2];return when(lang) {"fr"->"Étage $register Q$index";"es"->"Etapa $register Q$index";else->"Estágio $register Q$index"}}
        if(source=="Discrete action timing trace, not real time. Unknown states are marked X.")return when(lang) {"fr"->"Chronologie discrète des actions, pas du temps réel. Les états inconnus sont marqués X.";"es"->"Traza temporal de acciones discretas, no tiempo real. Los estados desconocidos se marcan con X.";else->"Traçado temporal de ações discretas, não tempo real. Estados desconhecidos são marcados com X."}
        return null
    }
}
