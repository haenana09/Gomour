package com.santaistiger.gomourdeliveryapp.ui.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.santaistiger.gomourdeliveryapp.R
import com.santaistiger.gomourdeliveryapp.data.model.Order
import com.santaistiger.gomourdeliveryapp.data.model.Store
import com.santaistiger.gomourdeliveryapp.data.repository.Repository
import com.santaistiger.gomourdeliveryapp.data.repository.RepositoryImpl
import com.santaistiger.gomourdeliveryapp.databinding.DialogInputCostBinding
import com.santaistiger.gomourdeliveryapp.databinding.ItemDetailStoreBinding

class OrderDetailStoreAdapter(var order: Order) :
    RecyclerView.Adapter<OrderDetailStoreAdapter.ViewHolder>() {
    companion object {
        private const val TAG = "StoreAdapter"
    }

    var items = ArrayList<Store>()
    private val repository: Repository = RepositoryImpl

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // button에 클릭 리스너 등록
        holder.viewBinding.btnInputPrice.setOnClickListener { inputPrice(it, position) }
        holder.bind(items[position])
    }

    /**
     * 상품 가격 입력하는 다이얼로그 띄우는 함수
     */
    private fun inputPrice(it: View, position: Int) {
        val width = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 250.toFloat(), it.resources.displayMetrics
        )

        val binding = DataBindingUtil.inflate<DialogInputCostBinding>(
            LayoutInflater.from(it.context),
            R.layout.dialog_input_cost,
            null, false
        )

        val dialog: AlertDialog = AlertDialog.Builder(it.context)
            .setView(binding.root)
            .create().apply {
                show()
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                window?.setLayout(width.toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
            }

        setOnClickListener(binding, position, dialog)
    }

    private fun setOnClickListener(
        binding: DialogInputCostBinding,
        position: Int,
        dialog: AlertDialog
    ) {
        binding.btnPositive.setOnClickListener {
            items[position].cost = binding.etCost.text.toString().toInt()
            repository.updateOrder(order)
            notifyDataSetChanged()
            dialog.dismiss()
        }
        binding.btnNegative.setOnClickListener { dialog.dismiss() }
    }


    class ViewHolder private constructor(val viewBinding: ItemDetailStoreBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(item: Store) {
            viewBinding.item = item
            viewBinding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDetailStoreBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

