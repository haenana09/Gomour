package com.santaistiger.gomourdeliveryapp.ui.view
/**
 * Created by Jangeunhye
 */
import android.content.Intent
import android.graphics.Color
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.santaistiger.gomourdeliveryapp.R
import com.santaistiger.gomourdeliveryapp.data.model.AccountInfo
import com.santaistiger.gomourdeliveryapp.data.model.DeliveryMan
import com.santaistiger.gomourdeliveryapp.data.repository.Repository
import com.santaistiger.gomourdeliveryapp.data.repository.RepositoryImpl
import com.santaistiger.gomourdeliveryapp.databinding.FragmentJoinBinding
import com.santaistiger.gomourdeliveryapp.ui.viewmodel.JoinViewModel
import kotlinx.android.synthetic.main.activity_base.*
import java.util.regex.Pattern

class JoinFragment: Fragment() {

    private val GALLERY_CODE = 10
    private var auth: FirebaseAuth? = null
    private lateinit var binding: FragmentJoinBinding
    private lateinit var viewModel: JoinViewModel
    private val repository: Repository = RepositoryImpl
    val db = Firebase.firestore
    val storage = Firebase.storage
    var file: Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setToolbar()

        auth = Firebase.auth
        binding = DataBindingUtil.inflate<FragmentJoinBinding>(inflater, R.layout.fragment_join,container,false)
        viewModel = ViewModelProvider(this).get(JoinViewModel::class.java)


        binding.apply {

            // 비어 있지 않을 때 이메일 중복확인 버튼 나오게하기
            emailEditText.addTextChangedListener(emailChageWatcher)
            // 비어 있지 않을 때 비밀번호 감지하여 판단
            // 비어 있지 않을 때 비밀번호 감지하여 판단
            passwordCheckEditText.addTextChangedListener(passwordCheckChangeWatcher)
            passwordEditText.addTextChangedListener(passwordChangeWatcher)

            // 가입버튼을 위해 비어있는지 확인
            emailEditText.addTextChangedListener(mTextWatcher)
            nameEditText.addTextChangedListener(mTextWatcher)
            passwordCheckEditText.addTextChangedListener(mTextWatcher)
            passwordEditText.addTextChangedListener(mTextWatcher)
            PhoneEditText.addTextChangedListener(mTextWatcher)
            accountEditText.addTextChangedListener(mTextWatcher)
            bankEditText.addTextChangedListener(mTextWatcher)
            imageFileText.addTextChangedListener(mTextWatcher)


            //이메일 포커스
            emailEditText.setOnFocusChangeListener { v, hasFocus ->
                if(hasFocus){
                    binding.emailLinearLayout.setBackgroundResource(R.drawable.edittext_focus)
                }
                else{
                    binding.emailLinearLayout.setBackgroundResource(R.drawable.edittext_basic)
                }
            }

            //은행명 포커스
            bankEditText.setOnFocusChangeListener { v, hasFocus ->
                if(hasFocus){
                    binding.accountLinearLayout.setBackgroundResource(R.drawable.edittext_focus)
                }
                else{
                    binding.accountLinearLayout.setBackgroundResource(R.drawable.edittext_basic)
                }
            }

            //계좌번호 포커스
            accountEditText.setOnFocusChangeListener { v, hasFocus ->
                if(hasFocus){
                    binding.accountLinearLayout.setBackgroundResource(R.drawable.edittext_focus)
                }
                else{
                    binding.accountLinearLayout.setBackgroundResource(R.drawable.edittext_basic)
                }
            }

        }



