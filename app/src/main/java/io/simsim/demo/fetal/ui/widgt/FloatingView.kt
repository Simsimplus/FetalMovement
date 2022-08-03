package io.simsim.demo.fetal.ui.widgt

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.isVisible
import io.simsim.demo.fetal.R
import io.simsim.demo.fetal.ui.theme.Purple40
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import splitties.views.dsl.core.horizontalLayout
import splitties.views.dsl.core.imageView
import splitties.views.dsl.core.textView
import splitties.views.onClick

@SuppressLint("ViewConstructor")
class FloatingView constructor(
    context: Context,
    private val remainTimeFlow: Flow<String>,
    private val clickTextFlow: Flow<String>,
    private val onClick: (View) -> Unit,
    private val coroutineScope: CoroutineScope
) : FrameLayout(
    context,
    null,
    0
) {
    @Volatile
    private var _autoCollapseJob: Job? = null
    private val autoCollapseJob: Job
        get() = _autoCollapseJob ?: coroutineScope.launch(start = CoroutineStart.LAZY) {
            delay(3000)
            toggleView()
        }.also {
            _autoCollapseJob = it
            it.invokeOnCompletion {
                _autoCollapseJob = null
            }
        }

    private val collapsedView = context.textView {
        setTextColor(Purple40.toArgb())
    }
    private val clickTextView = context.textView {
        setTextColor(Purple40.toArgb())
        text = "0/0"
    }
    private val clickImageView = context.imageView {
        setImageResource(R.drawable.ic_heart_rate)
        imageTintList = ColorStateList.valueOf(Purple40.toArgb())
        onClick {
            onClick(it)
            autoCollapseJob.cancel()
            autoCollapseJob.start()
        }
    }

    private val expandedView = context.horizontalLayout {
        isVisible = false
        addView(clickImageView)
        addView(clickTextView)
    }

    init {
        coroutineScope.launch {
            launch {
                remainTimeFlow.collectLatest {
                    collapsedView.text = it
                }
            }
            launch {
                clickTextFlow.collectLatest {
                    clickTextView.text = it
                }
            }
        }
        initView()
    }

    private fun initView() {
        onClick {
            toggleView()
        }
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        setBackgroundColor(Color.WHITE)
        addView(collapsedView)
        addView(expandedView)
    }

    private fun toggleView() {
        expandedView.isVisible = !expandedView.isVisible
        collapsedView.isVisible = !collapsedView.isVisible
        if (expandedView.isVisible) {
            autoCollapseJob.start()
        }
    }
}
