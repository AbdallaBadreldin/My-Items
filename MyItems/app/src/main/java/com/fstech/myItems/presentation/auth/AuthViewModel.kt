package com.fstech.myItems.presentation.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.jetawy.data.firebase.FirebaseService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

const val TAG = "AuthViewModel"

@HiltViewModel
class AuthViewModel @Inject constructor(private val firebase: FirebaseService) : ViewModel() {
    /* val callbacks= object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
    //            TODO("Not yet implemented")
                Log.e("TAG", "onVerificationCompleted: ")
            }

            override fun onVerificationFailed(p0: FirebaseException) {
    //            TODO("Not yet implemented")
                Log.e("TAG", "FirebaseException: ")
            }
        }*/


    fun isLoggedIn() = firebase.isLoggedIn()

    fun signIn(phoneNumber: String, language:String) {

    }

    fun signOut() = firebase.signOut()

}