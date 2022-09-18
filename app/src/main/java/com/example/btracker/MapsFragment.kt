package com.example.btracker

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions

data class Ui(
    val strSpeed:   String,
    val strDist:    String,
    val position:   LatLng?,
    val path:       List<LatLng>
) {
    companion object {
        val EMPTY = Ui(
            strSpeed    = "",
            strDist     = "",
            position    = null,
            path        = emptyList()
        )
    }
}

class MapsFragment : Fragment() {
    private lateinit var map: GoogleMap
    private lateinit var locationProvider: LocationProvider
    private var ui = MutableLiveData(Ui.EMPTY)

    fun setProvider(provider: LocationProvider) {
        this.locationProvider = provider
    }

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        this.map = googleMap

        locationProvider.liveLocation.observe(this) {
                latLng -> map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
        }
        map.setMyLocationEnabled(true)
        map.uiSettings.isZoomControlsEnabled = true

        ui.observe(this) { ui ->
            updateUi(ui)
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateUi(ui: Ui) {
        if (ui.position != null && ui.position != map.cameraPosition.target) {
            map.isMyLocationEnabled = true
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(ui.position, 14f))
        }
        drawRoute(ui.path)
    }

    private fun drawRoute(path: List<LatLng>) {
        val polylineOptions = PolylineOptions()
        map.clear()
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

        locationProvider.liveLocations.observe(viewLifecycleOwner) { locations ->
            val current = ui.value
            ui.value = current?.copy(path = locations)
        }
        locationProvider.liveLocation.observe(viewLifecycleOwner) { location ->
            val current = ui.value
            ui.value = current?.copy(position = location)
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}