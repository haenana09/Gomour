package com.santaistiger.gomourdeliveryapp.ui.orderrequest

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.santaistiger.gomourdeliveryapp.data.model.OrderRequest
import com.santaistiger.gomourdeliveryapp.databinding.FragmentOrderRequestBinding

class OrderRequestFragment(): DialogFragment() {

    private lateinit var binding: FragmentOrderRequestBinding

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentOrderRequestBinding.inflate(inflater, container, false)

        valueSet()
        return binding.root
    }


    fun valueSet() {
        val bundle = arguments
        val request: OrderRequest? = bundle?.getParcelable("order_request객체")
        val stores_count  = request?.stores?.count()


        if (stores_count == 1) {
            binding.place1.text = request?.stores?.get(0)?.place?.placeName
            binding.menu1.text = request?.stores?.get(0)?.menu
            //destination
            binding.destination.text = request?.destination?.placeName
            //deliverycharge
            binding.deliverycharge.text = request?.deliveryCharge.toString()

            binding.menu2.visibility = View.GONE
            binding.place2.visibility = View.GONE
            //binding.placeimg2.visibility = View.GONE
            binding.menu3.visibility = View.GONE
            binding.place3.visibility = View.GONE
            // binding.placeimg3.visibility = View.GONE
            binding.menu4.visibility = View.GONE
            binding.place4.visibility = View.GONE
            //binding.placeimg4.visibility = View.GONE
        }


        else if (stores_count == 2) {
            //place
            binding.place1.text = request?.stores?.get(0)?.place?.placeName
            binding.place2.text = request?.stores?.get(1)?.place?.placeName

            //menu
            binding.menu1.text = request?.stores?.get(0)?.menu
            binding.menu2.text = request?.stores?.get(1)?.menu

            //destination
            binding.destination.text = request?.destination?.placeName
            //deliverycharge
            binding.deliverycharge.text = request?.deliveryCharge.toString()

            binding.menu3.visibility = View.GONE
            binding.place3.visibility = View.GONE
            //binding.placeimg3.visibility = View.GONE
            binding.menu4.visibility = View.GONE
            binding.place4.visibility = View.GONE
            // binding.placeimg4.visibility = View.GONE

        } else if (stores_count == 3) {
            //place
            binding.place1.text = request?.stores?.get(0)?.place?.placeName
            binding.place2.text = request?.stores?.get(1)?.place?.placeName
            binding.place3.text = request?.stores?.get(2)?.place?.placeName

            //menu
            binding.menu1.text = request?.stores?.get(0)?.menu
            binding.menu2.text = request?.stores?.get(1)?.menu
            binding.menu3.text = request?.stores?.get(2)?.menu
            //destination
            binding.destination.text = request?.destination?.placeName
            //deliverycharge
            binding.deliverycharge.text = request?.deliveryCharge.toString()

            binding.menu4.visibility = View.GONE
            binding.place4.visibility = View.GONE
            // binding.placeimg4.visibility = View.GONE


        }

        else {
            //place
            binding.place1.text = request?.stores?.get(0)?.place?.placeName
            binding.place2.text = request?.stores?.get(1)?.place?.placeName
            binding.place3.text = request?.stores?.get(2)?.place?.placeName
            binding.place4.text = request?.stores?.get(3)?.place?.placeName

            //menu
            binding.menu1.text = request?.stores?.get(0)?.menu
            binding.menu2.text = request?.stores?.get(1)?.menu
            binding.menu3.text = request?.stores?.get(2)?.menu
            binding.menu4.text = request?.stores?.get(3)?.menu

            //destination
            binding.destination.text = request?.destination?.placeName
            //deliverycharge
            binding.deliverycharge.text = request?.deliveryCharge.toString()
        }


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