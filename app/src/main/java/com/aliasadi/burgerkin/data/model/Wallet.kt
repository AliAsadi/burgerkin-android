package com.aliasadi.burgerkin.data.model

import com.google.gson.annotations.SerializedName

class Wallet {

    @SerializedName("wallet_address")
    var address: String? = null

    override fun toString(): String {
        return "Wallet(address=$address)"
    }

}
