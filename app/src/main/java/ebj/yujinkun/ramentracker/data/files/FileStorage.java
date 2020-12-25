package ebj.yujinkun.ramentracker.data.files;

import android.net.Uri;

import ebj.yujinkun.ramentracker.data.models.Photo;
import io.reactivex.Single;

public interface FileStorage {

    /**
     * Save image from a content uri in app's internal storage
     * @param contentUri content uri of photo usually from image picker or photo captured
     * @return photo with related data
     */
    Single<Photo> saveImage(Uri contentUri);

}
