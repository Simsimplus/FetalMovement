package io.simsim.demo.fetal.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import io.simsim.demo.fetal.data.FetalDB
import javax.inject.Singleton

@Module
object HiltModule {

    @Provides
    @Singleton
    fun provideDB(
        @ApplicationContext app: Application
    ) = Room.databaseBuilder(
        app,
        FetalDB::class.java,
        "fetal movement"
    ).fallbackToDestructiveMigration().build()
}
