package io.simsim.demo.fetal.helper

import kotlinx.coroutines.flow.flow
import java.time.Duration
import kotlin.time.toKotlinDuration

object Timer {
    @JvmStatic
    fun timer(duration: Duration, interval: Duration) = flow {
        var tmp = duration
        emit(duration.format())
        while (tmp.seconds > 0) {
            tmp -= interval
            kotlinx.coroutines.delay(interval.toKotlinDuration())
            emit(tmp.format())
        }
    }

    private fun Duration.format() = when {
        this.toDays() > 0 -> {
            "%d天%d:%d:%d:".format(toDays(), toHours() % 24, toMinutes() % 60, seconds % 60)
        }
        this.toHours() > 0 -> {
            "%d:%d:%d".format(toHours() % 24, toMinutes() % 60, seconds % 60)
        }
        this.toMinutes() > 0 -> {
            "%d:%d".format(toMinutes() % 60, seconds % 60)
        }
        else -> {
            "%ds".format(seconds)
        }
    }
}
