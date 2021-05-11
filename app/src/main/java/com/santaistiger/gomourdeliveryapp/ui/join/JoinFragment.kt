package com.santaistiger.gomourdeliveryapp.ui.join

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
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
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.santaistiger.gomourdeliveryapp.R
import com.santaistiger.gomourdeliveryapp.data.model.AccountInfo
import com.santaistiger.gomourdeliveryapp.data.model.DeliveryMan
import com.santaistiger.gomourdeliveryapp.databinding.FragmentJoinBinding
import kotlinx.android.synthetic.main.activity_base.*
import java.util.regex.Pattern

class JoinFragment: Fragment() {
    private val GALLERY_CODE = 10
    private var auth: FirebaseAuth? = null
    private lateinit var binding: FragmentJoinBinding
    private lateinit var viewModel: JoinViewModel
    val db = Firebase.firestore
    val storage = Firebase.storage
    var file: Uri? = null



    //이메일 채워져있는지 감지
    private val emailChageWatcher = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            binding.emailCheckButton.setEnabled(true)
        }
    }


    //비밀번호 변경될때마다 인식
    private val passwordChageWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s != null) {
                passwordCheck(s)
            }


        }
    }
    // 모든 영역이 채워져있는지 있는지 감지
    private val mTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
        override fun afterTextChanged(editable: Editable) {
            checkFieldsForEmptyValues()
        }
    }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setToolbar()

        auth = Firebase.auth
        binding = DataBindingUtil.inflate<FragmentJoinBinding>(inflater, R.layout.fragment_join,container,false)
        viewModel = ViewModelProvider(this).get(JoinViewModel::class.java)


        binding.emailValid.visibility = View.GONE
        binding.passwordValid.visibility = View.GONE


        // 비어 있지 않을 때 이메일 중복확인 버튼 나오게하기
        binding.emailEditText.addTextChangedListener(emailChageWatcher)
        // 비어 있지 않을 때 비밀번호 감지하여 판단
        binding.passwordCheckEditText.addTextChangedListener(passwordChageWatcher)

        // 가입버튼을 위해 비어있는지 확인
        binding.emailEditText.addTextChangedListener(mTextWatcher)
        binding.nameEditText.addTextChangedListener(mTextWatcher)
        binding.passwordCheckEditText.addTextChangedListener(mTextWatcher)
        binding.passwordEditText.addTextChangedListener(mTextWatcher)
        binding.PhoneEditText.addTextChangedListener(mTextWatcher)
        binding.accountEditText.addTextChangedListener(mTextWatcher)
        binding.bankEditText.addTextChangedListener(mTextWatcher)


        //이메일 중복버튼
        binding.emailCheckButton.setOnClickListener {
            emailDuplicateCheck(binding.emailEditText.text.toString())
        }

        // 이미지 업로드 버튼
        binding.imageUploadButton.setOnClickListener { loadAlbum() }

        //가입버튼
        binding.signUpButton.setOnClickListener{
            val id:String = binding.emailEditText.text.toString()
            val email:String = id +"@dankook.ac.kr"
            val password:String =binding.passwordEditText.text.toString()
            val passwordCheck: String  = binding.passwordCheckEditText.text.toString()
            val name: String = binding.nameEditText.text.toString()
            val phone = binding.PhoneEditText.text.toString()
            val bank = binding.bankEditText.text.toString()
            val account = binding.accountEditText.text.toString()
            var accountInfo = AccountInfo(bank,account)
            //uid는 회원가입완료 후 생성됨. 일단은 임시로 ..!
            val uid = "1234"
            var deliveryMan = DeliveryMan(email, password, name, phone, uid, accountInfo,isCertified = false)
            Log.d("Test","EamilValieCheckh"+emailValidCheck().toString())
            Log.d("Test", passwordCheck(passwordCheck).toString())

            if(emailValidCheck() && passwordCheck(passwordCheck) &&emailDuplicateCheck(email)){

                createAccount(deliveryMan, binding.passwordCheckEditText.text.toString())
            }
            else{
                Toast.makeText(context,"정보를 다시 입력하세요", Toast.LENGTH_LONG).show()
            }

        }
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun setToolbar() {
        requireActivity().apply {
            toolbar.visibility = View.GONE  // 툴바 숨기기
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)   // 스와이프 비활성화
        }
    }


    //이미지 선택
    fun loadAlbum() {
        Log.d("HH","loadAlbum시작")
        val intent = Intent(Intent.ACTION_PICK)
        intent!!.type = MediaStore.Images.Media.CONTENT_TYPE
        Log.d("HH","loadAlbum끝")
        startActivityForResult(intent, GALLERY_CODE)
    }

    //파이어스토리지에 사진 업로드
    private fun sendImage(){
        val storageRef =
            storage.getReferenceFromUrl("gs://gomour-495e0.appspot.com/") //storage 서버로 이동
        val riversRef = storageRef.child("images/${auth?.currentUser?.uid}")

        val uploadTask = file?.let { riversRef.putFile(it) }
        if (uploadTask != null) {
            uploadTask.addOnFailureListener {
                Log.d("HH","실패")
                Toast.makeText(
                    context,
                    "사진이 정상적으로 업로드 되지 않았습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnSuccessListener {
                Toast.makeText(
                    context,
                    "사진이 정상적으로 업로드 되었습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    //선택한이미지 받기
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("HH","onActivityResult시작")
        if (requestCode === GALLERY_CODE) {
            Log.d("HH","스토리지 올리기 전")
            //uri를 못받아오는 듯?
            file = data?.data
            Log.d("HH", file.toString())

            if (data != null) {
                data.data?.let { returnUri ->
                    activity?.contentResolver?.query(returnUri, null, null, null, null)
                }?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    cursor.moveToFirst()
                    binding.imageFileText.text = cursor.getString(nameIndex)
                }
            }
        }
    }



    //비밀번호 제한 확인
    private fun passwordCheck(s: CharSequence):Boolean{
        val password = binding.passwordEditText.text.toString()
        val pwPattern = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$"

        if(password == s.toString()){
            if(Pattern.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$%^&*-]).{8,16}\$", password))
            {
                binding.passwordValid.visibility = View.VISIBLE
                binding.passwordValid.text = "사용할 수 있는 비밀번호입니다."
                return true
            }
            else{
                binding.passwordValid.visibility = View.VISIBLE
                binding.passwordValid.text = "비밀번호는 대,소문자,숫자,특수문자 포함 8~16자여야합니다."
                return false
            }
        }
        else{
            binding.passwordValid.visibility = View.VISIBLE
            binding.passwordValid.text = "비밀번호가 같지 않습니다."
            return false
        }
    }



    //모든 영역 널이 아닌지 확인
    private fun checkFieldsForEmptyValues() {
        val signUpButton = binding.signUpButton
        val email = binding.emailEditText.text.toString()
        val password: String = binding.passwordEditText.text.toString()
        val passwordCheck: String = binding.passwordCheckEditText.text.toString()
        val phone: String = binding.PhoneEditText.text.toString()

        if (email == "" || password == "" || passwordCheck=="" || phone=="") {
            signUpButton.isEnabled = false
        } else {
            signUpButton.isEnabled = true
        }
    }

    //이메일 형식 체크
    private fun emailValidCheck():Boolean{
        var returnvalue = true
        val id = binding.emailEditText.text .toString()
        val email = id + "@dankook.ac.kr"
        //이메일 형식이 맞지 않을 경우
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailValid.visibility = View.VISIBLE
            binding.emailValid.text = "이메일 형식이 아닙니다."
            returnvalue = false
            Log.d("Test", "이메일형식이 맞지 않을경우" + returnvalue.toString())
        }
        //이메일 형식이 맞는 경우
        else{
            returnvalue = true

        }
        return returnvalue
    }


    //이메일 중복 확인 버튼
    private fun emailDuplicateCheck(email:String):Boolean{
        var return_value = false
        if(emailValidCheck()){
            //deliveryMan에서 겹치는거 확인
            Firebase.firestore?.collection("deliveryMan").whereEqualTo("email",email).get()
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        for(dc in it.result!!.documents){
                            binding.emailValid.visibility = View.VISIBLE
                            binding.emailValid.text = "이미 사용 중인 이메일입니다."
                            return_value = false
                            Log.d("Test", "이미 사용중인 이메일입니다." + return_value.toString())
                        }
                        if (it.result!!.documents.isEmpty()){
                            binding.emailValid.visibility = View.VISIBLE
                            binding.emailValid.text = "사용할 수 있는 이메일입니다."
                            return_value = true
                            Log.d("Test", "사용가능 이메일입니다." + return_value.toString())
                        }
                    }
                }

            return return_value
        }
        else{
            Log.d("Test", "이메일 형식이 맞지않는걸?" + return_value.toString())
            return return_value
        }
    }


    //회원가입
    private fun createAccount(deliveryMan: DeliveryMan, passwordCheck:String){
        auth?.createUserWithEmailAndPassword(deliveryMan.email, deliveryMan.password)
            ?.addOnCompleteListener() {
                    task->
                if(task.isSuccessful){
                    sendImage()
                    Log.d("TEST","createUserWithEmail: success")
                    val user = auth!!.currentUser
                    deliveryMan.uid = user.uid
                    // Add a new document with a generated ID
                    db.collection("deliveryMan")
                        .document(deliveryMan.uid!!).set(deliveryMan)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(context,"학생증 인증을 기다리세요",Toast.LENGTH_LONG).show()
                            findNavController().navigate(R.id.action_joinFragment_to_loginFragment)
                        }
                        .addOnFailureListener { e ->
                            Log.w("TEST", "Error adding document", e)
                        }
                }
                else{
                    Log.w("TEST","createUserWithEmail: failure",task.exception )
                    Toast.makeText(context, "Authentication failed.", Toast.LENGTH_LONG).show()
                }
            }

    }
}