package com.santaistiger.gomourdeliveryapp.ui.customview
/**
 * Created by Jangeunhye
 */
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.santaistiger.gomourdeliveryapp.data.model.OrderRequest
import com.santaistiger.gomourdeliveryapp.databinding.FragmentOrderRequestBinding
import com.santaistiger.gomourdeliveryapp.utils.numberFormat
import com.santaistiger.gomourdeliveryapp.utils.toDp
import kotlinx.android.synthetic.main.item_order_request_destination.view.*
import kotlinx.android.synthetic.main.item_order_request_store.view.*

class OrderRequestFragment() : DialogFragment() {
    private lateinit var binding: FragmentOrderRequestBinding
    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderRequestBinding.inflate(inflater, container, false)
        valueSet()
        return binding.root
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setLayout(
                toDp(resources.displayMetrics, 330),
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }
    }
    private fun valueSet() {
        val bundle = arguments
        val request: OrderRequest? = bundle?.getParcelable("order_request객체")
        val stores_count = request?.stores?.count()
        // 가게
        binding.orStores.removeAllViewsInLayout()
        if (stores_count!! >= 1) {
            for (i in 0 until stores_count) {
                val view = OrderRequest_StoreView(requireContext())
                view.or_store_address.text = request.stores[i].place.getDisplayName()
                view.or_menu.text = request.stores[i].menu
                binding.orStores.addView(view)
            }
        }
        // 도착지
        binding.orDestination.removeAllViewsInLayout()
        val destView = OrderRequest_DestinationView(requireContext())
        destView.or_destination_address.text = request.destination!!.getDisplayName()
        binding.orDestination.addView(destView)
        binding.orDeliveryCharge.text =
            numberFormat.format(request.deliveryCharge.toString().toInt())
        binding.yesbutton.setOnClickListener {
            positiveOnClickedListener.PositiveOnClicked("HI")
            dialog?.dismiss()
        }
        binding.nobutton.setOnClickListener {
            negativeOnClickedListener.negativeOnClickedListener("HI")
            dialog?.dismiss()
        }
    }
    // 요청 수락 버튼
    interface PositiveButtonClickListener {
        fun PositiveOnClicked(myName: String)
    }
    private lateinit var positiveOnClickedListener: PositiveButtonClickListener
    fun positiveSetOnClickedListener(listener: PositiveButtonClickListener) {
        positiveOnClickedListener = listener
    }
    // 요청 거절 버튼
    interface NegativeButtonClickListener {
        fun negativeOnClickedListener(myName: String)
    }
    private lateinit var negativeOnClickedListener: NegativeButtonClickListener
    fun negativeSetOnClickedListener(listener: NegativeButtonClickListener) {
        negativeOnClickedListener = listener
    }
}