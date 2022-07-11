package ebj.yujinkun.ramentracker.data.room

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface RamenDao {

    @Query("SELECT * FROM ramen ORDER BY date DESC")
    fun getAllRamen(): Flowable<List<RamenEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(ramenEntity: RamenEntity): Completable

    @Delete
    fun delete(ramenEntity: RamenEntity): Completable
}