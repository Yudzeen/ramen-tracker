package ebj.yujinkun.ramentracker.data

import ebj.yujinkun.ramentracker.data.models.Ramen
import ebj.yujinkun.ramentracker.data.room.RamenEntity

fun RamenEntity.toModel(): Ramen {
    return Ramen(
        id = id,
        name = name,
        shop = shop,
        location = location,
        date = date,
        comments = comments,
        favorite = favorite,
        photoUri = photoUri
    )
}

fun Ramen.toEntity(): RamenEntity {
    return RamenEntity(
        id = id,
        name = name,
        shop = shop,
        location = location,
        date = date,
        comments = comments,
        favorite = favorite,
        photoUri = photoUri
    )
}