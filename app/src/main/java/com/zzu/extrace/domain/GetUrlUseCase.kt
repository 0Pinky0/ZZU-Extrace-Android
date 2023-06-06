package com.zzu.extrace.domain

object GetUrlUseCase {
    private const val url = "http://www.0Pinky0.com"

    operator fun invoke(route: String): String {
        return url + route
    }
}