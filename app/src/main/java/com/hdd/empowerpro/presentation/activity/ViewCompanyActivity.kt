package com.hdd.empowerpro.presentation.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.hdd.empowerpro.R
import com.hdd.empowerpro.data.models.Company
import com.hdd.empowerpro.data.remoteDataSource.ServiceBuilder
import com.hdd.empowerpro.repository.CompanyRepository
import com.hdd.empowerpro.utils.ExtensionFunction.hide
import com.hdd.empowerpro.utils.ExtensionFunction.show
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ViewCompanyActivity : AppCompatActivity() {

    private var isUserWantToUploadCoverImage: Boolean=false
    private var isUserWantToUploadImage: Boolean=false
    private var isEditModeEnabled: Boolean=false
    private var isOpeningTimeSelected: Boolean=true

    // for add company details
    private lateinit var addCompanyName: EditText
    private lateinit var addCompanyDescription: EditText
    private lateinit var addPhone: EditText
    private lateinit var addAddress: EditText
    private lateinit var addOpeningTime: EditText
    private lateinit var addClosingTime: EditText
    private lateinit var companyImage: ImageView
    private lateinit var companyCoverImage: ImageView
    private lateinit var map: ImageView
    private lateinit var avr_choose_cover_image: TextView

    // for add company details
    private lateinit var viewCompanyName: TextView
    private lateinit var viewCompanyDescription: TextView
    private lateinit var viewPhone: TextView
    private lateinit var viewAddress: TextView
    private lateinit var viewOpeningTime: TextView
    private lateinit var viewClosingTime: TextView

    // for button container layout
    private lateinit var buttonContainerLayout: LinearLayout
    private lateinit var addAdd: Button
    private lateinit var btnCancel: Button

    private lateinit var iv_add_img: ImageView
    private lateinit var avr_edit_icon: ImageView
    private lateinit var avr_delete_icon: ImageView

    // for container layout
    private lateinit var viewCompanyDetailsLayout: LinearLayout
    private lateinit var addCompanyDetailsLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_company)

        //Extract the data…
        val bundle = intent.extras
        val requiredCompanyId = ServiceBuilder.user!!.company!!._id
        getCompanyDetailsById(requiredCompanyId)

        // for images layout
        companyImage = findViewById(R.id.avr_company_image)
        companyCoverImage = findViewById(R.id.avr_add_company_cover_image)
        iv_add_img = findViewById(R.id.avr_add_company_image)
        avr_edit_icon = findViewById(R.id.avr_edit_icon)
        avr_delete_icon = findViewById(R.id.avr_delete_icon)
