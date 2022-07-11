package ebj.yujinkun.ramentracker.data

import ebj.yujinkun.ramentracker.data.room.AppDatabase
import ebj.yujinkun.ramentracker.data.RamenRepository
import ebj.yujinkun.ramentracker.data.room.RamenDao
import io.reactivex.Flowable
import ebj.yujinkun.ramentracker.data.models.Ramen
import io.reactivex.Completable

class RamenRepositoryImpl(appDatabase: AppDatabase) : RamenRepository {

    private val ramenDao: RamenDao = appDatabase.ramenDao()

    override fun getAllRamen(): Flowable<List<Ramen>> {
        return ramenDao.getAllRamen().map { ramenList ->
            ramenList.map { ramenEntity ->
                ramenEntity.toModel()
            }
        }
    }

    override fun saveRamen(ramen: Ramen): Completable {
        return ramenDao.save(ramen.toEntity())
    }

    override fun deleteRamen(ramen: Ramen): Completable {
        return ramenDao.delete(ramen.toEntity())
    }

}