package ebj.yujinkun.ramentracker.data.files;

import android.graphics.Bitmap;

import io.reactivex.Single;

public interface FileStorage {

    /**
     * Save bitmap
     * @param bitmap bitmap to save
     * @return String URI of saved location
     */
    Single<String> saveBitmap(String id, Bitmap bitmap);

    /**
     * Loads bitmap of given URI
     * @param bitmapUri String URI of saved location
     * @return bitmap loaded
     */
    Single<Bitmap> loadBitmap(String bitmapUri);

}
