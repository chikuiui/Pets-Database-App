package com.example.databasekotlinver.database

class Pet (val petName : String,val petBreed : String ,val petGender : String,
           val petWeight : Int){

    var petId : String? = null
        get() = field
        set(value) {
            field = value
        }
}