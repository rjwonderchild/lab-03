package com.example.listycity3

import androidx.compose.runtime.mutableStateListOf

class CityRepository {
    private val _cities = mutableStateListOf(
        City("Edmonton", "AB"),
        City("Vancouver", "BC"),
        City("Toronto", "ON")
    )
    val cities: List<City>
        get() = _cities

    fun addCity(city: City): Boolean {
        val cityNameTrimmed = city.name.trim()
        val provinceNameTrimmed = city.province.trim()

        val cityInList = _cities.any { existingCity ->
            existingCity.name.equals(cityNameTrimmed, ignoreCase = true) &&
                    existingCity.province.equals(provinceNameTrimmed, ignoreCase = true)
        }

        if (cityNameTrimmed.isBlank() || provinceNameTrimmed.isBlank() || cityInList ) {
            return false
        }

        _cities.add(
            City(
                name = cityNameTrimmed,
                province = provinceNameTrimmed
            )
        )

        return true
    }

    fun updateCity(oldCity: City, updatedCity: City) {
        val index = _cities.indexOf(oldCity)
        if (index != -1) {
            _cities[index] = updatedCity
        }
    }
}

