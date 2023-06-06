package com.zzu.extrace.activity.transfer

import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.BitmapDescriptorFactory
import com.baidu.mapapi.model.LatLng
import com.melody.map.baidu_compose.BDMap
import com.melody.map.baidu_compose.model.BDCameraPosition
import com.melody.map.baidu_compose.overlay.Marker
import com.melody.map.baidu_compose.overlay.Polyline
import com.melody.map.baidu_compose.overlay.rememberMarkerState
import com.melody.map.baidu_compose.poperties.MapUiSettings
import com.melody.map.baidu_compose.position.rememberCameraPositionState
import com.zzu.extrace.R
import com.zzu.extrace.data.history.PackageHistory
import com.zzu.extrace.data.trace.Trace
import com.zzu.extrace.domain.express.PostExpressNodeByPackageUseCase
import com.zzu.extrace.domain.express.PostExpressStateByPackageUseCase
import com.zzu.extrace.domain.history.PostExpressHistoryUseCase
import com.zzu.extrace.domain.history.PostPackageHistoryUseCase
import com.zzu.extrace.domain.packages.PostPackageStateUseCase
import com.zzu.extrace.domain.trace.AddTraceUseCase
import com.zzu.extrace.domain.trace.PostTraceUseCase
import java.time.LocalDateTime
import java.util.Timer
import kotlin.concurrent.fixedRateTimer


class TransferActivity : ComponentActivity() {
    private lateinit var timer: Timer

    class MyLocationListener : BDAbstractLocationListener() {
        var latitude = 0.0
        var longitude = 0.0

        override fun onReceiveLocation(location: BDLocation) {
            latitude = location.latitude //获取纬度信息
            longitude = location.longitude //获取经度信息
            val radius = location.radius //获取定位精度，默认值为0.0f
            val coorType = location.coorType
            val errorCode = location.locType

            Log.e("Pos", "$latitude, $longitude")
            Log.e("Pos", "$errorCode")
        }

        fun getLat() = latitude
        fun getLon() = longitude
    }

    lateinit var mLocationClient: LocationClient
    private val myListener = MyLocationListener()
    private var packageId: Int = 0
    private var srcName: String = ""
    private var dstName: String = ""
    private var dstId: Int = 0
    private var counter: Int = 0

    private val addTraceUseCase = AddTraceUseCase()
    private val postPackageHistoryUseCase = PostPackageHistoryUseCase()
    private val postPackageStateUseCase = PostPackageStateUseCase()
    private val postExpressStateByPackageUseCase = PostExpressStateByPackageUseCase()
    private val tracing: MutableList<LatLng> = mutableListOf()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        packageId = intent.getIntExtra("pid", 1)
        dstId = intent.getIntExtra("did", 1)
        srcName = intent.getStringExtra("src")!!
        dstName = intent.getStringExtra("dst")!!

        LocationClient.setAgreePrivacy(true)
        mLocationClient = LocationClient(applicationContext)
        mLocationClient.registerLocationListener(myListener);

        val option = LocationClientOption()

        option.locationMode = LocationClientOption.LocationMode.Hight_Accuracy
        option.setCoorType("bd09ll")
        option.setFirstLocType(LocationClientOption.FirstLocType.SPEED_IN_FIRST_LOC)
        option.setScanSpan(1000)
        option.isOpenGnss = true
        option.isLocationNotify = true
        option.setIgnoreKillProcess(false)
        option.SetIgnoreCacheException(false)
        option.setWifiCacheTimeOut(5 * 60 * 1000)
        option.setEnableSimulateGnss(false)
        option.setNeedNewVersionRgc(true)
        mLocationClient.locOption = option

        mLocationClient.start()


        setContent {
            val context = LocalContext.current
            var pos by remember { mutableStateOf("") }
            var lon by remember { mutableStateOf(0.0) }
            var lat by remember { mutableStateOf(0.0) }
            lon = myListener.getLon()
            lat = myListener.getLat()
            val cameraPositionState = rememberCameraPositionState {
                position = BDCameraPosition(
                    LatLng(lat, lon), 18F, 0f, 0f
                )
            }
            val markerState = rememberMarkerState(
                position = LatLng(lat, lon)
            )

            Surface(modifier = Modifier.fillMaxSize()) {
                Column {
                    BDMap(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(fraction = 0.6f),
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(
                            isScrollGesturesEnabled = true,
                            isZoomEnabled = true,
                            isZoomGesturesEnabled = true,
                        )
                    ) {
                        if (tracing.isNotEmpty()) {
                            Polyline(
                                // 不要抽稀，否则有棱角
                                isThined = false,
                                points = tracing,
                                polylineColor = Color(0xFFF38D0F)
                            )
                        }
                        Marker(
                            anchor = Offset(0.5f, 0.5f),
                            zIndex = 3,
                            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_start_guide_icon),
                            state = markerState
                        )
                    }


//                    Column {
//                        Text(text = pos)
//                        Button(onClick = {
//                            mLocationClient.requestLocation()
//                            lon = myListener.getLon()
//                            lat = myListener.getLat()
//                            cameraPositionState.position = BDCameraPosition(
//                                LatLng(lat, lon), 18F, 0f, 0f
//                            )
//                            markerState.position = LatLng(lat, lon)
//                            pos = "$lon, $lat"
//                            Toast.makeText(context, pos, Toast.LENGTH_SHORT).show()
//                        }) {
//                            Text(text = "fresh")
//                        }
                    Button(onClick = {
                        timer = fixedRateTimer("", false, 0, 2000) {
//                                mLocationClient.requestLocation()
                            lat = myListener.getLat()
                            lon = myListener.getLon()
                            cameraPositionState.position = BDCameraPosition(
                                LatLng(lat, lon), 18F, 0f, 0f
                            )
                            markerState.position = LatLng(lat, lon)
                            counter++
                            if (counter % 15 == 0) {
                                addTraceUseCase(
                                    Trace(
                                        packageId, lat, lon, LocalDateTime.now()
                                    )
                                )
                                tracing += LatLng(lat, lon)
                            }
                        }
                    }) {
                        Text(text = "开始")
                    }
                    Button(onClick = {
                        timer.cancel()
                        postPackageHistoryUseCase(
                            PackageHistory(
                                packageId,
                                4,
                                LocalDateTime.now(),
                                dstName,
                                "",
                            )
                        )
                        postPackageStateUseCase(
                            packageId, 2
                        )
                        postExpressStateByPackageUseCase(
                            packageId, 1
                        )
                        PostExpressNodeByPackageUseCase()(packageId, dstId)
                        Toast.makeText(context, "包裹已运送至${dstName}", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }) {
                        Text(text = "到站")
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mLocationClient.stop()
    }

    private var locationListener: LocationListener = object : LocationListener {
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
        override fun onLocationChanged(location: Location) {}
    }
}
