package com.santaistiger.gomourdeliveryapp.data.network.database

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

object StorageApi {
    private val TAG = "StorageApiService"
    private val storage = Firebase.storage
    private val storageRef =
        storage.getReferenceFromUrl("gs://gomour-495e0.appspot.com/") //storage 서버로 이동

    fun uploadImage(uid: String, file: Uri) =
        storageRef.child("images/$uid").putFile(file)

}