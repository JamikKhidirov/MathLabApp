package com.example.mathlab.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.data.TaskItem
import com.example.mathlab.R


@Composable
@Preview(showBackground = true)
fun TaskItemView(
    taskItem: TaskItem = TaskItem(
        leadIcon = R.drawable.trigonometry,
        title = "Algebra",
        description = "Мастер по алебре"
    ),
    onClick: () -> Unit = {}
){
    Row (
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(colorResource(R.color.colorBackItem))
            .clickable{
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically

    ){
        Icon(
            painter = painterResource(taskItem.leadIcon),
            contentDescription = null,
            modifier = Modifier
                .padding(10.dp),
            tint = colorResource(R.color.iconColor)

        )

        Column(
            modifier = Modifier
                .padding(start = 5.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(7.dp),


        ) {
            Text(
                text = taskItem.title,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp
            )

            Text(
                text = taskItem.description,
                color = colorResource(R.color.textcolorTheme)
            )


        }

        Icon(
            painter = painterResource(R.drawable.iconback),
            contentDescription = "iconBack",
            tint = colorResource(R.color.textcolorTheme),
            modifier = Modifier.size(32.dp)
                .padding(end = 10.dp)
        )
    }
}