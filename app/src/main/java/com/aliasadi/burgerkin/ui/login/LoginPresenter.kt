package com.aliasadi.burgerkin.ui.login

import android.content.Intent
import android.util.Log
import com.aliasadi.burgerkin.data.socket.SocketManagerImpl
import com.aliasadi.burgerkin.data.account.AccountRepository
import com.aliasadi.burgerkin.data.model.Account
import com.aliasadi.burgerkin.data.model.Wallet
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import java.io.IOException

class LoginPresenter(
    private val view: LoginContract.View,
    private val accountRepository: AccountRepository
) : LoginContract.Presenter {

    val TAG = "MainActivity"

    private val callbackManager = CallbackManager.Factory.create()

    init {
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                createKinAccount()
                view.showLoading()
            }

            override fun onCancel() {
                view.showMessage("Canceled!")
                view.hideLoading()
            }

            override fun onError(error: FacebookException?) {
                view.showMessage("Error!!")
                view.hideLoading()
                Log.d(TAG, "onError(): " + error.toString())
            }
        })
    }

    override fun login() {
        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired

        if (isLoggedIn) {

            view.showLoading()
            createKinAccount()

            Log.d(TAG, "token: ${accessToken.token}")
            Log.d(TAG, "userId: ${accessToken.userId}")
        } else {
            view.hideLoading()
        }
    }

    private fun createKinAccount() {

        accountRepository.createAccount(object : AccountRepository.AccountCallback {
            override fun accountCreated(masterWallet: Wallet, account: Account) {
                Log.d(TAG, "accountCreated: masterWallet= $masterWallet account= $account")

                SocketManagerImpl.init(account)
                view.startGameActivity()
            }

            override fun onError(e: IOException) {
                Log.d(TAG, "onError:" + e.message)
                view.showMessage("create account failed")
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        LoginManager.getInstance().unregisterCallback(callbackManager)
    }
}

