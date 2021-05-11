package com.santaistiger.gomourdeliveryapp.ui.orderdetail

import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.databinding.ObservableParcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.santaistiger.gomourdeliveryapp.data.model.Order
import com.santaistiger.gomourdeliveryapp.data.model.Place
import com.santaistiger.gomourdeliveryapp.data.model.Store
import com.santaistiger.gomourdeliveryapp.data.repository.Repository
import com.santaistiger.gomourdeliveryapp.data.repository.RepositoryImpl
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

val TAG = "OrderDetailViewModel"

class OrderDetailViewModel : ViewModel() {
    var order = MutableLiveData<Order>()
    var storeList = ObservableArrayList<Store>()
    var message = ObservableField<String>()
    var price = ObservableInt()
    var destination = ObservableParcelable<Place>()

    var isCompleteBtnClick = MutableLiveData<Boolean>()
    var isCallBtnClick = MutableLiveData<Boolean>()
    var isTextBtnClick = MutableLiveData<Boolean>()

    private val repository: Repository = RepositoryImpl

    fun getOrderDetail(orderId: String) {
        viewModelScope.launch {
            repository.getOrderDetail(orderId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            Log.d(TAG, "$snapshot")
                            snapshot.getValue(Order::class.java)?.let {
                                order.value = it
                                bindingOrder()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d(TAG, "current order is not in realtime database")
                    }
                })
        }
    }

    // Order 객체의 정보를 viewModel의 변수에 저장
    private fun bindingOrder() {
        for (store in order.value!!.stores) { storeList.add(store) }
        destination.set(order.value!!.destination)
        message.set(order.value!!.message)
        price.set(order.value!!.deliveryCharge)
    }

    // total 비용 계산
    fun getTotal() {
        var total = order.value!!.deliveryCharge
        for (store in order.value!!.stores) {
            if (store.cost != null) total += store.cost!!
        }
        price.set(total)
    }

    fun updateOrder() {
        val stores = ArrayList<Store>()
        stores.addAll(storeList)
        order.value!!.stores = stores
        repository.updateOrder(order.value!!)
    }

    fun doneCompleteBtnClick() {
        isCompleteBtnClick.value = false
    }

    fun onCompleteBtnClick() {
        isCompleteBtnClick.value = true
    }

    fun onCallBtnClick() {
        isCallBtnClick.value = true
    }

    fun onTextBtnClick() {
        isTextBtnClick.value = true
    }

    fun doneCallBtnClick() {
        isCallBtnClick.value = false
    }

    fun doneTextBtnClick() {
        isTextBtnClick.value = false
    }

}