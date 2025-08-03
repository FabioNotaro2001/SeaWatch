package com.example.seawatch

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.seawatch.data.Favourite
import com.example.seawatch.data.FavouriteViewModel
import com.example.seawatch.data.User
import com.example.seawatch.data.UserViewModel
import okhttp3.*
import org.bouncycastle.crypto.digests.SHA512Digest
import org.bouncycastle.crypto.macs.HMac
import org.bouncycastle.crypto.params.KeyParameter
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*



var ok = true
var em=""

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    goToHome: () -> Unit,
    goToSignUp:() ->Unit,
    modifier: Modifier = Modifier,
    sharedPrefForLogin: SharedPreferences,
    userViewModel: UserViewModel,
) {
    val configuration = LocalConfiguration.current
    val min = configuration.screenHeightDp.dp/40
    val med = configuration.screenHeightDp.dp/20
    val hig = configuration.screenHeightDp.dp/10
    val backGround = MaterialTheme.colorScheme.primaryContainer
    var mail by rememberSaveable { mutableStateOf(sharedPrefForLogin.getString("USER", "").toString()) }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordHidden by rememberSaveable { mutableStateOf(true) }
    val biometricManager = BiometricManager.from(LocalContext.current)
    val executor = ContextCompat.getMainExecutor(LocalContext.current)
    val context = LocalContext.current as FragmentActivity
    var errorMessage by rememberSaveable { mutableStateOf("") }

    val userItems: List<User> by userViewModel.all.collectAsState(initial = listOf())

    if(sharedPrefForLogin.getString("USER", "")!="" && ok ){
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                val promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle("LOGIN CON IMPRONTA DIGITALE")
                    .setSubtitle("Utilizza l'impronta digitale per accedere")
                    .setDescription("Posiziona il dito sull'impronta digitale")
                    .setNegativeButtonText("Annulla")
                    .build()

                val biometricPrompt = BiometricPrompt(context, executor, object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        goToHome()
                        ok = false
                        em=mail
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        ok = false
                    }
                })
                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
            }
        }
    }

    if (errorMessage.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = {errorMessage=""},
            title = { Text("ERRORE") },
            text = {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(20.dp))
            },
            confirmButton = {
                TextButton(onClick = {errorMessage=""}) {
                    Text("Chiudi")
                }
            }
        )
    }

    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {                   /** Login orizzontale */
            Row (
                modifier = modifier
                    .fillMaxSize()
                    .background(backGround)
            ) {
                LazyColumn(
                    modifier = modifier
                        .fillMaxHeight()
                        .padding(horizontal = hig),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    items(1) { element ->
                        //Spacer(modifier = Modifier.height(hig))
                        Image(
                            painter = painterResource(R.drawable.sea),
                            contentDescription = "Immagine Logo"
                        )
                        Spacer(modifier = Modifier.height(min))
                        Text("ACCEDI", style = MaterialTheme.typography.titleLarge)
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
                        TextField(
                            value = mail,
                            onValueChange = { mail = it },
                            label = { Text("Email") },
                            singleLine = true,
                            placeholder = { Text("esempio@provider.com") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                            modifier = Modifier.background(backGround),
                            colors = TextFieldDefaults.outlinedTextFieldColors()
                        )
                        Spacer(modifier = Modifier.height(min))

                        TextField(
                            value = password,
                            onValueChange = { password = it },
                            singleLine = true,
                            label = { Text("Password") },
                            keyboardActions = KeyboardActions(onDone = {
                                if(isNetworkAvailable(context)){
                                    val client = OkHttpClient()
                                    val formBody = MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart("request", "email")
                                        .addFormDataPart("email", mail)
                                        .build()
                                    val request = Request.Builder()
                                        .url("https://isi-seawatch.csr.unibo.it/Sito/sito/templates/main_login/login_api.php")
                                        .post(formBody)
                                        .build()

                                    client.newCall(request).enqueue(object : Callback {
                                        override fun onFailure(call: Call, e: IOException) {
                                            errorMessage = "Impossibile comunicare col server!"
                                        }


                                        override fun onResponse(call: Call, response: Response) {
                                            val bodyKey = response.body?.string()
                                            val jsonObjectKey = JSONObject(bodyKey.toString())
                                            if(jsonObjectKey.getString("state")=="true"){
                                                val pwdC = calculateHmacSha512(password, jsonObjectKey.getString("Key"))
                                                val client = OkHttpClient()
                                                val formBody = MultipartBody.Builder()
                                                    .setType(MultipartBody.FORM)
                                                    .addFormDataPart("request", "ApwdMob")
                                                    .addFormDataPart("email", mail)
                                                    .addFormDataPart("password", pwdC)
                                                    .build()
                                                val request = Request.Builder()
                                                    .url("https://isi-seawatch.csr.unibo.it/Sito/sito/templates/main_login/login_api.php")
                                                    .post(formBody)
                                                    .build()

                                                client.newCall(request).enqueue(object : Callback {
                                                    override fun onFailure(call: Call, e: IOException) {
                                                        errorMessage = "Impossibile comunicare col server!"
                                                    }

                                                    override fun onResponse(call: Call, response: Response) {
                                                        val body = response.body?.string()
                                                        val jsonObject = JSONObject(body.toString())
                                                        if(jsonObject.getString("state")=="true"){
                                                            with(sharedPrefForLogin.edit()){
                                                                putString("USER", mail)
                                                                apply()
                                                            }
                                                            ok = false
                                                            em=mail
                                                            val pers = JSONArray(jsonObject.get("val").toString()).get(0) as JSONObject
                                                            userViewModel.insert(User(mail, pers.get("Nome").toString(), pers.get("Cognome").toString(), calculateHmacSha512(password, pers.get("Key").toString()) , pers.get("Key").toString()))
                                                            (context as MainActivity).runOnUiThread {
                                                                goToHome()
                                                            }
                                                        } else {
                                                            errorMessage =  jsonObject.getString("msg")
                                                        }
                                                    }
                                                })
                                            } else {
                                                errorMessage =  jsonObjectKey.getString("msg")
                                            }
                                        }
                                    })
                                } else {
                                    var check = false
                                    for (elem in userItems){
                                        if(elem.mail==mail){
                                            check=true
                                            if(elem.password == calculateHmacSha512(password, elem.sale)){
                                                with(sharedPrefForLogin.edit()){
                                                    putString("USER", mail)
                                                    apply()
                                                }
                                                ok = false
                                                em=mail
                                                (context as MainActivity).runOnUiThread {
                                                    goToHome()
                                                }
                                            } else {
                                                errorMessage = "Password errata!"
                                            }
                                            break
                                        }
                                    }
                                    if(!check){
                                        errorMessage = "Impossibile accedere: nessuna connessione ad internet disponibile oppure utente non presente! Si prega di eseguire il primo accesso in presenza di rete!"
                                    }
                                }
                            }),
                            visualTransformation =
                            if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(onClick = { passwordHidden = !passwordHidden }) {
                                    if (passwordHidden) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_visibility_24),
                                            contentDescription = "Visibile"
                                        )
                                    } else {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_visibility_off_24),
                                            contentDescription = "Non visibile"
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.background(backGround),
                            colors = TextFieldDefaults.outlinedTextFieldColors()
                        )
                    }
                }

                LazyColumn(
                    modifier = modifier
                        .fillMaxHeight()
                        .padding(horizontal = hig)
                        .background(backGround),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    items(1) { element ->
                        Button(
                            onClick = {
                                if(isNetworkAvailable(context)){
                                    val client = OkHttpClient()
                                    val formBody = MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart("request", "email")
                                        .addFormDataPart("email", mail)
                                        .build()
                                    val request = Request.Builder()
                                        .url("https://isi-seawatch.csr.unibo.it/Sito/sito/templates/main_login/login_api.php")
                                        .post(formBody)
                                        .build()

                                    client.newCall(request).enqueue(object : Callback {
                                        override fun onFailure(call: Call, e: IOException) {
                                            errorMessage = "Impossibile comunicare col server!"
                                        }


                                        override fun onResponse(call: Call, response: Response) {
                                            val bodyKey = response.body?.string()
                                            val jsonObjectKey = JSONObject(bodyKey.toString())
                                            if(jsonObjectKey.getString("state")=="true"){
                                                val pwdC = calculateHmacSha512(password, jsonObjectKey.getString("Key"))
                                                val client = OkHttpClient()
                                                val formBody = MultipartBody.Builder()
                                                    .setType(MultipartBody.FORM)
                                                    .addFormDataPart("request", "ApwdMob")
                                                    .addFormDataPart("email", mail)
                                                    .addFormDataPart("password", pwdC)
                                                    .build()
                                                val request = Request.Builder()
                                                    .url("https://isi-seawatch.csr.unibo.it/Sito/sito/templates/main_login/login_api.php")
                                                    .post(formBody)
                                                    .build()

                                                client.newCall(request).enqueue(object : Callback {
                                                    override fun onFailure(call: Call, e: IOException) {
                                                        errorMessage = "Impossibile comunicare col server!"
                                                    }

                                                    override fun onResponse(call: Call, response: Response) {
                                                        val body = response.body?.string()
                                                        val jsonObject = JSONObject(body.toString())
                                                        if(jsonObject.getString("state")=="true"){
                                                            with(sharedPrefForLogin.edit()){
                                                                putString("USER", mail)
                                                                apply()
                                                            }
                                                            ok = false
                                                            em=mail
                                                            val pers = JSONArray(jsonObject.get("val").toString()).get(0) as JSONObject
                                                            userViewModel.insert(User(mail, pers.get("Nome").toString(), pers.get("Cognome").toString(), calculateHmacSha512(password, pers.get("Key").toString()) , pers.get("Key").toString()))
                                                            (context as MainActivity).runOnUiThread {
                                                                goToHome()
                                                            }
                                                        } else {
                                                            errorMessage =  jsonObject.getString("msg")
                                                        }
                                                    }
                                                })
                                            } else {
                                                errorMessage =  jsonObjectKey.getString("msg")
                                            }
                                        }
                                    })
                                } else {
                                    var check = false
                                    for (elem in userItems){
                                        if(elem.mail==mail){
                                            check=true
                                            if(elem.password == calculateHmacSha512(password, elem.sale)){
                                                with(sharedPrefForLogin.edit()){
                                                    putString("USER", mail)
                                                    apply()
                                                }
                                                ok = false
                                                em=mail
                                                (context as MainActivity).runOnUiThread {
                                                    goToHome()
                                                }
                                            } else {
                                                errorMessage = "Password errata!"
                                            }
                                            break
                                        }
                                    }
                                    if(!check){
                                        errorMessage = "Impossibile accedere: nessuna connessione ad internet disponibile oppure utente non presente! Si prega di eseguire il primo accesso in presenza di rete!"
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onPrimaryContainer),
                            modifier = modifier.widthIn(min = 200.dp)
                        ) {
                            Text("ENTRA")
                        }
                        Spacer(modifier = Modifier.height(min))
                        Button(
                            onClick = { goToSignUp() },
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                            modifier = modifier.widthIn(min = 150.dp)
                        ) {
                            Text("CREA ACCOUNT")
                        }
                    }
                }
            }
        }
        else -> {                                                   /** Login verticale */
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(backGround),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(1) { element ->
                    Spacer(modifier = Modifier.height(hig))
                    Image(
                        painter = painterResource(R.drawable.sea),
                        contentDescription = "Immagine Logo"
                    )
                    Spacer(modifier = Modifier.height(med))
                    Text("ACCEDI", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(min))
                    TextField(
                        value = mail,
                        onValueChange = { mail = it },
                        label = { Text("Email") },
                        singleLine = true,
                        placeholder = { Text("esempio@provider.com") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                        modifier = Modifier.background(backGround),
                        colors = TextFieldDefaults.outlinedTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(min))
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        singleLine = true,
                        label = { Text("Password") },
                        visualTransformation =
                        if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        keyboardActions = KeyboardActions(onDone = {
                            if(isNetworkAvailable(context)){
                                val client = OkHttpClient()
                                val formBody = MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("request", "email")
                                    .addFormDataPart("email", mail)
                                    .build()
                                val request = Request.Builder()
                                    .url("https://isi-seawatch.csr.unibo.it/Sito/sito/templates/main_login/login_api.php")
                                    .post(formBody)
                                    .build()

                                client.newCall(request).enqueue(object : Callback {
                                    override fun onFailure(call: Call, e: IOException) {
                                        errorMessage = "Impossibile comunicare col server!"
                                    }


                                    override fun onResponse(call: Call, response: Response) {
                                        val bodyKey = response.body?.string()
                                        val jsonObjectKey = JSONObject(bodyKey.toString())
                                        if(jsonObjectKey.getString("state")=="true"){
                                            val pwdC = calculateHmacSha512(password, jsonObjectKey.getString("Key"))
                                            val client = OkHttpClient()
                                            val formBody = MultipartBody.Builder()
                                                .setType(MultipartBody.FORM)
                                                .addFormDataPart("request", "ApwdMob")
                                                .addFormDataPart("email", mail)
                                                .addFormDataPart("password", pwdC)
                                                .build()
                                            val request = Request.Builder()
                                                .url("https://isi-seawatch.csr.unibo.it/Sito/sito/templates/main_login/login_api.php")
                                                .post(formBody)
                                                .build()

                                            client.newCall(request).enqueue(object : Callback {
                                                override fun onFailure(call: Call, e: IOException) {
                                                    errorMessage = "Impossibile comunicare col server!"
                                                }

                                                override fun onResponse(call: Call, response: Response) {
                                                    val body = response.body?.string()
                                                    val jsonObject = JSONObject(body.toString())
                                                    if(jsonObject.getString("state")=="true"){
                                                        with(sharedPrefForLogin.edit()){
                                                            putString("USER", mail)
                                                            apply()
                                                        }
                                                        ok = false
                                                        em=mail
                                                        val pers = JSONArray(jsonObject.get("val").toString()).get(0) as JSONObject
                                                        userViewModel.insert(User(mail, pers.get("Nome").toString(), pers.get("Cognome").toString(), calculateHmacSha512(password, pers.get("Key").toString()) , pers.get("Key").toString()))
                                                        (context as MainActivity).runOnUiThread {
                                                            goToHome()
                                                        }
                                                    } else {
                                                        errorMessage =  jsonObject.getString("msg")
                                                    }
                                                }
                                            })
                                        } else {
                                            errorMessage =  jsonObjectKey.getString("msg")
                                        }
                                    }
                                })
                            } else {
                                var check = false
                                for (elem in userItems){
                                    if(elem.mail==mail){
                                        check=true
                                        if(elem.password == calculateHmacSha512(password, elem.sale)){
                                            with(sharedPrefForLogin.edit()){
                                                putString("USER", mail)
                                                apply()
                                            }
                                            ok = false
                                            em=mail
                                            (context as MainActivity).runOnUiThread {
                                                goToHome()
                                            }
                                        } else {
                                            errorMessage = "Password errata!"
                                        }
                                        break
                                    }
                                }
                                if(!check){
                                    errorMessage = "Impossibile accedere: nessuna connessione ad internet disponibile oppure utente non presente! Si prega di eseguire il primo accesso in presenza di rete!"
                                }
                            }
                        }),
                        trailingIcon = {
                            IconButton(onClick = { passwordHidden = !passwordHidden }) {
                                if (passwordHidden) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_visibility_24),
                                        contentDescription = "Visibile"
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_visibility_off_24),
                                        contentDescription = "Non visibile"
                                    )
                                }
                            }
                        },
                        modifier = Modifier.background(backGround),
                        colors = TextFieldDefaults.outlinedTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(med))
                    Button(
                        onClick = {
                            if(isNetworkAvailable(context)){
                                val client = OkHttpClient()
                                val formBody = MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("request", "email")
                                    .addFormDataPart("email", mail)
                                    .build()
                                val request = Request.Builder()
                                    .url("https://isi-seawatch.csr.unibo.it/Sito/sito/templates/main_login/login_api.php")
                                    .post(formBody)
                                    .build()

                                client.newCall(request).enqueue(object : Callback {
                                    override fun onFailure(call: Call, e: IOException) {
                                        errorMessage = "Impossibile comunicare col server!"
                                    }


                                    override fun onResponse(call: Call, response: Response) {
                                        val bodyKey = response.body?.string()
                                        val jsonObjectKey = JSONObject(bodyKey.toString())
                                        if(jsonObjectKey.getString("state")=="true"){
                                            val pwdC = calculateHmacSha512(password, jsonObjectKey.getString("Key"))
                                            val client = OkHttpClient()
                                            val formBody = MultipartBody.Builder()
                                                .setType(MultipartBody.FORM)
                                                .addFormDataPart("request", "ApwdMob")
                                                .addFormDataPart("email", mail)
                                                .addFormDataPart("password", pwdC)
                                                .build()
                                            val request = Request.Builder()
                                                .url("https://isi-seawatch.csr.unibo.it/Sito/sito/templates/main_login/login_api.php")
                                                .post(formBody)
                                                .build()

                                            client.newCall(request).enqueue(object : Callback {
                                                override fun onFailure(call: Call, e: IOException) {
                                                    errorMessage = "Impossibile comunicare col server!"
                                                }

                                                override fun onResponse(call: Call, response: Response) {
                                                    val body = response.body?.string()
                                                    val jsonObject = JSONObject(body.toString())
                                                    if(jsonObject.getString("state")=="true"){
                                                        with(sharedPrefForLogin.edit()){
                                                            putString("USER", mail)
                                                            apply()
                                                        }
                                                        ok = false
                                                        em=mail
                                                        val pers = JSONArray(jsonObject.get("val").toString()).get(0) as JSONObject
                                                        userViewModel.insert(User(mail, pers.get("Nome").toString(), pers.get("Cognome").toString(), calculateHmacSha512(password, pers.get("Key").toString()) , pers.get("Key").toString()))
                                                        (context as MainActivity).runOnUiThread {
                                                            goToHome()
                                                        }
                                                    } else {
                                                        errorMessage =  jsonObject.getString("msg")
                                                    }
                                                }
                                            })
                                        } else {
                                            errorMessage =  jsonObjectKey.getString("msg")
                                        }
                                    }
                                })
                            } else {
                                var check = false
                                for (elem in userItems) {
                                    if (elem.mail == mail) {
                                        check = true
                                        if (elem.password == calculateHmacSha512(
                                                password,
                                                elem.sale
                                            )
                                        ) {
                                            with(sharedPrefForLogin.edit()) {
                                                putString("USER", mail)
                                                apply()
                                            }
                                            ok = false
                                            em = mail
                                            (context as MainActivity).runOnUiThread {
                                                goToHome()
                                            }
                                        } else {
                                            errorMessage = "Password errata!"
                                        }
                                        break
                                    }
                                }
                                if (!check) {
                                    errorMessage =
                                        "Impossibile accedere: nessuna connessione ad internet disponibile oppure utente non presente! Si prega di eseguire il primo accesso in presenza di rete!"
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onPrimaryContainer),
                        modifier = modifier.widthIn(min = 230.dp)
                    ) {
                        Text("ENTRA")
                    }
                    Spacer(modifier = Modifier.height(min))
                    Button(
                        onClick = { goToSignUp() },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                        modifier = modifier.widthIn(min = 180.dp)
                    ) {
                        Text("CREA ACCOUNT")
                    }
                }
            }
        }
    }
}

