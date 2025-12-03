package com.example.climatedata

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.climatedata.data.authen.AuthenticationManager
import com.example.climatedata.data.db.ClimateDatabase
import com.example.climatedata.data.models.Planting
import com.example.climatedata.fragment_createSowing.CreateSowingActivity
import com.example.climatedata.fragment_createSowing.SpecificCardPlant
import com.example.climatedata.fragment_homeActivity.PlantAdapter
import com.example.climatedata.utils.SwipeToDeleteCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlantFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlantAdapter
    private lateinit var db: ClimateDatabase
    private var userId: Int = 0

    private var currentStatus: String = "active"
    private lateinit var searchView: SearchView
    private lateinit var resetFilter: TextView


    private var allCrops: List<Planting> = emptyList()
    private var activeCategory: String? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_plant, container, false)


        val activityButton = view.findViewById<Button>(R.id.activity)
        val archiveButton = view.findViewById<Button>(R.id.arhive)

        recyclerView = view.findViewById(R.id.recyclerPlantings)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        searchView = view.findViewById(R.id.searchView)
        resetFilter = view.findViewById(R.id.textView3) // можешь поменять на отдельную кнопку "сброс"

        // DB
        db = ClimateDatabase.getInstance(requireContext())

        val authManager = AuthenticationManager(requireContext())
        val currentUserId = authManager.getCurrentUserId()
        if (currentUserId == -1L) {
            Log.d("PlantFragment", "Пользователь не найден")
            userId = 0
        } else {
            userId = currentUserId.toInt()
            Log.d("PlantFragment", "Текущий userId = $userId")
        }

        // Настройка адаптера
        adapter = PlantAdapter(
            requireContext(),
            mutableListOf(),
            getCropName = { id -> db.getCropById(id)?.name ?: "Неизвестная культура" },
            getLocationName = { id -> db.getLocationById(id)?.name ?: "Неизвестная локация" },
            onDelete = { planting -> deletePlanting(planting) },
            onItemClick = { selectedPlanting ->
                val fragment = SpecificCardPlant.newInstance(selectedPlanting.planID)
                fragment.setOnPlantCompletedListener { completedPlanting ->
                    adapter.markAsArchived(completedPlanting) // добавляем карточку в архив
                }

                parentFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, SpecificCardPlant.newInstance(selectedPlanting.planID))
                    .addToBackStack(null)
                    .commit()
            }

        )
        recyclerView.adapter = adapter

        // 🔹 Обработчики кнопок Активные / Архив
        val statusButtons = mapOf(
            activityButton to "active",
            archiveButton to "archive"
        )

        statusButtons.forEach { (button, status) ->
            button.setOnClickListener {
                updateStatusButtonStates(statusButtons, button)
                filterByStatus(button, status)
            }
        }

        allCrops = db.getPlantingsByUser(userId)
        // 🔹 По умолчанию — активные посадки и подсветка кнопки
        updateStatusButtonStates(statusButtons, activityButton)
        filterByStatus(activityButton, "active")



        activityButton.setOnClickListener {
            currentStatus = "active"
            filterByStatus(activityButton, currentStatus)
        }

        archiveButton.setOnClickListener {
            currentStatus = "archive"
            filterByStatus(archiveButton, currentStatus)
        }

        // Swipe to delete
        val swipeCallback = SwipeToDeleteCallback(requireContext(), adapter)
        ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView)

        // Кнопки фильтров по категориям
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

        buttons.forEach { (id, category) ->
            val button = view.findViewById<Button>(id)
            button.setOnClickListener {
                activeCategory = category
                updateButtonStates(buttons, button, view)
                filterAndDisplay()
            }
        }

        // Подсветка "Все" при запуске
        val allButton = view.findViewById<Button>(R.id.all)
        updateButtonStates(buttons, allButton, view)


        setupSearch()
        setupResetFilter()
        loadPlantings("active")


        val addPlantIcon: ImageView = view.findViewById(R.id.addPlant)
        addPlantIcon.setOnClickListener {
            startActivity(Intent(requireContext(), CreateSowingActivity::class.java))
        }

        super.onViewCreated(view, savedInstanceState)

        return view

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
    }
    private fun checkEmptyPlantings(container: ViewGroup) {
        // Фильтруем по текущему пользователю
        val userPlantings = allCrops.filter { it.userID == userId }

        if (userPlantings.isEmpty()) {
            recyclerView.visibility = View.GONE
            if (container.findViewById<View>(R.id.item_empty_list_plant) == null) {
                val emptyCard = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_empty_list_plant, container, false)
                container.addView(emptyCard)
            }
        } else {
            recyclerView.visibility = View.VISIBLE
            container.findViewById<View>(R.id.item_empty_list_plant)?.let { container.removeView(it) }
        }
    }

    private fun filterByStatus(activeButton: Button?, status: String?) {
        Log.d("PlantFragment", "filterByStatus вызвано: status=$status, allCrops.size=${allCrops.size}")
        // Подсветка кнопок
        val activityButton = view?.findViewById<Button>(R.id.activity)
        val archiveButton = view?.findViewById<Button>(R.id.arhive)

        listOf(activityButton, archiveButton).forEach { btn ->
            if (btn == activeButton) {
                btn?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
                btn?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            } else {
                btn?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray4))
                btn?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray2))
            }
        }

        if (allCrops.isEmpty()) {
            Log.d("PlantFragment", "allCrops пустой, данные еще не загружены")
            return
        }

        // Фильтрация данных
        val filtered = allCrops.filter { planting ->
            val match = when (status?.lowercase()) {
                "active" -> planting.status.equals("active", ignoreCase = true)
                "archive" -> planting.status.equals("archive", ignoreCase = true)
                else -> false
            }
            Log.d("PlantFragment", "PlantID=${planting.planID}, status=${planting.status}, match=$match")
            match
        }

        Log.d("PlantFragment", "filtered.size=${filtered.size}")
        displayCrops(filtered)
    }

    private fun filterAndDisplay(searchText: String? = null) {
        var filtered = allCrops

        // Фильтр по категории
        activeCategory?.let { category ->
            filtered = filtered.filter { planting ->
                val cropCategory = db.getCropById(planting.cropID)?.category ?: ""
                cropCategory.equals(category, ignoreCase = true) || cropCategory.contains(category, ignoreCase = true)
            }
        }

        // Фильтр по поиску
        searchText?.takeIf { it.isNotBlank() }?.let { query ->
            filtered = filtered.filter { planting ->
                val cropName = db.getCropById(planting.cropID)?.name ?: ""
                cropName.contains(query, ignoreCase = true)
            }
        }

        displayCrops(filtered)
    }

    // 🔹 Отображение списка в адаптере
    private fun displayCrops(crops: List<Planting>) {
        adapter.updateData(crops)
    }



    private fun loadPlantings(status: String) {
        val allPlantings = db.getPlantingsByUser(userId) // возвращает List<Planting>
        val filtered = allPlantings.filter { it.status == status }
        adapter.updateData(filtered)

    }


    private fun deletePlanting(planting: Planting) {
        CoroutineScope(Dispatchers.IO).launch {
            db.deletePlanting(planting.planID)
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Посев удалён", Toast.LENGTH_SHORT).show()
                loadPlantings(currentStatus)
            }
        }
    }


    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterAndDisplay(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterAndDisplay(newText)
                return true
            }
        })
    }

    // 🔹 Сброс фильтров
    private fun setupResetFilter() {
        resetFilter.setOnClickListener {
            activeCategory = null
            searchView.setQuery("", false)
            displayCrops(allCrops)
        }
    }
    private fun updateStatusButtonStates(buttons: Map<Button, String>, active: Button) {
        buttons.keys.forEach { button ->
            if (button == active) {
                button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
                button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            } else {
                button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray4))
                button.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray2))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("PlantFragment", "onResume - обновляем активные посадки по пользователю")

        // Загружаем все посадки пользователя
        allCrops = db.getPlantingsByUser(userId)

        // Фильтруем только активные
        val activePlantings = allCrops.filter { it.status.equals("active", ignoreCase = true) }

        // Обновляем адаптер
        adapter.updateData(activePlantings)
    }




}
