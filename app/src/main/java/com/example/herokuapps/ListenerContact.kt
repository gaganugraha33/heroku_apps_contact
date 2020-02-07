package com.example.herokuapps

import com.example.herokuapps.model.contactmodel.Datum

interface ListenerContact {
    fun listenerEdit(dataContact: Datum)
    fun listenerDelete(id: String)
}