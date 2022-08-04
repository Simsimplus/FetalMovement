package io.simsim.demo.fetal.ui.main

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.simsim.demo.fetal.base.BaseViewModel
import io.simsim.demo.fetal.data.FetalDB
import io.simsim.demo.fetal.data.entity.FetalMovementRecord
import io.simsim.demo.fetal.helper.Timer
import io.simsim.demo.fetal.helper.ValidChecker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.time.Duration
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainVM @Inject constructor(
    private val db: FetalDB
) : BaseViewModel() {
    private val validChecker = ValidChecker(Duration.ofMinutes(5))
    private val _validTimeFlowStarter = MutableStateFlow(false)
    private val _fetalMovementRecordFlow = MutableSharedFlow<FetalMovementRecord>()

    val timerFlow = Timer.formatTimeString(Duration.ofHours(1), Duration.ofSeconds(1))
        .stateIn(viewModelScope, SharingStarted.Lazily, initialValue = "--")

    val validTimeFlow = _validTimeFlowStarter.flatMapLatest {
        Timer.formatTimeString(
            validChecker.duration,
            Duration.ofSeconds(1),
            it
        ).onCompletion {
            _validTimeFlowStarter.value = false
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, initialValue = "--")

    val totalClick = _fetalMovementRecordFlow.flatMapLatest {
        db.recordDao().queryRecord(it.recordId)
    }.map {
        it.totalMovement
    }

    val validClick = _fetalMovementRecordFlow.flatMapLatest {
        db.recordDao().queryRecord(it.recordId)
    }.map {
        it.validMovement
    }

    fun onClick() {
//        validChecker.apply {
//            _validTimeFlowStarter.value = true
//            validClick.update {
//                it + 1
//            }
//        }
//        totalClick.update {
//            it + 1
//        }
    }
}
