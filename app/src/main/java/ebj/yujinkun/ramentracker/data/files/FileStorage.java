package ebj.yujinkun.ramentracker.data.files;

import android.graphics.Bitmap;

import io.reactivex.Single;

public interface FileStorage {

    /**
     * Save image from a content uri in app's internal storage
     * @param filename name of image when saved to internal storage
     * @param bitmap bitmap of the image
     * @return string path in internal storage
     */
    Single<String> saveImage(String filename, Bitmap bitmap);

}
