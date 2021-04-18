package com.example.examen.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.examen.R
import com.example.examen.models.HistorySearchModel
import kotlin.collections.ArrayList

abstract class HistoryAdapter(val context: Context, var arrayList: ArrayList<HistorySearchModel>) :
    RecyclerView.Adapter<HistoryAdapter.RecyclerViewHolder>() {
    inner class RecyclerViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(
            inflater.inflate(R.layout.view_holder_history, parent, false)
        ) {

        private var cardView: CardView? = null
        private var txtDate: TextView? = null
        private var txtText: TextView? = null


        init {
            cardView = itemView.findViewById(R.id.cardView)
            txtDate = itemView.findViewById(R.id.txtDate)
            txtText = itemView.findViewById(R.id.txtText)


        }

        fun bind(histo: HistorySearchModel) {
            txtDate!!.text = histo.date
            txtText!!.text =  histo.text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val holder = RecyclerViewHolder(inflater, parent)
        holder.setIsRecyclable(true)

        return holder
    }

    override fun getItemCount(): Int = arrayList.size

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val gasto: HistorySearchModel = arrayList[position]
        holder.bind(gasto)
    }

}