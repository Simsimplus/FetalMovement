package io.simsim.demo.fetal.helper

import java.time.Duration

class ValidChecker(val duration: Duration) {
    internal constructor(mills: Long) : this(Duration.ofMillis(mills))

    private var _lastSetTime = 0L

    fun apply(
        block: () -> Unit
    ): Boolean {
        return if (System.currentTimeMillis() - _lastSetTime >= duration.toMillis()) {
            block.invoke()
            _lastSetTime = System.currentTimeMillis()
            true
        } else false
    }
}
