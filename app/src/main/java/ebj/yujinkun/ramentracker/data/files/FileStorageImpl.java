package ebj.yujinkun.ramentracker.data.files;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import ebj.yujinkun.ramentracker.util.FileUtils;
import io.reactivex.Single;

public class FileStorageImpl implements FileStorage {

    private final Application application;

    public FileStorageImpl(Application application) {
        this.application = application;
    }

    @Override
    public Single<String> saveBitmap(String id, Bitmap bitmap) {
        return Single.fromCallable(() -> FileUtils.saveBitmapToInternalStorage(application, bitmap, id));
    }

    @Override
    public Single<Bitmap> loadBitmap(String bitmapUri) {
        return Single.fromCallable(() -> BitmapFactory.decodeFile(bitmapUri));
    }

}
