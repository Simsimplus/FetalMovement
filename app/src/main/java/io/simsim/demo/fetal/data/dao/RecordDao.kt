package io.simsim.demo.fetal.data.dao

import androidx.room.*
import io.simsim.demo.fetal.data.entity.FetalMovement
import io.simsim.demo.fetal.data.entity.FetalMovementRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
interface RecordDao {
    // C
    @Insert
    suspend fun insertMovement(movementList: List<FetalMovement>)

    @Insert
    suspend fun insertRecord(fetalMovementRecord: FetalMovementRecord)

    @Transaction
    suspend fun insert(
        fetalMovementRecord: FetalMovementRecord
    ) {
        insertRecord(fetalMovementRecord)
        insertMovement(fetalMovementRecord.movements)
    }

    // R
    @Query(
        "select * from table_record join table_movement on table_record.recordId = table_movement.recordId"
    )
    fun queryAllRecordMap(): Flow<Map<FetalMovementRecord, List<FetalMovement>>>

    fun queryAllRecords() = queryAllRecordMap().map { map ->
        map.map { (record, movements) ->
            record.apply {
                this.movements = movements.toMutableList()
            }
        }
    }

    @Query(
        "select * from table_record where recordId =:id"
    )
    fun queryRecord(id: String): Flow<FetalMovementRecord>

    // U
    @Query(
        "update table_record set validMovement = case when(:isValid = 1) then validMovement + 1 else validMovement end,totalMovement = totalMovement+1"
    )
    suspend fun updateRecord(id: String, isValid: Boolean)

    @Insert
    suspend fun addMovement(fetalMovement: FetalMovement)

//    suspend fun updateRecord

    // D
    @Delete
    suspend fun deleteRecord(fetalMovementRecord: FetalMovementRecord)
}
