package com.zzu.extrace.ui.screen.node

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zzu.extrace.data.node.Node
import com.zzu.extrace.domain.node.SearchNodeUseCase
import com.zzu.extrace.ui.component.SearchBar
import com.zzu.extrace.ui.screen.LoadingScreen
import com.zzu.extrace.ui.theme.Pink80

@Composable
fun NodeLayoutView(navController: NavController, state: MutableState<Node>, close: () -> Unit) {
    val searchNodeUseCase = SearchNodeUseCase()
    val nodes: MutableList<Node> = remember { mutableListOf() }

    val isLoaded = remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    Column {
        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            searchText = searchText,
            onClick = {
                isLoaded.value = false
                nodes.clear()
                nodes += searchNodeUseCase(searchText).content
                isLoaded.value = true
            },
            onValueChange = {
                searchText = it
            })
        if (!isLoaded.value) {
            LoadingScreen()
            nodes += searchNodeUseCase(searchText).content
            isLoaded.value = true
        } else {
            LazyColumn(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                for (node in nodes) {
                    item {
                        NodeCard(navController, node, state, close)
                    }
                }
            }
        }
    }
}

@Composable
fun NodeCard(navController: NavController, node: Node, state: MutableState<Node>, close: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 0.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(node.name, fontSize = 19.sp, modifier = Modifier.padding(bottom = 16.dp))
            Text(node.address, fontSize = 15.sp)
            Button(onClick = {
                state.value = node
                close()
            }) {
                Text(text = "选择")
            }
        }
    }
}