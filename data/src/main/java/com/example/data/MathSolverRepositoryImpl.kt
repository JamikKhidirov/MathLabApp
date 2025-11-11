package com.example.data

import com.example.domain.data.repository.MathSolverRepository
import com.example.domain.data.stateMathlabCategory.MathCategory
import com.example.domain.data.stateScreen.MathProblem
import org.apache.commons.math3.analysis.UnivariateFunction
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction
import org.apache.commons.math3.analysis.solvers.NewtonRaphsonSolver
import org.apache.commons.math3.util.CombinatoricsUtils
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.math.pow

@Singleton
class MathSolverRepositoryImpl @Inject constructor() : MathSolverRepository {

    override suspend fun solve(problem: MathProblem): String {
        return try {
            when (problem.category) {
                MathCategory.ALGEBRA -> solveAlgebra(problem.expression, problem.variable)
                MathCategory.GEOMETRY -> solveGeometry(problem.expression)
                MathCategory.TRIGONOMETRY -> solveTrigonometry(problem.expression)
                MathCategory.COMBINATORICS -> solveCombinatorics(problem.expression)
            }
        } catch (e: Exception) {
            "Ошибка вычисления: ${e.message ?: "проверьте правильность введенных данных"}"
        }
    }

    private fun solveAlgebra(expression: String, variable: String): String {
        val cleanExpr = expression.trim().replace(" ", "")

        // Если есть знак равенства, решаем как уравнение
        if (cleanExpr.contains("=")) {
            return solveUniversalEquation(cleanExpr, variable)
        }

        // Если нет равенства, вычисляем выражение
        return when {
            cleanExpr.contains('(') || cleanExpr.contains(')') -> solveExpressionWithBrackets(cleanExpr)
            cleanExpr.contains("^2") -> solveQuadraticEquation("$cleanExpr=0", variable)
            cleanExpr.contains("^3") -> solveCubicEquation("$cleanExpr=0", variable)
            cleanExpr.contains(variable) -> solveLinearEquation("$cleanExpr=0", variable)
            else -> solveArithmeticExpression(cleanExpr)
        }
    }

    private fun solveUniversalEquation(equation: String, variable: String): String {
        return try {
            val sides = equation.split("=")
            if (sides.size != 2) {
                return "❌ Неверный формат уравнения. Используйте: выражение = выражение"
            }

            val leftSide = sides[0].trim()
            val rightSide = sides[1].trim()

            // Упрощаем обе стороны
            val simplifiedLeft = simplifyExpression(leftSide)
            val simplifiedRight = simplifyExpression(rightSide)

            // Переносим все члены в левую часть
            val equationInStandardForm = "$simplifiedLeft - ($simplifiedRight)"
            val simplifiedEquation = simplifyExpression(equationInStandardForm)

            // Определяем тип уравнения и решаем соответствующим методом
            when {
                isQuadraticEquation(simplifiedEquation, variable) ->
                    solveQuadraticEquation("$simplifiedEquation=0", variable)
                isCubicEquation(simplifiedEquation, variable) ->
                    solveCubicEquation("$simplifiedEquation=0", variable)
                isLinearEquation(simplifiedEquation, variable) ->
                    solveLinearEquationDetailed(simplifiedLeft, simplifiedRight, variable)
                else -> solveEquationNumerically(simplifiedEquation, variable)
            }

        } catch (e: Exception) {
            "❌ Ошибка при решении уравнения: ${e.message}"
        }
    }

