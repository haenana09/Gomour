package com.santaistiger.gomourdeliveryapp.ui.view

import android.content.Context
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.santaistiger.gomourdeliveryapp.R
import com.santaistiger.gomourdeliveryapp.data.model.AccountInfo
import com.santaistiger.gomourdeliveryapp.data.model.DeliveryMan
import com.santaistiger.gomourdeliveryapp.data.repository.Repository
import com.santaistiger.gomourdeliveryapp.data.repository.RepositoryImpl
import com.santaistiger.gomourdeliveryapp.databinding.FragmentJoinBinding
import com.santaistiger.gomourdeliveryapp.ui.base.BaseActivity
import com.santaistiger.gomourdeliveryapp.ui.customview.RoundedAlertDialog
import com.santaistiger.gomourdeliveryapp.ui.viewmodel.JoinViewModel
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.regex.Pattern

/**
 * Created by Jangeunhye
 */


class JoinFragment : Fragment() {
    companion object {
        private const val TAG = "JoinFragment"
        private const val GALLERY_CODE = 10
    }

    private var auth: FirebaseAuth? = null
    private lateinit var binding: FragmentJoinBinding
    private lateinit var viewModel: JoinViewModel
    private val repository: Repository = RepositoryImpl
    var file: Uri? = null
    var isUniqueEmail = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setToolbar()

        auth = Firebase.auth
        binding = DataBindingUtil.inflate<FragmentJoinBinding>(
            inflater,
            R.layout.fragment_join,
            container,
            false
        )
        viewModel = ViewModelProvider(this).get(JoinViewModel::class.java)

        binding.apply {

            // 비어 있지 않을 때 이메일 중복확인 버튼 나오게하기
            emailEditText.addTextChangedListener(emailChangeWatcher)

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
                if (hasFocus) {
                    binding.emailLinearLayout.setBackgroundResource(R.drawable.edittext_focus)
                } else {
                    binding.emailLinearLayout.setBackgroundResource(R.drawable.edittext_basic)
                }
            }

