package com.repositories

import com.models.City
import com.models.CityTable
import com.models.CreateCityRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class CityRepository {

    suspend fun findAll() : List<City> = withContext(Dispatchers.IO) {
        transaction {
            CityTable.selectAll().map { it.toCity() }
        }
    }

    suspend fun findById(id : Int) : City? = withContext(Dispatchers.IO) {
        transaction {
            CityTable.selectAll()
                .where { CityTable.id eq id }
                .map { it.toCity() }
                .singleOrNull()
        }
    }

    suspend fun create(city : CreateCityRequest) : City = withContext(Dispatchers.IO) {
        transaction {
            val id = CityTable.insertAndGetId {
                it[name] = city.name
                it[country] = city.country
                it[population] = city.population
            }

            City(id = id.value, name = city.name, country = city.country, population = city.population)
        }
    }

    suspend fun delete(id: Int): Boolean = withContext(Dispatchers.IO) {
        transaction {
            CityTable.deleteWhere { CityTable.id eq id } > 0
        }
    }

    private fun ResultRow.toCity() : City = City(
        id = this[CityTable.id].value,
        name = this[CityTable.name],
        country = this[CityTable.country],
        population = this[CityTable.population]
    )
}