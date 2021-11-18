package com.santaistiger.gomourdeliveryapp.data.model

import android.os.Parcelable
import com.santaistiger.gomourdeliveryapp.data.model.Place
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Store(
        var place: Place = Place(),
        var menu: String? = null,
        var cost: Int? = null
) : Parcelable
