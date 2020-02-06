package com.example.herokuapps.model.messagemodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MessageModel {

    @SerializedName("message")
    @Expose
    var message: String? = null

}