            //은행명 포커스
            bankEditText.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    binding.accountLinearLayout.setBackgroundResource(R.drawable.edittext_focus)
                } else {
                    binding.accountLinearLayout.setBackgroundResource(R.drawable.edittext_basic)
                }
            }

            //계좌번호 포커스
            accountEditText.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    binding.accountLinearLayout.setBackgroundResource(R.drawable.edittext_focus)
                } else {
                    binding.accountLinearLayout.setBackgroundResource(R.drawable.edittext_basic)
                }
            }

        }


        //이메일 중복버튼
        binding.emailCheckButton.setOnClickListener {
            viewModel.email = binding.emailEditText.text.toString() + "@dankook.ac.kr"
            Log.d(TAG, viewModel.email)
            CoroutineScope(Dispatchers.IO).launch {
                val deferredCheckResult = async { viewModel.duplicateCheck() }
                val checkResult: Boolean = deferredCheckResult.await()

                launch(Dispatchers.Main) {
                    if (checkResult) emailValidCorrect() //사용 가능함
                    else emailValidWrong() //이미 사용하고 있음
                }
            }
        }

        // 이미지 업로드 버튼
        binding.imageUploadButton.setOnClickListener { loadAlbum() }


        //가입버튼
        binding.signUpButton.setOnClickListener {

            val id: String = binding.emailEditText.text.toString()
            val email: String = "$id@dankook.ac.kr"
            val password: String = binding.passwordEditText.text.toString()
            val passwordCheck: String = binding.passwordCheckEditText.text.toString()
            val name: String = binding.nameEditText.text.toString()
            val phone = binding.PhoneEditText.text.toString()
            val bank = binding.bankEditText.text.toString()
            val account = binding.accountEditText.text.toString()
            var accountInfo = AccountInfo(bank, account)

            var deliveryMan =
                DeliveryMan(email, password, name, phone, null, accountInfo, isCertified = false)

            if (isUniqueEmail) {
                if (password(password) && passwordEqual(passwordCheck)) {
                    createAccount(deliveryMan)
                }
            } else {
                showAlertDialog(resources.getString(R.string.join_email_check_info))
            }
        }

        viewModel.isImageUploaded.observe(viewLifecycleOwner, Observer {
            Log.i(TAG, "isImageUploaded = $it")
            if (it != null) {
                if (it) {
                    showToast("사진이 정상적으로 업로드 되었습니다")
                } else {
                    showToast("사진이 정상적으로 업로드 되지 않았습니다")
                }
                viewModel.doneUploadImage()
            }
        })

        return binding.root
    }

    private fun showAlertDialog(msg: String) {
        RoundedAlertDialog()
            .setMessage(msg)
            .setPositiveButton(resources.getString(R.string.ok), null)
            .show(
                (requireActivity() as BaseActivity).supportFragmentManager,
                "rounded alert dialog"
            )
    }


    private fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }


    //툴바
    private fun setToolbar() {
        requireActivity().apply {
            toolbar.visibility = View.GONE  // 툴바 숨기기
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)   // 스와이프 비활성화
        }
    }

    //이메일 채워져있는지 감지
    private val emailChangeWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {
            isUniqueEmail = false
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
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


    //이미지 선택
    private fun loadAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent!!.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, GALLERY_CODE)
    }

    //파이어스토리지에 사진 업로드
    private fun uploadImage() {
        if (file != null) {
            viewModel.uploadImage(file!!)
        }
//        val storageRef =
//            storage.getReferenceFromUrl("gs://gomour-495e0.appspot.com/") //storage 서버로 이동
//        val riversRef = storageRef.child("images/${auth?.currentUser?.uid}")
//
//        val uploadTask = file?.let { riversRef.putFile(it) }
//        uploadTask?.addOnFailureListener {
//            Toast.makeText(
//                context,
//                "사진이 정상적으로 업로드 되지 않았습니다.",
//                Toast.LENGTH_SHORT
//            ).show()
//        }?.addOnSuccessListener {
//            Toast.makeText(
//                context,
//                "사진이 정상적으로 업로드 되었습니다.",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
    }


    //선택한이미지 받기
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == GALLERY_CODE) {
            file = intent?.data

            if (file != null) {
                val cursor = file.let { uri ->
                    requireActivity().contentResolver.query(
                        uri!!, null, null, null, null
                    )
                }

                cursor?.use { cursor ->
                    cursor.moveToFirst()
                    binding.imageFileText.text =
                        cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
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
            !(email == "" || password == "" || passwordCheck == "" || phone == "" || imageFileText == "")
    }

    // 패스워드 체크 제한
    private fun password(password: CharSequence): Boolean {
        val pwPattern = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$%^&*-]).{8,16}\$"
        if (Pattern.matches(pwPattern, password)) {
            binding.passwordValid.visibility = View.GONE
            return true
        } else {
            // 비밀번호 형식 맞지 않을떄
            binding.passwordValid.setTextColor(Color.parseColor("#FFF44336"))
            binding.passwordValid.visibility = View.VISIBLE
            binding.passwordValid.setText(R.string.password_form_info)
            return false
        }
    }

    private fun passwordEqual(passwordCheck: CharSequence): Boolean {
        val password = binding.passwordEditText.text.toString()
        if (password == passwordCheck.toString()) {
            binding.passwordValid.setTextColor(Color.parseColor("#000000"))
            binding.passwordValid.visibility = View.VISIBLE
            binding.passwordValid.setText(R.string.password_available_info)
            return true
        } else {
            // 비밀번호 같지 않을 때
            binding.passwordValid.setTextColor(Color.parseColor("#FFF44336"))
            binding.passwordValid.visibility = View.VISIBLE
            binding.passwordValid.setText(R.string.password_different_info)
            return false
        }
    }

    //emailValid 관련 텍스트
    private fun emailValidWrong() {
        // 이미 사용중인 이메일입니다.
        binding.emailValid.visibility = View.VISIBLE
        binding.emailValid.setTextColor(Color.parseColor("#FFF44336")) //레드
        binding.emailValid.setText(R.string.join_email_duplicate_info)
        Toast.makeText(context, R.string.confirm_fail, Toast.LENGTH_LONG).show()

    }

    private fun emailValidCorrect() {
        // 사용가능 이메일
        binding.emailValid.visibility = View.VISIBLE
        binding.emailValid.setTextColor(Color.parseColor("#000000"))
        binding.emailValid.setText(R.string.join_email_available_info)
        isUniqueEmail = true
    }


    //회원가입
    private fun createAccount(deliveryMan: DeliveryMan) {
        viewModel.email = binding.emailEditText.text.toString() + "@dankook.ac.kr"
        viewModel.password = binding.passwordCheckEditText.text.toString()
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.join().join()
            if (viewModel.joinInfo.value != null) {

                // 스토리지에 학생증 사진 올리기
                uploadImage()

                // 회원가입
                deliveryMan.uid = repository.getUid()
                repository.writeFireStoreDeliveryMan(deliveryMan)
                // 자동로그인에 저장
                val auto = requireActivity()
                    .getSharedPreferences("auto", Context.MODE_PRIVATE)
                val autoLogin = auto.edit()
                autoLogin.putString("email", deliveryMan.email)
                autoLogin.putString("password", deliveryMan.password)
                autoLogin.commit()
                findNavController().navigate(R.id.action_joinFragment_to_loginFragment)
                (requireActivity() as BaseActivity).setNavigationDrawerHeader()
            } else {
                launch(Dispatchers.Main) {
                    Toast.makeText(context, R.string.join_fail_info, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}