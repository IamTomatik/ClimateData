package com.example.climatedata.data.authen

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.climatedata.data.db.ClimateDatabase
import com.example.climatedata.data.models.User

class AuthenticationManager(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val bcrypt = BCrypt.verifyer()

    fun hashPassword(password: String): String {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray())
    }

    fun verifyPassword(password: String, hash: String): Boolean {
        return bcrypt.verify(password.toCharArray(), hash).verified
    }

    fun saveCurrentUser(userId: Long, username: String) {
        prefs.edit().apply {
            putLong("current_user_id", userId)
            putString("current_username", username)
            putBoolean("is_logged_in", true)
            apply()
        }
    }

    fun getCurrentUserId(): Long = prefs.getLong("current_user_id", -1)
    fun getCurrentUsername(): String = prefs.getString("current_username", "") ?: ""
    fun isLoggedIn(): Boolean = prefs.getBoolean("is_logged_in", false)

    fun logout() {
        prefs.edit().clear().apply()
    }

    fun userExists(email: String, db: ClimateDatabase): Boolean {
        val user = db.getUserByEmail(email)
        Log.d("AuthManager", "userExists check for $email: $user")
        return user != null
    }

    // --- регистрация с логами ---
    fun registerUser(email: String, name: String, hashedPassword: String, db: ClimateDatabase): Boolean {
        Log.d("AuthManager", "Попытка регистрации пользователя: email=$email, name=$name")

        if (userExists(email, db)) {
            Log.d("AuthManager", "Регистрация не выполнена: пользователь с таким email уже существует")
            return false
        }

        val newUser = User(email = email, password = hashedPassword, name = name)
        val userId = db.addUser(newUser)

        if (userId == -1L) {
            Log.d("AuthManager", "Ошибка вставки пользователя в БД: email=$email")
            return false
        }

        Log.d("AuthManager", "Пользователь зарегистрирован успешно: ID=$userId, email=$email")

        // проверка: действительно ли пользователь добавлен
        val checkUser = db.getUserByEmail(email)
        Log.d("AuthManager", "Проверка после вставки: $checkUser")

        return true
    }

    fun loginUser(email: String, password: String, db: ClimateDatabase): Boolean {
        val user = db.getUserByEmail(email)
        if (user == null) {
            Log.d("AuthManager", "loginUser: пользователь не найден")
            return false
        }

        val verified = verifyPassword(password, user.password)
        if (verified) {
            saveCurrentUser(user.userID.toLong(), user.name)
            Log.d("AuthManager", "loginUser: вход успешен для email=$email")
        } else {
            Log.d("AuthManager", "loginUser: неверный пароль для email=$email")
        }
        return verified
    }
}
