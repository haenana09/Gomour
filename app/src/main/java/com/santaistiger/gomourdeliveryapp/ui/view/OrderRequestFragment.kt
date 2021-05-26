package com.santaistiger.gomourdeliveryapp.ui.view
/**
 * Created by Jangeunhye
 */
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.santaistiger.gomourdeliveryapp.data.model.OrderRequest
import com.santaistiger.gomourdeliveryapp.databinding.FragmentOrderRequestBinding
import com.santaistiger.gomourdeliveryapp.ui.customview.OrderRequest_DestinationView
import com.santaistiger.gomourdeliveryapp.ui.customview.OrderRequest_StoreView
import kotlinx.android.synthetic.main.item_list_store.view.*
import kotlinx.android.synthetic.main.item_order_request_destination.view.*
import kotlinx.android.synthetic.main.item_order_request_store.view.*


class OrderRequestFragment(): DialogFragment() {

    private lateinit var binding: FragmentOrderRequestBinding

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentOrderRequestBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        valueSet()
        return binding.root
    }


    fun valueSet() {
        val bundle = arguments
        val request: OrderRequest? = bundle?.getParcelable("order_request객체")
        val stores_count  = request?.stores?.count()

        // 가게
        binding.orStores.removeAllViewsInLayout()
        if(stores_count!! >= 1){
            for(i in 0..stores_count!! -1){
                val view = OrderRequest_StoreView(requireContext())
                view.or_store_address.setText(request.stores[i].place.getDisplayName())
                view.or_menu.setText(request.stores[i].menu)
                binding.orStores.addView(view)
            }
        }

        // 도착지
        binding.orDestination.removeAllViewsInLayout()
        val destView = OrderRequest_DestinationView(requireContext())
        destView.or_destination_address.setText(request.destination!!.getDisplayName())
        binding.orDestination.addView(destView)

        binding.orDeliveryCharge.text = request.deliveryCharge.toString()


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