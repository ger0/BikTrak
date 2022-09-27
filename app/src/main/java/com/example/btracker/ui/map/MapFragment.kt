package com.example.btracker.ui.map

import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.btracker.R
import com.example.btracker.databinding.FragmentMapBinding

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null

    private val mapViewModel: MapViewModel by activityViewModels()

    // chronometer base
    private var timerTime = Long.MIN_VALUE

    private lateinit var durationChrono : Chronometer
    private lateinit var trackButton  : Button
    private lateinit var speedText    : TextView
    private lateinit var distanceText : TextView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        init(mapViewModel)

        return root
    }

    private fun changeButtonText() {
        if (mapViewModel.isTracking.value == true) {
            trackButton.text = getString(R.string.button_toggle_tracking_off)
            trackButton.setTextColor(Color.RED)
        } else {
            trackButton.text = getString(R.string.button_toggle_tracking_on)
            trackButton.setTextColor(Color.BLACK)
        }
    }

    private fun init(mapViewModel: MapViewModel) {
        binding.apply {
            durationChrono = binding.dur!!
            trackButton = binding.trackButt!!
            speedText = binding.spd!!
            distanceText = binding.dist!!
        }
        changeButtonText()
        trackButton.setOnClickListener {
            val isTracking = mapViewModel.isTracking.value
            if (isTracking == true) {
                stopTimer()
                mapViewModel.lastTime = timerTime
                mapViewModel.isTracking.value = false
            } else if (isTracking == false) {
                startTimer()
                mapViewModel.startTracking()
            }
            changeButtonText()
        }
        mapViewModel.speed.observe(viewLifecycleOwner) { speed ->
            speedText.text = getString(R.string.speed, speed)
        }
        mapViewModel.distance.observe(viewLifecycleOwner) { distance ->
            distanceText.text = getString(R.string.distance, distance)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ----------- chronometer functions -------------
    private fun startTimer(isResumed: Boolean = false) {
        val offset = if (isResumed) mapViewModel.lastTime else 0L
        durationChrono.base = SystemClock.elapsedRealtime() + offset
        durationChrono.start()
        //timerTime = Long.MIN_VALUE + offset
    }
    private fun stopTimer() {
        mapViewModel.lastTime = durationChrono.base - SystemClock.elapsedRealtime()
        durationChrono.stop()
        timerTime = SystemClock.elapsedRealtime() - durationChrono.base
    }
    // -------------------------

    override fun onStart() {
        super.onStart()
        if (mapViewModel.isTracking.value == true) {
            startTimer(isResumed = true)
        }
    }

    override fun onStop() {
        super.onStop()
        stopTimer()
    }
}
