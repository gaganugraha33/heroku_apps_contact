package com.example.herokuapps.api

import com.example.herokuapps.model.contactmodel.ContactModel
import com.example.herokuapps.model.contactmodel.Datum
import com.example.herokuapps.model.contactmodelbyid.ContactModelById
import com.example.herokuapps.model.messagemodel.MessageModel
import io.reactivex.Observable

class ContactRepository(private val contactService: ContactService) {
    fun getContact():Observable<ContactModel>{
        return contactService.getContact()
    }

    fun getContactById(id:String):Observable<ContactModelById>{
        return contactService.getContactById(id)
    }

    fun addContact(datum: Datum):Observable<MessageModel>{
        return contactService.addContact(datum)
    }
}