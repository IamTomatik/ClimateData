package com.example.climatedata.fragment_createSowing

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.climatedata.R
import com.example.climatedata.data.db.ClimateDatabase
import java.text.SimpleDateFormat
import java.util.*

class LandingDateFragment : Fragment() {

    private lateinit var editDateStart: EditText
    private lateinit var editDateEnd: EditText
    private lateinit var continueButton: Button

    private var selectedCropName: String? = null
    private var selectedCropId: Int? = null
    private var enteredName: String? = null
    private var growthDays: Int = 0
    private var locationName: String? = null
    private var locationArea: Double = 0.0

    private var locationId: Int? = null
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private var startCalendar = Calendar.getInstance()
    private var endCalendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = ClimateDatabase.getInstance(requireContext())

        arguments?.let { args ->
            selectedCropName = args.getString("selected_crop_name")
            selectedCropId = args.getInt("selected_crop_id")
            locationId = args.getInt("locationId", -1).takeIf { it != -1 }
            enteredName = args.getString("entered_name")
            locationName = args.getString("locationName")
            locationArea = args.getDouble("locationArea", 0.0)
        }


        // Берем данные культуры по ID, если есть
        val crop = selectedCropId?.let { db.getCropById(it) }
        growthDays = crop?.growthDays ?: 90 // дефолт, если не найдено
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_landing_date, container, false)
        editDateStart = view.findViewById(R.id.editDateStart)
        editDateEnd = view.findViewById(R.id.editDateEnd)
        continueButton = view.findViewById(R.id.all)

        // По умолчанию кнопка неактивна
        continueButton.isEnabled = false
        continueButton.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.gray1)

        // Устанавливаем сегодняшнюю дату в start
        val today = Calendar.getInstance()
        startCalendar.time = today.time
        editDateStart.setText(dateFormat.format(today.time))

        // Рассчитываем дату окончания
        updateEndDate()

        // При клике открываем DatePicker
        editDateStart.setOnClickListener { showDatePicker() }

        // Активируем кнопку
        continueButton.isEnabled = true
        continueButton.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.black)

        // При нажатии "Продолжить" передаём всё дальше
        continueButton.setOnClickListener {
            val bundle = Bundle().apply {
                putString("selected_crop_name", selectedCropName)
                putString("entered_name", enteredName)
                putString("start_date", editDateStart.text.toString())
                putString("end_date", editDateEnd.text.toString())
                putString("locationName", locationName)
                putDouble("locationArea", locationArea)
                selectedCropId?.let { putInt("selected_crop_id", it)
                    locationId?.let { putInt("locationId", it) }} // передаем ID дальше
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.createSowingContainer, ConfirmationFragment().apply { arguments = bundle })
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun showDatePicker() {
        val year = startCalendar.get(Calendar.YEAR)
        val month = startCalendar.get(Calendar.MONTH)
        val day = startCalendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(requireContext(), { _, y, m, d ->
            startCalendar.set(y, m, d)
            editDateStart.setText(dateFormat.format(startCalendar.time))
            updateEndDate()
        }, year, month, day)
        datePicker.show()
    }

    private fun updateEndDate() {
        endCalendar.time = startCalendar.time
        endCalendar.add(Calendar.DAY_OF_YEAR, growthDays)
        editDateEnd.setText(dateFormat.format(endCalendar.time))
    }
}
