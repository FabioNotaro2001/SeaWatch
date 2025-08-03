package com.example.seawatch


import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import com.example.seawatch.data.*
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

@Composable
fun drawLocalImages(img:String, contex:Context, sighting: Sighting, viewModel: AvvistamentiViewModel): Sighting{
    if (img != "") {
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
                    val painter = rememberAsyncImagePainter(img)
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
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setDataAndType(Uri.parse(img), "image/*")
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    contex.startActivity(intent)
                                }catch(e: Exception){

                                }
                            }
                        ) {
                            Text("VISUALIZZA")
                        }
                        Button(
                            modifier = Modifier
                                .padding(10.dp),
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
                            onClick = {
                                try {
                                    if(sighting.image1==img){sighting.image1=""}
                                    if(sighting.image2==img){sighting.image2=""}
                                    if(sighting.image3==img){sighting.image3=""}
                                    if(sighting.image4==img){sighting.image4=""}
                                    if(sighting.image5==img){sighting.image5=""}
                                    val a = AvvistamentiDaCaricare(
                                        sighting.id,
                                        sighting.user,
                                        sighting.date,
                                        sighting.numberOfSamples,
                                        sighting.position,
                                        sighting.animal,
                                        sighting.specie,
                                        sighting.sea,
                                        sighting.wind,
                                        sighting.notes,
                                        sighting.image1,
                                        sighting.image2,
                                        sighting.image3,
                                        sighting.image4,
                                        sighting.image5,
                                        false
                                    )
                                    viewModel.insert(a)
                                    contex.contentResolver.delete(Uri.parse(img), null, null)
                                }catch(e: Exception){

                                }

                            }
                        ) {
                            Text("ELIMINA")
                        }
                    }
                }
            }
        }
    }
    return sighting
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SightingViewScreen(
    modifier: Modifier = Modifier,
    avvistamentiViewModel: AvvistamentiViewModel,
    avvistamentiViewViewModel: AvvistamentiViewViewModel,
    descriptionViewModel: DescriptionViewModel
) {
    val elem = sightingID!!
    val configuration = LocalConfiguration.current
    val min = configuration.screenHeightDp.dp / 40
    val hig = configuration.screenHeightDp.dp / 10
    val backGround = MaterialTheme.colorScheme.primaryContainer
    val utente by remember {
        mutableStateOf(
            if (elem.nome == "null") {
                ""
            } else {
                elem.nome
            } + " " + if (elem.cognome == "null") {
                ""
            } else {
                elem.cognome
            }
        )
    }
    val data by rememberSaveable {
        mutableStateOf(
            if (elem.data == "null") {
                ""
            } else {
                elem.data
            }
        )
    }
    var numeroEsemplari by rememberSaveable {
        mutableStateOf(
            if (elem.numeroEsemplari == "null") {
                ""
            } else {
                elem.numeroEsemplari
            }
        )
    }
    var posizione by rememberSaveable {
        mutableStateOf(
            if (elem.latid == "null") {
                ""
            } else {
                elem.latid
            } + " " + if (elem.long == "null") {
                ""
            } else {
                elem.long
            }
        )
    }
    var mare by rememberSaveable {
        mutableStateOf(
            if (elem.mare == "null") {
                ""
            } else {
                elem.mare
            }
        )
    }
    var vento by rememberSaveable {
        mutableStateOf(
            if (elem.vento == "null") {
                ""
            } else {
                elem.vento
            }
        )
    }
    var note by rememberSaveable {
        mutableStateOf(
            if (elem.note == "null") {
                ""
            } else {
                elem.note
            }
        )
    }
    val options by rememberSaveable { mutableStateOf(getAnimal()) }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedOptionText by rememberSaveable {
        mutableStateOf(
            if (elem.animale == "null") {
                ""
            } else {
                elem.animale
            }
        )
    }
    var expandedSpecie by rememberSaveable { mutableStateOf(false) }
    var selectedOptionTextSpecie by rememberSaveable {
        mutableStateOf(
            if (elem.specie == "null") {
                ""
            } else {
                elem.specie
            }
        )
    }
    var showFilterInfoSpecie by rememberSaveable { mutableStateOf(false) }
    val contex = LocalContext.current
    var info by rememberSaveable { mutableStateOf(false) }
    var imageMessage by rememberSaveable { mutableStateOf(false) }
    var alreadySeen by rememberSaveable { mutableStateOf(false) }

    var currentDateTime by rememberSaveable {mutableStateOf( System.currentTimeMillis().toString())}
    var count by rememberSaveable {mutableStateOf(0)}
    val imagesList =(contex as MainActivity).getAllSavedImages(currentDateTime)
    var sighting = Sighting(elem.id, em, data, numeroEsemplari, posizione, selectedOptionText, selectedOptionTextSpecie, mare, vento, note)

    var errorMessage by rememberSaveable { mutableStateOf("") }
    var descMessage by rememberSaveable { mutableStateOf("") }
    var immaginiOnline by rememberSaveable { mutableStateOf("") }
    val descrizioni:List<Description> by descriptionViewModel.all.collectAsState(initial = listOf())


    if (showFilterInfoSpecie){
        AlertDialog(
            onDismissRequest = {showFilterInfoSpecie=false},
            title = { Text("Dettagli") },
            text = {
                Column {
                    Row{
                        LazyColumn(
                            modifier = Modifier
                                .padding(5.dp)
                        ) {
                            items(1) { element ->
                                Text(
                                    text = descMessage,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {showFilterInfoSpecie=false}) {
                    Text("Chiudi")
                }
            }
        )
    }
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

    if(imageMessage) {
        AlertDialog(
            onDismissRequest = { imageMessage = false; alreadySeen=true },
            title = { Text(text = "ATTENZIONE") },
            text = { Text(text = "Le immagini verranno aggiunte all'avvistamento solo dopo aver premuto il pulsante Salva!") },
            confirmButton = {
                Button(onClick = { imageMessage=false;alreadySeen=true }) {
                    Text(text = "OK")
                }
            }
        )
    }


    if (isNetworkAvailable(contex)){
        if (elem.online) {
            val client = OkHttpClient()
            val formBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", elem.id)
                .addFormDataPart("request", "getImages")
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
                    val msg = body.toString()
                    immaginiOnline = msg
                }
            })
        } else {
            if(em==elem.avvistatore){
                val tempAvvLocali: List<AvvistamentiDaCaricare> by avvistamentiViewModel.all.collectAsState(initial = listOf())
                for(e in tempAvvLocali){
                    if(e.id == elem.id){
                        count = 0
                        sighting.image1 = e.immagine1
                        sighting.image2= e.immagine2
                        sighting.image3 = e.immagine3
                        sighting.image4 = e.immagine4
                        sighting.image5 = e.immagine5
                        if(sighting.image1 != "") {count+=1}
                        if(sighting.image2 != "") {count+=1}
                        if(sighting.image3 != "") {count+=1}
                        if(sighting.image4 != "") {count+=1}
                        if(sighting.image5 != "") {count+=1}
                        break
                    }
                }
            }
        }
    } else {
        if(em==elem.avvistatore){
            val tempAvvLocali: List<AvvistamentiDaCaricare> by avvistamentiViewModel.all.collectAsState(initial = listOf())
            for(e in tempAvvLocali){
                if(e.id == elem.id){
                    count = 0
                    sighting.image1 = e.immagine1
                    sighting.image2= e.immagine2
                    sighting.image3 = e.immagine3
                    sighting.image4 = e.immagine4
                    sighting.image5 = e.immagine5
                    if(sighting.image1 != "") {count+=1}
                    if(sighting.image2 != "") {count+=1}
                    if(sighting.image3 != "") {count+=1}
                    if(sighting.image4 != "") {count+=1}
                    if(sighting.image5 != "") {count+=1}
                    break
                }
            }
        } else {
            if(!info) {
                errorMessage = "Attenzione: essendo offline le immagini non possono essere caricate!"
                info = true
            }
        }
    }


    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            /** SightingViewScreen orizzontale */
            Column(modifier = Modifier.background(backGround)) {
                if (em == elem.avvistatore) {                       // Sono il proprietario
                    Row(
                        modifier = modifier
                            .fillMaxSize()
                            .background(backGround)
                    ) {
                        Column(
                            modifier = modifier
                                .width(configuration.screenWidthDp.dp / 2)
                                .padding(horizontal = hig),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
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
                                                override fun onPageFinished(
                                                    view: WebView?,
                                                    url: String?
                                                ) {
                                                    super.onPageFinished(view, url)
                                                    try {
                                                        var mkList = mutableListOf<MarkerData>()
                                                        mkList.add(
                                                            MarkerData(
                                                                elem.latid,
                                                                elem.long,
                                                                elem.data,
                                                                elem.animale,
                                                                elem.specie
                                                            )
                                                        )
                                                        val gson = Gson()
                                                        var markerDataJson = gson.toJson(mkList)
                                                        var currentMarkerDataJson = markerDataJson
                                                        try {
                                                            view?.evaluateJavascript(
                                                                "addMarkers('$currentMarkerDataJson')",
                                                                null
                                                            )
                                                        } catch (e: Exception) {
                                                        }
                                                    } catch (e: Exception) {

                                                    }
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.width(configuration.screenWidthDp.dp / 2)
                        ) {
                            items(1) { element ->
                                Row() {
                                    Column() {
                                        Text(
                                            text = "Utente:",
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = "Data:",
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                    }
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = utente,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = data,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                                // Numero Esemplari
                                OutlinedTextField(
                                    value = numeroEsemplari,
                                    onValueChange = { numeroEsemplari = it },
                                    label = { Text("Numero Esemplari") },
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Number
                                    ),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Row(
                                ) {
                                    OutlinedTextField(
                                        value = posizione,
                                        onValueChange = { posizione = it },
                                        label = { Text("Posizione") },
                                        keyboardOptions = KeyboardOptions.Default.copy(
                                            keyboardType = KeyboardType.Decimal
                                        ),
                                        singleLine = true,
                                        readOnly = true

                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                // Mare
                                OutlinedTextField(
                                    value = mare,
                                    onValueChange = { mare = it },
                                    label = { Text("Mare") },
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Number
                                    )
                                )
                                // Vento
                                OutlinedTextField(
                                    value = vento,
                                    onValueChange = { vento = it },
                                    label = { Text("Vento") },
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Number
                                    )
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                ExposedDropdownMenuBox(
                                    expanded = expanded,
                                    onExpandedChange = { expanded = !expanded },
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.primaryContainer)
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
                                                    selectedOptionTextSpecie = ""
                                                },
                                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                // Specie
                                Row {
                                    ExposedDropdownMenuBox(
                                        modifier = Modifier
                                            .width(245.dp)
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                            .border(
                                                1.dp,
                                                MaterialTheme.colorScheme.outline,
                                                RoundedCornerShape(2.dp)
                                            ),
                                        expanded = expandedSpecie && selectedOptionText != "",
                                        onExpandedChange = { expandedSpecie = !expandedSpecie },
                                    ) {
                                        TextField(
                                            // The `menuAnchor` modifier must be passed to the text field for correctness.
                                            modifier = Modifier.menuAnchor(),
                                            readOnly = true,
                                            value = selectedOptionTextSpecie,
                                            onValueChange = {},
                                            label = { Text("Specie") },
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(
                                                    expanded = expandedSpecie
                                                )
                                            },
                                            enabled = selectedOptionText != "",
                                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                        )
                                        ExposedDropdownMenu(
                                            expanded = expandedSpecie && selectedOptionText != "",
                                            onDismissRequest = { expanded = false },
                                        ) {
                                            getSpecieFromAniaml(animal = selectedOptionText).forEach { selectionOptionSpecie ->
                                                DropdownMenuItem(
                                                    text = { Text(selectionOptionSpecie.name) },
                                                    onClick = {
                                                        selectedOptionTextSpecie =
                                                            selectionOptionSpecie.name
                                                        expandedSpecie = false
                                                    },
                                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                                )
                                            }
                                        }
                                    }
                                    Button(
                                        modifier = Modifier
                                            .size(35.dp)
                                            .padding(0.dp)
                                            .align(Alignment.CenterVertically),
                                        colors = ButtonDefaults.buttonColors(
                                            MaterialTheme.colorScheme.secondaryContainer,
                                            MaterialTheme.colorScheme.primary
                                        ),
                                        contentPadding = PaddingValues(0.dp),
                                        enabled = selectedOptionText != "",
                                        onClick = {
                                            var entrato=false
                                            for(d in descrizioni){
                                                if (d.animale==selectedOptionText && d.specie==selectedOptionTextSpecie){
                                                    entrato=true
                                                    descMessage=d.descrizione
                                                    showFilterInfoSpecie = true;
                                                    break
                                                }
                                            }
                                            if(!entrato){
                                                descMessage="Descrizione non disponibile!"
                                                showFilterInfoSpecie = true;
                                            }
                                        }) {
                                        Icon(
                                            modifier = Modifier.fillMaxSize(),
                                            imageVector = Icons.Filled.Info,
                                            contentDescription = "Vedi dettagli specie"
                                        )
                                    }
                                }
                                // Note
                                OutlinedTextField(
                                    value = note,
                                    onValueChange = { note = it },
                                    label = { Text("Note") }
                                )

                                Row {
                                    Button(
                                        modifier = Modifier
                                            .padding(vertical = 16.dp),
                                        onClick = {
                                            if (!isNetworkAvailable(contex) && elem.online) {
                                                errorMessage =
                                                    "Impossibile aggiungere l'immagine all'avvistamento online: si prega di riprovare quando si è connessi alla rete!"
                                            } else {
                                                if (count <= 5) {
                                                    contex.requestCameraPermission(
                                                        currentDateTime,
                                                        count
                                                    )
                                                    count += 1
                                                    if(!alreadySeen){
                                                        imageMessage=true
                                                    }
                                                } else {
                                                    errorMessage = "Si prega di caricare al massimo 5 foto!"
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            MaterialTheme.colorScheme.primary
                                        ),
                                    ) {
                                        Icon(
                                            painterResource(id = R.drawable.baseline_camera_alt_24),
                                            contentDescription = "Foto"
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(text = "AGGIUNGI")
                                    }
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Button(
                                        modifier = Modifier
                                            .padding(vertical = 16.dp),
                                        onClick = {
                                            if (elem.online) {
                                                if (isNetworkAvailable(contex)) {
                                                    errorMessage = "Caricamento dati attendere..."
                                                    val client = OkHttpClient()
                                                    val formBody = MultipartBody.Builder()
                                                        .setType(MultipartBody.FORM)
                                                        .addFormDataPart("id", elem.id)
                                                        .addFormDataPart(
                                                            "specie",
                                                            selectedOptionText
                                                        )
                                                        .addFormDataPart(
                                                            "sottospecie",
                                                            selectedOptionTextSpecie
                                                        )
                                                        .addFormDataPart(
                                                            "esemplari",
                                                            numeroEsemplari
                                                        )
                                                        .addFormDataPart("vento", vento)
                                                        .addFormDataPart("mare", mare)
                                                        .addFormDataPart("note", note)
                                                        .addFormDataPart("latitudine", elem.latid)
                                                        .addFormDataPart("longitudine", elem.long)
                                                        .addFormDataPart("request", "saveDates")
                                                        .build()
                                                    val request = Request.Builder()
                                                        .url("https://isi-seawatch.csr.unibo.it/Sito/sito/templates/single_sighting/single_api.php")
                                                        .post(formBody)
                                                        .build()

                                                    client.newCall(request)
                                                        .enqueue(object : Callback {
                                                            override fun onFailure(
                                                                call: Call,
                                                                e: IOException
                                                            ) {
                                                            }

                                                            override fun onResponse(
                                                                call: Call,
                                                                response: Response
                                                            ) {
                                                                val body = response.body?.string()
                                                                if (body.toString() == "true") {
                                                                    if (isNetworkAvailable(contex)) {
                                                                        val client = OkHttpClient()
                                                                        val formBody =
                                                                            MultipartBody.Builder()
                                                                                .setType(
                                                                                    MultipartBody.FORM
                                                                                )
                                                                                .addFormDataPart(
                                                                                    "request",
                                                                                    "tbl_avvistamenti"
                                                                                )
                                                                                .build()
                                                                        val request =
                                                                            Request.Builder()
                                                                                .url("https://isi-seawatch.csr.unibo.it/Sito/sito/templates/main_sighting/sighting_api.php")
                                                                                .post(formBody)
                                                                                .build()

                                                                        client.newCall(request)
                                                                            .enqueue(object :
                                                                                Callback {
                                                                                override fun onFailure(
                                                                                    call: Call,
                                                                                    e: IOException
                                                                                ) {
                                                                                }

                                                                                override fun onResponse(
                                                                                    call: Call,
                                                                                    response: Response
                                                                                ) {
                                                                                    val body =
                                                                                        response.body?.string()
                                                                                    var temp =
                                                                                        JSONArray(
                                                                                            body
                                                                                        )

                                                                                    avvistamentiViewViewModel.deleteAll()
                                                                                    for (i in 0 until temp.length() step 1) {
                                                                                        avvistamentiViewViewModel.insert(
                                                                                            AvvistamentiDaVedere(
                                                                                                (temp.get(
                                                                                                    i
                                                                                                ) as JSONObject).get(
                                                                                                    "ID"
                                                                                                )
                                                                                                    .toString(),
                                                                                                (temp.get(
                                                                                                    i
                                                                                                ) as JSONObject).get(
                                                                                                    "Email"
                                                                                                )
                                                                                                    .toString(),
                                                                                                (temp.get(
                                                                                                    i
                                                                                                ) as JSONObject).get(
                                                                                                    "Data"
                                                                                                )
                                                                                                    .toString(),
                                                                                                (temp.get(
                                                                                                    i
                                                                                                ) as JSONObject).get(
                                                                                                    "Numero_Esemplari"
                                                                                                )
                                                                                                    .toString(),
                                                                                                (temp.get(
                                                                                                    i
                                                                                                ) as JSONObject).get(
                                                                                                    "Latid"
                                                                                                )
                                                                                                    .toString(),
                                                                                                (temp.get(
                                                                                                    i
                                                                                                ) as JSONObject).get(
                                                                                                    "Long"
                                                                                                )
                                                                                                    .toString(),
                                                                                                (temp.get(
                                                                                                    i
                                                                                                ) as JSONObject).get(
                                                                                                    "Anima_Nome"
                                                                                                )
                                                                                                    .toString(),
                                                                                                (temp.get(
                                                                                                    i
                                                                                                ) as JSONObject).get(
                                                                                                    "Specie_Nome"
                                                                                                )
                                                                                                    .toString(),
                                                                                                (temp.get(
                                                                                                    i
                                                                                                ) as JSONObject).get(
                                                                                                    "Mare"
                                                                                                )
                                                                                                    .toString(),
                                                                                                (temp.get(
                                                                                                    i
                                                                                                ) as JSONObject).get(
                                                                                                    "Vento"
                                                                                                )
                                                                                                    .toString(),
                                                                                                (temp.get(
                                                                                                    i
                                                                                                ) as JSONObject).get(
                                                                                                    "Note"
                                                                                                )
                                                                                                    .toString(),
                                                                                                (temp.get(
                                                                                                    i
                                                                                                ) as JSONObject).get(
                                                                                                    "Img"
                                                                                                )
                                                                                                    .toString(),
                                                                                                (temp.get(
                                                                                                    i
                                                                                                ) as JSONObject).get(
                                                                                                    "Nome"
                                                                                                )
                                                                                                    .toString(),
                                                                                                (temp.get(
                                                                                                    i
                                                                                                ) as JSONObject).get(
                                                                                                    "Cognome"
                                                                                                )
                                                                                                    .toString(),
                                                                                                true
                                                                                            )
                                                                                        )
                                                                                    }
                                                                                }
                                                                            })
                                                                    }
                                                                }
                                                            }
                                                        })

                                                    errorMessage = "Dati salvati con successo!"

                                                    for (image in imagesList) {
                                                        val bitmap: Bitmap? =
                                                            BitmapFactory.decodeStream(
                                                                contex.contentResolver.openInputStream(
                                                                    image
                                                                )
                                                            )
                                                        if (bitmap == null) {
                                                            errorMessage =
                                                                "Impossibile caricare le foto dalla memoria del sistema!"
                                                            break
                                                        }
                                                        val file =
                                                            File(contex.cacheDir, "image.jpg")
                                                        val outputStream = FileOutputStream(file)
                                                        bitmap.compress(
                                                            Bitmap.CompressFormat.JPEG,
                                                            100,
                                                            outputStream
                                                        )
                                                        outputStream.flush()
                                                        outputStream.close()
                                                        // restituisci l'URI del file temporaneo

                                                        val requestUrl =
                                                            "https://isi-seawatch.csr.unibo.it/Sito/sito/templates/single_sighting/single_api.php"
                                                        val requestBody =
                                                            MultipartBody.Builder()
                                                                .setType(MultipartBody.FORM)
                                                                .addFormDataPart(
                                                                    "id",
                                                                    elem.id
                                                                )
                                                                .addFormDataPart(
                                                                    "file",
                                                                    file.name,
                                                                    file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                                                                )
                                                                .addFormDataPart(
                                                                    "request",
                                                                    "addImage"
                                                                )
                                                                .build()

                                                        val request = Request.Builder()
                                                            .url(requestUrl)
                                                            .post(requestBody)
                                                            .build()

                                                        val client = OkHttpClient()

                                                        client.newCall(request)
                                                            .enqueue(object : Callback {
                                                                override fun onFailure(
                                                                    call: Call,
                                                                    e: IOException
                                                                ) {
                                                                    errorMessage =
                                                                        e.toString()
                                                                }

                                                                override fun onResponse(
                                                                    call: Call,
                                                                    response: Response
                                                                ) {
                                                                    val body =
                                                                        response.body?.string()

                                                                    if (JSONObject(body).get("state").toString() == "true"
                                                                    ) {
                                                                        errorMessage =
                                                                            "Immagini e dati caricati con successo!"
                                                                    } else {
                                                                        errorMessage =
                                                                            "Errore durante il caricamento delle immagini!"
                                                                    }
                                                                }
                                                            })
                                                    }
                                                    errorMessage = "Caricamento dati in corso attendere...!"
                                                    invioImgView(0, imagesList, contex, elem.id){
                                                        val client = OkHttpClient()
                                                        val formBody = MultipartBody.Builder()
                                                            .setType(MultipartBody.FORM)
                                                            .addFormDataPart("id", elem.id)
                                                            .addFormDataPart("request", "getImages")
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
                                                                val msg = body.toString()
                                                                immaginiOnline = msg
                                                                errorMessage = "Caricamento avvenuto con successo!"
                                                            }
                                                        })
                                                    }
                                                    currentDateTime=System.currentTimeMillis().toString()
                                                } else {
                                                    errorMessage =
                                                        "Errore di rete: impossibile aggiornare l'avvistamento online!"
                                                }
                                            } else {
                                                for( image in imagesList){
                                                    if(sighting.image1==""){
                                                        sighting.image1 = image.toString()
                                                    } else if(sighting.image2==""){
                                                        sighting.image2 = image.toString()
                                                    }else if(sighting.image3==""){
                                                        sighting.image3 = image.toString()
                                                    }else if(sighting.image4==""){
                                                        sighting.image4 = image.toString()
                                                    }else if(sighting.image5==""){
                                                        sighting.image5 = image.toString()
                                                    }
                                                }
                                                val a = AvvistamentiDaCaricare(
                                                    sighting.id,
                                                    sighting.user,
                                                    sighting.date,
                                                    sighting.numberOfSamples,
                                                    sighting.position,
                                                    sighting.animal,
                                                    sighting.specie,
                                                    sighting.sea,
                                                    sighting.wind,
                                                    sighting.notes,
                                                    sighting.image1,
                                                    sighting.image2,
                                                    sighting.image3,
                                                    sighting.image4,
                                                    sighting.image5,
                                                    false
                                                )
                                                avvistamentiViewModel.insert(a)
                                                errorMessage = "Dati salvati con successo"
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            MaterialTheme.colorScheme.primary
                                        ),

                                        ) {
                                        Icon(
                                            painterResource(id = R.drawable.baseline_save_24),
                                            contentDescription = "Salva"
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(text = "SALVA")
                                    }
                                }
                                Row(){
                                    Button(
                                        onClick = {
                                            errorMessage = "Caricamento in corso, attendere..."
                                            if (isNetworkAvailable(contex) && elem.online) {
                                                val client = OkHttpClient.Builder()
                                                    .connectTimeout(30, TimeUnit.SECONDS)
                                                    .readTimeout(30, TimeUnit.SECONDS)
                                                    .writeTimeout(30, TimeUnit.SECONDS)
                                                    .build()
                                                val formBody = MultipartBody.Builder()
                                                    .setType(MultipartBody.FORM)
                                                    .addFormDataPart("request", "recognition")
                                                    .addFormDataPart("id", elem.id)
                                                    .build()
                                                val request = Request.Builder()
                                                    .url("https://isi-seawatch.csr.unibo.it/Sito/sito/templates/single_sighting/single_api.php")
                                                    .post(formBody)
                                                    .build()

                                                client.newCall(request).enqueue(object : Callback {
                                                    override fun onFailure(call: Call, e: IOException) {
                                                        errorMessage = "Errore nella comunicazione col server"
                                                    }

                                                    override fun onResponse(call: Call, response: Response) {
                                                        val body = response.body?.string()
                                                        try{
                                                            var temp = JSONObject(body)
                                                            if(temp.get("state")==true){
                                                                var testo = "Dalle immagini, la visione artificiale ha riscontratto che si potrebbe trattare di: \n"
                                                                testo += removeDuplicates(temp.get("data").toString())
                                                                descMessage = testo
                                                                errorMessage=""


                                                                showFilterInfoSpecie=true
                                                                Log.e("KEYYY", descMessage)
                                                            } else {
                                                                errorMessage = "Non ci sono immagini relative all'avvistamento, caricarne almeno una."
                                                            }
                                                        }catch(e: Exception){

                                                        }
                                                    }
                                                })
                                            } else {
                                                errorMessage = "Nessuna connessione a internet, riprovare quando si è connessi."
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            MaterialTheme.colorScheme.primary
                                        ),
                                        enabled = elem.online &&  (selectedOptionText == "Delfino" || selectedOptionText == "Balena")
                                    ) {
                                        Icon(
                                            painterResource(id = R.drawable.baseline_search_24),
                                            contentDescription = "Autoriconoscimento"
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(text = "RICONOSCI")
                                    }
                                }
                                showImages(imagesUri = imagesList, context = contex)
                                Row(horizontalArrangement = Arrangement.Center) {
                                    if (elem.online) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            if (immaginiOnline != "") {
                                                val jsn = JSONArray(immaginiOnline)
                                                for (k in 0..jsn.length() - 1) {
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
                                                                Image(
                                                                    modifier = Modifier
                                                                        .fillMaxWidth()
                                                                        .height(200.dp),
                                                                    painter = rememberAsyncImagePainter(
                                                                        model = if (isNetworkAvailable(
                                                                                contex
                                                                            )
                                                                        ) {
                                                                            "https://isi-seawatch.csr.unibo.it/Sito/img/avvistamenti/" + (jsn.get(
                                                                                k
                                                                            ) as JSONObject).get("Img")
                                                                                .toString()
                                                                        } else {
                                                                            ""
                                                                        }
                                                                    ),
                                                                    contentDescription = null,
                                                                    contentScale = ContentScale.Crop
                                                                )
                                                                Row(horizontalArrangement = Arrangement.Center) {
                                                                    Button(
                                                                        modifier = Modifier
                                                                            .padding(10.dp),
                                                                        colors = ButtonDefaults.buttonColors(
                                                                            MaterialTheme.colorScheme.primary
                                                                        ),
                                                                        onClick = {
                                                                            try {
                                                                                val intent =
                                                                                    Intent(Intent.ACTION_VIEW)
                                                                                intent.setDataAndType(
                                                                                    Uri.parse(
                                                                                        "https://isi-seawatch.csr.unibo.it/Sito/img/avvistamenti/" + (jsn.get(
                                                                                            k
                                                                                        ) as JSONObject).get(
                                                                                            "Img"
                                                                                        ).toString()
                                                                                    ),
                                                                                    "image/*"
                                                                                )
                                                                                intent.addFlags(
                                                                                    Intent.FLAG_ACTIVITY_NEW_TASK
                                                                                )
                                                                                contex.startActivity(
                                                                                    intent
                                                                                )
                                                                            } catch (e: Exception) {
                                                                                errorMessage =
                                                                                    "Operazione non supportata dal dispositivo!"
                                                                            }
                                                                        }
                                                                    ) {
                                                                        Text("VISUALIZZA")
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            sighting=drawLocalImages(sighting.image1, contex, sighting, avvistamentiViewModel)
                                            sighting=drawLocalImages(sighting.image2, contex, sighting, avvistamentiViewModel)
                                            sighting=drawLocalImages(sighting.image3, contex, sighting, avvistamentiViewModel)
                                            sighting=drawLocalImages(sighting.image4, contex, sighting, avvistamentiViewModel)
                                            sighting=drawLocalImages(sighting.image5, contex, sighting, avvistamentiViewModel)
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = modifier
                            .fillMaxSize()
                            .background(backGround)
                    ) {
                        Column(
                            modifier = modifier
                                .width(configuration.screenWidthDp.dp / 2)
                                .padding(horizontal = hig),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
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
                                                override fun onPageFinished(
                                                    view: WebView?,
                                                    url: String?
                                                ) {
                                                    super.onPageFinished(view, url)
                                                    try {
                                                        var mkList = mutableListOf<MarkerData>()
                                                        mkList.add(
                                                            MarkerData(
                                                                elem.latid,
                                                                elem.long,
                                                                elem.data,
                                                                elem.animale,
                                                                elem.specie
                                                            )
                                                        )
                                                        val gson = Gson()
                                                        var markerDataJson = gson.toJson(mkList)
                                                        var currentMarkerDataJson = markerDataJson
                                                        view?.evaluateJavascript(
                                                            "addMarkers('$currentMarkerDataJson')",
                                                            null
                                                        )
                                                    } catch (e: Exception) {

                                                    }
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        LazyColumn(
                            modifier = Modifier.width(configuration.screenWidthDp.dp / 2)
                        ) {
                            items(1) { element ->
                                Card(
                                    shape = MaterialTheme.shapes.medium,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .fillMaxSize(),
                                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                                    elevation = CardDefaults.cardElevation(4.dp)
                                )
                                {
                                    Row(
                                        modifier = modifier
                                            .fillMaxSize()
                                            .background(MaterialTheme.colorScheme.secondaryContainer)
                                            .padding(10.dp)
                                    )
                                    {
                                        Column() {
                                            Text(
                                                text = "Utente:",
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = "Data:",
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = "N. Esemplari:",
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = "Posizione:",
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = "Animale:",
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = "Specie:",
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = "Mare:",
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = "Vento:",
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = "Note:",
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                        }
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = utente,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = data,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = numeroEsemplari,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = posizione,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = selectedOptionText,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Row() {
                                                Text(
                                                    text = selectedOptionTextSpecie,
                                                    style = MaterialTheme.typography.bodyLarge
                                                )
                                                Spacer(modifier = Modifier.width(5.dp))
                                                Button(
                                                    modifier = Modifier
                                                        .size(20.dp)
                                                        .padding(0.dp)
                                                        .align(Alignment.CenterVertically),
                                                    colors = ButtonDefaults.buttonColors(
                                                        MaterialTheme.colorScheme.secondaryContainer,
                                                        MaterialTheme.colorScheme.primary
                                                    ),
                                                    contentPadding = PaddingValues(0.dp),
                                                    onClick = {
                                                        var entrato=false
                                                        for(d in descrizioni){

                                                            if (d.animale==selectedOptionText && d.specie==selectedOptionTextSpecie){
                                                                entrato=true
                                                                descMessage=d.descrizione
                                                                showFilterInfoSpecie = true;
                                                                break
                                                            }
                                                        }
                                                        if(!entrato){
                                                            descMessage="Descrizione non disponibile!"
                                                            showFilterInfoSpecie = true;
                                                        }
                                                    }) {
                                                    Icon(
                                                        modifier = Modifier.fillMaxSize(),
                                                        imageVector = Icons.Filled.Info,
                                                        contentDescription = "Vedi dettagli specie"
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = mare,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = vento,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = note,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
                                    }
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    if (immaginiOnline != "") {
                                        val jsn = JSONArray(immaginiOnline)
                                        for (k in 0..jsn.length() - 1) {
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
                                                        Image(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .height(200.dp),
                                                            painter = rememberAsyncImagePainter(
                                                                model = if (isNetworkAvailable(
                                                                        contex
                                                                    )
                                                                ) {
                                                                    "https://isi-seawatch.csr.unibo.it/Sito/img/avvistamenti/" + (jsn.get(
                                                                        k
                                                                    ) as JSONObject).get("Img")
                                                                        .toString()
                                                                } else {
                                                                    ""
                                                                }
                                                            ),
                                                            contentDescription = null,
                                                            contentScale = ContentScale.Crop
                                                        )
                                                        Row(horizontalArrangement = Arrangement.Center) {
                                                            Button(
                                                                modifier = Modifier
                                                                    .padding(10.dp),
                                                                colors = ButtonDefaults.buttonColors(
                                                                    MaterialTheme.colorScheme.primary
                                                                ),
                                                                onClick = {
                                                                    try {
                                                                        val intent =
                                                                            Intent(Intent.ACTION_VIEW)
                                                                        intent.setDataAndType(
                                                                            Uri.parse(
                                                                                "https://isi-seawatch.csr.unibo.it/Sito/img/avvistamenti/" + (jsn.get(
                                                                                    k
                                                                                ) as JSONObject).get(
                                                                                    "Img"
                                                                                ).toString()
                                                                            ), "image/*"
                                                                        )
                                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                                        contex.startActivity(intent)
                                                                    } catch (e: Exception) {
                                                                        errorMessage =
                                                                            "Operazione non supportata dal dispositivo!"
                                                                    }
                                                                }
                                                            ) {
                                                                Text("VISUALIZZA")
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
            }
        } else -> {                                                           /** SightingViewScreen verticale */
            Column(modifier= Modifier
                .background(backGround)
                .fillMaxSize()) {
                if(elem.avvistatore==em){
                    Row(
                        modifier = modifier
                            .fillMaxSize()
                            .background(backGround)
                    ) {
                        LazyColumn(
                            modifier = modifier
                                .fillMaxSize()
                                .padding(horizontal = min / 2),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            items(1) { element ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize(),
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
                                                        try{
                                                            var mkList = mutableListOf<MarkerData>()
                                                            mkList.add(
                                                                MarkerData(
                                                                    elem.latid,
                                                                    elem.long,
                                                                    elem.data,
                                                                    elem.animale,
                                                                    elem.specie
                                                                )
                                                            )
                                                            val gson = Gson()
                                                            var markerDataJson = gson.toJson(mkList)
                                                            var currentMarkerDataJson = markerDataJson
                                                            try {
                                                                view?.evaluateJavascript("addMarkers('$currentMarkerDataJson')", null)
                                                            } catch (e: Exception) {
                                                            }
                                                        }catch (e: Exception){

                                                        }
                                                    }
                                                }
                                            }
                                        },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Row() {
                                    Column() {
                                        Text(
                                            text = "Utente:",
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = "Data:",
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                    }
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = utente,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = data,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                                // Numero Esemplari
                                OutlinedTextField(
                                    value = numeroEsemplari,
                                    onValueChange = { numeroEsemplari = it },
                                    label = { Text("Numero Esemplari") },
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Number
                                    ),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Row(
                                ) {
                                    OutlinedTextField(
                                        value = posizione,
                                        onValueChange = { posizione = it },
                                        label = { Text("Posizione") },
                                        keyboardOptions = KeyboardOptions.Default.copy(
                                            keyboardType = KeyboardType.Decimal
                                        ),
                                        singleLine = true
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                // Mare
                                OutlinedTextField(
                                    value = mare,
                                    onValueChange = { mare = it },
                                    label = { Text("Mare") },
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Number
                                    )
                                )
                                // Vento
                                OutlinedTextField(
                                    value = vento,
                                    onValueChange = { vento = it },
                                    label = { Text("Vento") },
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Number
                                    )
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                ExposedDropdownMenuBox(
                                    expanded = expanded,
                                    onExpandedChange = { expanded = !expanded },
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.primaryContainer)
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
                                                    selectedOptionTextSpecie=""
                                                },
                                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                // Specie
                                Row {
                                    ExposedDropdownMenuBox(
                                        modifier = Modifier
                                            .width(245.dp)
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                            .border(
                                                1.dp,
                                                MaterialTheme.colorScheme.outline,
                                                RoundedCornerShape(2.dp)
                                            ),
                                        expanded = expandedSpecie && selectedOptionText!="",
                                        onExpandedChange = { expandedSpecie = !expandedSpecie },
                                    ) {
                                        TextField(
                                            // The `menuAnchor` modifier must be passed to the text field for correctness.
                                            modifier = Modifier.menuAnchor(),
                                            readOnly = true,
                                            value = selectedOptionTextSpecie,
                                            onValueChange = {},
                                            label = { Text("Specie") },
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(
                                                    expanded = expandedSpecie
                                                )
                                            },
                                            enabled = selectedOptionText!="",
                                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                        )
                                        ExposedDropdownMenu(
                                            expanded = expandedSpecie && selectedOptionText!="",
                                            onDismissRequest = { expanded = false },
                                        ) {
                                            getSpecieFromAniaml(animal = selectedOptionText).forEach { selectionOptionSpecie ->
                                                DropdownMenuItem(
                                                    text = { Text(selectionOptionSpecie.name) },
                                                    onClick = {
                                                        selectedOptionTextSpecie = selectionOptionSpecie.name
                                                        expandedSpecie = false
                                                    },
                                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                                )
                                            }
                                        }
                                    }
                                    Button(
                                        modifier = Modifier
                                            .size(35.dp)
                                            .padding(0.dp)
                                            .align(Alignment.CenterVertically),
                                        colors = ButtonDefaults.buttonColors(
                                            MaterialTheme.colorScheme.secondaryContainer,
                                            MaterialTheme.colorScheme.primary
                                        ),
                                        contentPadding = PaddingValues(0.dp),
                                        enabled = selectedOptionText!="",
                                        onClick = {
                                            var entrato=false
                                            for(d in descrizioni){

                                                if (d.animale==selectedOptionText && d.specie==selectedOptionTextSpecie){
                                                    entrato=true
                                                    descMessage=d.descrizione
                                                    showFilterInfoSpecie = true;
                                                    break
                                                }
                                                if(!entrato){
                                                    descMessage="Descrizione non disponibile!"
                                                    showFilterInfoSpecie = true;
                                                }
                                            }
                                        }) {
                                        Icon(
                                            modifier = Modifier.fillMaxSize(),
                                            imageVector = Icons.Filled.Info,
                                            contentDescription = "Vedi dettagli specie"
                                        )
                                    }
                                }
                                // Note
                                OutlinedTextField(
                                    value = note,
                                    onValueChange = { note = it },
                                    label = { Text("Note") }
                                )

                                Row {
                                    Button(
                                        modifier = Modifier
                                            .padding(vertical = 16.dp),
                                        onClick = {
                                            if (!isNetworkAvailable(contex) && elem.online) {
                                                errorMessage =
                                                    "Impossibile aggiungere l'immagine all'avvistamento online: si prega di riprovare quando si è connessi alla rete!"
                                            } else {
                                                if (count <= 5) {
                                                    contex.requestCameraPermission(
                                                        currentDateTime,
                                                        count
                                                    )
                                                    count += 1
                                                    if(!alreadySeen){
                                                        imageMessage=true
                                                    }
                                                } else {
                                                    errorMessage = "Si prega di caricare al massimo 5 foto!"
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            MaterialTheme.colorScheme.primary
                                        ),
                                    ) {
                                        Icon(
                                            painterResource(id = R.drawable.baseline_camera_alt_24),
                                            contentDescription = "Foto"
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(text = "AGGIUNGI")
                                    }
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Button(
                                        modifier = Modifier
                                            .padding(vertical = 16.dp),
                                        onClick = {
                                            if (elem.online) {
                                                if (isNetworkAvailable(contex)) {
                                                    errorMessage = "Caricamento dati attendere..."
                                                    val client = OkHttpClient()
                                                    val formBody = MultipartBody.Builder()
                                                        .setType(MultipartBody.FORM)
                                                        .addFormDataPart("id", elem.id)
                                                        .addFormDataPart(
                                                            "specie",
                                                            selectedOptionText
                                                        )
                                                        .addFormDataPart(
                                                            "sottospecie",
                                                            selectedOptionTextSpecie
                                                        )
                                                        .addFormDataPart(
                                                            "esemplari",
                                                            numeroEsemplari
                                                        )
                                                        .addFormDataPart("vento", vento)
                                                        .addFormDataPart("mare", mare)
                                                        .addFormDataPart("note", note)
                                                        .addFormDataPart("latitudine", elem.latid)
                                                        .addFormDataPart("longitudine", elem.long)
                                                        .addFormDataPart("request", "saveDates")
                                                        .build()
                                                    val request = Request.Builder()
                                                        .url("https://isi-seawatch.csr.unibo.it/Sito/sito/templates/single_sighting/single_api.php")
                                                        .post(formBody)
                                                        .build()

                                                    client.newCall(request)
                                                        .enqueue(object : Callback {
                                                            override fun onFailure(
                                                                call: Call,
                                                                e: IOException
                                                            ) {
                                                            }

                                                            override fun onResponse(
                                                                call: Call,
                                                                response: Response
                                                            ) {
                                                                val body = response.body?.string()
                                                                if (body.toString() == "true") {
                                                                    if (isNetworkAvailable(contex)) {
                                                                        val client = OkHttpClient()
                                                                        val formBody =
                                                                            MultipartBody.Builder()
                                                                                .setType(
                                                                                    MultipartBody.FORM
                                                                                )
                                                                                .addFormDataPart(
                                                                                    "request",
                                                                                    "tbl_avvistamenti"
                                                                                )
                                                                                .build()
                                                                        val request =
                                                                            Request.Builder()
                                                                                .url("https://isi-seawatch.csr.unibo.it/Sito/sito/templates/main_sighting/sighting_api.php")
                                                                                .post(formBody)
                                                                                .build()

                                                                        client.newCall(request)
                                                                            .enqueue(object :
                                                                                Callback {
                                                                                override fun onFailure(
                                                                                    call: Call,
                                                                                    e: IOException
                                                                                ) {
                                                                                }

                                                                                override fun onResponse(
                                                                                    call: Call,
                                                                                    response: Response
                                                                                ) {
                                                                                    val body =
                                                                                        response.body?.string()
                                                                                    var temp =
                                                                                        JSONArray(
                                                                                            body
                                                                                        )

                                                                                    avvistamentiViewViewModel.deleteAll()
                                                                                    for (i in 0 until temp.length() step 1) {
                                                                                        avvistamentiViewViewModel.insert(
                                                                                            AvvistamentiDaVedere(
                                                                                                (temp.get(
                                                                                                    i
                                                                                                ) as JSONObject).get(
                                                                                                    "ID"
                                                                                                )
                                                                                                    .toString(),
                                                                                                (temp.get(
                                                                                                    i
                                                                                                ) as JSONObject).get(
                                                                                                    "Email"
                                                                                                )
                                                                                                    .toString(),
                                                                                                (temp.get(
                                                                                                    i
                                                                                                ) as JSONObject).get(
                                                                                                    "Data"
                                                                                                )
                                                                                                    .toString(),
                                                                                                (temp.get(
                                                                                                    i
                                                                                                ) as JSONObject).get(
                                                                                                    "Numero_Esemplari"
                                                                                                )
                                                                                                    .toString(),
                                                                                                (temp.get(
                                                                                                    i
                                                                                                ) as JSONObject).get(
                                                                                                    "Latid"
                                                                                                )
                                                                                                    .toString(),
                                                                                                (temp.get(
                                                                                                    i
                                                                                                ) as JSONObject).get(
                                                                                                    "Long"
                                                                                                )
                                                                                                    .toString(),
                                                                                                (temp.get(
                                                                                                    i
                                                                                                ) as JSONObject).get(
                                                                                                    "Anima_Nome"
                                                                                                )
                                                                                                    .toString(),
                                                                                                (temp.get(
                                                                                                    i
                                                                                                ) as JSONObject).get(
                                                                                                    "Specie_Nome"
                                                                                                )
                                                                                                    .toString(),
                                                                                                (temp.get(
                                                                                                    i
                                                                                                ) as JSONObject).get(
                                                                                                    "Mare"
                                                                                                )
                                                                                                    .toString(),
                                                                                                (temp.get(
                                                                                                    i
                                                                                                ) as JSONObject).get(
                                                                                                    "Vento"
                                                                                                )
                                                                                                    .toString(),
                                                                                                (temp.get(
                                                                                                    i
                                                                                                ) as JSONObject).get(
                                                                                                    "Note"
                                                                                                )
                                                                                                    .toString(),
                                                                                                (temp.get(
                                                                                                    i
                                                                                                ) as JSONObject).get(
                                                                                                    "Img"
                                                                                                )
                                                                                                    .toString(),
                                                                                                (temp.get(
                                                                                                    i
                                                                                                ) as JSONObject).get(
                                                                                                    "Nome"
                                                                                                )
                                                                                                    .toString(),
                                                                                                (temp.get(
                                                                                                    i
                                                                                                ) as JSONObject).get(
                                                                                                    "Cognome"
                                                                                                )
                                                                                                    .toString(),
                                                                                                true
                                                                                            )
                                                                                        )
                                                                                    }
                                                                                }
                                                                            })
                                                                    }
                                                                }
                                                            }
                                                        })
                                                    errorMessage = "Caricamento dati in corso attendere...!"
                                                    invioImgView(0, imagesList, contex, elem.id){
                                                        val client = OkHttpClient()
                                                        val formBody = MultipartBody.Builder()
                                                            .setType(MultipartBody.FORM)
                                                            .addFormDataPart("id", elem.id)
                                                            .addFormDataPart("request", "getImages")
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
                                                                val msg = body.toString()
                                                                immaginiOnline = msg
                                                                errorMessage = "Caricamento avvenuto con successo!"
                                                            }
                                                        })
                                                    }
                                                    currentDateTime=System.currentTimeMillis().toString()
                                                } else {
                                                    errorMessage =
                                                        "Errore di rete: impossibile aggiornare l'avvistamento online!"
                                                }
                                            } else {
                                                for( image in imagesList){
                                                    if(sighting.image1==""){
                                                        sighting.image1 = image.toString()
                                                    } else if(sighting.image2==""){
                                                        sighting.image2 = image.toString()
                                                    }else if(sighting.image3==""){
                                                        sighting.image3 = image.toString()
                                                    }else if(sighting.image4==""){
                                                        sighting.image4 = image.toString()
                                                    }else if(sighting.image5==""){
                                                        sighting.image5 = image.toString()
                                                    }
                                                }
                                                val a = AvvistamentiDaCaricare(
                                                    sighting.id,
                                                    sighting.user,
                                                    sighting.date,
                                                    sighting.numberOfSamples,
                                                    sighting.position,
                                                    sighting.animal,
                                                    sighting.specie,
                                                    sighting.sea,
                                                    sighting.wind,
                                                    sighting.notes,
                                                    sighting.image1,
                                                    sighting.image2,
                                                    sighting.image3,
                                                    sighting.image4,
                                                    sighting.image5,
                                                    false
                                                )
                                                avvistamentiViewModel.insert(a)
                                                errorMessage = "Dati salvati con successo"
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            MaterialTheme.colorScheme.primary
                                        ),

                                        ) {
                                        Icon(
                                            painterResource(id = R.drawable.baseline_save_24),
                                            contentDescription = "Salva"
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(text = "SALVA")
                                    }
                                }
                                Row(){
                                    Button(
                                        onClick = {
                                            errorMessage = "Caricamento in corso, attendere..."
                                            if (isNetworkAvailable(contex) && elem.online) {
                                                val client = OkHttpClient.Builder()
                                                    .connectTimeout(30, TimeUnit.SECONDS)
                                                    .readTimeout(30, TimeUnit.SECONDS)
                                                    .writeTimeout(30, TimeUnit.SECONDS)
                                                    .build()
                                                val formBody = MultipartBody.Builder()
                                                    .setType(MultipartBody.FORM)
                                                    .addFormDataPart("request", "recognition")
                                                    .addFormDataPart("id", elem.id)
                                                    .build()
                                                val request = Request.Builder()
                                                    .url("https://isi-seawatch.csr.unibo.it/Sito/sito/templates/single_sighting/single_api.php")
                                                    .post(formBody)
                                                    .build()

                                                client.newCall(request).enqueue(object : Callback {
                                                    override fun onFailure(call: Call, e: IOException) {
                                                        errorMessage = "Errore nella comunicazione col server"
                                                    }

                                                    override fun onResponse(call: Call, response: Response) {
                                                        val body = response.body?.string()
                                                        try{
                                                            var temp = JSONObject(body)
                                                            if(temp.get("state")==true){
                                                                var testo = "Dalle immagini, la visione artificiale ha riscontratto che si potrebbe trattare di: \n"
                                                                testo += removeDuplicates(temp.get("data").toString())
                                                                descMessage = testo
                                                                errorMessage=""


                                                                showFilterInfoSpecie=true
                                                                Log.e("KEYYY", descMessage)
                                                            } else {
                                                                errorMessage = "Non ci sono immagini relative all'avvistamento, caricarne almeno una."
                                                            }
                                                        }catch(e: Exception){

                                                        }
                                                    }
                                                })
                                            } else {
                                                errorMessage = "Nessuna connessione a internet, riprovare quando si è connessi."
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            MaterialTheme.colorScheme.primary
                                        ),
                                        enabled = elem.online &&  (selectedOptionText == "Delfino" || selectedOptionText == "Balena")
                                    ) {
                                        Icon(
                                            painterResource(id = R.drawable.baseline_search_24),
                                            contentDescription = "Autoriconoscimento"
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(text = "RICONOSCI")
                                    }
                                }
                                showImages(imagesUri = imagesList, context = contex)
                                Row(horizontalArrangement = Arrangement.Center) {
                                    if (elem.online) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            if (immaginiOnline != "") {
                                                val jsn = JSONArray(immaginiOnline)
                                                for (k in 0..jsn.length() - 1) {
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
                                                                Image(
                                                                    modifier = Modifier
                                                                        .fillMaxWidth()
                                                                        .height(200.dp),
                                                                    painter = rememberAsyncImagePainter(
                                                                        model = if (isNetworkAvailable(
                                                                                contex
                                                                            )
                                                                        ) {
                                                                            "https://isi-seawatch.csr.unibo.it/Sito/img/avvistamenti/" + (jsn.get(
                                                                                k
                                                                            ) as JSONObject).get("Img")
                                                                                .toString()
                                                                        } else {
                                                                            ""
                                                                        }
                                                                    ),
                                                                    contentDescription = null,
                                                                    contentScale = ContentScale.Crop
                                                                )
                                                                Row(horizontalArrangement = Arrangement.Center) {
                                                                    Button(
                                                                        modifier = Modifier
                                                                            .padding(10.dp),
                                                                        colors = ButtonDefaults.buttonColors(
                                                                            MaterialTheme.colorScheme.primary
                                                                        ),
                                                                        onClick = {
                                                                            try {
                                                                                val intent =
                                                                                    Intent(Intent.ACTION_VIEW)
                                                                                intent.setDataAndType(
                                                                                    Uri.parse(
                                                                                        "https://isi-seawatch.csr.unibo.it/Sito/img/avvistamenti/" + (jsn.get(
                                                                                            k
                                                                                        ) as JSONObject).get(
                                                                                            "Img"
                                                                                        ).toString()
                                                                                    ),
                                                                                    "image/*"
                                                                                )
                                                                                intent.addFlags(
                                                                                    Intent.FLAG_ACTIVITY_NEW_TASK
                                                                                )
                                                                                contex.startActivity(
                                                                                    intent
                                                                                )
                                                                            } catch (e: Exception) {
                                                                                errorMessage =
                                                                                    "Operazione non supportata dal dispositivo!"
                                                                            }
                                                                        }
                                                                    ) {
                                                                        Text("VISUALIZZA")
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            sighting=drawLocalImages(sighting.image1, contex, sighting, avvistamentiViewModel)
                                            sighting=drawLocalImages(sighting.image2, contex, sighting, avvistamentiViewModel)
                                            sighting=drawLocalImages(sighting.image3, contex, sighting, avvistamentiViewModel)
                                            sighting=drawLocalImages(sighting.image4, contex, sighting, avvistamentiViewModel)
                                            sighting=drawLocalImages(sighting.image5, contex, sighting, avvistamentiViewModel)

                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = modifier
                            .fillMaxSize()
                            .background(backGround)
                    ) {
                        LazyColumn(
                            modifier = modifier
                                .fillMaxSize()
                                .padding(horizontal = min / 2),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            items(1) { element ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize(),
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
                                                        if(elem.latid!="" && elem.long!=""){
                                                            var mkList = mutableListOf<MarkerData>()
                                                            mkList.add(
                                                                MarkerData(
                                                                    elem.latid,
                                                                    elem.long,
                                                                    elem.data,
                                                                    elem.animale,
                                                                    elem.specie
                                                                )
                                                            )
                                                            val gson = Gson()
                                                            var markerDataJson = gson.toJson(mkList)
                                                            var currentMarkerDataJson = markerDataJson
                                                            try {
                                                                view?.evaluateJavascript("addMarkers('$currentMarkerDataJson')", null)
                                                            } catch (e: Exception) {
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Card(
                                    shape = MaterialTheme.shapes.medium,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .fillMaxSize(),
                                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                                    elevation = CardDefaults.cardElevation(4.dp)
                                )
                                {
                                    Row(
                                        modifier = modifier
                                            .fillMaxSize()
                                            .background(MaterialTheme.colorScheme.secondaryContainer)
                                            .padding(10.dp)
                                    )
                                    {
                                        Column() {
                                            Text(
                                                text = "Utente:",
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = "Data:",
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = "N. Esemplari:",
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = "Posizione:",
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = "Animale:",
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = "Specie:",
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = "Mare:",
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = "Vento:",
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = "Note:",
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                        }
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = utente,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = data,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = numeroEsemplari,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = posizione,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = selectedOptionText,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Row() {
                                                Text(
                                                    text = selectedOptionTextSpecie,
                                                    style = MaterialTheme.typography.bodyLarge
                                                )
                                                Spacer(modifier = Modifier.width(5.dp))
                                                Button(
                                                    modifier = Modifier
                                                        .size(20.dp)
                                                        .padding(0.dp)
                                                        .align(Alignment.CenterVertically),
                                                    colors = ButtonDefaults.buttonColors(
                                                        MaterialTheme.colorScheme.secondaryContainer,
                                                        MaterialTheme.colorScheme.primary
                                                    ),
                                                    contentPadding = PaddingValues(0.dp),
                                                    onClick = {
                                                        var entrato=false
                                                        for(d in descrizioni){

                                                            if (d.animale==selectedOptionText && d.specie==selectedOptionTextSpecie){
                                                                entrato=true
                                                                descMessage=d.descrizione
                                                                showFilterInfoSpecie = true;
                                                                break
                                                            }
                                                        }
                                                        if(!entrato){
                                                            descMessage="Descrizione non disponibile!"
                                                            showFilterInfoSpecie = true;
                                                        }
                                                    }) {
                                                    Icon(
                                                        modifier = Modifier.fillMaxSize(),
                                                        imageVector = Icons.Filled.Info,
                                                        contentDescription = "Vedi dettagli specie"
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = mare,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = vento,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = note,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
                                    }
                                }
                                Column (horizontalAlignment = Alignment.CenterHorizontally){
                                    if(immaginiOnline!=""){
                                        val jsn = JSONArray(immaginiOnline)
                                        for(k in 0..jsn.length()-1) {
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
                                                        Image(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .height(200.dp),
                                                            painter = rememberAsyncImagePainter(
                                                                model = if(isNetworkAvailable(contex)){"https://isi-seawatch.csr.unibo.it/Sito/img/avvistamenti/" + (jsn.get(k) as JSONObject).get("Img").toString()} else {""}
                                                            ),
                                                            contentDescription = null,
                                                            contentScale = ContentScale.Crop
                                                        )
                                                        Row(horizontalArrangement = Arrangement.Center){
                                                            Button(
                                                                modifier = Modifier
                                                                    .padding(10.dp),
                                                                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                                                                onClick = {
                                                                    try{
                                                                        val intent = Intent(Intent.ACTION_VIEW)
                                                                        intent.setDataAndType(Uri.parse("https://isi-seawatch.csr.unibo.it/Sito/img/avvistamenti/" + (jsn.get(k) as JSONObject).get("Img").toString()), "image/*")
                                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                                        contex.startActivity(intent)
                                                                    }catch (e:Exception){
                                                                        errorMessage = "Operazione non supportata dal dispositivo!"
                                                                    }
                                                                }
                                                            ) {
                                                                Text("VISUALIZZA")
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
            }
        }
    }
}

fun invioImgView(ind: Int, elem:List<Uri>, context: Context, id: String, update: () -> Unit){
    if (ind == elem.size){
        update()
        return
    }
    val list = elem
    val img = list[ind]
    val id = id
    val image = img
    if(img!= Uri.EMPTY) {
        try {
            val bitmap: Bitmap =
                BitmapFactory.decodeStream(
                    context.contentResolver.openInputStream(
                        image
                    )
                )
            if(bitmap!=null) {
                val file =
                    File(context.cacheDir, "image.jpg")
                val outputStream = FileOutputStream(file)
                bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    100,
                    outputStream
                )
                outputStream.flush()
                outputStream.close()

                // restituisci l'URI del file temporaneo
                if (file != null) {
                    val requestUrl =
                        "https://isi-seawatch.csr.unibo.it/Sito/sito/templates/single_sighting/single_api.php"
                    val requestBody =
                        MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("id", id)
                            .addFormDataPart(
                                "file",
                                file.name,
                                file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                            )
                            .addFormDataPart(
                                "request",
                                "addImage"
                            )
                            .build()

                    val request = Request.Builder()
                        .url(requestUrl)
                        .post(requestBody)
                        .build()

                    val client = OkHttpClient()

                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            invioImgView(ind, elem, context, id, update)
                        }

                        override fun onResponse(
                            call: Call,
                            response: Response
                        ) {
                            invioImgView(ind+1, elem, context, id, update)
                        }
                    })
                } else {
                    Log.e("KEYYY", "Errore: un'immagine è null!")
                }
            }
        }catch(e: Exception){
            Log.e("KEYYY", "Eccezione immagini!")
            invioImgView(ind+1, elem, context, id, update)
        }
    } else {
        invioImgView(ind+1, elem, context, id, update)
    }
}

fun removeDuplicates(str: String): String {
    val names = str
        .replace("[", "")
        .replace("]", "")
        .replace("\"", "")
        .split(",")
        .map { it.trim() }
        .distinct()
        .filter { it.isNotEmpty() }
        .map { it.replace("_", " ").replace("-", " ") }
    return names.joinToString("\n")
}

