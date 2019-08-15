package com.aliasadi.burgerkin.ui.login

import android.content.Intent

interface LoginContract {

    interface View {
        fun startGameActivity()
        fun showMessage(message: String)
        fun showLoading()
        fun hideLoading()
    }

    interface Presenter {
        fun login()
        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
        fun onDestroy()
    }

}
