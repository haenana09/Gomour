package com.santaistiger.gomourdeliveryapp.ui.login

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.santaistiger.gomourdeliveryapp.R
import com.santaistiger.gomourdeliveryapp.data.model.DeliveryMan
import com.santaistiger.gomourdeliveryapp.databinding.FragmentLoginBinding
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.fragment_join.*


class LoginFragment: Fragment(){

    private var auth: FirebaseAuth? = null
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel





    //  create a textWatcher member
    private val mTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
        override fun afterTextChanged(editable: Editable) {
            // check Fields For Empty Values
            checkFieldsForEmptyValues()
        }
    }

    fun checkFieldsForEmptyValues() {
        val loginButton = binding.loginButton
        val email = binding.emailLogin.text.toString()
        val password: String = binding.passwordLogin.text.toString()
        if (email == "" || password == "") {
            loginButton.isEnabled = false
        } else {
            loginButton.isEnabled = true
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setToolbar()

        auth = Firebase.auth
        binding = DataBindingUtil.inflate<FragmentLoginBinding>(inflater,
            R.layout.fragment_login,container,false)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)



        binding.emailLogin.addTextChangedListener(mTextWatcher)
        binding.passwordLogin.addTextChangedListener(mTextWatcher)

        // run once to disable if empty
        checkFieldsForEmptyValues();

        binding.loginButton.setOnClickListener{
            val id = binding.emailLogin.text.toString()
            val email = id + "@dankook.ac.kr"
            val password = binding.passwordLogin.text.toString()
            signIn(email,password)

        }


        binding.goSignUpPage.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_joinFragment)
        }










        return binding.root
    }

    private fun setToolbar() {
        requireActivity().apply {
            toolbar.visibility = View.GONE  // 툴바 숨기기
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)   // 스와이프 비활성화
        }
    }

    private fun signIn(email:String, password:String){
        if (email.contains("@dankook.ac.kr")){

            auth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(){ task ->
                    if(task.isSuccessful){


                        //파이어스토어에서 인증상태 확인
                        val user = auth!!.currentUser
                        val db = Firebase.firestore

                        val docRef = db.collection("deliveryMan").document(user.uid)
                        docRef.get().addOnSuccessListener { documentSnapshot ->
                            val data = documentSnapshot.toObject<DeliveryMan>()
                            if (data != null) {
                                if(data.isCertified == true){

                                    db.collection("deliveryMan")
                                    val auto = this.requireActivity()
                                        .getSharedPreferences("auto", Context.MODE_PRIVATE)
                                    val autoLogin = auto.edit()
                                    autoLogin.putString("email",email)
                                    autoLogin.putString("password",password)
                                    autoLogin.commit()
                                    Toast.makeText(context,"commit 완료", Toast.LENGTH_LONG).show()

                                    findNavController().navigate(R.id.action_loginFragment_to_orderListFragment)
                                }
                                else{
                                    Toast.makeText(context,"아직 학생증 인증이 완료되지 않았습니다",Toast.LENGTH_LONG).show()
                                }
                            }
                        }


                    }
                }
                ?.addOnFailureListener {
                    alertCancel()
                }

        }

        else{
            Toast.makeText(context,"학교이메일만 사용가능합니다.",Toast.LENGTH_LONG).show()
        }


    }

    private fun hideKeyboard() {


    }



    fun alertCancel() {
        AlertDialog.Builder(requireActivity())
            .setMessage("로그인에 실패하였습니다.")
            .setPositiveButton("확인",null)
            .create()
            .show()
    }




}