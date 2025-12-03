package com.example.climatedata.fragment_createLocation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.climatedata.HomeActivity
import com.example.climatedata.R
import com.example.climatedata.data.authen.AuthenticationManager
import com.example.climatedata.data.db.ClimateDatabase
import com.example.climatedata.data.models.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FinishCreateLocation : Fragment() {

    private lateinit var db: ClimateDatabase
    private lateinit var authManager: AuthenticationManager

    private lateinit var spinnerType: Spinner
    private lateinit var spinnerSoil: Spinner
    private lateinit var editName: EditText
    private lateinit var editRegion: EditText
    private lateinit var editSquare: EditText
    private lateinit var buttonCreate: Button

    private var locationCreated = false // чтобы избежать двойного добавления


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

    private val types = listOf(
        "тип участка",
        "поле",
        "теплица",
        "грядка",
        "другое"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_finish_create_location, container, false)

        db = ClimateDatabase.getInstance(requireContext())
        authManager = AuthenticationManager(requireContext())

        spinnerType = view.findViewById(R.id.spinner_locationType)
        spinnerSoil = view.findViewById(R.id.spinnerSoil)
        editName = view.findViewById(R.id.locationName)
        editRegion = view.findViewById(R.id.locationRegion)
        editSquare = view.findViewById(R.id.locationSquare)
        buttonCreate = view.findViewById(R.id.all)

        setupSpinners()
        setupInitialValues()

        buttonCreate.setOnClickListener { createLocation() }

        return view
    }

    private fun setupSpinners() {

        val typeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = typeAdapter

        val soilAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, soilTypes)
        soilAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSoil.adapter = soilAdapter
    }

    private fun setupInitialValues() {
        arguments?.getString("name")?.let { editName.setText(it) }
        arguments?.getString("region")?.let { editRegion.setText(it) }
        arguments?.getDouble("area")?.let { editSquare.setText(it.toString()) }

        arguments?.getString("type")?.let {
            val index = (spinnerType.adapter as ArrayAdapter<String>).getPosition(it)
            if (index >= 0) spinnerType.setSelection(index)
        }
        arguments?.getString("soil")?.let {
            val index = (spinnerSoil.adapter as ArrayAdapter<String>).getPosition(it)
            if (index >= 0) spinnerSoil.setSelection(index)
        }
    }

    private fun createLocation() {
        if (locationCreated) return

        val name = editName.text.toString().trim()
        val region = editRegion.text.toString().trim()
        val areaText = editSquare.text.toString().trim()
        val type = spinnerType.selectedItem?.toString() ?: ""
        val soil = spinnerSoil.selectedItem?.toString() ?: ""

        if (name.isEmpty() || region.isEmpty() || areaText.isEmpty()) {
            Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        val area = areaText.toDoubleOrNull()
        if (area == null || area <= 0) {
            Toast.makeText(requireContext(), "Введите корректную площадь", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = authManager.getCurrentUserId()
        if (userId == -1L) {
            Toast.makeText(requireContext(), "Ошибка: пользователь не найден", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val location = Location(
                userID = userId.toInt(),
                name = name,
                type = type,
                region = region,
                soilType = soil,
                area = area
            )
            db.addLocation(location)

            locationCreated = true //  пометка, что локация создана

            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Локация успешно создана!", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), HomeActivity::class.java)
                startActivity(intent)

                // Закрываем текущую Activity, если фрагмент внутри Activity
                activity?.finish()
            }
        }
    }
}
