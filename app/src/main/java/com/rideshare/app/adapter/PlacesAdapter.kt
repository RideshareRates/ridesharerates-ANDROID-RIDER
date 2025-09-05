package com.rideshare.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.AutocompletePrediction


class PlacesAdapter(
    private val places: MutableList<AutocompletePrediction>,
    private val onItemClick: (AutocompletePrediction) -> Unit
) : RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return PlaceViewHolder(view)
    }


    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val prediction = places[position]
        holder.bind(prediction)
    }


    override fun getItemCount(): Int = places.size

    fun updatePlaces(newPlaces: List<AutocompletePrediction>) {
        places.clear()
        places.addAll(newPlaces)
        notifyDataSetChanged()
    }


    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(prediction: AutocompletePrediction) {
            (itemView as TextView).apply {
                text = prediction.getFullText(null)
                setTextColor(android.graphics.Color.WHITE)         // White text
                setBackgroundColor(android.graphics.Color.BLACK)   // Black background
            }

            itemView.setOnClickListener {
                onItemClick(prediction)
            }
        }

    }
}



