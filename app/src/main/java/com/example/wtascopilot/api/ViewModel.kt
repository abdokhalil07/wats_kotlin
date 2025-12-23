package com.example.wtascopilot.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _posts = MutableLiveData<List<PostModel>>()
    val posts: LiveData<List<PostModel>> get() = _posts

    fun loadPosts() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient1.api.getPosts()
                if (response.isSuccessful) {
                    _posts.value = response.body()
                } else {
                    println("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                println("Exception: ${e.message}")
            }
        }
    }

    fun fetchUserPosts(name: String, password: String) {
        viewModelScope.launch {
            try {
                // نجهز الـ Body
                val requestBody = UserSearchDto(name, password)

                // نبعت الـ requestBody
                val response = RetrofitClient1.api.getPostsByUserId(requestBody)

                if (response.isSuccessful) {
                    val posts = response.body()
                    // تأكد إن الباك إند بيرجع List فعلاً مش Object واحد
                    println("Found posts: $posts")
                } else {
                    println("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                println("Exception: ${e.message}")
            }
        }
    }
}
