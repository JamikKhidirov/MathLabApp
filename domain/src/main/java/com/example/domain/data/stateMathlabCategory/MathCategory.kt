package com.example.domain.data.stateMathlabCategory

import kotlinx.serialization.Serializable


enum class MathCategory {
    ALGEBRA,
    GEOMETRY,
    TRIGONOMETRY,
    COMBINATORICS,
}


fun MathCategory.displayName(): String = when (this) {
    MathCategory.ALGEBRA -> "Алгебра"
    MathCategory.GEOMETRY -> "Геометрия"
    MathCategory.TRIGONOMETRY -> "Тригонометрия"
    MathCategory.COMBINATORICS -> "Комбинаторика"
}



// Вспомогательные функции
fun getPlaceholderForCategory(category: MathCategory): String {
    return when (category) {
        MathCategory.ALGEBRA -> "x^2 - 5*x + 6 = 0"
        MathCategory.GEOMETRY -> "area circle 5"
        MathCategory.TRIGONOMETRY -> "sin(30)"
        MathCategory.COMBINATORICS -> "factorial 5"
    }
}

fun getHintForCategory(category: MathCategory): String {
    return when (category) {
        MathCategory.ALGEBRA -> "Используйте x как переменную. Пример: 2*x + 3 = 7"
        MathCategory.GEOMETRY -> "Используйте: area/volume/perimeter circle/triangle/rectangle"
        MathCategory.TRIGONOMETRY -> "sin/cos/tan(угол), angle значение, identity для тождеств"
        MathCategory.COMBINATORICS -> "factorial n, combination n k, permutation n k"
    }
}

fun getFormatExamples(category: MathCategory): String {
    return when (category) {
        MathCategory.ALGEBRA -> "• x^2 - 5x + 6 = 0\n• 2x + 3 = 7\n• x^3 - 2x - 5 = 0"
        MathCategory.GEOMETRY -> "• area circle 5\n• volume sphere 3\n• perimeter rectangle 4 6"
        MathCategory.TRIGONOMETRY -> "• sin(30)\n• cos(45)\n• trig identity\n• angle 60"
        MathCategory.COMBINATORICS -> "• factorial 5\n• combination 10 3\n• permutation 5 2"
    }
}

private fun isValidExpression(expression: String, category: MathCategory): Boolean {
    if (expression.isBlank()) return true
    return when (category) {
        MathCategory.ALGEBRA -> expression.contains(Regex("[x0-9+\\-*/^=()]"))
        MathCategory.GEOMETRY -> expression.contains(Regex("[a-zA-Z0-9 ]"))
        MathCategory.TRIGONOMETRY -> expression.contains(Regex("[a-zA-Z0-9()]"))
        MathCategory.COMBINATORICS -> expression.contains(Regex("[a-zA-Z0-9 ]"))
    }
}