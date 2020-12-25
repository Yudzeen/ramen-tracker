package ebj.yujinkun.ramentracker.di;

import android.app.Application;

import ebj.yujinkun.ramentracker.data.RamenRepository;
import ebj.yujinkun.ramentracker.data.RamenRepositoryImpl;
import ebj.yujinkun.ramentracker.data.files.FileStorage;
import ebj.yujinkun.ramentracker.data.files.FileStorageImpl;
import ebj.yujinkun.ramentracker.data.room.AppDatabase;

public class AppComponent {

    private final RamenRepository ramenRepository;
    private final AppDatabase appDatabase;
    private final FileStorage fileStorage;

    public AppComponent(RamenRepository ramenRepository, AppDatabase appDatabase, FileStorage fileStorage) {
        this.ramenRepository = ramenRepository;
        this.appDatabase = appDatabase;
        this.fileStorage = fileStorage;
    }

    public RamenRepository getRamenRepository() {
        return ramenRepository;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }

    public FileStorage getFileStorage() {
        return fileStorage;
    }

    public static AppComponent initializeAppComponent(Application application) {
        FileStorage fileStorage = provideFileStorage(application);
        AppDatabase appDatabase = provideAppDatabase(application);
        RamenRepository ramenRepository = provideRamenRepository(appDatabase, fileStorage);
        return new AppComponent(ramenRepository, appDatabase, fileStorage);
    }

    private static FileStorage provideFileStorage(Application application) {
        return new FileStorageImpl(application);
    }

    private static AppDatabase provideAppDatabase(Application application) {
        return AppDatabase.createDatabase(application);
    }

    private static RamenRepository provideRamenRepository(AppDatabase appDatabase, FileStorage fileStorage) {
        return new RamenRepositoryImpl(appDatabase, fileStorage);
    }
}
