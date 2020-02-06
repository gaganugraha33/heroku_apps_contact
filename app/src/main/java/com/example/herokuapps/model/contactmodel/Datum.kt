package com.example.herokuapps.model.contactmodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Datum : Serializable {
    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("firstName")
    @Expose
    var firstName: String? = null
    @SerializedName("lastName")
    @Expose
    var lastName: String? = null
    @SerializedName("age")
    @Expose
    var age: Int? = null
    @SerializedName("photo")
    @Expose
    var photo: String? = null

}
