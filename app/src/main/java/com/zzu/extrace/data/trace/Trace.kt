package com.zzu.extrace.data.trace

import java.time.LocalDateTime

data class Trace(
    val packageId: Int,
    val lat: Double,
    val lng: Double,
    val time: LocalDateTime,
)