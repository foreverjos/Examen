package com.example.examen.models

import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class ProductsModel(

    public open var id     : String = "",
    public open var rating : String = "",
    public open var price  : Double = 0.00,
    public open var image  : String = "",
    public open var title  : String = "",

) : RealmObject()
