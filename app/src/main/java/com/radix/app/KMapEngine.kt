package com.radix.app

/** Exact two-level Boolean minimization for 2–6 variables. A is the MSB. */
object KMapEngine {
    enum class Form { SOP, POS }
    const val DONT_CARE = 2
    private const val LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    data class Cube(val mask: Int, val bits: Int, val members: Long) {
        val literals: Int get() = Integer.bitCount(mask)
        fun contains(index: Int): Boolean = index in 0..63 && members and (1L shl index) != 0L
        fun indices(size: Int): List<Int> = (0 until size).filter(::contains)
    }
    data class Group(val cube: Cube, val term: String, val essential: Boolean, val wraps: Boolean, val explanation: List<String>)
    data class Solution(val variables: Int, val form: Form, val expression: String, val groups: List<Group>, val primeCount: Int, val requiredMask: Long, val note: String) {
        val literalCount: Int get() = groups.sumOf { it.cube.literals }
        fun evaluate(index: Int): Boolean {
            require(index in 0 until (1 shl variables))
            val matches = groups.any { it.cube.contains(index) }
            return if (form == Form.SOP) matches else !matches
        }
    }
    sealed interface Input {
        data class Valid(val cells: List<Int>) : Input
        data class Invalid(val message: String) : Input
    }
    data class GroupCheck(val valid: Boolean, val message: String, val cube: Cube? = null)
    data class Notation(val variables: Int, val names: String, val form: Form, val cells: List<Int>)
    data class Exercise(val id: String, val title: String, val variables: Int, val ones: List<Int>, val dc: List<Int> = emptyList(), val form: Form = Form.SOP, val hint: String, val lesson: String) {
        fun cells(): List<Int> = List(1 shl variables) { if (it in dc) DONT_CARE else if (it in ones) 1 else 0 }
    }

    fun gray(bits: Int): List<Int> { require(bits in 1..3); return (0 until (1 shl bits)).map { it xor (it shr 1) } }
    fun rowBits(variables: Int): Int { require(variables in 2..6); return variables / 2 }
    fun columnBits(variables: Int): Int = variables - rowBits(variables)
    fun cellAt(variables: Int, row: Int, column: Int): Int {
        val rows = gray(rowBits(variables)); val columns = gray(columnBits(variables))
        require(row in rows.indices && column in columns.indices)
        return (rows[row] shl columnBits(variables)) or columns[column]
    }
    fun binary(index: Int, variables: Int): String = index.toString(2).padStart(variables, '0')
    fun names(variables: Int): String { require(variables in 2..6); return LETTERS.take(variables) }
    private fun validateNames(variables: Int, names: String) {
        require(variables in 2..6 && names.length == variables && names.all { it in LETTERS } && names.toSet().size == variables) {
            "Use $variables different uppercase variable labels from A–Z."
        }
    }
    fun indicesText(cells: List<Int>, value: Int): String = cells.indices.filter { cells[it] == value }.joinToString(", ")

    private val allCubes: Map<Int, List<Cube>> by lazy {
        (2..6).associateWith { n ->
            val size = 1 shl n
            buildList {
                for (mask in 0 until size) for (bits in 0 until size) {
                    if (bits and mask != bits) continue
                    var members = 0L
                    for (index in 0 until size) if (index and mask == bits) members = members or (1L shl index)
                    add(Cube(mask, bits, members))
                }
            }.sortedWith(compareBy<Cube> { it.literals }.thenBy { it.mask }.thenBy { it.bits })
        }
    }

