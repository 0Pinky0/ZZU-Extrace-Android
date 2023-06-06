package com.zzu.extrace.ui.screen.login

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.zzu.extrace.domain.user.LoginUseCase
import com.zzu.extrace.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val loginUseCase = LoginUseCase()

    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(12.dp)
        ) {
            Text("卓越快递", fontWeight = FontWeight.Bold, fontSize = 42.sp)
            Spacer(modifier = Modifier.height(32.dp))
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("手机号") },
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("密码") },
            )
            Spacer(modifier = Modifier.height(28.dp))
            Row {
                Button(
                    onClick = {
                        if (username.isEmpty()) {
                            Toast.makeText(context, "用户名为空！", Toast.LENGTH_SHORT).show()
                        } else if (password.isEmpty()) {
                            Toast.makeText(context, "密码为空！", Toast.LENGTH_SHORT).show()
                        } else {
                            val loginResult = loginUseCase(username, password)
                            if (loginResult.success) {
                                val sharedPreference =  context.getSharedPreferences("User", Context.MODE_PRIVATE)
                                with (sharedPreference.edit()) {
                                    putInt("id", loginResult.content.id)
                                    putString("username", loginResult.content.username)
                                    putString("phone", loginResult.content.telephone)
                                    apply()
                                }
                                Toast.makeText(context, "欢迎用户${loginResult.content.username}", Toast.LENGTH_SHORT).show()
                                navController.navigate("business")
                            } else {
                                Toast.makeText(context, "登陆失败！", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.weight(0.05f)
                ) {
                    Text(text = "登录")
                }
                Spacer(modifier = Modifier.weight(0.02f))
                Button(
                    onClick = {
                        navController.navigate("register")
                    },
                    modifier = Modifier.weight(0.05f)
                ) {
                    Text(text = "注册")
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Preview
@Composable
fun LoginPreview() {
    val navController = rememberAnimatedNavController()
    LoginScreen(navController)
}