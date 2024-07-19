package com.jetawy.data.sp

interface ProfileSharedPreference {
    fun setLang(query: String)
    fun getLang(): String
    fun setStoreId(query: String)
    fun getStoreId(): String
    fun setIsDefaultAddressExist(flag: Boolean)
    fun getIsDefaultAddressExist(): Boolean
}