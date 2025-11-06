package com.example.domain.data.repository

import com.example.domain.data.stateScreen.MathProblem


interface MathSolverRepository {

    suspend fun solve(problem: MathProblem): String
}