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

class ChoiceNameLocation : Fragment() {

    private lateinit var spinner: Spinner
    private lateinit var editText: EditText
    private lateinit var buttonNext: Button

    private var selectedType: String? = null
    private var enteredName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_choice_name_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spinner = view.findViewById(R.id.spinner)
        editText = view.findViewById(R.id.editTextText)
        buttonNext = view.findViewById(R.id.all)

        // Список типов участков
        val types = listOf("тип участка", "поле", "теплица", "грядка",  "другое")

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item,
            types
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Изменение выбранного типа
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedType = if (position != 0) types[position] else null
                updateButtonState()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Изменение текста в поле
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                enteredName = s?.toString()?.trim()
                updateButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Обработчик кнопки "продолжить"
        buttonNext.setOnClickListener {
            val bundle = Bundle().apply {
                putString("name", enteredName)      // имя участка
                putString("type", selectedType)     // тип участка
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.createLocationContainer, ChoiceRegionLocation().apply { arguments = bundle })
                .addToBackStack(null)
                .commit()
        }
    }

    private fun updateButtonState() {
        val isReady = !selectedType.isNullOrEmpty() && !enteredName.isNullOrEmpty()
        buttonNext.isEnabled = isReady
        val color = if (isReady) R.color.black else R.color.gray4
        buttonNext.backgroundTintList = ContextCompat.getColorStateList(requireContext(), color)
    }
}
