package com.zzu.extrace.ui.component

import com.zzu.extrace.data.express.Express
import com.zzu.extrace.data.node.Node
import com.zzu.extrace.data.packages.Packages


val packages = Packages(
    1,
    0,
    1,
    2
)

val nodes = listOf(
    Node(1, "郑州大学松园菜鸟驿站", "河南省郑州市高新大道100号郑州大学松园", 1.0, 2.0),
    Node(1, "郑州大学柳园菜鸟驿站", "河南省郑州市高新大道100号郑州大学松园", 1.0, 2.0),
)

val expresses = listOf(
    Express(
        1,
        "咏浩",
        0,
        "王晨",
        "15615516660",
        "李天浩",
        "13583167770"
    ),
    Express(
        2,
        "送天浩",
        0,
        "王佳乐",
        "18853187736",
        "李天浩",
        "13583167770"
    ),
)