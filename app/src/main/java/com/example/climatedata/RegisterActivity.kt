package com.example.climatedata

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.climatedata.data.authen.AuthenticationManager
import com.example.climatedata.data.db.ClimateDatabase

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentification_register)

        val emailInput: EditText = findViewById(R.id.editTextText)
        val nameInput: EditText = findViewById(R.id.editTextTextName)
        val passwordInput: EditText = findViewById(R.id.editTextTextPassword)
        val continueButton: Button = findViewById(R.id.button)
        val textVhod: TextView = findViewById(R.id.textVhod)

        val authManager = AuthenticationManager(this)
        val db = ClimateDatabase.getInstance(this)


        fun checkFields() {
            val isFilled = emailInput.text.isNotEmpty() &&
                    nameInput.text.isNotEmpty() &&
                    passwordInput.text.isNotEmpty()
            continueButton.isEnabled = isFilled
            continueButton.backgroundTintList =
                getColorStateList(if (isFilled) R.color.black else R.color.gray)
        }

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { checkFields() }
            override fun afterTextChanged(s: Editable?) {}
        }

        emailInput.addTextChangedListener(watcher)
        nameInput.addTextChangedListener(watcher)
        passwordInput.addTextChangedListener(watcher)
        checkFields()

        textVhod.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        continueButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val name = nameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val hashedPassword = authManager.hashPassword(password)


            val success = authManager.registerUser(email, name, hashedPassword, db)

            if (success) {
                // Сохраняем нового пользователя сразу через AuthenticationManager
                val user = db.getUserByEmail(email)!!
                authManager.saveCurrentUser(user.userID.toLong(), user.name)

                Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Пользователь с таким email уже существует", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
