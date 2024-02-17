package com.example.databasekotlinver.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.widget.Toast

class PetDbHelper(context : Context) : SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {

    private val context : Context = context.applicationContext

    companion object{
       private const val DATABASE_NAME = "shelter.db"
       private const val DATABASE_VERSION = 1

       const val TABLE_NAME : String = "pets"
       const val _ID : String = BaseColumns._ID
       const val COLUMN_PET_NAME : String = "name"
       const val COLUMN_PET_BREED : String = "breed"
       const val COLUMN_PET_GENDER : String = "gender"
       const val COLUMN_PET_WEIGHT : String = "weight"

       const val GENDER_UNKNOWN : Int = 0
       const val GENDER_MALE : Int = 1
       const val GENDER_FEMALE : Int = 2
    }





    override fun onCreate(db: SQLiteDatabase?) {
       val SQL_CREATE_PETS_TABLE : String = "CREATE TABLE $TABLE_NAME (" +
               "$_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
               "$COLUMN_PET_NAME TEXT NOT NULL, " +
               "$COLUMN_PET_BREED TEXT, " +
               "$COLUMN_PET_GENDER INTEGER NOT NULL, " +
               "$COLUMN_PET_WEIGHT INTEGER NOT NULL DEFAULT 0);"

        db?.execSQL(SQL_CREATE_PETS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {}

    fun addPet(pet : Pet){
        val db : SQLiteDatabase = this.writableDatabase
        val values : ContentValues = ContentValues().apply {
            put(COLUMN_PET_NAME,pet.petName)
            put(COLUMN_PET_BREED,pet.petBreed)
            put(COLUMN_PET_GENDER,pet.petGender)
            put(COLUMN_PET_WEIGHT,pet.petWeight)
        }
        val res : Long = db.insert(TABLE_NAME,null,values)
        if(res.toInt() == -1)Toast.makeText(context,"Failed to insert a pet",Toast.LENGTH_SHORT).show()
        else Toast.makeText(context,"Pet added successfully",Toast.LENGTH_SHORT).show()
    }
    fun readAllPets () : Cursor {
        val query : String = "SELECT * FROM $TABLE_NAME"
        val db : SQLiteDatabase = this.writableDatabase
        val cursor : Cursor = db.rawQuery(query,null)
        return cursor
    }


    fun updatePet(pet : Pet){
        val db : SQLiteDatabase = this.writableDatabase
        val values : ContentValues = ContentValues().apply {
             put(COLUMN_PET_NAME,pet.petName)
             put(COLUMN_PET_BREED,pet.petBreed)
             put(COLUMN_PET_GENDER,pet.petGender)
             put(COLUMN_PET_WEIGHT,pet.petWeight)
        }
        val res : Int = db.update(TABLE_NAME,values,"$_ID=?", arrayOf(pet.petId))
        if(res == -1) Toast.makeText(context,"Failed to update a pet",Toast.LENGTH_SHORT).show()
        else Toast.makeText(context,"Successfully update a pet",Toast.LENGTH_SHORT).show()
    }


    fun deletePet(id : String){
        val db : SQLiteDatabase = this.writableDatabase
        val res : Int = db.delete(TABLE_NAME, "$_ID=?", arrayOf(id))
        if(res == -1)Toast.makeText(context,"Failed to delete a pet.",Toast.LENGTH_SHORT).show()
        else Toast.makeText(context,"Successfully delete a pet.",Toast.LENGTH_SHORT).show()
    }
    fun deleteAllPets(){
        val db : SQLiteDatabase = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_NAME")
    }


}