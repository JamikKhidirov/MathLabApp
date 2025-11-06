package com.example.mathlab.view.components.decisionScreen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imeAnimationSource
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemGestures
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mathlab.ui.theme.MathLabTheme


@Composable
@Preview
fun BottomBar(
    modifier: Modifier = Modifier,
    onClickButton: () -> Unit = {}
){

    BottomAppBar(
        modifier = modifier.fillMaxWidth().navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.background,
        contentPadding = PaddingValues(8.dp),
        windowInsets = WindowInsets()
    ) {
        BottomButton(
            modifier = Modifier.fillMaxSize(),
            onClick = onClickButton
        )
    }
}