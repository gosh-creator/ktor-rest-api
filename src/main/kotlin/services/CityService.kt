package com.services

import com.models.City
import com.models.CreateCityRequest
import com.repositories.CityRepository

class CityService(private val cityRepository: CityRepository) {

    suspend fun getAll() : List<City> = cityRepository.findAll()

    suspend fun getById(id : Int) : City? = cityRepository.findById(id)

    suspend fun create(city : CreateCityRequest) : City {
        require(city.name.isNotBlank()) { "Name cannot be blank" }
        require(city.country.isNotBlank()) { "Country cannot be blank" }
        require(city.population > 0) { "Population cannot be zero" }
        return cityRepository.create(city)
    }

    suspend fun delete (id : Int) : Boolean = cityRepository.delete(id)
}