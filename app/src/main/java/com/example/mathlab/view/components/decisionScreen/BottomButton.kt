package com.example.mathlab.view.components.decisionScreen

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
@Preview(showBackground = true)
fun BottomButton(
    modifier: Modifier = Modifier,
    buttomText: String = "Проверить",
    onClick: () -> Unit = {}
){

    Button(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Blue
        ),
        shape = RoundedCornerShape(17.dp)
    ) {
        Text(
            text = buttomText,
            fontSize = 20.sp
        )
    }
}