package com.zzu.extrace.data

data class Response<T> (
    val success: Boolean,
    val content: T,
)