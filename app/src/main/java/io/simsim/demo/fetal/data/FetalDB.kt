package io.simsim.demo.fetal.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.simsim.demo.fetal.data.dao.RecordDao
import io.simsim.demo.fetal.data.entity.FetalMovement
import io.simsim.demo.fetal.data.entity.FetalMovementRecord
import io.simsim.demo.fetal.data.entity.LocalDateTimeConverter

@Database(
    entities = [
        FetalMovementRecord::class,
        FetalMovement::class
    ],
    exportSchema = true,
    version = 1
)
@TypeConverters(
    LocalDateTimeConverter::class
)
abstract class FetalDB : RoomDatabase() {
    abstract fun recordDao(): RecordDao
}
