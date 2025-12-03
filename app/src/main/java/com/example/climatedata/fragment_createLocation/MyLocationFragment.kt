package com.example.climatedata.fragment_createLocation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.climatedata.R
import com.example.climatedata.data.authen.AuthenticationManager
import com.example.climatedata.data.db.ClimateDatabase
import com.example.climatedata.data.models.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyLocationFragment : Fragment() {

    private lateinit var container: LinearLayout
    private var allLocations: List<Location> = emptyList()
    private var currentUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // можно получить userId через arguments или SharedPreferences
    }

    override fun onCreateView(
        inflater: LayoutInflater, containerLayout: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_location, containerLayout, false)
        container = view.findViewById(R.id.list_card) // контейнер для карточек

        loadUserIdAndLocations()

        return view
    }

    private fun loadUserIdAndLocations() {
        val db = ClimateDatabase.getInstance(requireContext())
        val authManager = AuthenticationManager(requireContext())

        val userId = authManager.getCurrentUserId()
        if (userId == -1L) return

        CoroutineScope(Dispatchers.IO).launch {
            currentUserId = userId.toInt()
            allLocations = db.getLocationsByUser(currentUserId)

            withContext(Dispatchers.Main) {
                displayLocations()
            }
        }
    }

    private fun displayLocations() {
        container.removeAllViews()

        if (allLocations.isEmpty()) {
            // показать заглушку
            val emptyCard = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_empty_list_location, container, false)
            container.addView(emptyCard)
        } else {
            allLocations.forEach { addLocationCard(it) }
        }
    }

    private fun addLocationCard(location: Location) {
        val inflater = LayoutInflater.from(requireContext())
        val cardView = inflater.inflate(R.layout.location_card, container, false)

        val nameView = cardView.findViewById<TextView>(R.id.text_location_name)
        val regionView = cardView.findViewById<TextView>(R.id.text_location_region)
        val typeView = cardView.findViewById<TextView>(R.id.text_location_type)
        val areaView = cardView.findViewById<TextView>(R.id.text_location_area)
        val imageView = cardView.findViewById<ImageView>(R.id.location_icon)

        nameView.text = location.name
        regionView.text = location.region
        typeView.text = location.type ?: "Не указано"
        areaView.text = location.area?.toString() ?: "—"

        when (location.type?.lowercase()) {
            "поле" -> imageView.setImageResource(R.drawable.ic_location_pole)
            "грядка" -> imageView.setImageResource(R.drawable.ic_location_garden_bed)
            "теплица" -> imageView.setImageResource(R.drawable.ic_location_greenhouse)
            else -> imageView.setImageResource(R.drawable.ic_location_other)
        }

        // клик по карточке можно обработать
        cardView.setOnClickListener {
            Toast.makeText(requireContext(), "Выбрана локация: ${location.name}", Toast.LENGTH_SHORT).show()
            // Здесь можно открыть новый фрагмент/редактировать локацию
        }

        container.addView(cardView)
    }
}
