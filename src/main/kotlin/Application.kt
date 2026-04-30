package com

import io.ktor.server.application.*
import com.plugins.configureSerialization
import com.plugins.configureDatabase
import com.plugins.configureRouting

fun main(args : Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureDatabase()
    configureRouting()
}