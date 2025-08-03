package com.example.seawatch.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.seawatch.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

fun convertDateFormat(dateString: String): String {
    val inputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    val date = inputFormat.parse(dateString)
    val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return outputFormat.format(date)
}

fun inviaRichieste(index: Int, tempAvvLocali: List<AvvistamentiDaCaricare> , context: Context, avvistamentiViewViewModel: AvvistamentiViewViewModel,avvistamentiViewModel: AvvistamentiViewModel ) {
    if (index >= tempAvvLocali.size) {
        avvistamentiViewModel.deleteAll()
        if (isNetworkAvailable(context)) {
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

            client.newCall(request).enqueue(object :
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
            return
        }
    }
    val elem = tempAvvLocali[index]
    val client = OkHttpClient()
    val formBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("idd", elem.id)
        .addFormDataPart("user", elem.avvistatore)
        .addFormDataPart("data", convertDateFormat(elem.data))
        .addFormDataPart("specie", elem.animale)
        .addFormDataPart("sottospecie", elem.specie)
        .addFormDataPart("esemplari", elem.numeroEsemplari)
        .addFormDataPart("vento", elem.vento)
        .addFormDataPart("mare", elem.mare)
        .addFormDataPart("note", elem.note)
        .addFormDataPart(
            "latitudine", try {
                elem.posizione.split(" ")[0]
            } catch (e: Exception) {
                ""
            }
        )
        .addFormDataPart(
            "longitudine", try {
                elem.posizione.split(" ")[1]
            } catch (e: Exception) {
                ""
            }
        )
        .addFormDataPart("request", "saveAvvMob")
        .build()
    val request = Request.Builder()
        .url("https://isi-seawatch.csr.unibo.it/Sito/sito/templates/main_sighting/sighting_api.php")
        .post(formBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(
            call: Call,
            e: IOException
        ) {
            Log.e("KEYYY", "Errore caricamento avvistamento!")
            // passa alla successiva
            inviaRichieste(
                index + 1,
                tempAvvLocali,
                context,
                avvistamentiViewViewModel,
                avvistamentiViewModel
            )
        }

        override fun onResponse(
            call: Call,
            response: Response
        ) {
            val body = response.body?.string()
            Log.e("KEYYY", body.toString())
            if (body.toString() == "true") {
                if (isNetworkAvailable(context)) {
                    invioImg(0, elem, context)
                }
            }
            // passa alla successiva
            inviaRichieste(
                index + 1,
                tempAvvLocali,
                context,
                avvistamentiViewViewModel,
                avvistamentiViewModel
            )
        }
    })
}


fun uploadToServer(context: Context, tempAvvLocali: List<AvvistamentiDaCaricare> , avvistamentiViewViewModel: AvvistamentiViewViewModel, avvistamentiViewModel: AvvistamentiViewModel) {
    if (isNetworkAvailable(context)) {
        inviaRichieste(0, tempAvvLocali, context, avvistamentiViewViewModel, avvistamentiViewModel)
    }
}

fun invioImg(ind: Int, elem:AvvistamentiDaCaricare, context: Context){
    if (ind == 5){
        return
    }
    val list = listOf(elem.immagine1, elem.immagine2, elem.immagine3, elem.immagine4, elem.immagine5)
    val img = list[ind]
    val id = elem.id
    val image = Uri.parse(img)
    if(img!="") {
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
                bitmap?.compress(
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
                            invioImg(ind, elem, context)
                        }

                        override fun onResponse(
                            call: Call,
                            response: Response
                        ) {
                            invioImg(ind+1, elem, context)
                        }
                    })
                } else {
                    Log.e("KEYYY", "Errore: un'immagine è null!")
                }
            }
        }catch(e: Exception){
            Log.e("KEYYY", "Eccezione immagini!")
            invioImg(ind+1, elem, context)
        }
    } else {
        invioImg(ind+1, elem, context)
    }
}
