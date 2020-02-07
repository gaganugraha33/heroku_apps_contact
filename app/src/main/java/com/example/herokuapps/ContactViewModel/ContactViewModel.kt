package com.example.herokuapps.ContactViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.herokuapps.api.ContactRepository
import com.example.herokuapps.model.contactmodel.Datum
import com.example.herokuapps.model.contactmodelbyid.Data
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class ContactViewModel(
    private val compositeDisposable: CompositeDisposable,
    private val repository: ContactRepository,
    private val backgroundScheduler: Scheduler,
    private val mainScheduler: Scheduler
) : ViewModel() {
    private var listContact = MutableLiveData<MutableList<Datum>>()
    private var contactData = MutableLiveData<Data>()
    private var messageAddContact = MutableLiveData<String>()

    fun setListContact() {
        compositeDisposable.add(
            repository.getContact()
                .observeOn(backgroundScheduler)
                .subscribeOn(mainScheduler)
                .subscribe({ ContactViewModel ->
                    listContact.postValue(ContactViewModel.data as java.util.ArrayList<Datum>?)
                }, { error ->
                    println("error message " + error.message)
                    listContact.postValue(null)
                }
                )
        )
    }

    fun setListContactById(id: String) {
        compositeDisposable.add(
            repository.getContactById(id)
                .observeOn(backgroundScheduler)
                .subscribeOn(mainScheduler)
                .subscribe({
                    contactData.postValue(it.data)
                }, { error ->
                    contactData.postValue(null)
                    println("error message " + error.message)
                }
                )
        )
    }

    fun addContact(dataContact: Datum) {
        compositeDisposable.add(
            repository.addContact(dataContact)
                .observeOn(backgroundScheduler)
                .subscribeOn(mainScheduler)
                .subscribe({
                    messageAddContact.postValue(it.message)
                }, { error ->
                    messageAddContact.postValue(null)
                    println("error message " + error.message)
                }
                )
        )
    }

    fun editContact(dataContact: Datum, id:String) {
        compositeDisposable.add(
            repository.editContact(dataContact, id)
                .observeOn(backgroundScheduler)
                .subscribeOn(mainScheduler)
                .subscribe({
                    messageAddContact.postValue(it.message)
                }, { error ->
                    messageAddContact.postValue(null)
                    println("error message " + error.message)
                }
                )
        )
    }

    fun deleteContact(id:String) {
        compositeDisposable.add(
            repository.deleteContact(id)
                .observeOn(backgroundScheduler)
                .subscribeOn(mainScheduler)
                .subscribe({
                    messageAddContact.postValue(it.message)
                }, { error ->
                    messageAddContact.postValue(null)
                    println("error message " + error.message)
                }
                )
        )
    }

    fun getListContact(): LiveData<MutableList<Datum>> {
        return listContact
    }

    fun getContactById(): LiveData<Data> {
        return contactData
    }

    fun getContactMessage(): LiveData<String> {
        return messageAddContact
    }
}

class ViewModelMovieFactory(
    private val compositeDisposable: CompositeDisposable,
    private val repository: ContactRepository,
    private val backgroundScheduler: Scheduler,
    private val mainScheduler: Scheduler
) : ViewModelProvider.NewInstanceFactory() {
    @SuppressWarnings("unchecked")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ContactViewModel(
            compositeDisposable,
            repository,
            backgroundScheduler,
            mainScheduler
        ) as T
    }

}