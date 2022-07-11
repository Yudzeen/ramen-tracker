package ebj.yujinkun.ramentracker.data

import ebj.yujinkun.ramentracker.data.models.Ramen
import io.reactivex.Completable
import io.reactivex.Flowable

interface RamenRepository {
    fun getAllRamen(): Flowable<List<Ramen>>
    fun saveRamen(ramen: Ramen): Completable
    fun deleteRamen(ramen: Ramen): Completable
}