package com.radix.app

/** K-map lesson catalog and solver feedback. Algebraic expressions remain language-neutral. */
internal object KMapContentTranslations {
    private data class Text3(val fr:String,val es:String,val pt:String)
    private val entries=mapOf(
        "Overlapping pairs" to Text3("Paires qui se chevauchent","Pares superpuestos","Pares sobrepostos"),
        "Pairs may share a cell. Cover every 1." to Text3("Les paires peuvent partager une case. Couvrez chaque 1.","Los pares pueden compartir una celda. Cubre todos los 1.","Os pares podem compartilhar uma célula. Cubra todos os 1."),
        "Overlap is allowed; m3 belongs to both useful pairs." to Text3("Le chevauchement est autorisé ; m3 appartient aux deux paires utiles.","Se permite superponer; m3 pertenece a los dos pares útiles.","A sobreposição é permitida; m3 pertence aos dois pares úteis."),
        "One isolated 1" to Text3("Un 1 isolé","Un 1 aislado","Um 1 isolado"),
        "A group of one is legal when it cannot expand." to Text3("Un groupe d’une case est valide s’il ne peut pas être agrandi.","Un grupo de una celda es válido si no puede ampliarse.","Um grupo de uma célula é válido quando não pode ser ampliado."),
        "Both variables stay 1, giving AB." to Text3("Les deux variables restent à 1, ce qui donne AB.","Ambas variables permanecen en 1, dando AB.","As duas variáveis permanecem em 1, resultando em AB."),
        "Do not group diagonals" to Text3("Ne regroupez pas les diagonales","No agrupes diagonales","Não agrupe diagonais"),
        "The two 1s differ in two bits." to Text3("Les deux 1 diffèrent sur deux bits.","Los dos 1 difieren en dos bits.","Os dois 1 diferem em dois bits."),
        "Diagonal cells do not touch. Keep two one-cell terms." to Text3("Les cases diagonales ne sont pas voisines. Gardez deux termes d’une case.","Las celdas diagonales no se tocan. Mantén dos términos de una celda.","Células diagonais não são adjacentes. Mantenha dois termos de uma célula."),
        "The other diagonal" to Text3("L’autre diagonale","La otra diagonal","A outra diagonal"),
        "Corners in a 2×2 map are not all mutually adjacent." to Text3("Dans une carte 2×2, les coins ne sont pas tous voisins entre eux.","En un mapa 2×2, las esquinas no son todas adyacentes entre sí.","Em um mapa 2×2, os cantos não são todos adjacentes entre si."),
        "A two-cell diagonal is not a Gray-code rectangle." to Text3("Une diagonale de deux cases ne forme pas un rectangle en code Gray.","Una diagonal de dos celdas no forma un rectángulo de código Gray.","Uma diagonal de duas células não forma um retângulo de código Gray."),
        "Drop a changing variable" to Text3("Éliminez la variable qui change","Elimina la variable que cambia","Elimine a variável que muda"),
        "A stays 0; B changes." to Text3("A reste à 0 ; B change.","A permanece en 0; B cambia.","A permanece em 0; B muda."),
        "The changing B disappears; A=0 contributes A'." to Text3("B, qui change, disparaît ; A=0 apporte A′.","B, que cambia, desaparece; A=0 aporta A′.","B, que muda, desaparece; A=0 contribui com A′."),
        "The constant-one map" to Text3("La carte entièrement à 1","El mapa todo en 1","O mapa todo em 1"),
        "Use the whole map as one group." to Text3("Utilisez toute la carte comme un seul groupe.","Usa todo el mapa como un grupo.","Use o mapa inteiro como um grupo."),
        "All variables change, leaving F=1." to Text3("Toutes les variables changent ; il reste F=1.","Todas las variables cambian; queda F=1.","Todas as variáveis mudam, restando F=1."),
        "No required 1s" to Text3("Aucun 1 requis","Sin 1 obligatorios","Nenhum 1 obrigatório"),
        "A SOP needs no products when there are no 1s." to Text3("En SOP, aucun produit n’est nécessaire s’il n’y a pas de 1.","En SOP no se necesitan productos si no hay 1.","Em SOP, não são necessários produtos quando não há 1."),
        "The simplest function is F=0." to Text3("La fonction la plus simple est F=0.","La función más sencilla es F=0.","A função mais simples é F=0."),
        "Use a helpful don't-care" to Text3("Utilisez une indifférence utile","Aprovecha un valor indiferente","Use um valor indiferente útil"),
        "An X can enlarge the group, but need not be used." to Text3("Un X peut agrandir le groupe, mais son utilisation n’est pas obligatoire.","Una X puede ampliar el grupo, pero no es obligatorio usarla.","Um X pode ampliar o grupo, mas não precisa ser usado."),
        "The pair m1,m3 simplifies to B." to Text3("La paire m1,m3 se simplifie en B.","El par m1,m3 se simplifica a B.","O par m1,m3 simplifica para B."),
        "Three-variable edge wrap" to Text3("Raccordement des bords à trois variables","Conexión de bordes con tres variables","Conexão das bordas com três variáveis"),
        "Columns 00 and 10 are neighbors across the edge." to Text3("Les colonnes 00 et 10 sont voisines de part et d’autre du bord.","Las columnas 00 y 10 son vecinas a través del borde.","As colunas 00 e 10 são vizinhas através da borda."),
        "The outer pair gives A'C'." to Text3("La paire extérieure donne A′C′.","El par exterior da A′C′.","O par externo resulta em A′C′."),
        "A whole row" to Text3("Une ligne entière","Una fila completa","Uma linha inteira"),
        "B and C both vary within this row." to Text3("B et C varient toutes deux dans cette ligne.","B y C varían en esta fila.","B e C variam nesta linha."),
        "Only A=0 remains: A'." to Text3("Seul A=0 reste : A′.","Solo queda A=0: A′.","Só resta A=0: A′."),
        "An odd-index pattern" to Text3("Un motif d’indices impairs","Un patrón de índices impares","Um padrão de índices ímpares"),
        "Read binary indices; C is the least significant bit." to Text3("Lisez les indices binaires ; C est le bit de poids faible.","Lee los índices binarios; C es el bit menos significativo.","Leia os índices binários; C é o bit menos significativo."),
        "C stays 1, so the answer is C." to Text3("C reste à 1 ; la réponse est donc C.","C permanece en 1, así que la respuesta es C.","C permanece em 1, então a resposta é C."),
        "Three is not a group size" to Text3("Trois n’est pas une taille de groupe valide","Tres no es un tamaño de grupo válido","Três não é um tamanho de grupo válido"),
        "Cover three 1s using overlapping power-of-two groups." to Text3("Couvrez les trois 1 avec des groupes de puissances de deux qui se chevauchent.","Cubre los tres 1 con grupos superpuestos de tamaño potencia de dos.","Cubra os três 1 com grupos sobrepostos de tamanho potência de dois."),
        "Use two pairs; never draw a group of three." to Text3("Utilisez deux paires ; ne formez jamais un groupe de trois.","Usa dos pares; nunca formes un grupo de tres.","Use dois pares; nunca forme um grupo de três."),
        "No adjacent 1s" to Text3("Aucun 1 adjacent","Sin 1 adyacentes","Nenhum 1 adjacente"),
        "Every possible pair would include a 0." to Text3("Chaque paire possible contiendrait un 0.","Cualquier par posible incluiría un 0.","Todo par possível incluiria um 0."),
        "Four isolated cells need four product terms." to Text3("Quatre cases isolées nécessitent quatre termes produits.","Cuatro celdas aisladas requieren cuatro términos producto.","Quatro células isoladas precisam de quatro termos produto."),
        "Grow to four with Xs" to Text3("Agrandissez à quatre avec les X","Amplía a cuatro con las X","Amplie para quatro usando X"),
        "The two Xs can complete the C=1 group." to Text3("Les deux X peuvent compléter le groupe C=1.","Las dos X pueden completar el grupo C=1.","Os dois X podem completar o grupo C=1."),
        "C alone covers the required 1s without covering any required 0." to Text3("C seul couvre les 1 requis sans couvrir aucun 0 requis.","C por sí sola cubre los 1 obligatorios sin cubrir ningún 0 obligatorio.","C sozinho cobre os 1 obrigatórios sem cobrir nenhum 0 obrigatório."),
        "Four corners are adjacent" to Text3("Les quatre coins sont voisins","Las cuatro esquinas son adyacentes","Os quatro cantos são adjacentes"),
        "Wrap horizontally and vertically at the same time." to Text3("Le raccordement se fait simultanément à l’horizontale et à la verticale.","Conecta los bordes horizontal y vertical al mismo tiempo.","Conecte as bordas horizontal e vertical ao mesmo tempo."),
        "The four corners give B'D', not four separate terms." to Text3("Les quatre coins donnent B′D′, et non quatre termes séparés.","Las cuatro esquinas dan B′D′, no cuatro términos separados.","Os quatro cantos resultam em B′D′, não em quatro termos separados."),
        "An eight-cell group" to Text3("Un groupe de huit cases","Un grupo de ocho celdas","Um grupo de oito células"),
        "Three variables change; A stays 0." to Text3("Trois variables changent ; A reste à 0.","Tres variables cambian; A permanece en 0.","Três variáveis mudam; A permanece em 0."),
        "The largest group is eight cells, giving A'." to Text3("Le plus grand groupe compte huit cases et donne A′.","El grupo más grande tiene ocho celdas y da A′.","O maior grupo tem oito células, resultando em A′."),
        "Read Gray-code rows" to Text3("Lisez les lignes en code Gray","Lee las filas de código Gray","Leia as linhas em código Gray"),
        "Row AB=01 contains these four minterms." to Text3("La ligne AB=01 contient ces quatre mintermes.","La fila AB=01 contiene estos cuatro minterms.","A linha AB=01 contém estes quatro mintermos."),
        "CD vary; AB=01 leaves A'B." to Text3("CD varient ; AB=01 laisse A′B.","CD varían; AB=01 deja A′B.","CD variam; AB=01 resulta em A′B."),
        "Four Gray-code rows" to Text3("Quatre lignes en code Gray","Cuatro filas de código Gray","Quatro linhas em código Gray"),
        "Column CD=01 spans all rows." to Text3("La colonne CD=01 s’étend sur toutes les lignes.","La columna CD=01 abarca todas las filas.","A coluna CD=01 percorre todas as linhas."),
        "A and B vanish, leaving C'D." to Text3("A et B disparaissent ; il reste C′D.","A y B desaparecen; queda C′D.","A e B desaparecem, restando C′D."),
        "Three overlapping groups" to Text3("Trois groupes qui se chevauchent","Tres grupos superpuestos","Três grupos sobrepostos"),
        "A cell may be used in several maximal groups." to Text3("Une case peut appartenir à plusieurs groupes maximaux.","Una celda puede pertenecer a varios grupos máximos.","Uma célula pode pertencer a vários grupos máximos."),
        "Overlapping four-cell groups cover all 1s with three terms." to Text3("Trois groupes de quatre cases qui se chevauchent couvrent tous les 1.","Grupos superpuestos de cuatro celdas cubren todos los 1 con tres términos.","Grupos sobrepostos de quatro células cobrem todos os 1 com três termos."),
        "An X-assisted octet" to Text3("Un octet complété par des X","Un octeto ampliado con X","Um octeto ampliado com X"),
        "Don't-cares complete all odd-index cells." to Text3("Les indifférences complètent toutes les cases d’indice impair.","Los valores indiferentes completan todas las celdas de índice impar.","Os valores indiferentes completam todas as células de índice ímpar."),
        "Use the eight-cell D=1 group." to Text3("Utilisez le groupe de huit cases où D=1.","Usa el grupo de ocho celdas con D=1.","Use o grupo de oito células em que D=1."),
        "POS groups zeroes" to Text3("En POS, regroupez les zéros","En POS se agrupan los ceros","Em POS, agrupe os zeros"),
        "Group the lone 0, not the three 1s." to Text3("Regroupez le 0 isolé, pas les trois 1.","Agrupa el 0 aislado, no los tres 1.","Agrupe o 0 isolado, não os três 1."),
        "A=0 and B=0 produce the sum clause (A+B)." to Text3("A=0 et B=0 donnent la clause somme (A+B).","A=0 y B=0 producen la cláusula suma (A+B).","A=0 e B=0 produzem a cláusula soma (A+B)."),
        "POS complement rule" to Text3("Règle de complément en POS","Regla del complemento en POS","Regra do complemento em POS"),
        "B stays 1 in the zero group." to Text3("B reste à 1 dans le groupe de zéros.","B permanece en 1 en el grupo de ceros.","B permanece em 1 no grupo de zeros."),
        "For POS, fixed 1 becomes complemented: (B')." to Text3("En POS, un 1 fixe devient complémenté : (B′).","En POS, un 1 fijo se complementa: (B′).","Em POS, um 1 fixo é complementado: (B′)."),
        "POS corner wrapping" to Text3("Raccordement des coins en POS","Conexión de esquinas en POS","Conexão dos cantos em POS"),
        "The zeroes are the four corners; B and D stay 0." to Text3("Les zéros occupent les quatre coins ; B et D restent à 0.","Los ceros están en las cuatro esquinas; B y D permanecen en 0.","Os zeros estão nos quatro cantos; B e D permanecem em 0."),
        "Fixed 0 is uncomplemented in POS: (B+D)." to Text3("En POS, un 0 fixe reste non complémenté : (B+D).","En POS, un 0 fijo no se complementa: (B+D).","Em POS, um 0 fixo não é complementado: (B+D)."),
        "All don't-cares" to Text3("Toutes les cases indifférentes","Todo indiferente","Todas as condições indiferentes"),
        "No input combination imposes a required output." to Text3("Aucune combinaison d’entrée n’impose une sortie.","Ninguna combinación de entrada exige una salida.","Nenhuma combinação de entrada exige uma saída."),
        "Either constant is valid; the SOP solver chooses 0 and POS chooses 1." to Text3("Les deux constantes sont valides ; le solveur choisit 0 en SOP et 1 en POS.","Cualquiera de las constantes es válida; el solucionador elige 0 en SOP y 1 en POS.","Qualquer constante é válida; o solucionador escolhe 0 em SOP e 1 em POS."),
        "Every cell is unspecified. 0 is a valid simplest choice; the other constant is valid too." to Text3("Toutes les cases sont indéterminées. 0 est le choix valide le plus simple ; l’autre constante convient aussi.","Todas las celdas están sin especificar. 0 es una opción válida y sencilla; la otra constante también sirve.","Todas as células estão indefinidas. 0 é uma escolha válida e simples; a outra constante também serve."),
        "Every cell is unspecified. 1 is a valid simplest choice; the other constant is valid too." to Text3("Toutes les cases sont indéterminées. 1 est le choix valide le plus simple ; l’autre constante convient aussi.","Todas las celdas están sin especificar. 1 es una opción válida y sencilla; la otra constante también sirve.","Todas as células estão indefinidas. 1 é uma escolha válida e simples; a outra constante também serve."),
        "There are no required 1s. The constant 0 satisfies every specified cell." to Text3("Aucun 1 n’est requis. La constante 0 respecte toutes les cases spécifiées.","No se requiere ningún 1. La constante 0 cumple todas las celdas especificadas.","Nenhum 1 é obrigatório. A constante 0 atende a todas as células especificadas."),
        "There are no required 0s. The constant 1 satisfies every specified cell." to Text3("Aucun 0 n’est requis. La constante 1 respecte toutes les cases spécifiées.","No se requiere ningún 0. La constante 1 cumple todas las celdas especificadas.","Nenhum 0 é obrigatório. A constante 1 atende a todas as células especificadas."),
        "Exact minimum: fewest product terms, then fewest literals. Other equally minimal covers may exist." to Text3("Minimum exact : nombre minimal de termes produits, puis de littéraux. D’autres couvertures aussi minimales peuvent exister.","Mínimo exacto: la menor cantidad de términos producto y luego de literales. Puede haber otras coberturas igual de mínimas.","Mínimo exato: menor número de termos produto e, depois, de literais. Podem existir outras coberturas igualmente mínimas."),
        "Exact minimum: fewest sum clauses, then fewest literals. Other equally minimal covers may exist." to Text3("Minimum exact : nombre minimal de clauses sommes, puis de littéraux. D’autres couvertures aussi minimales peuvent exister.","Mínimo exacto: la menor cantidad de cláusulas suma y luego de literales. Puede haber otras coberturas igual de mínimas.","Mínimo exato: menor número de cláusulas soma e, depois, de literais. Podem existir outras coberturas igualmente mínimas."),
        "Check the map dimensions and values." to Text3("Vérifiez les dimensions et les valeurs de la carte.","Comprueba las dimensiones y los valores del mapa.","Confira as dimensões e os valores do mapa."),
        "Check the input lists." to Text3("Vérifiez les listes saisies.","Comprueba las listas de entrada.","Confira as listas de entrada."),
        "Choose 2 to 6 variables." to Text3("Choisissez de 2 à 6 variables.","Elige de 2 a 6 variables.","Escolha de 2 a 6 variáveis."),
        "A required cell cannot also be a don't-care: " to Text3("Une case obligatoire ne peut pas aussi être une indifférence : ","Una celda obligatoria no puede ser también indiferente: ","Uma célula obrigatória não pode ser também indiferente: "),
        "Keep function notation within 512 characters." to Text3("Limitez la notation de la fonction à 512 caractères.","Limita la notación de la función a 512 caracteres.","Mantenha a notação da função em até 512 caracteres."),
        "Keep each list to 256 characters or fewer." to Text3("Limitez chaque liste à 256 caractères.","Limita cada lista a 256 caracteres.","Mantenha cada lista com até 256 caracteres."),
        "Use F(A.B.C)=Σm(1,3) or F(A,B,C)=ΠM(0,2). Variables may be any distinct A–Z labels; indices are comma-separated." to Text3("Utilisez F(A.B.C)=Σm(1,3) ou F(A,B,C)=ΠM(0,2). Choisissez des variables distinctes de A à Z ; séparez les indices par des virgules.","Usa F(A.B.C)=Σm(1,3) o F(A,B,C)=ΠM(0,2). Las variables pueden ser letras distintas de A a Z; separa los índices con comas.","Use F(A.B.C)=Σm(1,3) ou F(A,B,C)=ΠM(0,2). As variáveis podem ser letras distintas de A a Z; separe os índices por vírgulas."),
        "List 2–6 different variables in significance order." to Text3("Indiquez de 2 à 6 variables différentes dans l’ordre de poids.","Indica de 2 a 6 variables distintas en orden de significancia.","Liste de 2 a 6 variáveis diferentes em ordem de significância."),
        "Use comma-separated cell indices, not ranges or expressions." to Text3("Saisissez les indices des cases séparés par des virgules, sans plage ni expression.","Usa índices de celdas separados por comas, no rangos ni expresiones.","Use índices de células separados por vírgulas, não intervalos ou expressões."),
        "A SOP needs no products when there are no 1s." to Text3("En SOP, aucun produit n’est nécessaire s’il n’y a pas de 1.","En SOP no se necesitan productos si no hay 1.","Em SOP, não são necessários produtos quando não há 1."),
        "Select at least one cell." to Text3("Sélectionnez au moins une case.","Selecciona al menos una celda.","Selecione pelo menos uma célula."),
        "A selected index is outside this map." to Text3("Un indice sélectionné est hors de cette carte.","Un índice seleccionado está fuera del mapa.","Um índice selecionado está fora deste mapa."),
        "Groups must have a power-of-two size: 1, 2, 4, 8, 16, 32 or 64." to Text3("La taille d’un groupe doit être une puissance de deux : 1, 2, 4, 8, 16, 32 ou 64.","El tamaño del grupo debe ser una potencia de dos: 1, 2, 4, 8, 16, 32 o 64.","O tamanho do grupo deve ser uma potência de dois: 1, 2, 4, 8, 16, 32 ou 64."),
        "SOP groups cannot contain a 0." to Text3("Les groupes SOP ne peuvent pas contenir de 0.","Los grupos SOP no pueden contener un 0.","Grupos SOP não podem conter 0."),
        "POS groups cannot contain a 1." to Text3("Les groupes POS ne peuvent pas contenir de 1.","Los grupos POS no pueden contener un 1.","Grupos POS não podem conter 1."),
        "A useful group must cover a required 1, not only don't-cares." to Text3("Un groupe utile doit couvrir un 1 obligatoire, pas seulement des indifférences.","Un grupo útil debe cubrir un 1 obligatorio, no solo valores indiferentes.","Um grupo útil deve cobrir um 1 obrigatório, não apenas condições indiferentes."),
        "A useful group must cover a required 0, not only don't-cares." to Text3("Un groupe utile doit couvrir un 0 obligatoire, pas seulement des indifférences.","Un grupo útil debe cubrir un 0 obligatorio, no solo valores indiferentes.","Um grupo útil deve cobrir um 0 obrigatório, não apenas condições indiferentes."),
        "Use adjacent Gray-code rectangles. Diagonal-only groups are not valid; opposite edges can touch." to Text3("Utilisez des rectangles de cases voisines en code Gray. Les diagonales seules ne forment pas un groupe valide ; les bords opposés sont adjacents.","Usa rectángulos de celdas vecinas en código Gray. Las diagonales no forman grupos válidos; los bordes opuestos sí se tocan.","Use retângulos de células vizinhas em código Gray. Apenas diagonais não formam grupos válidos; bordas opostas podem se conectar."),
        "Opposite edges are adjacent." to Text3("Les bords opposés sont voisins.","Los bordes opuestos son adyacentes.","As bordas opostas são adjacentes."),
        "It can grow into a larger valid group." to Text3("Il peut être agrandi en un groupe valide plus grand.","Puede ampliarse hasta formar un grupo válido mayor.","Ele pode crescer e formar um grupo válido maior."),
        "It is already a maximal group." to Text3("C’est déjà un groupe maximal.","Ya es un grupo máximo.","Ele já é um grupo maximal."),
        "Use cell indices 0–" to Text3("Utilisez les indices de cases de 0 à ","Usa índices de celdas de 0 a ","Use índices de células de 0 a "),
        "Every cell is unspecified. " to Text3("Toutes les cases sont indéterminées. ","Todas las celdas están sin especificar. ","Todas as células estão indefinidas. "),
        "There are no required " to Text3("Aucun ","No se requiere ningún ","Nenhum "),
        "Use " to Text3("Utilisez ","Usa ","Use ")
    )

