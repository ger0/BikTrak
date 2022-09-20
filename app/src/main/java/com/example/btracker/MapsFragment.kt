package com.example.btracker

import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.example.btracker.LocationProvider.Companion.PERIOD_BACKGROUND
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

// current data to be drawn
data class Ui(
    val strSpeed: String,
    val strDist : String,
    val position: LatLng?,
    val bearing : Float,
    val path    : List<LatLng>,
) {
    companion object {
        val EMPTY = Ui(
            strSpeed    = "",
            strDist     = "",
            position    = null,
            bearing     = 0f,
            path        = emptyList(),
        )
    }
}

class MapsFragment : Fragment(), SensorEventListener {
    private lateinit var map                : GoogleMap
    private lateinit var locationProvider   : LocationProvider
    private lateinit var sensorManager      : SensorManager

    private var tiltSensor : Sensor? = null
    private var optTilted   = true

    private var isActive    = false

    var ui = MutableLiveData(Ui.EMPTY)

    var cameraZoom = 14f
    var cameraTilt = 0f

    fun setProvider(provider: LocationProvider) {
        this.locationProvider = provider
    }

    fun setSensorManager(manager: SensorManager) {
        this.sensorManager = manager
    }

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        this.map = googleMap

        map.setMyLocationEnabled(true)
        map.uiSettings.isZoomControlsEnabled = true

        ui.observe(this) { ui ->
            updateUi(ui)
        }
    }

    fun clearTrack() {
        map.clear()
    }

    @SuppressLint("MissingPermission")
    private fun updateUi(ui: Ui) {
        if (ui.position != null && ui.position != map.cameraPosition.target) {
            val tilt = if (optTilted) cameraTilt else 0f
            val update  = CameraPosition(ui.position, cameraZoom, tilt, ui.bearing)

            map.isMyLocationEnabled = true
            map.animateCamera(CameraUpdateFactory.newCameraPosition(update))

            cameraZoom = map.cameraPosition.zoom
            drawRoute(ui.path)
        }
    }

    private fun drawRoute(path: List<LatLng>) {
        val polylineOptions = PolylineOptions()
        clearTrack()
        val points = polylineOptions.points
        points.addAll(path)

        map.addPolyline(polylineOptions)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tiltSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        // observers
        locationProvider.liveLocations.observe(viewLifecycleOwner) { locations ->
            val current = ui.value
            ui.value = current?.copy(path = locations)
        }
        locationProvider.liveLocation.observe(viewLifecycleOwner) { location ->
            val current = ui.value
            ui.value = current?.copy(position = location)
        }
        locationProvider.liveDistance.observe(viewLifecycleOwner) { distance ->
            val current = ui.value
            val formatDist = getString(R.string.distance, distance)
            ui.value = current?.copy(strDist = formatDist)
        }
        locationProvider.liveSpeed.observe(viewLifecycleOwner) { speed ->
            val current = ui.value
            val formatSpeed = getString(R.string.speed, speed)
            ui.value = current?.copy(strSpeed = formatSpeed)
        }
        locationProvider.liveBearing.observe(viewLifecycleOwner) { bearing ->
            val current = ui.value
            ui.value = current?.copy(bearing = bearing)
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onStart() {
        super.onStart()
        // 0.5 seconds sampling
        sensorManager.registerListener(this, tiltSensor, 500000)
        isActive = true
    }

    // gdy wyjdziemy z aplikacji odpala sie watek sprawdzajacy pozycje
    override fun onStop() {
        super.onStop()
        sensorManager.unregisterListener(this)
        isActive = false

        // there might be a better way to do this...
        Thread {
            while (!isActive) {
                Thread.sleep(PERIOD_BACKGROUND)
                locationProvider.getUserLocation()
            }
        }.start()
    }

    // wodotryski
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
            cameraTilt = max(min(event.values[1] * 180f, 90f), 0f)
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}