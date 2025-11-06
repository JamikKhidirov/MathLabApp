package com.example.mathlab.viewModal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.data.stateMathlabCategory.MathCategory
import com.example.domain.data.stateScreen.MathProblem
import com.example.domain.data.usecase.SolveMathProblemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject




@HiltViewModel
class MathViewModel @Inject constructor(
    private val solveMathProblem: SolveMathProblemUseCase
) : ViewModel() {

    private val _result = MutableStateFlow("")
    val result: StateFlow<String> = _result

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun solve(expression: String, category: MathCategory) {
        if (expression.isBlank()) {
            _result.value = "Введите выражение"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val problem = MathProblem(
                    expression = expression.trim(),
                    category = category,
                    variable = "x" // или можно определить из выражения
                )
                _result.value = solveMathProblem(problem)
            } catch (e: Exception) {
                _result.value = "Ошибка: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}