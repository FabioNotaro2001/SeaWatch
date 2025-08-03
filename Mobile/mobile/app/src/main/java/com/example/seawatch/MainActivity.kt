package com.example.seawatch

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.OrientationEventListener
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import coil.compose.rememberImagePainter
import com.example.seawatch.data.*
import com.example.seawatch.ui.theme.SeaWatchTheme
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

public var lastImageBitmap: Bitmap? = null
@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    val avvistamentiViewModel by viewModels<AvvistamentiViewModel> {
        ViewModelFactory(repository=(application as SWApplication).repository)
    }

    val favouriteViewModel by viewModels<FavouriteViewModel> {
        FavouriteViewModelFactory(repository=(application as SWApplication).repository2)
    }

    val userViewModel by viewModels<UserViewModel> {
        UserViewModelFactory(repository=(application as SWApplication).repository3)
    }

    val avvistamentiViewViewModel by viewModels<AvvistamentiViewViewModel> {
        AvvistamentiViewModelFactory(repository=(application as SWApplication).repository4)
    }

    val descriptionViewModel by viewModels<DescriptionViewModel> {
        DescriptionViewModelFactory(repository=(application as SWApplication).repository5)
    }

    public var showSnackBar = mutableStateOf(false)
    public var showAlertDialog = mutableStateOf(false)

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    val placesViewModel by viewModels<PlacesViewModel>()

    private var requestingLocationUpdates = mutableStateOf(false)

    private lateinit var locationPermissionRequest: ActivityResultLauncher<String>

    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private lateinit var connectivityManager : ConnectivityManager

    val warningViewModel by viewModels<WarningViewModel>()

    val location = mutableStateOf(LocationDetails(0.toDouble(), 0.toDouble()))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        descriptionViewModel.deleteAll()
        descriptionViewModel.populate()
        val sharedPrefForLogin=getPreferences(Context.MODE_PRIVATE)
        getDatesFromServer(avvistamentiViewViewModel, this)




        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                startLocationUpdates()
            } else {
                warningViewModel.setPermissionSnackBarVisibility(true)
            }
        }

        locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000).apply {
                setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                location.value = LocationDetails(
                    p0.locations.first().latitude,
                    p0.locations.first().longitude
                )
                stopLocationUpdates()
                placesViewModel.setGPSPlace(location.value)

            }
        }

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network : Network) {
                if (requestingLocationUpdates.value) {
                    placesViewModel.setGPSPlace(location.value)
                    warningViewModel.setConnectivitySnackBarVisibility(false)
                }
            }

            override fun onLost(network : Network) {
                warningViewModel.setConnectivitySnackBarVisibility(true)
            }
        }


        var coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        coroutineScope.launch {
            val avvistamenti = runBlocking { avvistamentiViewModel.all.first() }
            uploadToServer(applicationContext, avvistamenti, avvistamentiViewViewModel, avvistamentiViewModel)
        }

        setContent {
            val theme by settingsViewModel.theme.collectAsState(initial = "")
            val listItems by favouriteViewModel.all.collectAsState(initial = listOf())
            SeaWatchTheme(darkTheme = theme == getString(R.string.dark_theme)) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val radioOptions = listOf(getString(R.string.light_theme), getString(R.string.dark_theme))
                    NavigationApp(radioOptions = radioOptions,
                        theme = theme,
                        settingsViewModel =  settingsViewModel,
                        sharedPrefForLogin=sharedPrefForLogin,
                        avvistamentiViewModel=avvistamentiViewModel,
                        favouriteViewModel=favouriteViewModel,
                        listItems=listItems,
                        userViewModel=userViewModel,
                        avvistamentiViewViewModel=avvistamentiViewViewModel,
                        descriptionViewModel=descriptionViewModel,
                        warningViewModel = warningViewModel,
                        startLocationUpdates=::startLocationUpdates
                    )
                }
                if (requestingLocationUpdates.value) {
                    connectivityManager.registerDefaultNetworkCallback(networkCallback)
                }
            }
        }
    }

    private var name: String? = null
    private var count: Int? = null
    private var photoUri: Uri? = null

    fun capturePhoto() {
        val context = (this as Context)
        val tempFile = context.createImageFile()
        photoUri = FileProvider.getUriForFile(
            Objects.requireNonNull(context),
            context.packageName + ".provider", tempFile!!
        )
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        startActivityForResult(cameraIntent, 200)
    }

    fun Context.createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        return File.createTempFile(
            imageFileName,
            ".jpg",
            externalCacheDir
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 200) {
            photoUri?.let {
                saveImage((this as Context).applicationContext.contentResolver, it, name, count)
                recreate()
            }
        }
    }

    fun saveImage(contentResolver: ContentResolver, capturedImageUri: Uri, label:String?, c:Int?) {
        val filename = "$label$c.jpg"
        val bitmap = getBitmap(capturedImageUri, contentResolver)
        Log.e("KEYYY",bitmap.byteCount.toString())

        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename)

        val imageUri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val outputStream = imageUri?.let { contentResolver.openOutputStream(it) }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream?.close()
    }

    private fun getBitmap(selectedPhotoUri: Uri, contentResolver: ContentResolver): Bitmap {
        val bitmap = when {
            Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(
                contentResolver,
                selectedPhotoUri
            )
            else -> {
                val source = ImageDecoder.createSource(contentResolver, selectedPhotoUri)
                ImageDecoder.decodeBitmap(source)
            }
        }
        return bitmap
    }

    fun getDatesFromServer(avvistamentiViewViewModel: AvvistamentiViewViewModel, context:Context){
        if(isNetworkAvailable(context)){
            val client = OkHttpClient()
            val formBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("request", "tbl_avvistamenti")
                .build()
            val request = Request.Builder()
                .url("https://isi-seawatch.csr.unibo.it/Sito/sito/templates/main_sighting/sighting_api.php")
                .post(formBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    var temp = JSONArray(body)

                    avvistamentiViewViewModel.deleteAll()
                    for (i in 0 until temp.length() step 1) {
                        avvistamentiViewViewModel.insert(
                            AvvistamentiDaVedere(
                                (temp.get(i) as JSONObject).get("ID").toString(),
                                (temp.get(i) as JSONObject).get("Email").toString(),
                                (temp.get(i) as JSONObject).get("Data").toString(),
                                (temp.get(i) as JSONObject).get("Numero_Esemplari").toString(),
                                (temp.get(i) as JSONObject).get("Latid").toString(),
                                (temp.get(i) as JSONObject).get("Long").toString() ,
                                (temp.get(i) as JSONObject).get("Anima_Nome").toString(),
                                (temp.get(i) as JSONObject).get("Specie_Nome").toString(),
                                (temp.get(i) as JSONObject).get("Mare").toString(),
                                (temp.get(i) as JSONObject).get("Vento").toString(),
                                (temp.get(i) as JSONObject).get("Note").toString(),
                                (temp.get(i) as JSONObject).get("Img").toString(),
                                (temp.get(i) as JSONObject).get("Nome").toString(),
                                (temp.get(i) as JSONObject).get("Cognome").toString(),
                                true
                            )
                        )
                    }
                }
            })
        }
    }

    /** Controllo permessi camera*/
    private val requestPermissionLauncherCamera = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            if(Build.VERSION.SDK_INT < 28){
                requestStoragePermission()
            } else {
                capturePhoto()
            }
        } else {
            // Il permesso è stato negato, mostra un messaggio all'utente
            Toast.makeText(this, "Il permesso è necessario per accedere alla fotocamera!", Toast.LENGTH_SHORT).show()
        }
    }

    public fun requestCameraPermission(id : String, c: Int=0) {
        name = id
        count = c
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                if(Build.VERSION.SDK_INT < 28){
                    requestStoragePermission()
                } else {
                    capturePhoto()
                }
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                // L'utente ha negato il permesso in precedenza, mostra un messaggio con una spiegazione
                requestPermissionLauncherCamera.launch(Manifest.permission.CAMERA)
            }
            else -> {
                // Il permesso non è stato ancora richiesto, richiedilo all'utente
                requestPermissionLauncherCamera.launch(Manifest.permission.CAMERA)
            }
        }
    }


    /** Permessi per il salvataggio in memoria */
    private fun requestStoragePermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                capturePhoto()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.MANAGE_EXTERNAL_STORAGE) -> {
                requestPermissionLauncher.launch(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            }
            else -> {
                // Il permesso non è stato ancora richiesto, richiedilo all'utente
                requestPermissionLauncher.launch(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            capturePhoto()
        } else {
            Toast.makeText(this, "Il permesso è necessario per accedere alla fotocamera!", Toast.LENGTH_SHORT).show()
        }
    }

    /** Prendo tutte le immaigni salvate */
    public fun getAllSavedImages(label: String?): List<Uri> {
        val imagesList = mutableListOf<Uri>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT
        )
        val selection = "${MediaStore.Images.Media.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("%$label%")
        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN}"
        applicationContext.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                imagesList.add(contentUri)
            }
        }
        return imagesList
    }


    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates.value) startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    override fun onStop() {
        super.onStop()
        if (requestingLocationUpdates.value)
            (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
                .unregisterNetworkCallback(networkCallback)
    }

    override fun onStart() {
        super.onStart()
        if (requestingLocationUpdates.value)
            (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
                .registerDefaultNetworkCallback(networkCallback)
    }

    private fun startLocationUpdates() {
        requestingLocationUpdates.value = true
        val permission = Manifest.permission.ACCESS_COARSE_LOCATION

        when {
            //permission already granted
            ContextCompat.checkSelfPermission (this, permission) == PackageManager.PERMISSION_GRANTED -> {
                locationRequest =
                    LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 3000).apply {
                        setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                        setWaitForAccurateLocation(true)
                    }.build()

                val gpsEnabled = checkGPS()
                if (gpsEnabled) {
                    fusedLocationProviderClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )
                } else {
                    warningViewModel.setGPSAlertDialogVisibility(true)
                }
            }
            //permission already denied
            shouldShowRequestPermissionRationale(permission) -> {
                warningViewModel.setPermissionSnackBarVisibility(true)
            }
            else -> {
                //first time: ask for permissions
                locationPermissionRequest.launch(
                    permission
                )
            }
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun checkGPS(): Boolean {
        val mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}


