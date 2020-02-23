package com.example.herokuapps

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.herokuapps.ContactViewModel.ContactViewModel
import com.example.herokuapps.ContactViewModel.ViewModelMovieFactory
import com.example.herokuapps.api.ContactProvider
import com.example.herokuapps.model.contactmodel.Datum
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.add_contact_activity.*
import kotlinx.android.synthetic.main.progress_loading.*
import android.app.Activity
import android.content.Intent


class EditContactActivity : AppCompatActivity() {
    private lateinit var dataContact: Datum
    private val compositeDisposable = CompositeDisposable()
    private val repository = ContactProvider.contactProviderRepository()
    private lateinit var contactViewModel: ContactViewModel
    private var idContact = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_contact_activity)
        idLoading.visibility = View.GONE
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dataContact = intent.getSerializableExtra(getString(R.string.name_intent_data_contact)) as Datum

        firstname.setText(dataContact.firstName)
        lastname.setText(dataContact.lastName)
        age.setText(dataContact.age.toString())
        urlPhoto.setText(dataContact.photo)
        idContact = dataContact.id.toString()
        submit.setText(getString(R.string.edit_contact))

        submit.setOnClickListener(View.OnClickListener {
            when {
                firstname.text.isEmpty() -> {
                    firstname.error = getString(R.string.firstname_info_error)
                    return@OnClickListener
                }
                lastname.text.isEmpty() -> {
                    lastname.error = getString(R.string.lastname_info_error)
                    return@OnClickListener
                }
                age.text.isEmpty() -> {
                    age.error = getString(R.string.age_info_error)
                    return@OnClickListener
                }
                urlPhoto.text.isEmpty() -> {
                    urlPhoto.error = getString(R.string.photo_info_error)
                    return@OnClickListener
                }
                else -> {
                    idLoading.visibility = View.VISIBLE
                    dataContact = Datum()
                    dataContact.firstName = firstname.text.toString()
                    dataContact.lastName = lastname.text.toString()
                    dataContact.age = age.text.toString().toInt()
                    dataContact.photo = urlPhoto.text.toString()

                    contactViewModel = ViewModelProviders.of(
                        this,
                        ViewModelMovieFactory(
                            compositeDisposable,
                            repository,
                            AndroidSchedulers.mainThread(),
                            Schedulers.io()
                        )
                    ).get(ContactViewModel::class.java)
                    contactViewModel.editContact(dataContact, idContact)
                    contactViewModel.getContactMessage().observe(this, getEditContactMessage)
                }
            }

        })
    }

    private val getEditContactMessage = Observer<String> { contactMessage ->
        if (contactMessage != null) {
            idLoading.visibility = View.GONE
            if (contactMessage == getString(R.string.contact_edited)) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.info_success_edit_contact),
                    Toast.LENGTH_LONG
                ).show()
            }
            val returnIntent = Intent()
            returnIntent.putExtra("resultEdit", contactMessage)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()

        } else {
            idLoading.visibility = View.GONE
            Toast.makeText(
                applicationContext,
                getString(R.string.info_error_response),
                Toast.LENGTH_LONG
            ).show()
        }
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