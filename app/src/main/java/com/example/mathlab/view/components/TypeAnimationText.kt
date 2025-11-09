package com.example.mathlab.view.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.TextUnit
import kotlinx.coroutines.delay


@Composable
fun TypeAnimationText(
    text: String,
    color: Color,
    fontSize: TextUnit,
    fontWeight: FontWeight,
    modifier: Modifier,
    letterDelayMillis: Long = 25L
){
    var visibleTextLenght by remember { mutableStateOf(0) }

    LaunchedEffect(text) {
        visibleTextLenght = 0
        for (i in text.indices){
            delay(letterDelayMillis)
            visibleTextLenght = i + 1

        }
    }

    Box(
        modifier = Modifier.fillMaxWidth().then(modifier),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = text.take(visibleTextLenght),
            modifier = modifier,
            fontSize = fontSize,
            fontWeight = fontWeight,
            color = color
        )
    }
}