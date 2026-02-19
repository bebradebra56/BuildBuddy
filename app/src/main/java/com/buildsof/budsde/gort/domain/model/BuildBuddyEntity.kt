package com.buildsof.budsde.gort.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BuildBuddyEntity (
    @SerialName("ok")
    val buildBuddyOk: Boolean,
    @SerialName("url")
    val buildBuddyUrl: String,
    @SerialName("expires")
    val buildBuddyExpires: Long,
)