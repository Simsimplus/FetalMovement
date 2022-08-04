package io.simsim.demo.fetal.data.entity

import androidx.room.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@Entity(
    tableName = "table_record"
)
data class FetalMovementRecord(
    @PrimaryKey var recordId: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "recordStartTime") val recordStartTime: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name = "recordEndTime") var recordEndTime: LocalDateTime = recordStartTime,
    @ColumnInfo(name = "validMovement") var validMovement: Int = 0,
    @ColumnInfo(name = "totalMovement") var totalMovement: Int = 0
) {
    @Ignore
    var movements: MutableList<FetalMovement> = mutableListOf()
}

@Entity(
    tableName = "table_movement",
    foreignKeys = [
        ForeignKey(
            entity = FetalMovementRecord::class,
            parentColumns = ["recordId"],
            onDelete = ForeignKey.CASCADE,
            childColumns = ["recordId"]
        )
    ]
)
data class FetalMovement(
    @PrimaryKey(autoGenerate = true) var movementId: Long = 0L,
    @ColumnInfo(name = "recordId", index = true) val recordId: String,
    @ColumnInfo(name = "movementTime") val movementTime: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name = "isMovementValid") val isMovementValid: Boolean
)

object LocalDateTimeConverter {
    @TypeConverter
    fun time2Long(time: LocalDateTime): Long = time.toInstant(ZoneOffset.UTC).toEpochMilli()

    @TypeConverter
    fun long2Time(long: Long): LocalDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(long), ZoneOffset.UTC)
}
