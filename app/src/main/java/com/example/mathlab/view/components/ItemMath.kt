package com.example.mathlab.view.components

import android.accessibilityservice.GestureDescription
import android.graphics.drawable.Icon
import android.graphics.fonts.Font
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.data.MathItemData
import com.example.domain.data.stateMathlabCategory.MathCategory
import com.example.mathlab.R


@Composable
fun ItemMath(
    modifier: Modifier = Modifier,
    icon: Int? = R.drawable.geometry,
    textMath: String = "Алгебра",
    textDescriptionMath: String = "Тренируйте свою логику",
    category: MathCategory,
    onClickItem: (MathItemData) -> Unit = {}
){
    Card(
        modifier = Modifier.width(120.dp)
            .height(310.dp).then(modifier),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.colorBackItem)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 0.dp
        ),
        onClick = {
            val item = MathItemData(
                icon = icon,
                title = textMath,
                description = textDescriptionMath,
                category = category
            )

            onClickItem(item)

        }
    ) {
        Column(
            modifier = Modifier
        ) {
            icon?.let {
                Icon(
                    painter = painterResource(it),
                    contentDescription = null,
                    tint = colorResource(R.color.iconColor),
                    modifier = Modifier.padding(
                        top = 10.dp,
                        start = 10.dp
                    )
                )
            }

            Text(
                text = textMath,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier.padding(
                    top = if (icon == null) 60.dp else 10.dp,
                ).fillMaxWidth()
                    .padding(horizontal = 5.dp)
            )
            Text(
                text = textDescriptionMath,
                color = colorResource(R.color.textcolorTheme),
                modifier = Modifier.padding(
                    top = 10.dp,
                    start = 10.dp
                )
                    .fillMaxWidth()

            )
        }
    }
}