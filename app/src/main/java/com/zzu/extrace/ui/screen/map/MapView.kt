package com.zzu.extrace.ui.screen.map

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.baidu.mapapi.map.BitmapDescriptorFactory
import com.baidu.mapapi.model.LatLng
import com.melody.map.baidu_compose.BDMap
import com.melody.map.baidu_compose.overlay.Marker
import com.melody.map.baidu_compose.overlay.Polyline
import com.melody.map.baidu_compose.overlay.rememberMarkerState
import com.melody.map.baidu_compose.poperties.MapUiSettings
import com.melody.map.baidu_compose.position.CameraPositionState
import com.zzu.extrace.R
import com.zzu.extrace.data.history.History
import com.zzu.extrace.data.trace.Trace
import com.zzu.extrace.domain.business.FormatExpressHistoryUseCase
import com.zzu.extrace.domain.business.FormatPackageHistoryUseCase
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapView(
    cameraPositionState: CameraPositionState,
    pts: MutableList<LatLng>,
    trace: MutableList<Trace>,
    history: MutableList<History>,
    isPackage: Boolean,
) {
    Column {
        BDMap(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = 0.5f),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                isScrollGesturesEnabled = true,
                isZoomEnabled = true,
                isZoomGesturesEnabled = true,
            )
        ) {
            if (pts.size > 1) {
                Polyline(
                    // 不要抽稀，否则有棱角
                    isThined = false,
                    points = pts,
                    polylineColor = Color(0xFFF38D0F)
                )
                Marker(
                    anchor = Offset(0.5f, 0.5f),
                    zIndex = 3,
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_start_guide_icon),
                    state = rememberMarkerState(
                        position = LatLng(
                            trace[0].lat,
                            trace[0].lng,
                        )
                    )
                )
            }
            Marker(
                anchor = Offset(0.5f, 0.5f),
                zIndex = 3,
                icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_end_guide_icon),
                state = rememberMarkerState(
                    position = LatLng(
                        trace[trace.size - 1].lat,
                        trace[trace.size - 1].lng,
                    )
                )
            )
        }
        LazyColumn {
            history.forEach {history ->
                item {
                    Row {
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        Text(formatter.format(history.time))
                        val content: String = if (isPackage) {
                            val formatPackageHistoryUseCase = FormatPackageHistoryUseCase()
                            formatPackageHistoryUseCase(history.type, history.a, history.b)
                        } else {
                            val formatExpressHistoryUseCase = FormatExpressHistoryUseCase()
                            formatExpressHistoryUseCase(history.type, history.a, history.b)
                        }
                        Text(content)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun HistoryCard(date: String, content: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(2F)) {
            Text(date, fontSize = 15.sp)
        }
        Spacer(modifier = Modifier.weight(1F))
        Column(modifier = Modifier.weight(3F)) {
            Text(content)
        }
    }
}

@Preview
@Composable
fun HistoryCardPreview() {
    HistoryCard("2023-11-2 11:39:11", "您的外卖已送至楼下")
}