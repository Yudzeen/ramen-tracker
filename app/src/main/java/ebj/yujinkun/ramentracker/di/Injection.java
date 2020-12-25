package ebj.yujinkun.ramentracker.di;

import android.app.Application;

import ebj.yujinkun.ramentracker.MainApplication;
import ebj.yujinkun.ramentracker.data.RamenRepository;

public class Injection {

    public static RamenRepository getRamenRepository(Application application) {
        return ((MainApplication) application)
                .getAppComponent().getRamenRepository();
    }

}