    /** Parse editable textbook notation such as F(A.B.C)=Σm(1,3,5) or ΠM(0,2). */
    fun fromNotation(source: String): Notation {
        require(source.length <= 512) { "Keep function notation within 512 characters." }
        val normalized = source.trim().uppercase(java.util.Locale.ROOT)
        val match = Regex("""F\s*\(\s*([A-Z](?:\s*[,·.]\s*[A-Z]){1,5})\s*\)\s*=\s*([Σ∑Π])\s*([MM])\s*\(\s*([^()]*)\s*\)""").matchEntire(normalized)
            ?: throw IllegalArgumentException("Use F(A.B.C)=Σm(1,3) or F(A,B,C)=ΠM(0,2). Variables may be any distinct A–Z labels; indices are comma-separated.")
        val names = match.groupValues[1].split(Regex("[,·.\\s]+")).joinToString("")
        require(names.length in 2..6 && names.toSet().size == names.length) { "List 2–6 different variables in significance order." }
        val form = when (match.groupValues[2]) { "Σ", "∑" -> Form.SOP; "Π" -> Form.POS; else -> error("Unknown form") }
        val indices = parseIndices(match.groupValues[4], 1 shl names.length)
        val target = if (form == Form.SOP) 1 else 0
        val cells = List(1 shl names.length) { if (it in indices) target else 1-target }
        return Notation(names.length, names, form, cells)
    }

    private fun parseIndices(text: String, size: Int): Set<Int> {
        require(text.length <= 256) { "Keep each list to 256 characters or fewer." }
        if (text.isBlank()) return emptySet()
        val tokens = text.trim().split(Regex("[,\\s]+")).filter { it.isNotEmpty() }
        if (tokens.isEmpty()) return emptySet()
        return tokens.map { token ->
            require(token.matches(Regex("[0-9]+"))) { "Use comma-separated cell indices, not ranges or expressions." }
            val index = token.toIntOrNull()
            require(index != null && index in 0 until size) { "Use cell indices 0–${size - 1}." }
            index
        }.toSet()
    }
    fun fromLists(variables: Int, terms: String, dc: String, form: Form): Input = try {
        require(variables in 2..6) { "Choose 2 to 6 variables." }
        val size = 1 shl variables
        val required = parseIndices(terms, size); val ignored = parseIndices(dc, size)
        val overlap = required.intersect(ignored)
        require(overlap.isEmpty()) { "A required cell cannot also be a don't-care: ${overlap.sorted().joinToString(", ")}." }
        val target = if (form == Form.SOP) 1 else 0
        Input.Valid(List(size) { if (it in ignored) DONT_CARE else if (it in required) target else 1 - target })
    } catch (error: IllegalArgumentException) { Input.Invalid(error.message ?: "Check the input lists.") }

    fun term(cube: Cube, variables: Int, form: Form, labels: String = KMapEngine.names(variables)): String {
        validateNames(variables, labels)
        val literals = (0 until variables).mapNotNull { i ->
            val bit = 1 shl (variables - 1 - i)
            if (cube.mask and bit == 0) null else {
                val one = cube.bits and bit != 0
                "${labels[i]}" + if ((form == Form.SOP && !one) || (form == Form.POS && one)) "'" else ""
            }
        }
        return if (form == Form.SOP) literals.joinToString("").ifEmpty { "1" }
        else literals.joinToString(" + ").ifEmpty { "0" }
    }
    private fun wraps(cube: Cube, n: Int): Boolean {
        val rowCount = 1 shl rowBits(n); val colCount = 1 shl columnBits(n)
        val rows = mutableSetOf<Int>(); val columns = mutableSetOf<Int>()
        for (r in 0 until rowCount) for (c in 0 until colCount) if (cube.contains(cellAt(n, r, c))) { rows.add(r); columns.add(c) }
        return (rowCount > 2 && rows.size < rowCount && 0 in rows && rowCount - 1 in rows) ||
            (colCount > 2 && columns.size < colCount && 0 in columns && colCount - 1 in columns)
    }

