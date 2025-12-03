package com.example.climatedata.data.db


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import com.example.climatedata.data.models.Crop
import com.example.climatedata.data.models.User
import com.example.climatedata.data.models.WeatherData
import com.example.climatedata.data.models.Location
import com.example.climatedata.data.models.Planting
import com.example.climatedata.data.models.DiseaseDetection
import com.example.climatedata.data.models.History
import com.example.climatedata.data.models.Recommendation

class ClimateDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    companion object{
        private const val DATABASE_NAME = "climate_data.db"
        private const val DATABASE_VERSION = 9


        @Volatile
        private var instance: ClimateDatabase? = null

        fun getInstance(context: Context): ClimateDatabase {
            return instance ?: synchronized(this) {
                val newInstance = ClimateDatabase(context.applicationContext)
                instance = newInstance
                newInstance
            }
        }

        //таблица культура
        private const val TABLE_CROP = "crop"
        private const val COLUMN_CROP_ID = "crop_ID"
        private const val COLUMN_CROP_NAME = "name"
        private const val COLUMN_CROP_CATEGORY = "category"
        private const val COLUMN_CROP_OPTIMAL_TEMP_MIN = "optimal_temp_min"
        private const val COLUMN_CROP_OPTIMAL_TEMP_MAX = "optimal_temp_max"
        private const val COLUMN_CROP_OPTIMAL_HUMIDITY = "optimal_humidity"
        private const val COLUMN_CROP_GROWTH_DAYS = "growth_days"
        private const val COLUMN_CROP_RISK_FACTORS = "risk_factors"
        private const val COLUMN_CROP_SOIL_HUMIDITY = "optimal_soil_humidity"

        //таблица дефекты
        private const val TABLE_DISEASE = "disease_detection"
        private const val COLUMN_DISEASE_ID = "disease_detection_ID"
        private const val COLUMN_DISEASE_PLANTING_ID = "planting_ID"
        private const val COLUMN_DISEASE_IMAGE_PATH = "image_path"
        private const val COLUMN_DISEASE_DETECTED_DISEASE = "detected_disease"
        private const val COLUMN_DISEASE_CONFIDENCE = "confidence"
        private const val COLUMN_DISEASE_DETECTION_DATE = "detection_date"
        private const val COLUMN_DISEASE_RECOMMENDED_ACTION = "recommended_action"

        //таблица локация
        private const val TABLE_LOCATIONS = "locations"
        private const val COLUMN_LOCATIONS_ID = "locations_ID"
        private const val COLUMN_LOCATION_USER_ID = "user_ID"
        private const val COLUMN_LOCATION_NAME = "name"
        private const val COLUMN_LOCATION_REGION = "region"
        private const val COLUMN_LOCATION_AREA = "area"
        private const val COLUMN_LOCATION_SOIL_TYPE = "soil_type"
        private const val COLUMN_LOCATION_DESCRIPTION = "description"
        private  const val COLUMN_LOCATION_TYPE = "type"
        private const val COLUMN_LOCATION_IMG ="imageUri"

        //таблица посадка
        private const val TABLE_PLANTINGS = "planting"
        private const val COLUMN_PLANTINGS_ID = "planting_ID"
        private const val COLUMN_PLANTING_LOCATION_ID = "locations_ID"
        private const val COLUMN_PLANTING_CROP_ID = "crop_ID"
        private const val COLUMN_PLANTING_USER_ID = "user_ID"
        private const val COLUMN_PLANTING_PLANTED_DATE = "planted_date"
        private const val COLUMN_PLANTING_EXPECTED_HARVEST_DATE = "expected_harvest_date"
        private const val COLUMN_PLANTING_AREA = "area"
        private const val COLUMN_PLANTING_STATUS = "status"
        private const val COLUMN_PLANTING_NAME = "name"

        //таблица рекомендация
        private const val TABLE_RECOMMENDATIONS = "recommendations"
        private const val COLUMN_RECOMMENDATIONS_ID = "recommendations_ID"
        private const val COLUMN_RECOMMENDATION_USER_ID = "user_ID"
        private const val COLUMN_RECOMMENDATION_PLANTING_ID = "planting_ID"
        private const val COLUMN_RECOMMENDATION_TYPE = "type"
        private const val COLUMN_RECOMMENDATION_MESSAGE = "message"
        private const val COLUMN_RECOMMENDATION_PRIORITY = "priority"
        private const val COLUMN_RECOMMENDATION_GENERATED_DATE = "generated_date"
        private const val COLUMN_RECOMMENDATION_IS_COMPLETED = "is_completed"

