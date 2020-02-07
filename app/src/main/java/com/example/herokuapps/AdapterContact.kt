package com.example.herokuapps

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.herokuapps.model.contactmodel.Datum
import com.example.herokuapps.util.Constant
import kotlinx.android.synthetic.main.adapter_contact_view.view.*

class AdapterContact(
    private var listContact: MutableList<Datum?>,
    private val context: Context?,
    private val listenerContact: ListenerContact,
    private val listener: (Datum) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == Constant.VIEW_TYPE_ITEM) {
            val v =
                LayoutInflater.from(context).inflate(R.layout.adapter_contact_view, parent, false)
            Item(v)
        } else {
            val v =
                LayoutInflater.from(context).inflate(R.layout.progress_loading, parent, false)
            LoadingHolder(v)
        }
    }


    fun addData(listDataMovie: MutableList<Datum?>) {
        listDataMovie.let { this.listContact.addAll(it) }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return listContact.size//To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        context?.let {
            listContact[position]?.let { it1 ->
                (holder as Item).bindItem(
                    it1,
                    listener, listenerContact
                )
            }
        }
    }

    class Item(itemview: View) : RecyclerView.ViewHolder(itemview) {
        @SuppressLint("CheckResult", "SetTextI18n")
        fun bindItem(dataContact: Datum, listener: (Datum) -> Unit,listenerContact: ListenerContact) {
            itemView.nameUser.text = dataContact.firstName + " " + dataContact.lastName
            itemView.ageUser.text = dataContact.age.toString()

            val requestOption = RequestOptions()
            requestOption.placeholder(R.drawable.ic_launcher_background)
            requestOption.error(R.drawable.ic_launcher_background)
            Glide.with(itemView.profile_image).setDefaultRequestOptions(requestOption)
                .load(dataContact.photo).into(itemView.profile_image)

            itemView.setOnClickListener {
                listener(dataContact)
            }

            itemView.edit.setOnClickListener {
                listenerContact.listenerEdit(dataContact)
            }

            itemView.delete.setOnClickListener {
               listenerContact.listenerDelete(dataContact.id.toString())
            }

        }
    }

    class LoadingHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


}