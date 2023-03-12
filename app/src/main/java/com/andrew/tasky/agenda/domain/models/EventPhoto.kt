package com.andrew.tasky.agenda.domain.models

import android.graphics.Bitmap
import android.os.Parcelable
import java.util.UUID
import kotlinx.parcelize.Parcelize

sealed class EventPhoto(open val key: String) {

    @Parcelize
    data class Remote(
        override val key: String = UUID.randomUUID().toString(),
        val photoUrl: String,
    ) : EventPhoto(key = key), Parcelable

    @Parcelize
    data class Local(
        override val key: String = UUID.randomUUID().toString(),
        val bitmap: Bitmap? = null,
        val byteArray: ByteArray? = null,
        val savedInternally: Boolean = false
    ) : EventPhoto(key = key), Parcelable {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Local

            if (key != other.key) return false
            if (bitmap != other.bitmap) return false
            if (byteArray != null) {
                if (other.byteArray == null) return false
                if (!byteArray.contentEquals(other.byteArray)) return false
            } else if (other.byteArray != null) return false
            if (savedInternally != other.savedInternally) return false

            return true
        }

        override fun hashCode(): Int {
            var result = key.hashCode()
            result = 31 * result + (bitmap?.hashCode() ?: 0)
            result = 31 * result + (byteArray?.contentHashCode() ?: 0)
            result = 31 * result + savedInternally.hashCode()
            return result
        }
    }
}
