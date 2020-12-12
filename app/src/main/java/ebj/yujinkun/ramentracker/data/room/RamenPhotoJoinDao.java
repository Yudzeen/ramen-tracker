package ebj.yujinkun.ramentracker.data.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import ebj.yujinkun.ramentracker.data.models.Photo;
import ebj.yujinkun.ramentracker.data.models.RamenPhotoJoin;
import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface RamenPhotoJoinDao {

    @Query("SELECT photo.* FROM photos photo " +
            "INNER JOIN ramen_photo_join " +
            "ON photo.id = ramen_photo_join.photo_id " +
            "WHERE ramen_photo_join.ramen_id = :ramenId")
    Flowable<List<Photo>> getPhotosForRamen(String ramenId);

    @Query("DELETE FROM ramen_photo_join WHERE ramen_id = :ramenId")
    Completable deleteByRamenId(String ramenId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(RamenPhotoJoin ramenPhotoJoin);

    @Delete
    Completable delete(RamenPhotoJoin ramenPhotoJoin);
}
