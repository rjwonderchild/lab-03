# LLM Reference Document

## Student Details

- **Full Name:** `Riley Whitford`
- **CCID:** `whitfor1`

## Prompt | Date | Agent

Prompt | Septermber 17, 2026 | ChatGPT:  

Currently creating a small app in Android Studio using Kotlin. I have a .kt file seen here: **INSERTED CODE**  
Is there an efficient way to change the state of the Add City button, and not redraw, or create a new button object?  
  
Since you already have:

var selectCity by remember { mutableStateOf<City?>(null) }

you can make the button text depend on whether selectCity is null.

Replace:

Text("Add City")

with:

Text(
    if (selectCity == null) "Add City"
    else "Update City"
)

That is enough for just the label change.

So when no row has been selected:

selectCity == null
→ Add City

When a row is clicked and you do:

selectCity = city

Compose sees that state change and recomposes the UI:

selectCity != null
→ Update City

This is a good example of declarative UI in Compose: you do not manually tell the button to change its text. You change the state, and the UI reflects that state automatically.  

  
  
Prompt | September 17, 2026 | ChatGPT:  
  
That was simple enough. I am trying to get when a user taps the '+' button, or they tap anywhere  
besides one of the cityrows, it clears the text fields, and updates the add city / update city state. It seems to be working for the most part following documentation: https://developer.android.com/reference/kotlin/androidx/compose/ui/Modifier, but the text states are not being cleared, and sometimes the errormessage does not clear. Any suggestions or fixes?  
  
1. Make the outer area clickable
hange your outer Column from:

Column(modifier = modifier.fillMaxSize()) {

to:

Column(
    modifier = modifier
        .fillMaxSize()
        .clickable {
            selectCity = null
            newCityName = ""
            newProvinceName = ""
            errorMessage = null
        }
) {

Your individual CityRow is already clickable, so clicking a city should be handled by the row. Clicking unused space in the surrounding Column will cancel the selection.

When selectCity becomes null, your existing button logic automatically changes:

Text(
    if (selectCity == null) "Add City"
    else "Update City"
)

back to "Add City".

2. Modify the + button

hange it so that when the fields are being hidden, their state is reset:

onClick = {
    if (showAddCityFields) {
        newCityName = ""
        newProvinceName = ""
        selectCity = null
        errorMessage = null
    }

    showAddCityFields = !showAddCityFields
}

The ordering matters a little.

If:

showAddCityFields == true

then the user is currently looking at the fields and pressing + means they're about to hide them. That's when you clear everything.

If:

showAddCityFields == false

then pressing + simply opens empty fields.

3. Your row click should probably also show the fields

Your row callback should now have something along these lines:

onClick = {
    selectCity = city
    newCityName = city.name
    newProvinceName = city.province
    showAddCityFields = true
}

That way if the fields are currently hidden and the user taps:

Vancouver    BC

the app automatically opens:

[ Vancouver ] [ BC ] [ Update City ]

rather than selecting Vancouver invisibly.

Resulting state behavior

You'll now have a fairly clean state system:

Action	selectCity	Fields	Button
Start app	null	hidden	—
Press +	null	empty	Add City
Click Edmonton	Edmonton	Edmonton / AB	Update City
Tap blank space	null	empty	Add City
Click Vancouver	Vancouver	Vancouver / BC	Update City
Press +	null	cleared + hidden	—
Press + again	null	empty	Add City

One thing to test after adding the outer clickable: make sure tapping inside the text fields doesn't accidentally trigger the outer click and clear them. Compose's child input normally handles its own interaction, but if you see that behavior, switch the outer handling to a more targeted blank-area gesture rather than putting clickable on the entire Column.
  
Prompt | September 17, 2026 | ChatGPT:  
I'm having difficulty getting the layout of my delete button correct: **INSERT DELETE BUTTON CODE**  
I am using the modifier like the above, but its squished into the text fields and the update/add city button. Do I need to make a completely seperate row for this to work?  
  
m trying to use a modifier to move my delete button lower, but its squished into the text fields and update/add city row (fields?). How can I make this centered right below the add/update city button?

Yes. The issue is that your text fields and Add/Update button are all inside the same Row, so if you put the Delete button there too, Compose tries to lay everything out horizontally.

If you want the Delete button centered directly underneath, the easiest structure is:

Column {
    Row {
        // city text field
        // province text field
        // add/update button
    }

    if (selectCity != null) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    // delete logic
                }
            ) {
                Text("Delete City")
            }
        }
    }
}

So your layout becomes:

[ City field ] [ Province field ] [ Update City ]

                 [ Delete City ]

You can also add vertical spacing above the Delete button:

Spacer(modifier = Modifier.height(8.dp))

which would go between the first Row and the delete-button Row.

You’ll need:

import androidx.compose.foundation.layout.height

and you already have Arrangement imported.

The key idea is:

Row = puts items side by side
Column = stacks items vertically

So the delete button should not be inside the same Row as the fields if you want it underneath them.
  
Prompt | September 17, 2026 | ChatGPT:  
Chat, I have some type bug mismatch when implementing my updateCity function. I have it set for type boolean, but it shows '???' in the error log  
window. See the current iteration here: **INSERTED CODE**  

  
Yes. There are two type-related issues in the files you uploaded.

The main one is in CityListScreen.

You currently declare:

onUpdateCity: (City) -> Boolean,

which means:

“This callback accepts one City and returns a Boolean.”

But later you call it with two City objects:

val updated = onUpdateCity(
    selectedCity!!,
    updatedCity
)

So the callback signature needs to accept two City parameters:

onUpdateCity: (City, City) -> Boolean,

That matches what you're already doing in MainActivity, where your lambda has oldCity and updatedCity.

So change this:

onUpdateCity: (City) -> Boolean,

to:

onUpdateCity: (City, City) -> Boolean,

Your call here will then make sense:

val updated = onUpdateCity(
    selectedCity!!,
    updatedCity
)

because the two parameters represent:

selectedCity!!  → old City
updatedCity     → replacement City

There is also a second error in your Preview.

Your CityListScreenPreview() currently provides:

onAddCity = { true },
onDeleteCity = {}

but does not provide the newly required onUpdateCity callback.

Add a dummy two-argument callback for the preview:

onUpdateCity = { _, _ -> true }

So the bottom should look like:

CityListScreen(
    cities = listOf(
        City("Edmonton", "AB"),
        City("Vancouver", "BC"),
        City("Calgary", "AB")
    ),
    onAddCity = { true },
    onDeleteCity = {},
    onUpdateCity = { _, _ -> true }
)

The _ means:

“This parameter exists, but I don't need to use it.”

One final thing to verify in CityRepository: your updateCity() must also return Boolean. If it's still the older version:

fun updateCity(oldCity: City, updatedCity: City) {

then MainActivity will still have a mismatch because CityListScreen expects the update callback to return a Boolean.

It should have the general shape:

fun updateCity(oldCity: City, updatedCity: City): Boolean {
    // ...
}

So the types should line up all the way through:

CityListScreen:
(City, City) -> Boolean
        ↓
MainActivity:
{ oldCity, updatedCity ->
    cityRepository.updateCity(oldCity, updatedCity)
}
        ↓
CityRepository:
fun updateCity(oldCity: City, updatedCity: City): Boolean

That should resolve the type mismatch chain.
