package com.example.databasekotlinver

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NavUtils
import com.example.databasekotlinver.database.Pet
import com.example.databasekotlinver.database.PetDbHelper
import com.example.databasekotlinver.databinding.ActivityEditorBinding
import kotlinx.coroutines.awaitAll

class EditorsActivity : AppCompatActivity() {

    private  lateinit var binding : ActivityEditorBinding
    private  var currentPet : Pet? = null
    private var newGender: Int? = null
    private  lateinit var dbHelper: PetDbHelper
    private var mPetHasChanged : Boolean = false

    private val mTouch : View.OnTouchListener = View.OnTouchListener { view, motionEvent ->
        mPetHasChanged = true
        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = PetDbHelper(this)

        val extras : Bundle? = intent.extras
        if(extras == null){
            setTitle("Add a Pet")
            currentPet = null
            newGender = PetDbHelper.GENDER_UNKNOWN
        }else{
            setTitle("Edit a Pet")
            val petId : String? = extras.getString(PetDbHelper._ID)
            val petName : String? = extras.getString(PetDbHelper.COLUMN_PET_NAME)
            val petBreed : String? = extras.getString(PetDbHelper.COLUMN_PET_BREED)
            val petGender : String? = extras.getString(PetDbHelper.COLUMN_PET_GENDER)
            val petWeight : Int = extras.getInt(PetDbHelper.COLUMN_PET_WEIGHT)

            currentPet = Pet(petName ?:"",petBreed?:"",petGender?:"",petWeight)
            currentPet!!.petId = petId
        }

        binding.editPetName.setOnTouchListener(mTouch)
        binding.editPetBreed.setOnTouchListener(mTouch)
        binding.editPetWeight.setOnTouchListener(mTouch)
        binding.spinnerGender.setOnTouchListener(mTouch)

        setupSpinner()

        if(currentPet != null){
            val num : Int = (currentPet!!.petGender).toInt()
            binding.spinnerGender.setSelection(num)
            binding.editPetName.setText(currentPet!!.petName)
            binding.editPetWeight.setText((currentPet!!.petWeight).toString())
            binding.editPetBreed.setText(currentPet!!.petBreed)
        }
    }

    private fun deletePet(){
        if(currentPet != null){
            dbHelper.deletePet(currentPet!!.petId?:"")
            setResult(RESULT_OK)
        }
        finish()
    }
    private fun savePet(){
         val nameString : String = binding.editPetName.text.toString().trim()
         val breedString : String = binding.editPetBreed.text.toString().trim()
         val weightString : String = binding.editPetWeight.text.toString().trim()

         if(TextUtils.isEmpty(nameString) && TextUtils.isEmpty(breedString) && TextUtils.isEmpty(weightString)
             && newGender == PetDbHelper.GENDER_UNKNOWN){
             Toast.makeText(this,"Some Columns are empty",Toast.LENGTH_SHORT).show()
             return
         }

         val pet : Pet = Pet(nameString,breedString,newGender.toString(),weightString.toInt())
         if(currentPet == null){
             dbHelper.addPet(pet)
         }else{
             pet.petId = currentPet!!.petId
             dbHelper.updatePet(pet)
         }
         setResult(RESULT_OK)
         finish()
    }

    private fun setupSpinner() {
         val genderSpinnerAdapter  = ArrayAdapter.createFromResource(this,R.array.array_gender_options,android.R.layout.simple_spinner_item)
         genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
         binding.spinnerGender.adapter = genderSpinnerAdapter
         binding.spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
             override fun onItemSelected(adapterView : AdapterView<*>?, view : View?, i : Int, l : Long) {
                  val selection : String = adapterView?.getItemAtPosition(i) as String
                  if(!selection.isNullOrEmpty()){
                      when(selection){
                          getString(R.string.gender_male) -> newGender = PetDbHelper.GENDER_MALE
                          getString(R.string.gender_female) -> newGender = PetDbHelper.GENDER_FEMALE
                          else -> newGender = PetDbHelper.GENDER_UNKNOWN
                      }
                  }
             }

             override fun onNothingSelected(adapterView : AdapterView<*>?) {
                 newGender = PetDbHelper.GENDER_UNKNOWN
             }

         }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_editor,menu)
        val item = menu.findItem(R.id.action_delete)
        if(currentPet == null)item?.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_save){
            savePet()
            finish()
        }else if(item.itemId == R.id.action_delete){
            showDeleteConfirmationDialog();
        }else if(item.itemId == android.R.id.home){
            if(!mPetHasChanged){
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
            val discardButtonClickListener = DialogInterface.OnClickListener { dialogInterface, i ->
                NavUtils.navigateUpFromSameTask(this)
            }
            showUnsavedChangesDialog(discardButtonClickListener)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDeleteConfirmationDialog() {
        val builder : AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.delete_dialog_msg)
        builder.setPositiveButton(R.string.delete) { _, _ -> deletePet() }
        builder.setNegativeButton(R.string.cancel) {dialog,_ ->
            dialog?.dismiss()
        }
        val alertDialog : AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun showUnsavedChangesDialog(discardButtonClickListener: DialogInterface.OnClickListener) {
        val builder : AlertDialog.Builder  =  AlertDialog.Builder(this)
        builder.setMessage(R.string.unsaved_changes_dialog_msg)
        builder.setPositiveButton(R.string.discard,discardButtonClickListener)
        builder.setNegativeButton(R.string.keep_editing) { dialogInterface, _ ->
            dialogInterface?.dismiss()
        }
        val alertDialog : AlertDialog = builder.create()
        alertDialog.show()
    }

    override fun onBackPressed() {
        if(!mPetHasChanged){
            super.onBackPressed()
            return
        }
        val discardButtonClickListener : DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialogInterface, i ->
            finish()
        }
        showUnsavedChangesDialog(discardButtonClickListener)

    }



}