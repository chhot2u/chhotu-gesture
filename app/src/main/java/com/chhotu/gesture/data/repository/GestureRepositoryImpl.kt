package com.chhotu.gesture.data.repository

import com.chhotu.gesture.data.local.db.dao.GestureDao
import com.chhotu.gesture.data.mapper.toDomain
import com.chhotu.gesture.data.mapper.toEntity
import com.chhotu.gesture.domain.model.DefaultMappings
import com.chhotu.gesture.domain.model.Gesture
import com.chhotu.gesture.domain.model.GestureCategory
import com.chhotu.gesture.domain.model.GestureMapping
import com.chhotu.gesture.domain.repository.GestureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GestureRepositoryImpl @Inject constructor(
    private val gestureDao: GestureDao
) : GestureRepository {

    override fun getAllGestures(): Flow<List<Gesture>> =
        gestureDao.getAllGestures().map { entities -> entities.map { it.toDomain() } }

    override fun getGestureById(id: String): Flow<Gesture?> =
        gestureDao.getGestureById(id).map { it?.toDomain() }

    override suspend fun insertGesture(gesture: Gesture) {
        gestureDao.insertGesture(gesture.toEntity())
    }

    override suspend fun updateGesture(gesture: Gesture) {
        gestureDao.updateGesture(gesture.toEntity())
    }

    override suspend fun deleteGesture(id: String) {
        gestureDao.deleteGesture(id)
    }

    override fun getAllMappings(): Flow<List<GestureMapping>> =
        gestureDao.getAllMappings().map { entities -> entities.map { it.toDomain() } }

    override suspend fun upsertMapping(mapping: GestureMapping) {
        gestureDao.upsertMapping(mapping.toEntity())
    }

    override suspend fun deleteMapping(gestureId: String) {
        gestureDao.deleteMapping(gestureId)
    }

    override suspend fun initializeDefaults() {
        if (gestureDao.getGestureCount() > 0) return

        val builtInGestures = listOf(
            Gesture(
                id = "open_hand",
                name = "Open Hand",
                emoji = "\uD83E\uDD1A",
                description = "Open palm facing the camera",
                category = GestureCategory.BUILT_IN
            ),
            Gesture(
                id = "fist",
                name = "Fist",
                emoji = "✊",
                description = "Closed fist facing the camera",
                category = GestureCategory.BUILT_IN
            ),
            Gesture(
                id = "thumbs_up",
                name = "Thumbs Up",
                emoji = "\uD83D\uDC4D",
                description = "Thumb pointing upward",
                category = GestureCategory.BUILT_IN
            ),
            Gesture(
                id = "peace",
                name = "Peace",
                emoji = "✌\uFE0F",
                description = "Index and middle fingers raised",
                category = GestureCategory.BUILT_IN
            ),
            Gesture(
                id = "pointing",
                name = "Pointing",
                emoji = "\uD83D\uDC46",
                description = "Index finger pointing upward",
                category = GestureCategory.BUILT_IN
            )
        )

        builtInGestures.forEach { gestureDao.insertGesture(it.toEntity()) }
        DefaultMappings.all().forEach { gestureDao.upsertMapping(it.toEntity()) }
    }
}
