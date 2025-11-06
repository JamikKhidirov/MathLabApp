package com.example.mathlab.view.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.domain.data.stateMathlabCategory.MathCategory
import com.example.domain.data.stateMathlabCategory.displayName
import com.example.mathlab.R
import com.example.mathlab.view.components.TopBarMathLab
import com.example.mathlab.view.components.decisionScreen.BottomBar
import com.example.mathlab.viewModal.MathViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecisionScreen(
    category: MathCategory = MathCategory.ALGEBRA,
    bacStack: Boolean = false,
    viewModel: MathViewModel = hiltViewModel(),
    onBackIconTopAppBarClick: () -> Unit
) {
    val result by viewModel.result.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var expression by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    // Показываем snackbar при ошибках
    LaunchedEffect(result) {
        if (result.startsWith("Ошибка:")) {
            snackbarHostState.showSnackbar(
                message = "Не удалось решить задачу",
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopBarMathLab(
                textTitle = category.displayName(),
                backStack = bacStack,
                onBackIconClick = onBackIconTopAppBarClick
            )
        },

    ) { paddingValues ->
        BottomDecisionScreen(
            paddingValues = paddingValues,
            category = category,
            expression = expression,
            isLoading = isLoading,
            result = result,
            onNewFildText = { newString ->
                expression = newString
            },
            onSolveClick = {
                if (expression.isNotBlank()) {
                    viewModel.solve(expression, category)
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Введите выражение",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            },
            onCopyResult = {
                clipboardManager.setText(AnnotatedString(result))
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Результат скопирован",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        )
    }
}

@Composable
fun BottomDecisionScreen(
    paddingValues: PaddingValues,
    category: MathCategory,
    expression: String,
    isLoading: Boolean,
    result: String,
    onNewFildText: (String) -> Unit,
    onSolveClick: () -> Unit,
    onCopyResult: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isError = result.startsWith("Ошибка:")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Заголовок с иконкой
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = colorResource(R.color.colorBackItem),
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Решатель задач",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Тема: ${category.displayName()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Поле ввода
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Введите математическое выражение:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = expression,
                    onValueChange = onNewFildText,
                    placeholder = {
                        Text(
                            text = getPlaceholderForCategory(category),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    ),
                    singleLine = true
                )

                // Подсказки под полем ввода
                AnimatedVisibility(
                    visible = expression.isNotBlank(),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = getHintForCategory(category),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // Кнопка решения с анимацией загрузки
        AnimatedVisibility(
            visible = expression.isNotBlank(),
            enter = slideInVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Button(
                onClick = onSolveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = !isLoading && expression.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Решаем...")
                } else {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Решить задачу",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        // Индикатор загрузки
        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Вычисляем...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Блок с результатом
        AnimatedVisibility(
            visible = result.isNotBlank(),
            enter = slideInVertically(
                animationSpec = tween(durationMillis = 500)
            ) + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(12.dp),
                        clip = false
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isError) {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        MaterialTheme.colorScheme.primaryContainer
                    }
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Заголовок результата
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isError) "Ошибка" else "Результат",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isError) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )

                        IconButton(
                            onClick = onCopyResult,
                            enabled = !isError
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Копировать результат",
                                tint = if (isError) {
                                    MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Иконка статуса
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                if (isError) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isError) Icons.Default.Error else Icons.Default.Calculate,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = if (isError) {
                                MaterialTheme.colorScheme.onError
                            } else {
                                MaterialTheme.colorScheme.onPrimary
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Текст результата
                    Text(
                        text = if (isError) result.substringAfter("Ошибка:") else result,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isError) {
                            MaterialTheme.colorScheme.onErrorContainer
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    // Дополнительная информация для успешных результатов
                    AnimatedVisibility(
                        visible = !isError && result.isNotBlank(),
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Задача успешно решена!",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Подсказки по формату ввода
        if (expression.isBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "💡 Подсказки по формату:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = getFormatExamples(category),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // Автоскролл к результату
    LaunchedEffect(result) {
        if (result.isNotBlank()) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }
}

// Вспомогательные функции для UI
private fun getPlaceholderForCategory(category: MathCategory): String {
    return when (category) {
        MathCategory.ALGEBRA -> "x^2 - 5*x + 6 = 0"
        MathCategory.GEOMETRY -> "area circle 5"
        MathCategory.TRIGONOMETRY -> "sin(30)"
        MathCategory.COMBINATORICS -> "factorial 5"
    }
}

private fun getHintForCategory(category: MathCategory): String {
    return when (category) {
        MathCategory.ALGEBRA -> "Используйте x как переменную. Пример: 2*x + 3 = 7"
        MathCategory.GEOMETRY -> "Используйте: area/volume/perimeter circle/triangle/rectangle"
        MathCategory.TRIGONOMETRY -> "sin/cos/tan(угол), angle значение, identity для тождеств"
        MathCategory.COMBINATORICS -> "factorial n, combination n k, permutation n k"
    }
}

private fun getFormatExamples(category: MathCategory): String {
    return when (category) {
        MathCategory.ALGEBRA -> "• x^2 - 5x + 6 = 0\n• 2x + 3 = 7\n• x^3 - 2x - 5 = 0"
        MathCategory.GEOMETRY -> "• area circle 5\n• volume sphere 3\n• perimeter rectangle 4 6"
        MathCategory.TRIGONOMETRY -> "• sin(30)\n• cos(45)\n• trig identity\n• angle 60"
        MathCategory.COMBINATORICS -> "• factorial 5\n• combination 10 3\n• permutation 5 2"
    }
}
