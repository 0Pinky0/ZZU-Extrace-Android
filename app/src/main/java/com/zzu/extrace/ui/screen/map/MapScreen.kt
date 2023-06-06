package com.zzu.extrace.ui.screen.map

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.baidu.mapapi.model.LatLng
import com.melody.map.baidu_compose.model.BDCameraPosition
import com.melody.map.baidu_compose.position.rememberCameraPositionState
import com.zzu.extrace.data.history.History
import com.zzu.extrace.data.trace.Trace
import com.zzu.extrace.domain.express.GetExpressNodeUseCase
import com.zzu.extrace.domain.history.GetExpressHistoryUseCase
import com.zzu.extrace.domain.history.GetPackageHistoryUseCase
import com.zzu.extrace.domain.trace.GetExpressTraceUseCase
import com.zzu.extrace.domain.trace.GetPackageTraceUseCase
import com.zzu.extrace.ui.screen.LoadingScreen
import java.time.LocalDateTime
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapScreen(navController: NavController, isPackage: Int = 1, id: Int = 1) {

    val isLoaded = remember {
        mutableStateOf(false)
    }

    val histories = remember {
        mutableListOf<History>()
    }
    val pts = remember {
        mutableListOf<LatLng>()
    }
    val trace = remember {
        mutableListOf<Trace>()
    }
    if (!isLoaded.value) {
        LoadingScreen()
        if (isPackage == 1) {
            val getPackageTraceUseCast = GetPackageTraceUseCase()
            val getPackageHistoryUseCase = GetPackageHistoryUseCase()
            trace += getPackageTraceUseCast(id).content
            histories += getPackageHistoryUseCase(id).content
        } else {
            val getExpressTraceUseCast = GetExpressTraceUseCase()
            val getExpressHistoryUseCase = GetExpressHistoryUseCase()
            trace += getExpressTraceUseCast(id).content
            histories += getExpressHistoryUseCase(id).content
            if (trace.isEmpty()) {
                val srdNode = GetExpressNodeUseCase()(id)
                trace += Trace(0, srdNode.content.x, srdNode.content.y, LocalDateTime.now())
            }
        }
        isLoaded.value = true
    }

    val cameraPositionState = rememberCameraPositionState {
        position = BDCameraPosition(
            LatLng(
                trace[trace.size - 1].lat,
                trace[trace.size - 1].lng,
            ), 18F, 0f, 0f
        )
    }
    if (trace.isNotEmpty()) {
        for (point in trace) {
            pts.add(LatLng(point.lat, point.lng))
        }
    }
//    if (!isLoaded.value) {
//        if (isPackage == 1) {
//            val getPackageTraceUseCast = GetPackageTraceUseCase()
//            val getPackageHistoryUseCase = GetPackageHistoryUseCase()
//            trace += getPackageTraceUseCast(id).content
//            histories += getPackageHistoryUseCase(id).content
//        } else {
//            val getExpressTraceUseCast = GetExpressTraceUseCase()
//            val getExpressHistoryUseCase = GetExpressHistoryUseCase()
//            trace += getExpressTraceUseCast(id).content
//            histories += getExpressHistoryUseCase(id).content
//        }
//        if (trace.isNotEmpty()) {
//            for (point in trace) {
//                pts.add(LatLng(point.lat, point.lng))
//            }
//        }
//        cameraPositionState.position = BDCameraPosition(
//            LatLng(
//                trace[trace.size - 1].lat,
//                trace[trace.size - 1].lng,
//            ), 18F, 0f, 0f
//        )
//        isLoaded.value = true
//    } else {
        MapView(
            cameraPositionState = cameraPositionState,
            pts = pts,
            trace = trace,
            history = histories,
            isPackage = (isPackage == 1),
        )
//    }
}