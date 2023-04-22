package com.andrew.tasky.core.domain

interface SharedPrefs {

    fun matchesUserId(s: String): Boolean
    fun getFullName(): String
    fun getJwt(): String?
    fun containsJwt(): Boolean
    fun putJwt(s: String)
    fun putUserId(s: String)
    fun putFullName(s: String)
    fun clearPrefs()
}
