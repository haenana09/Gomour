// 배달원 - mvvm 적용 orderlist

package com.santaistiger.gomourdeliveryapp.ui.view

/**
 * Created by Jieun Park.
 */


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.santaistiger.gomourdeliveryapp.R
import com.santaistiger.gomourdeliveryapp.data.model.Order
import com.santaistiger.gomourdeliveryapp.data.model.Status
import com.santaistiger.gomourdeliveryapp.data.network.database.RealtimeApi
import com.santaistiger.gomourdeliveryapp.data.repository.Repository
import com.santaistiger.gomourdeliveryapp.data.repository.RepositoryImpl
import com.santaistiger.gomourdeliveryapp.databinding.FragmentOrderListBinding
import com.santaistiger.gomourdeliveryapp.ui.adapter.OrderListAdapter
import com.santaistiger.gomourdeliveryapp.ui.base.BaseActivity
import com.santaistiger.gomourdeliveryapp.ui.viewmodel.OrderListViewModel
import kotlinx.android.synthetic.main.activity_base.*

class OrderListFragment : Fragment() {
    private val TAG = "OrderListFragment"
    private lateinit var binding: FragmentOrderListBinding
    private lateinit var viewModel: OrderListViewModel
    private val repository: Repository = RepositoryImpl

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setToolbar()

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_order_list, container, false
        )
        viewModel = ViewModelProvider(this).get(OrderListViewModel::class.java)
        binding.orderListViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // 리사이클러뷰 어댑터 생성 후 설정
        val adapter = OrderListAdapter(context)
        val deliveryManUid = repository.getUid()
        val emptyNoticeTextView = binding.emptyNoticeString

        /**
         * realtime database에 주문 내역이 존재하는지 확인
         * 주문 내역이 존재하면 주문 내역을 최근 날짜 순으로 배열하면 어댑터의 주문 목록 리스트에 넣어준다.
         * 주문 내역이 존재하지 않으면 빈 리싸이클러뷰 안내 문구를 표시한다.
         * 한 주문 배달 제한을 위해 최근 주문의 주문의 배달 상태를 저장한다.
         */
        viewModel.getOrderList(deliveryManUid)

        viewModel.orders.observe(viewLifecycleOwner, Observer<ArrayList<Order>> { orders ->
            Log.i(TAG, orders.toString())
            if (!orders.isNullOrEmpty()) {
                // 최근 날짜 순으로 주문 목록 재배열 후 adapter의 orders에 할당
                adapter.orderList = orders.asReversed()
                adapter.notifyDataSetChanged()

                emptyNoticeTextView.visibility = View.GONE     // 빈 리싸이클러뷰 안내 문구 숨김
                (activity as BaseActivity).recentOrder = orders.last()   // 최근 주문의 배달 상태 설정
            } else {
                emptyNoticeTextView.visibility = View.VISIBLE  // 빈 리싸이클러뷰 안내 문구 표시
                (activity as BaseActivity).recentOrder = null   // 최근 주문의 배달 상태 설정
            }
        })

        binding.orderList.adapter = adapter

        return binding.root
    }

    private fun setToolbar() {
        requireActivity().apply {
            toolbar.visibility = View.VISIBLE     // 툴바 보이도록 설정
            toolbar_title.setText(R.string.toolbar_title_order_list)     // 툴바 타이틀 변경
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)  // 스와이프 활성화
        }
    }
}