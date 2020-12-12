package ebj.yujinkun.ramentracker.data.room;

import android.app.Application;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ebj.yujinkun.ramentracker.data.models.Photo;
import ebj.yujinkun.ramentracker.data.models.Ramen;
import ebj.yujinkun.ramentracker.data.models.RamenPhotoJoin;

@Database(entities = {Ramen.class, Photo.class, RamenPhotoJoin.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DB_NAME = "app-db";

    public abstract RamenDao ramenDao();
    public abstract PhotoDao photoDao();
    public abstract RamenPhotoJoinDao ramenPhotoJoinDao();

    public static AppDatabase createDatabase(Application application) {
        return Room.databaseBuilder(application, AppDatabase.class, DB_NAME).build();
    }

}
