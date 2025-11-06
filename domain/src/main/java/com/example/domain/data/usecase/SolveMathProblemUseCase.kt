package com.example.domain.data.usecase

import com.example.domain.data.repository.MathSolverRepository
import com.example.domain.data.stateScreen.MathProblem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class SolveMathProblemUseCase @Inject constructor(
    private val repository: MathSolverRepository
) {
    suspend operator fun invoke(problem: MathProblem): String {
        return withContext(Dispatchers.IO) {
            repository.solve(problem)
        }
    }
}