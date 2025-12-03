package com.example.climatedata.fragment_createSowing

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.climatedata.R
import com.example.climatedata.data.db.ClimateDatabase
import com.example.climatedata.data.models.Location

class CreateSowingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_sowing)

        val db = ClimateDatabase.Companion.getInstance(this)

        // Загружаем первый фрагмент только один раз
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.createSowingContainer, ChoiceCultureFragment())
                .commit()
        }

        val backButton = findViewById<ImageView>(R.id.back)
        backButton.setOnClickListener { handleBackNavigation() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { handleBackNavigation() }
        })
    }

    private fun handleBackNavigation() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 0) {
            // Если есть фрагменты в back stack — возвращаемся
            fragmentManager.popBackStack()
        } else {
            // Если фрагментов нет — закрываем Activity
            finish()
        }
    }
}