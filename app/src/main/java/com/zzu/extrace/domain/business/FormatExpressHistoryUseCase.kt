package com.zzu.extrace.domain.business

class FormatExpressHistoryUseCase {
    operator fun invoke(state: Int, a: String = "", b: String = "") = when (state) {
        1 -> "已收取快件"
        2 -> "快件在${a}完成分拣，准备发往${b}"
        3 -> "快件已发车"
        4 -> "快件到达${a}"
        5 -> "快件交给${a}，正在派送途中（联系电话：${b}）"
        6 -> "您的快件已签收"
        else -> {""}
    }
}