        //таблица юзер
        private const val TABLE_USERS = "users"
        private const val COLUMN_USER_ID = "user_ID"
        private const val COLUMN_USER_EMAIL = "email"
        private const val COLUMN_USER_PASSWORD = "password"
        private const val COLUMN_USER_NAME = "name"
        private const val COLUMN_USER_ROLE = "role"

        //таблица погодное апи?
        private const val TABLE_WEATHER_DATA = "weather_data"
        private const val COLUMN_WEATHER_DATA_ID = "weather_data_ID"
        private const val COLUMN_WEATHER_LOCATION_ID = "locations_ID"
        private const val COLUMN_WEATHER_DATE = "date"
        private const val COLUMN_WEATHER_TEMPERATURE = "temperature"
        private const val COLUMN_WEATHER_HUMIDITY = "humidity"
        private const val COLUMN_WEATHER_PRECIPITATION = "precipitation"
        private const val COLUMN_WEATHER_WIND_SPEED = "wind_speed"
        private const val COLUMN_WEATHER_PRESSURE = "pressure"
        private const val COLUMN_WEATHER_IS_FORECAST = "is_forecast"
        private const val COLUMN_WEATHER_SOURCE = "source"

        //история
        private const val TABLE_HISTORY = "history"
        private const val COLUMN_HISTORY_ID = "hist_ID"
        private const val COLUMN_HISTORY_USER_ID = "user_ID"
        private const val COLUMN_HISTORY_PLANTING_ID = "planting_ID"
        private const val COLUMN_HISTORY_LOCATION_ID = "locations_ID"
        private const val COLUMN_HISTORY_MESSAGE = "message"
        private const val COLUMN_HISTORY_TIMESTAMP = "timestamp"
        private const val COLUMN_HISTORY_DATE = "date"



    }

    override fun onCreate(db: SQLiteDatabase){
        //создание  //таблица культура
        val createCropTable = """
            CREATE TABLE $TABLE_CROP (
                $COLUMN_CROP_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CROP_NAME TEXT NOT NULL,
                $COLUMN_CROP_CATEGORY TEXT NOT NULL,
                $COLUMN_CROP_OPTIMAL_TEMP_MIN REAL NOT NULL,
                $COLUMN_CROP_OPTIMAL_TEMP_MAX REAL NOT NULL,
                $COLUMN_CROP_OPTIMAL_HUMIDITY REAL NOT NULL,
                $COLUMN_CROP_GROWTH_DAYS INTEGER NOT NULL,
                $COLUMN_CROP_RISK_FACTORS TEXT NOT NULL,
                $COLUMN_CROP_SOIL_HUMIDITY REAL  NOT NULL
            ) """.trimIndent()

        //создание  //таблица дефекты
        val createDiseaseTable = """
            CREATE TABLE $TABLE_DISEASE (
                $COLUMN_DISEASE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_DISEASE_PLANTING_ID INTEGER NOT NULL,
                $COLUMN_DISEASE_IMAGE_PATH TEXT NOT NULL,
                $COLUMN_DISEASE_DETECTED_DISEASE TEXT NOT NULL,
                $COLUMN_DISEASE_CONFIDENCE REAL NOT NULL,
                $COLUMN_DISEASE_DETECTION_DATE INTEGER NOT NULL,
                $COLUMN_DISEASE_RECOMMENDED_ACTION TEXT NOT NULL,
                FOREIGN KEY ($COLUMN_DISEASE_PLANTING_ID) REFERENCES $TABLE_PLANTINGS($COLUMN_PLANTINGS_ID)
            )""".trimIndent()

        //создание  //таблица локация
        val  createLocationsTable = """
            CREATE TABLE $TABLE_LOCATIONS (
                $COLUMN_LOCATIONS_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_LOCATION_USER_ID INTEGER NOT NULL,
                $COLUMN_LOCATION_NAME TEXT NOT NULL,
                $COLUMN_LOCATION_REGION TEXT NOT NULL,
                $COLUMN_LOCATION_AREA REAL,
                $COLUMN_LOCATION_SOIL_TYPE TEXT,
                $COLUMN_LOCATION_DESCRIPTION TEXT,
                $COLUMN_LOCATION_TYPE TEXT,
                image_uri TEXT,
                FOREIGN KEY ($COLUMN_LOCATION_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
            )""".trimIndent()

        //создание  //таблица посадка
        val createPlantingsTable = """
            CREATE TABLE $TABLE_PLANTINGS (
                $COLUMN_PLANTINGS_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PLANTING_LOCATION_ID INTEGER NOT NULL,
                $COLUMN_PLANTING_CROP_ID INTEGER NOT NULL,
                $COLUMN_PLANTING_USER_ID INTEGER NOT NULL,
                $COLUMN_PLANTING_PLANTED_DATE INTEGER NOT NULL,
                $COLUMN_PLANTING_EXPECTED_HARVEST_DATE INTEGER NOT NULL,
                $COLUMN_PLANTING_AREA REAL NOT NULL,
                $COLUMN_PLANTING_STATUS TEXT DEFAULT 'active',
                $COLUMN_PLANTING_NAME TEXT NOT NULL,
                 image_uri TEXT, 
                FOREIGN KEY ($COLUMN_PLANTING_LOCATION_ID) REFERENCES $TABLE_LOCATIONS($COLUMN_LOCATIONS_ID),
                FOREIGN KEY ($COLUMN_PLANTING_CROP_ID) REFERENCES $TABLE_CROP($COLUMN_CROP_ID),
                FOREIGN KEY ($COLUMN_PLANTING_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
            ) """.trimIndent()


        //создание  //таблица рекомендация
        val createRecommendationsTable = """
            CREATE TABLE $TABLE_RECOMMENDATIONS (
                $COLUMN_RECOMMENDATIONS_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_RECOMMENDATION_USER_ID INTEGER NOT NULL,
                $COLUMN_RECOMMENDATION_PLANTING_ID INTEGER NOT NULL,
                $COLUMN_RECOMMENDATION_TYPE TEXT NOT NULL,
                $COLUMN_RECOMMENDATION_MESSAGE TEXT NOT NULL,
                $COLUMN_RECOMMENDATION_PRIORITY TEXT NOT NULL,
                $COLUMN_RECOMMENDATION_GENERATED_DATE INTEGER NOT NULL,
                $COLUMN_RECOMMENDATION_IS_COMPLETED INTEGER DEFAULT 0,
                FOREIGN KEY ($COLUMN_RECOMMENDATION_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID),
                FOREIGN KEY ($COLUMN_RECOMMENDATION_PLANTING_ID) REFERENCES $TABLE_PLANTINGS($COLUMN_PLANTINGS_ID)
            ) """.trimIndent()

        //создание  //таблица юзер
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_EMAIL TEXT UNIQUE NOT NULL,
                $COLUMN_USER_PASSWORD TEXT NOT NULL,
                $COLUMN_USER_NAME TEXT NOT NULL,
                $COLUMN_USER_ROLE TEXT DEFAULT 'user',
                 city TEXT,
                 photo_uri TEXT
            )""".trimIndent()

        //создание  //таблица погодное апи?
        val createWeatherDataTable = """
            CREATE TABLE $TABLE_WEATHER_DATA (
                $COLUMN_WEATHER_DATA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_WEATHER_LOCATION_ID INTEGER NOT NULL,
                $COLUMN_WEATHER_DATE INTEGER NOT NULL,
                $COLUMN_WEATHER_TEMPERATURE REAL NOT NULL,
                $COLUMN_WEATHER_HUMIDITY REAL NOT NULL,
                $COLUMN_WEATHER_PRECIPITATION REAL NOT NULL,
                $COLUMN_WEATHER_WIND_SPEED REAL NOT NULL,
                $COLUMN_WEATHER_PRESSURE REAL NOT NULL,
                $COLUMN_WEATHER_IS_FORECAST INTEGER DEFAULT 0,
                $COLUMN_WEATHER_SOURCE TEXT DEFAULT 'api',
                FOREIGN KEY ($COLUMN_WEATHER_LOCATION_ID) REFERENCES $TABLE_LOCATIONS($COLUMN_LOCATIONS_ID)
            )  """.trimIndent()

        //создание  //таблица история
        val createHistoryTable = """
    CREATE TABLE $TABLE_HISTORY (
        $COLUMN_HISTORY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COLUMN_HISTORY_USER_ID INTEGER NOT NULL,
        $COLUMN_HISTORY_PLANTING_ID INTEGER,
        $COLUMN_HISTORY_LOCATION_ID INTEGER,
        $COLUMN_HISTORY_MESSAGE TEXT NOT NULL,
        $COLUMN_HISTORY_TIMESTAMP INTEGER NOT NULL,
        $COLUMN_HISTORY_DATE TEXT NOT NULL,
        FOREIGN KEY ($COLUMN_HISTORY_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID),
        FOREIGN KEY ($COLUMN_HISTORY_PLANTING_ID) REFERENCES $TABLE_PLANTINGS($COLUMN_PLANTINGS_ID),
        FOREIGN KEY ($COLUMN_HISTORY_LOCATION_ID) REFERENCES $TABLE_LOCATIONS($COLUMN_LOCATIONS_ID)
    )""".trimIndent()

        //запросы создания
        db.execSQL(createUsersTable)
        db.execSQL(createCropTable)
        db.execSQL(createLocationsTable)
        db.execSQL(createPlantingsTable)
        db.execSQL(createWeatherDataTable)
        db.execSQL(createDiseaseTable)
        db.execSQL(createRecommendationsTable)
        db.execSQL(createHistoryTable)

    }
    //удаление
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Удаляем все таблицы при обновлении
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RECOMMENDATIONS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DISEASE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WEATHER_DATA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PLANTINGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LOCATIONS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CROP")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HISTORY")
        onCreate(db)
    }


    //таблица культура
    fun addCrop(crop: Crop): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CROP_NAME, crop.name)
            put(COLUMN_CROP_CATEGORY, crop.category)
            put(COLUMN_CROP_OPTIMAL_TEMP_MIN, crop.optimalTempMin)
            put(COLUMN_CROP_OPTIMAL_TEMP_MAX, crop.optimalTempMax)
            put(COLUMN_CROP_OPTIMAL_HUMIDITY, crop.optimalHumidity)
            put(COLUMN_CROP_GROWTH_DAYS, crop.growthDays)
            put(COLUMN_CROP_RISK_FACTORS, crop.riskFactors)
            put(COLUMN_CROP_SOIL_HUMIDITY, crop.soilHumidity)
        }
        return db.insert(TABLE_CROP, null, values)
    }

    fun getAllCrops(): List<Crop> {
        val db = readableDatabase
        val crops = mutableListOf<Crop>()
        val cursor = db.query(TABLE_CROP, null, null, null, null, null, null)

        while (cursor.moveToNext()) {
            crops.add(
                Crop(
                    cropID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CROP_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CROP_NAME)),
                    category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CROP_CATEGORY)),
                    optimalTempMin = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CROP_OPTIMAL_TEMP_MIN)),
                    optimalTempMax = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CROP_OPTIMAL_TEMP_MAX)),
                    optimalHumidity = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CROP_OPTIMAL_HUMIDITY)),
                    growthDays = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CROP_GROWTH_DAYS)),
                    riskFactors = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CROP_RISK_FACTORS)),
                    soilHumidity = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CROP_SOIL_HUMIDITY))
                )
            )
        }
        cursor.close()
        return crops
    }

    //таблица дефекты
    fun addDiseaseDetection(detection: DiseaseDetection): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DISEASE_PLANTING_ID, detection.planID)
            put(COLUMN_DISEASE_IMAGE_PATH, detection.imagePath)
            put(COLUMN_DISEASE_DETECTED_DISEASE, detection.detectedDisease)
            put(COLUMN_DISEASE_CONFIDENCE, detection.confidence)
            put(COLUMN_DISEASE_DETECTION_DATE, detection.detectionDate)
            put(COLUMN_DISEASE_RECOMMENDED_ACTION, detection.recommendedAction)
        }
        return db.insert(TABLE_DISEASE, null, values)
    }

    //таблица локация
    fun addLocation(location: Location): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_LOCATION_USER_ID, location.userID)
            put(COLUMN_LOCATION_NAME, location.name)
            put(COLUMN_LOCATION_REGION, location.region)
            put(COLUMN_LOCATION_AREA, location.area)
            put(COLUMN_LOCATION_SOIL_TYPE, location.soilType)
            put(COLUMN_LOCATION_DESCRIPTION, location.description)
            put(COLUMN_LOCATION_TYPE, location.type)
            put("image_uri", location.imageUri)
        }
        return db.insert(TABLE_LOCATIONS, null, values)
    }

    fun getLocationsByUser(userId: Int): List<Location> {
        val db = readableDatabase
        val locations = mutableListOf<Location>()
        val cursor = db.query(
            TABLE_LOCATIONS,
            null,
            "$COLUMN_LOCATION_USER_ID = ?",
            arrayOf(userId.toString()),
            null, null, null
        )

        while (cursor.moveToNext()) {
            locations.add(
                Location(
                    locID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LOCATIONS_ID)),
                    userID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LOCATION_USER_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION_NAME)),
                    region = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION_REGION)),
                    area = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LOCATION_AREA)),
                    soilType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION_SOIL_TYPE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION_DESCRIPTION)),
                    type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION_TYPE)),
                    imageUri  = cursor.getString(cursor.getColumnIndexOrThrow("image_uri"))

                )
            )
        }
        cursor.close()
        return locations
    }
    //таблица посадка
    fun addPlanting(planting: Planting): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PLANTING_LOCATION_ID, planting.locID)
            put(COLUMN_PLANTING_CROP_ID, planting.cropID)
            put(COLUMN_PLANTING_USER_ID, planting.userID)
            put(COLUMN_PLANTING_PLANTED_DATE, planting.plantedDate)
            put(COLUMN_PLANTING_EXPECTED_HARVEST_DATE, planting.expectedHarvestDate)
            put(COLUMN_PLANTING_AREA, planting.area)
            put(COLUMN_PLANTING_STATUS, planting.status)
            put(COLUMN_PLANTING_NAME, planting.name)
            put("image_uri", planting.imageUri)
        }
        return db.insert(TABLE_PLANTINGS, null, values)
    }

    fun getPlantingsByUser(userId: Int): List<Planting> {
        val db = readableDatabase
        val plantings = mutableListOf<Planting>()
        val cursor = db.query(
            TABLE_PLANTINGS,
            null,
            "$COLUMN_PLANTING_USER_ID = ?",
            arrayOf(userId.toString()),
            null, null, "$COLUMN_PLANTING_PLANTED_DATE DESC"
        )

        while (cursor.moveToNext()) {
            plantings.add(
                Planting(
                    planID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLANTINGS_ID)),
                    locID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLANTING_LOCATION_ID)),
                    cropID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLANTING_CROP_ID)),
                    userID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLANTING_USER_ID)),
                    plantedDate = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PLANTING_PLANTED_DATE)),
                    expectedHarvestDate = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PLANTING_EXPECTED_HARVEST_DATE)),
                    area = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PLANTING_AREA)),
                    status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLANTING_STATUS)),
                    imageUri = cursor.getString(cursor.getColumnIndexOrThrow("image_uri")),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLANTING_NAME))
                )
            )
        }
        cursor.close()
        return plantings
    }
    //таблица рекомендация
    fun addRecommendation(recommendation: Recommendation): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_RECOMMENDATION_USER_ID, recommendation.userID)
            put(COLUMN_RECOMMENDATION_PLANTING_ID, recommendation.planID)
            put(COLUMN_RECOMMENDATION_TYPE, recommendation.type)
            put(COLUMN_RECOMMENDATION_MESSAGE, recommendation.message)
            put(COLUMN_RECOMMENDATION_PRIORITY, recommendation.priority)
            put(COLUMN_RECOMMENDATION_GENERATED_DATE, recommendation.generatedDate)
            put(COLUMN_RECOMMENDATION_IS_COMPLETED, if (recommendation.isCompleted) 1 else 0)
        }
        return db.insert(TABLE_RECOMMENDATIONS, null, values)
    }

    fun getActiveRecommendationsForUser(userId: Int): List<Recommendation> {
        val db = readableDatabase
        val recommendations = mutableListOf<Recommendation>()
        val cursor = db.query(
            TABLE_RECOMMENDATIONS,
            null,
            "$COLUMN_RECOMMENDATION_USER_ID = ? AND $COLUMN_RECOMMENDATION_IS_COMPLETED = 0",
            arrayOf(userId.toString()),
            null, null, "$COLUMN_RECOMMENDATION_PRIORITY DESC, $COLUMN_RECOMMENDATION_GENERATED_DATE DESC"
        )

        while (cursor.moveToNext()) {
            recommendations.add(
                Recommendation(
                    recomID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECOMMENDATIONS_ID)),
                    userID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECOMMENDATION_USER_ID)),
                    planID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECOMMENDATION_PLANTING_ID)),
                    type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECOMMENDATION_TYPE)),
                    message = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECOMMENDATION_MESSAGE)),
                    priority = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECOMMENDATION_PRIORITY)),
                    generatedDate = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_RECOMMENDATION_GENERATED_DATE)),
                    isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECOMMENDATION_IS_COMPLETED)) == 1
                )
            )
        }
        cursor.close()
        return recommendations
    }
    //таблица юзер

    fun addUser(user: User): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_EMAIL, user.email)
            put(COLUMN_USER_PASSWORD, user.password)
            put(COLUMN_USER_NAME, user.name)
            put(COLUMN_USER_ROLE, user.role)
            put("city", user.city)
            put("photo_uri", user.photoUri)
        }
        return db.insert(TABLE_USERS, null, values)
    }

    fun updateUser(user: User): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("email", user.email)
            put("password", user.password)
            put("name", user.name)
            put("role", user.role)
            put("city", user.city)
            put("photo_uri", user.photoUri)
        }
        return db.update(
            "users",
            values,
            "user_ID = ?",
            arrayOf(user.userID.toString())
        )
    }




    fun getUserByEmail(email: String): User? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            "$COLUMN_USER_EMAIL = ?",
            arrayOf(email),
            null, null, null
        )

        return if (cursor.moveToFirst()) {
            User(
                userID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)),
                role = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ROLE)),
                city = cursor.getString(cursor.getColumnIndexOrThrow("city")),
                photoUri = cursor.getString(cursor.getColumnIndexOrThrow("photo_uri"))
            )
        } else {
            null
        }.also { cursor.close() }
    }
    //обновление  //таблица погодное апи?
    fun addWeatherData(weatherData: WeatherData): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_WEATHER_LOCATION_ID, weatherData.locID)
            put(COLUMN_WEATHER_DATE, weatherData.date)
            put(COLUMN_WEATHER_TEMPERATURE, weatherData.temperature)
            put(COLUMN_WEATHER_HUMIDITY, weatherData.humidity)
            put(COLUMN_WEATHER_PRECIPITATION, weatherData.precipitation)
            put(COLUMN_WEATHER_WIND_SPEED, weatherData.windSpeed)
            put(COLUMN_WEATHER_PRESSURE, weatherData.pressure)
            put(COLUMN_WEATHER_IS_FORECAST, if (weatherData.isForecast) 1 else 0)
            put(COLUMN_WEATHER_SOURCE, weatherData.source)
        }
        return db.insert(TABLE_WEATHER_DATA, null, values)
    }


    fun addHistory(history: History): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_HISTORY_USER_ID, history.userID)
            put(COLUMN_HISTORY_PLANTING_ID, history.planID)
            put(COLUMN_HISTORY_LOCATION_ID, history.locID)
            put(COLUMN_HISTORY_MESSAGE, history.message)
            put(COLUMN_HISTORY_TIMESTAMP, history.timestamp)
            put(COLUMN_HISTORY_DATE, history.date)
        }

        val result = db.insert(TABLE_HISTORY, null, values)
        db.close()
        return result
    }

    fun getHistoryByUser(userId: Int): List<History> {
        val db = readableDatabase
        val historyList = mutableListOf<History>()
        val cursor = db.query(
            TABLE_HISTORY,
            null,
            "$COLUMN_HISTORY_USER_ID= ?",
            arrayOf(userId.toString()),
            null, null, "$COLUMN_HISTORY_TIMESTAMP DESC"
        )

        while (cursor.moveToNext()) {
            historyList.add(
                History(
                    histID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HISTORY_ID)),
                    userID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HISTORY_USER_ID)),
                    planID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HISTORY_PLANTING_ID)),
                    locID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HISTORY_LOCATION_ID)),
                    message = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HISTORY_MESSAGE)),
                    timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_HISTORY_TIMESTAMP)),
                    date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HISTORY_DATE))
                )
            )
        }
        cursor.close()
        return historyList
    }


    fun getHistoryByDateRange(userId: Int, startDate: String, endDate: String): List<History> {
        val db = readableDatabase
        val historyList = mutableListOf<History>()
        val cursor = db.rawQuery(
            """
        SELECT * FROM history
        WHERE user_ID = ? AND date BETWEEN ? AND ?
        ORDER BY timestamp DESC
        """.trimIndent(),
            arrayOf(userId.toString(), startDate, endDate)
        )

        while (cursor.moveToNext()) {
            historyList.add(
                History(
                    histID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HISTORY_ID)),
                    userID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HISTORY_USER_ID)),
                    planID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HISTORY_PLANTING_ID)),
                    locID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HISTORY_LOCATION_ID)),
                    message = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HISTORY_MESSAGE)),
                    timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_HISTORY_TIMESTAMP)),
                    date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HISTORY_DATE))
                )
            )
        }
        cursor.close()
        return historyList
    }


    fun getWeatherDataForLocation(locationId: Int): List<WeatherData> {
        val db = readableDatabase
        val weatherDataList = mutableListOf<WeatherData>()
        val cursor = db.query(
            TABLE_WEATHER_DATA,
            null,
            "$COLUMN_WEATHER_LOCATION_ID = ?",
            arrayOf(locationId.toString()),
            null, null, "$COLUMN_WEATHER_DATE DESC"
        )

        while (cursor.moveToNext()) {
            weatherDataList.add(
                WeatherData(
                    wdID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WEATHER_DATA_ID)),
                    locID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WEATHER_LOCATION_ID)),
                    date = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_WEATHER_DATE)),
                    temperature = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_WEATHER_TEMPERATURE)),
                    humidity = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_WEATHER_HUMIDITY)),
                    precipitation = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_WEATHER_PRECIPITATION)),
                    windSpeed = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_WEATHER_WIND_SPEED)),
                    pressure = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_WEATHER_PRESSURE)),
                    isForecast = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WEATHER_IS_FORECAST)) == 1,
                    source = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WEATHER_SOURCE))
                )
            )
        }
        cursor.close()
        return weatherDataList
    }

    fun getCropById(cropID: Int): Crop? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_CROP,
            null,
            "$COLUMN_CROP_ID = ?",
            arrayOf(cropID.toString()),
            null, null, null
        )

        return if (cursor.moveToFirst()) {
            Crop(
                cropID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CROP_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CROP_NAME)),
                category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CROP_CATEGORY)),
                optimalTempMin = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CROP_OPTIMAL_TEMP_MIN)),
                optimalTempMax = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CROP_OPTIMAL_TEMP_MAX)),
                optimalHumidity = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CROP_OPTIMAL_HUMIDITY)),
                growthDays = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CROP_GROWTH_DAYS)),
                riskFactors = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CROP_RISK_FACTORS)),
                soilHumidity = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CROP_SOIL_HUMIDITY))
            )
        } else {
            null
        }.also { cursor.close() }
    }

    fun completeRecommendation(recommendationId: Int): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_RECOMMENDATION_IS_COMPLETED, 1)
        }
        return db.update(
            TABLE_RECOMMENDATIONS,
            values,
            "$COLUMN_RECOMMENDATIONS_ID = ?",
            arrayOf(recommendationId.toString())
        )
    }

    fun getLocationById(locationId: Int): Location? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_LOCATIONS,
            null,
            "$COLUMN_LOCATIONS_ID = ?",
            arrayOf(locationId.toString()),
            null, null, null
        )

        return if (cursor.moveToFirst()) {
            Location(
                locID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LOCATIONS_ID)),
                userID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LOCATION_USER_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION_NAME)),
                region = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION_REGION)),
                area = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LOCATION_AREA)),
                soilType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION_SOIL_TYPE)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION_DESCRIPTION)),
                type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION_TYPE)),
                imageUri = cursor.getString(cursor.getColumnIndexOrThrow("image_uri"))
            )
        } else {
            null
        }.also { cursor.close() }


    }

    fun getPlantingById(plantingId: Int): Planting? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_PLANTINGS,
            null,
            "$COLUMN_PLANTINGS_ID = ?",
            arrayOf(plantingId.toString()),
            null, null, null
        )

        val planting = if (cursor.moveToFirst()) {
            Planting(
                planID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLANTINGS_ID)),
                locID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLANTING_LOCATION_ID)),
                cropID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLANTING_CROP_ID)),
                userID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLANTING_USER_ID)),
                plantedDate = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PLANTING_PLANTED_DATE)),
                expectedHarvestDate = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PLANTING_EXPECTED_HARVEST_DATE)),
                area = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PLANTING_AREA)),
                status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLANTING_STATUS)),
                imageUri = cursor.getString(cursor.getColumnIndexOrThrow("image_uri")),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLANTING_NAME))
            )
        } else null

        cursor.close()
        return planting
    }


    // --- Planting ---
    fun updatePlanting(planting: Planting): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PLANTING_LOCATION_ID, planting.locID)
            put(COLUMN_PLANTING_CROP_ID, planting.cropID)
            put(COLUMN_PLANTING_USER_ID, planting.userID)
            put(COLUMN_PLANTING_PLANTED_DATE, planting.plantedDate)
            put(COLUMN_PLANTING_EXPECTED_HARVEST_DATE, planting.expectedHarvestDate)
            put(COLUMN_PLANTING_AREA, planting.area)
            put(COLUMN_PLANTING_STATUS, planting.status)
            put("image_uri", planting.imageUri)
            put(COLUMN_PLANTING_NAME,planting.name)
        }
        return db.update(
            TABLE_PLANTINGS,
            values,
            "$COLUMN_PLANTINGS_ID = ?",
            arrayOf(planting.planID.toString())
        )
    }

    // --- Location ---
    fun updateLocation(location: Location): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_LOCATION_USER_ID, location.userID)
            put(COLUMN_LOCATION_NAME, location.name)
            put(COLUMN_LOCATION_REGION, location.region)
            put(COLUMN_LOCATION_AREA, location.area)
            put(COLUMN_LOCATION_SOIL_TYPE, location.soilType)
            put(COLUMN_LOCATION_DESCRIPTION, location.description)
            put(COLUMN_LOCATION_TYPE, location.type)
            put("image_uri", location.imageUri)
        }
        return db.update(
            TABLE_LOCATIONS,
            values,
            "$COLUMN_LOCATIONS_ID = ?",
            arrayOf(location.locID.toString())
        )
    }






    /**
     * Возвращает средние климатические показатели (температура, влажность, осадки)
     * для каждой локации за указанный период.
     */
    fun getAverageClimateStats(startDate: String, endDate: String): List<ClimateStats> {
        val db = readableDatabase
        val query = """
        SELECT 
            location_id,
            AVG(temperature) AS avg_temp,
            AVG(humidity) AS avg_humidity,
            AVG(precipitation) AS avg_rain
        FROM weather_data
        WHERE date BETWEEN ? AND ?
        GROUP BY location_id
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(startDate, endDate))
        val results = mutableListOf<ClimateStats>()

        if (cursor.moveToFirst()) {
            do {
                val locationId = cursor.getInt(cursor.getColumnIndexOrThrow("location_id"))
                val avgTemp = cursor.getDouble(cursor.getColumnIndexOrThrow("avg_temp"))
                val avgHumidity = cursor.getDouble(cursor.getColumnIndexOrThrow("avg_humidity"))
                val avgRain = cursor.getDouble(cursor.getColumnIndexOrThrow("avg_rain"))

                results.add(ClimateStats(locationId, avgTemp, avgHumidity, avgRain))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return results
    }

    /**
     * Сравнивает средние фактические климатические условия с оптимальными для каждой культуры.
     */
    fun compareActualToOptimalConditions(): List<CropConditionComparison> {
        val db = readableDatabase
        val query = """
        SELECT 
            c.id AS crop_id,
            c.name AS crop_name,
            AVG(w.temperature) AS avg_temp,
            c.optimal_temp_min,
            c.optimal_temp_max,
            (AVG(w.temperature) BETWEEN c.optimal_temp_min AND c.optimal_temp_max) AS within_optimal_range
        FROM planting p
        JOIN crop c ON p.crop_id = c.id
        JOIN weather_data w ON p.location_id = w.location_id
        GROUP BY c.id, c.name
    """.trimIndent()

        val cursor = db.rawQuery(query, null)
        val results = mutableListOf<CropConditionComparison>()

        if (cursor.moveToFirst()) {
            do {
                val cropId = cursor.getInt(cursor.getColumnIndexOrThrow("crop_id"))
                val cropName = cursor.getString(cursor.getColumnIndexOrThrow("crop_name"))
                val avgTemp = cursor.getDouble(cursor.getColumnIndexOrThrow("avg_temp"))
                val minTemp = cursor.getDouble(cursor.getColumnIndexOrThrow("optimal_temp_min"))
                val maxTemp = cursor.getDouble(cursor.getColumnIndexOrThrow("optimal_temp_max"))
                val withinRange = cursor.getInt(cursor.getColumnIndexOrThrow("within_optimal_range")) == 1

                results.add(CropConditionComparison(cropId, cropName, avgTemp, minTemp, maxTemp, withinRange))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return results
    }

    data class ClimateStats(
        val locationId: Int,
        val avgTemperature: Double,
        val avgHumidity: Double,
        val avgPrecipitation: Double
    )

    // Сравнение фактических условий с оптимальными
    data class CropConditionComparison(
        val cropId: Int,
        val cropName: String,
        val avgTemperature: Double,
        val optimalMinTemp: Double,
        val optimalMaxTemp: Double,
        val withinOptimalRange: Boolean
    )


    fun deletePlanting(plantingId: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_PLANTINGS, "$COLUMN_PLANTINGS_ID = ?", arrayOf(plantingId.toString()))
    }

    fun deleteLocation(locationId: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_LOCATIONS, "$COLUMN_LOCATIONS_ID = ?", arrayOf(locationId.toString()))
    }


}