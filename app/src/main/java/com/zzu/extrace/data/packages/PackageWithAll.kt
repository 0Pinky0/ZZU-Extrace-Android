package com.zzu.extrace.data.packages

import com.zzu.extrace.data.express.Express
import com.zzu.extrace.data.node.Node

data class PackageWithAll(
    val packages: Packages,
    val expresses: List<Express>,
    val src: Node,
    val dst: Node
)