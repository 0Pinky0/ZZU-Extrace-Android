package com.zzu.extrace.domain.business

class FormatPackageHistoryUseCase {
    operator fun invoke(state: Int, a: String = "", b: String = "") = when (state) {
        1 -> "已收取包裹"
        2 -> "包裹在${a}完成封包，准备发往${b}"
        3 -> "包裹已发车"
        4 -> "包裹到达${a}"
        5 -> "包裹正在派送途中"
        6 -> "您的包裹已签收"
        7 -> "包裹已拆包"
        else -> {""}
    }
}