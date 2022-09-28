package com.example.btracker.ui.routes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.btracker.R
import com.example.btracker.databinding.RouteItemBinding


class RouteAdapter(val onClickFunc: (route: Route) -> Unit): RecyclerView.Adapter<RouteAdapter.RouteHolder>(), View.OnClickListener {
    private val routeList = ArrayList<Route>()

    class RouteHolder(item: View): RecyclerView.ViewHolder(item), View.OnClickListener {
        val binding = RouteItemBinding.bind(item)
        fun bind(route: Route) = with(binding){
            //routeImage.setImageResource(route.imageId)
            routeImage.setImageBitmap(route.bitmap)
            routeDate.text = route.date
            routeDistance.text = route.distance
            routeDuration.text = route.duration
        }

        override fun onClick(p0: View?) {
            TODO("Not yet implemented")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.route_item, parent, false)
        val holder = RouteHolder(view)
        view.setOnClickListener{
            val idx = holder.absoluteAdapterPosition
            onClickFunc(routeList[idx])
        }
        return holder
    }

    override fun onBindViewHolder(holder: RouteHolder, position: Int) {
        holder.bind(routeList[position])
    }

    override fun getItemCount(): Int {
        return routeList.size
    }

    fun addRoute(route: Route){
        routeList.add(route)
        notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
    }
}