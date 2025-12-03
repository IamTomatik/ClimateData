package com.example.climatedata.fragment_createSowing


import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.climatedata.AnalysisFragment
import com.example.climatedata.HomeActivity
import com.example.climatedata.R
import com.example.climatedata.data.db.ClimateDatabase
import com.example.climatedata.data.models.History
import com.example.climatedata.data.models.Planting
import com.example.climatedata.data.models.Recommendation
import com.example.climatedata.fake.RecommendationAdapter
import com.example.climatedata.fake.RecommendationGenerator
import com.example.climatedata.history.HistoryAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


class SpecificCardPlant : Fragment() {

    private var onPlantCompletedListener: ((Planting) -> Unit)? = null

    private var planId: Int? = null
    private val db by lazy { ClimateDatabase.getInstance(requireContext()) }
    private var imageUri: Uri? = null
    private var currentPhotoUri: Uri? = null

    private lateinit var moreHistory: LinearLayout
    private lateinit var moreHistoryText: TextView


    private var isHistoryExpanded = false
    private var fullHistoryList: List<History> = emptyList()

    private val weatherHandler = android.os.Handler()
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var recommendationAdapter: RecommendationAdapter

    private var currentTemp: Double = 0.0
    private var currentHumidity: Double = 0.0
    private var currentSoil: Double = 0.0
    private var userId: Int = 0

    private val weatherRunnable = object : Runnable {
        override fun run() {

            // Запланировать следующий запуск через 12 секунд
            weatherHandler.postDelayed(this, 12000)
        }
    }


