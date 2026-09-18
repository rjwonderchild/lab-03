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

    // Added a private function, since I was beginning to reused duplication
    // check, which makes refactoring, maintainability, and readability
    // a pain.


    // Switch from City to Boolean return logic, this is for checking for
    // objects already stored in the list and prevent user duplication
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

    // Adding Lab 2 deletion function from city repo
    fun removeCity(city: City) {
        _cities.remove(city)
    }

    // Function from Lab 3 hint suggestion
    fun updateCity(oldCity: City, updatedCity: City) {
        val index = _cities.indexOf(oldCity)
        if (index != -1) {
            _cities[index] = updatedCity
        }
    }
}

