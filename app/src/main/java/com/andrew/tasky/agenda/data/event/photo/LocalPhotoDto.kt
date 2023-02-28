package com.andrew.tasky.agenda.data.event.photo

data class LocalPhotoDto(
    val key: String,
    val byteArray: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LocalPhotoDto

        if (key != other.key) return false
        if (!byteArray.contentEquals(other.byteArray)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + byteArray.contentHashCode()
        return result
    }
}
