package com.andrew.tasky.agenda.data.event

import com.andrew.tasky.agenda.data.util.BitmapConverters
import com.andrew.tasky.agenda.domain.models.EventPhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import okhttp3.internal.toImmutableList

class EventPhotoCompressor {

    private val bitmapConverter = BitmapConverters

    data class CompressPhotosResult(
        val photos: List<EventPhoto>,
        val deletedPhotosCount: Int,
        val deletedPhoto: List<EventPhoto.Local>
    )

    suspend fun compressPhotos(
        photos: List<EventPhoto>,
        compressionSize: Int
    ): CompressPhotosResult {
        return withContext(Dispatchers.Default) {
            var deletedPhotosCount = 0
            val deletedPhotos = emptyList<EventPhoto.Local>().toMutableList()
            supervisorScope {
                photos
                    .chunked(MAX_PARALLEL_IMAGE_COMPRESS_COUNT)
                    .map { eventPhotoChunk ->
                        eventPhotoChunk.map { eventPhoto ->
                            async {
                                if (eventPhoto is LocalPhoto) {
                                    val byteArray = eventPhoto.bitmap?.let { bitmap ->
                                        bitmapConverter.bitmapToCompressByteArray(
                                            bitmap, compressionSize
                                        )
                                    }
                                    if (byteArray != null) {
                                        eventPhoto.copy(byteArray = byteArray)
                                    } else {
                                        deletedPhotosCount++
                                        deletedPhotos.add(eventPhoto)
                                        null
                                    }
                                } else {
                                    eventPhoto
                                }
                            }
                        }.mapNotNull { it.await() }
                    }.flatten()
            }
            CompressPhotosResult(
                photos = photos,
                deletedPhotosCount = deletedPhotosCount,
                deletedPhoto = deletedPhotos.toImmutableList()
            )
        }
    }
    companion object {
        private const val MAX_PARALLEL_IMAGE_COMPRESS_COUNT = 3
    }
}
