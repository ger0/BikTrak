package com.example.btracker.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.btracker.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val changeThemeCheckBox: CheckBox = binding.changeTheme
        val rotationCheckBox: CheckBox = binding.mapRotation
        val inclinationCheckBox: CheckBox = binding.mapInclination

        changeThemeCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                Toast.makeText(context, "Pressed!", Toast.LENGTH_SHORT).show()
        }else{
                Toast.makeText(context, "Not pressed...", Toast.LENGTH_SHORT).show()
        }}

        rotationCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                //
            }else{
                //
            }}

        inclinationCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                //
            }else{
                //
            }}

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}