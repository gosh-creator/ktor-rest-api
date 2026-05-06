package com

import com.models.UserResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.*

class UserRoutesTest {

    private fun ApplicationTestBuilder.configureClient() = createClient {
        install(ContentNegotiation) { json() }
    }

    private suspend fun HttpClient.loginAndGetToken(): String {
        val response = post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Test","email":"test@test.com","hashPassword":"secret"}""")
        }
        return response.body<Map<String, String>>()["token"]!!
    }

    private suspend fun HttpClient.createUser(
        name: String = "Test User",
        email: String = "user_${System.nanoTime()}@test.com",
        password: String = "secret",
        token: String
    ): UserResponse {
        val response = post("/users") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"$name","email":"$email","hashPassword":"$password"}""")
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        return response.body<UserResponse>()
    }

    // --- GET /users ---

    @Test
    fun `GET users returns 401 without token`() = testApplication {
        application { module() }
        assertEquals(HttpStatusCode.Unauthorized, client.get("/users").status)
    }

    @Test
    fun `GET users returns 200 with token`() = testApplication {
        application { module() }
        val client = configureClient()
        val token = client.loginAndGetToken()

        val response = client.get("/users") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `GET users returns list containing created user`() = testApplication {
        application { module() }
        val client = configureClient()
        val token = client.loginAndGetToken()
        val created = client.createUser(token = token)

        val users = client.get("/users") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body<List<UserResponse>>()

        assertTrue(users.any { it.id == created.id && it.email == created.email })
    }

    // --- GET /users/{id} ---

    @Test
    fun `GET users by id returns 401 without token`() = testApplication {
        application { module() }
        assertEquals(HttpStatusCode.Unauthorized, client.get("/users/1").status)
    }

    @Test
    fun `GET users by id returns 200 for existing user`() = testApplication {
        application { module() }
        val client = configureClient()
        val token = client.loginAndGetToken()
        val created = client.createUser(token = token)

        val response = client.get("/users/${created.id}") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val user = response.body<UserResponse>()
        assertEquals(created.id, user.id)
        assertEquals(created.name, user.name)
        assertEquals(created.email, user.email)
    }

    @Test
    fun `GET users by id returns 404 for non-existent user`() = testApplication {
        application { module() }
        val client = configureClient()
        val token = client.loginAndGetToken()

        val response = client.get("/users/999999") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `GET users by non-numeric id returns 400`() = testApplication {
        application { module() }
        val client = configureClient()
        val token = client.loginAndGetToken()

        val response = client.get("/users/abc") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    // --- POST /users ---

    @Test
    fun `POST users returns 401 without token`() = testApplication {
        application { module() }
        val client = configureClient()

        val response = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Igor","email":"new@test.com","hashPassword":"secret"}""")
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `POST users returns 201 with valid data`() = testApplication {
        application { module() }
        val client = configureClient()
        val token = client.loginAndGetToken()
        val email = "new_${System.nanoTime()}@test.com"

        val response = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Igor","email":"$email","hashPassword":"secret"}""")
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.Created, response.status)
        val user = response.body<UserResponse>()
        assertEquals("Igor", user.name)
        assertEquals(email, user.email)
    }

    @Test
    fun `POST users returns 400 for blank name`() = testApplication {
        application { module() }
        val client = configureClient()
        val token = client.loginAndGetToken()

        val response = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"","email":"valid@test.com","hashPassword":"secret"}""")
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `POST users returns 400 for blank email`() = testApplication {
        application { module() }
        val client = configureClient()
        val token = client.loginAndGetToken()

        val response = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Igor","email":"","hashPassword":"secret"}""")
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `POST users returns 400 for blank password`() = testApplication {
        application { module() }
        val client = configureClient()
        val token = client.loginAndGetToken()

        val response = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Igor","email":"valid@test.com","hashPassword":""}""")
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    // --- DELETE /users/{id} ---

    @Test
    fun `DELETE users returns 401 without token`() = testApplication {
        application { module() }
        assertEquals(HttpStatusCode.Unauthorized, client.delete("/users/1").status)
    }

    @Test
    fun `DELETE users returns 204 for existing user`() = testApplication {
        application { module() }
        val client = configureClient()
        val token = client.loginAndGetToken()
        val created = client.createUser(token = token)

        val response = client.delete("/users/${created.id}") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.NoContent, response.status)
    }

    @Test
    fun `DELETE users returns 404 for non-existent user`() = testApplication {
        application { module() }
        val client = configureClient()
        val token = client.loginAndGetToken()

        val response = client.delete("/users/999999") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `DELETE users returns 404 on second delete`() = testApplication {
        application { module() }
        val client = configureClient()
        val token = client.loginAndGetToken()
        val created = client.createUser(token = token)

        client.delete("/users/${created.id}") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        val secondDelete = client.delete("/users/${created.id}") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.NotFound, secondDelete.status)
    }
}