fun calculateSHA1Hash(): String {
    val messageDigest = MessageDigest.getInstance("SHA-1")
    val digest = messageDigest.digest(System.currentTimeMillis().toString().toByteArray(StandardCharsets.UTF_8))
    return digest.fold("", { str, it -> str + "%02x".format(it) })
}

fun calculateHmacSha512(password: String, key: String): String {
    val hmac = HMac(SHA512Digest())
    val keyBytes = key.toByteArray(StandardCharsets.UTF_8)
    hmac.init(KeyParameter(keyBytes))
    hmac.update(password.toByteArray(StandardCharsets.UTF_8), 0, password.length)
    val result = ByteArray(hmac.macSize)
    hmac.doFinal(result, 0)
    return result.joinToString("") { "%02x".format(it) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    goToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val min = configuration.screenHeightDp.dp/40
    val med = configuration.screenHeightDp.dp/20
    val hig = configuration.screenHeightDp.dp/10
    val backGround = MaterialTheme.colorScheme.primaryContainer
    var mail by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var surname by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val key by rememberSaveable { mutableStateOf(calculateSHA1Hash()) }
    var passwordHidden by rememberSaveable { mutableStateOf(true) }
    val context = LocalContext.current as FragmentActivity
    var errorMessage by rememberSaveable { mutableStateOf("") }

    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {                    /** SignUp Orizzontale */
            Row (
                modifier = modifier
                    .fillMaxSize()
                    .background(backGround)
            ) {
                LazyColumn(
                    modifier = modifier
                        .fillMaxHeight()
                        .padding(horizontal = hig),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    items(1) { element ->
                        //Spacer(modifier = Modifier.height(hig))
                        Image(
                            painter = painterResource(R.drawable.sea),
                            contentDescription = "Immagine Logo"
                        )
                        Spacer(modifier = Modifier.height(med))
                        Text(text = "REGISTRATI", style = MaterialTheme.typography.titleLarge)
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
                        TextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nome") },
                            singleLine = true,
                            placeholder = { Text("Mario") },
                            modifier = Modifier.background(backGround),
                            colors = TextFieldDefaults.outlinedTextFieldColors()
                        )
                        Spacer(modifier = Modifier.height(min))
                        TextField(
                            value = surname,
                            onValueChange = { surname = it },
                            label = { Text("Cognome") },
                            singleLine = true,
                            placeholder = { Text("Rossi") },
                            modifier = Modifier.background(backGround),
                            colors = TextFieldDefaults.outlinedTextFieldColors()
                        )
                        Spacer(modifier = Modifier.height(min))
                        TextField(
                            value = mail,
                            onValueChange = { mail = it },
                            label = { Text("Email") },
                            singleLine = true,
                            placeholder = { Text("esempio@provider.com") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                            modifier = Modifier.background(backGround),
                            colors = TextFieldDefaults.outlinedTextFieldColors()
                        )
                        Spacer(modifier = Modifier.height(min))
                        TextField(
                            value = password,
                            onValueChange = { password = it },
                            singleLine = true,
                            label = { Text("Password") },
                            keyboardActions = KeyboardActions(onDone = {
                                val client = OkHttpClient()
                                val formBody = MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("request", "aggiungiUtente")
                                    .addFormDataPart("email", mail)
                                    .addFormDataPart("nome", name)
                                    .addFormDataPart("cognome", surname)
                                    .addFormDataPart("password", calculateHmacSha512(password, key))
                                    .addFormDataPart("key", key)
                                    .build()
                                val request = Request.Builder()
                                    .url("https://isi-seawatch.csr.unibo.it/Sito/sito/templates/main_signup/signup_api.php")
                                    .post(formBody)
                                    .build()

                                client.newCall(request).enqueue(object : Callback {
                                    override fun onFailure(call: Call, e: IOException) {
                                        errorMessage = "Impossibile comunicare col server!"
                                    }

                                    override fun onResponse(call: Call, response: Response) {
                                        val body = response.body?.string()
                                        val jsonObject = JSONObject(body.toString())
                                        if(jsonObject.getString("state")=="true"){
                                            (context as MainActivity).runOnUiThread {
                                                goToLogin()
                                            }

                                        } else {
                                            errorMessage =  jsonObject.getString("msg")
                                        }
                                    }
                                })
                            }),
                            visualTransformation =
                            if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(onClick = { passwordHidden = !passwordHidden }) {
                                    if (passwordHidden) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_visibility_24),
                                            contentDescription = "Visibile"
                                        )
                                    } else {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_visibility_off_24),
                                            contentDescription = "Non visibile"
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.background(backGround),
                            colors = TextFieldDefaults.outlinedTextFieldColors()
                        )
                    }
                }
                LazyColumn(
                    modifier = modifier
                        .fillMaxHeight()
                        .padding(horizontal = hig)
                        .background(backGround),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    items(1) { element ->
                        Button(
                            onClick = {
                                val client = OkHttpClient()
                                val formBody = MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("request", "aggiungiUtente")
                                    .addFormDataPart("email", mail)
                                    .addFormDataPart("nome", name)
                                    .addFormDataPart("cognome", surname)
                                    .addFormDataPart("password", calculateHmacSha512(password, key))
                                    .addFormDataPart("key", key)
                                    .build()
                                val request = Request.Builder()
                                    .url("https://isi-seawatch.csr.unibo.it/Sito/sito/templates/main_signup/signup_api.php")
                                    .post(formBody)
                                    .build()

                                client.newCall(request).enqueue(object : Callback {
                                    override fun onFailure(call: Call, e: IOException) {
                                        errorMessage = "Impossibile comunicare col server!"
                                    }

                                    override fun onResponse(call: Call, response: Response) {
                                        val body = response.body?.string()
                                        val jsonObject = JSONObject(body.toString())
                                        if(jsonObject.getString("state")=="true"){
                                            (context as MainActivity).runOnUiThread {
                                                goToLogin()
                                            }

                                        } else {
                                            errorMessage =  jsonObject.getString("msg")
                                        }
                                    }
                                })
                            },
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onPrimaryContainer),
                            modifier = modifier.widthIn(min = 200.dp)
                        ) {
                            Text("CREA ACCOUNT")
                        }
                        Spacer(modifier = Modifier.height(min))
                        Button(
                            onClick = { goToLogin() },
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                            modifier = modifier.widthIn(min = 150.dp)
                        ) {
                            Text("HO UN ACCOUNT")
                        }
                    }
                }
            }
        }
        else -> {                                                   /** SignUp verticale*/
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(backGround),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(1) { element ->
                    Spacer(modifier = Modifier.height(med))
                    Image(
                        painter = painterResource(R.drawable.sea),
                        contentDescription = "Immagine Logo"
                    )
                    Spacer(modifier = Modifier.height(min))
                    Text(text = "REGISTRATI", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(min))
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome") },
                        singleLine = true,
                        placeholder = { Text("Mario") },
                        modifier = Modifier.background(backGround),
                        colors = TextFieldDefaults.outlinedTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(min/2))
                    TextField(
                        value = surname,
                        onValueChange = { surname = it },
                        label = { Text("Cognome") },
                        singleLine = true,
                        placeholder = { Text("Rossi") },
                        modifier = Modifier.background(backGround),
                        colors = TextFieldDefaults.outlinedTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(min/2))
                    TextField(
                        value = mail,
                        onValueChange = { mail = it },
                        label = { Text("Email") },
                        singleLine = true,
                        placeholder = { Text("esempio@provider.com") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                        modifier = Modifier.background(backGround),
                        colors = TextFieldDefaults.outlinedTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(min/2))
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        singleLine = true,
                        label = { Text("Password") },
                        keyboardActions = KeyboardActions(onDone = {
                            val client = OkHttpClient()
                            val formBody = MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("request", "aggiungiUtente")
                                .addFormDataPart("email", mail)
                                .addFormDataPart("nome", name)
                                .addFormDataPart("cognome", surname)
                                .addFormDataPart("password", calculateHmacSha512(password, key))
                                .addFormDataPart("key", key)
                                .build()
                            val request = Request.Builder()
                                .url("https://isi-seawatch.csr.unibo.it/Sito/sito/templates/main_signup/signup_api.php")
                                .post(formBody)
                                .build()

                            client.newCall(request).enqueue(object : Callback {
                                override fun onFailure(call: Call, e: IOException) {
                                    errorMessage = "Impossibile comunicare col server!"
                                }

                                override fun onResponse(call: Call, response: Response) {
                                    val body = response.body?.string()
                                    val jsonObject = JSONObject(body.toString())
                                    if(jsonObject.getString("state")=="true"){
                                        (context as MainActivity).runOnUiThread {
                                            goToLogin()
                                        }

                                    } else {
                                        errorMessage =  jsonObject.getString("msg")
                                    }
                                }
                            })
                        }),
                        visualTransformation =
                        if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { passwordHidden = !passwordHidden }) {
                                if (passwordHidden) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_visibility_24),
                                        contentDescription = "Visibile"
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_visibility_off_24),
                                        contentDescription = "Non visibile"
                                    )
                                }
                            }
                        },
                        modifier = Modifier.background(backGround),
                        colors = TextFieldDefaults.outlinedTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(med))
                    Button(
                        onClick = {
                            val client = OkHttpClient()
                            val formBody = MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("request", "aggiungiUtente")
                                .addFormDataPart("email", mail)
                                .addFormDataPart("nome", name)
                                .addFormDataPart("cognome", surname)
                                .addFormDataPart("password", calculateHmacSha512(password, key))
                                .addFormDataPart("key", key)
                                .build()
                            val request = Request.Builder()
                                .url("https://isi-seawatch.csr.unibo.it/Sito/sito/templates/main_signup/signup_api.php")
                                .post(formBody)
                                .build()

                            client.newCall(request).enqueue(object : Callback {
                                override fun onFailure(call: Call, e: IOException) {
                                    errorMessage = "Impossibile comunicare col server!"
                                }

                                override fun onResponse(call: Call, response: Response) {
                                    val body = response.body?.string()
                                    val jsonObject = JSONObject(body.toString())
                                    if(jsonObject.getString("state")=="true"){
                                        (context as MainActivity).runOnUiThread {
                                            goToLogin()
                                        }

                                    } else {
                                        errorMessage =  jsonObject.getString("msg")
                                    }
                                }
                            })
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onPrimaryContainer),
                        modifier = modifier.widthIn(min = 250.dp)
                    ) {
                        Text("CREA ACCOUNT")
                    }
                    Spacer(modifier = Modifier.height(min))
                    Button(
                        onClick = { goToLogin() },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                        modifier = modifier.widthIn(min = 180.dp)
                    ) {
                        Text("HO UN ACCOUNT")
                    }
                }
            }
        }
    }
}