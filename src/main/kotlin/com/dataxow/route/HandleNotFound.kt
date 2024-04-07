package com.dataxow.route

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend fun ApplicationCall.handleNotFound() {
    respond(HttpStatusCode.NotFound, "Not Found")
}
