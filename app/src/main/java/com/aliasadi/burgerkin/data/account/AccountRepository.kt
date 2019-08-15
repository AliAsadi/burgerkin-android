package com.aliasadi.burgerkin.data.account

import com.aliasadi.burgerkin.App
import com.aliasadi.burgerkin.data.model.Account
import com.aliasadi.burgerkin.data.model.Wallet
import com.facebook.Profile
import com.google.gson.Gson
import kin.sdk.KinClient
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

class AccountRepository(private val kinClient: KinClient) {

    private val TAG: String = "MainActivity"

        private val URL_CREATE_ACCOUNT: String = "https://kinburger.herokuapp.com/users/login?public_key=%s"
//    private val URL_CREATE_ACCOUNT: String = "https://burgerkin-py-server.herokuapp.com/login?public_key=%s"

    interface AccountCallback {
        fun accountCreated(masterWallet: Wallet, account: Account)
        fun onError(e: IOException)
    }

    fun createAccount(callback: AccountCallback) {
        val kinAccount = if (kinClient.hasAccount()) {
            kinClient.getAccount(0)
        } else {
            kinClient.addAccount()
        }

        val request = okhttp3.Request.Builder()
            .url(String.format(URL_CREATE_ACCOUNT, kinAccount.publicAddress))
            .get()
            .build()

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        okHttpClient.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val json = response.body?.string()
                    val masterWallet = Gson().fromJson<Wallet>(json, Wallet::class.java)

                    val profile = Profile.getCurrentProfile()
                    val account = Account(profile.id, profile.firstName, kinAccount.publicAddress!!)

                    App.instance.setAccount(account)

                    callback.accountCreated(masterWallet, account)
                }
            })
    }
}
