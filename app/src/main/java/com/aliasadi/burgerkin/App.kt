package com.aliasadi.burgerkin

import android.app.Application
import android.util.Log
import com.aliasadi.burgerkin.data.model.Account
import kin.sdk.Environment
import kin.sdk.KinClient

class App : Application() {

    private lateinit var account: Account
    private lateinit var kinClient: KinClient

    override fun onCreate() {
        super.onCreate()
        instance = this

        kinClient = KinClient(this, Environment.TEST, "burk")

    }

    fun getKinClient(): KinClient {
        return kinClient
    }

    fun setAccount(account: Account) {
        this.account = account
    }

    fun getAccount(): Account {
        return account
    }

    companion object {
        lateinit var instance: App
            private set
    }
}
