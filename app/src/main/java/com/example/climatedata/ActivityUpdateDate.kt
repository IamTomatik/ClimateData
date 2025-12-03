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

class ActivityUpdateDate : AppCompatActivity() {

    private lateinit var editEmail: EditText
    private lateinit var editName: EditText
    private lateinit var editCity: EditText
    private lateinit var buttonSave: Button
    private lateinit var backButton: ImageView
    private lateinit var db: ClimateDatabase

    private var originalEmail: String = ""
    private var originalName: String = ""
    private var originalCity: String = ""

    private var currentUser: User? = null
    private lateinit var manager: AuthenticationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_date)

        editEmail = findViewById(R.id.editEmail)
        editName = findViewById(R.id.editName)
        editCity = findViewById(R.id.editCity)
        buttonSave = findViewById(R.id.button_save)
        backButton = findViewById(R.id.back)

        db = ClimateDatabase.getInstance(this)
        manager = AuthenticationManager(this)

        val currentEmail = manager.getCurrentUsername()
        currentUser = db.getUserByEmail(currentEmail)

        currentUser?.let { user ->
            originalEmail = user.email
            originalName = user.name
            originalCity = user.city ?: ""

            editEmail.setText(user.email)
            editName.setText(user.name)
            editCity.setText(user.city ?: "")
        }

        setInputsEnabled(false)

        listOf(editEmail, editName, editCity).forEach { field ->
            field.setOnClickListener { setInputsEnabled(true) }
        }

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val changed = editEmail.text.toString() != originalEmail ||
                        editName.text.toString() != originalName ||
                        editCity.text.toString() != originalCity

                buttonSave.isEnabled = changed
                buttonSave.backgroundTintList = ContextCompat.getColorStateList(
                    this@ActivityUpdateDate,
                    if (changed) R.color.black else R.color.gray1
                )
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        editEmail.addTextChangedListener(watcher)
        editName.addTextChangedListener(watcher)
        editCity.addTextChangedListener(watcher)

        buttonSave.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Сохранить изменения?")
                .setMessage("Вы уверены, что хотите обновить данные?")
                .setPositiveButton("Да") { dialog, _ ->
                    currentUser?.let {
                        val updatedUser = it.copy(
                            email = editEmail.text.toString(),
                            name = editName.text.toString(),
                            city = editCity.text.toString()
                        )
                        db.updateUser(updatedUser)

                        originalEmail = updatedUser.email
                        originalName = updatedUser.name
                        originalCity = updatedUser.city ?: ""
                        currentUser = updatedUser
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

        backButton.setOnClickListener { finish() }
    }

    private fun setInputsEnabled(enabled: Boolean) {
        val color = if (enabled) R.color.black else R.color.gray1
        listOf(editEmail, editName, editCity).forEach {
            it.isEnabled = enabled
            it.setTextColor(ContextCompat.getColor(this, color))
        }
    }
}
