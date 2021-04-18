package hommi.foods.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.example.examen.R
import com.example.examen.models.HistorySearchModel
import kotlin.collections.ArrayList

class SearchAdapter(
    val mcontext: Context,
    @LayoutRes private val layout: Int,
    private val searhModels: ArrayList<HistorySearchModel>
) : ArrayAdapter<HistorySearchModel>(mcontext, layout, searhModels), Filterable {
    private var mSerarch: ArrayList<HistorySearchModel> = searhModels

    @SuppressLint("ViewHolder")
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val view = LayoutInflater.from(mcontext).inflate(
            mcontext.resources.getLayout(R.layout.view_holder_seach),
            parent,
            false
        )

        val serarch = mSerarch[position]
        val textView = view.findViewById<TextView>(R.id.txtUser)
        textView.text = serarch.text

        return view
    }

    override fun getCount(): Int {
        return mSerarch.size
    }

    override fun getItem(position: Int): HistorySearchModel {
        return mSerarch[position]
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val queryString = constraint?.toString()?.toLowerCase()
                val filterResults = FilterResults()
                filterResults.values = if (queryString == null || queryString.isEmpty())
                    searhModels
                else
                    searhModels.filter {
                        it.text.toLowerCase().contains(queryString)
                    }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                mSerarch = if (results != null) {
                    if (results.values != null) {
                        results.values as ArrayList<HistorySearchModel>
                    } else {
                        searhModels
                    }
                } else {
                    searhModels
                }
                notifyDataSetChanged()
            }
        }
    }
}

