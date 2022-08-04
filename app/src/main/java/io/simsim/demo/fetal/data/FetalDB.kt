package io.simsim.demo.fetal.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [],
    exportSchema = true,
    version = 1
)
abstract class FetalDB : RoomDatabase()
