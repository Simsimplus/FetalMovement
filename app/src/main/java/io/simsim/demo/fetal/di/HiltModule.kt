package io.simsim.demo.fetal.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.simsim.demo.fetal.data.FetalDB

@InstallIn(
    SingletonComponent::class
)
@Module
object HiltModule {

    @Provides
    fun provideDB(
        @ApplicationContext ctx: Context
    ): FetalDB = Room.databaseBuilder(
        ctx,
        FetalDB::class.java,
        "fetal movement"
    ).fallbackToDestructiveMigration().build()
}
