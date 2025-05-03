package com.example.summitdiaryserver.routes

import com.example.summitdiaryserver.models.UserTable
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

@Serializable
data class UserRequest(val uuid: String)

fun Route.userRoutes() {
    route("/api/users") {
        post {
            try {
                val rawBody = call.receiveText()
                println("Raw body: $rawBody")

                val user = Json.decodeFromString<UserRequest>(rawBody)
                println("Received user request: $user")

                if (user.uuid.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "UUID cannot be blank"))
                    return@post
                }

                val userId = transaction {
                    val existingUser = UserTable.select { UserTable.name eq user.uuid }.singleOrNull()
                    if (existingUser == null) {
                        UserTable.insertAndGetId { it[name] = user.uuid }.value
                    } else {
                        existingUser[UserTable.id].value
                    }
                }

                println("Responding with user_id: $userId")
                call.respond(HttpStatusCode.OK, mapOf("user_id" to userId))
            } catch (e: Exception) {
                println("Error in userRoutes: ${e.localizedMessage}")
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.localizedMessage))
            }
        }
    }
}

