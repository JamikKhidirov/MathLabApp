package com.example.mathlab.view.components.decisionScreen

import androidx.camera.core.Preview
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp



@Composable
@androidx.compose.ui.tooling.preview.Preview
fun BottomBar(
    modifier: Modifier = Modifier,
    onClickButton: () -> Unit = {}
){
    BottomAppBar(
        modifier = modifier.fillMaxWidth().navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.background,
        contentPadding = PaddingValues(8.dp),
    ) {
        BottomButton(
            modifier = Modifier.fillMaxSize(),
            onClick = onClickButton
        )
    }
}