package org.ailingo.app.features.topics.data

import kotlinx.serialization.Serializable

@Serializable
data class Topic(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val price: Int,
)
