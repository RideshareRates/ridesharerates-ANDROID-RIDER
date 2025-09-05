package com.rideshare.app.acitivities

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.rideshare.app.R
import com.rideshare.app.adapter.PlacesAdapter

class CustomPlaceAutoCompleteActivity : AppCompatActivity() {

    private lateinit var placesClient: PlacesClient
    private lateinit var adapter: PlacesAdapter
    private val predictionList = mutableListOf<AutocompletePrediction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_custom_place_auto_complete)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val recyclerView = findViewById<RecyclerView>(R.id.placesRecyclerView)


        placesClient = Places.createClient(this)


        adapter = PlacesAdapter(predictionList) { prediction ->
            val placeId = prediction.placeId
            val placeFields =  listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)


            val request = FetchPlaceRequest.builder(placeId, placeFields).build()
            placesClient.fetchPlace(request)
                .addOnSuccessListener { response ->
                    val place = response.place
                    val intent = Intent()
                    intent.putExtra("place_name", place.name)
                    intent.putExtra("place_address", place.address)
                    intent.putExtra("place_lat", place.latLng?.latitude)
                    intent.putExtra("place_lng", place.latLng?.longitude)
                    setResult(RESULT_OK, intent)
                    finish()
                }
        }

        val backButton: ImageButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            val homeIntent = Intent(this, HomeActivity::class.java)
            startActivity(homeIntent)
            finish()
        }


        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        searchEditText.doOnTextChanged { text, _, _, _ ->
            val query = text.toString()
            if (query.isEmpty()) {
                // Clear the suggestions when the query is empty
                adapter.updatePlaces(emptyList())
            } else if (query.length > 2) {
                val request = FindAutocompletePredictionsRequest.builder()
                    .setQuery(query)
                    .build()

                placesClient.findAutocompletePredictions(request)
                    .addOnSuccessListener { response ->
                        adapter.updatePlaces(response.autocompletePredictions)
                    }
            }
        }
    }

}

