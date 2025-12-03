package com.example.climatedata

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.view.MotionEvent
import android.graphics.Rect
import android.view.inputmethod.InputMethodManager
import android.view.View
import com.example.climatedata.data.authen.AuthenticationManager
import com.example.climatedata.data.db.ClimateDatabase

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentification_sign_in)

        val loginInput: EditText = findViewById(R.id.editTextText)
        val passwordInput: EditText = findViewById(R.id.editTextTextPassword2)
        val continueButton: Button = findViewById(R.id.button)
        val textRegistration: TextView = findViewById(R.id.textregestration)


        val db = ClimateDatabase.getInstance(this)


        val authManager = AuthenticationManager(this)
        if (authManager.isLoggedIn()) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }


        fun checkFields() {
            val isFilled = loginInput.text.isNotEmpty() && passwordInput.text.isNotEmpty()
            continueButton.isEnabled = isFilled
            continueButton.backgroundTintList =
                getColorStateList(if (isFilled) R.color.black else R.color.gray)
        }

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { checkFields() }
            override fun afterTextChanged(s: Editable?) {}
        }

        loginInput.addTextChangedListener(watcher)
        passwordInput.addTextChangedListener(watcher)
        checkFields()

        textRegistration.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        continueButton.setOnClickListener {
            val email = loginInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            val success = authManager.loginUser(email, password, db)
            if (success) {
                // Сохраняем пользователя через AuthenticationManager
                val user = db.getUserByEmail(email)!!
                authManager.saveCurrentUser(user.userID.toLong(), user.name)

                Toast.makeText(this, "Вход выполнен успешно", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            currentFocus?.let { view ->
                if (view is EditText) {
                    val outRect = Rect()
                    view.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                        hideKeyboard(view)
                        view.clearFocus()
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
