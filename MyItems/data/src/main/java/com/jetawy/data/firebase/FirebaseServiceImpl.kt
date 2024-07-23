package com.jetawy.data.firebase

import android.app.Activity
import android.content.Context
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jetawy.domain.utils.AuthState
import com.jetawy.domain.utils.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FirebaseServiceImpl @Inject constructor(private val auth: FirebaseAuth) : FirebaseService {
    var storedVerificationId: String? = null
    var credential: PhoneAuthCredential? = null
    var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private val _signIn: MutableStateFlow<AuthState> =
        MutableStateFlow(AuthState.Initial)
    val signIn: Flow<AuthState> =
        _signIn.asStateFlow()

    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            CoroutineScope(Dispatchers.IO).launch {
                _signIn.emit(AuthState.OnSuccess)
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            CoroutineScope(Dispatchers.IO).launch {
                _signIn.emit(AuthState.Error(e))
            }
            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                // reCAPTCHA verification attempted with null Activity
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                _signIn.emit(AuthState.OnCodeSent)
            }
            storedVerificationId = verificationId
            resendToken = token
        }
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
//                        Log.d(TAG, "signInWithCredential:success")
                    CoroutineScope(Dispatchers.IO).launch {
                        _signIn.emit(AuthState.OnSuccess)
                    }
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        _signIn.emit(AuthState.Error(task.exception))
                    }
                    // Sign in failed, display a message and update the UI
//                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }
    override fun signIn(phoneNumber: String, lang: String): Flow<AuthState> {
        auth.setLanguageCode(lang)
        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
//            .setActivity(context.applicationContext as Activity) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        return signIn
    }



    override fun signOut() {
        auth.signOut()
    }

    override fun isLoggedIn() = auth.currentUser != null
    override fun verifyCode(code: String):Flow<AuthState> {
        credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)
        signInWithPhoneAuthCredential(credential!!)
        return signIn
    }

}