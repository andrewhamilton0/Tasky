package com.andrew.tasky.agenda.domain.models

import com.andrew.tasky.core.Resource

data class UpsertEventResult(
    val resource: Resource<Unit>,
    val photosTooBig: Int
)
