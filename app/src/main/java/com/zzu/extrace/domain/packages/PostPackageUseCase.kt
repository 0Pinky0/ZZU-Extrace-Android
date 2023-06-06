package com.zzu.extrace.domain.packages

import android.os.Build
import androidx.annotation.RequiresApi
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.zzu.extrace.domain.GetUrlUseCase
import com.zzu.extrace.data.Response
import com.zzu.extrace.data.express.Express
import com.zzu.extrace.data.history.PackageHistory
import com.zzu.extrace.data.packages.Packages
import com.zzu.extrace.data.packages.PackageBody
import com.zzu.extrace.data.pkg_ctn.PackageContent
import com.zzu.extrace.domain.express.PostExpressStateByPackageUseCase
import com.zzu.extrace.domain.history.PostPackageHistoryUseCase
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.jackson.jackson
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime

class PostPackageUseCase {
    operator fun invoke(
        startId: Int,
        endId: Int,
        expresses: List<Int>
    ): Response<Packages> = runBlocking {
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
        val responsePackage: Response<Packages> = client.post(GetUrlUseCase("/package/add")) {
            contentType(ContentType.Application.Json)
            setBody(
                PackageBody(
                    0,
                    startId,
                    endId
                )
            )
        }.body()
        if (responsePackage.success) {
            val pid = responsePackage.content.id
            val pkg_ctn = mutableListOf<PackageContent>()
            for (eid in expresses) {
                pkg_ctn.add(PackageContent(pid, eid))
            }
            val responsePkgctn: Response<Boolean> = client.post(GetUrlUseCase("/package/pkg_ctn")) {
                contentType(ContentType.Application.Json)
                setBody(pkg_ctn)
            }.body()
            PostExpressStateByPackageUseCase()(responsePackage.content.id, 2)
            client.close()
            return@runBlocking Response(responsePkgctn.success, responsePackage.content)
        } else {
            client.close()
            return@runBlocking Response(false, Packages(0, 0, 0, 0))
        }
    }
}