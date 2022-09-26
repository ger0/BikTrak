package com.example.btracker.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.btracker.DB.DatabaseHelper
import com.example.btracker.DB.ImageData
import com.example.btracker.R
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.*
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates

// current data to be drawn
class MapFrag : Fragment(), SensorEventListener {
    private var isTracking by Delegates.notNull<Boolean>()
    private var position   : LatLng?        = null
    private var path       : List<LatLng>?  = null
    private var bearing    : Float          = 45f

    private var tiltSensor : Sensor? = null
    private var optTilted   = true
    private lateinit var map: GoogleMap

    private lateinit var sensorManager: SensorManager

    private val viewModel: MapViewModel by activityViewModels()

    var ui = MutableLiveData(Ui.EMPTY)

    var cameraZoom = 14f
    var cameraTilt = 0f

    // callback
    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        this.map = googleMap

        map.isMyLocationEnabled = true
        map.uiSettings.isZoomControlsEnabled = true

        // Observers
        viewModel.position.observe(viewLifecycleOwner, Observer { position ->
            this.position = position
            updateUi()
        })
        viewModel.isTracking.observe(viewLifecycleOwner, Observer { isTracking ->
            this.isTracking = isTracking
            if (isTracking) {
                map.clear()
            }
        })
        viewModel.bearing.observe(viewLifecycleOwner, Observer { bearing ->
            this.bearing = bearing
        })
        viewModel.path.observe(viewLifecycleOwner, Observer { path ->
            this.path = path
        })
        viewModel.shouldSnapshot.observe(viewLifecycleOwner) { shouldSnapshot ->
            if (shouldSnapshot) {
                writePolygonSnapshot()
                viewModel.shouldSnapshot.value = false
            }
        }
    }

    private fun clearTrack() {
        if (this::map.isInitialized) {
            map.clear()
        }
    }

    // take a picture and return it | ugly
    @SuppressLint("MissingPermission")
    fun writePolygonSnapshot() {
        if (path == null) return
        // prepare the map for drawing
        map.isMyLocationEnabled = false
        val boundsBuilder = LatLngBounds.builder()
        for (it in path!!) {
            boundsBuilder.include(it)
        }
        val bounds = boundsBuilder.build()
        map.setLatLngBoundsForCameraTarget(bounds)
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 1))
        // take snapshot and save it to a variable in the viewmodel
        map.snapshot{ bitmap ->
            viewModel.saveSnapshot(bitmap)
        }
        map.isMyLocationEnabled = true
    }

    // update camera position
    @SuppressLint("MissingPermission")
    private fun updateUi() {
        if (position != null && position != map.cameraPosition.target) {
            cameraZoom = map.cameraPosition.zoom
            val tilt = if (optTilted) cameraTilt else 0f
            val update  = CameraPosition(position!!, cameraZoom, tilt, bearing)

            map.isMyLocationEnabled = true
            map.animateCamera(CameraUpdateFactory.newCameraPosition(update))

            if (isTracking && path != null) {
                drawRoute(path!!)
            }
        }
    }

    // draw a polygon on the map
    private fun drawRoute(path: List<LatLng>) {
        val polylineOptions = PolylineOptions()
        clearTrack()
        val points = polylineOptions.points
        points.addAll(path)

        map.addPolyline(polylineOptions)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        tiltSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        isTracking = viewModel.isTracking.value!!
    }

    // wodotryski
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
            cameraTilt = max(min(event.values[1] * 180f, 90f), 0f)
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onStart() {
        super.onStart()
        // 0.5 sec sampling
        sensorManager.registerListener(this, tiltSensor, 500000)
        clearTrack()
    }

    override fun onStop() {
        super.onStop()
        sensorManager.unregisterListener(this)
        clearTrack()
    }
}