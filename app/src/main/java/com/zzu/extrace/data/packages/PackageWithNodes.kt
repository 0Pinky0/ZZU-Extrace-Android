package com.zzu.extrace.data.packages

import com.zzu.extrace.data.express.Express
import com.zzu.extrace.data.node.Node

data class PackageWithNodes(
    val packages: Package,
    val src: Node,
    val dst: Node
)