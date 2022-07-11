package ebj.yujinkun.ramentracker.util;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    private FileUtils() {}

    public static String saveBitmapToInternalStorage(Application application, Bitmap bitmap, String fileName) throws IOException {
        String fileNameWithPrefix = fileName + ".webp";
        FileOutputStream outputStream = application.openFileOutput(fileNameWithPrefix, Context.MODE_PRIVATE);
        bitmap.compress(Bitmap.CompressFormat.WEBP, 100, outputStream);
        outputStream.flush();
        outputStream.close();
        return application.getFilesDir() + "/" + fileNameWithPrefix;
    }

}
