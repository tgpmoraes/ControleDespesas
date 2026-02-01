package com.tgpmoraes.controledespesas

import android.app.Application
import com.google.firebase.FirebaseApp

class ControleDespesasApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
