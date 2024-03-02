package com.dataxow.app

import com.dataxow.route.handleGetImage
import com.dataxow.route.handleModuleCall
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Routing.configureRoutes() {
    staticResources("/", "web")

    post("/module/call") {
        call.handleModuleCall()
    }
    get("/image") {
        call.handleGetImage()
    }
}