    fun solve(variables: Int, cells: List<Int>, form: Form = Form.SOP, labels: String = KMapEngine.names(variables)): Solution {
        require(variables in 2..6)
        validateNames(variables, labels)
        require(cells.size == 1 shl variables && cells.all { it in 0..2 })
        val target = if (form == Form.SOP) 1 else 0
        var required = 0L; var forbidden = 0L
        for (i in cells.indices) {
            if (cells[i] == target) required = required or (1L shl i)
            if (cells[i] == 1 - target) forbidden = forbidden or (1L shl i)
        }
        if (required == 0L) return Solution(variables, form, if (form == Form.SOP) "0" else "1", emptyList(), 0, 0L,
            if (cells.all { it == DONT_CARE }) "Every cell is unspecified. ${if (form == Form.SOP) "0" else "1"} is a valid simplest choice; the other constant is valid too."
            else "There are no required ${target}s. The constant ${if (form == Form.SOP) "0" else "1"} satisfies every specified cell.")
        val eligible = allCubes.getValue(variables).filter { (it.members and forbidden) == 0L && (it.members and required) != 0L }
        val primes = eligible.filter { cube -> eligible.none { other -> other.members != cube.members && other.members and cube.members == cube.members } }
        val coverage = primes.map { it.members and required }
        val essential = primes.indices.filter { p ->
            (0 until cells.size).any { cell -> required and (1L shl cell) != 0L && coverage[p] and (1L shl cell) != 0L && coverage.count { it and (1L shl cell) != 0L } == 1 }
        }.toSet()
        var startCoverage = 0L
        essential.forEach { startCoverage = startCoverage or coverage[it] }
        var best: List<Int>? = null
        var bestLiterals = Int.MAX_VALUE
        val visited = mutableMapOf<Long, Pair<Int, Int>>()
        fun search(covered: Long, chosen: List<Int>, literalCount: Int) {
            if (covered == required) {
                val sorted = chosen.sorted()
                val current = best
                if (current == null || sorted.size < current.size ||
                    (sorted.size == current.size && (literalCount < bestLiterals ||
                    (literalCount == bestLiterals && sorted.joinToString(",") < current.joinToString(","))))) {
                    best = sorted; bestLiterals = literalCount
                }
                return
            }
            if (best != null && chosen.size >= best!!.size) return
            val old = visited[covered]
            if (old != null && (old.first < chosen.size || (old.first == chosen.size && old.second <= literalCount))) return
            visited[covered] = chosen.size to literalCount
            val remaining = required and covered.inv()
            var candidates = emptyList<Int>()
            var fewest = Int.MAX_VALUE
            for (cell in cells.indices) if (remaining and (1L shl cell) != 0L) {
                val options = primes.indices.filter { coverage[it] and (1L shl cell) != 0L }
                if (options.size < fewest) { fewest = options.size; candidates = options }
            }
            candidates.sortedWith(compareByDescending<Int> { java.lang.Long.bitCount(coverage[it] and remaining) }.thenBy { primes[it].literals }.thenBy { it }).forEach { p ->
                search(covered or coverage[p], chosen + p, literalCount + primes[p].literals)
            }
        }
        search(startCoverage, essential.sorted(), essential.sumOf { primes[it].literals })
        val chosen = checkNotNull(best)
        val groups = chosen.map { index ->
            val cube = primes[index]
            val explanation = (0 until variables).map { variable ->
                val bit = 1 shl (variables - 1 - variable)
                if (cube.mask and bit == 0) "${labels[variable]} changes inside the group → remove it."
                else {
                    val fixed = if (cube.bits and bit == 0) 0 else 1
                    val literal = "${labels[variable]}" + if ((form == Form.SOP && fixed == 0) || (form == Form.POS && fixed == 1)) "'" else ""
                    "${labels[variable]} stays $fixed → $literal."
                }
            }
            Group(cube, term(cube, variables, form, labels), index in essential, wraps(cube, variables), explanation)
        }.sortedBy { it.term }
        val expression = if (form == Form.SOP) groups.joinToString(" + ") { it.term }
        else if (groups.any { it.term == "0" }) "0" else groups.joinToString("") { "(${it.term})" }
        return Solution(variables, form, expression, groups, primes.size, required,
            "Exact minimum: fewest ${if (form == Form.SOP) "product terms" else "sum clauses"}, then fewest literals. Other equally minimal covers may exist.")
    }

