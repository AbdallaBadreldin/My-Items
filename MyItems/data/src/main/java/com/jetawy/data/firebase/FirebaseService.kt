package com.jetawy.data.firebase

interface FirebaseService {
    fun signIn(phoneNumber: String,lang:String)
    fun signOut()
    fun isLoggedIn(): Boolean
}