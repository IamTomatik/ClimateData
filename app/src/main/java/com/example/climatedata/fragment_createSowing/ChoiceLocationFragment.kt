package com.example.climatedata.fragment_createSowing

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.climatedata.R
import com.example.climatedata.data.authen.AuthenticationManager
import com.example.climatedata.data.db.ClimateDatabase
import com.example.climatedata.data.models.Location
import com.example.climatedata.fragment_createLocation.CreateLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChoiceLocationFragment : Fragment() {

    private lateinit var listRops: LinearLayout
    private lateinit var searchView: SearchView
    private var allLocations: List<Location> = emptyList()
    private var activeType: String? = null
    private var selectedButton: Button? = null
    private var selectedCropId: Int? = 0
    private var selectedCropName: String? = null
    private var enteredName: String? = null
    private lateinit var addPlant: ImageView
    private var currentUserId: Int = -1 // ID пользователя

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedCropName = arguments?.getString("selected_crop_name")
        enteredName = arguments?.getString("entered_name")
        selectedCropId = arguments?.getInt("selected_crop_id")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_choice_location, container, false)
        listRops = view.findViewById(R.id.list_card)
        searchView = view.findViewById(R.id.searchView)
        addPlant = view.findViewById(R.id.addPlant)

        setupFilterButtons(view)
        setupSearch()
        setupAddPlantButton()
        loadUserIdAndLocations()

        return view
    }

    private fun loadUserIdAndLocations() {
        val db = ClimateDatabase.getInstance(requireContext())
        val authManager = AuthenticationManager(requireContext())

        val userId = authManager.getCurrentUserId()
        if (userId == -1L) return // если пользователь не найден, выходим
        Log.d("ChoiceLocationFragment", "Пользователь не найден (userId = -1)")
        Log.d("ChoiceLocationFragment", "Текущий userId = $userId")
        CoroutineScope(Dispatchers.IO).launch {
            currentUserId = userId.toInt()
            allLocations = db.getLocationsByUser(currentUserId)

            withContext(Dispatchers.Main) {
                filterAndDisplay() // фильтруем и отображаем карточки
            }
        }
    }

    private fun checkEmptyLocations(container: ViewGroup) {
        if (allLocations.isEmpty()) {
            if (container.findViewById<View>(R.id.item_empty_list_location) == null) {
                val emptyCard = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_empty_list_location, container, false)
                container.addView(emptyCard)
            }
        } else {
            container.findViewById<View>(R.id.item_empty_list_location)?.let { container.removeView(it) }
        }
    }

    private fun setupFilterButtons(view: View) {
        val buttons = mapOf(
            R.id.all to null,
            R.id.pole to "поле",
            R.id.garden_bed to "грядка",
            R.id.greenhouse to "теплица",
            R.id.other to "другое"
        )

        buttons.forEach { (id, type) ->
            val button = view.findViewById<Button>(id)
            button.setOnClickListener {
                activeType = type
                updateButtonStates(buttons, button, view)
                filterAndDisplay()
            }
        }

        // Изначально выделяем "Все"
        val allButton = view.findViewById<Button>(R.id.all)
        updateButtonStates(buttons, allButton, view)
    }

    private fun updateButtonStates(buttons: Map<Int, String?>, active: Button, rootView: View) {
        buttons.keys.forEach { id ->
            val button = rootView.findViewById<Button>(id)
            if (button == active) {
                button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
                button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            } else {
                button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                button.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }
        }
        selectedButton = active
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterAndDisplay(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterAndDisplay(newText ?: "")
                return true
            }
        })
    }

    private fun setupAddPlantButton() {
        addPlant.setOnClickListener {
            val intent = Intent(requireContext(), CreateLocation::class.java)
            startActivity(intent)
        }
    }

    private fun filterAndDisplay(query: String = searchView.query.toString()) {
        val filtered = allLocations.filter { location ->
            (activeType == null || location.type.equals(activeType, ignoreCase = true)) &&
                    (query.isEmpty() || location.name.contains(query, ignoreCase = true))
        }

        listRops.removeAllViews()
        filtered.forEach { addLocationCard(it) }
    }

    private fun addLocationCard(location: Location) {
        val inflater = LayoutInflater.from(requireContext())
        val cardView = inflater.inflate(R.layout.location_card, listRops, false)

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

        cardView.setOnClickListener { selectLocation(location) }
        listRops.addView(cardView)
    }

    private fun selectLocation(location: Location) {
        val bundle = Bundle().apply {
            putInt("locationId", location.locID)
            putString("locationName", location.name)
            putDouble("locationArea", location.area ?: 0.0)
            putString("selected_crop_name", selectedCropName)
            putString("entered_name", enteredName)
            putInt("selected_crop_id", selectedCropId ?: -1)
            putInt("currentUserId", currentUserId)
        }

        val fragment = ChoiceNameFragment().apply { arguments = bundle }
        parentFragmentManager.beginTransaction()
            .replace(R.id.createSowingContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}
