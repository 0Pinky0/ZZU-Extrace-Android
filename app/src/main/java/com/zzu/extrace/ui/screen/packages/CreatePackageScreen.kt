package com.zzu.extrace.ui.screen.packages

import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
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
import com.zzu.extrace.activity.camera.CameraActivity
import com.zzu.extrace.data.express.Express
import com.zzu.extrace.data.history.PackageHistory
import com.zzu.extrace.data.node.Node
import com.zzu.extrace.domain.express.GetExpressByIdUseCase
import com.zzu.extrace.domain.express.PostExpressStateByPackageUseCase
import com.zzu.extrace.domain.history.PostPackageHistoryUseCase
import com.zzu.extrace.domain.packages.PostPackageStateUseCase
import com.zzu.extrace.domain.packages.PostPackageUseCase
import com.zzu.extrace.ui.component.ProfileProperty
import com.zzu.extrace.ui.component.expresses
import com.zzu.extrace.ui.screen.node.NodeLayoutView
import com.zzu.extrace.ui.screen.query.ExpressCard
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreatePackageScreen(navController: NavController) {
    val getExpressByIdUseCase = GetExpressByIdUseCase()
    val postPackageUseCase = PostPackageUseCase()
    val postPackageHistoryUseCase = PostPackageHistoryUseCase()

    val context = LocalContext.current
    val expresses = remember {
        mutableListOf<Express>()
    }
    val expressIds = remember {
        mutableListOf<Int>()
    }
    val srcNode = remember {
        mutableStateOf(Node(1, "", "", .0, .0))
    }
    val dstNode = remember {
        mutableStateOf(Node(1, "", "", .0, .0))
    }
    var qrCode by remember { mutableStateOf("") }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == ComponentActivity.RESULT_OK) {
                qrCode = result.data?.getStringExtra("data").toString()
                val type = qrCode[0]
                val id = qrCode.substring(1).toInt()
                expresses.add(getExpressByIdUseCase(id).content)
                expressIds.add(id)
                Toast.makeText(context, qrCode, Toast.LENGTH_SHORT).show()
            }
        }
    var isSrc by remember { mutableStateOf(false) }
    var openDialog by remember { mutableStateOf(false) }
    val dialogWidth = 350.dp
    val dialogHeight = 500.dp
    if (openDialog) {
        Dialog(onDismissRequest = { openDialog = false }) {
            // Draw a rectangle shape with rounded corners inside the dialog
            Surface(
                Modifier
                    .size(dialogWidth, dialogHeight)
                    .background(Color.White)
            ) {
                NodeLayoutView(navController = navController, state = if (isSrc) {
                    srcNode
                } else {
                    dstNode
                }, close = {
                    openDialog = false
                })
            }
        }
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("创建包裹", fontWeight = FontWeight.Bold, fontSize = 42.sp)
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1F)) {
                    ProfileProperty("起始网点", srcNode.value.name, false, 0.dp)
                }
                Spacer(modifier = Modifier.weight(0.5F))
                Column(modifier = Modifier.weight(1F)) {
                    ProfileProperty("目标网点", dstNode.value.name, false, 0.dp)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1F)) {
                    Button(onClick = {
                        openDialog = true
                        isSrc = true
                    }) {
                        Text(text = "选择")
                    }
                }
                Spacer(modifier = Modifier.weight(0.5F))
                Column(modifier = Modifier.weight(1F)) {
                    Button(onClick = {
                        openDialog = true
                        isSrc = false
                    }) {
                        Text(text = "选择")
                    }
                }
            }
            Column(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .height(320.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Divider()
                Text(
                    text = "包含运单",
                    modifier = Modifier.align(Alignment.Start),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = {
                    launcher.launch(Intent(context, CameraActivity::class.java))
                }) {
                    Text(text = "扫码添加")
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    expresses.forEach { express ->
                        item {
                            Surface(
                                color = MaterialTheme.colorScheme.background,
                                modifier = Modifier.padding(vertical = 4.dp, horizontal = 0.dp)
                            ) {
                                // 板子里的内容
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    // 占满宽度，长度留空24dp
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        express.content,
                                        fontSize = 19.sp,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                    Row {
                                        Column(
                                            horizontalAlignment = Alignment.Start,
                                            modifier = Modifier.fillMaxWidth(0.4f)
                                        ) {
                                            Text(express.srcName, fontSize = 17.sp)
                                            Text(express.srcPhone, fontSize = 15.sp)
                                        }
                                        Column(
                                            horizontalAlignment = Alignment.End,
                                            modifier = Modifier.fillMaxWidth(0.5f)
                                        ) {
                                            Text(express.dstName, fontSize = 17.sp)
                                            Text(express.dstPhone, fontSize = 15.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Divider()
            Button(onClick = {
                val created = postPackageUseCase(
                    srcNode.value.id, dstNode.value.id, expressIds
                )
                Log.e("a", created.content.toString())
                if (created.success) {
                    postPackageHistoryUseCase(
                        PackageHistory(
                            created.content.id,
                            2,
                            LocalDateTime.now(),
                            srcNode.value.name,
                            dstNode.value.name,
                        )
                    )
                    Toast.makeText(context, "创建成功！", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "创建失败！", Toast.LENGTH_SHORT).show()
                }
                navController.popBackStack()
            }) {
                Text(text = "创建")
            }
        }
    }
}

@Preview
@Composable
fun CreatePackagePreview() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("创建包裹", fontWeight = FontWeight.Bold, fontSize = 42.sp)
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1F)) {
                    ProfileProperty("起始网点", "OK", false, 0.dp)
                }
                Spacer(modifier = Modifier.weight(0.5F))
                Column(modifier = Modifier.weight(1F)) {
                    ProfileProperty("目标网点", "OK", true, 0.dp)
                }
            }
            Column(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .height(320.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Divider()
                Text(
                    text = "包含运单",
                    modifier = Modifier.align(Alignment.Start),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = {

                }) {
                    Text(text = "新增运单")
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    expresses.forEach { express ->
                        item {
                            Card(
                                modifier = Modifier.width(32.dp)
                            ) {
                                Text(text = express.content)
                            }
                        }
                    }
                }
            }
            Divider()
        }
    }
}
