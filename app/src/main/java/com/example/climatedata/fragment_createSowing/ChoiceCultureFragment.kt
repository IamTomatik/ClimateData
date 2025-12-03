package com.example.climatedata.fragment_createSowing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.climatedata.R
import com.example.climatedata.additional_lists.PlantExecutedDb
import com.example.climatedata.data.db.ClimateDatabase
import com.example.climatedata.data.models.Crop

class ChoiceCultureFragment : Fragment() {

    private lateinit var db: ClimateDatabase
    private lateinit var allCrops: List<Crop>
    private lateinit var listRops: LinearLayout
    private lateinit var searchView: SearchView
    private var activeCategory: String? = null
    private var selectedTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = ClimateDatabase.getInstance(requireContext())
        PlantExecutedDb.seedCrops(db)
        allCrops = db.getAllCrops()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_choice_culture, container, false)

        listRops = view.findViewById(R.id.list_rops)
        searchView = view.findViewById(R.id.searchView)

        // Кнопки фильтров
        val buttons = mapOf(
            R.id.all to null,
            R.id.cereals to "Зерновые",
            R.id.root_vegetables to "Корнеплоды",
            R.id.vegetable to "Овощи",
            R.id.berry to "Ягоды",
            R.id.legumes to "Бобовые",
            R.id.oilseeds to "Масличные",
            R.id.floral to "Цветы",
            R.id.feed to "Кормовые",
            R.id.technical to "Технические"
        )

        // Настройка кнопок и обработчиков клика
        buttons.forEach { (id, category) ->
            val button = view.findViewById<Button>(id)
            button.isEnabled = true
            button.setOnClickListener {
                activeCategory = category // null для "Все"
                updateButtonStates(buttons, button, view)
                filterAndDisplay()
            }
        }

        // Поиск
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterAndDisplay(newText)
                return true
            }
        })

        // Изначально подсвечиваем "Все" и отображаем все культуры
        val allButton = view.findViewById<Button>(R.id.all)
        updateButtonStates(buttons, allButton, view)
        displayCrops(allCrops)

        return view
    }

    // Обновляем состояние кнопок (подсветка активной)
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
    }

    // Фильтрация и отображение
    private fun filterAndDisplay(searchText: String? = null) {
        var filtered = allCrops

        // Фильтр по категории
        activeCategory?.let { category ->
            filtered = filtered.filter { crop ->
                crop.category.equals(category, ignoreCase = true) ||
                        crop.category.contains(category, ignoreCase = true)
            }
        }

        // Фильтр по поиску
        searchText?.takeIf { it.isNotBlank() }?.let { query ->
            filtered = filtered.filter { it.name.contains(query, ignoreCase = true) }
        }

        displayCrops(filtered)
    }

    // Отображение культур с возможностью выбора
    private fun displayCrops(crops: List<Crop>) {
        listRops.removeAllViews()

        crops.forEach { crop ->
            val textView = TextView(requireContext()).apply {
                text = crop.name
                textSize = 16f
                setPadding(16, 16, 16, 16)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                typeface = ResourcesCompat.getFont(requireContext(), R.font.cygre_regular)
                setOnClickListener { selectCrop(this, crop) }
            }
            listRops.addView(textView)
        }
    }

    // Метод выбора культуры
    private fun selectCrop(textView: TextView, crop: Crop) {
        // Снимаем подсветку с предыдущего выбора
        selectedTextView?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        selectedTextView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

        // Подсвечиваем текущий выбор
        textView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        selectedTextView = textView

        // Передаем выбранную культуру в другой фрагмент
        val bundle = Bundle().apply {
            putString("selected_crop_name", crop.name)
            putInt("selected_crop_id", crop.cropID)
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.createSowingContainer, ChoiceLocationFragment().apply { arguments = bundle })
            .addToBackStack(null)
            .commit()
    }
}
