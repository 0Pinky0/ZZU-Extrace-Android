package com.zzu.extrace.domain.business

class FormatExpressStateUseCase {
    operator fun invoke(state: Int): String {
        """
            0	待揽收（刚创建）【可以被揽收】
            1 <-\	待分拣（到网点）【扫包裹，封包or派送】
            2      |	已分拣（创建Package）【包裹可以运输】
            3  ---|	运输中（在路上）【无法修改】
            4 <-/	派送中
            5	已签收
            6	错误
        """.trimIndent()
        return when (state) {
            0 -> {
                "待揽收"
            }
            1 -> {
                "待分拣"
            }
            2 -> {
                "已分拣"
            }
            3 -> {
                "运输中"
            }
            4 -> {
                "派送中"
            }
            5 -> {
                "已签收"
            }
            else -> {
                "错误"
            }
        }
    }
}