package com.example.climatedata.additional_lists

import com.example.climatedata.data.db.ClimateDatabase
import com.example.climatedata.data.models.Crop

object PlantExecutedDb {

    fun seedCrops(db: ClimateDatabase) {
        val crops = listOf(
            Crop(
                cropID = 1,
                name = "Пшеница",
                category = "Зерновые",
                optimalTempMin = 10.0,
                optimalTempMax = 25.0,
                optimalHumidity = 60.0,
                soilHumidity = 55.0,
                growthDays = 120,
                riskFactors = "Засуха, грибки"
            ),
            Crop(
                cropID = 2,
                name = "Кукуруза",
                category = "Зерновые",
                optimalTempMin = 15.0,
                optimalTempMax = 30.0,
                optimalHumidity = 70.0,
                soilHumidity = 65.0,
                growthDays = 100,
                riskFactors = "Вредители, засуха"
            ),
            Crop(
                cropID = 3,
                name = "Ячмень",
                category = "Зерновые",
                optimalTempMin = 8.0,
                optimalTempMax = 22.0,
                optimalHumidity = 55.0,
                soilHumidity = 50.0,
                growthDays = 90,
                riskFactors = "Грибковые инфекции"
            ),
            Crop(
                cropID = 4,
                name = "Картофель",
                category = "Клубнеплоды",
                optimalTempMin = 12.0,
                optimalTempMax = 25.0,
                optimalHumidity = 65.0,
                soilHumidity = 70.0,
                growthDays = 80,
                riskFactors = "Колорадский жук, гниль"
            ),
            Crop(
                cropID = 5,
                name = "Морковь",
                category = "Корнеплоды",
                optimalTempMin = 10.0,
                optimalTempMax = 20.0,
                optimalHumidity = 60.0,
                soilHumidity = 60.0,
                growthDays = 70,
                riskFactors = "Морковная муха"
            ),
            Crop(
                cropID = 6,
                name = "Соя",
                category = "Бобовые",
                optimalTempMin = 15.0,
                optimalTempMax = 30.0,
                optimalHumidity = 70.0,
                soilHumidity = 65.0,
                growthDays = 110,
                riskFactors = "Фузариоз, засуха"
            ),
            Crop(
                cropID = 7,
                name = "Подсолнечник",
                category = "Масличные",
                optimalTempMin = 18.0,
                optimalTempMax = 32.0,
                optimalHumidity = 50.0,
                soilHumidity = 45.0,
                growthDays = 90,
                riskFactors = "Гельминтоспориоз, засуха"
            ),
            Crop(
                cropID = 8,
                name = "Свекла",
                category = "Корнеплоды",
                optimalTempMin = 10.0,
                optimalTempMax = 25.0,
                optimalHumidity = 60.0,
                soilHumidity = 55.0,
                growthDays = 100,
                riskFactors = "Мучнистая роса"
            ),
            Crop(
                cropID = 9,
                name = "Овес",
                category = "Зерновые",
                optimalTempMin = 5.0,
                optimalTempMax = 20.0,
                optimalHumidity = 55.0,
                soilHumidity = 50.0,
                growthDays = 85,
                riskFactors = "Грибковые болезни"
            ),
            Crop(
                cropID = 10,
                name = "Огурцы",
                category = "Овощи",
                optimalTempMin = 18.0,
                optimalTempMax = 28.0,
                optimalHumidity = 70.0,
                soilHumidity = 65.0,
                growthDays = 50,
                riskFactors = "Мучнистая роса, тля"
            ),
            Crop(
                cropID = 11,
                name = "Помидоры",
                category = "Овощи",
                optimalTempMin = 20.0,
                optimalTempMax = 30.0,
                optimalHumidity = 65.0,
                soilHumidity = 60.0,
                growthDays = 90,
                riskFactors = "Фитофтора, тля"
            ),
            Crop(
                cropID = 12,
                name = "Лук",
                category = "Овощи",
                optimalTempMin = 10.0,
                optimalTempMax = 25.0,
                optimalHumidity = 60.0,
                soilHumidity = 50.0,
                growthDays = 90,
                riskFactors = "Луковая муха, гниль"
            ),
            Crop(
                cropID = 13,
                name = "Чеснок",
                category = "Овощи",
                optimalTempMin = 10.0,
                optimalTempMax = 25.0,
                optimalHumidity = 60.0,
                soilHumidity = 55.0,
                growthDays = 120,
                riskFactors = "Фузариоз, гниль"
            ),
            Crop(
                cropID = 14,
                name = "Капуста белокачанная",
                category = "Овощи",
                optimalTempMin = 10.0,
                optimalTempMax = 20.0,
                optimalHumidity = 70.0,
                soilHumidity = 65.0,
                growthDays = 90,
                riskFactors = "Капустная муха, гниль"
            ),
            Crop(
                cropID = 15,
                name = "Капуста брокколи",
                category = "Овощи",
                optimalTempMin = 12.0,
                optimalTempMax = 24.0,
                optimalHumidity = 70.0,
                soilHumidity = 65.0,
                growthDays = 80,
                riskFactors = "Грибковые инфекции, бабочка-капустница"
            ),
            Crop(
                cropID = 16,
                name = "Редис",
                category = "Овощи",
                optimalTempMin = 5.0,
                optimalTempMax = 18.0,
                optimalHumidity = 60.0,
                soilHumidity = 55.0,
                growthDays = 30,
                riskFactors = "Корневая гниль, вредители"
            ),
            Crop(
                cropID = 17,
                name = "Горох",
                category = "Бобовые",
                optimalTempMin = 10.0,
                optimalTempMax = 25.0,
                optimalHumidity = 60.0,
                soilHumidity = 55.0,
                growthDays = 70,
                riskFactors = "Грибковые болезни"
            ),
            Crop(
                cropID = 18,
                name = "Фасоль",
                category = "Бобовые",
                optimalTempMin = 15.0,
                optimalTempMax = 30.0,
                optimalHumidity = 65.0,
                soilHumidity = 60.0,
                growthDays = 60,
                riskFactors = "Фузариоз, вредители"
            ),
            Crop(
                cropID = 19,
                name = "Гвоздика",
                category = "Цветы",
                optimalTempMin = 15.0,
                optimalTempMax = 25.0,
                optimalHumidity = 55.0,
                soilHumidity = 50.0,
                growthDays = 90,
                riskFactors = "Грибковые болезни"
            ),
            Crop(
                cropID = 20,
                name = "Гортензия",
                category = "Цветы",
                optimalTempMin = 15.0,
                optimalTempMax = 25.0,
                optimalHumidity = 70.0,
                soilHumidity = 65.0,
                growthDays = 120,
                riskFactors = "Грибковые инфекции"
            ),
            Crop(
                cropID = 21,
                name = "Баклажан",
                category = "Овощи",
                optimalTempMin = 20.0,
                optimalTempMax = 30.0,
                optimalHumidity = 65.0,
                soilHumidity = 60.0,
                growthDays = 100,
                riskFactors = "Фитофтора, тля"
            ),
            Crop(
                cropID = 22,
                name = "Кабачок",
                category = "Овощи",
                optimalTempMin = 18.0,
                optimalTempMax = 28.0,
                optimalHumidity = 70.0,
                soilHumidity = 65.0,
                growthDays = 50,
                riskFactors = "Тля, мучнистая роса"
            ),
            Crop(
                cropID = 23,
                name = "Брокколи",
                category = "Овощи",
                optimalTempMin = 12.0,
                optimalTempMax = 24.0,
                optimalHumidity = 70.0,
                soilHumidity = 60.0,
                growthDays = 80,
                riskFactors = "Грибковые инфекции"
            ),
            Crop(
                cropID = 24,
                name = "Яблоня",
                category = "Фрукты",
                optimalTempMin = 15.0,
                optimalTempMax = 25.0,
                optimalHumidity = 60.0,
                soilHumidity = 55.0,
                growthDays = 365,
                riskFactors = "Парша, тля"
            ),
            Crop(
                cropID = 25,
                name = "Груша",
                category = "Фрукты",
                optimalTempMin = 15.0,
                optimalTempMax = 25.0,
                optimalHumidity = 60.0,
                soilHumidity = 55.0,
                growthDays = 365,
                riskFactors = "Парша, бактериальный ожог"
            ),
            Crop(
                cropID = 26,
                name = "Клубника",
                category = "Ягоды",
                optimalTempMin = 15.0,
                optimalTempMax = 25.0,
                optimalHumidity = 70.0,
                soilHumidity = 65.0,
                growthDays = 90,
                riskFactors = "Серая гниль, тля"
            ),
            Crop(
                cropID = 27,
                name = "Малина",
                category = "Ягоды",
                optimalTempMin = 15.0,
                optimalTempMax = 25.0,
                optimalHumidity = 70.0,
                soilHumidity = 65.0,
                growthDays = 120,
                riskFactors = "Серая гниль, галловый клещ"
            ),
            Crop(
                cropID = 28,
                name = "Петуния",
                category = "Цветы",
                optimalTempMin = 15.0,
                optimalTempMax = 25.0,
                optimalHumidity = 60.0,
                soilHumidity = 55.0,
                growthDays = 60,
                riskFactors = "Тля, грибковые болезни"
            ),
            Crop(
                cropID = 29,
                name = "Роза",
                category = "Цветы",
                optimalTempMin = 15.0,
                optimalTempMax = 25.0,
                optimalHumidity = 65.0,
                soilHumidity = 60.0,
                growthDays = 120,
                riskFactors = "Тля, мучнистая роса"
            ),
            Crop(
                cropID = 30,
                name = "Лаванда",
                category = "Цветы",
                optimalTempMin = 18.0,
                optimalTempMax = 28.0,
                optimalHumidity = 50.0,
                soilHumidity = 45.0,
                growthDays = 150,
                riskFactors = "Корневая гниль, мучнистая роса"
            ),
            Crop(
                cropID = 31,
                name = "Базилик",
                category = "Овощи",
                optimalTempMin = 20.0,
                optimalTempMax = 30.0,
                optimalHumidity = 60.0,
                soilHumidity = 55.0,
                growthDays = 60,
                riskFactors = "Мучнистая роса, тля"
            ),
            Crop(
                cropID = 32,
                name = "Шпинат",
                category = "Овощи",
                optimalTempMin = 10.0,
                optimalTempMax = 22.0,
                optimalHumidity = 65.0,
                soilHumidity = 60.0,
                growthDays = 40,
                riskFactors = "Гниль, тля"
            ),
            Crop(
                cropID = 33,
                name = "Капуста кольраби",
                category = "Овощи",
                optimalTempMin = 12.0,
                optimalTempMax = 24.0,
                optimalHumidity = 70.0,
                soilHumidity = 65.0,
                growthDays = 80,
                riskFactors = "Грибковые инфекции, бабочка-капустница"
            ),

        )

        // Проверяем, что таблица пуста, чтобы не добавлять дубликаты
        if (db.getAllCrops().isEmpty()) {
            crops.forEach { db.addCrop(it) }
        }
    }
}