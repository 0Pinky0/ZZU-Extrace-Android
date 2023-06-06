package com.zzu.extrace.domain.business

class FormatPackageStateUseCase {
    operator fun invoke(state: Int): String {
        """
            0	待运输
            1	运输中
            2	已到站
            3	已拆包
        """.trimIndent()
        return when (state) {
            0 -> {
                "待运输"
            }
            1 -> {
                "运输中"
            }
            2 -> {
                "已到站"
            }
            3 -> {
                "已拆包"
            }
            else -> {
                "错误"
            }
        }
    }
}