package com.zzu.extrace.domain.express

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.zzu.extrace.domain.GetUrlUseCase
import com.zzu.extrace.data.Response
import com.zzu.extrace.data.express.Express
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.jackson.jackson
import kotlinx.coroutines.runBlocking

class GetExpressBySrcPhoneUseCase {
    operator fun invoke(
        phone: String
    ): Response<Express> = runBlocking {
        val client = HttpClient {
            install(ContentNegotiation) {
                jackson {
                    configure(SerializationFeature.INDENT_OUTPUT, true)
                    setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
                        indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
                        indentObjectsWith(DefaultIndenter("  ", "\n"))
                    })
                    registerModule(JavaTimeModule())  // support java.time.* types
                }
            }
        }
        val response: Response<Express> = client.get(GetUrlUseCase("/express/get_by_src_phone/${phone}")).body()
        client.close()
        return@runBlocking response
    }
}