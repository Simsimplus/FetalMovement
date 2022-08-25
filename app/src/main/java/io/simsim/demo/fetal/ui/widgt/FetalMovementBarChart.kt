package io.simsim.demo.fetal.ui.widgt

import android.content.Context
import io.data2viz.charts.chart.Chart
import io.data2viz.charts.chart.discrete
import io.data2viz.charts.chart.mark.bar
import io.data2viz.charts.chart.quantitative
import io.data2viz.charts.config.ChartConfig
import io.data2viz.charts.config.configuration
import io.data2viz.charts.core.PanMode
import io.data2viz.charts.core.SelectionMode
import io.data2viz.charts.core.ZoomMode
import io.data2viz.color.Colors

class FetalMovementBarChart(context: Context) : ChartsView<FetalMovementRecordSimpleData>(context) {
    override val chartConfig: ChartConfig = configuration {
        this.xAxis {
            this.fontSize = 1.0
            this.fontColor = Colors.rgb(0xba5140)
        }
        this.chart {
            this.backgroundColor
        }
        events {
            zoomMode = ZoomMode.None // enable zooming on X and Y directions
            panMode = PanMode.X // disable panning
            this.selectionMode = SelectionMode.Single
        }
    }
    override val dataResetBlock: Chart<FetalMovementRecordSimpleData>.() -> Unit = {
        title = "fetal movement"
        tooltip {
            this.formatter = {
                "${this.domain.movementCount}"
            }
        }
        val x = discrete({
            domain.date
        }) {
            name = "日期"
        }
        val y = quantitative({
            domain.movementCount.toDouble()
        }) {
            name = "总胎动数"
        }
        bar(x, y)
    }
}

data class FetalMovementRecordSimpleData(
    val date: String,
    val movementCount: Int
)
