package com.zzu.extrace.data.packages

import com.zzu.extrace.data.express.Express

data class PackageWithExpress(
    val packages: Packages,
    val expresses: List<Express>,
)