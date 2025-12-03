package com.example.climatedata

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.climatedata.data.db.ClimateDatabase
import com.example.climatedata.data.models.Planting
import com.example.climatedata.data.models.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import com.example.climatedata.data.fake.FakeWeatherDataGenerator
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import android.graphics.Color
import android.util.Log
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IFillFormatter

class AnalysisFragment : Fragment() {

    private lateinit var choicePlant: LinearLayout
    private lateinit var choiceText: TextView
    private lateinit var containerLayout: LinearLayout
    private lateinit var informationScroll: ScrollView
    private lateinit var backButton: ImageView

    private lateinit var avTemperature: TextView
    private lateinit var avHumidityAir: TextView
    private lateinit var avPrecipitation: TextView
    private lateinit var avHumidityCarbon: TextView

    private lateinit var graphicLayout: LinearLayout

    // Показатели
    private lateinit var temperatureBtn: LinearLayout
    private lateinit var humidityAirBtn: LinearLayout
    private lateinit var precipitationBtn: LinearLayout
    private lateinit var humidityCarbonBtn: LinearLayout

    // Интервалы
    private lateinit var allTimeBtn: Button
    private lateinit var dayBtn: Button
    private lateinit var weekBtn: Button
    private lateinit var monthBtn: Button

    private var currentInterval: String = "month"
    private var currentChartType: String = "temperature"
    private var selectedPlanting: Planting? = null
    private var allCrops: List<Planting> = emptyList()
    private var userId: Int = 0
    private lateinit var db: ClimateDatabase
    private lateinit var dayWeather: List<WeatherData>
    private lateinit var namePlant: TextView
    companion object {
        fun newInstance(planID: Int? = null): AnalysisFragment {
            val fragment = AnalysisFragment()
            planID?.let {
                val args = Bundle()
                args.putInt("planID", it)
                fragment.arguments = args
            }
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_analysis, container, false)

        // Инициализация UI
        choicePlant = view.findViewById(R.id.choicePlant)
        choiceText = view.findViewById(R.id.choiceText)
        containerLayout = view.findViewById(R.id.recyclerViewPlants) // LinearLayout
        informationScroll = view.findViewById(R.id.information)
        backButton = view.findViewById(R.id.back)
        graphicLayout = view.findViewById(R.id.graphic)

        avTemperature = view.findViewById(R.id.avTemperature)
        avHumidityAir = view.findViewById(R.id.avHumidityAir)
        avPrecipitation = view.findViewById(R.id.avPrecipitation)
        avHumidityCarbon = view.findViewById(R.id.avHumidityCarbon)

        temperatureBtn = view.findViewById(R.id.temperatyre)
        humidityAirBtn = view.findViewById(R.id.humidityA)
        precipitationBtn = view.findViewById(R.id.precipitation)
        humidityCarbonBtn = view.findViewById(R.id.humidityC)

        allTimeBtn = view.findViewById(R.id.allTime)
        dayBtn = view.findViewById(R.id.day)
        weekBtn = view.findViewById(R.id.week)
        monthBtn = view.findViewById(R.id.month)

        namePlant = view.findViewById<TextView>(R.id.namePlant)
        backButton = view.findViewById(R.id.back)

        // Скрываем ScrollView, список и показываем кнопку
        informationScroll.visibility = View.GONE
        containerLayout.visibility = View.GONE
        choicePlant.visibility = View.VISIBLE
        backButton.visibility = View.GONE
        namePlant.visibility = View.GONE

        // Получаем базу и пользователя
        db = ClimateDatabase.getInstance(requireContext())
        val authManager = com.example.climatedata.data.authen.AuthenticationManager(requireContext())
        val currentUserId = authManager.getCurrentUserId() // возвращает Long
        if (currentUserId == -1L) {
            Toast.makeText(requireContext(), "Пользователь не найден", Toast.LENGTH_SHORT).show()
            userId = 0
        } else {
            userId = currentUserId.toInt()
        }

// Логируем для отладки
        lifecycleScope.launch(Dispatchers.IO) {
            val locationsCount = db.getLocationsByUser(userId).size
            withContext(Dispatchers.Main) {
                Log.d("AnalysisFragment", "Текущий userId = $userId, количество локаций = $locationsCount")
            }
        }

        val planID = arguments?.getInt("planID")


        if (planID == null) {
            // Нет выбранной посадки → показываем экран выбора
            informationScroll.visibility = View.GONE
            containerLayout.visibility = View.GONE
            choicePlant.visibility = View.VISIBLE
            loadPlantings() // список всех посадок
        } else {
            // Режим: детальный график выбранной посадки
            choicePlant.visibility = View.GONE
            informationScroll.visibility = View.VISIBLE
            containerLayout.visibility = View.VISIBLE

            lifecycleScope.launch {
                selectedPlanting = withContext(Dispatchers.IO) { db.getPlantingById(planID) }
                selectedPlanting?.let { onPlantSelected(it) }
            }
        }

        backButton.setOnClickListener {
            // Возврат к выбору посадки
            informationScroll.visibility = View.GONE
            choicePlant.visibility = View.VISIBLE
            containerLayout.removeAllViews()
            loadPlantings()
            selectedPlanting = null
            backButton.visibility = View.GONE
            namePlant.visibility = View.GONE
        }

        choicePlant.setOnClickListener { loadPlantings() }

        return view
    }

