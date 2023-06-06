package com.zzu.extrace.ui.screen.express

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.zzu.extrace.data.express.Express
import com.zzu.extrace.data.history.ExpressHistory
import com.zzu.extrace.data.node.Node
import com.zzu.extrace.domain.business.FormatExpressStateUseCase
import com.zzu.extrace.domain.business.FormatPackageStateUseCase
import com.zzu.extrace.domain.express.GetExpressByIdUseCase
import com.zzu.extrace.domain.express.GetExpressNodeUseCase
import com.zzu.extrace.domain.express.PostExpressNodeUseCase
import com.zzu.extrace.domain.express.PostExpressStateUseCase
import com.zzu.extrace.domain.history.PostExpressHistoryUseCase
import com.zzu.extrace.ui.component.ProfileProperty
import com.zzu.extrace.ui.screen.LoadingScreen
import com.zzu.extrace.ui.screen.node.NodeLayoutView
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShowExpressScreen(navController: NavController, eid: Int) {
    val context = LocalContext.current

    val getExpressByIdUseCase = GetExpressByIdUseCase()
    val getExpressNodeUseCase = GetExpressNodeUseCase()
    val formatExpressStateUseCase = FormatExpressStateUseCase()
    val postExpressHistoryUseCase = PostExpressHistoryUseCase()
    val postExpressNodeUseCase = PostExpressNodeUseCase()
    val postExpressStateUseCase = PostExpressStateUseCase()

    var express by remember { mutableStateOf(Express(1, "", 0, "", "", "", "")) }
    var node by remember { mutableStateOf(Node(1, "暂未揽收", "", .0, .0)) }
    val isLoaded = remember { mutableStateOf(false) }

    val srcNode = remember {
        mutableStateOf(Node(1, "", "", .0, .0))
    }
    var openDialog by remember { mutableStateOf(false) }
    val dialogWidth = 350.dp
    val dialogHeight = 500.dp
    if (openDialog) {
        Dialog(onDismissRequest = { openDialog = false }) {
            // Draw a rectangle shape with rounded corners inside the dialog
            androidx.compose.material.Surface(
                Modifier
                    .size(dialogWidth, dialogHeight)
                    .background(Color.White)
            ) {
                NodeLayoutView(navController = navController, state = srcNode, close = {
                    openDialog = false
                })
            }
        }
    }

    if (!isLoaded.value) {
        LoadingScreen()
        express = getExpressByIdUseCase(eid).content
        val nodeResponse = getExpressNodeUseCase(eid)
        if (nodeResponse.success) {
            node = nodeResponse.content
        }
        isLoaded.value = true
    } else {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(12.dp)
            ) {
                Text("包裹信息", fontWeight = FontWeight.Bold, fontSize = 32.sp)
                Spacer(modifier = Modifier.height(32.dp))
                ProfileProperty(label = "运单号", value = express.id.toString())
                ProfileProperty(label = "内容", value = express.content)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1F)) {
                        ProfileProperty("发送人名称", express.srcName, false, 0.dp)
                    }
                    Spacer(modifier = Modifier.weight(0.5F))
                    Column(modifier = Modifier.weight(1F)) {
                        ProfileProperty("收货人名称", express.dstName, false, 0.dp)
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1F)) {
                        ProfileProperty("发送人电话", express.srcPhone, false, 0.dp)
                    }
                    Spacer(modifier = Modifier.weight(0.5F))
                    Column(modifier = Modifier.weight(1F)) {
                        ProfileProperty("收货人电话", express.dstPhone, false, 0.dp)
                    }
                }
                ProfileProperty(label = "所在网点", value = node.name)
                ProfileProperty(label = "状态", value = formatExpressStateUseCase(express.state))
                Divider()
                Spacer(modifier = Modifier.height(28.dp))
                Button(
                    onClick = {
                        if (node.name != "暂未揽收") {
                            navController.navigate("map/0/$eid")
                        } else {
                            Toast.makeText(context, "暂未揽收！", Toast.LENGTH_SHORT).show()
                        }
                    },
                ) {
                    Text(text = "定位")
                }
                when (express.state) {
                    0 -> { // 待揽收
                        if (srcNode.value.name == "") {
                            Button(
                                onClick = { openDialog = true },
                            ) {
                                Text(text = "选择揽收网点")
                            }
                        } else {
                            Text(text = srcNode.value.name)
                            Button(
                                onClick = {
                                    postExpressHistoryUseCase(
                                        ExpressHistory(
                                            express.id,
                                            1,
                                            LocalDateTime.now(),
                                            srcNode.value.name,
                                            ""
                                        )
                                    )
                                    postExpressNodeUseCase(express.id, srcNode.value.id)
                                    postExpressStateUseCase(express.id, 1)
                                    Toast.makeText(
                                        context,
                                        "快递已被${srcNode.value.name}揽收",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.popBackStack()
                                },
                            ) {
                                Text(text = "揽收")
                            }
                        }
                    }

                    1 -> { // 待分拣
                        Button(
                            onClick = {
                                val sharedPreference =  context.getSharedPreferences("User", Context.MODE_PRIVATE)
                                val userId = sharedPreference.getInt("id", 0)
                                val userName = sharedPreference.getString("username", "")!!
                                val userPhone = sharedPreference.getString("phone", "")!!
                                if (userId == 0) {
                                    Toast.makeText(context, "未登录，无法派送", Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    postExpressHistoryUseCase(
                                        ExpressHistory(
                                            express.id,
                                            5,
                                            LocalDateTime.now(),
                                            userName,
                                            userPhone
                                        )
                                    )
                                    postExpressStateUseCase(express.id, 4)
                                    Toast.makeText(context, "快件已交给${userName}", Toast.LENGTH_SHORT)
                                        .show()
                                    navController.popBackStack()
                                }
                            },
                        ) {
                            Text(text = "开始派送")
                        }
                    }

                    4 -> { // 派送中
                        Button(
                            onClick = {
                                val sharedPreference =  context.getSharedPreferences("User", Context.MODE_PRIVATE)
                                val userId = sharedPreference.getInt("id", 0)
                                val userName = sharedPreference.getString("username", "")!!
                                val userPhone = sharedPreference.getString("phone", "")!!
                                if (userPhone == express.dstPhone) {
                                    postExpressHistoryUseCase(
                                        ExpressHistory(
                                            express.id,
                                            6,
                                            LocalDateTime.now(),
                                            "",
                                            ""
                                        )
                                    )
                                    postExpressStateUseCase(express.id, 5)
                                    Toast.makeText(context, "快件已签收", Toast.LENGTH_SHORT)
                                        .show()
                                    navController.popBackStack()
                                } else {
                                    Toast.makeText(context, "用户并非收件人", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            },
                        ) {
                            Text(text = "确认签收")
                        }
                    }

                }
//                Button(
//                    onClick = { navController.navigate("map/0/$eid") },
//                ) {
//                    Text(text = "更改状态")
//                }
            }
        }
    }
}


