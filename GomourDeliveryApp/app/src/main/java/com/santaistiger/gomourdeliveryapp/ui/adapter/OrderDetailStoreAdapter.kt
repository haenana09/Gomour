/**
 * created by Kang Gumsil
 */
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
import com.santaistiger.gomourdeliveryapp.databinding.DialogRoundedAlertBinding
import com.santaistiger.gomourdeliveryapp.databinding.ItemDetailStoreBinding
import com.santaistiger.gomourdeliveryapp.utils.toDp

class OrderDetailStoreAdapter(var order: Order) :
    RecyclerView.Adapter<OrderDetailStoreAdapter.ViewHolder>() {
    companion object {
        private const val TAG = "OrderDetailStoreAdapter"
    }

    var items = ArrayList<Store>()
    private val repository: Repository = RepositoryImpl

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 가격 입력 버튼 클릭 시 가격 입력하는 다이얼로그 띄우기
        holder.viewBinding.btnInputPrice.setOnClickListener { inputPrice(it, position) }
        holder.bind(items[position])
    }

    /** 상품 가격 입력하는 다이얼로그 띄우는 함수 */
    private fun inputPrice(it: View, position: Int) {
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
                window?.setLayout(toDp(it.resources.displayMetrics, 250), WindowManager.LayoutParams.WRAP_CONTENT)
            }

        // 확인버튼 클릭 시 해당 store의 cost 수정하고, 데이터베이스 서버에 해당 order를 update하도록 설정
        setOnClickListener(binding, position, dialog)
    }

    /** 확인버튼 클릭 시 해당 store의 cost 수정하고, 데이터베이스 서버에 해당 order를 update 하도록 설정하는 함수*/
    private fun setOnClickListener(
        binding: DialogInputCostBinding,
        position: Int,
        dialog: AlertDialog
    ) {
        binding.btnPositive.setOnClickListener {
            try {
                items[position].cost = binding.etCost.text.toString().toInt()
                repository.updateOrder(order)
                notifyDataSetChanged()
                dialog.dismiss()
            } catch (e: NumberFormatException) {
                showCostInputErrorDialog(it)
            }
        }
        binding.btnNegative.setOnClickListener { dialog.dismiss() }
    }

    private fun showCostInputErrorDialog(it: View) {
        val binding = DataBindingUtil.inflate<DialogRoundedAlertBinding>(
            LayoutInflater.from(it.context),
            R.layout.dialog_rounded_alert,
            null,
            false
        ).apply {
            tvMessage.text = it.resources.getString(R.string.warning_input_cost)
            btnPositive.text = it.resources.getString(R.string.ok)
        }

        val dialog: AlertDialog = AlertDialog.Builder(it.context)
            .setView(binding.root)
            .create()
            .apply {
                show()
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                window?.setLayout(
                    toDp(it.resources.displayMetrics ,240),
                    WindowManager.LayoutParams.WRAP_CONTENT
                )
            }
        binding.btnPositive.setOnClickListener { dialog.dismiss() }
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

