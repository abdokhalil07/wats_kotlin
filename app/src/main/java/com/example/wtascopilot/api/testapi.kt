// تعديل لملف api/testapi.kt
package com.example.wtascopilot.api
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) = runBlocking {
    fetchUserPosts("abdo")
}

suspend fun createNewPost() {
    val newPost = PostModel(
        account_id = 24,
        user_name = "This is a test",
        password_ = "12345",
        max_number = 5
    )

    try {
        val response = RetrofitClient1.api.createPost(newPost)
        if (response.isSuccessful) {
            println("Created: ${response.body()}")
        } else {
            println("Error Code: ${response.code()}")
        }
    } catch (e: Exception) {
        println("Exception: ${e.message}")
    }
}

suspend fun fetchAllPosts() {
    try {
        // نستخدم api.getPosts() اللي متعرفة في الـ Interface
        val response = RetrofitClient1.api.getPosts()

        if (response.isSuccessful) {
            val posts = response.body() // هنا هيرجع لك List<PostModel>
            posts?.forEach { post ->
                println("account id: ${post.account_id}, user name: ${post.user_name}, password: ${post.password_}, max number: ${post.max_number}")
            }
        } else {
            println("Error: ${response.code()}")
        }
    } catch (e: Exception) {
        println("Exception: ${e.message}")
    }
}


suspend fun fetchUserPosts(name: String) {
    try {
        // نجهز الـ Body
        val requestBody = UserSearchDto(user_name = name)

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