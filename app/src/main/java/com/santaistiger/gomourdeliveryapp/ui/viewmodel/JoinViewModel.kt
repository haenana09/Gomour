package com.santaistiger.gomourdeliveryapp.ui.viewmodel
/**
 * Created by Jangeunhye
 */
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.santaistiger.gomourdeliveryapp.data.repository.Repository
import com.santaistiger.gomourdeliveryapp.data.repository.RepositoryImpl
import kotlinx.coroutines.launch


class JoinViewModel : ViewModel() {
    companion object {
        private const val TAG = "JoinViewModel"
    }

    private var repository: Repository = RepositoryImpl
    private var auth = Firebase.auth

    var email = String()
    var password = String()
    val joinInfo = MutableLiveData<AuthResult>()
    val isImageUploaded = MutableLiveData<Boolean?>()

    fun join() =
        viewModelScope.launch { joinInfo.value = repository.join(auth!!, email, password) }

    suspend fun duplicateCheck(): Boolean {
        return repository.checkJoinable(email)
    }

    fun uploadImage(file: Uri) {
        repository.uploadImage(file)
            .addOnSuccessListener {
                isImageUploaded.value = true
                Log.i(TAG, "upload task success, value = ${isImageUploaded.value}")
            }.addOnFailureListener {
                isImageUploaded.value = false
                Log.i(TAG, "upload task failure, value = ${isImageUploaded.value}")
            }
    }

    fun doneUploadImage() {
        isImageUploaded.value = null
    }
}