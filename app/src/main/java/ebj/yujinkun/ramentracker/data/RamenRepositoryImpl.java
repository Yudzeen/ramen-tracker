package ebj.yujinkun.ramentracker.data;

import java.util.List;

import ebj.yujinkun.ramentracker.data.models.Ramen;
import ebj.yujinkun.ramentracker.data.room.AppDatabase;
import ebj.yujinkun.ramentracker.data.room.RamenDao;
import io.reactivex.Completable;
import io.reactivex.Flowable;

public class RamenRepositoryImpl implements RamenRepository {

    private final RamenDao ramenDao;

    public RamenRepositoryImpl(AppDatabase appDatabase) {
        this.ramenDao = appDatabase.ramenDao();
    }

    @Override
    public Flowable<List<Ramen>> getAllRamen() {
        return ramenDao.getAllRamen();
    }

    @Override
    public Completable save(Ramen ramen) {
        return ramenDao.save(ramen);
    }

    @Override
    public Completable delete(Ramen ramen) {
        return ramenDao.delete(ramen);
    }

}
