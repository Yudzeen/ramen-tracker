package ebj.yujinkun.ramentracker.data.models

import java.io.Serializable

data class Ramen(
    val id: String,
    val name: String?,
    val shop: String?,
    val location: String?,
    val date: String?,
    val comments: String?,
    val favorite: Boolean,
    val photoUri: String?
) : Serializable