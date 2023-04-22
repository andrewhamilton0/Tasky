package com.andrew.tasky.core.data

import android.content.SharedPreferences
import com.andrew.tasky.core.domain.SharedPrefs

class SharedPrefsImpl(
    private val sharedPrefs: SharedPreferences
) : SharedPrefs {

    override fun matchesSavedUserId(userId: String): Boolean {
        return userId == sharedPrefs.getString(PrefsKeys.USER_ID, "")
    }

    override fun getFullName(): String {
        return sharedPrefs.getString(PrefsKeys.FULL_NAME, "") ?: ""
    }

    override fun getJwt(): String? {
        return sharedPrefs.getString(PrefsKeys.JWT, null)
    }

    override fun containsJwt(): Boolean {
        return sharedPrefs.contains(PrefsKeys.JWT)
    }

    override fun putJwt(jwt: String) {
        sharedPrefs.edit().putString(PrefsKeys.JWT, jwt).apply()
    }

    override fun putUserId(userId: String) {
        sharedPrefs.edit().putString(PrefsKeys.USER_ID, userId).apply()
    }

    override fun putFullName(name: String) {
        sharedPrefs.edit().putString(PrefsKeys.FULL_NAME, name).apply()
    }

    override fun clearPrefs() {
        sharedPrefs.edit().clear().apply()
    }
}
