package com.example.mathlab.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.domain.data.TaskItem
import com.example.mathlab.R
import com.example.mathlab.view.components.TaskItemView
import com.example.mathlab.view.components.TopBarMathLab


@Composable
@Preview
fun TasksScreen(
    titleText: String = "Test",
    tasksList: List<TaskItem> = listOf(
        TaskItem(
            leadIcon = R.drawable.trigonometry,
            title = "Algebra",
            description = "Мастер по алебре"
        ),
        TaskItem(
            leadIcon = R.drawable.trigonometry,
            title = "Algebra",
            description = "Мастер по алебре"
        )
    )
){
    Scaffold(
        modifier = Modifier.fillMaxSize()
            .statusBarsPadding()
            .background(colorResource(R.color.backGroundColorMain)),
        topBar = {
            TopBarMathLab (
                textTitle = titleText
            ){  }
        }
    ) { paddingValues ->
        BottomTasksScreen(
            paddingValues = paddingValues,
            items = tasksList
            )
    }
}


@Composable
fun BottomTasksScreen(
    paddingValues: PaddingValues,
    items: List<TaskItem>
){

    LazyColumn(
        modifier = Modifier.fillMaxSize()
            .padding(paddingValues = paddingValues)
            .background(colorResource(R.color.backGroundColorMain))
            .padding(top = 20.dp)
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)

    ){
        items(items){item ->
            TaskItemView(
                item
            )
        }
    }
}