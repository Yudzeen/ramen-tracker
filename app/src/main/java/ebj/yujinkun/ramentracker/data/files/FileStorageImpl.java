package ebj.yujinkun.ramentracker.data.files;

import android.app.Application;
import android.net.Uri;

import java.util.UUID;

import ebj.yujinkun.ramentracker.data.models.Photo;
import ebj.yujinkun.ramentracker.util.FileUtils;
import io.reactivex.Single;
import timber.log.Timber;

public class FileStorageImpl implements FileStorage {

    private Application application;

    public FileStorageImpl(Application application) {
        this.application = application;
    }

    @Override
    public Single<Photo> saveImage(Uri contentUri) {
        return Single.fromCallable(() -> {
            Timber.i("Copy photo: %s", contentUri);
            String id = UUID.randomUUID().toString();
            String outputFileName = id + ".png";
            String location = FileUtils.copyFileToInternalStorage(application, contentUri, outputFileName);
            return new Photo(id, location);
        });
    }


}
