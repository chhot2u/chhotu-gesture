package com.chhotu.gesture.domain.model

enum class ScrollSpeed(val intervalMs: Long, val distancePx: Int, val label: String) {
    SLOW(800L, 200, "Slow"),
    MEDIUM(400L, 350, "Medium"),
    FAST(150L, 500, "Fast")
}
