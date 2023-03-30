package com.andrew.tasky.agenda.data.event

import android.content.Context
import com.andrew.tasky.agenda.data.storage.ImageStorage
import com.andrew.tasky.agenda.data.util.BitmapConverters
import com.andrew.tasky.agenda.domain.models.AgendaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext

class EventCompressor(context: Context) {

    private val imageStorage = ImageStorage(context)
    private val bitmapConverter = BitmapConverters

    data class CompressEventResult(val event: AgendaItem.Event, val photosDeleted: Int)
    suspend fun compressEvent(event: AgendaItem.Event): CompressEventResult {
        return withContext(Dispatchers.Default) {
            var photosDeleted = 0
            val compressedEvent = event.copy(
                photos = supervisorScope {
                    event.photos
                        .chunked(MAX_PARALLEL_IMAGE_COMPRESS_COUNT)
                        .map { eventPhotoChunk ->
                            eventPhotoChunk.map { eventPhoto ->
                                async {
                                    if (eventPhoto is LocalPhoto) {
                                        if (eventPhoto.savedInternally) {
                                            val byteArray =
                                                imageStorage.getByteArray(eventPhoto.key)
                                            eventPhoto.copy(byteArray = byteArray)
                                        } else {
                                            val byteArray = eventPhoto.bitmap?.let { bitmap ->
                                                bitmapConverter.bitmapToCompressByteArray(
                                                    bitmap, MAX_PHOTO_SIZE_IN_BYTES
                                                )
                                            }
                                            if (byteArray != null) {
                                                eventPhoto.copy(byteArray = byteArray)
                                            } else {
                                                photosDeleted++
                                                null
                                            }
                                        }
                                    } else {
                                        eventPhoto
                                    }
                                }
                            }.mapNotNull { it.await() }
                        }.flatten()
                }
            )
            CompressEventResult(event = compressedEvent, photosDeleted = photosDeleted)
        }
    }
    companion object {
        private const val MAX_PHOTO_SIZE_IN_BYTES = 1_000_000
        private const val MAX_PARALLEL_IMAGE_COMPRESS_COUNT = 3
    }
}
