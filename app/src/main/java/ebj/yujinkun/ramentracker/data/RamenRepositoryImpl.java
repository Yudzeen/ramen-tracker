package ebj.yujinkun.ramentracker.data;

import android.net.Uri;

import java.util.List;

import ebj.yujinkun.ramentracker.data.files.FileStorage;
import ebj.yujinkun.ramentracker.data.models.Photo;
import ebj.yujinkun.ramentracker.data.models.RamenPhotoJoin;
import ebj.yujinkun.ramentracker.data.room.AppDatabase;
import ebj.yujinkun.ramentracker.data.room.PhotoDao;
import ebj.yujinkun.ramentracker.data.room.RamenDao;
import ebj.yujinkun.ramentracker.data.models.Ramen;
import ebj.yujinkun.ramentracker.data.room.RamenPhotoJoinDao;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class RamenRepositoryImpl implements RamenRepository {

    private final RamenDao ramenDao;
    private final PhotoDao photoDao;
    private final RamenPhotoJoinDao ramenPhotoJoinDao;

    private final FileStorage fileStorage;

    public RamenRepositoryImpl(AppDatabase appDatabase, FileStorage fileStorage) {
        this.ramenDao = appDatabase.ramenDao();
        this.photoDao = appDatabase.photoDao();
        this.ramenPhotoJoinDao = appDatabase.ramenPhotoJoinDao();
        this.fileStorage = fileStorage;
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

    @Override
    public Completable save(Photo photo) {
        return photoDao.save(photo);
    }

    @Override
    public Completable delete(Photo photo) {
        return photoDao.delete(photo);
    }

    @Override
    public Flowable<List<Photo>> getPhotosForRamen(String ramenId) {
        return ramenPhotoJoinDao.getPhotosForRamen(ramenId);
    }

    @Override
    public Completable addPhotoToRamen(Photo photo, Ramen ramen) {
        return ramenPhotoJoinDao.insert(RamenPhotoJoin.from(ramen, photo));
    }

    @Override
    public Completable removePhotoFromRamen(Photo photo, Ramen ramen) {
        return ramenPhotoJoinDao.delete(RamenPhotoJoin.from(ramen, photo));
    }

    @Override
    public Single<Photo> copyPhotoToInternalStorage(Uri contentUri) {
        return fileStorage.saveImage(contentUri);
    }
}
