package com.example.herokuapps

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.herokuapps.ContactViewModel.ContactViewModel
import com.example.herokuapps.ContactViewModel.ViewModelMovieFactory
import com.example.herokuapps.api.ContactProvider
import com.example.herokuapps.model.contactmodelbyid.Data
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.detail_contact_activity.*
import kotlinx.android.synthetic.main.failed_load_layout.*
import kotlinx.android.synthetic.main.progress_loading.*

class DetailActivity : AppCompatActivity() {
    private lateinit var contactViewModel: ContactViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_contact_activity)
        idError.visibility = View.GONE
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val compositeDisposable = CompositeDisposable()
        val repository = ContactProvider.contactProviderRepository()

        contactViewModel = ViewModelProviders.of(
            this,
            ViewModelMovieFactory(
                compositeDisposable,
                repository,
                AndroidSchedulers.mainThread(),
                Schedulers.io()
            )
        ).get(ContactViewModel::class.java)
        contactViewModel.setListContactById(intent.getStringExtra(getString(R.string.intent_id_contact_detail)))
        contactViewModel.getContactById().observe(this, getContactById)

    }

    private val getContactById = Observer<Data> { contactItems ->
        if (contactItems != null) {
            idLoading.visibility = View.GONE
            setUI(contactItems)
        } else {
            idError.visibility = View.VISIBLE
        }
    }

    @SuppressLint("SetTextI18n", "CheckResult")
    private fun setUI(dataContact: Data) {
        nameUser.text =
            getString(R.string.name_user) + " " + dataContact.firstName + " " + dataContact.lastName
        ageUser.text = getString(R.string.age_user) + " " + dataContact.age.toString()

        val requestOption = RequestOptions()
        requestOption.placeholder(R.drawable.ic_launcher_background)
        requestOption.error(R.drawable.ic_launcher_background)
        Glide.with(profile_image).setDefaultRequestOptions(requestOption).load(dataContact.photo)
            .into(profile_image)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }

}