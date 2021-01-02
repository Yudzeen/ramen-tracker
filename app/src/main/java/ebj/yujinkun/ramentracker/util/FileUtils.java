package ebj.yujinkun.ramentracker.util;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    private FileUtils() {}

    public static String copyFileToInternalStorage(Application application, Uri srcUri, String outputFileName) throws IOException {
        InputStream inputStream = application.getContentResolver().openInputStream(srcUri);
        OutputStream outputStream = application.openFileOutput(outputFileName, Context.MODE_PRIVATE);

        // copy from input stream to output stream
        byte[] buff=new byte[1024];
        int len;
        while((len=inputStream.read(buff))>0){
            outputStream.write(buff,0,len);
        }

        inputStream.close();
        outputStream.close();

        return application.getFilesDir() + "/" + outputFileName;
    }

    public static String saveBitmapToInternalStorage(Application application, Bitmap bitmap, String outputFileName) throws IOException {
        String filename = outputFileName + ".webp";
        FileOutputStream outputStream = application.openFileOutput(filename, Context.MODE_PRIVATE);
        bitmap.compress(Bitmap.CompressFormat.WEBP, 100, outputStream);
        outputStream.flush();
        outputStream.close();
        return application.getFilesDir() + "/" + filename;
    }

}
