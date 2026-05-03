package com

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.*

class CityRoutesTest {

    private fun ApplicationTestBuilder.configureClient() = createClient {
        install(ContentNegotiation) {
            json()
        }
    }

    @Test
    fun `GET cities returns 401 without token`() = testApplication {
        application { module() }
        val response = client.get("/cities")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `POST auth register returns 201`() = testApplication {
        application { module() }
        val client = configureClient()

        val response = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Igor","email":"test@test.com","hashPassword":"secret"}""")
        }

        assertEquals(HttpStatusCode.Created, response.status)
    }

    @Test
    fun `POST city with invalid data returns 400`() = testApplication {
        application { module() }
        val client = configureClient()

        val token = loginAndGetToken(client)

        val response = client.post("/cities") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"","country":"","population":-1}""")
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `POST city with invalid data returns 403`() = testApplication {
        application { module() }
        val client = configureClient()
        val token = loginAndGetToken(client)

        val response = client.post("/cities") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"","country":"","population":-1}""")
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    private suspend fun loginAndGetToken(client: HttpClient): String {
        val response = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Igor","email":"test@test.com","hashPassword":"secret"}""")
        }
        return response.body<Map<String, String>>()["token"]!!
    }
}