//        avr_choose_cover_image = findViewById(R.id.avr_choose_cover_image)


        // for binding add company details
        addCompanyName = findViewById(R.id.avr_add_company_name)
        addCompanyDescription = findViewById(R.id.avr_add_company_description)
        addPhone = findViewById(R.id.avr_add_company_phone_number)
        addAddress = findViewById(R.id.avr_add_company_address)
        addOpeningTime = findViewById(R.id.avr_add_company_opening_time_schedule)
        addClosingTime = findViewById(R.id.avr_add_company_closing_time_schedule)
        buttonContainerLayout = findViewById(R.id.avr_buttonContainerLayout)
        map = findViewById(R.id.avr_map_address)


        // for binding view company details
        viewCompanyName = findViewById(R.id.avr_view_company_name)
        viewCompanyDescription = findViewById(R.id.avr_view_company_description)
        viewPhone = findViewById(R.id.avr_view_company_phone_number)
        viewAddress = findViewById(R.id.avr_view_company_address)
        viewOpeningTime = findViewById(R.id.avr_view_company_opening_time_schedule)
        viewClosingTime = findViewById(R.id.avr_view_company_closing_time_schedule)

        // for container layout
        viewCompanyDetailsLayout = findViewById(R.id.avr_view_company_details_layout)
        addCompanyDetailsLayout = findViewById(R.id.avr_add_company_details_layout)

        // for bottom container layout
        addAdd = findViewById(R.id.avr_btnAddCompany)
        btnCancel = findViewById(R.id.avr_btnCancel)

        val hourList = mutableListOf<String>()

        val minuteList = mutableListOf<String>()

        val categoryList = mutableListOf<String>("AM", "PM")

        generateList(hourList,minuteList)

        avr_edit_icon.setOnClickListener {
            isEditModeEnabled=true
            setEditMode()
        }

        avr_delete_icon.setOnClickListener {
//            showDeleteAlertDialog(requiredCompanyId)
        }

        iv_add_img.setOnClickListener {
            if (isEditModeEnabled){
                isUserWantToUploadImage=true
                loadPopUpMenu(iv_add_img)
            }
        }

        btnCancel.setOnClickListener {
            isEditModeEnabled=false
            setEditMode()
        }

        addOpeningTime.setOnClickListener {
            isOpeningTimeSelected=true
            openTimePickerDialog(hourList,minuteList, categoryList)
        }

        addClosingTime.setOnClickListener {
            isOpeningTimeSelected=false
            openTimePickerDialog(hourList,minuteList, categoryList)
        }

        companyCoverImage.setOnClickListener {
            if (isEditModeEnabled){
                isUserWantToUploadCoverImage=true
                loadPopUpMenu(companyCoverImage)
            }
        }

        map.setOnClickListener {
            val intent = Intent(this,AddressPickerActivity::class.java)
            intent.putExtra(AddressPickerActivity.ARG_LAT_LNG,MyLatLng(27.678616119670615, 84.4358566124604))
            val pinList=ArrayList<Pin>()
            pinList.add(Pin(MyLatLng(27.678579704941686, 84.42964182567329),"Bharatpur Airport"))
            intent.putExtra(AddressPickerActivity.ARG_LIST_PIN,  pinList)
            intent.putExtra(AddressPickerActivity.ARG_ZOOM_LEVEL,  18.0f)
            startActivityForResult(intent, AddCompanyActivity.REQUEST_ADDRESS)
        }

        addAdd.setOnClickListener {
            updateCompanyDetails(requiredCompanyId)
            isEditModeEnabled=false
            setEditMode()
        }

    }

    private fun getCompanyDetailsById(companyId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val companyRepository= CompanyRepository()
                val response=companyRepository.getCompanyById(companyId)
                if(response.success==true){
                    val company = response.data!!
                    withContext(Dispatchers.Main){
                        Glide.with(this@ViewCompanyActivity).load(company.image).into(companyImage)
                        if (company.coverImage!=""){
                            Glide.with(this@ViewCompanyActivity).load(company.coverImage).into(companyCoverImage)
                        }
                        viewCompanyName.text = company.name
                        viewCompanyDescription.text = company.description
                        viewPhone.text = company.phone
                        viewAddress.text = company.address
                        viewOpeningTime.text = company.openingTime
                        viewClosingTime.text = company.closingTime

                        addCompanyName.setText(company.name)
                        addCompanyDescription.setText(company.description)
                        addPhone.setText(company.phone)
                        addAddress.setText(company.address)
                        addOpeningTime.setText(company.openingTime)
                        addClosingTime.setText(company.closingTime)

                    }
                }
            }catch (ex:Exception){
                print(ex)
            }
        }
    }

    private fun updateCompanyDetails(companyId: String) {
        val company = Company(
            name=addCompanyName.text.toString(),
            description  =addCompanyDescription.text.toString(),
            phone=addPhone.text.toString(),
            openingTime = addOpeningTime.text.toString(),
            closingTime = addClosingTime.text.toString(),
            address = addAddress.text.toString(),
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val companyRepository = CompanyRepository()
                val response = companyRepository.updateCompanyDetails(companyId,company)
                if (response.success == true) {
                    withContext(Dispatchers.Main) {
                        viewCompanyName.text=addCompanyName.text.toString()
                        viewCompanyDescription.text=addCompanyDescription.text.toString()
                        viewPhone.text=addPhone.text.toString()
                        viewAddress.text=addAddress.text.toString()
                        viewOpeningTime.text=addOpeningTime.text.toString()
                        viewClosingTime.text=addClosingTime.text.toString()
                        Toast.makeText(this@ViewCompanyActivity, "Company Details Updated", Toast.LENGTH_SHORT).show()
                    }
                }
                if (companyImageUrl != null) {
                    updateCompanyImage(companyId)
                    super.onBackPressed()
                }
                if (companyCoverImageUrl != null) {
                    updateCompanyCoverImage(companyId)
                    super.onBackPressed()
                }

            } catch (ex: Exception) {
                print(ex)
            }
        }
    }

    private fun updateCompanyImage(companyId:String) {
        if (companyImageUrl != null) {
            val file = File(companyImageUrl!!)
            val mimeType = getMimeType(file.path);
            val reqFile = RequestBody.create(mimeType!!.toMediaTypeOrNull(), file)
            val body = MultipartBody.Part.createFormData("image", file.name, reqFile)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val companyRepository = CompanyRepository()
                    companyRepository.updateCompanyImage(companyId,body)
                    getCompanyDetailsById(companyId)
                } catch (ex: Exception) {
                    withContext(Dispatchers.Main) {
                        print(ex)
                    }
                }
            }

        }
    }

    private fun updateCompanyCoverImage(companyId:String) {
        if (companyCoverImageUrl != null) {
            val file = File(companyCoverImageUrl!!)
            val mimeType = getMimeType(file.path);
            val reqFile = RequestBody.create(mimeType!!.toMediaTypeOrNull(), file)
            val body = MultipartBody.Part.createFormData("image", file.name, reqFile)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val companyRepository = CompanyRepository()
                    companyRepository.updateCompanyCoverImage(companyId,body)
                    getCompanyDetailsById(companyId)
                } catch (ex: Exception) {
                    withContext(Dispatchers.Main) {
                        print(ex)
                    }
                }
            }

        }
    }

    private fun setEditMode() {
        if (isEditModeEnabled){
            // list of things for their visibility ON
            iv_add_img.show()
            addCompanyName.show()
            addCompanyDescription.show()
            addCompanyDetailsLayout.show()
            buttonContainerLayout.show()

            // list of things for their visibility OFF
            viewCompanyName.hide()
            viewCompanyDescription.hide()
            viewCompanyDetailsLayout.hide()
        } else {
            // list of things for their visibility ON
            viewCompanyName.show()
            viewCompanyDescription.show()
            viewCompanyDetailsLayout.show()

            // list of things for their visibility OFF
            iv_add_img.hide()
            addCompanyName.hide()
            addCompanyDescription.hide()
            addCompanyDetailsLayout.hide()
            buttonContainerLayout.hide()
        }
    }

    private fun generateList(hourList: MutableList<String>, minuteList: MutableList<String>) {
        for (hour in 1..12){
            if(hour.toString().count()==1) {
                hourList.add("0$hour")
            }
            if (hour.toString().count()==1){
                continue
            }
            hourList.add(hour.toString())
        }

        for (minute in 1..61){
            if(minute%5==0) {
                if(minute.toString() == "5") {
                    minuteList.add("0$minute")
                }
                if (minute==5){
                    continue
                }
                minuteList.add(minute.toString())
            }
        }
    }

    private fun openTimePickerDialog(hourList:MutableList<String>,minuteList:MutableList<String>,categoryList:MutableList<String>) {
        val timeDialog = Dialog(this)
        timeDialog.setContentView(R.layout.time_picker_dialog)
        timeDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val hourPicker:NumberPicker = timeDialog.findViewById(R.id.hourPicker)
        val minutePicker:NumberPicker = timeDialog.findViewById(R.id.minutePicker)
        val categoryPicker:NumberPicker = timeDialog.findViewById(R.id.categoryPicker)
        val openingTime: TextView = timeDialog.findViewById(R.id.tpd_opening_time)
        val closingTime: TextView = timeDialog.findViewById(R.id.tpd_closing_time)
        val cancel: Button = timeDialog.findViewById(R.id.tpd_cancel)
        val btnOk: Button = timeDialog.findViewById(R.id.tpd_ok)

        hourPicker.minValue = 0
        hourPicker.maxValue = hourList.size - 1
        hourPicker.displayedValues = hourList.toTypedArray()
        hourPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        minutePicker.minValue = 0
        minutePicker.maxValue = minuteList.size - 1
        minutePicker.displayedValues = minuteList.toTypedArray()
        minutePicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        categoryPicker.minValue = 0
        categoryPicker.maxValue = categoryList.size - 1
        categoryPicker.displayedValues = categoryList.toTypedArray()
        categoryPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        // set value
        openingTime.text=addOpeningTime.text
        closingTime.text=addClosingTime.text

        cancel.setOnClickListener {
            timeDialog.dismiss()
        }

        btnOk.setOnClickListener {
            if (isOpeningTimeSelected){
                val selectedOpeningTime = convertToTime(hourList[hourPicker.value],minuteList[minutePicker.value],categoryList[categoryPicker.value])
                openingTime.text = selectedOpeningTime
                addOpeningTime.setText(selectedOpeningTime)
            } else {
                val selectedClosingTime = convertToTime(hourList[hourPicker.value],minuteList[minutePicker.value],categoryList[categoryPicker.value])
                closingTime.text = selectedClosingTime
                addClosingTime.setText(selectedClosingTime)
            }
            timeDialog.dismiss()
        }
        timeDialog.show()
    }

    private fun convertToTime(hour: String, minute: String, category: String): String {
        return "$hour:$minute $category"
    }

    // Load pop up menu
    private fun loadPopUpMenu(where: View) {
        val popupMenu = PopupMenu(this, where)
        popupMenu.menuInflater.inflate(R.menu.gallery_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuCamera -> openCamera()
                R.id.menuGallery -> openGallery()
            }
            true
        }
        popupMenu.show()
    }

    private var REQUEST_GALLERY_CODE = 0
    private var REQUEST_CAMERA_CODE = 1
    private var companyImageUrl: String? = null
    private var companyCoverImageUrl: String? = null

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY_CODE)
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_CAMERA_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AddCompanyActivity.REQUEST_ADDRESS && resultCode == Activity.RESULT_OK) {
            val address: Address = data?.getParcelableExtra<Address>(AddressPickerActivity.RESULT_ADDRESS)!!
            val cityName: String = address.locality
            val featureName: String = address.featureName
            val subAdminArea: String = address.subAdminArea
            val countryName: String = address.countryName
            addAddress.setText("$featureName, $cityName, $subAdminArea, $countryName")
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GALLERY_CODE && data != null) {
                val selectedImage: Uri? = data.data
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                val contentResolver = contentResolver
                val cursor = contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
                cursor!!.moveToFirst()
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                if (isUserWantToUploadCoverImage){
                    companyCoverImageUrl = cursor.getString(columnIndex)
                    companyCoverImage.setImageBitmap(BitmapFactory.decodeFile(companyCoverImageUrl))
                    isUserWantToUploadCoverImage=false
                }
                if(isUserWantToUploadImage){
                    companyImageUrl = cursor.getString(columnIndex)
                    companyImage.setImageBitmap(BitmapFactory.decodeFile(companyImageUrl))
                    isUserWantToUploadImage=false
                }
                cursor.close()

            }
            else if (requestCode == REQUEST_CAMERA_CODE && data != null) {
                val imageBitmap = data.extras?.get("data") as Bitmap
                val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val file = bitmapToFile(imageBitmap, "$timeStamp.jpg")
                if (isUserWantToUploadCoverImage){
                    companyCoverImageUrl = file!!.absolutePath
                    companyCoverImage.setImageBitmap(BitmapFactory.decodeFile(companyCoverImageUrl))
                    isUserWantToUploadCoverImage=false
                }
                if(isUserWantToUploadImage){
                    companyImageUrl = file!!.absolutePath
                    companyImage.setImageBitmap(BitmapFactory.decodeFile(companyImageUrl))
                    isUserWantToUploadImage=false
                }
            }
        }
    }

    private fun getMimeType(url: String?): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    private fun bitmapToFile( bitmap: Bitmap, fileNameToSave: String ): File? {
        var file: File? = null
        return try {
            file = File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    .toString() + File.separator + fileNameToSave
            )
            file.createNewFile()
            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos) // YOU can also save it in JPEG
            val bitMapData = bos.toByteArray()
            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitMapData)
            fos.flush()
            fos.close()
            file

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            file // it will return null
        }
    }

}