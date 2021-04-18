package com.example.examen.models

import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class HistorySearchModel(
    public open var date   : String = "",
    public open var text   : String = ""
) : RealmObject()
