package com.zzu.extrace.ui.screen.home

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Surface
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.zzu.extrace.activity.camera.CameraActivity
import com.zzu.extrace.activity.transfer.TransferActivity
import com.zzu.extrace.data.node.Node
import com.zzu.extrace.ui.component.ProfileProperty
import com.zzu.extrace.ui.screen.node.NodeLayoutView

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    var qrCode by remember { mutableStateOf("") }
    val sharedPreference =  context.getSharedPreferences("User", Context.MODE_PRIVATE)
    val user_id = sharedPreference.getInt("id", 0)
    val user_name = sharedPreference.getString("username", "")!!
    val user_phone = sharedPreference.getString("phone", "")!!
    var userId by remember {
        mutableStateOf(user_id)
    }
    var userName by remember {
        mutableStateOf(user_name)
    }
    var userPhone by remember {
        mutableStateOf(user_phone)
    }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == ComponentActivity.RESULT_OK) {
                qrCode = result.data?.getStringExtra("data").toString()
                val type = qrCode[0]
                val id = qrCode.substring(1).toInt()
                when (type) {
                    'E' -> {
                        navController.navigate("showExpress/${id}")
                    }
                    'P' -> {
                        navController.navigate("showPackage/${id}")
                    }
                    else -> {
                        Toast.makeText(context, "无法识别", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
        ) {

            if (userId == 0) {
                Button(onClick = {
                    navController.navigate("login")
                }) {
                    Text(text = "登录")
                }
            } else {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ProfileProperty(label = "用户名", value = userName)
                    ProfileProperty(label = "手机", value = userPhone)
                    Button(onClick = {
                        userId = 0
                        userName = ""
                        userPhone = ""
                        with (sharedPreference.edit()) {
                            remove("id")
                            remove("username")
                            remove("phone")
                            apply()
                        }
                        Toast.makeText(context, "成功登出！", Toast.LENGTH_SHORT).show()
                    }) {
                        Text(text = "登出")
                    }
                }
            }
            Button(onClick = { navController.navigate("newExpress") }) {
                Text("新建运单")
            }
            Button(onClick = { navController.navigate("newPackage") }) {
                Text("新建包裹")
            }
            Button(onClick = {
                launcher.launch(Intent(context, CameraActivity::class.java))
            }) {
                Text(text = "扫码查件")
            }
        }
    }
}