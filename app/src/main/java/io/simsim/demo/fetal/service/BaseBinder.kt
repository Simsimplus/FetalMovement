package io.simsim.demo.fetal.service

import android.app.Service
import android.os.Binder

abstract class BaseBinder : Binder() {
    abstract val service: Service
}