    private fun solveLinearEquationDetailed(leftSide: String, rightSide: String, variable: String): String {
        // Вычисляем численное значение правой части
        val rightValue = evaluateArithmeticExpression(rightSide)

        // Разбираем левую часть на коэффициент и константу
        val (coefficient, constant) = parseLinearExpression(leftSide, variable)

        return if (coefficient != 0.0) {
            val solution = (rightValue - constant) / coefficient

            buildString {
                appendLine("🧮 Решение линейного уравнения:")
                appendLine("Уравнение: $leftSide = $rightSide")
                appendLine("Упрощенное: ${formatTerm(coefficient, variable)} ${constant.toSignedString()} = $rightValue")
                appendLine()
                appendLine("📝 Шаги решения:")
                appendLine("1. Переносим постоянные: ${formatTerm(coefficient, variable)} = $rightValue ${(-constant).toSignedString()}")
                appendLine("2. Вычисляем: ${formatTerm(coefficient, variable)} = ${(rightValue - constant).format(3)}")
                appendLine("3. Делим на коэффициент: $variable = ${(rightValue - constant).format(3)} / ${coefficient.toCleanString()}")
                appendLine("4. Ответ: $variable = ${solution.format(3)}")
            }
        } else {
            if (rightValue - constant == 0.0) {
                "✅ Уравнение имеет бесконечно много решений"
            } else {
                "❌ Уравнение не имеет решений"
            }
        }
    }

    private fun parseLinearExpression(expression: String, variable: String): Pair<Double, Double> {
        var coefficient = 0.0
        var constant = 0.0

        // Разбиваем выражение на члены
        val terms = splitIntoTerms(expression)

        for (term in terms) {
            when {
                term.contains(variable) -> {
                    coefficient += parseCoefficient(term, variable)
                }
                else -> {
                    constant += evaluateArithmeticExpression(term)
                }
            }
        }

        return Pair(coefficient, constant)
    }

    private fun splitIntoTerms(expression: String): List<String> {
        val terms = mutableListOf<String>()
        var currentTerm = StringBuilder()
        var depth = 0

        for (char in expression) {
            when (char) {
                '(' -> depth++
                ')' -> depth--
            }

            if (depth == 0 && (char == '+' || char == '-') && currentTerm.isNotEmpty()) {
                terms.add(currentTerm.toString())
                currentTerm = StringBuilder(if (char == '-') "-" else "")
            } else {
                currentTerm.append(char)
            }
        }

        if (currentTerm.isNotEmpty()) {
            terms.add(currentTerm.toString())
        }

        return terms.filter { it.isNotEmpty() && it != "+" }
    }

    private fun parseCoefficient(term: String, variable: String): Double {
        val cleanTerm = term.replace(variable, "")
        return when {
            cleanTerm.isEmpty() || cleanTerm == "+" -> 1.0
            cleanTerm == "-" -> -1.0
            else -> evaluateArithmeticExpression(cleanTerm)
        }
    }

    private fun solveEquationNumerically(equation: String, variable: String): String {
        return try {
            val solver = NewtonRaphsonSolver()

            val function: UnivariateFunction = object : UnivariateFunction {
                override fun value(x: Double): Double {
                    return evaluateFunction(equation, variable, x)
                }
            }

            // Ищем корень в диапазоне [-100, 100]
            val root = solver.solve(1000, function as UnivariateDifferentiableFunction?, -100.0, 100.0)

            buildString {
                appendLine("🧮 Численное решение уравнения:")
                appendLine("Уравнение: $equation = 0")
                appendLine("Найденный корень: $variable = ${root.format(5)}")
                appendLine("Метод: Ньютона-Рафсона")
                appendLine("Проверка: f(${root.format(3)}) = ${function.value(root).format(6)}")
            }
        } catch (e: Exception) {
            "❌ Не удалось найти численное решение уравнения"
        }
    }

    private fun evaluateFunction(expression: String, variable: String, value: Double): Double {
        val substituted = expression.replace(variable, value.toString())
        return evaluateArithmeticExpression(substituted)
    }

    private fun isLinearEquation(expression: String, variable: String): Boolean {
        return expression.contains(variable) &&
                !expression.contains("^2") &&
                !expression.contains("^3") &&
                !expression.contains("sin") &&
                !expression.contains("cos") &&
                !expression.contains("tan")
    }

    private fun isQuadraticEquation(expression: String, variable: String): Boolean {
        return expression.contains("$variable^2") ||
                expression.contains("$variable²") ||
                (expression.contains(variable) && expression.contains("^2"))
    }

    private fun isCubicEquation(expression: String, variable: String): Boolean {
        return expression.contains("$variable^3") ||
                expression.contains("$variable³") ||
                (expression.contains(variable) && expression.contains("^3"))
    }

