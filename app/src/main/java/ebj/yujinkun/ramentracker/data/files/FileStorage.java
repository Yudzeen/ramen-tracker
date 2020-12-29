package ebj.yujinkun.ramentracker.data.files;

import android.net.Uri;

import ebj.yujinkun.ramentracker.data.models.Photo;
import io.reactivex.Single;

public interface FileStorage {

    /**
     * Save image from a content uri in app's internal storage
     * @param filename name of image when saved to internal storage
     * @param contentUri content uri of photo usually from image picker or photo captured
     * @return string path in internal storage
     */
    Single<String> saveImage(String filename, Uri contentUri);

}
