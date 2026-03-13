package com.chhotu.gesture.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.chhotu.gesture.data.local.db.dao.GestureDao
import com.chhotu.gesture.data.local.db.dao.StatsDao
import com.chhotu.gesture.data.local.db.entity.ActionMappingEntity
import com.chhotu.gesture.data.local.db.entity.GestureEntity
import com.chhotu.gesture.data.local.db.entity.GestureStatsEntity

@Database(
    entities = [
        GestureEntity::class,
        ActionMappingEntity::class,
        GestureStatsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ChhotuDatabase : RoomDatabase() {
    abstract fun gestureDao(): GestureDao
    abstract fun statsDao(): StatsDao
}
