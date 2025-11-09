package com.example.mathlab.view.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mathlab.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun TopBarMathLab(
    modifier: Modifier = Modifier,
    textTitle: String = "MathLab",
    backStack: Boolean = true,
    onBackIconClick: () -> Unit = {}
    ){

    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(R.color.colorBackItem)
        ),
        title = {
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                TypeAnimationText(
                    text = textTitle,
                    modifier = Modifier,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.black)
                )
            }
        },
        navigationIcon = {
            if (backStack){
                IconButton(modifier = Modifier.size(32.dp),
                    onClick = onBackIconClick) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "backIcon",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    )
}