package com.santaistiger.gomourdeliveryapp.data.network.firebase

import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.santaistiger.gomourdeliveryapp.data.model.Order
import com.santaistiger.gomourdeliveryapp.ui.orderlist.OrderListAdapter

private const val TAG: String = "FirebaseApiService"
private const val ORDER_REQUEST_TABLE = "order_request"
private const val ORDER_TABLE = "order"

object FirebaseApi {
    private val database = Firebase.database
    private val orderRequestTable = database.getReference(ORDER_REQUEST_TABLE)
    private val orderTable = database.getReference(ORDER_TABLE)

    // 배달원의 주문 목록 받아오기
    fun getOrders(deliveryManUid: String, adapter: OrderListAdapter, textView: TextView) {
        val orders = mutableListOf<Order>()
        val ordersReference = orderTable.orderByChild("deliveryManUid").equalTo(deliveryManUid)

        val ordersListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                orders.clear()
                adapter.orders.clear()

                for (messageSnapshot in dataSnapshot.children) {
                    val order: Order? = messageSnapshot.getValue(Order::class.java)
                    if (order != null) {
                        orders.add(order)
                    }
                }

                // 날짜 역순으로 재배열 후 adapter의 orders에 할당
                adapter.orders = orders.asReversed()
                Log.d(TAG, "orders was changed")
                adapter.notifyDataSetChanged()

                // 주문 내역이 없을 경우 안내 문구 표시
                if (adapter.orders.count() == 0) {
                    textView.visibility = View.VISIBLE
                } else {
                    textView.visibility = View.GONE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        ordersReference.addValueEventListener(ordersListener)
    }
}