    fun checkGroup(variables: Int, cells: List<Int>, indices: Collection<Int>, form: Form): GroupCheck {
        if (variables !in 2..6 || cells.size != 1 shl variables || cells.any { it !in 0..2 }) return GroupCheck(false, "Check the map dimensions and values.")
        val selected = indices.toSet()
        if (selected.isEmpty()) return GroupCheck(false, "Select at least one cell.")
        if (selected.any { it !in cells.indices }) return GroupCheck(false, "A selected index is outside this map.")
        if (selected.size and (selected.size - 1) != 0) return GroupCheck(false, "Groups must have a power-of-two size: 1, 2, 4, 8, 16, 32 or 64.")
        val target = if (form == Form.SOP) 1 else 0
        if (selected.any { cells[it] == 1 - target }) return GroupCheck(false, "${form.name} groups cannot contain a ${1 - target}.")
        if (selected.none { cells[it] == target }) return GroupCheck(false, "A useful group must cover a required $target, not only don't-cares.")
        var members = 0L; selected.forEach { members = members or (1L shl it) }
        val cube = allCubes.getValue(variables).firstOrNull { it.members == members }
            ?: return GroupCheck(false, "Use adjacent Gray-code rectangles. Diagonal-only groups are not valid; opposite edges can touch.")
        val canGrow = allCubes.getValue(variables).any { other -> other.members != members && other.members and members == members && other.indices(cells.size).all { cells[it] != 1 - target } }
        return GroupCheck(true, "Valid ${selected.size}-cell group → ${if (form == Form.POS) "(${term(cube, variables, form)})" else term(cube, variables, form)}. " +
            (if (wraps(cube, variables)) "Opposite edges are adjacent. " else "") + (if (canGrow) "It can grow into a larger valid group." else "It is already a maximal group."), cube)
    }

    /** Independent expression parser used by tests, not by the solver. */
    fun evaluateExpression(expression: String, variables: Int, index: Int, form: Form): Boolean {
        require(variables in 2..6 && index in 0 until (1 shl variables))
        if (expression == "0") return false
        if (expression == "1") return true
        fun literal(token: String): Boolean {
            val s = token.trim(); require(s.matches(Regex("[A-F]'?")))
            val variable = LETTERS.indexOf(s[0]); require(variable < variables)
            val value = index and (1 shl (variables - 1 - variable)) != 0
            return if (s.endsWith("'")) !value else value
        }
        return if (form == Form.SOP) expression.split('+').any { product ->
            val tokens = Regex("[A-F]'?").findAll(product.replace(" ", "")).map { it.value }.toList()
            require(tokens.joinToString("") == product.replace(" ", ""))
            tokens.all(::literal)
        } else {
            val clauses = Regex("\\(([^()]*)\\)").findAll(expression).map { it.value }.toList()
            require(clauses.joinToString("") == expression)
            clauses.all { clause -> clause.removeSurrounding("(", ")").split('+').any(::literal) }
        }
    }

