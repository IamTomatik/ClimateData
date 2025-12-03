package com.example.climatedata

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.climatedata.data.authen.AuthenticationManager
import com.example.climatedata.data.db.ClimateDatabase
import com.example.climatedata.data.models.User
import com.example.climatedata.fragment_createLocation.MyLocationFragment

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val authManager = AuthenticationManager(requireContext())
        val db = ClimateDatabase.getInstance(requireContext())


        // --- Получаем email текущего пользователя ---
        val currentUserEmail = authManager.getCurrentUsername()  // возвращает имя пользователя, но мы можем хранить email вместо имени при login/register
        val user: User? = currentUserEmail?.let { email ->
            db.getUserByEmail(email)
        }

        // --- Подставляем данные на экран ---
        view.findViewById<TextView>(R.id.user_name).text = user?.name ?: "Неизвестный пользователь"
        view.findViewById<TextView>(R.id.user_email).text = user?.email ?: "-"

        // --- Кнопка "Выйти" ---
        view.findViewById<LinearLayout>(R.id.exit)?.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Выход")
                .setMessage("Вы уверены, что хотите выйти?")
                .setPositiveButton("Да") { dialog, _ ->
                    authManager.logout() // теперь сброс через AuthenticationManager
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    activity?.finish()
                    dialog.dismiss()
                }
                .setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }

        // --- Переход на экран обновления данных ---
        view.findViewById<LinearLayout>(R.id.update_date)?.setOnClickListener {
            startActivity(Intent(requireContext(), ActivityUpdateDate::class.java))
        }

        // --- Переход на экран обновления пароля ---
        view.findViewById<LinearLayout>(R.id.update_password)?.setOnClickListener {
            startActivity(Intent(requireContext(), ActivityUpdatePassword::class.java))
        }

        // --- Переход на MyLocationFragment ---
        view.findViewById<LinearLayout>(R.id.mylocation)?.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, MyLocationFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}
