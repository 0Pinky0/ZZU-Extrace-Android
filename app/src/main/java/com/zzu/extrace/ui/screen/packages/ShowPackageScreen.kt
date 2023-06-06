package com.zzu.extrace.ui.screen.packages

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zzu.extrace.activity.transfer.TransferActivity
import com.zzu.extrace.data.express.Express
import com.zzu.extrace.data.history.PackageHistory
import com.zzu.extrace.data.node.Node
import com.zzu.extrace.data.packages.Packages
import com.zzu.extrace.domain.business.FormatPackageStateUseCase
import com.zzu.extrace.domain.express.PostExpressStateByPackageUseCase
import com.zzu.extrace.domain.history.PostPackageHistoryUseCase
import com.zzu.extrace.domain.packages.GetPackageWithAllUseCase
import com.zzu.extrace.domain.packages.PostPackageStateUseCase
import com.zzu.extrace.ui.component.ProfileProperty
import com.zzu.extrace.ui.screen.LoadingScreen
import com.zzu.extrace.ui.screen.query.ExpressCard
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShowPackageScreen(navController: NavController, pid: Int) {
    val context = LocalContext.current

    val getPackageWithAllUseCase = GetPackageWithAllUseCase()
    val formatPackageStateUseCase = FormatPackageStateUseCase()

    var packages by remember {
        mutableStateOf(
            Packages(1, 1, 1, 1)
        )
    }
    var expresses by remember {
        mutableStateOf(
            listOf<Express>()
        )
    }
    var src by remember {
        mutableStateOf(
            Node(1, "", "", .0, .0)
        )
    }
    var dst by remember {
        mutableStateOf(
            Node(1, "", "", .0, .0)
        )
    }

    val isLoaded = remember { mutableStateOf(false) }

    if (!isLoaded.value) {
        LoadingScreen()
        val pkgAll = getPackageWithAllUseCase(pid).content
        packages = pkgAll.packages
        expresses = pkgAll.expresses
        src = pkgAll.src
        dst = pkgAll.dst
        isLoaded.value = true
    } else {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(12.dp)
            ) {
                Text("运单信息", fontWeight = FontWeight.Bold, fontSize = 32.sp)
                Spacer(modifier = Modifier.height(32.dp))
                ProfileProperty(label = "运单号", value = packages.id.toString())
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1F)) {
                        ProfileProperty("起始网点", src.name, false, 0.dp)
                    }
                    Spacer(modifier = Modifier.weight(0.5F))
                    Column(modifier = Modifier.weight(1F)) {
                        ProfileProperty("目标网点", dst.name, false, 0.dp)
                    }
                }
                ProfileProperty(label = "状态", value = formatPackageStateUseCase(packages.state))
                Divider()
                Spacer(modifier = Modifier.height(28.dp))
                Button(
                    onClick = { navController.navigate("map/1/$pid") },
                ) {
                    Text(text = "定位")
                }
                when (packages.state) {
                    0 -> {
                        Button(
                            onClick = {
                                PostPackageHistoryUseCase()(
                                    PackageHistory(
                                        packages.id,
                                        3,
                                        LocalDateTime.now(),
                                        "",
                                        "",
                                    )
                                )
                                PostPackageStateUseCase()(packages.id, 1)
                                PostExpressStateByPackageUseCase()(packages.id, 3)
                                val intent = Intent(context, TransferActivity::class.java)
                                intent.putExtra("pid", packages.id)
                                intent.putExtra("did", dst.id)
                                intent.putExtra("src", src.name)
                                intent.putExtra("dst", dst.name)
                                context.startActivity(intent)
                            },
                        ) {
                            Text(text = "开始运送")
                        }
                    }
                }
                Divider()
                LazyColumn {
                    expresses.forEach { express ->
                        item {
                            ExpressCard(navController = navController, express = express)
                        }
                    }
                }
            }
        }
    }
}