package com.jetawy.data.sp
/*

import android.content.SharedPreferences
import com.google.gson.Gson
import com.jetawy.data.sp.AuthSharedPreference
import com.jetawy.data.sp.ProfileSharedPreference
import store.msolapps.domain.models.response.LoginResponse
import store.msolapps.domain.models.response.ProfileUpdateResponseModel
import store.msolapps.domain.models.response.SignupResponse
import javax.inject.Inject

class SharedPreferenceImpl @Inject constructor(private val prefs: SharedPreferences) :
    AuthSharedPreference, ProfileSharedPreference {

    private val TOKEN = "TOKEN"
    private val REMEMBER_ME = "REMEMBER_ME"
    private val IS_FIRST_TIME = "IS_FIRST_TIME"
    private val LANGUAGE = "LANGUAGE"
    private val STOREID = "STOREID"
    private val USERNAME = "USERNAME"
    private val USER_DATA = "USER_DATA"
    private val DEFAULT_ADDRESS = "DEFAULT_ADDRESS"

    override fun getToken(): String {
        return prefs.getString(TOKEN, "").toString()
    }

    override fun getUserName(): String {
        val gson = Gson()
        val json = prefs.getString(USER_DATA, "")
        return gson.fromJson(json, LoginResponse::class.java).user?.name.toString()
    }

    override fun logout() {
        prefs.edit().putString(TOKEN, null).apply()
    }

    override fun setUserData(userData: SignupResponse) {
        val prefsEditor = this.prefs.edit()
        val gson = Gson()
        val json = gson.toJson(userData)
        prefsEditor.putString(USER_DATA, json)
        prefsEditor.apply()
    }

    override fun setUserData(userData: ProfileUpdateResponseModel) {
        val prefsEditor = this.prefs.edit()
        val gson = Gson()
        val json = gson.toJson(userData)
        prefsEditor.putString(USER_DATA, json)
        prefsEditor.apply()
    }

    override fun setUserData(userData: LoginResponse) {
        val prefsEditor = this.prefs.edit()
        val gson = Gson()
        val json = gson.toJson(userData)
        prefsEditor.putString(USER_DATA, json)
        prefsEditor.apply()
    }

    override fun getUserData(): LoginResponse {
        val gson = Gson()
        val json = prefs.getString(USER_DATA, "")
        return gson.fromJson(json, LoginResponse::class.java)
    }

    override fun setToken(query: String) {
        prefs.edit().putString(TOKEN, query).apply()
    }

    override fun isLogged(): Boolean {
        return getToken().isNotEmpty()
    }

    override fun setIsRememberMe(flag: Boolean) {
        prefs.edit().putBoolean(REMEMBER_ME, flag).apply()
    }

    override fun isRememberMe(): Boolean {
        return prefs.getBoolean(REMEMBER_ME, false)
    }

    override fun isFirstOpen(): Boolean {
        return prefs.getBoolean(IS_FIRST_TIME, true)
    }

    override fun setAppOpenedFirstTime() {
        prefs.edit().putBoolean(IS_FIRST_TIME, false).apply()
    }

    override fun clearAllCache() {
        prefs.edit().clear().apply()
    }

    override fun setLang(query: String) {
        prefs.edit().putString(LANGUAGE, query).apply()
    }

    override fun getLang(): String {
        return prefs.getString(LANGUAGE, "en").toString()
    }

    override fun setStoreId(query: String) {
        prefs.edit().putString(STOREID, query).apply()
    }

    override fun getStoreId(): String {
        return prefs.getString(STOREID, "01").toString()
    }

    override fun setIsDefaultAddressExist(flag: Boolean) {
        prefs.edit().putBoolean(DEFAULT_ADDRESS, flag).apply()
    }

    override fun getIsDefaultAddressExist(): Boolean {
        return prefs.getBoolean(DEFAULT_ADDRESS, false)
    }


}*/
