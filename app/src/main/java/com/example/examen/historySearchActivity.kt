package com.example.examen

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.examen.adapters.HistoryAdapter
import com.example.examen.adapters.ProductsAdapter
import com.example.examen.models.HistorySearchModel
import com.example.examen.models.ProductsModel
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_historysearh.*
import kotlinx.android.synthetic.main.activity_historysearh.toolbar
import kotlinx.android.synthetic.main.activity_main.*

class historySearchActivity  : AppCompatActivity(){

    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historysearh)

        realm = Realm.getDefaultInstance()

        setSupportActionBar(toolbar)
        toolbar!!.setNavigationOnClickListener(View.OnClickListener {
            hideKeyboard(getWindow().getDecorView().getRootView())
            finish()
        })

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        var drawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_back, null)

        drawable = DrawableCompat.wrap(drawable!!)
        DrawableCompat.setTint(
            drawable,
            ContextCompat.getColor(this@historySearchActivity, R.color.colorWhite)
        )

        supportActionBar!!.setHomeAsUpIndicator(drawable)
        supportActionBar!!.setTitle("Historico de consultas")

        var res = realm.where<HistorySearchModel>().sort("text", Sort.ASCENDING).findAll()
        var hist: java.util.ArrayList<HistorySearchModel>  = java.util.ArrayList()

        if (res.size > 0) {
            hist.addAll(realm.copyFromRealm(res))
        }

        var historyAdapter  = object : HistoryAdapter(this, hist) {}

        rcvLista.apply {
            setHasFixedSize(true)
            adapter = historyAdapter
        }

    }

    fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}