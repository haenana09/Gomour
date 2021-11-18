package com.santaistiger.gomourdeliveryapp.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import com.santaistiger.gomourdeliveryapp.R
import com.santaistiger.gomourdeliveryapp.databinding.ItemOrderRequestStoreBinding


class OrderRequest_StoreView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    val binding:ItemOrderRequestStoreBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.item_order_request_store,
        this,
        true
    )
}