package com.example.climatedata.fragment_createLocation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.climatedata.R

class ChoiceSoilLocation : Fragment() {

    private var enteredName: String? = null
    private var selectedType: String? = null

    private var selectedRegion: String? = null
    private var selectedCity: String? = null



    private lateinit var spinnerSoil: Spinner
    private lateinit var editTextArea: EditText
    private lateinit var switchDefault: Switch
    private lateinit var buttonNext: Button

    private val soilTypes = listOf(
        "тип почвы",
        "Аллювиальные (речных долин) почвы",
        "Дерново-подзолистые почвы",
        "Лёсовые почвы",
        "Песчаные почвы",
        "Подзолистые почвы",
        "Серозёмы",
        "Серые лесные почвы",
        "Солонцы",
        "Супесчаные и суглинистые почвы",
        "Торфяные почвы (болотные)",
        "Чернозём типичный",
        "Чернозём южный (каштановый)"
    )

    private var defaultSoil = "Дерново-подзолистые почвы"
    private var defaultArea = 1.0

    private var selectedSoil: String? = null
    private var areaValue: Double? = null
    private var switchChecked: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_choice_soil_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        enteredName = arguments?.getString("name")
        selectedType = arguments?.getString("type")
        selectedRegion = arguments?.getString("region")
        selectedCity = arguments?.getString("city")

        spinnerSoil = view.findViewById(R.id.spinnerSoil)
        editTextArea = view.findViewById(R.id.editTextArea)
        switchDefault = view.findViewById(R.id.switchDefault)
        buttonNext = view.findViewById(R.id.buttonNext)

        setupSpinner()
        setupEditText()
        setupSwitch()
        setupButton()
        restoreState()
    }

    private fun setupSpinner() {
        val adapter = object : ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_item,
            soilTypes
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

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSoil.adapter = adapter
        spinnerSoil.setSelection(0)

        spinnerSoil.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedSoil = if (position != 0) soilTypes[position] else null
                updateButtonState()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedSoil = null
                updateButtonState()
            }
        }
    }

    private fun setupEditText() {
        editTextArea.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                areaValue = s.toString().toDoubleOrNull()
                updateButtonState()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupSwitch() {
        switchDefault.setOnCheckedChangeListener { _, isChecked ->
            switchChecked = isChecked
            if (isChecked) {
                selectedSoil = defaultSoil
                areaValue = defaultArea
                spinnerSoil.setSelection(soilTypes.indexOf(defaultSoil))
                editTextArea.setText(defaultArea.toString())
                spinnerSoil.isEnabled = false
                editTextArea.isEnabled = false
            } else {
                spinnerSoil.isEnabled = true
                editTextArea.isEnabled = true
                selectedSoil = null
                areaValue = null
                spinnerSoil.setSelection(0)
                editTextArea.text.clear()
            }
            updateButtonState()
        }
    }

    private fun setupButton() {
        buttonNext.setOnClickListener {
            val bundle = Bundle().apply {
                putString("name", enteredName)         // имя участка
                putString("type", selectedType)        // тип участка
                putString("region", selectedRegion)    // регион
                putString("city", selectedCity)        // город
                putString("soilType", selectedSoil)    // тип почвы
                putDouble("area", areaValue ?: 0.0)    // площадь
                putBoolean("switchChecked", switchChecked)
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.createLocationContainer, FinishCreateLocation().apply { arguments = bundle })
                .addToBackStack(null)
                .commit()
        }
    }

    private fun updateButtonState() {
        val isReady = selectedSoil != null && areaValue != null
        buttonNext.isEnabled = isReady
        val color = if (isReady) R.color.black else R.color.gray4
        buttonNext.backgroundTintList = ContextCompat.getColorStateList(requireContext(), color)
    }

    private fun restoreState() {
        arguments?.getString("soilType")?.let {
            selectedSoil = it
            spinnerSoil.setSelection(soilTypes.indexOf(it))
        }

        arguments?.getDouble("area")?.let {
            areaValue = it
            editTextArea.setText(it.toString())
        }

        arguments?.getBoolean("switchChecked")?.let {
            switchChecked = it
            switchDefault.isChecked = it
            if (it) {
                spinnerSoil.isEnabled = false
                editTextArea.isEnabled = false
            }
        }

        updateButtonState()
    }
}
