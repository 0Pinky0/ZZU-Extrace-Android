package com.zzu.extrace.ui.screen.query

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zzu.extrace.data.packages.Packages
import com.zzu.extrace.domain.packages.GetAllPackageUseCase
import com.zzu.extrace.domain.packages.SearchPackageUseCase
import com.zzu.extrace.ui.component.SearchBar
import com.zzu.extrace.ui.screen.LoadingScreen
import com.zzu.extrace.ui.theme.Pink80

@Composable
fun PackageSurface(navController: NavController) {
    val searchPackageUseCase = SearchPackageUseCase()
    val packages: MutableList<Packages> = remember {
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
                packages.clear()
                packages += searchPackageUseCase(searchText).content
                isLoaded.value = true
            },
            onValueChange = {
                searchText = it
            })
        if (!isLoaded.value) {
            LoadingScreen()
            packages += searchPackageUseCase(searchText).content
            isLoaded.value = true
        } else {
            LazyColumn(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                for (pkg in packages) {
                    item {
                        PackageCard(navController, pkg)
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun PackageCard(navController: NavController, pkg: Packages) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 0.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                "包裹  单号：" + pkg.id,
                fontSize = 19.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Button(onClick = { navController.navigate("showPackage/${pkg.id}") }) {
                Text(text = "查看")
            }
        }
    }
}