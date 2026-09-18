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
