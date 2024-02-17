package com.example.databasekotlinver

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.databasekotlinver.database.Pet
import com.example.databasekotlinver.database.PetDbHelper

class RecyclerPet(private val activity : Activity,private val context : Context,
                  private val pets : ArrayList<Pet>) : RecyclerView.Adapter<RecyclerPet.PetHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.listitem,parent,false)
        return PetHolder(view)
    }

    override fun getItemCount(): Int {
        return pets.size
    }

    override fun onBindViewHolder(holder: PetHolder, position: Int) {
        val pet : Pet = pets[position]
        holder.petName.text = pet.petName
        holder.petBreed.text = pet.petBreed
        holder.item.setOnClickListener {
            val intent : Intent = Intent(it.context,EditorsActivity::class.java).apply {
               putExtra(PetDbHelper._ID,pet.petId)
                putExtra(PetDbHelper.COLUMN_PET_NAME,pet.petName)
                putExtra(PetDbHelper.COLUMN_PET_BREED,pet.petBreed)
                putExtra(PetDbHelper.COLUMN_PET_GENDER,pet.petGender)
                putExtra(PetDbHelper.COLUMN_PET_WEIGHT,pet.petWeight)
            }
            activity.startActivityForResult(intent,2);
        }
    }


    class PetHolder (itemView : View): RecyclerView.ViewHolder(itemView) {
        val petName : TextView  = itemView.findViewById(R.id.name)
        val petBreed : TextView = itemView.findViewById(R.id.breed)
        val item : CardView = itemView.findViewById(R.id.item)
    }
}