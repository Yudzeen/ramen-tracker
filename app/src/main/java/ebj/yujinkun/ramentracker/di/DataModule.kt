package ebj.yujinkun.ramentracker.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ebj.yujinkun.ramentracker.data.RamenRepository
import ebj.yujinkun.ramentracker.data.RamenRepositoryImpl
import ebj.yujinkun.ramentracker.data.files.FileStorage
import ebj.yujinkun.ramentracker.data.files.FileStorageImpl
import ebj.yujinkun.ramentracker.data.room.AppDatabase
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideRamenRepository(appDatabase: AppDatabase): RamenRepository {
        return RamenRepositoryImpl(appDatabase)
    }

    @Singleton
    @Provides
    fun provideFileStorage(application: Application): FileStorage {
        return FileStorageImpl(application)
    }

}