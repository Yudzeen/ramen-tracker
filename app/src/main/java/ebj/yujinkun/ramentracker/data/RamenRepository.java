package ebj.yujinkun.ramentracker.data;

import android.graphics.Bitmap;

import java.util.List;

import ebj.yujinkun.ramentracker.data.models.Photo;
import ebj.yujinkun.ramentracker.data.models.Ramen;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface RamenRepository {

    Flowable<List<Ramen>> getAllRamen();
    Completable save(Ramen ramen);
    Completable delete(Ramen ramen);

    Completable save(Photo photo);
    Completable delete(Photo photo);

    Flowable<List<Photo>> getPhotosForRamen(String ramenId);

    Single<String> copyPhotoToInternalStorage(String filename, Bitmap bitmap);
}
