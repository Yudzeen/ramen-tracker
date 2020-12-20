package ebj.yujinkun.ramentracker.util;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

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

}
