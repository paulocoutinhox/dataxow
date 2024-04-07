package com.dataxow.route

import com.dataxow.net.RequestData
import io.ktor.server.application.*
import io.ktor.server.request.*

suspend fun ApplicationCall.handleModuleCall() {
    val requestData = receive<RequestData>()

    when (requestData.func) {
        "modules.player.update" -> handlePlayerUpdate(requestData)
        "modules.system.settings" -> handleSystemSettings()
        "modules.image.list" -> handleImageList(requestData)
        "modules.player.show" -> handlePlayerShow(requestData)
        "modules.player.hide" -> handlePlayerHide(requestData)
        else -> handleNotFound()
    }
}
