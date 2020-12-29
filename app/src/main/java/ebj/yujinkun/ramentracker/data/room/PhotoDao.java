package ebj.yujinkun.ramentracker.data.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import ebj.yujinkun.ramentracker.data.models.Photo;
import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface PhotoDao {

    @Query("SELECT * FROM photos WHERE ramen_id = :ramenId")
    Flowable<List<Photo>> getPhotosForRamen(String ramenId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable save(Photo photo);

    @Delete
    Completable delete(Photo photo);

}
