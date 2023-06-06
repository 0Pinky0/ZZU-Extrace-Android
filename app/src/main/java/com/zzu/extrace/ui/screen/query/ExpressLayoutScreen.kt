package com.zzu.extrace.ui.screen.query

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zzu.extrace.data.express.Express
import com.zzu.extrace.domain.express.GetAllExpressUseCase
import com.zzu.extrace.domain.express.SearchExpressUseCase
import com.zzu.extrace.ui.component.SearchBar
import com.zzu.extrace.ui.component.expresses
import com.zzu.extrace.ui.screen.LoadingScreen
import com.zzu.extrace.ui.theme.Pink80

@Composable
fun ExpressLayoutScreen(navController: NavController) {
    val searchExpressUseCase = SearchExpressUseCase()
    val express: MutableList<Express> = remember {
        mutableListOf()
    }

    val isLoaded = remember { mutableStateOf(false) }
    var searchText by remember {
        mutableStateOf("")
    }
    Column {
        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            searchText = searchText,
            onClick = {
                isLoaded.value = false
                express.clear()
                express += searchExpressUseCase(searchText).content
                isLoaded.value = true
            },
            onValueChange = {
                searchText = it
            })
        if (!isLoaded.value) {
            LoadingScreen()
            express += searchExpressUseCase(searchText).content
//            express += getAllExpressUseCase().content
            isLoaded.value = true
        } else {
            LazyColumn(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                for (ex in express) {
                    item {
                        ExpressCard(navController, ex)
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun ExpressCard(navController: NavController, express: Express) {
    // 一块板子，以及横竖间隔
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 0.dp)
    ) {
        // 板子里的内容
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            // 占满宽度，长度留空24dp
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(express.content, fontSize = 19.sp, modifier = Modifier.padding(bottom = 16.dp))
            Row {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .weight(1F)
                ) {
                    Text(express.srcName, fontSize = 17.sp)
                    Text(express.srcPhone, fontSize = 15.sp)
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .weight(1F)
                ) {
                    Text(express.dstName, fontSize = 17.sp)
                    Text(express.dstPhone, fontSize = 15.sp)
                }
            }
            Button(onClick = { navController.navigate("showExpress/${express.id}") }) {
                Text(text = "查看")
            }
        }
    }
}

@Preview
@Composable
fun ExpressLayoutPreview() {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 0.dp)
    ) {
        // 板子里的内容
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            // 占满宽度，长度留空24dp
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(expresses[0].content, fontSize = 19.sp, modifier = Modifier.padding(bottom = 16.dp))
            Row {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .weight(1F)
                ) {
                    Text(expresses[0].srcName, fontSize = 17.sp)
                    Text(expresses[0].srcPhone, fontSize = 15.sp)
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .weight(1F)
                ) {
                    Text(expresses[0].dstName, fontSize = 17.sp)
                    Text(expresses[0].dstPhone, fontSize = 15.sp)
                }
            }
        }
    }
}