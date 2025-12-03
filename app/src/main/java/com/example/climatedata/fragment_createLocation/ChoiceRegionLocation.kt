package com.example.climatedata.fragment_createLocation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.climatedata.R
import com.example.climatedata.data.RegionCityData
import com.example.climatedata.data.db.ClimateDatabase

class ChoiceRegionLocation : Fragment() {


    private var enteredName: String? = null
    private var selectedType: String? = null
    private lateinit var spinnerRegion: Spinner
    private lateinit var spinnerCity: Spinner
    private lateinit var switchDefault: Switch
    private lateinit var buttonContinue: Button

    private lateinit var db: ClimateDatabase
    private var currentUserEmail: String = "user@example.com"

    private var selectedRegion: String? = null
    private var selectedCity: String? = null
    private var switchChecked: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_choice_region_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enteredName = arguments?.getString("name")
        selectedType = arguments?.getString("type")

        db = ClimateDatabase.getInstance(requireContext())

        spinnerRegion = view.findViewById(R.id.spinner_region)
        spinnerCity = view.findViewById(R.id.spinner_city)
        switchDefault = view.findViewById(R.id.switch1)
        buttonContinue = view.findViewById(R.id.all)

        buttonContinue.isEnabled = false

        // Восстанавливаем значения, если пришли через аргументы
        selectedRegion = arguments?.getString("region")
        selectedCity = arguments?.getString("city")



        setupSpinners()
        setupSwitch()
        setupButton()
        restoreSelection()
    }

    private fun setupSpinners() {
        val regionsWithHint = listOf("регион") + RegionCityData.regions
        val regionAdapter = object : ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_item,
            regionsWithHint
        ) {
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.setTextColor(
                    if (position == 0) ContextCompat.getColor(context, R.color.gray2)
                    else ContextCompat.getColor(context, R.color.black)
                )
                return view
            }

            override fun isEnabled(position: Int) = position != 0
        }
        spinnerRegion.adapter = regionAdapter

        spinnerCity.adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, listOf("город"))
        spinnerCity.isEnabled = false

        spinnerRegion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedRegion = if (position == 0) {
                    spinnerCity.adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, listOf("город"))
                    spinnerCity.isEnabled = false
                    null
                } else {
                    val region = RegionCityData.regions[position - 1]
                    val cities = RegionCityData.citiesByRegion[region] ?: emptyList()
                    val cityAdapter = object : ArrayAdapter<String>(
                        requireContext(),
                        R.layout.spinner_item,
                        listOf("город") + cities
                    ) {
                        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                            val view = super.getDropDownView(position, convertView, parent) as TextView
                            view.setTextColor(
                                if (position == 0) ContextCompat.getColor(context, R.color.gray2)
                                else ContextCompat.getColor(context, R.color.black)
                            )
                            return view
                        }

                        override fun isEnabled(position: Int) = position != 0
                    }
                    spinnerCity.adapter = cityAdapter
                    spinnerCity.isEnabled = true
                    region
                }
                checkEnableButton()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedRegion = null
                checkEnableButton()
            }
        }

        spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedCity = if (position == 0) null else spinnerCity.selectedItem.toString()
                checkEnableButton()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedCity = null
                checkEnableButton()
            }
        }
    }

    private fun setupSwitch() {
        switchDefault.setOnCheckedChangeListener { _, isChecked ->
            switchChecked = isChecked
            if (isChecked && selectedRegion != null && selectedCity != null) {
                val user = db.getUserByEmail(currentUserEmail)
                user?.apply {
                    this.city = selectedCity!!
                    db.updateUser(this)
                }
                Toast.makeText(requireContext(), "Регион и город сохранены как значения по умолчанию", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupButton() {
        buttonContinue.setOnClickListener {
            val user = db.getUserByEmail(currentUserEmail)
            user?.apply {
                this.city = selectedCity ?: this.city
                db.updateUser(this)
            }

            val bundle = Bundle().apply {
                putString("name", enteredName)
                putString("type", selectedType)
                putString("region", selectedRegion)
                putString("city", selectedCity)
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.createLocationContainer, ChoiceSoilLocation().apply { arguments = bundle })
                .addToBackStack(null)
                .commit()

        }
    }

    private fun checkEnableButton() {
        val isReady = selectedRegion != null && selectedCity != null
        buttonContinue.isEnabled = isReady
        val color = if (isReady) R.color.black else R.color.gray4
        buttonContinue.backgroundTintList = ContextCompat.getColorStateList(requireContext(), color)
    }

    private fun restoreSelection() {
        // Восстанавливаем регион
        selectedRegion?.let { region ->
            val index = RegionCityData.regions.indexOf(region) + 1
            if (index > 0 && index < spinnerRegion.count) {
                spinnerRegion.setSelection(index)
            }
        }

        // Восстанавливаем город
        if (!selectedRegion.isNullOrEmpty() && !selectedCity.isNullOrEmpty()) {
            val cities = RegionCityData.citiesByRegion[selectedRegion] ?: emptyList()
            val cityIndex = cities.indexOf(selectedCity) + 1
            if (cityIndex > 0 && cityIndex < spinnerCity.count) {
                spinnerCity.setSelection(cityIndex)
            }
        }

        // Восстанавливаем switch
        switchDefault.isChecked = switchChecked

        checkEnableButton()
    }
}
