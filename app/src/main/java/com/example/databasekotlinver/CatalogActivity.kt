package com.example.databasekotlinver

import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.databasekotlinver.database.Pet
import com.example.databasekotlinver.database.PetDbHelper
import com.example.databasekotlinver.databinding.ActivityCatalogBinding

class CatalogActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCatalogBinding
    private lateinit var pets : ArrayList<Pet>
    private lateinit var adapter : RecyclerPet
    private lateinit var dbHelper : PetDbHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCatalogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.list.layoutManager = LinearLayoutManager(this)

        pets = ArrayList()
        dbHelper = PetDbHelper(this)
        adapter = RecyclerPet(this,this,pets)
        binding.list.adapter = adapter
        binding.fab.setOnClickListener {
            val intent : Intent = Intent(this,EditorsActivity::class.java)
            startActivityForResult(intent,3)
        }
        displayAllPets()
    }

    private fun displayAllPets(){
        val cursor : Cursor = dbHelper.readAllPets()
        pets.clear()
        if(cursor == null || cursor.count < 1){
            Toast.makeText(this,"No data to read!",Toast.LENGTH_SHORT).show()
            binding.emptyView.visibility = View.VISIBLE
        }else{
            if(cursor.moveToFirst()){
                do{
                    val petId : String = cursor.getString(cursor.getColumnIndexOrThrow(PetDbHelper._ID))
                    val petName : String = cursor.getString(cursor.getColumnIndexOrThrow(PetDbHelper.COLUMN_PET_NAME))
                    val petBreed : String = cursor.getString(cursor.getColumnIndexOrThrow(PetDbHelper.COLUMN_PET_BREED))
                    val petGender : String = cursor.getString(cursor.getColumnIndexOrThrow(PetDbHelper.COLUMN_PET_GENDER))
                    val petWeight : Int = cursor.getInt(cursor.getColumnIndexOrThrow(PetDbHelper.COLUMN_PET_WEIGHT))

                    val pet : Pet = Pet(petName, petBreed, petGender, petWeight)
                    pet.petId = petId
                    pets.add(pet)
                }while(cursor.moveToNext())
            }
            cursor.close()
            binding.emptyView.visibility = View.GONE
        }
        adapter.notifyDataSetChanged()
    }

    private fun insertDummyPet(){
        val pet : Pet = Pet("Toto","Terrier",(PetDbHelper.GENDER_MALE).toString(),6)
        dbHelper.addPet(pet)
        displayAllPets()
    }

    private fun deleteAllPets(){
        dbHelper.deleteAllPets()
        displayAllPets()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_catalog,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_insert_dummy_data){
            insertDummyPet()
            return true
        }else if(item.itemId == R.id.action_delete_all_entries){
            deleteAllPets()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if((requestCode == 2 || requestCode == 3) && resultCode == RESULT_OK)displayAllPets()
    }
}