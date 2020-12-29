package ebj.yujinkun.ramentracker.data.files;

import android.app.Application;
import android.net.Uri;

import ebj.yujinkun.ramentracker.util.FileUtils;
import io.reactivex.Single;
import timber.log.Timber;

public class FileStorageImpl implements FileStorage {

    private final Application application;

    public FileStorageImpl(Application application) {
        this.application = application;
    }

    @Override
    public Single<String> saveImage(String filename, Uri contentUri) {
        return Single.fromCallable(() -> {
            Timber.i("Copy photo: %s", contentUri);
            return FileUtils.copyFileToInternalStorage(application, contentUri, filename);
        });
    }


}
