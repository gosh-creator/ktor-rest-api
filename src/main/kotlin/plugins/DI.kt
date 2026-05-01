package com.plugins

import com.repositories.CityRepository
import com.repositories.UserRepository
import com.services.CityService
import com.services.UserService
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

val appModule = module {
    single { CityRepository() }
    single { UserRepository() }
    single { CityService(get()) }
    single { UserService(get()) }
}

fun Application.configureDI() {
    install(Koin) {
        modules(appModule)
    }
}