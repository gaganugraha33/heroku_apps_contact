package com.example.herokuapps

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.herokuapps.ContactViewModel.ContactViewModel
import com.example.herokuapps.ContactViewModel.ViewModelMovieFactory
import com.example.herokuapps.api.ContactProvider
import com.example.herokuapps.model.contactmodel.Datum
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.failed_load_layout.*
import kotlinx.android.synthetic.main.progress_loading.*
import java.io.Serializable


class MainActivity : AppCompatActivity(), ListenerContact {

    private lateinit var adapterContact: AdapterContact
    private lateinit var contactViewModel: ContactViewModel
    private var dataListContact: MutableList<Datum?> = mutableListOf()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private val resultCode = 3
    private val resultCodeEdit = 4
    private val compositeDisposable = CompositeDisposable()
    private val repository = ContactProvider.contactProviderRepository()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        idError.visibility = View.GONE

        fab.setOnClickListener {
            val intentAdd = Intent(applicationContext, AddContactActivity::class.java)
            startActivityForResult(intentAdd, resultCode)
        }

        adapterContact = AdapterContact(dataListContact, applicationContext, this) {
            val intentDetail = Intent(applicationContext, DetailActivity::class.java)
            intentDetail.putExtra(getString(R.string.intent_id_contact_detail), it.id)
            startActivity(intentDetail)
        }

        linearLayoutManager = LinearLayoutManager(applicationContext)
        rvContact.layoutManager = linearLayoutManager
        rvContact.hasFixedSize()
        rvContact.adapter = adapterContact

        contactViewModel = ViewModelProviders.of(
            this,
            ViewModelMovieFactory(
                compositeDisposable,
                repository,
                AndroidSchedulers.mainThread(),
                Schedulers.io()
            )
        ).get(ContactViewModel::class.java)
        contactViewModel.setListContact()
        contactViewModel.getListContact().observe(this, getContact)

    }


    private val getContact = Observer<MutableList<Datum>> { contactItems ->
        if (contactItems != null) {
            dataListContact.clear()
            idLoading.visibility = View.GONE
            if (contactItems.size > 0) {
                val datalistMovieNew: MutableList<Datum?> = mutableListOf()
                contactItems.let { datalistMovieNew.addAll(it) }
                adapterContact.addData(datalistMovieNew)
            }
        } else {
            mainView.visibility = View.GONE
            idLoading.visibility = View.GONE
            idError.visibility = View.VISIBLE
        }
    }

    override fun listenerEdit(dataContact: Datum) {
        val intentEdit = Intent(this, EditContactActivity::class.java)
        intentEdit.putExtra(
            getString(R.string.name_intent_data_contact),
            dataContact as Serializable
        )
        startActivityForResult(intentEdit, resultCodeEdit)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (resultCode == Activity.RESULT_OK) {
            if (data != null && data.getStringExtra("result") == getString(R.string.info_success_add_contact_message)) {
                idLoading.visibility = View.VISIBLE
                contactViewModel.setListContact()
                contactViewModel.getListContact().observe(this, getContact)
            } else if (data != null && data.getStringExtra("resultEdit") == getString(R.string.contact_edited)) {
                idLoading.visibility = View.VISIBLE
                contactViewModel.setListContact()
                contactViewModel.getListContact().observe(this, getContact)
            }
        }
    }
}
