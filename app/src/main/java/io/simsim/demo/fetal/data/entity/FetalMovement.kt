package io.simsim.demo.fetal.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "table_main"
)
data class FetalMovementRecord(
    @PrimaryKey(autoGenerate = true) val dbId: Long = 0L,
    @ColumnInfo(name = "record_time") val startTime: Long,
    @ColumnInfo(name = "movement_list") val movements: List<FetalMovement>
)

@Entity(
    tableName = "table_movement",
    foreignKeys = [
        ForeignKey(
            entity = FetalMovementRecord::class,
            parentColumns = ["dbId"],
            onDelete = ForeignKey.CASCADE,
            childColumns = ["dbId"]
        )
    ]
)
data class FetalMovement(
    @PrimaryKey(autoGenerate = true) val dbId: Long = 0L,
    @ColumnInfo(name = "movement_time") val time: Long,
    @ColumnInfo(name = "movement_valid") val isValid: Boolean
)
