package com.santaistiger.gomourdeliveryapp.ui.adapter

import android.text.InputType
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.santaistiger.gomourdeliveryapp.data.model.Store
import com.santaistiger.gomourdeliveryapp.databinding.ItemStoreBinding

class OrderDetailStoreAdapter : RecyclerView.Adapter<OrderDetailStoreAdapter.ViewHolder>() {
    val TAG = "StoreAdapter"
    var items = ArrayList<Store>()

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
        val editText = EditText(it.context).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            text = if (items[position].cost != null) {
                SpannableStringBuilder(items[position].cost.toString())
            } else {
                SpannableStringBuilder(String())
            }
        }

        AlertDialog.Builder(it.context)
            .setTitle("해당 가게의 물품 가격을 입력하세요")
            .setView(editText)
            .setPositiveButton("확인") { _, _ ->
                items[position].cost = editText.text.toString().toInt()
                notifyDataSetChanged()
            }
            .setNegativeButton("취소", null)
            .create()
            .show()
    }

    class ViewHolder private constructor(val viewBinding: ItemStoreBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(item: Store) {
            viewBinding.item = item
            viewBinding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemStoreBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

