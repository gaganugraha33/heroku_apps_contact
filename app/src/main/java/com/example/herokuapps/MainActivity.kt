package com.example.herokuapps

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
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
import org.jetbrains.anko.support.v4.onRefresh
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
        swpContact.onRefresh {
            swpContact.isRefreshing = true
            contactViewModel.setListContact()
            contactViewModel.getListContact().observe(this, getContact)
        }
    }


    private val getContact = Observer<MutableList<Datum>> { contactItems ->
        if (contactItems != null) {
            dataListContact.clear()
            rvContact.visibility = View.VISIBLE
            idLoading.visibility = View.GONE
            idError.visibility = View.GONE
            if (contactItems.size > 0) {
                val datalistMovieNew: MutableList<Datum?> = mutableListOf()
                contactItems.let { datalistMovieNew.addAll(it) }
                adapterContact.addData(datalistMovieNew)
            }
        } else {
            rvContact.visibility = View.GONE
            idLoading.visibility = View.GONE
            idError.visibility = View.VISIBLE
        }
        swpContact.isRefreshing = false
    }

    private val getDeleteContact = Observer<String> { contactMessage ->
        if (contactMessage != null) {
            Toast.makeText(applicationContext, getString(R.string.info_delete_contact_success), Toast.LENGTH_LONG).show()
            dataListContact.clear()
            idLoading.visibility = View.GONE
            rvContact.visibility = View.VISIBLE
            idError.visibility = View.GONE
            if (contactMessage == getString(R.string.contact_deleted)) {
                contactViewModel.setListContact()
                contactViewModel.getListContact().observe(this, getContact)
            }
        } else {
            rvContact.visibility = View.GONE
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

    override fun listenerDelete(id: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.title_dialog))
        builder.setMessage(getString(R.string.desc_dialog))

        builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
            idLoading.visibility = View.VISIBLE
            contactViewModel.deleteContact(id)
            contactViewModel.getContactMessage().observe(this, getDeleteContact)
        }

        builder.setNegativeButton(getString(R.string.no)) { dialog, which ->
            dialog.dismiss()
        }
        builder.show()

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