val expressCase = Express(
    1, "快递", 0, "王佳乐", "18853187736", "李天浩", "13583167770"
)

@Preview
@Composable
fun ExpressInfoPreview() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            Text("包裹", fontWeight = FontWeight.Bold, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(32.dp))
            ProfileProperty(label = "运单号", value = expressCase.id.toString())
            ProfileProperty(label = "内容", value = expressCase.id.toString())
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1F)) {
                    ProfileProperty("发送人名称", "OK", false, 0.dp)
                }
                Spacer(modifier = Modifier.weight(0.5F))
                Column(modifier = Modifier.weight(1F)) {
                    ProfileProperty("收货人名称", "OK", false, 0.dp)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1F)) {
                    ProfileProperty("发送人电话", "OK", false, 0.dp)
                }
                Spacer(modifier = Modifier.weight(0.5F))
                Column(modifier = Modifier.weight(1F)) {
                    ProfileProperty("收货人电话", "OK", false, 0.dp)
                }
            }
            ProfileProperty(label = "状态", value = expressCase.id.toString())
            Divider()
            Spacer(modifier = Modifier.height(28.dp))
            Button(
                onClick = { },
            ) {
                Text(text = "定位")
            }
            Button(
                onClick = { },
            ) {
                Text(text = "更改状态")
            }
        }
    }
}