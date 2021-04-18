package com.example.examen


import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.examen.adapters.ProductsAdapter
import com.example.examen.conection.HttpClass
import com.example.examen.models.ProductsModel
import com.example.examen.models.HistorySearchModel
import hommi.foods.adapters.SearchAdapter
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.pgdEspera
import kotlinx.android.synthetic.main.view_holder_products.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    var sMsg_error=""
    lateinit var realm: Realm

    var products = ArrayList<ProductsModel>()
    lateinit var Adapter: ProductsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        realm = Realm.getDefaultInstance()

        btnSearch.setOnClickListener {
            fnSearch()
        }


        Adapter  = object : ProductsAdapter(this, products) {
            override fun getList(Product: ProductsModel) {
                hideKeyboard(getWindow().getDecorView().getRootView())
                val busqueda= edtSearch.editText!!.text.toString()
                GetResult().execute(busqueda)
            }
        }

        rcvProducts.apply {
            layoutManager = GridLayoutManager(context, 2)
            setHasFixedSize(true)
            adapter = Adapter
        }

        edtSearch.editText!!.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                // if the event is a key down event on the enter button
                if ( keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_SEARCH
                ) {
                    fnSearch()
                    return true
                }
                return false
            }
        })

        gethitorysearch()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if(item.itemId==R.id.action_hist){
            var intent: Intent = Intent(this@MainActivity, historySearchActivity::class.java)
            startActivity(intent)
        }
        return true
    }

    fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun fnSearch(){
        pgdEspera.visibility=View.VISIBLE
        hideKeyboard(getWindow().getDecorView().getRootView())
        val busqueda= edtSearch.editText!!.text.toString()
        GetResult().execute(busqueda)
    }

    internal inner class GetResult : AsyncTask<String, String, String>() {


        override fun doInBackground(vararg params: String): String {

            sMsg_error=""
            var res_envio = ""

            var URL= getString(R.string.URL)

            var http_indice = HttpClass (URL)

            http_indice.AddParam("query", params[0])
            http_indice.AddHeader("X-IBM-Client-Id",getString(R.string.Key))

            try {
                http_indice.Execute(HttpClass.RequestMethod.GET)
            } catch (e: Exception) {
                if (e.toString() != null) res_envio = "Excepcion: $e"
            }


            val conect: String = http_indice.getErrorMessage()
            if (conect == "OK") {
                res_envio = http_indice.getResponse()
            }else{
                sMsg_error="Error: La conexión con el servidor es inestable  \nRevisa tu conexión y vuelve a intentar"
            }

            return res_envio

        }

        override fun onPostExecute(res_envio: String) {
            super.onPostExecute(res_envio)

            if(sMsg_error.trim().equals("")){
                try {
                    val json = JSONTokener(res_envio).nextValue()
                    if (json is JSONObject) {
                        val JSonResponse = JSONObject(res_envio)


                        if (JSonResponse.has("items")) {
                            products= ArrayList()
                            realm.executeTransaction {
                                it.delete(ProductsModel::class.java)
                            }
                            realm.beginTransaction()
                            val JSONProducts = JSONArray(JSonResponse.getString("items"))
                            for (i in 0 until JSONProducts.length()) {
                                val JsonData = JSONObject(JSONProducts.getString(i))
                                var Product = ProductsModel()

                                Product.id     = JsonData.getString("id").trim { it <= ' ' }
                                Product.title  = JsonData.getString("title").trim { it <= ' ' }
                                Product.price  = JsonData.getDouble("price")
                                Product.rating = JsonData.getString("rating").trim { it <= ' ' }
                                Product.image  = JsonData.getString("image").trim { it <= ' ' }

                                realm.insert(Product)
                            }
                            realm.commitTransaction()

                        } else {
                            sMsg_error = "Error: La recepción de datos no es la esperada, revisa tu conexión de datos  y vuelve a intentar"
                        }

                    }else{
                        sMsg_error = "Error: La recepción de datos no es la esperada, revisa tu conexión de datos  y vuelve a intentar"
                    }

                } catch (e: JSONException) {
                    //res_envio="Debes revisar tu conexión a Internet";
                    sMsg_error = "Error: La recepción de datos no es la esperada, revisa tu conexión de datos  y vuelve a intentar"
                }
            }

            pgdEspera.visibility=View.GONE
            if(sMsg_error.trim().equals("")){
                val results = realm.where<ProductsModel>().findAll()


                realm.beginTransaction()
                var busq=HistorySearchModel()
                busq.date=getDate()
                busq.text=edtSearch.editText!!.text.toString()
                realm.insert(busq)
                realm.commitTransaction()

                realm.executeTransaction {
                    products.clear()
                    products.addAll(results)
                    Adapter.refreshData(products)
                    gethitorysearch()
                }




            }else{
                Toast.makeText(this@MainActivity,"Error favor de intentar nuevamente",Toast.LENGTH_LONG).show()
            }
        }
    }

    fun gethitorysearch(){

        var res = realm.where<HistorySearchModel>().sort("text", Sort.ASCENDING).distinct("text").findAll()
        var hist: java.util.ArrayList<HistorySearchModel>  = java.util.ArrayList()

        if (res.size > 0) {
            hist.addAll(realm.copyFromRealm(res))
        }


        val Hsearh = SearchAdapter(this@MainActivity, android.R.layout.simple_list_item_1, hist)
        (edtSearch.editText as AutoCompleteTextView).setAdapter(Hsearh)
        (edtSearch.editText as AutoCompleteTextView).threshold = 1

        (edtSearch.editText as AutoCompleteTextView).setOnItemClickListener { parent, _, position, id ->
            val HistorySearchModel = parent?.adapter?.getItem(position) as HistorySearchModel?
            (edtSearch.editText as AutoCompleteTextView).setText(HistorySearchModel?.text)
            fnSearch()
        }
    }

    fun getDate(): String {
        val date = Date()
        var Fecha: String
        val calendar: Calendar
        val mes: Int
        val anu: Int
        val dia: Int
        val hora: Int
        val min: Int
        val seg: Int

        calendar = Calendar.getInstance(Locale.getDefault())
        calendar.time = date
        mes = calendar.get(Calendar.MONTH) + 1
        anu = calendar.get(Calendar.YEAR)
        dia = calendar.get(Calendar.DAY_OF_MONTH)
        hora = calendar.get(Calendar.HOUR_OF_DAY)
        min = calendar.get(Calendar.MINUTE)
        seg = calendar.get(Calendar.SECOND)

        var smes = mes.toString()
        var sdia = dia.toString()

        var shora=hora.toString()
        var smin = min.toString()
        var sseg = seg.toString()


        if (mes < 10) smes = "0$mes"
        if (dia < 10) sdia = "0$dia"

        if (hora < 10) shora = "0$shora"
        if (min < 10) smin = "0$smin"
        if (seg < 10) sseg = "0$sseg"

        Fecha = "$anu-$smes-$sdia $shora:$smin:$sseg"
        return Fecha
    }

}