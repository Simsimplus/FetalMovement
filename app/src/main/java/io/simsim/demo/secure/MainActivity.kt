package io.simsim.demo.secure


import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.biometric.BiometricManager.Authenticators.*
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import io.simsim.demo.secure.ui.theme.SecureDemoTheme


class MainActivity : FragmentActivity() {
    val types = mutableListOf(
        BIOMETRIC_STRONG,
        BIOMETRIC_WEAK,
        DEVICE_CREDENTIAL,
    )

    @Volatile
    private var prompt: BiometricPrompt? = null

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val bioTypes = remember {
                types.toMutableStateList()
            }
            BackHandler {
                prompt?.cancelAuthentication()
            }
            SecureDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            indication = null,
                            interactionSource = MutableInteractionSource(),
                            onClick = {
                                prompt?.cancelAuthentication()
                                prompt = null
                            }
                        ),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextButton(onClick = ::showFingerprintPrompt) {
                            Text(text = "authenticate")
                        }
                        CenterAlignRow {
                            Checkbox(
                                checked = bioTypes.contains(BIOMETRIC_STRONG),
                                onCheckedChange = { notContain ->
                                    if (!notContain) bioTypes.remove(BIOMETRIC_STRONG) else bioTypes.add(
                                        BIOMETRIC_STRONG
                                    )
                                })
                            Text(text = "BIOMETRIC_STRONG")
                        }
                        CenterAlignRow {
                            Checkbox(
                                checked = bioTypes.contains(BIOMETRIC_WEAK),
                                onCheckedChange = { notContain ->
                                    if (!notContain) bioTypes.remove(BIOMETRIC_WEAK) else bioTypes.add(
                                        BIOMETRIC_WEAK
                                    )
                                })
                            Text(text = "BIOMETRIC_WEAK")
                        }
                        CenterAlignRow {
                            Checkbox(
                                checked = bioTypes.contains(DEVICE_CREDENTIAL),
                                onCheckedChange = { notContain ->
                                    if (!notContain) bioTypes.remove(DEVICE_CREDENTIAL) else bioTypes.add(
                                        DEVICE_CREDENTIAL
                                    )
                                })
                            Text(text = "DEVICE_CREDENTIAL")
                        }
                    }
                }
            }
        }
    }

    private fun showFingerprintPrompt() {

        val promptInfoBuilder =
            BiometricPrompt.PromptInfo.Builder().setTitle("test").setDescription("test desc")
                .setNegativeButtonText("no")
                .setAllowedAuthenticators(
                    BIOMETRIC_STRONG or BIOMETRIC_WEAK
                )

        prompt = BiometricPrompt(this, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                toast("[$errorCode] $errString")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                toast("succeeded")
            }

            override fun onAuthenticationFailed() {
                toast("failed")
                prompt?.cancelAuthentication()
                prompt?.authenticate(promptInfoBuilder.build())
            }
        })
        prompt?.authenticate(promptInfoBuilder.build())
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SecureDemoTheme {
        Greeting("Android")
    }
}

@Composable
fun CenterAlignRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement,
        content = content
    )
}