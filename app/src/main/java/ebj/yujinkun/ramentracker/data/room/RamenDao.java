package ebj.yujinkun.ramentracker.data.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import ebj.yujinkun.ramentracker.data.models.Ramen;
import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface RamenDao {

    @Query("SELECT * FROM ramen ORDER BY date DESC")
    Flowable<List<Ramen>> getAllRamen();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable save(Ramen ramen);

    @Delete
    Completable delete(Ramen ramen);

}
