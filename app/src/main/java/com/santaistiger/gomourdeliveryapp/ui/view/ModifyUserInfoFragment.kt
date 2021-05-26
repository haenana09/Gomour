package com.santaistiger.gomourdeliveryapp.ui.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.santaistiger.gomourdeliveryapp.data.model.Customer
import com.santaistiger.gomourdeliveryapp.data.model.DeliveryMan
import com.santaistiger.gomourdeliveryapp.databinding.FragmentModifyUserInfoBinding
import com.santaistiger.gomourdeliveryapp.ui.base.BaseActivity
import com.santaistiger.gomourdeliveryapp.ui.viewmodel.ModifyUserInfoViewModel
import kotlinx.android.synthetic.main.activity_base.*
import java.util.regex.Pattern

class ModifyUserInfoFragment: Fragment() {

    private var auth: FirebaseAuth? = null
    private lateinit var binding: FragmentModifyUserInfoBinding
    private lateinit var viewModel: ModifyUserInfoViewModel
    val db = Firebase.firestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        setToolbar()

        auth = Firebase.auth
        binding = DataBindingUtil.inflate<FragmentModifyUserInfoBinding>(inflater,
            R.layout.fragment_modify_user_info,container,false)
        viewModel = ViewModelProvider(this).get(ModifyUserInfoViewModel::class.java)
        val currentUser = auth?.currentUser
        var password:String = ""
        var passwordCheck:String = ""
        if (currentUser != null) {
            val docRef = db.collection("deliveryMan").document(currentUser.uid)
            docRef.get().addOnSuccessListener { documentSnapshot ->
                val data = documentSnapshot.toObject<DeliveryMan>()
                if (data != null) {
                    password = data.password.toString()
                    passwordCheck = data.password.toString()

                    binding.nameModify.text = data.name
                    binding.emailModify.text = data.email
                    binding.phoneModify.setText(data.phone)
                    binding.bankModify.setText(data.accountInfo?.bank)
                    binding.accountModify.setText(data.accountInfo?.account)
                    binding.passwordModify.setText(data.password)
                    binding.passwordCheckModify.setText(data.password)
                }
            }

            // 비밀번호 입력창 누르면 사라져
            binding.passwordCheckModify.setOnFocusChangeListener { v, hasFocus ->
                if(hasFocus){
                    binding.passwordModify.text.clear()
                }
            }
            // 비밀번호 확인창 누르면 사라져
            binding.passwordCheckModify.setOnFocusChangeListener { v, hasFocus ->
                if(hasFocus){
                    binding.passwordCheckModify.text.clear()
                }
            }



            //비어 있지 않을 때 비밀번호 감지하여 판단
            binding.passwordCheckModify.addTextChangedListener(passwordCheckChangeWatcher)
            binding.passwordModify.addTextChangedListener(passwordChangeWatcher)

            //은행명 포커스
            binding.bankModify.setOnFocusChangeListener { v, hasFocus ->
                if(hasFocus){
                    binding.modifyAccountLinearLayout.setBackgroundResource(R.drawable.edittext_focus)
                }
                else{
                    binding.modifyAccountLinearLayout.setBackgroundResource(R.drawable.edittext_basic)
                }
            }

            //계좌번호 포커스
            binding.accountModify.setOnFocusChangeListener { v, hasFocus ->
                if(hasFocus){
                    binding.modifyAccountLinearLayout.setBackgroundResource(R.drawable.edittext_focus)
                }
                else{
                    binding.modifyAccountLinearLayout.setBackgroundResource(R.drawable.edittext_basic)
                }
            }







            // 변경완료 버튼 클릭 시
            binding.modifyButton.setOnClickListener{
                val password =binding.passwordModify.text.toString()
                val passwordCheck  = binding.passwordCheckModify.text.toString()
                if (password(password) && passwordEqual(passwordCheck)){
                    modifyUser()
                }
                else{
                    Toast.makeText(context,R.string.confirm_fail, Toast.LENGTH_LONG).show()
                }

            }

            // 탈퇴버튼 클릭 시
            binding.withdrawalButton.setOnClickListener {
                alertCancel()
            }
        }


