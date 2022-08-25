package io.simsim.demo.fetal.ui.history

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.simsim.demo.fetal.data.FetalDB
import javax.inject.Inject

@HiltViewModel
class HistoryVM @Inject constructor(
    db: FetalDB
) : ViewModel() {
    val recordsFlow = db.recordDao().queryAllRecords()
}
