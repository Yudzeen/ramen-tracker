package ebj.yujinkun.ramentracker.util

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import java.io.IOException

object FileUtils {
    @Throws(IOException::class)
    fun saveBitmapToInternalStorage(
        application: Application,
        bitmap: Bitmap,
        fileName: String
    ): String {
        val fileNameWithPrefix = "$fileName.webp"
        val outputStream = application.openFileOutput(fileNameWithPrefix, Context.MODE_PRIVATE)
        bitmap.compress(Bitmap.CompressFormat.WEBP, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        return application.filesDir.toString() + "/" + fileNameWithPrefix
    }
}