package com.zzu.extrace.activity.camera

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.common.Barcode
import com.zzu.extrace.ui.theme.ExtraceTheme

class CameraActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExtraceTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Button(onClick = {
                        val intent = Intent()
                        intent.putExtra("data", "data from SecondActivity")
                        setResult(RESULT_OK, intent)
                        finish()
                    }) {
                        Text("Return")
                    }
                    val context = LocalContext.current
                    val cameraPermissionState =
                        rememberPermissionState(permission = Manifest.permission.CAMERA)

                    PermissionRequired(
                        permissionState = cameraPermissionState,
                        permissionNotGrantedContent = {
                            LaunchedEffect(Unit) {
                                cameraPermissionState.launchPermissionRequest()
                            }
                        },
                        permissionNotAvailableContent = {
                            Column {
                                Toast.makeText(context, "Permission denied.", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }) {
                        val lifecycleOwner = LocalLifecycleOwner.current
                        val detectedBarcode = remember { mutableStateListOf<Barcode>() }

                        val screenWidth =
                            remember { mutableStateOf(context.resources.displayMetrics.widthPixels) }
                        val screenHeight =
                            remember { mutableStateOf(context.resources.displayMetrics.heightPixels) }

                        val imageWidth = remember { mutableStateOf(0) }
                        val imageHeight = remember { mutableStateOf(0) }

                        Box(modifier = Modifier.fillMaxSize()) {
                            CameraView(context = context,
                                lifecycleOwner = lifecycleOwner,
                                analyzer = BarcodeScanningAnalyzer { barcodes, width, height ->
                                    detectedBarcode.clear()
                                    detectedBarcode.addAll(barcodes)
                                    imageWidth.value = width
                                    imageHeight.value = height
                                })
                            Column(
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxHeight()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    IconButton(onClick = {
                                        val intent = Intent()
                                        setResult(RESULT_CANCELED, intent)
                                        finish()
                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.ArrowBack,
                                            contentDescription = "back",
                                            tint = Color.White
                                        )
                                    }
                                    Text(
                                        text = "Scanner",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color.White
                                    )
                                }
                            }
                            DrawBarcode(
                                barcodes = detectedBarcode,
                                imageWidth = imageWidth.value,
                                imageHeight = imageHeight.value,
                                screenWidth = screenWidth.value,
                                screenHeight = screenHeight.value
                            )
                        }
                        if (detectedBarcode.size == 1) {
                            val intent = Intent()
                            val scanned =
                                detectedBarcode.joinToString(separator = "\n") { it.displayValue.toString() }
                            intent.putExtra("data", scanned)
                            setResult(RESULT_OK, intent)
                            finish()
                        }
                    }
                }
            }
        }
    }
}

