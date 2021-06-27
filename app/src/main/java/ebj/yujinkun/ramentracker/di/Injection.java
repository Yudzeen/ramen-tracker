package ebj.yujinkun.ramentracker.di;

import android.app.Application;

import ebj.yujinkun.ramentracker.MainApplication;
import ebj.yujinkun.ramentracker.data.RamenRepository;
import ebj.yujinkun.ramentracker.data.files.FileStorage;

public class Injection {

    public static RamenRepository getRamenRepository(Application application) {
        return ((MainApplication) application)
                .getAppComponent().getRamenRepository();
    }

    public static FileStorage getFileStorage(Application application) {
        return ((MainApplication) application)
                .getAppComponent().getFileStorage();
    }

}
