package com.buildsof.budsde.gort.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


private const val BUILD_BUDDY_A = "com.buildsof.budsde"
private const val BUILD_BUDDY_B = "buildbuddy-237b9"
@Serializable
data class BuildBuddyParam (
    @SerialName("af_id")
    val buildBuddyAfId: String,
    @SerialName("bundle_id")
    val buildBuddyBundleId: String = BUILD_BUDDY_A,
    @SerialName("os")
    val buildBuddyOs: String = "Android",
    @SerialName("store_id")
    val buildBuddyStoreId: String = BUILD_BUDDY_A,
    @SerialName("locale")
    val buildBuddyLocale: String,
    @SerialName("push_token")
    val buildBuddyPushToken: String,
    @SerialName("firebase_project_id")
    val buildBuddyFirebaseProjectId: String = BUILD_BUDDY_B,
    )