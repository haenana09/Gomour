package com.santaistiger.gomourdeliveryapp.ui.orderdetail

import android.text.Editable
import android.text.InputType
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.santaistiger.gomourdeliveryapp.data.model.Store
import com.santaistiger.gomourdeliveryapp.data.repository.Repository
import com.santaistiger.gomourdeliveryapp.data.repository.RepositoryImpl
import com.santaistiger.gomourdeliveryapp.databinding.ItemStoreBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.reflect.Array.get

class StoreAdapter : RecyclerView.Adapter<StoreAdapter.ViewHolder>() {
    private val repository: Repository = RepositoryImpl
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

    private fun inputPrice(it: View, position: Int) {
        val item = items[position]
        val editText = EditText(it.context).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            if (item.cost != null) {
                text = SpannableStringBuilder(item.cost.toString())
            }
        }

        AlertDialog.Builder(it.context)
            .setTitle("해당 가게의 물품 가격을 입력하세요")
            .setView(editText)
            .setPositiveButton("확인") { _, _ ->
                item.cost = editText.text.toString().toInt()
                items[position] = item

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

