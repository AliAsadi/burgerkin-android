package com.aliasadi.burgerkin.data.model

class Account(
    val id: String,
    val name: String,
    val publicAddress: String
) {
    override fun toString(): String {
        return "Account(id='$id', name='$name', publicAddress='$publicAddress')"
    }
}
