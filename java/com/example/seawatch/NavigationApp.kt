package com.example.seawatch

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.seawatch.data.*
import com.example.seawatch.screens.NotifyScreen
import androidx.hilt.navigation.compose.hiltViewModel

sealed class NavigationScreen(val name: String) {
    object Home : NavigationScreen("Home")
    object SignUp: NavigationScreen("SignUp")
    object LogIn : NavigationScreen("LogIn")
    object Settings : NavigationScreen("Impostazioni")
    object DisplaySettings : NavigationScreen("Impostazioni Schermo")
    object SecuritySettings:NavigationScreen("Impostazioni Sicurezza")
    object ProfileSettings:NavigationScreen("Impostazioni Profilo")
    object Profile:NavigationScreen("Profilo")
    object AddSighting:NavigationScreen("Aggiungi")
    object Stats: NavigationScreen("Statistiche")
    object ViewSighting:NavigationScreen("Avvistamento")
    object Notify:NavigationScreen("Notifiche")
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationApp(
    navController: NavHostController = rememberNavController(),
    radioOptions: List<String>,
    theme: String,
    settingsViewModel: SettingsViewModel,
    sharedPrefForLogin: SharedPreferences,
    avvistamentiViewModel: AvvistamentiViewModel,
    favouriteViewModel: FavouriteViewModel,
    listItems: List<Favourite>,
    userViewModel: UserViewModel,
    avvistamentiViewViewModel: AvvistamentiViewViewModel,
    descriptionViewModel:DescriptionViewModel,
    warningViewModel: WarningViewModel,
    startLocationUpdates: () -> Unit
) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    val lastUser=sharedPrefForLogin.getString("USER", "")
    val profileViewModel: ProfileViewModel = viewModel()
    var currentScreen:String
    if(lastUser==""){
        currentScreen = backStackEntry?.destination?.route ?: NavigationScreen.LogIn.name
    } else{
        currentScreen = backStackEntry?.destination?.route ?: NavigationScreen.Home.name
    }
    // Get the name of the current screen

    val configuration = LocalConfiguration.current
    var barHeight = 0
    if (configuration.orientation==ORIENTATION_LANDSCAPE){
        barHeight = 47
    } else {
        barHeight = 55
    }
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        topBar = {
            CustomTopBar(currentScreen=currentScreen, navController=navController, barHeight=barHeight, sharedPrefForLogin=sharedPrefForLogin)
        },
        bottomBar = {
            CustomBottomBar(currentScreen, configuration, barHeight, navController, profileViewModel)
        },
        floatingActionButton = {
            CustomFAB(currentScreen, navController)
        },
        floatingActionButtonPosition = if(currentScreen == NavigationScreen.Home.name && configuration.orientation==ORIENTATION_PORTRAIT) FabPosition.Center else FabPosition.End,

    ) { innerPadding ->
        NavigationGraph(navController,
            innerPadding,
            radioOptions = radioOptions,
            theme = theme,
            settingsViewModel =  settingsViewModel,
            sharedPrefForLogin=sharedPrefForLogin,
            avvistamentiViewModel=avvistamentiViewModel,
            barH = barHeight,
            favouriteViewModel=favouriteViewModel,
            listItems=listItems,
            profileViewModel=profileViewModel,
            userViewModel=userViewModel,
            avvistamentiViewViewModel=avvistamentiViewViewModel,
            descriptionViewModel=descriptionViewModel,
            startLocationUpdates=startLocationUpdates
        )

        val context = LocalContext.current
        if (warningViewModel.showPermissionSnackBar.value) {
            PermissionSnackBarComposable(snackbarHostState, context, warningViewModel)
        }
        if (warningViewModel.showGPSAlertDialog.value) {
            GPSAlertDialogComposable(context, warningViewModel)
        }
        if (warningViewModel.showConnectivitySnackBar.value) {
            ConnectivitySnackBarComposable(
                snackbarHostState,
                context,
                warningViewModel
            )
        }
    }
}

