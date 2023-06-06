package com.zzu.extrace.data.history

import java.time.LocalDateTime

// 地区
data class History(
    val id: Int,
    val type: Int,
    val time: LocalDateTime,
    val a: String,
    val b: String,
)