package com.zzu.extrace.domain.express

import org.junit.Test
import org.junit.Assert.*

class ExpressTest {
    @Test
    fun get_all_exp() {
        val getAllExpressUseCase = GetAllExpressUseCase()
        val response = getAllExpressUseCase()
        println(response)
    }
}
