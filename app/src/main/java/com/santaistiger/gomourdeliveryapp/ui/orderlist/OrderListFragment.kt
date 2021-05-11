package com.santaistiger.gomourdeliveryapp.ui.orderlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.santaistiger.gomourdeliveryapp.R
import com.santaistiger.gomourdeliveryapp.data.network.firebase.FirebaseApi
import com.santaistiger.gomourdeliveryapp.databinding.FragmentOrderListBinding
import kotlinx.android.synthetic.main.activity_base.*

class OrderListFragment : Fragment() {

    private val TAG = "OrderListFragment"

    private lateinit var binding: FragmentOrderListBinding
    private lateinit var viewModel: OrderListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        requireActivity().toolbar.visibility = View.VISIBLE     // 툴바 보이도록 설정
        requireActivity().toolbar_title.setText("주문 목록")    // 툴바 타이틀 변경
        requireActivity().drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)  // 스와이프 활성화

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_order_list, container, false
        )
        viewModel = ViewModelProvider(this).get(OrderListViewModel::class.java)
        binding.orderListViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // 리사이클러뷰 어댑터 생성 후 설정
        val adapter = OrderListAdapter(context)
        val deliveryManUid = "123456789"
        val emptyTextView = binding.emptyNoticeString
        FirebaseApi.getOrders(deliveryManUid, adapter, emptyTextView)
        binding.orderList.adapter = adapter

        return binding.root
    }
}