package io.simsim.demo.fetal.helper

import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun tryAuth(activity: FragmentActivity) = runCatching {
    tryAuthOrThrows(activity)
}.fold(
    onSuccess = { it },
    onFailure = Throwable::message
) ?: "unknown error"

@Throws(
    Exception::class
)
suspend fun tryAuthOrThrows(activity: FragmentActivity) =
    suspendCancellableCoroutine { continuation ->
        val promptInfoBuilder =
            BiometricPrompt.PromptInfo.Builder().setTitle("test").setDescription("test desc")
                .setNegativeButtonText("取消")
        var prompt: BiometricPrompt? = null
        prompt = BiometricPrompt(
            activity,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    continuation.resumeWithException(Exception("[$errorCode] $errString"))
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    continuation.resume("yes")
                }

                override fun onAuthenticationFailed() {
                    if (continuation.isCancelled) {
                        prompt?.cancelAuthentication()
                        return
                    }
                    continuation.resumeWithException(Exception("unknown fail"))
                }
            }
        )
        continuation.invokeOnCancellation {
            prompt.cancelAuthentication()
        }
        prompt.authenticate(promptInfoBuilder.build())
    }
