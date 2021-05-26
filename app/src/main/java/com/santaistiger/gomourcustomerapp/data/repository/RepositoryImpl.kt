package com.santaistiger.gomourcustomerapp.data.repository


import android.util.Log
import android.widget.TextView
import com.google.firebase.database.Query
import com.santaistiger.gomourcustomerapp.data.model.Order
import com.santaistiger.gomourcustomerapp.data.model.OrderRequest
import com.santaistiger.gomourcustomerapp.data.model.Place
import com.santaistiger.gomourcustomerapp.data.network.database.AuthApi
import com.santaistiger.gomourcustomerapp.data.network.database.FireStoreApi
import com.santaistiger.gomourcustomerapp.data.network.database.RealtimeApi
import com.santaistiger.gomourcustomerapp.data.network.map.KakaoMapApi
import com.santaistiger.gomourcustomerapp.data.network.map.NaverMapApi
import com.santaistiger.gomourcustomerapp.data.network.map.asDomainModel
import com.santaistiger.gomourcustomerapp.data.network.map.getDistance
import com.santaistiger.gomourcustomerapp.ui.adapter.OrderListAdapter
import net.daum.mf.map.api.MapPoint

private const val TAG = "RepositoryImpl"

object RepositoryImpl : Repository {

    /**
     * 출발지, 배달 장소, 경유지를 매개변수로 받아서
     * 네이버 Directions API를 이용해서 최적 경로의 거리(단위: m)를 가져오는 함수
     */
    override suspend fun getDistance(start: String, goal: String): Int? {
        return getDistance(start, goal, null)
    }

    override suspend fun getDistance(start: String, goal: String, waypoints: String?): Int? {
        val jsonResponse =
            NaverMapApi.retrofitService.getDirections(start, goal, waypoints)
        Log.i(TAG, "jsonResponse : $jsonResponse")

        return jsonResponse.getDistance()
    }

    override suspend fun readOrderDetail(orderId: String): Order? {
        val response = RealtimeApi.readOrderDetail(orderId)
        return response.order
    }

    /**
     * 찾고싶은 장소명 또는 키워드로 장소를 찾는 함수
     * 현재의 지도의 중심점에서 거리순으로 15개의 Place를 리턴함
     */
    override suspend fun searchPlace(
        placeName: String,
        curMapPos: MapPoint.GeoCoordinate
    ): List<Place> {
        val kakaoMapProperty =
            KakaoMapApi.retrofitService.searchPlaces(
                placeName,
                curMapPos.longitude,
                curMapPos.latitude
            )

        Log.i(TAG, "kakaoMapProperty: $kakaoMapProperty")

        return kakaoMapProperty.documents.asDomainModel()
    }

    /**
     * orderRequest를 받아서 Firebase Realtime Database의 order_reqeust 테이블에 write
     * 이때, data key는 orderRequest의 orderId를 사용
     */
    override suspend fun writeOrderRequest(orderRequest: OrderRequest) {
        val key = orderRequest.orderId
        RealtimeApi.writeRequest(key, orderRequest)
    }

    override suspend fun readDeliveryManPhone(deliveryManUid: String): String? {
        val response = FireStoreApi.getDeliveryMan(deliveryManUid)
        val deliveryMan = response.deliveryMan

        return deliveryMan?.phone
    }

    override suspend fun readDeliveryManAccount(deliveryManUid: String): String? {
        val response = FireStoreApi.getDeliveryMan(deliveryManUid)
        val deliveryMan = response.deliveryMan

        return deliveryMan?.run {
            "${accountInfo?.bank} ${accountInfo?.account} $name"
        }
    }

    override fun getUid(): String = AuthApi.readUid()?:String()

    // realtime database의 order_request 테이블에서 인자로 받은 주문 번호에 해당하는 주문 정보를 받아와 해당 값을 반환한다.
    override fun readCurrentOrder(orderId: String): Query {
        val currentOrder = RealtimeApi.readCurrentOrder(orderId)
        return currentOrder
    }

    // realtime database에서 인자로 받은 주문 번호에 해당하는 주문 정보를 삭제한다.
    override fun deleteCurrentOrder(orderId: String) {
        RealtimeApi.deleteCurrentOrder(orderId)
    }

    // realtime database의 order 테이블에 있는 소비자의 주문 목록을 받아와 해당 값을 반환한다.
    override fun readOrderList(customerUid: String): Query {
        val orderList = RealtimeApi.readOrderList(customerUid)
        return orderList
    }
}