    private fun loadPlantings() {
        lifecycleScope.launch {
            allCrops = withContext(Dispatchers.IO) { db.getPlantingsByUser(userId) }

            containerLayout.removeAllViews() // очищаем контейнер

            if (allCrops.isEmpty()) {
                Toast.makeText(requireContext(), "Посадок нет", Toast.LENGTH_SHORT).show()
                val emptyCard = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_empty_list_plant, containerLayout, false)
                containerLayout.addView(emptyCard)
            } else {
                containerLayout.visibility = View.VISIBLE
                for (plant in allCrops) {
                    val card = LayoutInflater.from(requireContext())
                        .inflate(R.layout.item_plant_analysis, containerLayout, false)

                    val nameCrop = card.findViewById<TextView>(R.id.name_crop)
                    val daysPassed = card.findViewById<TextView>(R.id.days_passed)
                    val daysTotal = card.findViewById<TextView>(R.id.days_total)
                    val daysBeforeCollection =
                        card.findViewById<TextView>(R.id.days_before_collection)
                    val progressBar = card.findViewById<ProgressBar>(R.id.progressBar)

                    nameCrop.text = db.getCropById(plant.cropID)?.name ?: "Неизвестная культура"

                    val startDate = Date(plant.plantedDate)
                    val endDate = Date(plant.expectedHarvestDate)
                    val today = Date()

                    val totalDaysVal =
                        ((endDate.time - startDate.time) / (1000 * 60 * 60 * 24)).toInt()
                    val passedDaysVal =
                        ((today.time - startDate.time) / (1000 * 60 * 60 * 24)).toInt()
                            .coerceAtLeast(0)
                    val remainingDaysVal = (totalDaysVal - passedDaysVal).coerceAtLeast(0)

                    daysTotal.text = totalDaysVal.toString()
                    daysPassed.text = passedDaysVal.toString()
                    daysBeforeCollection.text = remainingDaysVal.toString()
                    progressBar.progress =
                        ((passedDaysVal.toFloat() / totalDaysVal) * 100).toInt().coerceIn(0, 100)

                    // При выборе карточки показываем ScrollView, но карточка остается
                    card.setOnClickListener {
                        onPlantSelected(plant) // plant — это объект Planting
                    }
                    containerLayout.addView(card)
                }
            }
        }
    }

    private fun onPlantSelected(plant: Planting) {
        selectedPlanting = plant

        // Показываем заголовок с названием посадки
        namePlant.visibility = View.VISIBLE
        namePlant.text = plant.name ?: "Посадка без имени"

        // Показываем ScrollView с информацией
        informationScroll.visibility = View.VISIBLE
        choicePlant.visibility = View.GONE
        backButton.visibility = View.VISIBLE

        // Очищаем контейнер и добавляем только выбранную карточку
        containerLayout.removeAllViews()
        val card = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_plant_analysis, containerLayout, false)
        containerLayout.addView(card)

        val nameCrop = card.findViewById<TextView>(R.id.name_crop)
        nameCrop.text = db.getCropById(plant.cropID)?.name ?: "Неизвестная культура"

        graphicLayout.removeAllViews()

        // Получаем список погодных данных за 30 дней
        val weatherList = FakeWeatherDataGenerator.generateFakeWeatherData(plant, 30) // 30 дней
       dayWeather = FakeWeatherDataGenerator.generateFakeWeatherDataForDay(plant, 8)


        // Средние показатели
        avTemperature.text = String.format("%.1f", weatherList.map { it.temperature }.average())
        avHumidityAir.text = weatherList.map { it.humidity }.average().toInt().toString()
        avPrecipitation.text = weatherList.map { it.precipitation }.average().toInt().toString()
        avHumidityCarbon.text = "50" // пока фейк

        setupIndicatorButtons(weatherList)
        setupIntervalButtons(weatherList)

        // По умолчанию: температура за месяц
        highlightIndicator(temperatureBtn)
        highlightInterval(monthBtn)
        addWeatherChart(weatherList, "temperature", "month")

        // Пример: кнопка "день" теперь будет строить график с dayWeather
        dayBtn.setOnClickListener {
            currentInterval = "day"
            highlightInterval(dayBtn)
            addWeatherChart(dayWeather, currentChartType, currentInterval)
        }

        // Расчет дней и прогресса
        val startDate = Date(plant.plantedDate)
        val endDate = Date(plant.expectedHarvestDate)
        val today = Date()

        val totalDays = ((endDate.time - startDate.time) / (1000 * 60 * 60 * 24)).toInt()
        val passedDays =
            ((today.time - startDate.time) / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
        val remainingDays = (totalDays - passedDays).coerceAtLeast(0)

        view?.findViewById<TextView>(R.id.days_total)?.text = totalDays.toString()
        view?.findViewById<TextView>(R.id.days_passed)?.text = passedDays.toString()
        view?.findViewById<TextView>(R.id.days_before_collection)?.text = remainingDays.toString()
        view?.findViewById<ProgressBar>(R.id.progressBar)?.progress =
            ((passedDays.toFloat() / totalDays) * 100).toInt().coerceIn(0, 100)

    }

    private fun setupIndicatorButtons(weatherList: List<WeatherData>) {
        temperatureBtn.setOnClickListener {
            currentChartType = "temperature"
            highlightIndicator(temperatureBtn)
            addWeatherChart(weatherList, currentChartType, currentInterval)
        }
        humidityAirBtn.setOnClickListener {
            currentChartType = "humidity"
            highlightIndicator(humidityAirBtn)
            addWeatherChart(weatherList, currentChartType, currentInterval)
        }
        precipitationBtn.setOnClickListener {
            currentChartType = "precipitation"
            highlightIndicator(precipitationBtn)
            addWeatherChart(weatherList, currentChartType, currentInterval)
        }
        humidityCarbonBtn.setOnClickListener {
            currentChartType = "carbon"
            highlightIndicator(humidityCarbonBtn)
            addWeatherChart(weatherList, currentChartType, currentInterval)
        }
    }

    private fun highlightIndicator(active: LinearLayout) {
        val buttons = listOf(temperatureBtn, humidityAirBtn, precipitationBtn, humidityCarbonBtn)
        buttons.forEach {
            it.setBackgroundColor(
                if (it == active) ContextCompat.getColor(requireContext(), R.color.white)
                else ContextCompat.getColor(requireContext(), R.color.gray5)
            )
        }
    }

    private fun setupIntervalButtons(weatherList: List<WeatherData>) {
        dayBtn.setOnClickListener { changeInterval("day") }
        weekBtn.setOnClickListener { changeInterval("week") }
        monthBtn.setOnClickListener { changeInterval("month") }
        allTimeBtn.setOnClickListener { changeInterval("all") }
    }

    private fun changeInterval(interval: String) {
        currentInterval = interval
        val activeBtn = when (interval) {
            "day" -> dayBtn
            "week" -> weekBtn
            "month" -> monthBtn
            "all" -> allTimeBtn
            else -> monthBtn
        }
        highlightInterval(activeBtn)

        val dataToUse = when (interval) {
            "day" -> dayWeather
            else -> selectedPlanting?.let {
                FakeWeatherDataGenerator.generateFakeWeatherData(it, 30)
            } ?: emptyList()
        }

        addWeatherChart(dataToUse, currentChartType, currentInterval)
    }

    private fun highlightInterval(activeBtn: Button) {
        val buttons = listOf(dayBtn, weekBtn, monthBtn, allTimeBtn)
        buttons.forEach {
            if (it == activeBtn) {
                it.setBackgroundColor(Color.BLACK)
                it.setTextColor(Color.WHITE)
            } else {
                it.setBackgroundColor(Color.WHITE)
                it.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray2))
            }
        }
    }


    private fun addWeatherChart(weatherList: List<WeatherData>, type: String, interval: String) {
        graphicLayout.removeAllViews()
        val chart = LineChart(requireContext())
        chart.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        val filteredList = when (interval) {
            "day" -> dayWeather
            "week" -> weatherList.takeLast(7)
            "month" -> weatherList.takeLast(30)
            "all" -> weatherList
            else -> weatherList
        }

        // Получаем норму для культуры
        val crop = selectedPlanting?.let { db.getCropById(it.cropID) }

        val entriesFact: List<Entry>
        val entriesNormalMin: List<Entry>
        val entriesNormalMax: List<Entry>

        when (type) {
            "temperature" -> {
                entriesFact = filteredList.mapIndexed { i, d -> Entry(i.toFloat(), d.temperature.toFloat()) }
                val minTemp = crop?.optimalTempMin?.toFloat() ?: 20f
                val maxTemp = crop?.optimalTempMax?.toFloat() ?: 25f
                entriesNormalMin = filteredList.mapIndexed { i, _ -> Entry(i.toFloat(), minTemp) }
                entriesNormalMax = filteredList.mapIndexed { i, _ -> Entry(i.toFloat(), maxTemp) }
            }

            "humidity" -> {
                entriesFact = filteredList.mapIndexed { i, d -> Entry(i.toFloat(), d.humidity.toFloat()) }
                val optimalHum = crop?.optimalHumidity?.toFloat() ?: 60f
                val delta = 5f
                entriesNormalMin = filteredList.mapIndexed { i, _ -> Entry(i.toFloat(), optimalHum - delta) }
                entriesNormalMax = filteredList.mapIndexed { i, _ -> Entry(i.toFloat(), optimalHum + delta) }
            }

            "precipitation" -> {
                entriesFact = filteredList.mapIndexed { i, d -> Entry(i.toFloat(), d.precipitation.toFloat()) }
                val normalPrecip = 5f
                val delta = 2f
                entriesNormalMin = filteredList.mapIndexed { i, _ -> Entry(i.toFloat(), normalPrecip - delta) }
                entriesNormalMax = filteredList.mapIndexed { i, _ -> Entry(i.toFloat(), normalPrecip + delta) }
            }

            "carbon" -> {
                entriesFact = filteredList.mapIndexed { i, _ -> Entry(i.toFloat(), (40..70).random().toFloat()) }
                val normalCarbon = 60f
                val delta = 5f
                entriesNormalMin = filteredList.mapIndexed { i, _ -> Entry(i.toFloat(), normalCarbon - delta) }
                entriesNormalMax = filteredList.mapIndexed { i, _ -> Entry(i.toFloat(), normalCarbon + delta) }
            }

            else -> {
                entriesFact = emptyList()
                entriesNormalMin = emptyList()
                entriesNormalMax = emptyList()
            }
        }

        val title = when (type) {
            "temperature" -> "Температура (°C)"
            "humidity" -> "Влажность воздуха (%)"
            "precipitation" -> "Осадки (мм)"
            "carbon" -> "Влажность почвы (%)"
            else -> ""
        }

        val outlineColor = Color.parseColor("#643DC5")  // цвет линии (контур)
        val myfillColor = Color.parseColor("#B5A2E3")     // цвет заливки области
        val myNorm = Color.parseColor("#DFF2E1")

        val dataSetFact = LineDataSet(entriesFact, "$title (факт)").apply {
            color = outlineColor
            lineWidth = 2f

            setCircleColor(outlineColor)
            circleHoleColor = Color.WHITE
            circleRadius = 3f

            isHighlightEnabled = true
            highLightColor = outlineColor

            setDrawValues(false)

            setDrawFilled(true)
            fillColor = myfillColor
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        val dataSetNormalMin = LineDataSet(entriesNormalMin, "Норма min").apply {
            color = myNorm
            setDrawValues(false)
            setDrawCircles(false)
            setDrawFilled(true)
            fillColor = myNorm
            mode = LineDataSet.Mode.LINEAR
            fillFormatter = IFillFormatter { _, _ ->
                entriesNormalMax.maxOfOrNull { it.y } ?: 0f
            }

        }

        val dataSetNormalMax = LineDataSet(entriesNormalMax, "Норма max").apply {
            color = myNorm
            setDrawValues(false)
            setDrawCircles(false)
            setDrawFilled(false)
            lineWidth = 2f
            mode = LineDataSet.Mode.LINEAR
        }

        // Создаём LineData с двумя линиями для закрашенной области и одной для факта
        val lineData = LineData(dataSetNormalMin, dataSetNormalMax, dataSetFact)

        chart.apply {
            data = lineData
            description.isEnabled = false
            legend.isEnabled = true
            axisRight.isEnabled = false
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                textColor = Color.BLACK
                textSize = 12f
            }
            xAxis.setDrawGridLines(false)
            axisLeft.setDrawGridLines(true)
            setTouchEnabled(true)
            setPinchZoom(true)
            invalidate()
        }



        graphicLayout.addView(chart)
    }
}