    private fun solveExpressionWithBrackets(expression: String): String {
        return try {
            val simplified = simplifyExpression(expression)
            val result = evaluateArithmeticExpression(simplified)

            buildString {
                appendLine("🧮 Решение выражения со скобками:")
                appendLine("Исходное выражение: $expression")
                appendLine("Упрощенное выражение: $simplified")
                appendLine("Результат: $result")
                appendLine()
                appendLine("📝 Порядок решения:")
                appendLine("1. Сначала вычисляются выражения в скобках")
                appendLine("2. Затем умножение и деление")
                appendLine("3. Затем сложение и вычитание")
            }
        } catch (e: Exception) {
            "❌ Ошибка при решении выражения: ${e.message}"
        }
    }

    private fun solveArithmeticExpression(expression: String): String {
        return try {
            val result = evaluateArithmeticExpression(expression)

            buildString {
                appendLine("🧮 Вычисление выражения:")
                appendLine("Выражение: $expression")
                appendLine("Результат: $result")
            }
        } catch (e: Exception) {
            "❌ Ошибка при вычислении выражения: ${e.message}"
        }
    }

    private fun simplifyExpression(expr: String): String {
        var expression = expr
        val bracketPattern = """\(([^()]+)\)""".toRegex()

        // Пока есть скобки, вычисляем внутренние выражения
        while (bracketPattern.containsMatchIn(expression)) {
            expression = bracketPattern.replace(expression) { match ->
                val innerExpr = match.groupValues[1]
                evaluateArithmeticExpression(innerExpr).toString()
            }
        }

        return expression
    }

    private fun evaluateArithmeticExpression(expression: String): Double {
        var expr = expression.replace(" ", "")

        // Обрабатываем степени
        expr = processPowers(expr)

        // Обрабатываем умножение и деление
        expr = processMultiplicationAndDivision(expr)

        // Обрабатываем сложение и вычитание
        return processAdditionAndSubtraction(expr)
    }

    private fun processPowers(expr: String): String {
        var expression = expr
        val powerPattern = """(-?\d+\.?\d*)\^(-?\d+\.?\d*)""".toRegex()

        while (powerPattern.containsMatchIn(expression)) {
            expression = powerPattern.replace(expression) { match ->
                val base = match.groupValues[1].toDouble()
                val exponent = match.groupValues[2].toDouble()
                base.pow(exponent).toString()
            }
        }

        return expression
    }

    private fun processMultiplicationAndDivision(expr: String): String {
        var expression = expr
        val mdPattern = """(-?\d+\.?\d*)([*/])(-?\d+\.?\d*)""".toRegex()

        while (mdPattern.containsMatchIn(expression)) {
            expression = mdPattern.replace(expression) { match ->
                val left = match.groupValues[1].toDouble()
                val operator = match.groupValues[2]
                val right = match.groupValues[3].toDouble()

                when (operator) {
                    "*" -> (left * right).toString()
                    "/" -> (left / right).toString()
                    else -> match.value
                }
            }
        }

        return expression
    }

    private fun processAdditionAndSubtraction(expr: String): Double {
        var expression = expr
        val terms = mutableListOf<Double>()

        // Разбиваем на слагаемые
        val pattern = """([+-]?\d+\.?\d*)""".toRegex()
        val matches = pattern.findAll(expression)

        for (match in matches) {
            terms.add(match.value.toDouble())
        }

        // Суммируем все слагаемые
        return terms.sum()
    }

    private fun formatTerm(coefficient: Double, variable: String): String {
        return when {
            coefficient == 1.0 -> variable
            coefficient == -1.0 -> "-$variable"
            else -> "${coefficient.toCleanString()}$variable"
        }
    }