        return binding.root
    }

    private fun setToolbar() {
        requireActivity().apply {
            toolbar.visibility = View.VISIBLE     // 툴바 보이도록 설정
            toolbar_title.setText("회원 정보 변경")     // 툴바 타이틀 변경
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)  // 스와이프 활성화
        }
    }


    //비밀번호 변경될때마다 인식
    private val passwordChangeWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s != null) {
                password(s)
            }
        }
    }

    //비밀번호 체크 변경될때마다 인식
    private val passwordCheckChangeWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s != null) {
                passwordEqual(s)
            }
        }
    }

    // 패스워드 체크 제한
    private fun password(password: CharSequence): Boolean {
        val pwPattern = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$%^&*-]).{8,16}\$"
        if (Pattern.matches(pwPattern, password)) {
            binding.passwordModifyValid.visibility = View.GONE
            return true
        }
        else {
            // 비밀번호 형식 맞지 않을떄
            passwordValidWrong()
            binding.passwordModifyValid.setText(R.string.password_form_info)
            return false
        }
    }

    private fun passwordEqual(passwordCheck: CharSequence):Boolean{
        val password = binding.passwordModify.text.toString()
        if(password == passwordCheck.toString()){
            passwordValidCorrect()
            return true
        }
        else{
            // 비밀번호 같지 않을 때
            passwordValidWrong()
            binding.passwordModifyValid.setText(R.string.password_different_info)
            return false
        }
    }


    // 회원정보 변경
    private fun modifyUser(){
        val currentUser = auth?.currentUser

        val password = binding.passwordModify.text.toString()
        val phone = binding.phoneModify.text.toString()


        if (currentUser != null) {
            currentUser.updatePassword(password)
                .addOnSuccessListener { Toast.makeText(context,"비밀번호 변경 성공", Toast.LENGTH_LONG).show() }
                .addOnFailureListener {  Toast.makeText(context,"비밀번호 변경 실패", Toast.LENGTH_LONG).show()}

            db.collection("deliveryMan")
                .document(currentUser.uid!!)
                .update("password",password)
                .addOnSuccessListener { documentReference ->

                }
                .addOnFailureListener { e ->
                    Log.w("TEST", "Error adding document", e)
                }

            db.collection("deliveryMan")
                .document(currentUser.uid!!)
                .update("phone",phone)
                .addOnSuccessListener { documentReference ->
                    //findNavController().navigate(R.id.action_modifyUserInfoFragment_to_orderListFragment)
                }
                .addOnFailureListener { e ->
                    Log.w("TEST", "Error adding document", e)
                }

        }
        else{
            findNavController().navigate(R.id.action_modifyUserInfoFragment_to_loginFragment)
        }

        (activity as BaseActivity).setNavigationDrawerHeader()  // 네비게이션 드로어 헤더 설정
    }

    //탈퇴 시
    fun alertCancel() {
        AlertDialog.Builder(requireActivity())
            .setMessage("정말 탈퇴하시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                withdrawal()
            }
            .setNegativeButton("아니오", null)
            .create()
            .show()
    }

    // 탈퇴
    @SuppressLint("RestrictedApi")
    private fun withdrawal(){
        var currentUser = Firebase.auth.currentUser
        currentUser.delete()
            .addOnFailureListener {
                Toast.makeText(context,"탈퇴 실패",Toast.LENGTH_LONG).show()
            }
            .addOnSuccessListener {
            }

        db.collection("deliveryMan").document(currentUser.uid)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context,"탈퇴 성공", Toast.LENGTH_LONG).show()
                val auto = this.requireActivity()
                    .getSharedPreferences("auto", Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = auto.edit()
                editor.clear()
                editor.commit()

                // 백스택 제거
                for (i in 1..findNavController().backStack.count()) {
                    findNavController().popBackStack()
                }

                // 로그인 페이지로 이동
                findNavController().navigate(R.id.loginFragment)
            }
            .addOnFailureListener { Toast.makeText(context,"탈퇴 실패", Toast.LENGTH_LONG).show() }
    }

    // 비밀번호 틀렸을 때 문구 색깔
    private fun passwordValidWrong(){
        binding.passwordModifyValid.setTextColor(Color.parseColor("#FFF44336"))
        binding.passwordModifyValid.visibility = View.VISIBLE
    }

    // 비밀번호 사용가능할 때 문구
    private fun passwordValidCorrect(){
        binding.passwordModifyValid.setTextColor(Color.parseColor("#000000"))
        binding.passwordModifyValid.visibility = View.VISIBLE
        binding.passwordModifyValid.setText(R.string.password_available_info)
    }


}