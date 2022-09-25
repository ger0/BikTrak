package com.example.btracker.ui.routes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.btracker.R
import com.example.btracker.databinding.FragmentRoutesBinding

class RoutesFragment : Fragment() {

    private var _binding: FragmentRoutesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val adapter = RouteAdapter()
    private val imageIdList = listOf(R.drawable.route1,
        R.drawable.route2,
        R.drawable.route3)
    private var index = 0
    var rnds = (10..50).random()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val routesViewModel =
            ViewModelProvider(this).get(RoutesViewModel::class.java)

        _binding = FragmentRoutesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*val textView: TextView = binding.textRoutes
        routesViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/

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

    private fun init() {
        binding.apply {
            recyclerViewRoutesFragment.layoutManager = LinearLayoutManager(context)
            recyclerViewRoutesFragment.adapter = adapter

            addButton.setOnClickListener {
                if (index > 2) index = 0
                val route = Route(imageIdList[index], "Route $index","$rnds, km")
                adapter.addRoute(route)
                index++
            }
        }
    }
}