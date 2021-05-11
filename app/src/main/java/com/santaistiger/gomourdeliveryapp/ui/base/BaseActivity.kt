package com.santaistiger.gomourdeliveryapp.ui.base

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.CompoundButton
import android.widget.Switch
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import com.pedro.library.AutoPermissions
import com.santaistiger.gomourdeliveryapp.R
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.nav_header.view.*

class BaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val TAG = "BaseActivity"
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        // Auto permission
        AutoPermissions.loadAllPermissions(this, 1)

        setSupportActionBar(toolbar)    // 툴바를 액티비티의 앱바로 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true)   // 툴바 홈버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.hamburger_btn)     // 홈버튼 이미지 변경
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 툴바에 앱 타이틀 보이지 않도록 설정

        // navigation 리스너 설정
        navigation_view.setNavigationItemSelectedListener(this)

        // 네비게이션 드로어 헤더 텍스트뷰 값 변경
        val header = navigation_view.getHeaderView(0)
        header.user_name_string.setText("강단국")
        header.user_phone_num_string.setText("010-1234-5678")
        header.user_email_string.setText("32181234@dankook.ac.kr")

        // 주문 받기 스위치 클릭 설정
        val item = navigation_view.menu.findItem(R.id.getOrderStatus)
        val get_order_status_switch = item.actionView.findViewById<Switch>(R.id.get_order_status_switch)
        get_order_status_switch.setOnCheckedChangeListener( object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (isChecked) {
                    Log.d(TAG, "주문 받기 on")
                    // 주문 받도록 설정
                } else {
                    Log.d(TAG, "주문 받기 off")
                    // 주문 더이상 받지 않도록 설정
                }
            }
        })
    }

    // 메뉴 클릭 시 동작 정의
    @SuppressLint("RestrictedApi")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val navController = nav_host_fragment.findNavController()

        // 회원 정보 변경 이외의 메뉴 클릭 시 백스택 제거하여 취소 버튼 누르면 바로 앱 종료되도록
        if (item.itemId != R.id.modifyUserInfoFragment) {
            for (i in 1..navController.backStack.count()) {
                navController.popBackStack()
            }
        }

        when(item.itemId){
            // 주문 목록 클릭 시
            R.id.orderListFragment -> navController.navigate(R.id.orderListFragment)

            // 회원 정보 변경 클릭 시
//            R.id.modifyUserInfoFragment -> navController.navigate(R.id.modifyUserInfoFragment)

            // 로그아웃 클릭 시
            R.id.logout -> {
                // 로그아웃 실행

                // 로그인 화면으로 이동
//                navController.navigate(R.id.loginFragment)
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
}