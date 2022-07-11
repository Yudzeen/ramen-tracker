package ebj.yujinkun.ramentracker.data.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "ramen", indices = [Index("id")])
data class RamenEntity(
    @PrimaryKey
    val id: String,
    val name: String?,
    val shop: String?,
    val location: String?,
    val date: String?,
    val comments: String?,
    val favorite: Boolean,
    val photoUri: String?
)