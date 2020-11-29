package ebj.yujinkun.ramentracker.di;

import android.app.Application;

import ebj.yujinkun.ramentracker.MainApplication;
import ebj.yujinkun.ramentracker.data.RamenRepository;
import ebj.yujinkun.ramentracker.data.RamenRepositoryImpl;

public class Injection {

    public static RamenRepository provideRamenRepository(Application application) {
        return new RamenRepositoryImpl(((MainApplication) application).getAppDatabase());
    }

}
