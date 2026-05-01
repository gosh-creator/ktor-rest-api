package com.services

import com.models.City
import com.models.CreateCityRequest
import com.repositories.CityRepository

class CityService(private val cityRepository: CityRepository = CityRepository()) {

    fun getAll() : List<City> = cityRepository.findAll()

    fun getById(id : Int) : City? = cityRepository.findById(id)

    fun create(city : CreateCityRequest) : City = cityRepository.create(city)

    fun delete (id : Int) : Boolean = cityRepository.delete(id)
}