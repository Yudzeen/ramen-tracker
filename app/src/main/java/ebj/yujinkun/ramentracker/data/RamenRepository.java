package ebj.yujinkun.ramentracker.data;

import java.util.List;

import ebj.yujinkun.ramentracker.data.models.Photo;
import ebj.yujinkun.ramentracker.data.models.Ramen;
import io.reactivex.Completable;
import io.reactivex.Flowable;

public interface RamenRepository {

    Flowable<List<Ramen>> getAllRamen();
    Completable save(Ramen ramen);
    Completable delete(Ramen ramen);

    Completable save(Photo photo);
    Completable delete(Photo photo);

    Flowable<List<Photo>> getPhotosForRamen(String ramenId);
    Completable addPhotoToRamen(Photo photo, Ramen ramen);
    Completable removePhotoFromRamen(Photo photo, Ramen ramen);
}
