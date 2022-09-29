package com.example.btracker

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.btracker.DB.DatabaseHelper
import com.example.btracker.DB.ImageData
import com.example.btracker.databinding.ActivityMainBinding
import com.example.btracker.ui.map.MapViewModel
import com.example.btracker.ui.routes.RoutesViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

class MainActivity : AppCompatActivity() {
    private var username: String = ""

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private var isActive: Boolean = true

    private val mapViewModel: MapViewModel by viewModels()
    private val routeViewModel: RoutesViewModel by viewModels()

    private lateinit var locationProvider:  LocationProvider
    private lateinit var permissionManager: PermissionsManager
    private var database = DatabaseHelper(this)

    private lateinit var sensorManager  : SensorManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // assign username
        if (intent.extras?.containsKey("username") == true) {
            username = intent.getStringExtra("username").toString()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // permissions
        sensorManager       = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        locationProvider    = LocationProvider(this, mapViewModel)
        permissionManager   = PermissionsManager(this)
        permissionManager.setLocationProvider(locationProvider)
        permissionManager.requestUserLocation()

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_map, R.id.nav_routes, R.id.nav_settings
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        mapViewModel.isTracking.observe(this) { isTracking ->
            if (mapViewModel.initialised) {
                toggleTracking(isTracking)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onStart() {
        super.onStart()
        isActive = true
    }

    override fun onStop() {
        super.onStop()
        isActive = false
        Thread {
            while (!isActive) {
                Thread.sleep(LocationProvider.PERIOD_BACKGROUND)
                locationProvider.getUserLocation()
            }
        }.start()
    }

    // ------------------------------------- Default activity --------------------------------------
    // handles starting and stopping tracking functionality
    @RequiresApi(Build.VERSION_CODES.O)
    private fun toggleTracking(isTracking: Boolean) {
        // Starting to track
        if (isTracking) {
            locationProvider.trackUser()
        }
        // Stopping tracking
        else if (!isTracking) {
            locationProvider.stopTracking()

            // check if theres data available
            val trackData = mapViewModel.scrapTrackData(username = username) ?: return
            database.addTrack(trackData)

            val imgData = ImageData(
                refId = trackData.id!!,
                type = ImageData.THUMBNAIL
            )

            /* we want the map fragment to take snapshot of the track       *
            *  receive the image and append it to the image database        *
            *  the observer will get removed once the image gets retrieved  */
            mapViewModel.shouldSnapshot.value = true
            mapViewModel.savedSnapshot.observe(this) { shouldRetrieve ->
                if (shouldRetrieve) {
                    val bitmap = mapViewModel.retrieveSnapshot(this)
                    if (bitmap != null) {
                        imgData.bitmap = bitmap
                        database.addImage(imgData)
                    }
                }
            }
            mapViewModel.initialised = false
        }
        mapViewModel.resetTrackData()
        locationProvider.clearData()
    }
    fun sendTrackInfo(bitmap: Bitmap, text: String) {
        this.permissionManager.requestWriteRead()
        val file = File(Environment.getExternalStorageDirectory().toString() +
                "/Android/data/" + packageName + "/cache/" + UUID.randomUUID().toString() + ".jpg");

        val uri: Uri

        val bos = ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        try {
            if (!file.exists())  file.createNewFile();
            val fos = FileOutputStream(file);
            fos.write(bos.toByteArray());
            uri = FileProvider.getUriForFile(applicationContext, BuildConfig.APPLICATION_ID + ".provider", file)

            val intent = Intent(Intent.ACTION_SEND);
            intent.type = "image/jpeg"
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_TEXT, text)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(intent, "SHARE"))
        } catch (exception: Exception) {
            println(exception.message)
        }
    }
}