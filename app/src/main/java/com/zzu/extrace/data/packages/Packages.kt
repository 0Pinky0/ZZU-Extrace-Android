package com.zzu.extrace.data.packages

// 包裹（运单）
data class Packages(
    val id: Int,
    val state: Int,
    val startId: Int,
    val endId: Int,
)