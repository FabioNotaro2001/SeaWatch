package com.example.seawatch

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

@Composable
fun CustomBottomBar(currentScreen:String, configuration:Configuration, barHeight:Int, navController:NavHostController, profileViewModel: ProfileViewModel){
    var icon by rememberSaveable { mutableStateOf(R.drawable.baseline_notifications_24) }

    if (isNetworkAvailable(LocalContext.current) && em != "") {
        val client = OkHttpClient()
        val formBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("user", em)
            .addFormDataPart("request", "getNotify")
            .build()
        val request = Request.Builder()
            .url("https://isi-seawatch.csr.unibo.it/Sito/sito/templates/single_sighting/single_api.php")
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val msg = JSONArray(body.toString())
                try {
                    if(JSONArray(msg.toString()).length()>0){
                        icon = R.drawable.baseline_notifications_active_24
                    } else {
                        icon = R.drawable.baseline_notifications_24
                    }
                } catch (e: Exception) {
                }
            }
        })
    }

    if(currentScreen != NavigationScreen.LogIn.name && currentScreen != NavigationScreen.SignUp.name){
        BottomAppBar (
            modifier = androidx.compose.ui.Modifier.height(barHeight.dp)
        ){
            // Elemento statistiche nella bottom bar.
            NavigationBarItem(
                icon = { Icon(painter = painterResource(id = R.drawable.baseline_bar_chart_24), contentDescription = "Statistiche", modifier = Modifier.size(30.dp)) },
                selected = currentScreen == NavigationScreen.Stats.name,
                onClick = {navController.navigate(NavigationScreen.Stats.name) }
            )

            // Elemento impostazioni nella bottom bar.
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Settings, contentDescription = "Impostazioni", modifier = Modifier.size(30.dp)) },
                selected = currentScreen == NavigationScreen.Settings.name,
                onClick = {navController.navigate(NavigationScreen.Settings.name) }
            )

            // Elemento home nella bottom bar.
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Home, contentDescription = "Homepage", modifier = Modifier.size(30.dp)) },
                selected = currentScreen == NavigationScreen.Home.name,
                onClick = {navController.navigate(NavigationScreen.Home.name) }
            )

            // Elemento notifiche nella bottom bar.
            NavigationBarItem(
                icon = { Icon(painter = painterResource(id = icon), contentDescription = "Notifiche", modifier = Modifier.size(30.dp))},
                selected = currentScreen == NavigationScreen.Notify.name,
                onClick = {navController.navigate(NavigationScreen.Notify.name) }
            )

            // Elemento profilo nella bottom bar.
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Person, contentDescription = "Profilo", modifier = Modifier.size(30.dp)) },
                selected = currentScreen == NavigationScreen.Profile.name,
                onClick = {profileViewModel.set(em);navController.navigate(NavigationScreen.Profile.name) }
            )
        }
    }
}