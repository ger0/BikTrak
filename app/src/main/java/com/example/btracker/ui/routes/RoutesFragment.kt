package com.example.btracker.ui.routes

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.btracker.DB.DatabaseHelper
import com.example.btracker.R
import com.example.btracker.databinding.FragmentRoutesBinding
import java.time.format.DateTimeFormatter

class RoutesFragment : Fragment() {
    private lateinit var database: DatabaseHelper
    private var _binding: FragmentRoutesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val adapter = RouteAdapter(onClickFunc = ::displayRouteDialog)
    private val imageIdList = listOf(R.drawable.route1,
        R.drawable.route2,
        R.drawable.route3)
    private var index = 0
    var rnds = (10..50).random()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val routesViewModel =
            ViewModelProvider(this)[RoutesViewModel::class.java]

        _binding = FragmentRoutesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*val textView: TextView = binding.textRoutes
        routesViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/

        database = DatabaseHelper(requireContext())
        init()
        return root
    }

    /*private fun fillList(): List<String> {
        val data = mutableListOf<String>()
        (0..30).forEach { i -> data.add("$i element") }
        return data
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun init() {
        binding.apply {
            recyclerViewRoutesFragment.layoutManager = LinearLayoutManager(context)
            recyclerViewRoutesFragment.adapter = adapter
            /*
            addButton.setOnClickListener {
                if (index > 2) index = 0
                val route = Route(imageIdList[index], "Route $index","$rnds, km")
                adapter.addRoute(route)
                index++
            }
             */
        }
        // lazy

        val dateFormatter = DateTimeFormatter.ofPattern("dd LLL yyyy")

        val tracks = database.allTracks
        val images = database.allImages
        for(item in tracks) {
            val image = images.find{ it.refId == item.id }

            val route = Route(
                image!!.bitmap,
                date = item.date.format(dateFormatter),
                distance = readableDistance(item.distance),
                duration = secondsToString(item.duration),
                averageSpeed = speedToKms(item.speed)
            )
            adapter.addRoute(route)
            index++
        }
    }
    private fun speedToKms(speed: Float): String {
        val _speed = speed * 3600 / 1000
        return "${_speed}km/h"
    }
    private fun readableDistance(rawMeters: Long): String {
        val kilo    = (rawMeters / 1000)
        return if (kilo > 1f) "${kilo}km"
        else "${rawMeters}m"
    }

    private fun secondsToString(rawSeconds: Long): String {
        val _hours  = (rawSeconds / (60 * 60)).toInt()
        val hours   = if (_hours > 0f) "$_hours:" else ""
        val minutes = "%02d".format((rawSeconds / 60) % 60)
        val seconds = "%02d".format(rawSeconds % 60)
        return "$hours$minutes:$seconds"
    }
    private fun displayRouteDialog(route: Route) {
        RouteDialogFragment().show(childFragmentManager, "What")
    }
}