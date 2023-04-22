package com.andrew.tasky.core.data

import android.content.SharedPreferences
import com.andrew.tasky.core.domain.SharedPrefs

class SharedPrefsImpl(
    private val sharedPrefs: SharedPreferences
) : SharedPrefs {

    override fun matchesUserId(s: String): Boolean {
        return s == sharedPrefs.getString(PrefsKeys.USER_ID, "")
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

    override fun putJwt(s: String) {
        sharedPrefs.edit().putString(PrefsKeys.JWT, s).apply()
    }

    override fun putUserId(s: String) {
        sharedPrefs.edit().putString(PrefsKeys.USER_ID, s).apply()
    }

    override fun putFullName(s: String) {
        sharedPrefs.edit().putString(PrefsKeys.FULL_NAME, s).apply()
    }

    override fun clearPrefs() {
        sharedPrefs.edit().clear().apply()
    }
}