    // Остальные методы остаются практически без изменений
    private fun solveQuadraticEquation(expression: String, variable: String): String {
        val cleanExpr = expression.replace(" ", "").replace("=0", "")

        // Упрощаем выражение
        val simplified = simplifyExpression(cleanExpr)

        // Парсим коэффициенты
        val (a, b, c) = parseQuadraticCoefficients(simplified, variable)

        if (a == 0.0) return "Это не квадратное уравнение (a = 0)"

        val discriminant = b * b - 4 * a * c

        return buildString {
            appendLine("📊 Решение квадратного уравнения:")
            appendLine("Уравнение: ${a.toCleanString()}${variable}² ${b.toSignedString()}$variable ${c.toSignedString()} = 0")
            appendLine("Дискриминант D = b² - 4ac = $b² - 4×${a.toCleanString()}×${c.toCleanString()} = $discriminant")

            when {
                discriminant > 0 -> {
                    val x1 = (-b + sqrt(discriminant)) / (2 * a)
                    val x2 = (-b - sqrt(discriminant)) / (2 * a)
                    appendLine("✅ D > 0, уравнение имеет два действительных корня:")
                    appendLine("$variable₁ = (-b + √D)/(2a) = (${-b} + ${sqrt(discriminant).format(3)})/(2×${a.toCleanString()}) = ${x1.format(3)}")
                    appendLine("$variable₂ = (-b - √D)/(2a) = (${-b} - ${sqrt(discriminant).format(3)})/(2×${a.toCleanString()}) = ${x2.format(3)}")
                }
                discriminant == 0.0 -> {
                    val x = -b / (2 * a)
                    appendLine("✅ D = 0, уравнение имеет один корень:")
                    appendLine("$variable = -b/(2a) = $b/(2×${a.toCleanString()}) = ${x.format(3)}")
                }
                else -> {
                    val realPart = -b / (2 * a)
                    val imaginaryPart = sqrt(-discriminant) / (2 * a)
                    appendLine("✅ D < 0, уравнение имеет два комплексных корня:")
                    appendLine("$variable₁ = ${realPart.format(3)} + ${imaginaryPart.format(3)}i")
                    appendLine("$variable₂ = ${realPart.format(3)} - ${imaginaryPart.format(3)}i")
                }
            }
        }
    }

    private fun parseQuadraticCoefficients(expression: String, variable: String): Triple<Double, Double, Double> {
        var a = 0.0
        var b = 0.0
        var c = 0.0

        val terms = splitIntoTerms(expression)

        for (term in terms) {
            when {
                term.contains("$variable^2") || term.contains("$variable²") -> {
                    a += parseCoefficient(term.replace("^2", "").replace("²", ""), variable)
                }
                term.contains(variable) && !term.contains("^") -> {
                    b += parseCoefficient(term, variable)
                }
                else -> {
                    c += evaluateArithmeticExpression(term)
                }
            }
        }

        return Triple(a, b, c)
    }

    private fun solveCubicEquation(expression: String, variable: String): String {
        val cleanExpr = expression.replace(" ", "").replace("=0", "")

        return buildString {
            appendLine("📊 Решение кубического уравнения:")
            appendLine("Уравнение: $cleanExpr")
            appendLine()
            appendLine("💡 Для кубических уравнений вида a${variable}³ + b${variable}² + c$variable + d = 0:")
            appendLine("1. Находим один действительный корень численными методами")
            appendLine("2. Разлагаем на линейный и квадратный множители")
            appendLine("3. Решаем полученное квадратное уравнение")
            appendLine()
            appendLine("Пример численного решения:")

            try {
                val solver = NewtonRaphsonSolver()
                val function: UnivariateFunction = object : UnivariateFunction {
                    override fun value(x: Double): Double {
                        return evaluateFunction(cleanExpr, variable, x)
                    }
                }

                val root = solver.solve(1000, function as UnivariateDifferentiableFunction?, -100.0, 100.0)
                appendLine("Найденный корень: $variable = ${root.format(5)}")
                appendLine("Метод: Ньютона-Рафсона")
                appendLine("Проверка: f(${root.format(3)}) = ${function.value(root).format(6)}")
            } catch (e: Exception) {
                appendLine("❌ Не удалось найти решение численными методами")
                appendLine("Попробуйте использовать конкретные уравнения вида:")
                appendLine("• ${variable}^3 - 2$variable - 5 = 0")
                appendLine("• ${variable}^3 - 3$variable - 1 = 0")
                appendLine("• ${variable}^3 - 6${variable}^2 + 11$variable - 6 = 0")
            }
        }
    }

