package com.example.climatedata.fake

import android.util.Log
import com.example.climatedata.data.db.ClimateDatabase
import com.example.climatedata.data.models.Crop
import com.example.climatedata.data.models.Planting
import com.example.climatedata.data.models.Recommendation

class RecommendationGenerator(private val db: ClimateDatabase? = null) {



    private val actionTextMap = listOf(
        "пересушена" to "Полить",
        "сух" to "Полить",
        "увлажнить" to "Полить",
        "переувлажн" to "Отключить полив",
        "проветр" to "Проветрить",
        "затен" to "Включить затенение",
        "освещ" to "Включить подсветку",
        "низкая" to "Увеличить температуру",
        "высокая" to "Уменьшить температуру",
        "охлажд" to "Включить охлаждение"
    )

    private fun getActionText(message: String): String {
        val text = message.lowercase()
        for ((key, value) in actionTextMap) {
            if (text.contains(key)) return value
        }
        return "Принять"
    }

    private val patterns = listOf(

        // Температура
        Triple(
            { temp: Double, crop: Crop -> temp > crop.optimalTempMax + 6 },
            "Температура значительно выше нормы. Возможно требуется включить охлаждение или дополнительное затенение.",
            "high"
        ),

        Triple(
            { temp: Double, crop: Crop -> temp < crop.optimalTempMin - 6 },
            "Температура критически низкая. Используйте обогрев или утеплите грядку.",
            "high"
        ),

        Triple(
            { temp: Double, crop: Crop -> temp > crop.optimalTempMax + 1 },
            "Температура слегка превышает оптимальную. Приоткройте вентиляционные отверстия.",
            "low"
        ),

        Triple(
            { temp: Double, crop: Crop -> temp < crop.optimalTempMin - 1 },
            "Температура немного ниже нормы. Проверьте утепление и закрытие теплицы.",
            "low"
        ),

        // Влажность воздуха
        Triple(
            { humidity: Double, crop: Crop -> humidity < crop.optimalHumidity - 5 },
            "Влажность немного снижена. Возможен дополнительный мелкодисперсный полив.",
            "low"
        ),

        Triple(
            { humidity: Double, crop: Crop -> humidity > crop.optimalHumidity + 5 },
            "Влажность немного увеличена. Немного проветрите теплицу.",
            "low"
        ),

        Triple(
            { humidity: Double, crop: Crop -> humidity < crop.optimalHumidity - 25 },
            "Очень сухой воздух! Растения под стрессом. Включите увлажнение.",
            "high"
        ),

        Triple(
            { humidity: Double, crop: Crop -> humidity > crop.optimalHumidity + 25 },
            "Влажность сильно повышена. Высокий риск грибковых заболеваний. Проветрите теплицу.",
            "high"
        ),

        // Влажность почвы
        Triple(
            { soil: Double, _: Crop -> soil < 20 },
            "Почва очень сухая! Срочно полейте растения.",
            "high"
        ),

        Triple(
            { soil: Double, _: Crop -> soil in 20.0..30.0 },
            "Почва начинает пересыхать. Желателен полив.",
            "medium"
        ),

        Triple(
            { soil: Double, _: Crop -> soil > 85 },
            "Почва слишком влажная. Проверьте систему автополива — возможно утечка.",
            "high"
        ),

        Triple(
            { soil: Double, _: Crop -> soil in 75.0..85.0 },
            "Почва переувлажнена. Уменьшите частоту полива.",
            "medium"
        )
    )


    fun generateRecommendations(
        planting: Planting,
        crop: Crop,
        temp: Double,
        humidity: Double,
        soil: Double,
        userID: Int,
        saveToDb: Boolean = true
    ): List<Recommendation> {

        Log.d("RecommendationGen", " Генерация рекомендаций для planID=${planting.planID}")

        val now = System.currentTimeMillis()
        val recommendations = mutableListOf<Recommendation>()

        patterns.forEach { (condition, message, priority) ->

            val conditionResult =
                try {
                    condition(temp, crop)
                } catch (e: Exception) {
                    false
                }

            if (conditionResult) {

                val actionText = getActionText(message)

                val rec = Recommendation(
                    recomID = 0,
                    planID = planting.planID,
                    userID = userID,
                    type = "weather",
                    message = message,
                    priority = priority,
                    generatedDate = now,
                    isCompleted = false
                )

                recommendations.add(rec)

                Log.d("RecommendationGen", " Сработала рекомендация: $message → кнопка: $actionText")
            }
        }

        Log.d("RecommendationGen", "Всего сгенерировано: ${recommendations.size}")

        if (saveToDb && db != null) {
            recommendations.forEach {
                db.addRecommendation(it)
                Log.d("RecommendationGen", "Добавлено в БД: ${it.message}")
            }
        }

        return recommendations
    }
}
