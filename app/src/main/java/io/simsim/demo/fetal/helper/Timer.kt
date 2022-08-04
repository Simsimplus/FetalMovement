package io.simsim.demo.fetal.helper

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.Duration
import kotlin.time.toKotlinDuration

object Timer {
    @JvmStatic
    fun formatTimeString(duration: Duration, interval: Duration, started: Boolean = true) =
        timer(duration, interval, started).map {
            it.format()
        }

    @JvmStatic
    fun timer(duration: Duration, interval: Duration, started: Boolean = true) = flow {
        var tmp = duration
        emit(duration)
        if (!started) return@flow
        while (tmp.seconds > 0) {
            tmp -= interval
            kotlinx.coroutines.delay(interval.toKotlinDuration())
            emit(tmp)
        }
    }

    private fun Duration.format() = when {
        this.seconds <= 60 -> {
            "%02ds".format(seconds)
        }
        this.toMinutes() <= 60 -> {
            "%02d:%02d".format(toMinutes() % 60, seconds % 60)
        }
        this.toHours() <= 24 -> {
            "%02d:%02d:%02d".format(toHours() % 24, toMinutes() % 60, seconds % 60)
        }
        else -> {
            "%d天%02d:%02d:%02d".format(toDays(), toHours() % 24, toMinutes() % 60, seconds % 60)
        }
    }
}
