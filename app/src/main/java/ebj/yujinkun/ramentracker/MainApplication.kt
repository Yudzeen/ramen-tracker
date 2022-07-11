package ebj.yujinkun.ramentracker;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import ebj.yujinkun.ramentracker.data.files.FileStorage;
import ebj.yujinkun.ramentracker.data.files.FileStorageImpl;
import ebj.yujinkun.ramentracker.data.room.AppDatabase;
import ebj.yujinkun.ramentracker.di.AppComponent;
import timber.log.Timber;

public class MainApplication extends Application {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeLogger();
        appComponent = AppComponent.initializeAppComponent(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    private void initializeLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

}
