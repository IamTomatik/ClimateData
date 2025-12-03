package com.example.climatedata

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.climatedata.databinding.HomeActivityBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: HomeActivityBinding
    private var currentFragmentTag: String? = null
    private lateinit var buttonAnalysis: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val menu = binding.bottomNavigationView.menu


        val icons = mapOf(
            R.id.home to Pair(R.drawable.ic_home_activ, R.drawable.ic_home_inactiv),
            R.id.plant to Pair(R.drawable.ic_plant_activ, R.drawable.ic_plant_inactiv),
            R.id.analysis to Pair(R.drawable.ic_analysis_activ, R.drawable.ic_analysis_inactiv),
            R.id.profile to Pair(R.drawable.ic_person_activ, R.drawable.ic_person_inactiv)
        )


        replaceFragment(HomeFragment(), "home")
        menu.findItem(R.id.home)?.icon = getDrawable(icons[R.id.home]?.first ?: R.drawable.ic_home_activ)

        // Обработчик выбора пункта BottomNavigationView
        binding.bottomNavigationView.setOnItemSelectedListener { item: MenuItem ->
            if (item.itemId == R.id.add) return@setOnItemSelectedListener false

            // Сбрасываем все иконки на неактивные
            icons.forEach { (id, pair) ->
                menu.findItem(id)?.icon = getDrawable(pair.second)
            }


            icons[item.itemId]?.first?.let { activeIcon ->
                item.icon = getDrawable(activeIcon)
            }

            // Переключаем фрагменты
            when (item.itemId) {
                R.id.home -> replaceFragment(HomeFragment(), "home")
                R.id.plant -> replaceFragment(PlantFragment(), "plant")
                R.id.analysis -> replaceFragment(AnalysisFragment(), "analysis")
                R.id.profile -> replaceFragment(ProfileFragment(), "profile")
            }


            showFabAndMenu()
            true
        }


        binding.fabAdd.setOnClickListener {
            hideFabAndMenu() // скрываем таббар и кнопку

            val bottomSheet = AddOptionsBottomSheet()
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)


            supportFragmentManager.setFragmentResultListener("bottomSheetClosed", this) { _, _ ->
                showFabAndMenu()
            }
        }


    }

    // Меняет фрагмент только если он новый
    private fun replaceFragment(fragment: Fragment, tag: String) {
        if (currentFragmentTag == tag) return
        currentFragmentTag = tag

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.frame_layout, fragment)
            .commit()
    }

    // Скрыть FAB и BottomNavigationView с анимацией
    private fun hideFabAndMenu() {
        binding.fabAdd.animate()
            .translationY(200f)
            .alpha(0f)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setDuration(200)
            .start()

        binding.bottomNavigationView.animate()
            .translationY(150f)
            .alpha(0f)
            .setDuration(200)
            .start()
    }

    // Показать FAB и BottomNavigationView обратно с анимацией
    private fun showFabAndMenu() {
        binding.fabAdd.animate()
            .translationY(0f)
            .alpha(1f)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setDuration(200)
            .start()

        binding.bottomNavigationView.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(200)
            .start()
    }

    fun highlightNavBarItem(itemId: Int) {
        val menu = binding.bottomNavigationView.menu

        val icons = mapOf(
            R.id.home to Pair(R.drawable.ic_home_activ, R.drawable.ic_home_inactiv),
            R.id.plant to Pair(R.drawable.ic_plant_activ, R.drawable.ic_plant_inactiv),
            R.id.analysis to Pair(R.drawable.ic_analysis_activ, R.drawable.ic_analysis_inactiv),
            R.id.profile to Pair(R.drawable.ic_person_activ, R.drawable.ic_person_inactiv)
        )
        icons.forEach { (id, pair) ->
            menu.findItem(id)?.icon = getDrawable(pair.second)
        }


        menu.findItem(itemId)?.icon = getDrawable(icons[itemId]?.first ?: R.drawable.ic_analysis_activ)
        binding.bottomNavigationView.selectedItemId = itemId
    }
}
