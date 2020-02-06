package com.example.herokuapps.api

object ContactProvider {

   fun contactProviderRepository():ContactRepository{
       return  ContactRepository(ContactService.create())
   }

}