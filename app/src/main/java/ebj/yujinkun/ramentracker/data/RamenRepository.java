package ebj.yujinkun.ramentracker.data;

import java.util.List;

import ebj.yujinkun.ramentracker.models.Ramen;
import io.reactivex.Completable;
import io.reactivex.Flowable;

public interface RamenRepository {

    Flowable<List<Ramen>> getAllRamen();
    Completable save(Ramen ramen);
    Completable delete(Ramen ramen);

}
