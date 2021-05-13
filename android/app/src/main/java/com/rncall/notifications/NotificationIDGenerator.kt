package com.rncall.notifications

import java.util.concurrent.atomic.AtomicInteger

object NotificationIDGenerator {
    private val c = AtomicInteger(0)
    fun generateNextID(): Int {
        return c.incrementAndGet()
    }
}
