package com.example.herokuapps.model.contactmodelbyid

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ContactModelById {

    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("data")
    @Expose
    var data: Data? = null

}