    fun translate(source:String,language:String):String? {
        val lang=language.lowercase()
        entries[source]?.let { return it.forLanguage(lang) }
        if(lang !in setOf("fr","es","pt"))return null
        val variableCount=Regex("^Use (\\d+) different uppercase variable labels from A–Z\\.$").matchEntire(source)
        if(variableCount!=null){val count=variableCount.groupValues[1];return when(lang) {"fr"->"Utilisez $count étiquettes de variable majuscules différentes de A à Z.";"es"->"Usa $count etiquetas de variable mayúsculas distintas de A a Z.";else->"Use $count rótulos de variável maiúsculos diferentes de A a Z."}}
        val cellRange=Regex("^Use cell indices 0–(\\d+)\\.$").matchEntire(source)
        if(cellRange!=null)return when(lang) {"fr"->"Utilisez les indices de cases de 0 à ${cellRange.groupValues[1]}.";"es"->"Usa índices de celdas de 0 a ${cellRange.groupValues[1]}.";else->"Use índices de células de 0 a ${cellRange.groupValues[1]}."}
        val requiredCell=Regex("^A required cell cannot also be a don't-care: (.+)\\.$").matchEntire(source)
        if(requiredCell!=null)return (entries["A required cell cannot also be a don't-care: "]?.forLanguage(lang)?:"")+requiredCell.groupValues[1]+"."
        val noRequired=Regex("^There are no required ([01])s\\. The constant ([01]) satisfies every specified cell\\.$").matchEntire(source)
        if(noRequired!=null)return when(lang) {"fr"->"Aucun ${noRequired.groupValues[1]} n’est requis. La constante ${noRequired.groupValues[2]} respecte toutes les cases spécifiées.";"es"->"No se requiere ningún ${noRequired.groupValues[1]}. La constante ${noRequired.groupValues[2]} cumple todas las celdas especificadas.";else->"Nenhum ${noRequired.groupValues[1]} é obrigatório. A constante ${noRequired.groupValues[2]} atende a todas as células especificadas."}
        val unspecified=Regex("^Every cell is unspecified\\. ([01]) is a valid simplest choice; the other constant is valid too\\.$").matchEntire(source)
        if(unspecified!=null)return when(lang) {"fr"->"Toutes les cases sont indéterminées. ${unspecified.groupValues[1]} est le choix valide le plus simple ; l’autre constante convient aussi.";"es"->"Todas las celdas están sin especificar. ${unspecified.groupValues[1]} es una opción válida y sencilla; la otra constante también sirve.";else->"Todas as células estão indefinidas. ${unspecified.groupValues[1]} é uma escolha válida e simples; a outra constante também serve."}
        val groupLabel=Regex("^([A-F]) changes inside the group → remove it\\.$").matchEntire(source)
        if(groupLabel!=null)return when(lang) {"fr"->"La variable ${groupLabel.groupValues[1]} change dans le groupe → éliminez-la.";"es"->"La variable ${groupLabel.groupValues[1]} cambia dentro del grupo → elimínala.";else->"A variável ${groupLabel.groupValues[1]} muda no grupo → elimine-a."}
        val fixedLabel=Regex("^([A-F]) stays ([01]) → ([A-F]'?)\\.$").matchEntire(source)
        if(fixedLabel!=null)return when(lang) {"fr"->"${fixedLabel.groupValues[1]} reste à ${fixedLabel.groupValues[2]} → gardez ${fixedLabel.groupValues[3]}.";"es"->"${fixedLabel.groupValues[1]} permanece en ${fixedLabel.groupValues[2]} → conserva ${fixedLabel.groupValues[3]}.";else->"${fixedLabel.groupValues[1]} permanece em ${fixedLabel.groupValues[2]} → mantenha ${fixedLabel.groupValues[3]}."}
        val validGroup=Regex("^Valid (\\d+)-cell group → (.+?)\\. (Opposite edges are adjacent\\. )?(It can grow into a larger valid group\\.|It is already a maximal group\\.)$").matchEntire(source)
        if(validGroup!=null) {
            val size=validGroup.groupValues[1];val term=validGroup.groupValues[2]
            val wraps=validGroup.groupValues[3].isNotEmpty();val canGrow=validGroup.groupValues[4].startsWith("It can")
            return when(lang) {
                "fr"->"Groupe valide de $size cases → $term. "+(if(wraps)"Les bords opposés sont voisins. "else"")+(if(canGrow)"Il peut être agrandi en un groupe valide plus grand."else"C’est déjà un groupe maximal.")
                "es"->"Grupo válido de $size celdas → $term. "+(if(wraps)"Los bordes opuestos son adyacentes. "else"")+(if(canGrow)"Puede ampliarse hasta formar un grupo válido mayor."else"Ya es un grupo máximo.")
                else->"Grupo válido de $size células → $term. "+(if(wraps)"As bordas opostas são adjacentes. "else"")+(if(canGrow)"Ele pode crescer e formar um grupo válido maior."else"Ele já é um grupo maximal.")
            }
        }
        val bullet=Regex("^• (.+)$").matchEntire(source)
        if(bullet!=null){val body=translate(bullet.groupValues[1],lang)?:return null;return "• $body"}
        val groupLine=Regex("^G(\\d+): cells (.+) → (.+?)(?: \\((edge wrap|wraps edge|opposite edges wrap)\\))?(?: · essential)?$").matchEntire(source)
        if(groupLine!=null){
            val wrap=when(groupLine.groupValues[4]) {"edge wrap"->when(lang){"fr"->" (bords raccordés)";"es"->" (bordes conectados)";else->" (bordas conectadas)"};"wraps edge"->when(lang){"fr"->" (bords raccordés)";"es"->" (bordes conectados)";else->" (bordas conectadas)"};"opposite edges wrap"->when(lang){"fr"->" (les bords opposés se rejoignent)";"es"->" (los bordes opuestos se conectan)";else->" (as bordas opostas se conectam)"};else->""}
            val essential=if(source.endsWith(" · essential"))when(lang){"fr"->" · essentiel";"es"->" · esencial";else->" · essencial"}else""
            return when(lang) {"fr"->"G${groupLine.groupValues[1]} : cases ${groupLine.groupValues[2]} → ${groupLine.groupValues[3]}$wrap$essential";"es"->"G${groupLine.groupValues[1]}: celdas ${groupLine.groupValues[2]} → ${groupLine.groupValues[3]}$wrap$essential";else->"G${groupLine.groupValues[1]}: células ${groupLine.groupValues[2]} → ${groupLine.groupValues[3]}$wrap$essential"}
        }
        val selectedPrime=Regex("^Selected prime (implicants|implicates) \\((\\d+)\\)$").matchEntire(source)
        if(selectedPrime!=null)return when(lang) {"fr"->"${if(selectedPrime.groupValues[1]=="implicants")"Implicants premiers"else"Implicates premiers"} sélectionnés (${selectedPrime.groupValues[2]})";"es"->"${if(selectedPrime.groupValues[1]=="implicants")"Implicantes primos"else"Implicados primos"} seleccionados (${selectedPrime.groupValues[2]})";else->"${if(selectedPrime.groupValues[1]=="implicants")"Implicantes primos"else"Implicados primos"} selecionados (${selectedPrime.groupValues[2]})"}
        val grayOrder=Regex("^Gray order: 00, 01, 11, 10\\. The leftmost listed variable is the most significant bit\\.$").matchEntire(source)
        if(grayOrder!=null)return when(lang) {"fr"->"Ordre Gray : 00, 01, 11, 10. La variable listée la plus à gauche est le bit de poids fort.";"es"->"Orden Gray: 00, 01, 11, 10. La variable indicada más a la izquierda es el bit más significativo.";else->"Ordem Gray: 00, 01, 11, 10. A variável listada mais à esquerda é o bit mais significativo."}
        val grayRows=Regex("^Gray-code rows; leftmost variable is MSB\\.$").matchEntire(source)
        if(grayRows!=null)return when(lang) {"fr"->"Lignes en code Gray ; la variable la plus à gauche est le MSB.";"es"->"Filas en código Gray; la variable más a la izquierda es el MSB.";else->"Linhas em código Gray; a variável mais à esquerda é o MSB."}
        val required=Regex("^Required (1s|0s): (.*); don't-cares: (.*)$").matchEntire(source)
        if(required!=null){val target=if(required.groupValues[1]=="1s")"1"else"0";return when(lang) {"fr"->"${if(target=="1")"1 obligatoires"else"0 obligatoires"} : ${required.groupValues[2]} ; indifférences : ${required.groupValues[3]}";"es"->"${if(target=="1")"Unos obligatorios"else"Ceros obligatorios"}: ${required.groupValues[2]}; condiciones indiferentes: ${required.groupValues[3]}";else->"${if(target=="1")"Uns obrigatórios"else"Zeros obrigatórios"}: ${required.groupValues[2]}; condições indiferentes: ${required.groupValues[3]}"}}
        val evaluate=Regex("^(\\d+\\. )?Evaluate (\\d+) rows: (.+); (.+)\\.$").matchEntire(source)
        if(evaluate!=null)return "${evaluate.groupValues[1]}${when(lang){"fr"->"Évaluez ${evaluate.groupValues[2]} lignes : ${evaluate.groupValues[3]} ; ${evaluate.groupValues[4]}.";"es"->"Evalúa ${evaluate.groupValues[2]} filas: ${evaluate.groupValues[3]}; ${evaluate.groupValues[4]}.";else->"Avalie ${evaluate.groupValues[2]} linhas: ${evaluate.groupValues[3]}; ${evaluate.groupValues[4]}."}}"
        val precedence=Regex("^(\\d+\\. )?Precedence: parentheses → NOT → AND/adjacency → XOR/XNOR → OR\\.$").matchEntire(source)
        if(precedence!=null)return "${precedence.groupValues[1]}${when(lang){"fr"->"Priorité : parenthèses → NOT → AND/juxtaposition → XOR/XNOR → OR.";"es"->"Precedencia: paréntesis → NOT → AND/yuxtaposición → XOR/XNOR → OR.";else->"Precedência: parênteses → NOT → AND/justaposição → XOR/XNOR → OR."}}"
        val minimize=Regex("^(\\d+\\. )?(SOP: group required ones; changing variables disappear, fixed 0s complement, fixed 1s do not\\.|POS: group required zeros; fixed 0s stay, fixed 1s complement\\.)$").matchEntire(source)
        if(minimize!=null){val sop=minimize.groupValues[2].startsWith("SOP");return "${minimize.groupValues[1]}${when(lang){"fr"->if(sop)"SOP : regroupez les 1 obligatoires ; éliminez les variables qui changent, complémentez les 0 fixes et gardez les 1 fixes."else"POS : regroupez les 0 obligatoires ; gardez les 0 fixes et complémentez les 1 fixes.";"es"->if(sop)"SOP: agrupa los 1 obligatorios; elimina las variables que cambian, complementa los 0 fijos y conserva los 1 fijos."else"POS: agrupa los 0 obligatorios; conserva los 0 fijos y complementa los 1 fijos.";else->if(sop)"SOP: agrupe os 1 obrigatórios; elimine as variáveis que mudam, complemente os 0 fixos e mantenha os 1 fixos."else"POS: agrupe os 0 obrigatórios; mantenha os 0 fixos e complemente os 1 fixos."}}"}
        val minimum=Regex("^(Minimum )?(SOP|POS): (.+)$").matchEntire(source)
        if(minimum!=null)return "${if(minimum.groupValues[1].isNotEmpty())when(lang){"fr"->"Minimum ";"es"->"Mínima ";else->"Mínima "}else""}${minimum.groupValues[2]} : ${minimum.groupValues[3]}"
        val parsed=Regex("^Parsed: (.+)$").matchEntire(source)
        if(parsed!=null)return "${when(lang){"fr"->"Interprétation";"es"->"Interpretación";else->"Interpretado"}} : ${parsed.groupValues[1]}"
        val original=Regex("^Original: (.+)$").matchEntire(source)
        if(original!=null)return "${when(lang){"fr"->"Expression d’origine";"es"->"Expresión original";else->"Expressão original"}} : ${original.groupValues[1]}"
        val exactCover=Regex("^(\\d+\\. )?Exact cover: fewest terms/clauses, then fewest literals\\.$").matchEntire(source)
        if(exactCover!=null)return "${exactCover.groupValues[1]}${when(lang){"fr"->"Couverture exacte : nombre minimal de termes/clauses, puis de littéraux.";"es"->"Cobertura exacta: mínimo de términos/cláusulas y después de literales.";else->"Cobertura exata: menos termos/cláusulas e, depois, menos literais."}}"
        val gateLevel=Regex("^Gate-level: (AND groups feed OR|OR clauses feed AND)\\.$").matchEntire(source)
        if(gateLevel!=null)return when(lang) {"fr"->if(gateLevel.groupValues[1].startsWith("AND"))"Au niveau des portes : les groupes ET alimentent OU."else"Au niveau des portes : les clauses OU alimentent ET.";"es"->if(gateLevel.groupValues[1].startsWith("AND"))"A nivel de puertas: los grupos AND alimentan OR."else"A nivel de puertas: las cláusulas OR alimentan AND.";else->if(gateLevel.groupValues[1].startsWith("AND"))"No nível das portas: grupos AND alimentam OR."else"No nível das portas: cláusulas OR alimentam AND."}
        val truthInput=Regex("^Truth table: (.+) / input / simplified$").matchEntire(source)
        if(truthInput!=null)return when(lang) {"fr"->"Table de vérité : ${truthInput.groupValues[1]} / entrée / simplifiée";"es"->"Tabla de verdad: ${truthInput.groupValues[1]} / entrada / simplificada";else->"Tabela-verdade: ${truthInput.groupValues[1]} / entrada / simplificada"}
        val truthOutput=Regex("^Truth table: (.+) / F$").matchEntire(source)
        if(truthOutput!=null)return when(lang) {"fr"->"Table de vérité : ${truthOutput.groupValues[1]} / F";"es"->"Tabla de verdad: ${truthOutput.groupValues[1]} / F";else->"Tabela-verdade: ${truthOutput.groupValues[1]} / F"}
        val noCells=Regex("^No ([01])-cells: the minimum (SOP|POS) is the constant ([01])\\.$").matchEntire(source)
        if(noCells!=null)return when(lang) {"fr"->"Aucune case avec ${noCells.groupValues[1]} : la ${noCells.groupValues[2]} minimale est la constante ${noCells.groupValues[3]}.";"es"->"Sin celdas con ${noCells.groupValues[1]}: la ${noCells.groupValues[2]} mínima es la constante ${noCells.groupValues[3]}.";else->"Sem células com ${noCells.groupValues[1]}: a ${noCells.groupValues[2]} mínima é a constante ${noCells.groupValues[3]}."}
        return null
    }
    private fun Text3.forLanguage(lang:String)=when(lang) {"fr"->fr;"es"->es;"pt"->pt;else->error("Unsupported language")}
}
