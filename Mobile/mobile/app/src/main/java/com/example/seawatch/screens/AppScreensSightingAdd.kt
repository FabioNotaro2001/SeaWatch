package com.example.seawatch

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.seawatch.data.*
import okhttp3.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SightingScreen(
    avvistamentiViewModel: AvvistamentiViewModel,
    avvistamentiViewViewModel: AvvistamentiViewViewModel,
    goToHome:()->Unit,
    modifier: Modifier = Modifier,
    descriptionViewModel: DescriptionViewModel,
    placesViewModel: PlacesViewModel,
    startLocationUpdates: () -> Unit
    ) {
    var posizione by rememberSaveable { placesViewModel.placeFromGPS }
    val configuration = LocalConfiguration.current
    val hig = configuration.screenHeightDp.dp/10
    val backGround = MaterialTheme.colorScheme.primaryContainer
    var utente by rememberSaveable { mutableStateOf(em) }
    val currentDateTimeClock = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.ITALIAN)
    val formattedDateTime = currentDateTimeClock.format(formatter)
    var data by rememberSaveable { mutableStateOf(formattedDateTime) }
    var numeroEsemplari by rememberSaveable { mutableStateOf("1") }
    var mare by rememberSaveable { mutableStateOf("") }
    var vento by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    val options by rememberSaveable { mutableStateOf(getAnimal()) }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedOptionText by rememberSaveable { mutableStateOf("") }
    var expandedSpecie by rememberSaveable { mutableStateOf(false) }
    var selectedOptionTextSpecie by rememberSaveable { mutableStateOf("") }
    var showFilterInfoSpecie by rememberSaveable { mutableStateOf(false) }
    val contex = LocalContext.current
    val currentDateTime by rememberSaveable {mutableStateOf( System.currentTimeMillis().toString())}
    var count by rememberSaveable {mutableStateOf(0)}
    var imagesList =(contex as MainActivity).getAllSavedImages(currentDateTime.toString())
    var showConfirmDialog by rememberSaveable { mutableStateOf(false) }
    val sighting = Sighting(currentDateTime, em, data, numeroEsemplari, posizione, selectedOptionText, selectedOptionTextSpecie, mare, vento, note)
    var errorMessage by rememberSaveable { mutableStateOf("") }
    var descriptionMessage by rememberSaveable { mutableStateOf("") }
    var imageMessage by rememberSaveable { mutableStateOf(false) }
    var alreadySeen by rememberSaveable { mutableStateOf(false) }
    val tempAvvLocali: List<AvvistamentiDaCaricare> by avvistamentiViewModel.all.collectAsState(initial = listOf())
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
                                    text = descriptionMessage,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Spacer(modifier = Modifier.height(2.dp))
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
                    TextButton(onClick = {showFilterInfoSpecie=false}) {
                        Text("Chiudi")
                    }
                }
            }
        )
    }

    if (errorMessage.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { errorMessage = "" },
            title = { Text(text = "AGGIORNAMENTO") },
            text = { Text(text = errorMessage) },
            confirmButton = {
                Button(onClick = { errorMessage = "" }) {
                    Text(text = "Ok")
                }
            }
        )
    }

    if (imageMessage) {
        AlertDialog(
            onDismissRequest = { imageMessage = false; alreadySeen=true },
            title = { Text(text = "ATTENZIONE") },
            text = { Text(text = "Le immagini che aggiungi saranno salvate anche sul tuo dispositivo dopo aver premuto il tasto Salva!") },
            confirmButton = {
                Button(onClick = { imageMessage=false;alreadySeen=true }) {
                    Text(text = "Ok")
                }
            }
        )
    }

    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            val snackbarHostState = remember { SnackbarHostState() }
            Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) },
                floatingActionButton = {
                FloatingActionButton(
                    shape= RoundedCornerShape(50.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        if(numeroEsemplari!="" && !imagesList.isEmpty() && posizione=="") {
                            for (image in imagesList) {
                                if (sighting.image1 == "") {
                                    sighting.image1 = image.toString()
                                } else if (sighting.image2 == "") {
                                    sighting.image2 = image.toString()
                                } else if (sighting.image3 == "") {
                                    sighting.image3 = image.toString()
                                } else if (sighting.image4 == "") {
                                    sighting.image4 = image.toString()
                                } else if (sighting.image5 == "") {
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
                            showConfirmDialog = true
                            uploadToServer(
                                context = contex,
                                tempAvvLocali = tempAvvLocali + listOf(a),
                                avvistamentiViewViewModel = avvistamentiViewViewModel,
                                avvistamentiViewModel = avvistamentiViewModel
                            )
                        } else if (numeroEsemplari=="") {
                            errorMessage = "Inserire il numero degli esemplari!"
                        } else if (imagesList.isEmpty()){
                            errorMessage="Si prega di inserire almeno un'immagine per l'avvistamento!"
                        } else if (posizione==""){
                            errorMessage="E' obbligatorio inserire anche la posizione dell'avvistamento! Si prega di inserirla!"
                        }
                    },
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                ) {
                    Icon(imageVector = Icons.Filled.Check, "Conferma aggiunta avvistamento")
                }
            }, floatingActionButtonPosition = FabPosition.End){paddingValues->
                Column(modifier= Modifier
                    .background(backGround)
                    .padding(paddingValues)) {
                    Card(
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxSize(),
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            LazyColumn(
                                modifier = modifier
                                    .fillMaxSize()
                                    .padding(horizontal = hig, vertical = 3.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                items(1) { element ->
                                    Row() {
                                        Column(
                                            modifier = Modifier.width(configuration.screenWidthDp.dp / 2)
                                        ) {
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Row() {
                                                Column() {
                                                    Text(
                                                        text = "Utente:  ",
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
                                                    onValueChange = { newText ->
                                                        posizione = newText
                                                    },
                                                    label = { Text("Posizione") },
                                                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                                                    singleLine = true,
                                                    trailingIcon = {
                                                        IconButton(onClick = {
                                                            startLocationUpdates()
                                                        }) {
                                                            Icon(
                                                                painter = painterResource(id = R.drawable.baseline_gps_fixed_24),
                                                                contentDescription = "GPS",
                                                                tint = Color.Black
                                                            )
                                                        }
                                                    },
                                                    readOnly = true
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(3.dp))
                                            // Mare
                                            OutlinedTextField(
                                                value = mare,
                                                onValueChange = { mare = it },
                                                label = { Text("Mare") },
                                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                                            )
                                            // Vento
                                            OutlinedTextField(
                                                value = vento,
                                                onValueChange = { vento = it },
                                                label = { Text("Vento") },
                                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                                            )
                                        }
                                        Column(modifier = Modifier.width(configuration.screenWidthDp.dp / 2)) {
                                            Spacer(modifier = Modifier.height(12.dp))
                                            ExposedDropdownMenuBox(
                                                expanded = expanded,
                                                onExpandedChange = { expanded = !expanded },
                                                modifier = Modifier
                                                    .background(MaterialTheme.colorScheme.secondaryContainer)
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
                                                                selectedOptionTextSpecie = ""
                                                                expanded = false
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
                                                        .background(MaterialTheme.colorScheme.secondaryContainer)
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
                                                        onDismissRequest = { expandedSpecie = false },
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
                                                                descriptionMessage=d.descrizione
                                                                showFilterInfoSpecie = true;
                                                                break
                                                            }
                                                            if(!entrato){
                                                                descriptionMessage="Descrizione non disponbile!"
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

                                            Button(
                                                modifier = Modifier
                                                    .padding(vertical = 16.dp)
                                                    .align(Alignment.CenterHorizontally),
                                                onClick = {
                                                    contex.requestCameraPermission(currentDateTime.toString(), count)
                                                    count+=1
                                                    if(!alreadySeen){
                                                        imageMessage=true
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                                            ) {
                                                Icon(painterResource(id =R.drawable.baseline_camera_alt_24), contentDescription = "Foto")
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(text = "AGGIUNGI")
                                            }

                                        }
                                    }
                                    Row(horizontalArrangement = Arrangement.Center){
                                        showImages(imagesUri = imagesList, contex)
                                    }
                                    if (showConfirmDialog) {
                                        AlertDialog(
                                            onDismissRequest = { showConfirmDialog = false; goToHome() },
                                            title = { Text("AVVISO") },
                                            text = {
                                                Text(text="Avvistamento caricato in maniera corretta! Se non si è connessi ad una rete il caricamento sarà caricato automaticamente online appena possibile!")
                                            },
                                            confirmButton = {
                                                TextButton(onClick = { showConfirmDialog = false; goToHome() }) {
                                                    Text("CHIUDI")
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        else -> {
            val snackbarHostState = remember { SnackbarHostState() }
            Scaffold(floatingActionButton = {
                FloatingActionButton(
                    shape= RoundedCornerShape(50.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        if(numeroEsemplari!="" && !imagesList.isEmpty() && posizione!="") {
                            for (image in imagesList) {
                                if (sighting.image1 == "") {
                                    sighting.image1 = image.toString()
                                } else if (sighting.image2 == "") {
                                    sighting.image2 = image.toString()
                                } else if (sighting.image3 == "") {
                                    sighting.image3 = image.toString()
                                } else if (sighting.image4 == "") {
                                    sighting.image4 = image.toString()
                                } else if (sighting.image5 == "") {
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
                            showConfirmDialog = true
                            uploadToServer(
                                context = contex,
                                tempAvvLocali = tempAvvLocali + listOf(a),
                                avvistamentiViewViewModel = avvistamentiViewViewModel,
                                avvistamentiViewModel = avvistamentiViewModel
                            )
                        } else if(numeroEsemplari=="") {
                            errorMessage = "Inserire il numero degli esemplari!"
                        } else if (imagesList.isEmpty()){
                            errorMessage="Si prega di inserire almeno un'immagine per l'avvistamento!"
                        } else if (posizione==""){
                            errorMessage="E' obbligatorio inserire anche la posizione dell'avvistamento! Si prega di inserirla!"
                        }
                    },
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                ) {
                    Icon(imageVector = Icons.Filled.Check, "Conferma aggiunta avvistamento")
                }
            }, floatingActionButtonPosition = FabPosition.End){paddingValues->
                LazyColumn(modifier= Modifier
                    .background(backGround)
                    .padding(paddingValues)
                    .fillMaxSize()){
                    items(1) { element ->
                        Card(
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxSize(),
                            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            /** Login verticale */
                            Column(
                                modifier = modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row() {
                                    Column() {
                                        Text(
                                            text = "Utente:  ",
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
                                        Text(text = data, style = MaterialTheme.typography.bodyLarge)
                                    }
                                }
                                if (contex.showSnackBar.value) {
                                    SnackBarComposable(snackbarHostState, contex, contex.showSnackBar)
                                }
                                if (contex.showAlertDialog.value) {
                                    AlertDialogComposable(contex, contex.showAlertDialog)
                                }
                                // Numero Esemplari
                                OutlinedTextField(
                                    value = numeroEsemplari,
                                    onValueChange = { numeroEsemplari = it },
                                    label = { Text("Numero Esemplari") },
                                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = posizione,
                                        onValueChange = { newText ->
                                            posizione = newText
                                        },
                                        label = { Text("Posizione") },
                                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                                        singleLine = true,
                                        trailingIcon = {
                                            IconButton(onClick = {
                                               startLocationUpdates()
                                            }) {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.baseline_gps_fixed_24),
                                                    contentDescription = "GPS",
                                                    tint = Color.Black
                                                )
                                            }
                                        },
                                        readOnly = true
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                //Animali
                                ExposedDropdownMenuBox(
                                    expanded = expanded,
                                    onExpandedChange = { expanded = !expanded },
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.secondaryContainer)
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
                                                    selectedOptionTextSpecie = ""
                                                    expanded = false
                                                },
                                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                // Specie
                                Row {
                                    ExposedDropdownMenuBox(
                                        modifier = Modifier
                                            .width(245.dp)
                                            .background(MaterialTheme.colorScheme.secondaryContainer)
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
                                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                            enabled = selectedOptionText!=""
                                        )
                                        ExposedDropdownMenu(
                                            expanded = expandedSpecie && selectedOptionText!="",
                                            onDismissRequest = { expandedSpecie = false },
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
                                                if (d.animale.lowercase()==selectedOptionText.lowercase() && d.specie.lowercase()==selectedOptionTextSpecie.lowercase()){
                                                    entrato=true
                                                    descriptionMessage=d.descrizione
                                                    showFilterInfoSpecie = true;
                                                    break
                                                }
                                            }
                                            if(!entrato){
                                                descriptionMessage="Descrizione non disponbile!"
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
                                Spacer(modifier = Modifier.height(3.dp))
                                // Mare
                                OutlinedTextField(
                                    value = mare,
                                    onValueChange = { mare = it },
                                    label = { Text("Mare") },
                                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                                )
                                // Vento
                                OutlinedTextField(
                                    value = vento,
                                    onValueChange = { vento = it },
                                    label = { Text("Vento") },
                                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                                )

                                // Note
                                OutlinedTextField(
                                    value = note,
                                    onValueChange = { note = it },
                                    label = { Text("Note") }
                                )

                                Button(
                                    modifier = Modifier.padding(vertical = 16.dp),
                                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                                    onClick = {
                                        if(imagesList.count()<5){
                                            contex.requestCameraPermission(currentDateTime, count)
                                            count+=1
                                        } else {
                                            errorMessage = "Non è possibile caricare più di 5 foto!"
                                        }
                                    }
                                ) {
                                    Icon(painterResource(id =R.drawable.baseline_camera_alt_24), contentDescription = "Foto")
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(text = "AGGIUNGI")
                                }
                                showImages(imagesUri = imagesList, contex)
                                if (showConfirmDialog) {
                                    AlertDialog(
                                        onDismissRequest = { showConfirmDialog = false; goToHome() },
                                        title = { Text("AVVISO") },
                                        text = {
                                            Text(text="Avvistamento caricato in maniera corretta! Se non si è connessi ad una rete il caricamento sarà caricato automaticamente online appena possibile!")
                                        },
                                        confirmButton = {
                                            TextButton(onClick = { showConfirmDialog = false; goToHome() }) {
                                                Text("CHIUDI")
                                            }
                                        }
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

@Composable
internal fun AlertDialogComposable(
    applicationContext: Context,
    showAlertDialog: MutableState<Boolean>
) {
    AlertDialog(
        onDismissRequest = {
            showAlertDialog.value = false
        },
        title = {
            Text(text = "ERRORE GPS")
        },
        text = {
            Text(text = "Il GPS è disabilitato ma è necessario per fornire la propria posizione! Si prega di attivarlo!")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    if (intent.resolveActivity(applicationContext.packageManager) != null) {
                        applicationContext.startActivity(intent)
                    }
                    showAlertDialog.value = false
                }
            ) {
                Text("GPS attivato!")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { showAlertDialog.value = false  }
            ) {
                Text("Annulla")
            }
        }
    )
}

@Composable
internal fun SnackBarComposable(
    snackbarHostState: SnackbarHostState,
    applicationContext: Context,
    showSnackBar: MutableState<Boolean>
) {
    LaunchedEffect(snackbarHostState) {
        val result = snackbarHostState.showSnackbar(
            message = "E' necessario accordare i permessi per fornire la propria posizione!",
            actionLabel = "Vai alle impostazioni"
        )
        when (result) {
            SnackbarResult.ActionPerformed -> {
                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", applicationContext.packageName, null)
                }
                if (intent.resolveActivity(applicationContext.packageManager) != null) {
                    applicationContext.startActivity(intent)
                }
            }
            SnackbarResult.Dismissed -> {
                showSnackBar.value = false
            }
        }
    }
}