package io.simsim.demo.fetal.ui.history

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import io.simsim.demo.fetal.ui.theme.FetalDemoTheme

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FetalDemoTheme {
            }
        }
    }
}
