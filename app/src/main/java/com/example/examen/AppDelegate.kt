package com.example.examen

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.rx.RealmObservableFactory

class AppDelegate : Application() {
    private var tokenFirebase = ""

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)

        val config = RealmConfiguration.Builder().name("examen.realm").schemaVersion(1)
            .deleteRealmIfMigrationNeeded().rxFactory(RealmObservableFactory(false)).build()
        Realm.setDefaultConfiguration(config)

    }

    fun getTokenFirebase(): String {
        return this.tokenFirebase
    }
}