package com.example.climatedata.fragment_createLocation

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.climatedata.R

class CreateLocation: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_location)

        // Загружаем первый фрагмент только один раз
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.createLocationContainer, ChoiceNameLocation())
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