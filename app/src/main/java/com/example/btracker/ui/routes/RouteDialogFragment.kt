package com.example.btracker.ui.routes

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.btracker.MainActivity
import com.example.btracker.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class RouteDialogFragment : DialogFragment() {
    private val viewModel: RoutesViewModel by activityViewModels()

    private fun setRouteData(view: View?) {
        view?.findViewById<ImageView>(R.id.dialogRouteImage)?.setImageBitmap(viewModel.route?.bitmap)
        view?.findViewById<TextView>(R.id.dialogRouteDate)?.text = viewModel.route?.date
        view?.findViewById<TextView>(R.id.dialogRouteDistance)?.text = viewModel.route?.distance
        view?.findViewById<TextView>(R.id.dialogRouteDuration)?.text = viewModel.route?.duration
        view?.findViewById<TextView>(R.id.dialogRouteUsername)?.text = viewModel.route?.username
        view?.findViewById<TextView>(R.id.dialogRouteAvgSpeed)?.text = viewModel.route?.averageSpeed

        view?.findViewById<FloatingActionButton>(R.id.dialogShareButton)?.setOnClickListener {
            val str = viewModel.route?.distance + "\n" +
                    viewModel.route?.duration
            (activity as MainActivity).sendTrackInfo(viewModel.route!!.bitmap, str)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.fragment_route_dialog, null)
            setRouteData(view)
            builder.setView(view)
            builder.create()
        } ?: throw IllegalStateException("Error during dialog creation!")

    }
}