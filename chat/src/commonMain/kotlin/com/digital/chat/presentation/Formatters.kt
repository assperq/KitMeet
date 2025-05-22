package com.digital.chat.presentation

import com.digital.chat.domain.Message
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime


fun List<Message>.groupByDate(): List<Message> {
    if (isEmpty()) return emptyList()

    val result = mutableListOf<Message>()
    var currentDate: LocalDate? = null

    forEach { message ->
        val messageDate = message.createdAt.toLocalDateTime(TimeZone.currentSystemDefault()).date
        if (currentDate != messageDate) {
            currentDate = messageDate
            result.add(
                message.copy(
                    content = "",
                    showDate = true
                )
            )
        }
        result.add(message)
    }

    return result
}

fun getDateFromInstant(instant: Instant): LocalDate {
    return instant
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
}

fun getTimeFromInstant(instant: Instant): String {
    val timeFormat = LocalTime.Format {
        hour(); char(':'); minute()
    }
    return instant
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .time
        .format(timeFormat)
}

fun formatMessageDate(instant: Instant): String {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val messageDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date

    return when (messageDate) {
        today -> "Сегодня"
        today.minus(1, DateTimeUnit.DAY) -> "Вчера"
        else -> {
            val monthName = when (messageDate.monthNumber) {
                1 -> "января"
                2 -> "февраля"
                3 -> "марта"
                4 -> "апреля"
                5 -> "мая"
                6 -> "июня"
                7 -> "июля"
                8 -> "августа"
                9 -> "сентября"
                10 -> "октября"
                11 -> "ноября"
                12 -> "декабря"
                else -> ""
            }
            "${messageDate.dayOfMonth} $monthName"
        }
    }
}

