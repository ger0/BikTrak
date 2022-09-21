package com.example.biktrak.ui.routes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biktrak.databinding.FragmentRoutesBinding

class RoutesFragment : Fragment() {

    private var _binding: FragmentRoutesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
        val recyclerView: RecyclerView = binding.recyclerViewRoutesFragment
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = CustomRecyclerAdapter(fillList())
        return root
    }

    private fun fillList(): List<String> {
        val data = mutableListOf<String>()
        (0..30).forEach { i -> data.add("$i element") }
        return data
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}