package com.santaistiger.gomourdeliveryapp.ui.base

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.pedro.library.AutoPermissions
import com.santaistiger.gomourdeliveryapp.R
import com.santaistiger.gomourdeliveryapp.data.model.Order
import com.santaistiger.gomourdeliveryapp.data.model.OrderRequest
import com.santaistiger.gomourdeliveryapp.ui.orderrequest.OrderRequestFragment
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.dialog_deliverytime.*
import kotlinx.android.synthetic.main.fragment_order_request.*
import kotlinx.android.synthetic.main.nav_header.view.*
import java.util.*
import kotlin.properties.Delegates


class BaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val TAG = "BaseActivity"
    val database = Firebase.database
    val databaseReference: DatabaseReference by lazy { FirebaseDatabase.getInstance().reference }

    // reqltimeDB에서 받아온 주문 리스트
    var order_request_list  = ArrayList<OrderRequest>()

    // realtime db에 있는 order_request 테이블에 접근
    val myRef = databaseReference.child("order_request")

    // 팝업창 상태 변화 감지
    var popUpState: Int by Delegates.observable(0) { property, oldValue, newValue ->

        // 0: 팝업창 띄워도 되는 상태
        // 1: 팝업창 이미 띄워진 상태. 띄우기 불가
        if(newValue== 0){
            if(order_request_list.isEmpty()){
                Log.d(TAG, "새로 들어온 주문이 없습니다.")
            }
            else{
                // 팝업창 띄우기
                sendValue(order_request_list[0])
            }
        }
        else{
            Log.d(TAG,"이미 띄워진 팝업창이 있습니다.")
        }

    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        // Auto permission
        AutoPermissions.loadAllPermissions(this, 1)

        setSupportActionBar(toolbar)    // 툴바를 액티비티의 앱바로 지정
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)   // 툴바 홈버튼 활성화
            setHomeAsUpIndicator(R.drawable.hamburger_btn)     // 홈버튼 이미지 변경
            setDisplayShowTitleEnabled(false)     // 툴바에 앱 타이틀 보이지 않도록 설정
        }

        // navigation 리스너 설정
        navigation_view.setNavigationItemSelectedListener(this)

        // 네비게이션 드로어 헤더 텍스트뷰 값 변경
        val header = navigation_view.getHeaderView(0)
        header.apply {
            user_name_string.setText("강단국")
            user_phone_num_string.setText("010-1234-5678")
            user_email_string.setText("32181234@dankook.ac.kr")
        }

        //  order_request_list에 요소 추가하는 리스너
        val listener: DataListener = object : DataListener {
            override fun onRequestReceived(data: OrderRequest?) {
                if (data != null) {
                    order_request_list.add(data)
                    popUpState = popUpState
                }
            }
            override fun onError(error: Throwable?) {
                Log.d(TAG,"데이터 받아오기 실패하였습니다.")
            }
        }

        // childeventlistener로 데이터변경될때마다 받아오기
        val childEventListener = object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val request: OrderRequest? = snapshot.getValue(OrderRequest::class.java)
                if (request != null) {
                    //리스너에 OrderRequest 객체 전달
                    listener.onRequestReceived(request)
                }
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
        }

        // 주문 받기 스위치 클릭 설정
        val item = navigation_view.menu.findItem(R.id.getOrderStatus)
        val get_order_status_switch = item.actionView.findViewById<Switch>(R.id.get_order_status_switch)
        get_order_status_switch.setOnCheckedChangeListener( object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (isChecked) {
                    Log.d(TAG, "주문 받기 on")
                    order_request_list.clear()
                    popUpState = 0 //주문받기 on을 누르면 팝업창 띄우도록 설정
                    // 주문 받도록 설정
                    myRef.addChildEventListener(childEventListener)

                } else {
                    Log.d(TAG, "주문 받기 off")
                    order_request_list.clear()
                    popUpState = 1 // 팝업창 못띄우도록 설정
                    // 주문 더이상 받지 않도록 설정
                    myRef.removeEventListener(childEventListener)
                }
            }
        })
    }


    // 주문 요청 리스트 받아오기 위한 인터페이스 선언
    interface DataListener {
        fun onRequestReceived(data: OrderRequest?)
        fun onError(error: Throwable?)
    }


    // 네비게이션 드로어 메뉴 클릭 시 동작 정의
    @SuppressLint("RestrictedApi")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val navController = nav_host_fragment.findNavController()

        // 주문 받기, 회원 정보 변경 이외의 메뉴 클릭 시 백스택 제거하여 취소 버튼 누르면 바로 앱 종료되도록
        if (item.itemId != R.id.modifyUserInfoFragment && item.itemId != R.id.getOrderStatus) {
            for (i in 1..navController.backStack.count()) {
                navController.popBackStack()
            }
        }

        when(item.itemId){
            // 주문 목록 클릭 시
            R.id.orderListFragment -> navController.navigate(R.id.orderListFragment)

            // 회원 정보 변경 클릭 시
            R.id.modifyUserInfoFragment -> navController.navigate(R.id.modifyUserInfoFragment)

            // 로그아웃 클릭 시
            R.id.logout -> {
                // 현재 사용자 가져와서 로그아웃
                var auth = Firebase.auth
                auth?.signOut()

                // 자동로그인 삭제
                val auto = getSharedPreferences("auto", Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = auto.edit()
                editor.clear()
                editor.commit()

                // 로그인 화면으로 이동
                navController.navigate(R.id.loginFragment)
            }
        }

        if (item.itemId != R.id.getOrderStatus) {
            drawer_layout.closeDrawers()
        }

        return false
    }


    // 툴바의 홈버튼 누르면 네비게이션 드로어 열리도록 설정
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                drawer_layout.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    // 뒤로가기 버튼 정의
    override fun onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawers()
        } else{
            super.onBackPressed()
        }
    }


    // 팝업창 띄우는 함수
    private fun sendValue(order_request: OrderRequest){
        //다른 팝업창 못띄우게 상태 설정
        popUpState =1

        //OrderRequestFragment에 전달할 객체
        val bundle = Bundle()
        bundle.putParcelable("order_request객체",order_request)

        //주문요청 팝업창 띄우기
        var myDialogFragment: OrderRequestFragment = OrderRequestFragment()
        myDialogFragment.arguments = bundle
        val fragmentManager = this?.supportFragmentManager
        fragmentManager?.let { fragmentManager ->
            myDialogFragment.isCancelable = false //다른 영역 터치 불가
            myDialogFragment.show(
                fragmentManager, "search filter"
            )
        }


        // 배달 예상 시간 입력하는 다이얼로그
        val builder = AlertDialog.Builder(this)
        builder.setView(layoutInflater.inflate(R.layout.dialog_deliverytime,null))

        // 배달예상시간다이얼로그 수락 시 리스너
        var listener = DialogInterface.OnClickListener{ p0, _->
            val dialog = p0 as AlertDialog
            var deliverytimeEditTEext = dialog.findViewById<EditText>(R.id.editText).text
            var deliverytime:Long = 30

            //배달예상 시간입력하지 않을 경우 디폴트 30분으로 전송
            if (deliverytimeEditTEext.length == 0) {
                deliverytime = 30
            }

            myRef.child(order_request.orderId).get().addOnSuccessListener {
                if(it.exists()){
                    //popUpState =1
                    val item = navigation_view.menu.findItem(R.id.getOrderStatus)
                    val get_order_status_switch = item.actionView.findViewById<Switch>(R.id.get_order_status_switch)
                    get_order_status_switch.setChecked(false)   // 주문 받기 스위치 off로 설정

                    //realtimeDB Order테이블에 업로드
                    orderCreate(order_request,deliverytime)
                    //request_order테이블에서 데이터 삭제
                    order_request.orderId?.let { myRef.child(it).removeValue() }
                }
                else{
                    // 이미 배정된 주문이거나, 주문자가 취소할 경우
                    alertCancel()
                }
            }
        }



        // 주문 거절 누를때
        myDialogFragment.negativeSetOnClickedListener(object:OrderRequestFragment.NegativeButtonClickListener{
            override fun negativeOnClickedListener(myName: String) {
                order_request_list.removeAt(0)
                popUpState = 0 // 다시 팝업창 띄울 수 있게
            }

        })



        // 주문 수락 눌렀을 때
        myDialogFragment.positiveSetOnClickedListener(object: OrderRequestFragment.PositiveButtonClickListener{
            override fun PositiveOnClicked(myName: String) {
                //popUpState = 1
                myRef.child(order_request.orderId).get().addOnSuccessListener {
                    if(it.exists()){
                        //popUpState = 1
                        builder.setView(R.layout.dialog_deliverytime)
                            .setPositiveButton("확인", listener)
                            .setCancelable(false)
                            .show()
                        myDialogFragment.dismiss()
                    }
                    else{
                        // 이미 배정된 주문이거나, 주문자가 취소할 경우
                        alertCancel()
                    }
                }
            }
        })
    }



    private fun orderCreate(orderRequest: OrderRequest, deliverytime: Long){
        val order = orderRequest.stores?.let {
            Order(
                customerUid = orderRequest.customerUid,
                //deliveryManUid 받아와야돼 ~
                deliveryManUid = "123456789",
                orderId = orderRequest.orderId,
                stores = it,
                deliveryCharge = orderRequest.deliveryCharge,
                destination = orderRequest.destination,
                message = orderRequest.message,
                orderDate = orderRequest.orderDate,
                deliveryTime = deliverytime,
                isCompleted = false
            )
        }
        orderRequest.orderId?.let { databaseReference.child("order").child(it).setValue(order) }
    }


    fun alertCancel() {
        AlertDialog.Builder(this)
            .setMessage("이미 처리된 주문입니다.")
            .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                order_request_list.removeAt(0)
                popUpState = 0 //팝업창 띄우는 상태로 변경

            })
            .setCancelable(false)
            .create()
            .show()

    }


}
