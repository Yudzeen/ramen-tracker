package ebj.yujinkun.ramentracker;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import ebj.yujinkun.ramentracker.data.room.AppDatabase;
import timber.log.Timber;

public class MainApplication extends Application {

    private AppDatabase appDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeLogger();
        initializeAppDatabase();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }

    private void initializeLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    private void initializeAppDatabase() {
        appDatabase = AppDatabase.createDatabase(this);
    }
}
