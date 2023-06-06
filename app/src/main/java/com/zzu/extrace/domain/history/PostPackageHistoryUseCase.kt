package com.zzu.extrace.domain.history

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.zzu.extrace.domain.GetUrlUseCase
import com.zzu.extrace.data.Response
import com.zzu.extrace.data.history.History
import com.zzu.extrace.data.history.PackageHistory
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.jackson.jackson
import kotlinx.coroutines.runBlocking

class PostPackageHistoryUseCase {
    operator fun invoke(
        history: PackageHistory
    ): Boolean = runBlocking {
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
        val response: Response<Boolean> = client.post(GetUrlUseCase("/history/add_pkg")) {
            contentType(ContentType.Application.Json)
            setBody(history)
        }.body()
        client.close()
        return@runBlocking response.success
    }
}