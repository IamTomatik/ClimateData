package com.example.climatedata.fragment_createSowing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.climatedata.R
import com.example.climatedata.data.authen.AuthenticationManager
import com.example.climatedata.data.db.ClimateDatabase
import com.example.climatedata.data.models.Planting
import java.text.SimpleDateFormat
import java.util.*

class ConfirmationFragment : Fragment() {

    private lateinit var plantName: EditText
    private lateinit var plantCategory: EditText
    private lateinit var plantDataStart: EditText
    private lateinit var plantDataEnd: EditText
    private lateinit var createButton: Button
    private lateinit var editArea: EditText

    private var locationName: String? = null
    private var locationArea: Double = 0.0
    private var locationId: Int? = null

    private var selectedCropName: String? = null
    private var selectedCropId: Int? = null
    private var enteredName: String? = null
    private var startDate: String? = null
    private var endDate: String? = null

    private val db by lazy { ClimateDatabase.getInstance(requireContext()) }
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedCropName = it.getString("selected_crop_name")
            selectedCropId = it.getInt("selected_crop_id")
            enteredName = it.getString("entered_name")
            startDate = it.getString("start_date")
            endDate = it.getString("end_date")
            locationName = it.getString("locationName")
            locationArea = it.getDouble("locationArea", 0.0)
            locationId = it.getInt("locationId", -1).takeIf { id -> id != -1 }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_confirmation, container, false)

        plantName = view.findViewById(R.id.plantName)
        plantCategory = view.findViewById(R.id.plantCategory)
        plantDataStart = view.findViewById(R.id.plantDataStart)
        plantDataEnd = view.findViewById(R.id.plantDataEnd)
        createButton = view.findViewById(R.id.all)
        editArea = view.findViewById(R.id.editArea)

        plantName.setText(enteredName ?: "")
        plantCategory.setText(selectedCropName ?: "")
        plantDataStart.setText(startDate ?: "")
        plantDataEnd.setText(endDate ?: "")
        if (!locationName.isNullOrEmpty()) {
            editArea.setText("$locationName (${locationArea} га)")
        }

        createButton.isEnabled = true
        createButton.setOnClickListener { savePlanting() }

        return view
    }

    private fun savePlanting() {
        if (selectedCropId == null || startDate.isNullOrEmpty() || endDate.isNullOrEmpty() || locationId == null) {
            Toast.makeText(requireContext(), "Заполните все поля и убедитесь, что есть локация", Toast.LENGTH_SHORT).show()
            return
        }

        val authManager = AuthenticationManager(requireContext())
        val currentUserId = authManager.getCurrentUserId()
        if (currentUserId == -1L) {
            Toast.makeText(requireContext(), "Ошибка: пользователь не найден", Toast.LENGTH_SHORT).show()
            return
        }


        val plantedDate = dateFormat.parse(startDate!!)!!.time
        val expectedHarvestDate = dateFormat.parse(endDate!!)!!.time

        val planting = Planting(
            locID = locationId!!,
            cropID = selectedCropId!!,
            userID = currentUserId.toInt(),
            plantedDate = plantedDate,
            expectedHarvestDate = expectedHarvestDate,
            area = locationArea,
            name = enteredName!!

        )

        db.addPlanting(planting)

        Toast.makeText(requireContext(), "Посев сохранен!", Toast.LENGTH_SHORT).show()

        parentFragmentManager.beginTransaction()
            .replace(R.id.createSowingContainer, CreationSuccessFragment())
            .addToBackStack(null)
            .commit()
    }
}