    // выбор из галереи
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val croppedUri = cropAndSaveImage(it)
                croppedUri?.let { resultUri ->
                    imageUri = resultUri
                    updatePhoto(resultUri)
                    saveImageUriToDb(resultUri)
                }
            }
        }

    // фото с камеры
    private val takePhotoLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
            if (success && imageUri != null) {
                val croppedUri = cropAndSaveImage(imageUri!!)
                croppedUri?.let {
                    imageUri = it
                    updatePhoto(it)
                    saveImageUriToDb(it)
                }
            }
        }

    companion object {
        private const val ARG_PLAN_ID = "plan_id"
        fun newInstance(planId: Int): SpecificCardPlant {
            val fragment = SpecificCardPlant()
            val bundle = Bundle()
            bundle.putInt(ARG_PLAN_ID, planId)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        planId = arguments?.getInt(ARG_PLAN_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_specific_card_plant, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val planting: Planting = planId?.let { db.getPlantingById(it) } ?: return

        userId = planting.userID



        setupCalendar(view)
        displayPlantingInfo()
        weatherHandler.post(weatherRunnable)

        // --- посадка ---
        val namePlant = view.findViewById<TextView>(R.id.namePlant)
        namePlant.text = planting.name


        // --- культура ---
        val nameCrop = view.findViewById<TextView>(R.id.name_crop)
        val cropName = db.getCropById(planting.cropID)?.name ?: "Неизвестная культура"
        nameCrop.text = cropName

        // --- фото ---
        val photoLayout = view.findViewById<LinearLayout>(R.id.photo)
        // загрузка сохранённого фото
        planting.imageUri?.let {
            val safeUri = Uri.parse(it)
            // пытаемся открыть и скопировать в локальный файл
            val croppedLocal = cropAndSaveImage(safeUri)
            if (croppedLocal != null) {
                imageUri = croppedLocal
                updatePhoto(croppedLocal)
            }
        }
        photoLayout.setOnClickListener { showPhotoSourceDialog() }

        // клик по фото
        photoLayout.setOnClickListener {
            showPhotoSourceDialog()
        }

        //смена текста
        moreHistory = view.findViewById(R.id.moreHistory)
        moreHistoryText = view.findViewById(R.id.moreHistoryText)
        moreHistory.visibility = View.GONE

        moreHistory.setOnClickListener {

            if (isHistoryExpanded) {
                // сворачиваем
                historyAdapter.updateData(fullHistoryList.take(4))
                moreHistoryText.text = "вся история"
                isHistoryExpanded = false

            } else {
                // разворачиваем весь список
                historyAdapter.updateData(fullHistoryList)
                moreHistoryText.text = "скрыть"
                isHistoryExpanded = true
            }
        }

        // --- счеты ---
        // расчёт дней и прогресса
        val startDate = Date(planting.plantedDate)
        val endDate = Date(planting.expectedHarvestDate)
        val today = Date()

        val totalDays = ((endDate.time - startDate.time) / (1000 * 60 * 60 * 24)).toInt()
        val passedDays = ((today.time - startDate.time) / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
        val remainingDays = (totalDays - passedDays).coerceAtLeast(0)

        view.findViewById<TextView>(R.id.days_total).text = totalDays.toString()
        view.findViewById<TextView>(R.id.days_passed).text = passedDays.toString()
        view.findViewById<TextView>(R.id.days_before_collection).text = remainingDays.toString()

        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val progress = ((passedDays.toFloat() / totalDays) * 100).toInt().coerceIn(0, 100)
        progressBar.progress = progress

        view.findViewById<ImageView>(R.id.back).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // чипсы
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val chipGroup = view.findViewById<ChipGroup>(R.id.chipGroup)
        chipGroup.removeAllViews()

        val chipS = Chip(requireContext())
        chipS.text = "посадка: ${sdf.format(startDate)}"
        chipS.isClickable = false
        chipS.isCheckable = false
        chipGroup.addView(chipS)

        val chipE = Chip(requireContext())
        chipE.text = "сбор: ${sdf.format(endDate)}"
        chipE.isClickable = false
        chipE.isCheckable = false
        chipGroup.addView(chipE)


        // --- истории ---
        val historyRecycler = view.findViewById<RecyclerView>(R.id.historyRecyclerView)
        historyRecycler.layoutManager = LinearLayoutManager(requireContext())
        historyAdapter = HistoryAdapter(mutableListOf())
        historyRecycler.adapter = historyAdapter

        // --- тесмтовые записи истории 2 строка!!!!! ---
        addTestHistoryIfNeeded(planting)
        updateHistoryForDate(Date(), planting.userID)

        // Загрузить историю пользователя сразу
        updateHistoryForDate(Date(), planting.userID)



        // --- рекомендейшен ---
        val allActiveRecommendations = db.getActiveRecommendationsForUser(userId)
        val recommendationsForPlanting = allActiveRecommendations.filter { it.planID == planting.planID }

        val recommendationRecyclerView: RecyclerView = view.findViewById(R.id.recommendationRecyclerView)
        recommendationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        recommendationRecyclerView.setHasFixedSize(true)
        val recommendationBlock = view.findViewById<LinearLayout>(R.id.recommendation)
        recommendationAdapter = RecommendationAdapter(
            recommendationsForPlanting.toMutableList(),
            planting.cropID.toString()
        ) { rec, action ->
            Toast.makeText(requireContext(), "Действие выполнено: ${rec.message}", Toast.LENGTH_SHORT).show()
            db.completeRecommendation(rec.recomID)

            // В историю добавляем короткое сообщение
            val historyMsg = "Рекомендация ${action.lowercase()} выполнена"
            addHistoryEntry(historyMsg, userId, planting.planID, planting.locID)

            recommendationAdapter.removeRecommendation(rec)
            if (recommendationAdapter.itemCount == 0) {
                recommendationBlock.visibility = View.GONE
            }

            historyAdapter.updateData(db.getHistoryByUser(userId).filter { it.planID == planting.planID })
        }
        if (recommendationsForPlanting.isEmpty()) {
            recommendationBlock.visibility = View.GONE
        } else {
            recommendationBlock.visibility = View.VISIBLE
        }
        recommendationRecyclerView.adapter = recommendationAdapter

        //генерация рекомендаций

        planId?.let { id ->
            Log.d("SpecificCardPlant", "planId=$id получен, пытаемся загрузить посадку")

            val planting = db.getPlantingById(id) ?: return@let
            updateWeatherUI(planting)
            Log.d("SpecificCardPlant", "UI погоды обновлён")

            val crop = db.getCropById(planting.cropID) ?: return@let

            // Генерируем рекомендации
            val generator = RecommendationGenerator(db)
            val newRecs = generator.generateRecommendations(
                planting,
                crop,
                currentTemp,
                currentHumidity,
                currentSoil,
                planting.userID,
                saveToDb = false
            )
            recommendationAdapter.updateData(newRecs)
            Log.d("SpecificCardPlant", " Данные обновлены в адаптере (размер=${newRecs.size})")
            val recommendationBlock = view.findViewById<LinearLayout>(R.id.recommendation)
            if (newRecs.isEmpty()) {
                recommendationBlock.visibility = View.GONE
            } else {
                recommendationBlock.visibility = View.VISIBLE
            }


        }


        //свитчи

        val switchPoliv = view.findViewById<Switch>(R.id.SwitchPoliv)
        val textOnPoliv = view.findViewById<TextView>(R.id.TextOnPoliv)
        switchPoliv.setOnCheckedChangeListener { _, isChecked ->
            val msg = if (isChecked) "Автополив включён" else "Автополив выключен"
            textOnPoliv.text = if (isChecked) "вкл" else "выкл"
            addHistoryEntry(msg, userId, planting.planID, planting.locID)
            historyAdapter.updateData(db.getHistoryByUser(userId).filter { it.planID == planting.planID })
        }

        val switchSvet = view.findViewById<Switch>(R.id.SwitchSvet)
        val textOnSvet = view.findViewById<TextView>(R.id.TextOnSvet)
        switchSvet.setOnCheckedChangeListener { _, isChecked ->
            val msg = if (isChecked) "Автосвет включён" else "Автосвет выключен"
            textOnSvet.text = if (isChecked) "вкл" else "выкл"
            addHistoryEntry(msg, userId, planting.planID, planting.locID)
            historyAdapter.updateData(db.getHistoryByUser(userId).filter { it.planID == planting.planID })
        }


        // --- Weather UI ---
        updateWeatherUI(planting)


        // --- график ---
        val graphicLayout = view.findViewById<LinearLayout>(R.id.graphic)
        graphicLayout.setOnClickListener {
            planId?.let { id ->
                val fragment = AnalysisFragment.newInstance(id)
                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.frame_layout, fragment)
                    .addToBackStack("analysis")
                    .commit()

                // Подсветка пункта BottomNavigationView
                (requireActivity() as? HomeActivity)?.highlightNavBarItem(R.id.analysis)
            } ?: run {
                Toast.makeText(requireContext(), "Не выбрана посадка", Toast.LENGTH_SHORT).show()
            }
        }


        // --- архив ---
        val completeButton = view.findViewById<Button>(R.id.buttonCompletePlanting)
        completeButton.setOnClickListener {
            completePlanting()
            Toast.makeText(requireContext(), "Посев завершён и отправлен в архив", Toast.LENGTH_SHORT).show()
        }


    }

    override fun onPause() {
        super.onPause()
        weatherHandler.removeCallbacks(weatherRunnable)
    }

    // ======== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ========

    private fun displayPlantingInfo() {
        val planting = planId?.let { db.getPlantingById(it) } ?: return
        val nameCrop = view?.findViewById<TextView>(R.id.name_crop)
        val cropName = db.getCropById(planting.cropID)?.name ?: "Неизвестная культура"
        nameCrop?.text = cropName
    }

    private fun setupCalendar(view: View) {
        val calendarRecyclerView = view.findViewById<RecyclerView>(R.id.calendarRecyclerView)
        calendarRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val days = generateMonthDays()
        val adapter = CalendarAdapter(days) { selectedDate ->
            Log.d("HISTORY", " Клик по дате: $selectedDate")
            updateNameMonth(selectedDate)
            updateHistoryForDate(selectedDate, userId)
        }
        calendarRecyclerView.adapter = adapter

        val todayPosition = days.indexOfFirst { adapter.isSameDay(it, Date()) }
        if (todayPosition >= 0) {
            calendarRecyclerView.scrollToPosition(todayPosition)
        }
    }

    private fun generateMonthDays(): List<Date> {
        val calendar = Calendar.getInstance()
        val days = mutableListOf<Date>()

        // первый день текущего месяца
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val month = calendar.get(Calendar.MONTH)

        while (calendar.get(Calendar.MONTH) == month) {
            days.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return days
    }


    private fun updateNameMonth(date: Date) {
        val nameMonth = view?.findViewById<TextView>(R.id.nameMonth) ?: return
        val sdf = SimpleDateFormat("LLLL yyyy", Locale("ru")) // например, "ноябрь 2025"
        nameMonth.text = sdf.format(date).replaceFirstChar { it.uppercase() } // чтобы с большой буквы
    }


    private fun showPhotoSourceDialog() {
        val options = arrayOf("Сделать фото", "Выбрать из галереи", "Удалить")
        AlertDialog.Builder(requireContext())
            .setTitle("Добавить фото")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> pickImageLauncher.launch("image/*")
                    2 -> deleteCurrentPhoto()
                }
            }
            .show()
    }



    private fun openCamera() {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "photo_${System.currentTimeMillis()}")
            put(MediaStore.Images.Media.DESCRIPTION, "Фото растения")
        }
        imageUri = requireContext().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
        takePhotoLauncher.launch(imageUri)
    }

    private fun cropAndSaveImage(uri: Uri): Uri? {
        return try {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
            val original = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (original == null) return null

            val targetWidth = 380
            val targetHeight = 300

            // исходные размеры
            val width = original.width
            val height = original.height

            // вычисляем соотношение сторон
            val scale = maxOf(
                targetWidth.toFloat() / width,
                targetHeight.toFloat() / height
            )

            // масштабируем без искажения (заполняем контейнер)
            val scaledWidth = (width * scale).toInt()
            val scaledHeight = (height * scale).toInt()
            val scaledBitmap = Bitmap.createScaledBitmap(original, scaledWidth, scaledHeight, true)

            // теперь центрируем и обрезаем лишнее
            val xOffset = (scaledWidth - targetWidth) / 2
            val yOffset = (scaledHeight - targetHeight) / 2
            val cropped = Bitmap.createBitmap(scaledBitmap, xOffset, yOffset, targetWidth, targetHeight)

            // сохраняем во внутреннее хранилище
            val file = File(requireContext().cacheDir, "cropped_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { fos ->
                cropped.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            }

            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveImageUriToDb(uri: Uri) {
        planId?.let { id ->
            val planting = db.getPlantingById(id)
            if (planting != null) {
                db.updatePlanting(planting.copy(imageUri = uri.toString()))
            }
        }
    }

    private fun updatePhoto(uri: Uri) {
        val photoLayout = view?.findViewById<LinearLayout>(R.id.photo) ?: return
        photoLayout.removeAllViews()

        val imageView = ImageView(requireContext()).apply {
            setImageURI(uri)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        photoLayout.addView(imageView)
    }

    private fun deleteCurrentPhoto() {
        val imageView = requireView().findViewById<ImageView>(R.id.photo)
        imageView.setImageDrawable(null)

        currentPhotoUri?.let { uri ->
            val file = File(uri.path ?: "")
            if (file.exists()) file.delete()
        }
        currentPhotoUri = null

        Toast.makeText(requireContext(), "Фото удалено", Toast.LENGTH_SHORT).show()
    }

    private fun updateWeatherUI(planting: Planting) {
        val crop = db.getCropById(planting.cropID) ?: return

        // Минимальные и максимальные значения
        val tempMin = crop.optimalTempMin
        val tempMax = crop.optimalTempMax
        val humidityMin = (crop.optimalHumidity - 10).coerceAtLeast(0.0)
        val humidityMax = (crop.optimalHumidity + 10).coerceAtMost(100.0)
        val humiditySoilMin = (crop.optimalHumidity - 10).coerceAtLeast(0.0)
        val humiditySoilMax = (crop.optimalHumidity + 10).coerceAtMost(100.0)

        // Отображение мин/макс значений
        view?.findViewById<TextView>(R.id.gradusMin)?.text = tempMin.toInt().toString()
        view?.findViewById<TextView>(R.id.gradusMax)?.text = tempMax.toInt().toString()
        view?.findViewById<TextView>(R.id.vlashnostMin)?.text = humidityMin.toInt().toString()
        view?.findViewById<TextView>(R.id.vlashnostMax)?.text = humidityMax.toInt().toString()
        view?.findViewById<TextView>(R.id.carbonMin)?.text = humiditySoilMin.toInt().toString()
        view?.findViewById<TextView>(R.id.carbonMax)?.text = humiditySoilMax.toInt().toString()

        // Генерация текущих значений
        val random = kotlin.random.Random(System.currentTimeMillis())
        currentTemp = (tempMin + random.nextDouble() * (tempMax - tempMin)).coerceAtLeast(1.0)
        currentHumidity = (humidityMin + random.nextDouble() * (humidityMax - humidityMin)).coerceAtLeast(1.0)
        currentSoil = (humiditySoilMin + random.nextDouble() * (humiditySoilMax - humiditySoilMin)).coerceAtLeast(1.0)


        // Обновление TextView
        view?.findViewById<TextView>(R.id.gradusFact)?.text = "%.1f".format(currentTemp)
        view?.findViewById<TextView>(R.id.vlashnostFact)?.text = "%.1f".format(currentHumidity)
        view?.findViewById<TextView>(R.id.carbonFact)?.text = "%.1f".format(currentSoil)

        // Обновление прогрессбаров
        val tempProgress = ((currentTemp - tempMin) / (tempMax - tempMin) * 100).toInt().coerceIn(0, 100)
        view?.findViewById<ProgressBar>(R.id.progressBarGradus)?.progress = tempProgress

        val humidityProgress = ((currentHumidity - humidityMin) / (humidityMax - humidityMin) * 100).toInt().coerceIn(0, 100)
        view?.findViewById<ProgressBar>(R.id.progressBarVlashnost)?.progress = humidityProgress

        val soilProgress = currentSoil.toInt().coerceIn(1, 100)
        view?.findViewById<ProgressBar>(R.id.progressBarCarbon)?.progress = soilProgress
    }


    private fun addHistoryEntry(message: String, userId: Int, planId: Int, locId: Int) {
        val now = System.currentTimeMillis()
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(now))

        val history = History(
            userID = userId,
            planID = planId,
            locID = locId,
            message = message,
            timestamp = now,
            date = date
        )

        db.addHistory(history)
    }

    private fun isSameDay(d1: Date, d2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = d1 }
        val cal2 = Calendar.getInstance().apply { time = d2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }

    private fun updateHistoryForDate(selectedDate: Date, userId: Int) {
        val allHistory = db.getHistoryByUser(userId)


        fullHistoryList = allHistory.filter { history ->
            val historyDate = Date(history.timestamp)
            val same = isSameDay(historyDate, selectedDate)
            same
        }

        // свернуто по умолчанию
        isHistoryExpanded = false

        val limited = fullHistoryList.take(4)
        historyAdapter.updateData(limited)

        // управление видимостью "вся история"
        if (fullHistoryList.size > 4) {
            moreHistory.visibility = View.VISIBLE
            moreHistoryText.text = "вся история"
        } else {
            moreHistory.visibility = View.GONE
        }

    }



    fun setOnPlantCompletedListener(listener: (Planting) -> Unit) {
        onPlantCompletedListener = listener
    }

    private fun completePlanting() {
        planId?.let { id ->
            val planting = db.getPlantingById(id) ?: return
            // Меняем статус
            val updatedPlanting = planting.copy(status = "archive")
            db.updatePlanting(updatedPlanting)
            // Добавляем запись в историю
            addHistoryEntry("Посев завершен", planting.userID, planting.planID, planting.locID)
            // Передаем обратно в PlantFragment
            onPlantCompletedListener?.invoke(updatedPlanting)
            // Закрываем карточку
            parentFragmentManager.popBackStack()
        }
    }


    // --- тестовые записи истории ---
    private fun addTestHistoryIfNeeded(planting: Planting) {
        val userId = planting.userID
        val planId = planting.planID
        val locId = planting.locID

        // Проверяем, есть ли уже тестовые записи
        val existing = db.getHistoryByUser(userId)
            .any { it.message.contains("ТЕСТ") }

        if (existing) return // уже добавлены

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        // Добавляем записи за последние 7 дней
        for (i in 1..7) {
            val calendarCopy = calendar.clone() as Calendar
            calendarCopy.add(Calendar.DAY_OF_MONTH, -i) // сдвиг на i дней назад

            val timestamp = calendarCopy.timeInMillis
            val dateStr = sdf.format(calendarCopy.time)

            val history = History(
                userID = userId,
                planID = planId,
                locID = locId,
                message = "ТЕСТ — действие за $dateStr",
                timestamp = timestamp,
                date = dateStr
            )
            db.addHistory(history)
        }
    }

}
