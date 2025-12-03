package com.example.climatedata


import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.climatedata.data.db.ClimateDatabase
import com.example.climatedata.data.repository.WeatherRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.climatedata.data.models.Planting
import com.example.climatedata.fragment_createSowing.SpecificCardPlant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private lateinit var db: ClimateDatabase
    private var userId: Int = 0
    private lateinit var temperatureText: TextView
    private lateinit var humidityText: TextView
    private lateinit var windText: TextView
    private lateinit var pressureText: TextView
    private lateinit var precipitationText: TextView
    private lateinit var monthText: TextView
    private lateinit var dateText: TextView
    private lateinit var citiName: TextView
    private lateinit var containerLayout: LinearLayout
    private var currentPlantings: List<Planting> = emptyList()

    private lateinit var dayWeekText : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        containerLayout = view.findViewById(R.id.list_card)

        db = ClimateDatabase.getInstance(requireContext())

        temperatureText = view.findViewById(R.id.gradus)
        humidityText = view.findViewById(R.id.humidity)
        windText = view.findViewById(R.id.wind)
        pressureText = view.findViewById(R.id.pressure)
        precipitationText = view.findViewById(R.id.precipitation)

        dayWeekText  = view.findViewById(R.id.dayWeek)
        dateText = view.findViewById(R.id.dateTextView)

        citiName = view.findViewById(R.id.citi_name)

        // --- получаем текущего пользователя ---
        val auth = com.example.climatedata.data.authen.AuthenticationManager(requireContext())
        val currentUserId = auth.getCurrentUserId()
        userId = if (currentUserId == -1L) 0 else currentUserId.toInt()



        loadActivePlantings()
        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Показываем текущую дату
        val currentDate = Date()

        // формат для дня и месяца
        val dateFormatter = SimpleDateFormat("d MMMM", Locale("ru"))
        dateText.text = dateFormatter.format(currentDate)  // 23 Ноября

        // формат для дня недели
        val dayOfWeekFormatter = SimpleDateFormat("EEEE", Locale("ru"))
        dayWeekText.text = dayOfWeekFormatter.format(currentDate)
            .replaceFirstChar { it.uppercase() }  // первая буква заглавная



        val db = ClimateDatabase.getInstance(requireContext())
        val repository = WeatherRepository(db)


        val lat = 51.67
        val lon = 39.18
        val locID = 1

        val citi = citiName.getText().toString()
        val apiKey ="6bda2f46993185c9e5183b5c8bc0b585"
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=$apiKey&units=metric&lang=ru"


        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Получаем данные с API и сохраняем их в базу
                repository.fetchAndSaveWeather(lat, lon, locID, apiKey)

                // Берем последнюю сохраненную запись
                val data = repository.getLatestWeather(locID)

                data?.let {
                    temperatureText.text = "${it.temperature.toInt()}°"
                    humidityText.text = "${it.humidity.toInt()}"
                    windText.text = "${it.windSpeed}"
                    pressureText.text = "${it.pressure.toInt()} "
                    // Осадки без разделения на дождь и снег
                    precipitationText.text = "${it.precipitation}"
                } ?: run {
                    Log.e("HomeFragment", "Нет данных о погоде")
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "Ошибка обновления погоды", e)
            }
        }

        val btnSort = view.findViewById<LinearLayout>(R.id.btn_sort)
        val sortText = view.findViewById<TextView>(R.id.Text)


        sortText.text = "сначала новые"
        sortPlantings(1)

        btnSort.setOnClickListener {
            showSortDialog { selectedText: String, index: Int ->
                sortText.text = selectedText.lowercase(Locale("ru"))  // всегда маленькие буквы
                sortPlantings(index)
            }
        }

    }


    private fun loadActivePlantings() {
        lifecycleScope.launch {
            Log.d("HomeFragment", "Загрузка активных посадок для пользователя $userId")
            val plantings = withContext(Dispatchers.IO) { db.getPlantingsByUser(userId) }
            containerLayout.removeAllViews()

            val today = Date()
            val activePlantings = plantings.filter {
                it.status == "active" && Date(it.expectedHarvestDate).after(today)
            }
            currentPlantings = activePlantings

            if (activePlantings.isEmpty()) {
                val emptyCard = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_empty_list_plant, containerLayout, false)
                containerLayout.addView(emptyCard)
                return@launch
            }

            for (plant in activePlantings) {
                Log.d("HomeFragment", "Создаём карточку для посадки: ${plant.name}, planID=${plant.planID}")
                val card = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_home_plant_card, containerLayout, false)

                val container = card.findViewById<LinearLayout>(R.id.card_home_plant_image)

                val plantName = card.findViewById<TextView>(R.id.plantName)
                val plantStatus = card.findViewById<TextView>(R.id.plantStatus)

                plantName.text = plant.name.ifEmpty { "Моя посадка" }
                plantStatus.text = "Активная"

                // --- вставка фото посадки ---
                if (!plant.imageUri.isNullOrEmpty()) {
                    Log.d("HomeFragment", "Есть фото: ${plant.imageUri}")
                    try {
                        val imageView = ImageView(requireContext())
                        imageView.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                        )
                        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                        val bitmap = BitmapFactory.decodeFile(plant.imageUri)
                        imageView.setImageBitmap(bitmap)

                        container.removeAllViews() // удаляем внутренний LinearLayout с иконкой и текстом
                        container.addView(imageView)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // если ошибка, оставляем твой макет с иконкой и текстом
                    }
                }
                // если фото нет — оставляем исходный макет с иконкой и текстом

                // клик на карточку
                card.setOnClickListener {
                    val fragment = SpecificCardPlant.newInstance(plant.planID) // <- используем newInstance
                    requireActivity().supportFragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.frame_layout, fragment)
                        .addToBackStack(null)
                        .commit()
                }

                containerLayout.addView(card)
            }
        }
    }

    private fun showSortDialog(onSelect: (String, Int) -> Unit) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.item_alert_homefragment, null)
        val dialog = android.app.Dialog(requireContext())
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroup)

        // Обновляем текст кнопок
        dialogView.findViewById<RadioButton>(R.id.radio_star).text = "Сначала старые"
        dialogView.findViewById<RadioButton>(R.id.radio_new).text = "Сначала новые"
        dialogView.findViewById<RadioButton>(R.id.radio1).text = "От А до Я"

        // Добавим 4-й вариант — Я→А
        val radioReverse = RadioButton(requireContext())
        radioReverse.id = View.generateViewId()
        radioReverse.text = "От Я до А"
        radioGroup.addView(radioReverse)

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_star  -> onSelect("сначала старые", 0)
                R.id.radio_new   -> onSelect("сначала новые", 1)
                R.id.radio1      -> onSelect("От А до Я", 2)
                radioReverse.id  -> onSelect("От Я до А", 3)
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun sortPlantings(index: Int) {
        Log.d("HomeFragment", "Сортировка index = $index, текущие посадки = ${currentPlantings.map { it.name }}")
        val sorted = when (index) {
            0 -> currentPlantings.sortedBy { it.plantedDate }           // сначала старые
            1 -> currentPlantings.sortedByDescending { it.plantedDate } // сначала новые
            2 -> currentPlantings.sortedBy { it.name.lowercase(Locale("ru")) } // А→Я
            3 -> currentPlantings.sortedByDescending { it.name.lowercase(Locale("ru")) } // Я→А
            else -> currentPlantings
        }
        Log.d("HomeFragment", "После сортировки: ${sorted.map { it.name }}")
        displayPlantings(sorted)
    }


    private fun displayPlantings(plantings: List<Planting>) {
        containerLayout.removeAllViews()

        if (plantings.isEmpty()) {
            val emptyCard = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_empty_list_plant, containerLayout, false)
            containerLayout.addView(emptyCard)
            return
        }

        for (plant in plantings) {
            val card = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_home_plant_card, containerLayout, false)

            val container = card.findViewById<LinearLayout>(R.id.card_home_plant_image)
            val plantName = card.findViewById<TextView>(R.id.plantName)
            val plantStatus = card.findViewById<TextView>(R.id.plantStatus)

            plantName.text = plant.name.ifEmpty { "Моя посадка" }


            if (!plant.imageUri.isNullOrEmpty()) {
                try {
                    val imageView = ImageView(requireContext())
                    imageView.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    val bitmap = BitmapFactory.decodeFile(plant.imageUri)
                    imageView.setImageBitmap(bitmap)

                    container.removeAllViews()
                    container.addView(imageView)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            card.setOnClickListener {
                val fragment = SpecificCardPlant.newInstance(plant.planID)
                requireActivity().supportFragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.frame_layout, fragment)
                    .addToBackStack(null)
                    .commit()
            }

            containerLayout.addView(card)
        }
    }



}
