package com.example.mathlab.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.data.MathItemData
import com.example.domain.data.stateMathlabCategory.MathCategory
import com.example.mathlab.R
import com.example.mathlab.view.components.ItemMath
import com.example.mathlab.view.components.TopBarMathLab
import kotlin.text.category


@Composable
@Preview
fun MathLabMainScreen(
    backStack: Boolean = false,
    onCategoryClick: (MathCategory) -> Unit = {}
){
    val items: List<MathItemData> = listOf(
        MathItemData(
            icon = R.drawable.algebra,
            title = "Алгебра",
            description = "Тренируйте свою логику",
            MathCategory.ALGEBRA
        ),
        MathItemData(
            icon = R.drawable.geometry,
            title = "Геометрия",
            description = "Визуализируйте формы",
            MathCategory.GEOMETRY
        ),
        MathItemData(
            icon = R.drawable.trigonometry,
            title = "Тригонометрия",
            description = "Исследуйте углы обзора",
            MathCategory.TRIGONOMETRY
        ),
        MathItemData(
            icon = R.drawable.combinatrics,
            title = "Комбинаторика",
            description = "Мастер подсчета голосов",
            MathCategory.COMBINATORICS
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize()
        .background(colorResource(R.color.backGroundColorMain)),
        topBar = {
            TopBarMathLab(
               backStack = backStack
            ) {

            }
        }

    ) { paddingValues ->
        BottomScreenMain(
            paddingValues = paddingValues,
            items = items,
            onClickItemData = { mathItem: MathItemData ->
                onCategoryClick(mathItem.category)
            }
        )
    }
}



@Composable
fun BottomScreenMain(
    paddingValues: PaddingValues,
    items: List<MathItemData>,
    onClickItemData: (MathItemData) -> Unit
){

    val state = rememberLazyGridState()

    LazyVerticalGrid(
        state = state,
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 5.dp)
            .background(colorResource(R.color.backGroundColorMain)),
        horizontalArrangement = Arrangement
            .spacedBy(13.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp)
    ) {



        item(span = {GridItemSpan(maxLineSpan) }){
            Text(
                text = "Добро пожаловать!",
                color = colorResource(R.color.iconColor),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 35.dp)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally),
                fontSize = 28.sp
            )
        }

        item(span = {GridItemSpan(maxLineSpan) }){
            Text(
                text = "Выберите тему для начала",
                modifier = Modifier.fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .padding(bottom = 35.dp),
                color = colorResource(R.color.textcolorTheme),
                fontSize = 18.sp
            )
        }


        items(items = items, key = {it.category}){ item ->
            ItemMath(
                modifier = Modifier,
                icon = item.icon,
                textMath = item.title,
                textDescriptionMath = item.description,
                category = item.category,
                onClickItem = onClickItemData
            )
        }



    }
}