    val exercises: List<Exercise> = listOf(
        Exercise("or", "Overlapping pairs", 2, listOf(1,2,3), hint="Pairs may share a cell. Cover every 1.", lesson="Overlap is allowed; m3 belongs to both useful pairs."),
        Exercise("and", "One isolated 1", 2, listOf(3), hint="A group of one is legal when it cannot expand.", lesson="Both variables stay 1, giving AB."),
        Exercise("xor", "Do not group diagonals", 2, listOf(1,2), hint="The two 1s differ in two bits.", lesson="Diagonal cells do not touch. Keep two one-cell terms."),
        Exercise("xnor", "The other diagonal", 2, listOf(0,3), hint="Corners in a 2×2 map are not all mutually adjacent.", lesson="A two-cell diagonal is not a Gray-code rectangle."),
        Exercise("pair", "Drop a changing variable", 2, listOf(0,1), hint="A stays 0; B changes.", lesson="The changing B disappears; A=0 contributes A'."),
        Exercise("all-one", "The constant-one map", 2, listOf(0,1,2,3), hint="Use the whole map as one group.", lesson="All variables change, leaving F=1."),
        Exercise("all-zero", "No required 1s", 2, emptyList(), hint="A SOP needs no products when there are no 1s.", lesson="The simplest function is F=0."),
        Exercise("dc-pair", "Use a helpful don't-care", 2, listOf(1), listOf(3), hint="An X can enlarge the group, but need not be used.", lesson="The pair m1,m3 simplifies to B."),
        Exercise("wrap-three", "Three-variable edge wrap", 3, listOf(0,2), hint="Columns 00 and 10 are neighbors across the edge.", lesson="The outer pair gives A'C'."),
        Exercise("row-three", "A whole row", 3, listOf(0,1,2,3), hint="B and C both vary within this row.", lesson="Only A=0 remains: A'."),
        Exercise("column-three", "An odd-index pattern", 3, listOf(1,3,5,7), hint="Read binary indices; C is the least significant bit.", lesson="C stays 1, so the answer is C."),
        Exercise("three-ones", "Three is not a group size", 3, listOf(0,1,3), hint="Cover three 1s using overlapping power-of-two groups.", lesson="Use two pairs; never draw a group of three."),
        Exercise("parity", "No adjacent 1s", 3, listOf(1,2,4,7), hint="Every possible pair would include a 0.", lesson="Four isolated cells need four product terms."),
        Exercise("dc-four", "Grow to four with Xs", 3, listOf(1,3), listOf(5,7), hint="The two Xs can complete the C=1 group.", lesson="C alone covers the required 1s without covering any required 0."),
        Exercise("corners", "Four corners are adjacent", 4, listOf(0,2,8,10), hint="Wrap horizontally and vertically at the same time.", lesson="The four corners give B'D', not four separate terms."),
        Exercise("octet", "An eight-cell group", 4, (0..7).toList(), hint="Three variables change; A stays 0.", lesson="The largest group is eight cells, giving A'."),
        Exercise("row-four", "Read Gray-code rows", 4, listOf(4,5,6,7), hint="Row AB=01 contains these four minterms.", lesson="CD vary; AB=01 leaves A'B."),
        Exercise("column-four", "Four Gray-code rows", 4, listOf(1,5,9,13), hint="Column CD=01 spans all rows.", lesson="A and B vanish, leaving C'D."),
        Exercise("overlap-four", "Three overlapping groups", 4, listOf(0,1,2,3,4,5,8,9), hint="A cell may be used in several maximal groups.", lesson="Overlapping four-cell groups cover all 1s with three terms."),
        Exercise("dc-octet", "An X-assisted octet", 4, listOf(1,3,7,11,15), listOf(5,9,13), hint="Don't-cares complete all odd-index cells.", lesson="Use the eight-cell D=1 group."),
        Exercise("pos-or", "POS groups zeroes", 2, listOf(1,2,3), form=Form.POS, hint="Group the lone 0, not the three 1s.", lesson="A=0 and B=0 produce the sum clause (A+B)."),
        Exercise("pos-complement", "POS complement rule", 2, listOf(0,2), form=Form.POS, hint="B stays 1 in the zero group.", lesson="For POS, fixed 1 becomes complemented: (B')."),
        Exercise("pos-corners", "POS corner wrapping", 4, (0..15).filter { it !in listOf(0,2,8,10) }, form=Form.POS, hint="The zeroes are the four corners; B and D stay 0.", lesson="Fixed 0 is uncomplemented in POS: (B+D)."),
        Exercise("all-x", "All don't-cares", 2, emptyList(), (0..3).toList(), hint="No input combination imposes a required output.", lesson="Either constant is valid; the SOP solver chooses 0 and POS chooses 1.")
    )
}
