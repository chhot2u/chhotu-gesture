package com.chhotu.gesture.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.chhotu.gesture.data.local.db.entity.ActionMappingEntity
import com.chhotu.gesture.data.local.db.entity.GestureEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GestureDao {

    @Query("SELECT * FROM gestures")
    fun getAllGestures(): Flow<List<GestureEntity>>

    @Query("SELECT * FROM gestures WHERE id = :id")
    fun getGestureById(id: String): Flow<GestureEntity?>

    @Upsert
    suspend fun insertGesture(gesture: GestureEntity)

    @Update
    suspend fun updateGesture(gesture: GestureEntity)

    @Query("DELETE FROM gestures WHERE id = :id")
    suspend fun deleteGesture(id: String)

    @Query("SELECT * FROM action_mappings")
    fun getAllMappings(): Flow<List<ActionMappingEntity>>

    @Upsert
    suspend fun upsertMapping(mapping: ActionMappingEntity)

    @Query("DELETE FROM action_mappings WHERE gestureId = :gestureId")
    suspend fun deleteMapping(gestureId: String)

    @Query("SELECT COUNT(*) FROM gestures")
    suspend fun getGestureCount(): Int
}
