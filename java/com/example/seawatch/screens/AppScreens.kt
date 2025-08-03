package com.example.seawatch

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat.recreate
import coil.compose.rememberAsyncImagePainter
import com.example.seawatch.data.*
import com.github.tehras.charts.bar.BarChart
import com.github.tehras.charts.bar.BarChartData
import com.github.tehras.charts.bar.renderer.bar.SimpleBarDrawer
import com.github.tehras.charts.bar.renderer.label.SimpleValueDrawer
import com.github.tehras.charts.bar.renderer.xaxis.SimpleXAxisDrawer
import com.github.tehras.charts.bar.renderer.yaxis.SimpleYAxisDrawer
import com.github.tehras.charts.line.LineChart
import com.github.tehras.charts.line.LineChartData
import com.github.tehras.charts.line.renderer.line.SolidLineDrawer
import com.github.tehras.charts.line.renderer.point.FilledCircularPointDrawer
import com.github.tehras.charts.piechart.animation.simpleChartAnimation
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.*



var entratoRete = false
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel,
    userViewModel: UserViewModel,
    goToModifyProfile: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val min = configuration.screenHeightDp.dp/40
    val hig = configuration.screenHeightDp.dp/10
    val backGround = MaterialTheme.colorScheme.primaryContainer
    var nome by rememberSaveable { mutableStateOf("") }
    var cognome by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var profilo by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    var errorMessage by rememberSaveable { mutableStateOf("") }

    if (errorMessage.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { errorMessage = "" },
            title = { Text(text = "ATTENZIONE") },
            text = { Text(text = errorMessage) },
            confirmButton = {
                Button(onClick = { errorMessage = "" }) {
                    Text(text = "Ok")
                }
            }
        )
    }

    if (isNetworkAvailable(context)) {
        val client = OkHttpClient()
        val formBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("user", profileViewModel.mail)
            .addFormDataPart("request", "getUserInfoMob")
            .build()
        val request = Request.Builder()
            .url("https://isi-seawatch.csr.unibo.it/Sito/sito/templates/main_settings/settings_api.php")
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val msg = JSONArray(body.toString())
                profilo = try {
                    "https://isi-seawatch.csr.unibo.it/Sito/img/profilo/" + (msg.get(0) as JSONObject).get(
                        "Img"
                    ).toString()
                } catch (e: Exception) {
                    "R.drawable.sea"
                }
                try {
                    nome = (msg.get(0) as JSONObject).get("Nome").toString()
                    cognome = (msg.get(0) as JSONObject).get("Cognome").toString()
                    email = profileViewModel.mail
                } catch (e: Exception) {
                }
            }
        })
    } else {
        val userItems: List<User> by userViewModel.all.collectAsState(initial = listOf())
        var b = true
        email = em
        for(elem in userItems){
            if(elem.mail == em){
                nome = elem.nome
                cognome = elem.cognome
                b=false
                break
            }
        }
        if (b) {
            if(!entratoRete) {
                errorMessage =
                    "Errore di connessione: impossibile ottenere i dati dell'utente richiesto!"
            }
            entratoRete = true
        }
    }

    Scaffold(
        floatingActionButton = {
            if(email == em) {
                FloatingActionButton(
                    shape = RoundedCornerShape(50.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = { goToModifyProfile() },
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                ) {
                    Icon(imageVector = Icons.Filled.Edit, "Modifica profilo")
                }
            }
        }
    ){it ->
        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {                   /** Profilo orizzontale */
                Row (
                    modifier = modifier
                        .fillMaxSize()
                        .background(backGround)
                        .padding(it)
                ) {
                    LazyColumn(
                        modifier = modifier
                            .fillMaxHeight()
                            .size(
                                width = configuration.screenWidthDp.dp / 3,
                                height = configuration.screenHeightDp.dp
                            )
                            .padding(horizontal = hig),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        items(1) { element ->
                            val scale = if(profilo=="https://isi-seawatch.csr.unibo.it/Sito/img/profilo/profilo.jpg" || !isNetworkAvailable(context)){1.0f}else{1.8f}
                            Image(
                                painter = rememberAsyncImagePainter(model = if(isNetworkAvailable(context)){profilo} else {R.drawable.profilo}),
                                contentDescription = "Immagine del profilo",
                                modifier = Modifier
                                    .size(200.dp)
                                    .clip(CircleShape)
                                    .scale(scale)
                            )
                        }
                    }
                    LazyColumn(
                        modifier = modifier
                            .fillMaxHeight()
                            .background(backGround),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        items(1) { element ->
                            Row(modifier=Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center){
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier=Modifier.width((configuration.screenWidthDp/3).dp)) {
                                    Spacer(modifier = Modifier.height(min))
                                    Text(text="Nome:", style = MaterialTheme.typography.titleLarge)
                                    Spacer(modifier = Modifier.height(min))
                                    Text(text="Cognome:", style = MaterialTheme.typography.titleLarge)
                                    Spacer(modifier = Modifier.height(min))
                                    Text(text="Mail:", style = MaterialTheme.typography.titleLarge)
                                }
                                Column(horizontalAlignment = Alignment.Start, modifier=Modifier.width((configuration.screenWidthDp/3).dp)) {
                                    Spacer(modifier = Modifier.height(min+5.dp))
                                    Text(text=nome, style = MaterialTheme.typography.bodyLarge)
                                    Spacer(modifier = Modifier.height(min+6.dp))
                                    Text(text=cognome, style = MaterialTheme.typography.bodyLarge)
                                    Spacer(modifier = Modifier.height(min+6.dp))
                                    Text(text=email,
                                        modifier = Modifier.clickable(onClick = {
                                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                                data = Uri.parse("mailto:$email")
                                            }
                                            if (intent.resolveActivity(context.packageManager) != null) {
                                                context.startActivity(intent)
                                            }
                                        }),
                                        color=Color.Blue,
                                        textDecoration = TextDecoration.Underline,
                                        style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        }
                    }
                }
            }
            else -> {                                                   /** Profilo verticale */
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .background(backGround),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(1) { element ->
                        Spacer(modifier = Modifier.height(hig))
                        val scale = if(profilo=="https://isi-seawatch.csr.unibo.it/Sito/img/profilo/profilo.jpg" || !isNetworkAvailable(context)){1.0f}else{1.8f}
                        Image(
                            painter = rememberAsyncImagePainter(model = if(isNetworkAvailable(context)){profilo} else {R.drawable.profilo}),
                            contentDescription = "Immagine del profilo",
                            modifier = Modifier
                                .size(200.dp)
                                .clip(CircleShape)
                                .scale(scale)
                        )
                        Row(modifier=Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center){
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier=Modifier.width((configuration.screenWidthDp/2).dp)) {
                                Spacer(modifier = Modifier.height(min))
                                Text(text="Nome:", style = MaterialTheme.typography.titleLarge)
                                Spacer(modifier = Modifier.height(min))
                                Text(text="Cognome:", style = MaterialTheme.typography.titleLarge)
                                Spacer(modifier = Modifier.height(min))
                                Text(text="Mail:", style = MaterialTheme.typography.titleLarge)
                            }
                            Column(horizontalAlignment = Alignment.Start, modifier=Modifier.width((configuration.screenWidthDp/2).dp)) {
                                Spacer(modifier = Modifier.height(min+5.dp))
                                Text(text=nome, style = MaterialTheme.typography.bodyLarge)
                                Spacer(modifier = Modifier.height(min+6.dp))
                                Text(text=cognome, style = MaterialTheme.typography.bodyLarge)
                                Spacer(modifier = Modifier.height(min+6.dp))
                                Text(text=email,
                                    modifier = Modifier.clickable(onClick = {
                                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                                            data = Uri.parse("mailto:$email")
                                        }
                                        if (intent.resolveActivity(context.packageManager) != null) {
                                            context.startActivity(intent)
                                        }
                                    }),
                                    color=Color.Blue,
                                    textDecoration = TextDecoration.Underline,
                                    style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun takeDatasList (avvistamentiViewModel: AvvistamentiViewModel, avvistamentiViewViewModel: AvvistamentiViewViewModel): List<AvvistamentiDaVedere>{
    val temp: List<AvvistamentiDaVedere> by avvistamentiViewViewModel.all.collectAsState(initial = listOf())
    val tempAvvLocali: List<AvvistamentiDaCaricare> by avvistamentiViewModel.all.collectAsState(initial = listOf())
    val l = mutableListOf<AvvistamentiDaVedere>()
    for(e in tempAvvLocali){
        l.add(AvvistamentiDaVedere(e.id, e.avvistatore, e.data, e.numeroEsemplari, try{e.posizione.split(" ")[0]}catch (e: Exception){""}, try{e.posizione.split(" ")[1]}catch (e: Exception){""}, e.animale, e.specie, e.mare, e.vento, e.note, "profilo.jpg", "nome", "cognome", false))
    }
    return l + temp
}

data class MarkerData(
    val latitude: String,
    val longitude: String,
    val data: String,
    val animale: String,
    val specie: String
)

var sightingID: AvvistamentiDaVedere? = null
var fav = true
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    goToSighting: () -> Unit,
    goToProfile: () -> Unit,
    barHeight: Int,
    modifier: Modifier = Modifier,
    favouriteViewModel: FavouriteViewModel,
    listItems:List<Favourite>,
    profileViewModel: ProfileViewModel,
    avvistamentiViewViewModel: AvvistamentiViewViewModel,
    avvistamentiViewModel : AvvistamentiViewModel,
    userViewModel: UserViewModel
) {
    val configuration = LocalConfiguration.current
    val min = configuration.screenHeightDp.dp / 40
    val med = configuration.screenHeightDp.dp / 20
    val backGround = MaterialTheme.colorScheme.primaryContainer
    val context = LocalContext.current
    var showFilterDialog by rememberSaveable { mutableStateOf(false) }
    val options by rememberSaveable { mutableStateOf(getAnimal()) }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedOptionText by rememberSaveable { mutableStateOf("") }
    var filterPref by rememberSaveable { mutableStateOf(false) }
    var isFiltersActive by rememberSaveable { mutableStateOf(false) }
    var filterAnima by rememberSaveable { mutableStateOf("") }
    val listFavourite by rememberSaveable {mutableStateOf(mutableListOf<String>())}
    var mapSet by  rememberSaveable { mutableStateOf(false) }




    if(fav){
        fav=false
        for (el in listItems) {
            if (el.utente == em) {
                listFavourite.add(el.avvistamento)
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(0.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Home")
                    }
                },
                modifier = Modifier.height(barHeight.dp),
                actions = {
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(0.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { showFilterDialog = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_filter_alt_24),
                                contentDescription = "Filtri",
                                tint = if (isFiltersActive) Color.Red else Color.Black
                            )
                        }
                    }
                    if (showFilterDialog) {
                        AlertDialog(
                            onDismissRequest = { showFilterDialog = false },
                            title = { Text("Filtri") },
                            text = {
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .fillMaxWidth()
                                    ) {
                                        Checkbox(
                                            checked =  filterPref,
                                            onCheckedChange = { filterPref=it; if(filterPref || selectedOptionText!="") isFiltersActive=true else isFiltersActive=false },
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Text("Solo preferiti")
                                    }
                                    Text("Tipo di animale: ")
                                    Spacer(modifier = Modifier.height(12.dp))
                                    ExposedDropdownMenuBox(
                                        expanded = expanded,
                                        onExpandedChange = { expanded = !expanded },
                                        modifier = Modifier
                                            .border(
                                                1.dp,
                                                MaterialTheme.colorScheme.outline,
                                                RoundedCornerShape(2.dp)
                                            )
                                    ) {
                                        TextField(
                                            // The `menuAnchor` modifier must be passed to the text field for correctness.
                                            modifier = Modifier.menuAnchor(),
                                            readOnly = true,
                                            value = selectedOptionText,
                                            onValueChange = {},
                                            label = { Text("Animale") },
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(
                                                    expanded = expanded
                                                )
                                            },
                                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                        )
                                        ExposedDropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false },
                                        ) {
                                            options.forEach { selectionOption ->
                                                DropdownMenuItem(
                                                    text = { Text(selectionOption) },
                                                    onClick = {
                                                        selectedOptionText = selectionOption
                                                        expanded = false
                                                        filterAnima=selectedOptionText; isFiltersActive =
                                                        selectedOptionText!="" || filterPref
                                                    },
                                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                                )
                                            }
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                Row(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = { showFilterDialog = false; }) {
                                        Text("OK")
                                    }
                                }
                            }
                        )
                    }
                }
            )
        }
    ){ it ->
        val list = mutableListOf<AvvistamentiDaVedere>()
        val temp = takeDatasList(avvistamentiViewModel, avvistamentiViewViewModel)

        for (e in temp){
            if (filterAnima == "" && !filterPref) {
                list.add(e)
            } else if (filterAnima != "" && !filterPref) {
                if(e.animale==filterAnima){
                    list.add(e)
                }
            } else if(filterAnima == "" && filterPref) {
                if(e.id in listFavourite){
                    list.add(e)
                }
            } else {
                if(e.animale==filterAnima && e.id in listFavourite){
                    list.add(e)
                }
            }
        }

        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                /** Homepage Orizzontale */
                Row(
                    modifier = modifier
                        .fillMaxSize()
                        .background(backGround)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier=Modifier.width((configuration.screenWidthDp/2.3).dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(it),
                            contentAlignment = Alignment.Center
                        ) {
                            AndroidView(
                                factory = { context ->
                                    WebView(context).apply {
                                        // Imposta le opzioni WebView necessarie
                                        settings.javaScriptEnabled = true
                                        settings.domStorageEnabled = true
                                        loadUrl("file:///android_asset/leaflet/index.html")

                                        webViewClient = object : WebViewClient() {
                                            override fun onPageFinished(view: WebView?, url: String?) {
                                                super.onPageFinished(view, url)
                                                val mkList = mutableListOf<MarkerData>()
                                                for (e in list) {
                                                    mkList.add(
                                                        MarkerData(
                                                            e.latid,
                                                            e.long,
                                                            e.data,
                                                            e.animale,
                                                            e.specie
                                                        )
                                                    )
                                                }
                                                val gson = Gson()
                                                val markerDataJson = gson.toJson(mkList)
                                                val currentMarkerDataJson = markerDataJson
                                                mapSet=true
                                                try {
                                                    view?.evaluateJavascript("addMarkers('$currentMarkerDataJson')", null)
                                                } catch (e: Exception) {
                                                }
                                            }
                                        }
                                    }
                                },
                                update = { webView ->
                                    if(mapSet) {
                                        try {
                                            webView.evaluateJavascript("removeMarkers()", null)
                                            val mkList = mutableListOf<MarkerData>()
                                            for (e in list) {
                                                mkList.add(
                                                    MarkerData(
                                                        e.latid,
                                                        e.long,
                                                        e.data,
                                                        e.animale,
                                                        e.specie
                                                    )
                                                )
                                            }
                                            val gson = Gson()
                                            val markerDataJson = gson.toJson(mkList)
                                            val currentMarkerDataJson = markerDataJson
                                            webView.evaluateJavascript(
                                                "addMarkers('$currentMarkerDataJson')",
                                                null
                                            )
                                        } catch (e: Exception) {
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    LazyColumn(modifier = modifier.padding(vertical = 10.dp),horizontalAlignment = Alignment.CenterHorizontally) {
                        items(1){element->
                            Spacer(modifier = Modifier.height(30.dp))
                            Spacer(modifier = Modifier.height(min))
                            Text(
                                text = "Avvistamenti",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.height(min/2))
                            Row(modifier=Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center) {
                                for(k in 0..1){
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier=Modifier.width((configuration.screenWidthDp/4).dp)) {
                                        for (i in k..list.count()-1 step 2) {
                                            var nome by rememberSaveable { mutableStateOf(list[i].nome) }
                                            var cognome by rememberSaveable { mutableStateOf(list.get(i).cognome) }
                                            var img by rememberSaveable { mutableStateOf(list.get(i).img) }
                                            if(!list.get(i).online){
                                                if (isNetworkAvailable(context)) {
                                                    val client = OkHttpClient()
                                                    val formBody = MultipartBody.Builder()
                                                        .setType(MultipartBody.FORM)
                                                        .addFormDataPart("user", list.get(i).avvistatore)
                                                        .addFormDataPart("request", "getUserInfoMob")
                                                        .build()
                                                    val request = Request.Builder()
                                                        .url("https://isi-seawatch.csr.unibo.it/Sito/sito/templates/main_settings/settings_api.php")
                                                        .post(formBody)
                                                        .build()

                                                    client.newCall(request).enqueue(object : Callback {
                                                        override fun onFailure(call: Call, e: IOException) {
                                                        }

                                                        override fun onResponse(call: Call, response: Response) {
                                                            val body = response.body?.string()
                                                            val msg = JSONArray(body.toString())
                                                            try {
                                                                nome = (msg.get(0) as JSONObject).get("Nome").toString()
                                                                cognome = (msg.get(0) as JSONObject).get("Cognome").toString()
                                                                img = (msg.get(0) as JSONObject).get("Img").toString()
                                                                list.get(i).nome = nome
                                                                list.get(i).cognome = cognome
                                                            } catch (e: Exception) {
                                                            }
                                                        }
                                                    })
                                                } else {
                                                    val userItems: List<User> by userViewModel.all.collectAsState(initial = listOf())
                                                    for(elem in userItems){
                                                        if(elem.mail == list.get(i).avvistatore){
                                                            nome = elem.nome
                                                            cognome = elem.cognome
                                                            list.get(i).nome = nome
                                                            list.get(i).cognome = cognome
                                                            break
                                                        }
                                                    }
                                                }
                                            } else {
                                                val userItems: List<User> by userViewModel.all.collectAsState(initial = listOf())
                                                for(elem in userItems){
                                                    if(elem.mail == list.get(i).avvistatore){
                                                        nome = elem.nome
                                                        cognome = elem.cognome
                                                        list.get(i).nome = nome
                                                        list.get(i).cognome = cognome
                                                        break
                                                    }
                                                }
                                            }
                                            Card(
                                                shape = MaterialTheme.shapes.medium,
                                                modifier = Modifier
                                                    .padding(5.dp)
                                                    .size(width = 180.dp, height = 160.dp),
                                                border = BorderStroke(2.dp, Color.Black),
                                                colors = CardDefaults.cardColors(containerColor = if(list.get(i).online){MaterialTheme.colorScheme.secondaryContainer} else {MaterialTheme.colorScheme.tertiaryContainer}),
                                                elevation = CardDefaults.cardElevation(4.dp),
                                                onClick = {
                                                    sightingID = list.get(i)
                                                    goToSighting()
                                                }
                                            ) {
                                                var isFavorite =list.get(i).id in listFavourite
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(12.dp)
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(48.dp)
                                                                .clip(CircleShape)
                                                                .clickable {
                                                                    profileViewModel.set(list.get(i).avvistatore)
                                                                    goToProfile()
                                                                }
                                                        ) {
                                                            val scale = if(img=="profilo.jpg" || !isNetworkAvailable(context)){1.0f}else{1.8f}
                                                            Image(
                                                                painter = rememberAsyncImagePainter(
                                                                    model = if(isNetworkAvailable(context)){
                                                                        "https://isi-seawatch.csr.unibo.it/Sito/img/profilo/" + img
                                                                    } else {
                                                                        R.drawable.profilo
                                                                    }
                                                                ),
                                                                contentDescription = "Immagine del profilo",
                                                                modifier = Modifier
                                                                    .size(48.dp)
                                                                    .clip(CircleShape)
                                                                    .scale(scale)
                                                            )
                                                        }
                                                        Spacer(modifier = Modifier.width(med + 30.dp))
                                                        IconButton(
                                                            onClick = {
                                                                if(isFavorite){
                                                                    favouriteViewModel.deletePref(list.get(i).id, em)
                                                                    listFavourite.remove(list.get(i).id)
                                                                } else {
                                                                    favouriteViewModel.insert(Favourite(System.currentTimeMillis().toString(), em, list.get(i).id))
                                                                    listFavourite.add(list.get(i).id)
                                                                }
                                                                isFavorite = !isFavorite
                                                            },
                                                            modifier = Modifier.align(Alignment.CenterVertically)
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.Favorite,
                                                                contentDescription = "Favorite",
                                                                tint = if (isFavorite) Color.Red else LocalContentColor.current
                                                            )
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.height(10.dp))
                                                    Text(
                                                        text = list.get(i).data.substring(0,16),
                                                        style = MaterialTheme.typography.titleMedium
                                                    )
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Text(
                                                        text = list.get(i).animale,
                                                        style = MaterialTheme.typography.titleMedium
                                                    )
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Text(
                                                        text = nome + " " + cognome,
                                                        style = MaterialTheme.typography.titleMedium
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else -> {
                /** Homepage verticale*/
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .background(backGround),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(1) { element ->
                        Spacer(modifier = Modifier.height(50.dp))
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            AndroidView(
                                factory = { context ->
                                    WebView(context).apply {
                                        // Imposta le opzioni WebView necessarie
                                        settings.javaScriptEnabled = true
                                        settings.domStorageEnabled = true
                                        loadUrl("file:///android_asset/leaflet/index.html")

                                        webViewClient = object : WebViewClient() {
                                            override fun onPageFinished(view: WebView?, url: String?) {
                                                try {
                                                    super.onPageFinished(view, url)
                                                    val mkList = mutableListOf<MarkerData>()
                                                    for (e in list) {
                                                        mkList.add(
                                                            MarkerData(
                                                                e.latid,
                                                                e.long,
                                                                e.data,
                                                                e.animale,
                                                                e.specie
                                                            )
                                                        )
                                                    }
                                                    val gson = Gson()
                                                    val markerDataJson = gson.toJson(mkList)
                                                    val currentMarkerDataJson = markerDataJson
                                                    mapSet = true
                                                    view?.evaluateJavascript("addMarkers('$currentMarkerDataJson')", null)
                                                } catch (e: Exception) {
                                                }
                                            }
                                        }
                                    }
                                },
                                update = { webView ->
                                    if(mapSet) {
                                        try {
                                            webView.evaluateJavascript("removeMarkers()", null)
                                            val mkList = mutableListOf<MarkerData>()
                                            for (e in list) {
                                                mkList.add(
                                                    MarkerData(
                                                        e.latid,
                                                        e.long,
                                                        e.data,
                                                        e.animale,
                                                        e.specie
                                                    )
                                                )
                                            }
                                            val gson = Gson()
                                            val markerDataJson = gson.toJson(mkList)
                                            val currentMarkerDataJson = markerDataJson
                                            webView.evaluateJavascript(
                                                "addMarkers('$currentMarkerDataJson')",
                                                null
                                            )
                                        } catch (e: Exception) {
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.height(min))
                        Text(
                            text = "Avvistamenti",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(min/2))
                        Row(modifier=Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center) {
                            for(k in 0..1){
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier=Modifier.width((configuration.screenWidthDp/2).dp)) {
                                    for (i in k..list.count()-1 step 2) {
                                        var nome by rememberSaveable { mutableStateOf(list.get(i).nome) }
                                        var cognome by rememberSaveable { mutableStateOf(list.get(i).cognome) }
                                        var img by rememberSaveable { mutableStateOf(list.get(i).img) }
                                        if(!list.get(i).online){
                                            if (isNetworkAvailable(context)) {
                                                val client = OkHttpClient()
                                                val formBody = MultipartBody.Builder()
                                                    .setType(MultipartBody.FORM)
                                                    .addFormDataPart("user", list.get(i).avvistatore)
                                                    .addFormDataPart("request", "getUserInfoMob")
                                                    .build()
                                                val request = Request.Builder()
                                                    .url("https://isi-seawatch.csr.unibo.it/Sito/sito/templates/main_settings/settings_api.php")
                                                    .post(formBody)
                                                    .build()

                                                client.newCall(request).enqueue(object : Callback {
                                                    override fun onFailure(call: Call, e: IOException) {
                                                    }

                                                    override fun onResponse(call: Call, response: Response) {
                                                        val body = response.body?.string()
                                                        val msg = JSONArray(body.toString())
                                                        try {
                                                            nome = (msg.get(0) as JSONObject).get("Nome").toString()
                                                            cognome = (msg.get(0) as JSONObject).get("Cognome").toString()
                                                            img = (msg.get(0) as JSONObject).get("Img").toString()
                                                            list.get(i).nome = nome
                                                            list.get(i).cognome = cognome
                                                        } catch (e: Exception) {
                                                        }
                                                    }
                                                })
                                            } else {
                                                val userItems: List<User> by userViewModel.all.collectAsState(initial = listOf())
                                                for(elem in userItems){
                                                    if(elem.mail == list.get(i).avvistatore){
                                                        nome = elem.nome
                                                        cognome = elem.cognome
                                                        list.get(i).nome = nome
                                                        list.get(i).cognome = cognome
                                                        break
                                                    }
                                                }
                                            }
                                        } else {
                                            val userItems: List<User> by userViewModel.all.collectAsState(initial = listOf())
                                            for(elem in userItems){
                                                if(elem.mail == list.get(i).avvistatore){
                                                    nome = elem.nome
                                                    cognome = elem.cognome
                                                    list.get(i).nome = nome
                                                    list.get(i).cognome = cognome
                                                    break
                                                }
                                            }
                                        }
                                        Card(
                                            shape = MaterialTheme.shapes.medium,
                                            modifier = Modifier
                                                .padding(5.dp)
                                                .size(width = 180.dp, height = 160.dp),
                                            border = BorderStroke(2.dp, Color.Black),
                                            colors = CardDefaults.cardColors(containerColor = if(list.get(i).online){MaterialTheme.colorScheme.secondaryContainer} else {MaterialTheme.colorScheme.tertiaryContainer}),
                                            elevation = CardDefaults.cardElevation(4.dp),
                                            onClick = {
                                                sightingID = list.get(i)
                                                goToSighting()
                                            }
                                        ) {
                                            var isFavorite =list.get(i).id in listFavourite
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(48.dp)
                                                            .clip(CircleShape)
                                                            .clickable {
                                                                profileViewModel.set(list.get(i).avvistatore)
                                                                goToProfile()
                                                            }
                                                    ) {
                                                        val scale = if(img=="profilo.jpg" || !isNetworkAvailable(context)){1.0f}else{1.8f}
                                                        Image(
                                                            painter = rememberAsyncImagePainter(
                                                                model = if(isNetworkAvailable(context)){
                                                                    "https://isi-seawatch.csr.unibo.it/Sito/img/profilo/" + img
                                                                } else {
                                                                    R.drawable.profilo
                                                                }
                                                            ),
                                                            contentDescription = "Immagine del profilo",
                                                            modifier = Modifier
                                                                .size(48.dp)
                                                                .clip(CircleShape)
                                                                .scale(scale)
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(med + 30.dp))
                                                    IconButton(
                                                        onClick = {
                                                            if(isFavorite){
                                                                favouriteViewModel.deletePref(list.get(i).id, em)
                                                                listFavourite.remove(list.get(i).id)
                                                            } else {
                                                                favouriteViewModel.insert(Favourite(System.currentTimeMillis().toString(), em, list.get(i).id))
                                                                listFavourite.add(list.get(i).id)
                                                            }
                                                            isFavorite = !isFavorite
                                                        },
                                                        modifier = Modifier.align(Alignment.CenterVertically)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Favorite,
                                                            contentDescription = "Favorite",
                                                            tint = if (isFavorite) Color.Red else LocalContentColor.current
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Text(
                                                    text = list.get(i).data.substring(0,16),
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = list.get(i).animale,
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = nome + " " + cognome,
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun showImages( imagesUri: List<Uri>?, context: Context) {
    val uris = imagesUri ?: emptyList()
    var sureDeleteImage by rememberSaveable { mutableStateOf(false) }
    var uriToBeDeleted by rememberSaveable { mutableStateOf<Uri>(Uri.EMPTY) }
    if(sureDeleteImage){
        AlertDialog(
            onDismissRequest = { sureDeleteImage = false },
            title = { Text("AVVISO") },
            text = { Text("Sei sicuro di voler eliminare l'immagine selezionata?") },
            confirmButton = {
                Button(
                    onClick = {
                        sureDeleteImage = false;
                        context.contentResolver.delete(uriToBeDeleted, null, null)
                        uriToBeDeleted=Uri.EMPTY
                        recreate((context as Activity)) },

                ) {
                    Text("Sì")
                }
            },
            dismissButton = {
                Button(
                    onClick = { sureDeleteImage = false }
                ) {
                    Text("No")
                }
            }
        )
    }

    Column (horizontalAlignment = Alignment.CenterHorizontally){
        for (uri in uris) {
            Card(
                modifier = Modifier
                    .width(500.dp)
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column {
                        val painter = rememberAsyncImagePainter(uri.toString())
                        Image(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            painter = painter,
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                        Row(horizontalArrangement = Arrangement.Center){
                            Button(
                                modifier = Modifier
                                    .padding(10.dp),
                                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setDataAndType(uri, "image/*")
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(intent)
                                }
                            ) {
                                Text("VISUALIZZA")
                            }
                            Button(
                                modifier = Modifier
                                    .padding(10.dp),
                                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
                                onClick = {
                                    sureDeleteImage=true
                                    uriToBeDeleted=uri
                                }
                            ) {
                                Text("ELIMINA")
                            }
                        }
                    }
                }
            }
        }
    }
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    modifier: Modifier = Modifier,
    avvistamentiViewViewModel: AvvistamentiViewViewModel
) {
    val configuration = LocalConfiguration.current
    val min = configuration.screenHeightDp.dp / 40
    val med = configuration.screenHeightDp.dp / 20
    val hig = configuration.screenHeightDp.dp / 10
    val backGround = MaterialTheme.colorScheme.primaryContainer
    val listaAvvistamenti: List<AvvistamentiDaVedere> by avvistamentiViewViewModel.all.collectAsState(
        initial = listOf()
    )

    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(backGround),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(1) { element ->
                    Spacer(Modifier.height(min))
                    Row(modifier = modifier.fillMaxWidth()) {
                        Column(
                            modifier = modifier
                                .width((configuration.screenWidthDp / 2).dp)
                                .padding(10.dp)
                        ) {
                            Card(
                                shape = MaterialTheme.shapes.medium,
                                border = BorderStroke(2.dp, Color.Black),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White
                                ),
                                elevation = CardDefaults.cardElevation(40.dp)
                            ) {
                                Column() {
                                    val animatedValue = animateFloatAsState(
                                        targetValue = listaAvvistamenti.count().toFloat(),
                                        animationSpec = TweenSpec(
                                            durationMillis = 1500,
                                            easing = LinearOutSlowInEasing
                                        )
                                    )
                                    Text(
                                        text = animatedValue.value.toInt().toString(),
                                        fontSize = 48.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Text(
                                        text = "avvistamenti",
                                        fontSize = 18.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = modifier.height(5.dp))
                                }

                            }
                        }
                        Column(
                            modifier = modifier
                                .width((configuration.screenWidthDp / 2).dp)
                                .padding(10.dp)
                        ) {
                            Card(
                                shape = MaterialTheme.shapes.medium,
                                border = BorderStroke(2.dp, Color.Black),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White
                                ),
                                elevation = CardDefaults.cardElevation(40.dp)
                            ) {
                                Column() {
                                    val animatedValue = animateFloatAsState(
                                        targetValue = listaAvvistamenti.distinctBy { it.avvistatore }.size.toFloat(),
                                        animationSpec = TweenSpec(
                                            durationMillis = 1500,
                                            easing = LinearOutSlowInEasing
                                        )
                                    )
                                    Text(
                                        text = animatedValue.value.toInt().toString(),
                                        fontSize = 48.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Text(
                                        text = "avvistatori",
                                        fontSize = 18.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = modifier.height(5.dp))
                                }
                            }
                        }
                    }
                    Column(modifier=modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                        val mappaOccorrenze = mutableMapOf<String, Int>()
                        for (avvistamento in listaAvvistamenti) {
                            val animale = avvistamento.animale
                            if (mappaOccorrenze.containsKey(animale)) {
                                mappaOccorrenze[animale] = mappaOccorrenze[animale]!! + 1
                            } else {
                                mappaOccorrenze[animale] = 1
                            }
                        }
                        val mappaFinale=mappaOccorrenze.toList().sortedByDescending { (_, value) -> value }.toMap()
                        val l= mutableListOf<BarChartData.Bar>()
                        for(e in mappaFinale){
                            l.add(BarChartData.Bar(label=e.key, value=e.value.toFloat(), color=MaterialTheme.colorScheme.primary))
                        }
                        Spacer(modifier=modifier.height(min))
                        Text(text="DISTRIBUZIONE AVVISTAMENTI", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier=modifier.height(min))
                        BarChart(
                            barChartData = BarChartData(bars = l, padBy = 0f, startAtZero = true),
                            modifier = Modifier.size(640.dp, 270.dp),
                            animation = simpleChartAnimation(),
                            barDrawer = SimpleBarDrawer(),
                            xAxisDrawer = SimpleXAxisDrawer(),
                            yAxisDrawer = SimpleYAxisDrawer(),
                            labelDrawer = SimpleValueDrawer(drawLocation = SimpleValueDrawer.DrawLocation.XAxis)
                        )

                        val mappaClassifica = mutableMapOf<String, Int>()
                        for (avvistamento in listaAvvistamenti) {
                            val utente = avvistamento.avvistatore
                            if (mappaClassifica.containsKey(utente)) {
                                mappaClassifica[utente] = mappaClassifica[utente]!! + 1
                            } else {
                                mappaClassifica[utente] = 1
                            }
                        }
                        val mappaFinaleClassifica=mappaClassifica.toList().sortedByDescending { (_, value) -> value }.take(3).toMap()
                        val lClassifica= mutableListOf<BarChartData.Bar>()
                        for(e in mappaFinaleClassifica){
                            lClassifica.add(BarChartData.Bar(label=e.key.split("@")[0], value=e.value.toFloat(), color=MaterialTheme.colorScheme.secondary))
                        }
                        Spacer(modifier=modifier.height(med))
                        Text(text="CLASSIFICA AVVISTATORI", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier=modifier.height(min))
                        BarChart(
                            barChartData = BarChartData(bars = lClassifica, padBy = 0f),
                            modifier = Modifier.size(640.dp, 270.dp),
                            animation = simpleChartAnimation(),
                            barDrawer = SimpleBarDrawer(),
                            xAxisDrawer = SimpleXAxisDrawer(),
                            yAxisDrawer = SimpleYAxisDrawer(),
                            labelDrawer = SimpleValueDrawer(drawLocation = SimpleValueDrawer.DrawLocation.XAxis)
                        )
                        val mappaDelfino = mutableMapOf<String, Int>()
                        for (avvistamento in listaAvvistamenti) {
                            if(avvistamento.animale.lowercase()=="delfino"){
                                var specie = avvistamento.specie
                                if(specie=="null"){
                                    specie="?"
                                }
                                if (mappaDelfino.containsKey(specie)) {
                                    mappaDelfino[specie] = mappaDelfino[specie]!! + 1
                                } else {
                                    mappaDelfino[specie] = 1
                                }
                            }
                        }
                        val mappaDelfinoFinale=mappaDelfino.toList().sortedByDescending { (_, value) -> value }.take(5).toMap()
                        val lDelfino= mutableListOf<BarChartData.Bar>()
                        for(e in mappaDelfinoFinale){
                            lDelfino.add(BarChartData.Bar(label=e.key, value=e.value.toFloat(), color=MaterialTheme.colorScheme.tertiary))
                        }
                        Spacer(modifier=modifier.height(med))
                        Text(text="DISTRIBUZIONE DELFINI", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier=modifier.height(min))
                        BarChart(
                            barChartData = BarChartData(bars = lDelfino, padBy = 0f, startAtZero = true),
                            modifier = Modifier.size(640.dp, 270.dp),
                            animation = simpleChartAnimation(),
                            barDrawer = SimpleBarDrawer(),
                            xAxisDrawer = SimpleXAxisDrawer(),
                            yAxisDrawer = SimpleYAxisDrawer(),
                            labelDrawer = SimpleValueDrawer(drawLocation = SimpleValueDrawer.DrawLocation.XAxis)
                        )
                        val mappaDate = mutableMapOf<String, Int>()
                        for (avvistamento in listaAvvistamenti) {
                            val data = avvistamento.data.substring(0..9)
                            if (mappaDate.containsKey(data)) {
                                mappaDate[data] = mappaDate[data]!! + 1
                            } else {
                                if(mappaDate.keys.count()<5){
                                    mappaDate[data] = 1
                                }
                            }
                        }
                        val mappaDateFinale=mappaDate.toList().reversed().toMap()
                        val lDate= mutableListOf<LineChartData.Point>()
                        for(e in mappaDateFinale){
                            lDate.add(LineChartData.Point(
                                e.value.toFloat(),
                                e.key))
                        }
                        Spacer(modifier=modifier.height(min+10.dp))
                        Text(text="EVOLUZIONE AVVISTAMENTI", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier=modifier.height(min))
                        val lab2=mutableListOf<String>()
                        val lab= mappaDateFinale.keys.toList()
                        for(e in lab){
                            lab2.add(e.substring(0..4))
                        }

                        LineChart(
                            linesChartData = listOf(LineChartData(points = lDate, padBy = 0f, startAtZero = true, lineDrawer = SolidLineDrawer(color=Color.Black))),
                            modifier = Modifier.size(640.dp, 270.dp),
                            animation = simpleChartAnimation(),
                            pointDrawer = FilledCircularPointDrawer(color=Color.Red),
                            horizontalOffset = 0f,
                            labels = lab2
                        )
                        Spacer(modifier=modifier.height(hig))
                    }
                }
            }
        }
        else -> {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(backGround),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(1) { element ->
                    Spacer(Modifier.height(min))
                    Row(modifier = modifier.fillMaxWidth()) {
                        Column(
                            modifier = modifier
                                .width((configuration.screenWidthDp / 2).dp)
                                .padding(10.dp)
                        ) {
                            Card(
                                shape = MaterialTheme.shapes.medium,
                                border = BorderStroke(2.dp, Color.Black),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White
                                ),
                                elevation = CardDefaults.cardElevation(40.dp)
                            ) {
                                Column() {
                                    val animatedValue = animateFloatAsState(
                                        targetValue = listaAvvistamenti.count().toFloat(),
                                        animationSpec = TweenSpec(
                                            durationMillis = 1500,
                                            easing = LinearOutSlowInEasing
                                        )
                                    )
                                    Text(
                                        text = animatedValue.value.toInt().toString(),
                                        fontSize = 48.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Text(
                                        text = "avvistamenti",
                                        fontSize = 18.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = modifier.height(5.dp))
                                }

                            }
                        }
                        Column(
                            modifier = modifier
                                .width((configuration.screenWidthDp / 2).dp)
                                .padding(10.dp)
                        ) {
                            Card(
                                shape = MaterialTheme.shapes.medium,
                                border = BorderStroke(2.dp, Color.Black),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White
                                ),
                                elevation = CardDefaults.cardElevation(40.dp)
                            ) {
                                Column {
                                    val animatedValue = animateFloatAsState(
                                        targetValue = listaAvvistamenti.distinctBy { it.avvistatore }.size.toFloat(),
                                        animationSpec = TweenSpec(
                                            durationMillis = 1500,
                                            easing = LinearOutSlowInEasing
                                        )
                                    )
                                    Text(
                                        text = animatedValue.value.toInt().toString(),
                                        fontSize = 48.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Text(
                                        text = "avvistatori",
                                        fontSize = 18.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = modifier.height(5.dp))
                                }
                            }
                        }
                    }
                    Column(modifier=modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                        val mappaOccorrenze = mutableMapOf<String, Int>()
                        for (avvistamento in listaAvvistamenti) {
                            val animale = avvistamento.animale
                            if (mappaOccorrenze.containsKey(animale)) {
                                mappaOccorrenze[animale] = mappaOccorrenze[animale]!! + 1
                            } else {
                                mappaOccorrenze[animale] = 1
                            }
                        }
                        val mappaFinale=mappaOccorrenze.toList().sortedByDescending { (_, value) -> value }.toMap()
                        val l= mutableListOf<BarChartData.Bar>()
                        for(e in mappaFinale){
                            l.add(BarChartData.Bar(label=e.key, value=e.value.toFloat(), color=MaterialTheme.colorScheme.primary))
                        }
                        Spacer(modifier=modifier.height(min))
                        Text(text="DISTRIBUZIONE AVVISTAMENTI", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier=modifier.height(min))
                        BarChart(
                            barChartData = BarChartData(bars = l, padBy = 0f, startAtZero = true),
                            modifier = Modifier.size(320.dp, 270.dp),
                            animation = simpleChartAnimation(),
                            barDrawer = SimpleBarDrawer(),
                            xAxisDrawer = SimpleXAxisDrawer(),
                            yAxisDrawer = SimpleYAxisDrawer(),
                            labelDrawer = SimpleValueDrawer(drawLocation = SimpleValueDrawer.DrawLocation.XAxis)
                        )


                        val mappaClassifica = mutableMapOf<String, Int>()
                        for (avvistamento in listaAvvistamenti) {
                            val utente = avvistamento.avvistatore
                            if (mappaClassifica.containsKey(utente)) {
                                mappaClassifica[utente] = mappaClassifica[utente]!! + 1
                            } else {
                                mappaClassifica[utente] = 1
                            }
                        }
                        val mappaFinaleClassifica=mappaClassifica.toList().sortedByDescending { (_, value) -> value }.take(3).toMap()
                        val lClassifica= mutableListOf<BarChartData.Bar>()
                        for(e in mappaFinaleClassifica){
                            lClassifica.add(BarChartData.Bar(label=e.key.split("@")[0], value=e.value.toFloat(), color=MaterialTheme.colorScheme.secondary))
                        }
                        Spacer(modifier=modifier.height(min+10.dp))
                        Text(text="CLASSIFICA AVVISTATORI", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier=modifier.height(min))
                        BarChart(
                            barChartData = BarChartData(bars = lClassifica, padBy = 0f),
                            modifier = Modifier.size(320.dp, 270.dp),
                            animation = simpleChartAnimation(),
                            barDrawer = SimpleBarDrawer(),
                            xAxisDrawer = SimpleXAxisDrawer(),
                            yAxisDrawer = SimpleYAxisDrawer(),
                            labelDrawer = SimpleValueDrawer(drawLocation = SimpleValueDrawer.DrawLocation.XAxis)
                        )

                        val mappaDelfino = mutableMapOf<String, Int>()
                        for (avvistamento in listaAvvistamenti) {
                            if(avvistamento.animale.lowercase()=="delfino"){
                                var specie = avvistamento.specie
                                if(specie=="null"){
                                    specie="?"
                                }
                                if (mappaDelfino.containsKey(specie)) {
                                    mappaDelfino[specie] = mappaDelfino[specie]!! + 1
                                } else {
                                    mappaDelfino[specie] = 1
                                }
                            }
                        }
                        val mappaDelfinoFinale=mappaDelfino.toList().sortedByDescending { (_, value) -> value }.take(5).toMap()
                        val lDelfino= mutableListOf<BarChartData.Bar>()
                        for(e in mappaDelfinoFinale){
                            lDelfino.add(BarChartData.Bar(label=e.key.split(" ")[0], value=e.value.toFloat(), color=MaterialTheme.colorScheme.tertiary))
                        }
                        Spacer(modifier=modifier.height(min+10.dp))
                        Text(text="DISTRIBUZIONE DELFINI", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier=modifier.height(min))
                        BarChart(
                            barChartData = BarChartData(bars = lDelfino, padBy = 0f, startAtZero = true),
                            modifier = Modifier.size(320.dp, 270.dp),
                            animation = simpleChartAnimation(),
                            barDrawer = SimpleBarDrawer(),
                            xAxisDrawer = SimpleXAxisDrawer(),
                            yAxisDrawer = SimpleYAxisDrawer(),
                            labelDrawer = SimpleValueDrawer(drawLocation = SimpleValueDrawer.DrawLocation.XAxis)
                        )

                        val mappaDate = mutableMapOf<String, Int>()
                        for (avvistamento in listaAvvistamenti) {
                            val data = avvistamento.data.substring(0..9)
                            if (mappaDate.containsKey(data)) {
                                mappaDate[data] = mappaDate[data]!! + 1
                            } else {
                                if(mappaDate.keys.count()<5){
                                    mappaDate[data] = 1
                                }
                            }
                        }
                        val mappaDateFinale=mappaDate.toList().reversed().toMap()
                        val lDate= mutableListOf<LineChartData.Point>()
                        for(e in mappaDateFinale){
                            lDate.add(LineChartData.Point(
                                e.value.toFloat(),
                                e.key))
                        }
                        Spacer(modifier=modifier.height(min+10.dp))
                        Text(text="EVOLUZIONE AVVISTAMENTI", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier=modifier.height(min))
                        val lab2=mutableListOf<String>()
                        val lab= mappaDateFinale.keys.toList()
                        for(e in lab){
                            lab2.add(e.substring(0..4))
                        }

                        LineChart(
                            linesChartData = listOf(LineChartData(points = lDate, padBy = 0f, startAtZero = true, lineDrawer = SolidLineDrawer(color=Color.Black))),
                            modifier = Modifier.size(320.dp, 270.dp),
                            animation = simpleChartAnimation(),
                            pointDrawer = FilledCircularPointDrawer(color=Color.Red),
                            horizontalOffset = 0f,
                            labels = lab2
                        )
                        Spacer(modifier=modifier.height(hig))






                    }
                }
            }
        }
    }
}
