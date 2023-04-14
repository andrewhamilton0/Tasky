package com.andrew.tasky.agenda.data.event

import com.andrew.tasky.agenda.data.util.BitmapConverters
import com.andrew.tasky.agenda.domain.models.EventPhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext

class EventPhotoCompressor {

    data class CompressPhotosResult(
        val photos: List<EventPhoto.Local>,
        val deletedPhotosCount: Int,
    )

    suspend fun compressPhotos(
        photos: List<EventPhoto.Local>,
        compressionSize: Int
    ): CompressPhotosResult {
        return withContext(Dispatchers.Default) {
            var deletedPhotosCount = 0
            supervisorScope {
                val compressedPhotos = photos
                    .chunked(MAX_PARALLEL_IMAGE_COMPRESS_COUNT)
                    .map { eventPhotoChunk ->
                        eventPhotoChunk.map { eventPhoto ->
                            async {
                                val byteArray = eventPhoto.bitmap?.let { bitmap ->
                                    BitmapConverters.bitmapToCompressByteArray(
                                        bitmap, compressionSize
                                    )
                                }
                                if (byteArray != null) {
                                    eventPhoto.copy(byteArray = byteArray)
                                } else {
                                    deletedPhotosCount++
                                    null
                                }
                            }
                        }.mapNotNull { it.await() }
                    }.flatten()
                CompressPhotosResult(compressedPhotos, deletedPhotosCount)
            }
        }
    }
    companion object {
        private const val MAX_PARALLEL_IMAGE_COMPRESS_COUNT = 3
    }
}
