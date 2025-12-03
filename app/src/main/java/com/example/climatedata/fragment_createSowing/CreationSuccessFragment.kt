package com.example.climatedata.fragment_createSowing

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.climatedata.R
import com.example.climatedata.HomeActivity

class CreationSuccessFragment : Fragment() {

    private lateinit var btnPlant: Button
    private lateinit var btnHome: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_creation_success, container, false)

        btnPlant = view.findViewById(R.id.plant)    // кнопка "мои посевы"
        btnHome = view.findViewById(R.id.home)    // кнопка "главная"

        btnPlant.isEnabled = true
        btnHome.isEnabled = true

        // Открыть HomeActivity с PlantFragment
        btnPlant.setOnClickListener {
            val intent = Intent(requireContext(), HomeActivity::class.java)
            intent.putExtra("fragmentToOpen", "PlantFragment")
            startActivity(intent)
        }

        // Открыть HomeActivity с HomeFragment
        btnHome.setOnClickListener {
            val intent = Intent(requireContext(), HomeActivity::class.java)
            intent.putExtra("fragmentToOpen", "HomeFragment")
            startActivity(intent)
        }

        return view
    }
}
