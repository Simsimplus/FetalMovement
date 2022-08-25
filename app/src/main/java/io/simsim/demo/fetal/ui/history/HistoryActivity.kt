package io.simsim.demo.fetal.ui.history

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.viewinterop.AndroidView
import dagger.hilt.android.AndroidEntryPoint
import io.data2viz.charts.chart.Chart
import io.data2viz.charts.chart.discrete
import io.data2viz.charts.chart.mark.area
import io.data2viz.charts.chart.quantitative
import io.data2viz.geom.Size
import io.simsim.demo.fetal.ui.theme.FetalDemoTheme
import io.simsim.demo.fetal.ui.widgt.ChartsView
import io.simsim.demo.fetal.ui.widgt.FetalMovementBarChart
import io.simsim.demo.fetal.ui.widgt.FetalMovementRecordSimpleData
import io.simsim.demo.fetal.ui.widgt.MarqueeText
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class HistoryActivity : AppCompatActivity() {
    private val vm: HistoryVM by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val records by vm.recordsFlow.collectAsState(initial = emptyList())
            FetalDemoTheme {
                Column {
                    MarqueeText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt")
                    AndroidView(
//                        modifier = Modifier.fillMaxWidth(),
                        factory = { FetalMovementBarChart(it) }
                    ) { barChart ->
                        barChart.datum = records.map {
                            FetalMovementRecordSimpleData(
                                date = it.recordStartTime.format(DateTimeFormatter.ofPattern("MM-dd")),
                                movementCount = it.totalMovement
                            )
                        }
                    }
//                    AndroidView(
// //                        modifier = Modifier.fillMaxWidth(),
//                        factory = {
//                            CanadaChart(it)
//                        },
//                    )
                }
            }
        }
    }
}

@Composable
fun MeasureUnconstrainedViewWidth(
    viewToMeasure: @Composable () -> Unit,
    content: @Composable (placeable: Placeable) -> Unit
) {
    SubcomposeLayout { constraints ->
        val measuredPlaceable = subcompose("viewToMeasure", viewToMeasure)[0]
            .measure(Constraints())

        val contentPlaceable = subcompose("content") {
            content(measuredPlaceable)
        }[0].measure(constraints)
        layout(contentPlaceable.width, contentPlaceable.height) {
            contentPlaceable.place(0, 0)
        }
    }
}

class CanadaChart(context: Context) : ChartsView<CanadaChart.PopCount>(context) {
    override val dataResetBlock: Chart<PopCount>.() -> Unit = {
        size = Size(vizSize, vizSize)
        title = "Population of Canada 1851–2001 (Statistics Canada)"

        // Create a discrete dimension for the year of the census
        val year = discrete({ domain.year })

        // Create a continuous numeric dimension for the population
        val population = quantitative({ domain.population }) {
            name = "Population of Canada (in millions)"
        }

        // Using a discrete dimension for the X-axis and a continuous one for the Y-axis
        area(year, population)
    }

    data class PopCount(val year: Int, val population: Double)

    init {
        datum = listOf(
            PopCount(1851, 2.436),
            PopCount(1861, 3.23),
            PopCount(1871, 3.689),
            PopCount(1881, 4.325),
            PopCount(1891, 4.833),
            PopCount(1901, 5.371),
            PopCount(1911, 7.207),
            PopCount(1921, 8.788),
            PopCount(1931, 10.377),
            PopCount(1941, 11.507),
            PopCount(1951, 13.648),
            PopCount(1961, 17.78),
            PopCount(1971, 21.046),
            PopCount(1981, 23.774),
            PopCount(1991, 26.429),
            PopCount(2001, 30.007)
        )
    }
}
