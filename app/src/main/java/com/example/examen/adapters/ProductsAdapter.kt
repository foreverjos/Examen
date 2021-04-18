package com.example.examen.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.examen.R
import com.example.examen.models.ProductsModel
import com.squareup.picasso.Picasso
import java.text.DecimalFormat
import kotlin.collections.ArrayList

abstract class ProductsAdapter(val context: Context, var arrayList: ArrayList<ProductsModel>) :
    RecyclerView.Adapter<ProductsAdapter.RecyclerViewHolder>() {
    inner class RecyclerViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(
            inflater.inflate(R.layout.view_holder_products, parent, false)
        ) {

        private var cardView: CardView? = null
        private var txtTitle: TextView? = null
        private var txtPrice: TextView? = null
        private var imgProduct: ImageView? = null


        init {
            cardView = itemView.findViewById(R.id.cardView)
            txtTitle = itemView.findViewById(R.id.txtTitle)
            txtPrice = itemView.findViewById(R.id.txtPrice)
            imgProduct = itemView.findViewById(R.id.imgProduct)

        }

        fun bind(product: ProductsModel) {
            txtTitle!!.text = product.title
            txtPrice!!.text = "$"+product.price.convert()



            if (!product.image.equals("")) {
                val rute = product.image
                Picasso.get().load(rute). into(imgProduct)
            }else{
                imgProduct!!.setImageResource(R.mipmap.ic_launcher)
            }

            cardView!!.setOnClickListener {
                getList(product)
            }
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
        val gasto: ProductsModel = arrayList[position]
        holder.bind(gasto)
    }

    abstract fun getList(arrayList: ProductsModel)

    fun refreshData(arrayList: ArrayList<ProductsModel>) {
        this.arrayList.clear()
        this.arrayList.addAll(arrayList)
        notifyDataSetChanged()
    }

    fun Double.convert(): String {
        val format = DecimalFormat("#,###.00")
        format.isDecimalSeparatorAlwaysShown = false
        return format.format(this).toString()
    }
}