        //이메일 중복버튼
        binding.emailCheckButton.setOnClickListener {
            checkEmail(object : EmailCallback {
                override fun isEmailExist(exist: Boolean) {
                    if (exist){
                        binding.emailValid.visibility = View.VISIBLE
                        binding.emailValid.text = "이미 사용 중인 이메일입니다."

                    }
                    else{
                        binding.emailValid.visibility = View.VISIBLE
                        binding.emailValid.text = "사용가능한 이메일입니다."

                    }
                }
            })
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
            val uid = ""

            var deliveryMan = DeliveryMan(email, password, name, phone, uid, accountInfo, isCertified = false)

            //이메일 검사 -> 비밀번호 검사 -> 계정 생성
            checkEmail(object : EmailCallback {
                override fun isEmailExist(exist: Boolean) {
                    if (exist) {
                        binding.emailValid.visibility = View.VISIBLE
                        binding.emailValid.text = "이미 사용 중인 이메일입니다."
                        Toast.makeText(context, "정보를 다시 입력하세요", Toast.LENGTH_LONG).show()
                    } else {
                        binding.emailValid.visibility = View.VISIBLE
                        binding.emailValid.text = "사용가능한 이메일입니다."
                        if (password(password) && passwordEqual(passwordCheck)) {
                            createAccount(deliveryMan)
                        }
                    }
                }
            })
        }
        return binding.root
    }


    //툴바
    private fun setToolbar() {
        requireActivity().apply {
            toolbar.visibility = View.GONE  // 툴바 숨기기
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)   // 스와이프 비활성화
        }
    }

    //이메일 채워져있는지 감지
    private val emailChageWatcher = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            binding.emailCheckButton.setEnabled(true)
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

    // 모든 영역이 채워져있는지 있는지 감지
    private val mTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
        override fun afterTextChanged(editable: Editable) {
            checkFieldsForEmptyValues()
        }
    }


    interface EmailCallback {
        fun isEmailExist(exist: Boolean)
    }

    // 이메일 존재하는지 확인
    fun checkEmail(emailCallback: EmailCallback){
        val db = FirebaseFirestore.getInstance()
        val id = binding.emailEditText.text.toString()
        val email = id + "@dankook.ac.kr"
        db.collection("deliveryMan").whereEqualTo("email",email ).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result?.isEmpty == true){
                        emailCallback.isEmailExist((false)) //사용 가능
                    }
                    else{
                        emailCallback.isEmailExist(true) //사용 불가
                    }

                }
            }
    }




    //이미지 선택
    fun loadAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent!!.type = MediaStore.Images.Media.CONTENT_TYPE
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
        if (requestCode === GALLERY_CODE) {
            file = data?.data
            if (data != null) {
                data.data?.let { returnUri ->
                    activity?.contentResolver?.query(returnUri, null, null, null, null)
                }?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    binding.imageFileText.text = cursor.getString(nameIndex)
                }
            }
        }
    }



    //모든 영역 널이 아닌지 확인
    private fun checkFieldsForEmptyValues() {
        val signUpButton = binding.signUpButton
        val email = binding.emailEditText.text.toString()
        val password: String = binding.passwordEditText.text.toString()
        val passwordCheck: String = binding.passwordCheckEditText.text.toString()
        val phone: String = binding.PhoneEditText.text.toString()
        val imageFileText = binding.imageFileText.text.toString()
        signUpButton.isEnabled =
            !(email == "" || password == "" || passwordCheck=="" || phone=="" || imageFileText=="")
    }
    // 패스워드 체크 제한
    private fun password(password: CharSequence): Boolean {
        val pwPattern = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$%^&*-]).{8,16}\$"
        if (Pattern.matches(pwPattern, password)) {
            binding.passwordValid.visibility = View.GONE
            return true
        }
        else {
            // 비밀번호 형식 맞지 않을떄
            binding.passwordValid.setTextColor(Color.parseColor("#FFF44336"))
            binding.passwordValid.visibility = View.VISIBLE
           binding.passwordValid.setText("비미ㅏ")
            return false
        }
    }

    private fun passwordEqual(passwordCheck: CharSequence):Boolean{
        val password = binding.passwordEditText.text.toString()
        if(password == passwordCheck.toString()){
            binding.passwordValid.setTextColor(Color.parseColor("#000000"))
            binding.passwordValid.visibility = View.VISIBLE
            binding.passwordValid.setText(R.string.password_available_info)
            return true
        }
        else{
            // 비밀번호 같지 않을 때
            binding.passwordValid.setTextColor(Color.parseColor("#FFF44336"))
            binding.passwordValid.visibility = View.VISIBLE
            binding.passwordValid.setText(R.string.password_different_info)
            return false
        }
    }

    //회원가입
    private fun createAccount(deliveryMan: DeliveryMan){
        auth?.createUserWithEmailAndPassword(deliveryMan.email, deliveryMan.password)
            ?.addOnCompleteListener() {
                    task->
                if(task.isSuccessful){
                    sendImage()
                    val user = auth!!.currentUser
                    deliveryMan.uid = user.uid
                    repository.writeFireStoreDeliveryMan(deliveryMan)
                    Toast.makeText(context,R.string.join_finish_info,Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.action_joinFragment_to_loginFragment)
                }
                else{
                    Log.w("TEST","createUserWithEmail: failure",task.exception )
                    Toast.makeText(context, "Authentication failed.", Toast.LENGTH_LONG).show()
                }
            }
    }
}