    private fun solveLinearEquation(expression: String, variable: String): String {
        val cleanExpr = expression.replace(" ", "")
        val sides = cleanExpr.split("=")

        if (sides.size == 2) {
            return solveLinearEquationDetailed(sides[0], sides[1], variable)
        }

        return "❌ Неверный формат уравнения"
    }

    // Методы геометрии, тригонометрии и комбинаторики остаются без изменений
    private fun solveGeometry(expression: String): String {
        return when {
            expression.contains("area") -> calculateArea(expression)
            expression.contains("volume") -> calculateVolume(expression)
            expression.contains("perimeter") -> calculatePerimeter(expression)
            else -> """
                📐 Геометрический калькулятор:
                Доступные команды:
                • area circle [радиус] - площадь круга
                • area triangle [основание] [высота] - площадь треугольника
                • area rectangle [длина] [ширина] - площадь прямоугольника
                • volume sphere [радиус] - объем сферы
                • volume cube [сторона] - объем куба
                • volume cylinder [радиус] [высота] - объем цилиндра
                • perimeter circle [радиус] - длина окружности
                • perimeter rectangle [длина] [ширина] - периметр прямоугольника
                • perimeter triangle [сторона1] [сторона2] [сторона3] - периметр треугольника
            """.trimIndent()
        }
    }

    private fun calculateArea(expression: String): String {
        val numbers = extractNumbers(expression)
        return when {
            expression.contains("circle") -> {
                val radius = numbers.firstOrNull() ?: 1.0
                val area = PI * radius * radius
                """
                    📐 Площадь круга:
                    Радиус: r = $radius
                    Формула: S = π × r²
                    Вычисление: S = ${PI.format(2)} × $radius² = ${area.format(3)}
                    Ответ: S ≈ ${area.format(3)}
                """.trimIndent()
            }
            expression.contains("triangle") -> {
                if (numbers.size >= 2) {
                    val area = 0.5 * numbers[0] * numbers[1]
                    """
                        📐 Площадь треугольника:
                        Основание: a = ${numbers[0]}
                        Высота: h = ${numbers[1]}
                        Формула: S = ½ × a × h
                        Вычисление: S = 0.5 × ${numbers[0]} × ${numbers[1]} = ${area.format(3)}
                        Ответ: S ≈ ${area.format(3)}
                    """.trimIndent()
                } else {
                    "❌ Укажите основание и высоту треугольника: area triangle 4 3"
                }
            }
            expression.contains("rectangle") -> {
                if (numbers.size >= 2) {
                    val area = numbers[0] * numbers[1]
                    """
                        📐 Площадь прямоугольника:
                        Длина: a = ${numbers[0]}
                        Ширина: b = ${numbers[1]}
                        Формула: S = a × b
                        Вычисление: S = ${numbers[0]} × ${numbers[1]} = ${area.format(3)}
                        Ответ: S = ${area.format(3)}
                    """.trimIndent()
                } else {
                    "❌ Укажите длину и ширину: area rectangle 4 3"
                }
            }
            else -> "❌ Укажите фигуру: area circle/triangle/rectangle [параметры]"
        }
    }

    private fun calculateVolume(expression: String): String {
        val numbers = extractNumbers(expression)
        return when {
            expression.contains("sphere") -> {
                val radius = numbers.firstOrNull() ?: 1.0
                val volume = 4.0 / 3.0 * PI * radius * radius * radius
                """
                    📐 Объем сферы:
                    Радиус: r = $radius
                    Формула: V = ⁴/₃ × π × r³
                    Вычисление: V = 4/3 × ${PI.format(2)} × $radius³ = ${volume.format(3)}
                    Ответ: V ≈ ${volume.format(3)}
                """.trimIndent()
            }
            expression.contains("cube") -> {
                val side = numbers.firstOrNull() ?: 1.0
                val volume = side * side * side
                """
                    📐 Объем куба:
                    Сторона: a = $side
                    Формула: V = a³
                    Вычисление: V = $side³ = ${volume.format(3)}
                    Ответ: V = ${volume.format(3)}
                """.trimIndent()
            }
            expression.contains("cylinder") -> {
                if (numbers.size >= 2) {
                    val volume = PI * numbers[0] * numbers[0] * numbers[1]
                    """
                        📐 Объем цилиндра:
                        Радиус: r = ${numbers[0]}
                        Высота: h = ${numbers[1]}
                        Формула: V = π × r² × h
                        Вычисление: V = ${PI.format(2)} × ${numbers[0]}² × ${numbers[1]} = ${volume.format(3)}
                        Ответ: V ≈ ${volume.format(3)}
                    """.trimIndent()
                } else {
                    "❌ Укажите радиус и высоту: volume cylinder 3 5"
                }
            }
            else -> "❌ Укажите фигуру: volume sphere/cube/cylinder [параметры]"
        }
    }

