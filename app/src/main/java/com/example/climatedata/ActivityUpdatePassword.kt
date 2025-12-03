package com.example.climatedata

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.climatedata.data.authen.AuthenticationManager
import com.example.climatedata.data.db.ClimateDatabase
import com.example.climatedata.data.models.User

class ActivityUpdatePassword : AppCompatActivity() {

    private lateinit var editOldPassword: EditText
    private lateinit var editNewPassword: EditText
    private lateinit var editConfirmPassword: EditText
    private lateinit var buttonSave: Button
    private lateinit var db: ClimateDatabase

    private var currentUser: User? = null
    private var originalOldPassword: String = ""
    private var originalNewPassword: String = ""
    private var originalConfirmPassword: String = ""
    private lateinit var manager: AuthenticationManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_password)

        editOldPassword = findViewById(R.id.editTextText)
        editNewPassword = findViewById(R.id.editTextTextName)
        editConfirmPassword = findViewById(R.id.editTextTextPassword)
        buttonSave = findViewById(R.id.button_save)

        db = ClimateDatabase.getInstance(this)
        manager = AuthenticationManager(this)

        val currentEmail = manager.getCurrentUsername()
        currentUser = db.getUserByEmail(currentEmail)

        currentUser?.let { user ->
            originalOldPassword = user.password
            originalNewPassword = ""
            originalConfirmPassword = ""

            editOldPassword.setText(originalOldPassword)
            editNewPassword.setText(originalNewPassword)
            editConfirmPassword.setText(originalConfirmPassword)
        }

        setInputsEnabled(false)

        listOf(editOldPassword, editNewPassword, editConfirmPassword).forEach { field ->
            field.setOnClickListener { setInputsEnabled(true) }
        }

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val changed = editOldPassword.text.toString() != originalOldPassword ||
                        editNewPassword.text.toString() != originalNewPassword ||
                        editConfirmPassword.text.toString() != originalConfirmPassword

                buttonSave.isEnabled = changed
                buttonSave.backgroundTintList = ContextCompat.getColorStateList(
                    this@ActivityUpdatePassword,
                    if (changed) R.color.black else R.color.gray1
                )
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        editOldPassword.addTextChangedListener(watcher)
        editNewPassword.addTextChangedListener(watcher)
        editConfirmPassword.addTextChangedListener(watcher)

        buttonSave.setOnClickListener {
            if (editNewPassword.text.toString() != editConfirmPassword.text.toString()) {
                AlertDialog.Builder(this)
                    .setTitle("Ошибка")
                    .setMessage("Новый пароль и подтверждение не совпадают")
                    .setPositiveButton("ОК") { dialog, _ -> dialog.dismiss() }
                    .show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("Сохранить изменения?")
                .setMessage("Вы уверены, что хотите обновить пароль?")
                .setPositiveButton("Да") { dialog, _ ->
                    currentUser?.let {
                        it.password = editNewPassword.text.toString()
                        db.updateUser(it)

                    }
                    setInputsEnabled(false)
                    buttonSave.isEnabled = false
                    buttonSave.backgroundTintList =
                        ContextCompat.getColorStateList(this, R.color.gray1)
                    dialog.dismiss()
                }
                .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        findViewById<ImageView>(R.id.back)?.setOnClickListener {
            finish()
        }
    }

    private fun setInputsEnabled(enabled: Boolean) {
        val color = if (enabled) R.color.black else R.color.gray1
        listOf(editOldPassword, editNewPassword, editConfirmPassword).forEach {
            it.isFocusable = enabled
            it.isFocusableInTouchMode = enabled
            it.setTextColor(ContextCompat.getColor(this, color))
        }
    }
}
