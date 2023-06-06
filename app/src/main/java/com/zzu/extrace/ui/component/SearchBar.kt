package com.zzu.extrace.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    searchText: String = "",
    onClick: () -> Unit,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = modifier
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextField(
            value = searchText,
            onValueChange = onValueChange,
            modifier = modifier
                .fillMaxWidth()
                .padding(
                    start = 0.dp,
                    top = 0.dp,
                    end = 16.dp,
                    bottom = 0.dp,
                )
                .weight(3F),
            placeholder = { Text(text = "Search") })
        Button(
            modifier = modifier.weight(1F),
            onClick = onClick
        ) {
            Text(text = "搜索")
        }
    }
}

@Preview
@Composable
fun SearchBarPreview() {
    SearchBar(onClick = { }, onValueChange = { })
}