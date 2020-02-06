package com.example.herokuapps.api

import com.example.herokuapps.BuildConfig
import com.example.herokuapps.model.contactmodel.ContactModel
import com.example.herokuapps.model.contactmodel.Datum
import com.example.herokuapps.model.contactmodelbyid.ContactModelById
import com.example.herokuapps.model.messagemodel.MessageModel
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ContactService {

    @GET("/contact")
    fun getContact(): Observable<ContactModel>

    @GET("/contact/{id}")
    fun getContactById(@Path("id") contact_id: String): Observable<ContactModelById>

    @POST("/contact")
    fun addContact(@Body datum:Datum): Observable<MessageModel>

    @PUT("/contact/{id}")
    fun editContact(@Body datum:Datum, @Path("id") contact_id: String): Observable<ContactModelById>

    @DELETE("/contact/{id}")
    fun deleteContact(@Path("id") contact_id: String): Observable<MessageModel>


    companion object Factory {
        fun create(): ContactService {

            val logging = HttpLoggingInterceptor()
            // set your desired log level
            logging.apply { logging.level = HttpLoggingInterceptor.Level.BODY }

            val httpClient = OkHttpClient.Builder()
            // add your other interceptors …

            // add logging as last interceptor
            httpClient.addInterceptor(logging)

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.BASEURL)
                .client(httpClient.build())
                .build()
            return retrofit.create(ContactService::class.java)
        }
    }

}