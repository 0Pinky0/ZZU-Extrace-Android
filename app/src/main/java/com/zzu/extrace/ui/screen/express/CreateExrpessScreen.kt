package com.zzu.extrace.ui.screen.express

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zzu.extrace.data.express.ExpressBody
import com.zzu.extrace.domain.express.PostExpressUseCase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExpressScreen(navController: NavController) {
    val postExpressUseCase = PostExpressUseCase()

    val context = LocalContext.current
    var content by remember { mutableStateOf("") }
    var srcName by remember { mutableStateOf("") }
    var srcPhone by remember { mutableStateOf("") }
    var dstName by remember { mutableStateOf("") }
    var dstPhone by remember { mutableStateOf("") }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(12.dp)
    ) {
        Text("新建运单", fontWeight = FontWeight.Bold, fontSize = 42.sp)
        Spacer(modifier = Modifier.height(32.dp))
        TextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("快件内容") },
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = srcName,
            onValueChange = { srcName = it },
            label = { Text("发件人姓名") },
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = srcPhone,
            onValueChange = { srcPhone = it },
            label = { Text("发件人手机") },
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = dstName,
            onValueChange = { dstName = it },
            label = { Text("收件人姓名") },
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = dstPhone,
            onValueChange = { dstPhone = it },
            label = { Text("收件人手机") },
        )
        Spacer(modifier = Modifier.height(28.dp))
        Row {
            Button(
                onClick = {
                    if (content.isEmpty()) {
                        Toast.makeText(context, "用户名为空！", Toast.LENGTH_SHORT).show()
                    } else if (srcName.isEmpty()) {
                        Toast.makeText(context, "密码为空！", Toast.LENGTH_SHORT).show()
                    } else if (srcPhone.isEmpty()) {
                        Toast.makeText(context, "手机号为空！", Toast.LENGTH_SHORT).show()
                    } else if (dstName.isEmpty()) {
                        Toast.makeText(context, "姓名为空！", Toast.LENGTH_SHORT).show()
                    } else if (dstPhone.isEmpty()) {
                        Toast.makeText(context, "姓名为空！", Toast.LENGTH_SHORT).show()
                    } else {
                        val postResult = postExpressUseCase(
                            ExpressBody(
                                content,
                                0,
                                srcName,
                                srcPhone,
                                dstName,
                                dstPhone,
                            )
                        )
                        if (postResult) {
                            Toast.makeText(
                                context,
                                "成功创建",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(context, "创建失败", Toast.LENGTH_SHORT).show()
                        }
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.weight(0.05f)
            ) {
                Text(text = "提交")
            }
        }
    }
}