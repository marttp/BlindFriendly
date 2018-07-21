package com.example.mart.blindfriendly

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.activity_read_text.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.cloud.text.FirebaseVisionCloudText
import com.google.firebase.ml.vision.cloud.text.FirebaseVisionCloudTextDetector
import com.google.firebase.ml.vision.text.FirebaseVisionText


class ReadTextActivity : AppCompatActivity() {

    var currentPath: String? = null
    val CAMERA_REQUEST_CODE = 0
    val GALLAERY_REQUEST_CODE = 1

    var options = FirebaseVisionCloudDetectorOptions.Builder()
            .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
            .setMaxResults(15)
            .build()


    lateinit var image: FirebaseVisionImage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_text)
        contentRecognition.movementMethod = ScrollingMovementMethod()
        openCamera.setOnClickListener {
            openCameraIntent()
        }
        openGallery.setOnClickListener {
            openGalleryIntent()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //get request code
        when(requestCode){
            CAMERA_REQUEST_CODE -> {
                if(resultCode == Activity.RESULT_OK) {
                    try {
                        //create file from current path. You will get current path from
                        //createImageSaveStorage()
                        val file = File(currentPath)
                        Log.i("File","$file")
                        val uri = Uri.fromFile(file)
                        Log.i("Uri","$uri")
                        imageView.setImageURI(uri)


                        image = FirebaseVisionImage.fromFilePath(this, uri)
//                        imageView.setImageBitmap(data.extras.get("data") as Bitmap)
//                        image = FirebaseVisionImage.fromBitmap(data.extras.get("data") as Bitmap)

                        val detector = FirebaseVision.getInstance().getVisionCloudTextDetector(options)
                        contentRecognition.text = null
                        val result = detector.detectInImage(image)
                            .addOnSuccessListener { texts ->
                                if(texts != null)
                                    contentRecognition.text = texts.text
                            }
                                .addOnFailureListener {
                                Toast.makeText(this,"Unsuccessful",Toast.LENGTH_SHORT).show()
                            }
                    } catch (e: IOException){
                        e.printStackTrace()
                    }
                }
            }
            GALLAERY_REQUEST_CODE -> {
                if(resultCode == Activity.RESULT_OK && data != null) {
                    try {
                        val uri = data.data
                        imageView.setImageURI(uri)

                        image = FirebaseVisionImage.fromFilePath(this, uri)

                        val detector = FirebaseVision.getInstance().getVisionCloudTextDetector(options)
                        contentRecognition.text = null
                        val result = detector.detectInImage(image)
                                .addOnSuccessListener { texts ->
                                    if(texts != null)
                                        contentRecognition.text = texts.text
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this,"Unsuccessful",Toast.LENGTH_SHORT).show()
                                }
                    } catch (e: IOException){
                        e.printStackTrace()
                    }
                }
            }
            else -> {
                Toast.makeText(this,"Unrecognizied request code",Toast.LENGTH_SHORT).show()
            }
        }

    }

    //Camera function
    //Check data if success will create image and save
    private fun openCameraIntent() {
        //create Intent for connect to camera device turn on camera
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(intent.resolveActivity(packageManager) != null){
            //create and initial as Nullable

            /////////////////////////////////////Test////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////
            var photoFile: File? = null
            try {
                //call createImageSaveStorage() and it will return file
                photoFile = createImageSaveStorage()
            } catch (e: IOException){
                e.printStackTrace()
            }

            // check orientation
            if(photoFile != null){
                //you must create a content provide matching the authority
                //get URI of file by find (photoFile)
                val photoUri = FileProvider.getUriForFile(this,
                        "com.example.mart.blindfriendly.fileprovider",photoFile)
                //Intent as OUTPUT by send URI of file
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                ////////////////////////////////////////////////////////////////////////////
                startActivityForResult(intent,CAMERA_REQUEST_CODE)
            }
        }
    }

    //This function is use for take photo and create file and save to directory of image
    private fun createImageSaveStorage(): File {
        //create time stamp form current date
        val timeStamp = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(Date())
        //Set file name not suffix (only name)
        val imageName = "Photo_$timeStamp"
        //find directory of images in your device
        var storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        //create image file
        var image = File.createTempFile(imageName,".jpg", storageDirectory)
        //set path of created image and store in current path (below name of Class)
        currentPath = image.absolutePath
        Log.i("Current path","$currentPath")
        //return File
        return image
        /*
         * throw it to openCameraIntent
         */
    }

    private fun openGalleryIntent(){
        val intent = Intent()
        //find all in image directory
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,"Select image"), GALLAERY_REQUEST_CODE)
    }


}
