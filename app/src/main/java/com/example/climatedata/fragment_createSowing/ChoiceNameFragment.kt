package com.example.climatedata.fragment_createSowing

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.climatedata.R

class ChoiceNameFragment : Fragment() {

    private var selectedCropName: String? = null
    private lateinit var textCulture: TextView
    private lateinit var editName: EditText
    private lateinit var continueButton: Button

    private  var selectedCropId: Int? = 0
    private var locationId: Int? = null

    private var locationName: String? = null
    private var locationArea: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            locationId = args.getInt("locationId", -1).takeIf { it != -1 }
            selectedCropName = args.getString("selected_crop_name")
            selectedCropId = args.getInt("selected_crop_id")
            locationName = args.getString("locationName")
            locationArea = args.getDouble("locationArea", 0.0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_choice_name, container, false)

        textCulture = view.findViewById(R.id.textCulture)
        editName = view.findViewById(R.id.editName)
        continueButton = view.findViewById(R.id.all)

        // Показываем выбранную культуру
        textCulture.text = selectedCropName ?: "Не выбрано"

        // Кнопка изначально неактивна
        continueButton.isEnabled = false
        continueButton.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.gray1)

        // Следим за вводом текста
        editName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val isNotEmpty = s?.isNotBlank() == true
                continueButton.isEnabled = isNotEmpty
                continueButton.backgroundTintList = ContextCompat.getColorStateList(
                    requireContext(),
                    if (isNotEmpty) R.color.black else R.color.gray1
                )
            }
        })

        // Скрытие клавиатуры при нажатии "галочки" на клавиатуре
        editName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                editName.clearFocus()
                true
            } else false
        }

        // Скрытие клавиатуры при нажатии на фон (но не на EditText)
        view.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val x = event.rawX
                val y = event.rawY

                // Проверяем, кликнули ли именно по EditText
                val editCoords = IntArray(2)
                editName.getLocationOnScreen(editCoords)
                val editLeft = editCoords[0]
                val editTop = editCoords[1]
                val editRight = editLeft + editName.width
                val editBottom = editTop + editName.height

                val isInsideEditText =
                    x in editLeft.toFloat()..editRight.toFloat() && y in editTop.toFloat()..editBottom.toFloat()

                if (!isInsideEditText && editName.isFocused) {
                    editName.clearFocus()
                    hideKeyboard()
                }
            }
            false
        }

        // Обработчик кнопки "продолжить"
        continueButton.setOnClickListener {
            val enteredName = editName.text.toString().trim()
            val bundle = Bundle().apply {
                putString("selected_crop_name", selectedCropName)
                putString("entered_name", enteredName)
                putString("locationName", locationName)
                putDouble("locationArea", locationArea)
                putInt("selected_crop_id", selectedCropId ?: -1)
                locationId?.let { putInt("locationId", it)
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.createSowingContainer, LandingDateFragment().apply { arguments = bundle })
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus ?: View(requireContext())
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
