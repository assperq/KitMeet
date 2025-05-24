package com.example.cardss.presentation

import com.russhwolf.settings.Settings
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class SwipeTracker(private val settings: Settings) {

    companion object {
        private const val KEY_COUNT = "swipe_count"
        private const val KEY_DATE = "swipe_date"
    }

    private fun today(): LocalDate {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    fun getSwipeCount(): Int {
        val savedDateStr = settings.getStringOrNull(KEY_DATE)
        val savedDate = savedDateStr?.let { LocalDate.parse(it) }
        val currentDate = today()

        return if (savedDate == currentDate) {
            settings.getInt(KEY_COUNT, 0)
        } else {
            0
        }
    }

    fun incrementSwipeCount(): Int {
        val currentDate = today()
        val savedDateStr = settings.getStringOrNull(KEY_DATE)
        val savedDate = savedDateStr?.let { LocalDate.parse(it) }

        return if (savedDate == currentDate) {
            val newCount = settings.getInt(KEY_COUNT, 0) + 1
            settings.putInt(KEY_COUNT, newCount)
            newCount
        } else {
            settings.putString(KEY_DATE, currentDate.toString())
            settings.putInt(KEY_COUNT, 1)
            1
        }
    }

    fun reset() {
        settings.putInt(KEY_COUNT, 0)
        settings.putString(KEY_DATE, today().toString())
    }
}