    private fun calculatePerimeter(expression: String): String {
        val numbers = extractNumbers(expression)
        return when {
            expression.contains("circle") -> {
                val radius = numbers.firstOrNull() ?: 1.0
                val perimeter = 2 * PI * radius
                """
                    📐 Длина окружности:
                    Радиус: r = $radius
                    Формула: C = 2 × π × r
                    Вычисление: C = 2 × ${PI.format(2)} × $radius = ${perimeter.format(3)}
                    Ответ: C ≈ ${perimeter.format(3)}
                """.trimIndent()
            }
            expression.contains("rectangle") -> {
                if (numbers.size >= 2) {
                    val perimeter = 2 * (numbers[0] + numbers[1])
                    """
                        📐 Периметр прямоугольника:
                        Длина: a = ${numbers[0]}
                        Ширина: b = ${numbers[1]}
                        Формула: P = 2 × (a + b)
                        Вычисление: P = 2 × (${numbers[0]} + ${numbers[1]}) = ${perimeter.format(3)}
                        Ответ: P = ${perimeter.format(3)}
                    """.trimIndent()
                } else {
                    "❌ Укажите длину и ширину: perimeter rectangle 4 3"
                }
            }
            expression.contains("triangle") -> {
                if (numbers.size >= 3) {
                    val perimeter = numbers[0] + numbers[1] + numbers[2]
                    """
                        📐 Периметр треугольника:
                        Сторона a = ${numbers[0]}
                        Сторона b = ${numbers[1]}
                        Сторона c = ${numbers[2]}
                        Формула: P = a + b + c
                        Вычисление: P = ${numbers[0]} + ${numbers[1]} + ${numbers[2]} = ${perimeter.format(3)}
                        Ответ: P = ${perimeter.format(3)}
                    """.trimIndent()
                } else {
                    "❌ Укажите три стороны: perimeter triangle 3 4 5"
                }
            }
            else -> "❌ Укажите фигуру: perimeter circle/rectangle/triangle [параметры]"
        }
    }

    private fun solveTrigonometry(expression: String): String {
        return when {
            expression.contains("sin") || expression.contains("cos") || expression.contains("tan") -> {
                calculateTrigFunction(expression)
            }
            expression.contains("identity") -> {
                """
                    📐 Основные тригонометрические тождества:
                    • sin²θ + cos²θ = 1
                    • 1 + tan²θ = sec²θ
                    • 1 + cot²θ = csc²θ
                    • sin(α ± β) = sinα cosβ ± cosα sinβ
                    • cos(α ± β) = cosα cosβ ∓ sinα sinβ
                    • sin(2θ) = 2 sinθ cosθ
                    • cos(2θ) = cos²θ - sin²θ
                """.trimIndent()
            }
            expression.contains("angle") -> {
                val angle = extractNumber(expression) ?: 30.0
                val rad = Math.toRadians(angle)
                """
                    📐 Тригонометрические функции угла:
                    Угол: $angle° (${rad.format(4)} рад)
                    sin($angle°) = ${sin(rad).format(4)}
                    cos($angle°) = ${cos(rad).format(4)}
                    tan($angle°) = ${tan(rad).format(4)}
                    cot($angle°) = ${(1.0 / tan(rad)).format(4)}
                """.trimIndent()
            }
            else -> {
                """
                    📐 Тригонометрический калькулятор:
                    • sin(угол) - синус угла в градусах
                    • cos(угол) - косинус угла в градусах
                    • tan(угол) - тангенс угла в градусах
                    • angle [значение] - все функции для угла
                    • identity - основные тождества
                """.trimIndent()
            }
        }
    }

