package io.simsim.demo.fetal.helper

import androidx.dynamicanimation.animation.FloatValueHolder
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce

fun scaleAnimation(
    scaleBack: Boolean = true,
    endScale: Float = 1.2f,
    onUpdate: (Float) -> Unit
): SpringAnimation {
    val heartViewScaleDown =
        SpringAnimation(FloatValueHolder(endScale)).addUpdateListener { _, value, _ ->
            onUpdate(value)
        }.setSpring(
            SpringForce().apply {
                finalPosition = 1f
                stiffness = SpringForce.STIFFNESS_VERY_LOW
                dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
            }
        )
    return SpringAnimation(FloatValueHolder(1f)).addUpdateListener { _, value, _ ->
        onUpdate(value)
    }.setSpring(
        SpringForce().apply {
            finalPosition = endScale
            stiffness = SpringForce.STIFFNESS_VERY_LOW
            dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
        }
    ).addEndListener { _, _, _, _ ->
        if (scaleBack) heartViewScaleDown.start()
    }
}
