package com.andrew.tasky.core.domain

interface SharedPrefs {

    fun matchesSavedUserId(userId: String): Boolean
    fun getFullName(): String
    fun getJwt(): String?
    fun getUserId(): String
    fun containsJwt(): Boolean
    fun putJwt(jwt: String)
    fun putUserId(userId: String)
    fun putFullName(name: String)
    fun clearPrefs()
}
