package com.andrew.tasky.agenda.domain.models

import com.andrew.tasky.core.data.Resource

data class UpsertEventResult(
    val resource: Resource<Unit>,
    val deletedPhotos: Int
)
