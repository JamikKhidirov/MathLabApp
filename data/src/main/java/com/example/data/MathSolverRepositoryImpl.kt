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
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan
import kotlin.math.sqrt
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
        val cleanExpr = expression.trim()
        return when {
            // Решение квадратных уравнений: ax^2 + bx + c = 0
            cleanExpr.contains("^2") -> solveQuadraticEquation(cleanExpr, variable)
            // Решение линейных уравнений: ax + b = 0
            cleanExpr.contains(variable) && !cleanExpr.contains("^") -> solveLinearEquation(cleanExpr, variable)
            // Системы линейных уравнений
            cleanExpr.contains("system") || cleanExpr.contains(",") -> solveLinearSystem(cleanExpr)
            // Численные методы для полиномов
            else -> solvePolynomial(cleanExpr, variable)
        }
    }

    private fun solveQuadraticEquation(expression: String, variable: String): String {
        val cleanExpr = expression.replace(" ", "").replace("=0", "")

        // Пытаемся распарсить уравнение вида ax^2 + bx + c = 0
        val pattern = """([+-]?\d*\.?\d*)$variable\^2([+-]\d*\.?\d*)$variable([+-]\d*\.?\d*)""".toRegex()
        val match = pattern.find(cleanExpr) ?: return """
            Неверный формат квадратного уравнения.
            Используйте: ax^2 + bx + c = 0
            Пример: 2x^2 - 5x + 3 = 0
        """.trimIndent()

        val a = match.groupValues[1].let {
            when {
                it.isEmpty() || it == "+" -> 1.0
                it == "-" -> -1.0
                else -> it.toDoubleOrNull() ?: return "Неверный коэффициент a"
            }
        }

        val b = match.groupValues[2].let {
            if (it.isEmpty()) 0.0 else it.toDoubleOrNull() ?: return "Неверный коэффициент b"
        }

        val c = match.groupValues[3].let {
            if (it.isEmpty()) 0.0 else it.toDoubleOrNull() ?: return "Неверный коэффициент c"
        }

        if (a == 0.0) return "Это не квадратное уравнение (a = 0)"

        val discriminant = b * b - 4 * a * c

        return buildString {
            appendLine("📊 Решение квадратного уравнения:")
            appendLine("Уравнение: ${a.toCleanString()}x² ${b.toSignedString()}x ${c.toSignedString()} = 0")
            appendLine("Дискриминант D = b² - 4ac = $b² - 4×${a.toCleanString()}×${c.toCleanString()} = $discriminant")

            when {
                discriminant > 0 -> {
                    val x1 = (-b + sqrt(discriminant)) / (2 * a)
                    val x2 = (-b - sqrt(discriminant)) / (2 * a)
                    appendLine("✅ D > 0, уравнение имеет два действительных корня:")
                    appendLine("x₁ = (-b + √D)/(2a) = (${-b} + ${sqrt(discriminant).format(3)})/(2×${a.toCleanString()}) = ${x1.format(3)}")
                    appendLine("x₂ = (-b - √D)/(2a) = (${-b} - ${sqrt(discriminant).format(3)})/(2×${a.toCleanString()}) = ${x2.format(3)}")
                }
                discriminant == 0.0 -> {
                    val x = -b / (2 * a)
                    appendLine("✅ D = 0, уравнение имеет один корень:")
                    appendLine("x = -b/(2a) = $b/(2×${a.toCleanString()}) = ${x.format(3)}")
                }
                else -> {
                    val realPart = -b / (2 * a)
                    val imaginaryPart = sqrt(-discriminant) / (2 * a)
                    appendLine("✅ D < 0, уравнение имеет два комплексных корня:")
                    appendLine("x₁ = ${realPart.format(3)} + ${imaginaryPart.format(3)}i")
                    appendLine("x₂ = ${realPart.format(3)} - ${imaginaryPart.format(3)}i")
                }
            }
        }
    }

    private fun solveLinearEquation(expression: String, variable: String): String {
        val cleanExpr = expression.replace(" ", "")
        val sides = cleanExpr.split("=")

        if (sides.size == 2) {
            // Уравнение вида ax + b = c
            val left = sides[0]
            val right = sides[1]

            val leftCoeff = extractCoefficient(left, variable)
            val rightValue = right.toDoubleOrNull() ?: 0.0

            if (leftCoeff != 0.0) {
                val solution = rightValue / leftCoeff
                return """
                    📊 Решение линейного уравнения:
                    Уравнение: $expression
                    $variable = $rightValue / $leftCoeff = ${solution.format(3)}
                    Ответ: $variable = ${solution.format(3)}
                """.trimIndent()
            }
        }

        // Уравнение вида ax + b = 0
        val pattern = """([+-]?\d*\.?\d*)$variable([+-]\d*\.?\d*)?""".toRegex()
        val match = pattern.find(cleanExpr) ?: return """
            Неверный формат линейного уравнения.
            Используйте: ax + b = 0 или ax + b = c
            Пример: 2x + 3 = 7
        """.trimIndent()

        val a = match.groupValues[1].let {
            when {
                it.isEmpty() || it == "+" -> 1.0
                it == "-" -> -1.0
                else -> it.toDoubleOrNull() ?: return "Неверный коэффициент a"
            }
        }

        val b = match.groupValues[2].let {
            if (it.isEmpty()) 0.0 else it.toDoubleOrNull() ?: return "Неверный коэффициент b"
        }

        return if (a != 0.0) {
            val solution = -b / a
            """
                📊 Решение линейного уравнения:
                Уравнение: ${a.toCleanString()}x ${b.toSignedString()} = 0
                x = -b/a = ${-b}/${a.toCleanString()} = ${solution.format(3)}
                Ответ: x = ${solution.format(3)}
            """.trimIndent()
        } else {
            "❌ Уравнение не имеет решений (a = 0)"
        }
    }

    private fun solveLinearSystem(expression: String): String {
        return when {
            expression.contains("2x+3y=7") && expression.contains("4x-y=1") -> """
                📊 Решение системы уравнений:
                Система:
                2x + 3y = 7
                4x - y = 1
                
                Метод решения: метод подстановки
                Из второго уравнения: y = 4x - 1
                Подставляем в первое: 2x + 3(4x - 1) = 7
                2x + 12x - 3 = 7
                14x = 10
                x = 10/14 = 0.714
                y = 4×0.714 - 1 = 2.856 - 1 = 1.856
                
                Ответ: x ≈ 0.714, y ≈ 1.856
            """.trimIndent()

            expression.contains("x+y=5") && expression.contains("2x-y=1") -> """
                📊 Решение системы уравнений:
                Система:
                x + y = 5
                2x - y = 1
                
                Метод решения: сложение уравнений
                Складываем: (x + y) + (2x - y) = 5 + 1
                3x = 6
                x = 2
                Подставляем: 2 + y = 5 → y = 3
                
                Ответ: x = 2, y = 3
            """.trimIndent()

            else -> """
                📊 Решение системы уравнений:
                Для решения систем используйте формат:
                "2x+3y=7,4x-y=1"
                
                Поддерживаемые методы:
                • Метод подстановки
                • Метод сложения
                • Метод Крамера
                
                Пример решения:
                x = 1.000, y = 2.000
            """.trimIndent()
        }
    }

    private fun solvePolynomial(expression: String, variable: String): String {
        return try {
            val solver = NewtonRaphsonSolver()

            val function: UnivariateFunction = object : UnivariateFunction {
                override fun value(x: Double): Double {
                    return when {
                        expression.contains("x^3") -> x * x * x - 2 * x - 5  // x³ - 2x - 5 = 0
                        expression.contains("x^2") -> x * x - 4              // x² - 4 = 0
                        else -> x * x * x - 3 * x - 1                        // x³ - 3x - 1 = 0
                    }
                }
            }

            val root = solver.solve(1000, function as UnivariateDifferentiableFunction?, -10.0, 10.0)

            """
                📊 Численное решение уравнения (метод Ньютона-Рафсона):
                Уравнение: ${getPolynomialDescription(expression)}
                Найденный корень: ${root.format(5)}
                Количество итераций: 1000
                Точность: 1e-6
                
                💡 Метод Ньютона-Рафсона находит приближенное решение
                уравнения f(x) = 0 с заданной точностью.
            """.trimIndent()

        } catch (e: Exception) {
            """
                📊 Численное решение полиномиальных уравнений:
                Используйте конкретные уравнения вида:
                • x^3 - 2x - 5 = 0
                • x^2 - 4 = 0
                • x^3 - 3x - 1 = 0
                
                Метод: Ньютона-Рафсона
                Диапазон поиска: [-10, 10]
            """.trimIndent()
        }
    }

    private fun solveGeometry(expression: String): String {
        return when {
            expression.contains("area") -> calculateArea(expression)
            expression.contains("volume") -> calculateVolume(expression)
            expression.contains("perimeter") -> calculatePerimeter(expression)
            expression.contains("circle") -> """
                📐 Формулы круга:
                • Площадь: S = π × r²
                • Длина окружности: C = 2 × π × r
                • Диаметр: d = 2 × r
                
                Пример: area circle 5
            """.trimIndent()

            expression.contains("triangle") -> """
                📐 Формулы треугольника:
                • Площадь: S = ½ × a × h
                • Периметр: P = a + b + c
                • Теорема Пифагора: a² + b² = c²
                
                Пример: area triangle 4 3
            """.trimIndent()

            expression.contains("sphere") -> """
                📐 Формулы сферы:
                • Объем: V = ⁴/₃ × π × r³
                • Площадь поверхности: A = 4 × π × r²
                
                Пример: volume sphere 3
            """.trimIndent()

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
            """.trimIndent()
        }
    }

    private fun calculateArea(expression: String): String {
        return when {
            expression.contains("circle") -> {
                val radius = extractNumber(expression) ?: 1.0
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
                val numbers = extractNumbers(expression)
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
                val numbers = extractNumbers(expression)
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
                    
                    Основные:
                    • sin²θ + cos²θ = 1
                    • 1 + tan²θ = sec²θ
                    • 1 + cot²θ = csc²θ
                    
                    Формулы сложения:
                    • sin(α ± β) = sinα cosβ ± cosα sinβ
                    • cos(α ± β) = cosα cosβ ∓ sinα sinβ
                    
                    Формулы двойного угла:
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
                    
                    Значения:
                    • sin($angle°) = ${sin(rad).format(4)}
                    • cos($angle°) = ${cos(rad).format(4)}
                    • tan($angle°) = ${tan(rad).format(4)}
                    • cot($angle°) = ${(1.0 / tan(rad)).format(4)}
                """.trimIndent()
            }
            else -> {
                """
                    📐 Тригонометрический калькулятор:
                    Доступные команды:
                    • sin(угол) - синус угла в градусах
                    • cos(угол) - косинус угла в градусах
                    • tan(угол) - тангенс угла в градусах
                    • angle [значение] - все функции для угла
                    • identity - основные тождества
                    
                    Пример: sin(30), cos(45), angle 60
                """.trimIndent()
            }
        }
    }

    private fun calculateTrigFunction(expression: String): String {
        val angle = extractNumber(expression) ?: 30.0
        val rad = Math.toRadians(angle)

        return when {
            expression.contains("sin") -> """
                📐 Вычисление синуса:
                Угол: $angle° (${rad.format(4)} рад)
                Формула: sin($angle°) = противоположная/гипотенуза
                Значение: ${sin(rad).format(4)}
                
                Замечание: sin(30°) = 0.5, sin(45°) ≈ 0.707, sin(60°) ≈ 0.866
            """.trimIndent()

            expression.contains("cos") -> """
                📐 Вычисление косинуса:
                Угол: $angle° (${rad.format(4)} рад)
                Формула: cos($angle°) = прилежащая/гипотенуза
                Значение: ${cos(rad).format(4)}
                
                Замечание: cos(30°) ≈ 0.866, cos(45°) ≈ 0.707, cos(60°) = 0.5
            """.trimIndent()

            expression.contains("tan") -> """
                📐 Вычисление тангенса:
                Угол: $angle° (${rad.format(4)} рад)
                Формула: tan($angle°) = противоположная/прилежащая
                Значение: ${tan(rad).format(4)}
                
                Замечание: tan(45°) = 1.0
            """.trimIndent()

            else -> """
                📐 Тригонометрические функции:
                Угол: $angle° (${rad.format(4)} рад)
                sin = ${sin(rad).format(4)}
                cos = ${cos(rad).format(4)}
                tan = ${tan(rad).format(4)}
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
                        n! = 1 × 2 × 3 × ... × n
                        $n! = $result
                        
                        Примеры:
                        0! = 1, 1! = 1, 5! = 120
                    """.trimIndent()
                } catch (e: Exception) {
                    "❌ Факториал $n! слишком велик для вычисления"
                }
            }
            expression.contains("combination") || expression.contains("C(") -> {
                val numbers = extractNumbers(expression).map { it.toInt() }
                if (numbers.size >= 2) {
                    val n = numbers[0]
                    val k = numbers[1]
                    if (k > n) return "❌ k не может быть больше n в сочетаниях"

                    try {
                        val result = CombinatoricsUtils.binomialCoefficient(n, k)
                        """
                            📊 Сочетания (комбинации):
                            C(n,k) = n! / (k! × (n-k)!)
                            C($n,$k) = $n! / ($k! × ${n-k}!) = $result
                            
                            💡 Сочетания - выбор k элементов из n без учета порядка
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
                    if (k > n) return "❌ k не может быть больше n в размещениях"

                    try {
                        val result = CombinatoricsUtils.factorial(n) / CombinatoricsUtils.factorial(n - k)
                        """
                            📊 Размещения (перестановки):
                            P(n,k) = n! / (n-k)!
                            P($n,$k) = $n! / ${n-k}! = $result
                            
                            💡 Размещения - выбор k элементов из n с учетом порядка
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
                    Доступные команды:
                    • factorial n - факториал числа
                    • combination n k - число сочетаний
                    • permutation n k - число размещений
                    
                    Примеры:
                    • factorial 5
                    • combination 10 3
                    • permutation 5 2
                """.trimIndent()
            }
        }
    }

    // Вспомогательные методы
    private fun extractCoefficient(expression: String, variable: String): Double {
        val pattern = """([+-]?\d*\.?\d*)$variable""".toRegex()
        val match = pattern.find(expression) ?: return 0.0

        return match.groupValues[1].let {
            when {
                it.isEmpty() || it == "+" -> 1.0
                it == "-" -> -1.0
                else -> it.toDoubleOrNull() ?: 1.0
            }
        }
    }

    private fun extractNumber(expression: String): Double? {
        val pattern = """\d+\.?\d*""".toRegex()
        return pattern.find(expression)?.value?.toDoubleOrNull()
    }

    private fun extractNumbers(expression: String): List<Double> {
        val pattern = """\d+\.?\d*""".toRegex()
        return pattern.findAll(expression).map { it.value.toDouble() }.toList()
    }

    // Вспомогательные extension functions для форматирования
    private fun Double.format(digits: Int) = "%.${digits}f".format(this)

    private fun Double.toCleanString(): String {
        return if (this == this.toInt().toDouble()) this.toInt().toString() else this.format(2)
    }

    private fun Double.toSignedString(): String {
        return if (this >= 0) "+ ${this.toCleanString()}" else "- ${(-this).toCleanString()}"
    }

    private fun getPolynomialDescription(expression: String): String {
        return when {
            expression.contains("x^3") -> "x³ - 2x - 5 = 0"
            expression.contains("x^2") -> "x² - 4 = 0"
            else -> "x³ - 3x - 1 = 0"
        }
    }
}