@Composable
private fun NavigationGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    radioOptions: List<String>,
    theme: String,
    settingsViewModel: SettingsViewModel,
    sharedPrefForLogin:SharedPreferences,
    avvistamentiViewModel: AvvistamentiViewModel,
    barH : Int,
    favouriteViewModel: FavouriteViewModel,
    listItems: List<Favourite>,
    profileViewModel: ProfileViewModel,
    userViewModel: UserViewModel,
    avvistamentiViewViewModel: AvvistamentiViewViewModel,
    descriptionViewModel: DescriptionViewModel,
    startLocationUpdates: () -> Unit
) {
    val placesViewModel = hiltViewModel<PlacesViewModel>()
    NavHost(
        navController = navController,
        startDestination = NavigationScreen.LogIn.name,
        modifier = modifier.padding(innerPadding),
    ) {
        composable(route = NavigationScreen.LogIn.name) {
            LoginScreen(
                goToHome = { navigateToHome(navController)},
                goToSignUp = { navController.navigate(NavigationScreen.SignUp.name) },
                sharedPrefForLogin=sharedPrefForLogin,
                userViewModel = userViewModel
            )
        }
        composable(route = NavigationScreen.Settings.name) {
            Settings(
                goToSecuritySettings = { navController.navigate(NavigationScreen.SecuritySettings.name) },
                goToProfileSettings = { navController.navigate(NavigationScreen.ProfileSettings.name) },
                goToDisplaySettings = { navController.navigate(NavigationScreen.DisplaySettings.name) })
        }
        composable(route = NavigationScreen.DisplaySettings.name) {
            DisplaySettings(radioOptions = radioOptions, theme = theme, settingsViewModel =  settingsViewModel)
        }
        composable(route = NavigationScreen.ProfileSettings.name){
            ProfileSettings(
                userViewModel = userViewModel
            )
        }
        composable(route = NavigationScreen.SecuritySettings.name) {
            SecuritySettings()
        }
        composable(route = NavigationScreen.Profile.name) {
            Profile(
                profileViewModel = profileViewModel,
                userViewModel = userViewModel,
                goToModifyProfile =  { navController.navigate(NavigationScreen.ProfileSettings.name) }
            )
        }
        composable(route = NavigationScreen.SignUp.name) {
            SignUpScreen(
                goToLogin = { navController.navigate(NavigationScreen.LogIn.name) }
            )
        }
        composable(route = NavigationScreen.Home.name){
            HomeScreen(
                goToSighting = { navController.navigate(NavigationScreen.ViewSighting.name)},
                barHeight = barH,
                favouriteViewModel = favouriteViewModel,
                listItems=listItems,
                goToProfile = {navController.navigate(NavigationScreen.Profile.name)},
                profileViewModel = profileViewModel,
                avvistamentiViewViewModel= avvistamentiViewViewModel,
                avvistamentiViewModel = avvistamentiViewModel,
                userViewModel = userViewModel
            )
        }
        composable(route = NavigationScreen.AddSighting.name){
            SightingScreen(
                avvistamentiViewModel=avvistamentiViewModel,
                goToHome = { navController.navigate(NavigationScreen.Home.name) },
                avvistamentiViewViewModel = avvistamentiViewViewModel,
                descriptionViewModel=descriptionViewModel,
                placesViewModel = placesViewModel,
                startLocationUpdates = startLocationUpdates
            )
        }
        composable(route = NavigationScreen.Stats.name){
            StatsScreen(avvistamentiViewViewModel = avvistamentiViewViewModel)
        }
        composable(route = NavigationScreen.ViewSighting.name){
            SightingViewScreen(avvistamentiViewModel = avvistamentiViewModel, avvistamentiViewViewModel = avvistamentiViewViewModel, descriptionViewModel=descriptionViewModel)
        }
        composable(route = NavigationScreen.Notify.name){
            NotifyScreen(goToSighting =  {navController.navigate(NavigationScreen.ViewSighting.name)}, avvistamentiViewViewModel = avvistamentiViewViewModel)
        }
    }
}

class ProfileViewModel:ViewModel(){
    private var _mail = ""
    val mail
        get() = _mail

    fun set(mail:String){
        _mail = mail
    }
}

private fun navigateToHome(navController: NavHostController) {
    navController.popBackStack(NavigationScreen.LogIn.name, inclusive = true)
    navController.navigate(NavigationScreen.Home.name)
}