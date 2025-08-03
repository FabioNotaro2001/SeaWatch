package com.example.seawatch.screens


import androidx.compose.foundation.BorderStroke

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.R
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.seawatch.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotifyScreen(
    avvistamentiViewViewModel: AvvistamentiViewViewModel,
    goToSighting: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val min = configuration.screenHeightDp.dp / 40
    val backGround = MaterialTheme.colorScheme.primaryContainer
    val context = LocalContext.current
    var errorMessage by rememberSaveable { mutableStateOf("") }
    var elems by remember { mutableStateOf(JSONArray()) }

    if(!isNetworkAvailable(context)){
        errorMessage = "Errore nella connessione non è possibile carcare le notifiche!"
    }

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
                    elems = JSONArray(msg.toString())
                } catch (e: Exception) {
                }
            }
        })
    }

    if(elems.length()==0){
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(backGround),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(1) { element ->
                Text(
                    text = "NESSUNA NOTIFICA",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    } else {
        Row(
            modifier = modifier
                .fillMaxSize()
                .background(backGround)
        ) {
            Row(modifier=Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center) {
                LazyColumn(modifier = modifier.padding(10.dp),horizontalAlignment = Alignment.CenterHorizontally) {
                    items(1) { element ->
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = {
                                    if (isNetworkAvailable(context) && em != "") {
                                        val client = OkHttpClient()
                                        val formBody = MultipartBody.Builder()
                                            .setType(MultipartBody.FORM)
                                            .addFormDataPart("user", em)
                                            .addFormDataPart("request", "deleteNotify")
                                            .build()
                                        val request = Request.Builder()
                                            .url("https://isi-seawatch.csr.unibo.it/Sito/sito/templates/single_sighting/single_api.php")
                                            .post(formBody)
                                            .build()

                                        client.newCall(request).enqueue(object : Callback {
                                            override fun onFailure(call: Call, e: IOException) {
                                            }

                                            override fun onResponse(call: Call, response: Response) {
                                                try {
                                                    elems = JSONArray()
                                                } catch (e: Exception) {
                                                }
                                            }
                                        })
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
                                modifier = modifier.widthIn(min = 150.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Elimina notifiche",
                                )
                                Text("NOTIFICHE")
                            }
                            Spacer(modifier = Modifier.height(min))
                        }
                        for (i in 0 until elems.length() step 1) {
                            val e = elems.get(i) as JSONObject
                            Card(
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier
                                    .padding(3.dp),
                                border = BorderStroke(1.dp, Color.Black),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                                elevation = CardDefaults.cardElevation(4.dp),
                                onClick = {
                                    var coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
                                    coroutineScope.launch {
                                        val avvistamenti = runBlocking { avvistamentiViewViewModel.all.first() }
                                        for (elem in avvistamenti){
                                            if(elem.id==e.get("Avvistamento").toString()){
                                                sightingID = elem
                                                goToSighting()
                                            }
                                        }

                                    }
                                }
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        Text(
                                            text = e.get("Mittente").toString(),
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Text(
                                            text = e.get("Data").toString()
                                                .substring(0..15),
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = e.get("Testo").toString(),
                                        style = MaterialTheme.typography.bodyMedium
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