    private fun calculateTrigFunction(expression: String): String {
        val angle = extractNumber(expression) ?: 30.0
        val rad = Math.toRadians(angle)

        return when {
            expression.contains("sin") -> """
                📐 Синус угла:
                Угол: $angle° (${rad.format(4)} рад)
                sin($angle°) = ${sin(rad).format(4)}
            """.trimIndent()
            expression.contains("cos") -> """
                📐 Косинус угла:
                Угол: $angle° (${rad.format(4)} рад)
                cos($angle°) = ${cos(rad).format(4)}
            """.trimIndent()
            expression.contains("tan") -> """
                📐 Тангенс угла:
                Угол: $angle° (${rad.format(4)} рад)
                tan($angle°) = ${tan(rad).format(4)}
            """.trimIndent()
            else -> """
                📐 Тригонометрические функции:
                Угол: $angle° (${rad.format(4)} рад)
                sin = ${sin(rad).format(4)}, cos = ${cos(rad).format(4)}, tan = ${tan(rad).format(4)}
            """.trimIndent()
        }
    }

    private fun solveCombinatorics(expression: String): String {
        return when {
            expression.contains("factorial") || expression.contains("!") -> {
                val n = extractNumber(expression)?.toLong() ?: 5L
                if (n < 0) return "❌ Факториал определен только для неотрицательных чисел"
                try {
                    val result = CombinatoricsUtils.factorial(n.toInt())
                    """
                        📊 Факториал:
                        $n! = $result
                    """.trimIndent()
                } catch (e: Exception) {
                    "❌ Факториал $n! слишком велик"
                }
            }
            expression.contains("combination") || expression.contains("C(") -> {
                val numbers = extractNumbers(expression).map { it.toInt() }
                if (numbers.size >= 2) {
                    val n = numbers[0]
                    val k = numbers[1]
                    if (k > n) return "❌ k не может быть больше n"
                    try {
                        val result = CombinatoricsUtils.binomialCoefficient(n, k)
                        """
                            📊 Сочетания:
                            C($n,$k) = $result
                        """.trimIndent()
                    } catch (e: Exception) {
                        "❌ Невозможно вычислить C($n,$k)"
                    }
                } else {
                    "❌ Укажите n и k: combination 10 3"
                }
            }
            expression.contains("permutation") || expression.contains("P(") -> {
                val numbers = extractNumbers(expression).map { it.toInt() }
                if (numbers.size >= 2) {
                    val n = numbers[0]
                    val k = numbers[1]
                    if (k > n) return "❌ k не может быть больше n"
                    try {
                        val result = CombinatoricsUtils.factorial(n) / CombinatoricsUtils.factorial(n - k)
                        """
                            📊 Размещения:
                            P($n,$k) = $result
                        """.trimIndent()
                    } catch (e: Exception) {
                        "❌ Невозможно вычислить P($n,$k)"
                    }
                } else {
                    "❌ Укажите n и k: permutation 5 2"
                }
            }
            else -> {
                """
                    📊 Комбинаторика:
                    • factorial n - факториал числа
                    • combination n k - число сочетаний
                    • permutation n k - число размещений
                """.trimIndent()
            }
        }
    }

    // Вспомогательные методы
    private fun extractNumber(expression: String): Double? {
        val pattern = """-?\d+\.?\d*""".toRegex()
        return pattern.find(expression)?.value?.toDoubleOrNull()
    }

    private fun extractNumbers(expression: String): List<Double> {
        val pattern = """-?\d+\.?\d*""".toRegex()
        return pattern.findAll(expression).map { it.value.toDouble() }.toList()
    }

    private fun Double.format(digits: Int) = "%.${digits}f".format(this)

    private fun Double.toCleanString(): String {
        return if (this == this.toInt().toDouble()) this.toInt().toString() else this.format(2)
    }

    private fun Double.toSignedString(): String {
        return if (this >= 0) "+ ${this.toCleanString()}" else "- ${(-this).toCleanString()}"
    }
}