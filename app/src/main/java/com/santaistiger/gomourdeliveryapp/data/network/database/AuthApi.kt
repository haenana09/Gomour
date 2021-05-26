package com.santaistiger.gomourdeliveryapp.data.network.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object AuthApi {
    // 로그인
    suspend fun login(firebaseAuth: FirebaseAuth, email: String, password: String): AuthResponse {
        val authResponse = AuthResponse()

        try {
            authResponse.authResult = firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()
        } catch (e: Exception) {
            authResponse.exception = e
        }

        return authResponse
    }

    //회원가입
    suspend fun join(firebaseAuth:FirebaseAuth, email:String, password:String):AuthResponse{
        val authResponse = AuthResponse()
        try{
            authResponse.authResult = firebaseAuth
                .createUserWithEmailAndPassword(email,password)
                .await()
        } catch (e:java.lang.Exception){
            authResponse.exception = e
        }
        return authResponse
    }
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