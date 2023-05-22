package com.example.esp8266

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.esp8266.ui.theme.ESP8266Theme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ESP8266Theme {
                val database =
                    Firebase.database("https://esp8266-3eb00-default-rtdb.asia-southeast1.firebasedatabase.app/")
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (number in 1..3) {
                        val myRef = database.getReference("bulb $number")
                        LightBulb(
                            number = number,
                            databaseValue = myRef
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LightBulb(
    number: Int,
    databaseValue: DatabaseReference
) {
    var isOn by remember { mutableStateOf(false)}

    databaseValue.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            isOn = dataSnapshot.getValue(Boolean::class.java)!!
        }

        override fun onCancelled(databaseError: DatabaseError) {
            println("The read failed: " + databaseError.code)
        }
    })

    val bulbOff = R.drawable.bulb_off
    val bulbOn = R.drawable.bulb_on
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Đèn $number",
            fontSize = MaterialTheme.typography.h5.fontSize
        )

        IconButton(onClick = {
            isOn = !isOn
            databaseValue.setValue(isOn)
        }) {
            Image(
                modifier = Modifier.size(96.dp),
                imageVector = ImageVector.vectorResource(id = if (isOn) bulbOn else bulbOff),
                contentDescription = "Bulb",
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ESP8266Theme {
        val database =
            Firebase.database("https://esp8266-ae4bc-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val myRef = database.getReference("bulb 1")
        LightBulb(1, myRef)
    }
}