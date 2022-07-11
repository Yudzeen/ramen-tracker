package ebj.yujinkun.ramentracker.data.files

import android.graphics.Bitmap
import io.reactivex.Single

interface FileStorage {
    /**
     * Save bitmap
     * @param bitmap bitmap to save
     * @return String URI of saved location
     */
    fun saveBitmap(id: String, bitmap: Bitmap): Single<String>

    /**
     * Loads bitmap of given URI
     * @param bitmapUri String URI of saved location
     * @return bitmap loaded
     */
    fun loadBitmap(bitmapUri: String): Single<Bitmap>
}