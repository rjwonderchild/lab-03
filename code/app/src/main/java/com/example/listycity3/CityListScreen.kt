package com.example.listycity3

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.listycity3.ui.theme.ListyCity3Theme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.FloatingActionButton
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.height

@Composable
fun CityListScreen(
    cities: List<City>,
    // onAddCity: (City) -> Unit means CityListScreen receives a function, then that
    // function takes a City object and performs an action
    // For lab implementation, want to check for duplicates, so added onAddCity
    // as boolean type
    onAddCity: (City) -> Boolean,
    onDeleteCity: (City) -> Unit,
    onUpdateCity: (City, City) -> Boolean,
    modifier: Modifier = Modifier
) {
    // newCityName stores the city name typed by the user.
    // newProvinceName stores the province name typed by the user.
    // remember keeps these values available while CityListScreen is on screen.
    // mutableStateOf("") tells Compose to update the UI when the value changes.
    var newCityName by remember { mutableStateOf("") }
    var newProvinceName by remember { mutableStateOf("") }
    var showAddCityFields by remember { mutableStateOf(false) }

    // var for error message that city & province object exists already
    var errorMessage by remember {mutableStateOf<String?>(null) }

    // added new var to remember selected city to be updated
    var selectedCity by remember {mutableStateOf<City?>(null) }

    Column(modifier = modifier
        .fillMaxSize()
        .clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            selectedCity = null
            newCityName = ""
            newProvinceName = ""
            errorMessage = null
            }
        ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            FloatingActionButton(
                modifier = Modifier.padding(16.dp),
                onClick = {
                    if (showAddCityFields) {
                        newCityName = ""
                        newProvinceName = ""
                        errorMessage = null
                        selectedCity = null
                    }
                    showAddCityFields = !showAddCityFields
                }
            ) {
                Text("+")
            }
        }

        if (showAddCityFields) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = newCityName,
                    onValueChange = { newCityName = it },
                    label = { Text("City") },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = newProvinceName,
                    onValueChange = { newProvinceName = it },
                    label = { Text("Province") },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    modifier = Modifier.padding(vertical = 12.dp),
                    onClick = {
                        if (newCityName.isNotBlank() && newProvinceName.isNotBlank()) {

                            if (selectedCity == null) {
                                val adding = onAddCity(
                                    City(
                                        name = newCityName,
                                        province = newProvinceName
                                    )
                                )

                                if (adding) {

                                    newCityName = ""
                                    newProvinceName = ""
                                    showAddCityFields = false
                                    errorMessage = null
                                } else {
                                    errorMessage = "This city & province in list!"
                                }
                            } else {

                                val updatedCity = City(
                                    name = newCityName,
                                    province = newProvinceName
                                )

                                val updated = onUpdateCity(
                                    selectedCity!!,
                                    updatedCity
                                )

                                if (updated) {
                                    selectedCity = null
                                    newCityName = ""
                                    newProvinceName = ""
                                    showAddCityFields = false
                                    errorMessage = null
                                } else {
                                    errorMessage = "This city & province in list!"
                                }
                            }
                        }
                    }
                ) {
                    Text(
                        if (selectedCity == null) "Add City"
                        else "Update City"
                    )
                }
            }

            // Secondary row for the delete button and function
            // Allows user to delete an entry from the list
            if (selectedCity != null) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                ) {

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            selectedCity?.let { city ->
                                onDeleteCity(city)
                                selectedCity = null
                                newCityName = ""
                                newProvinceName = ""
                                errorMessage = null
                                showAddCityFields = false
                            }
                        }
                    ) {
                        Text("Delete City")
                    }
                }
            }
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        LazyColumn(modifier = modifier.fillMaxSize()) {
            itemsIndexed(cities) { index, city ->
                CityRow(city = city,
                    onClick = {
                        selectedCity = city
                        newCityName = city.name
                        newProvinceName = city.province
                    }
                )

                if (index < cities.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun CityRow(city: City,
            onClick: () -> Unit)
{
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = city.name,
            fontSize = 30.sp,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = city.province,
            fontSize = 30.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CityListScreenPreview() {
    ListyCity3Theme {
        CityListScreen(
            cities = listOf(
                City("Edmonton", "AB"),
                City("Vancouver", "BC"),
                City("Calgary", "AB")
            ),
            onAddCity = { true },
            onDeleteCity = {},
            onUpdateCity = {_, _ -> true}
        )
    }
}