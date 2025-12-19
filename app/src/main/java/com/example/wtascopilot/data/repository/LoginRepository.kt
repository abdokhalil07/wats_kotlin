package com.example.wtascopilot.data.repository

class LoginRepository {

    // Placeholder – later we will connect to API or DB
    suspend fun login(username: String, password: String): Boolean {
        return username == "admin" && password == "1234"
    }
}
