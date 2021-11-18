package com.santaistiger.gomourdeliveryapp.ui.view

/**
 * Created by Jangeunhye
 */
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.santaistiger.gomourdeliveryapp.data.model.DeliveryMan
import com.santaistiger.gomourdeliveryapp.data.repository.Repository
import com.santaistiger.gomourdeliveryapp.data.repository.RepositoryImpl
import com.santaistiger.gomourdeliveryapp.databinding.FragmentLoginBinding
import com.santaistiger.gomourdeliveryapp.ui.base.BaseActivity
import com.santaistiger.gomourdeliveryapp.ui.customview.RoundedAlertDialog
import com.santaistiger.gomourdeliveryapp.ui.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.fragment_join.*
import kotlinx.coroutines.*


class LoginFragment : Fragment() {

    private var auth: FirebaseAuth? = null
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel
    private val repository: Repository = RepositoryImpl

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // 툴바 설정
        (requireActivity() as BaseActivity).setToolbar(
            requireContext(), false, null, false)

        auth = Firebase.auth
        binding = DataBindingUtil.inflate<FragmentLoginBinding>(
            inflater,
            R.layout.fragment_login, container, false
        )
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        // 빈칸 감지
        binding.emailLogin.addTextChangedListener(mTextWatcher)
        binding.passwordLogin.addTextChangedListener(mTextWatcher)

        // 로그인 버튼 눌렀을 때
        binding.loginButton.setOnClickListener {
            viewModel.email = binding.emailLogin.text.toString() + "@dankook.ac.kr"
            viewModel.password = binding.passwordLogin.text.toString()
            CoroutineScope(Dispatchers.IO).launch {

                // 로그인 전
                if (repository.getUid().isNullOrEmpty()) {
                    viewModel.login().join()

                    if (viewModel.loginInfo.value != null) { // 로그인 성공 시
                        val deliveryMan =
                            withContext(Dispatchers.Default) { repository.readDeliveryManInfo() }
                        if (deliveryMan!!.isCertified) { // 학생증 인증 완료시
                            setSharedPreferences()
                            findNavController().navigate(R.id.action_loginFragment_to_orderListFragment)
                            (requireActivity() as BaseActivity).setNavigationDrawerHeader()
                        } else { // 학생증 인증 전
                            launch(Dispatchers.Main) {
                                showToast("학생증 인증을 기다리세요")
                            }
                        }

                    } else { // 로그인 실패 시
                        launch(Dispatchers.Main) {
                            showAlertDialog(resources.getString(R.string.login_fail_dialog))
                        }
                    }
                }
            }
        }

        // 이메일 포커스
        binding.emailLogin.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.emailLoginLinearLayout.setBackgroundResource(R.drawable.edittext_focus)
            } else {
                binding.emailLoginLinearLayout.setBackgroundResource(R.drawable.edittext_basic)
            }
        }


        // 회워가입 누르면 회원가입페이지로 이동
        binding.goSignUpPage.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_joinFragment)
        }

        return binding.root
    }

    private fun setSharedPreferences() {

        val auto = requireActivity()
            .getSharedPreferences("auto", Context.MODE_PRIVATE)
        val autoLogin = auto.edit()
        autoLogin.putString("email", viewModel.email)
        autoLogin.putString("password", viewModel.password)
        autoLogin.commit()
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    // EditTExt 비어있는지 확인
    private val mTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
        override fun afterTextChanged(editable: Editable) {
            checkFieldsForEmptyValues()
        }
    }

    // 이메일과 패스워드의 입력유무에 따라 버튼 활성화
    fun checkFieldsForEmptyValues() {
        val loginButton = binding.loginButton
        val email = binding.emailLogin.text.toString()
        val password: String = binding.passwordLogin.text.toString()
        loginButton.isEnabled = !(email == "" || password == "")
    }

    // 로그인
    private fun signIn(email: String, password: String) {
        auth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener() { task ->
                if (task.isSuccessful) {

                    //파이어스토어에서 인증상태 확인
                    val user = auth!!.currentUser
                    val db = Firebase.firestore
                    val docRef = db.collection("deliveryMan").document(user.uid)
                    docRef.get().addOnSuccessListener { documentSnapshot ->
                        val data = documentSnapshot.toObject<DeliveryMan>()
                        if (data != null) {

                            //학생증 인증이 완료된 상태 - 로그인 가능
                            if (data.isCertified) {

                                db.collection("deliveryMan")
                                val auto = this.requireActivity()
                                    .getSharedPreferences("auto", Context.MODE_PRIVATE)
                                val autoLogin = auto.edit()
                                autoLogin.putString("email", email)
                                autoLogin.putString("password", password)
                                autoLogin.commit()

                                // 주문 목록 페이지로 이동
                                findNavController().navigate(R.id.action_loginFragment_to_orderListFragment)
                                (activity as BaseActivity).setNavigationDrawerHeader()  // 네비게이션 드로어 헤더 설정
                            } else {
                                //학생증인증이 안되었을 때
                                Toast.makeText(
                                    context,
                                    R.string.login_student_id_info,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            }

            ?.addOnFailureListener {
                // 해당 정보가 없을 때
                showAlertDialog(resources.getString(R.string.login_fail_dialog))
            }
    }

    private fun showAlertDialog(msg: String) {
        RoundedAlertDialog()
            .setMessage(msg)
            .setPositiveButton(resources.getString(R.string.ok), null)
            .show((requireActivity() as BaseActivity).supportFragmentManager, "rounded alert dialog")
    }
}