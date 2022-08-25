package io.simsim.demo.fetal.ui.widgt

import android.content.Context
import io.data2viz.charts.chart.Chart
import io.data2viz.charts.chart.chart
import io.data2viz.charts.config.configuration
import io.data2viz.geom.Size
import io.data2viz.viz.VizContainerView

abstract class ChartsView<DATA>(context: Context) : VizContainerView(context) {
    open val vizSize = 500.0
    abstract val dataResetBlock: Chart<DATA>.() -> Unit
    open val chartConfig = configuration { }
    var datum: List<DATA> = emptyList()
        set(value) {
            field = value
            if (value.isNotEmpty()) {
                chart = this.chart(value, config = chartConfig, init = dataResetBlock).apply {
                    size = Size(vizSize)
                }
            }
        }
    var chart: Chart<DATA>? = null
}
