package com.example.btracker.ui.routes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.btracker.R
import com.example.btracker.databinding.RouteItemBinding

class RouteAdapter: RecyclerView.Adapter<RouteAdapter.RouteHolder>(){
    val routeList = ArrayList<Route>()

    class RouteHolder(item: View): RecyclerView.ViewHolder(item) {
        val binding = RouteItemBinding.bind(item)
        fun bind(route: Route) = with(binding){
            //routeImage.setImageResource(route.imageId)
            routeImage.setImageBitmap(route.bitmap)
            routeName.text = route.name
            routeData.text = route.data
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.route_item, parent, false)
        return RouteHolder(view)
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

}