package ebj.yujinkun.ramentracker.data.files

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import ebj.yujinkun.ramentracker.util.FileUtils
import io.reactivex.Single

class FileStorageImpl(private val application: Application) : FileStorage {
    override fun saveBitmap(id: String, bitmap: Bitmap): Single<String> {
        return Single.fromCallable {
            FileUtils.saveBitmapToInternalStorage(
                application, bitmap, id
            )
        }
    }

    override fun loadBitmap(bitmapUri: String): Single<Bitmap> {
        return Single.fromCallable { BitmapFactory.decodeFile(bitmapUri) }
    }
}