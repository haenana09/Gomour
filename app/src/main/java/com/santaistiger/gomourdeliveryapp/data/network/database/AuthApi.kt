package com.santaistiger.gomourdeliveryapp.data.network.database

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object AuthApi {

    fun writeAuthDeliveryMan(email:String,password: String){
        Firebase.auth.createUserWithEmailAndPassword(email, password)
    }

    fun updateAuthPassword(password:String){
        Firebase.auth.currentUser.updatePassword(password)
    }

    fun deleteAuthDeliveryMan(){
        Firebase.auth.currentUser.delete()
    }


    fun readUid() = Firebase.auth.uid
}