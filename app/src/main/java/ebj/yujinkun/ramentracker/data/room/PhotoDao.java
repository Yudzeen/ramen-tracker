package ebj.yujinkun.ramentracker.data.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import ebj.yujinkun.ramentracker.data.models.Photo;
import io.reactivex.Completable;

@Dao
public interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable save(Photo photo);

    @Delete
    Completable delete(Photo photo);

}
