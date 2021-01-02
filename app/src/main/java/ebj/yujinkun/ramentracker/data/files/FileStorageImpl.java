package ebj.yujinkun.ramentracker.data.files;

import android.app.Application;
import android.graphics.Bitmap;

import ebj.yujinkun.ramentracker.util.FileUtils;
import io.reactivex.Single;
import timber.log.Timber;

public class FileStorageImpl implements FileStorage {

    private final Application application;

    public FileStorageImpl(Application application) {
        this.application = application;
    }

    @Override
    public Single<String> saveImage(String filename, Bitmap bitmap) {
        return Single.fromCallable(() -> {
            Timber.i("Copy photo: %s", bitmap);
            return FileUtils.saveBitmapToInternalStorage(application, bitmap, filename);
        });
    }


}
