package com.example.seawatch

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.recreate
import androidx.navigation.NavHostController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(currentScreen:String, navController: NavHostController, barHeight:Int, sharedPrefForLogin:SharedPreferences){
    var sureExit by rememberSaveable { mutableStateOf(false) }
    if(sureExit){
        AlertDialog(
            onDismissRequest = { sureExit = false },
            title = { Text("AVVISO") },
            text = { Text("Sei sicuro di voler uscire dall'applicazione e tornare nella schermata di login?") },
            confirmButton = {
                Button(
                    onClick = {sureExit = false;
                        with(sharedPrefForLogin.edit()){
                            putString("USER", "")
                            apply()
                        }
                        navController.navigate(NavigationScreen.LogIn.name)
                              },
                ) {
                    Text("Sì")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        sureExit = false
                    }
                ) {
                    Text("No")
                }
            }
        )

    }
    if(currentScreen != NavigationScreen.LogIn.name && currentScreen != NavigationScreen.SignUp.name && currentScreen != NavigationScreen.Home.name) {
        TopAppBar(
            title = {
                Row(
                    modifier = Modifier.fillMaxHeight().padding(0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = currentScreen)
                }
            },
            navigationIcon = {
                if(currentScreen != NavigationScreen.Home.name){
                    Row(
                        modifier = Modifier.fillMaxHeight().padding(0.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Naviga indietro")
                        }
                    }
                }
            },
            modifier = Modifier.height(barHeight.dp),
            actions = {
                if (currentScreen == NavigationScreen.Settings.name || currentScreen == NavigationScreen.DisplaySettings.name || currentScreen == NavigationScreen.SecuritySettings.name || currentScreen == NavigationScreen.ProfileSettings.name) {
                    Row(
                        modifier = Modifier.fillMaxHeight().padding(0.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            sureExit=true}
                        ){
                            Icon(
                                Icons.Filled.ExitToApp,
                                contentDescription = "Esci",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        )
    }
}


