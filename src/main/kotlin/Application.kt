package com

import com.plugins.configureDI
import io.ktor.server.application.*
import com.plugins.configureSerialization
import com.plugins.configureDatabase
import com.plugins.configureErrors
import com.plugins.configureRouting

fun main(args : Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDI()
    configureSerialization()
    configureErrors()
    configureDatabase()
    configureRouting()
}