package com.aliasadi.burgerkin.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.aliasadi.burgerkin.App
import com.aliasadi.burgerkin.data.account.AccountRepository
import com.aliasadi.burgerkin.R
import com.aliasadi.burgerkin.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : AppCompatActivity(), LoginContract.View {



    private lateinit var presenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        val accountRepository = AccountRepository(App.instance.getKinClient())
        presenter = LoginPresenter(this, accountRepository)
        presenter.login()

    }

    override fun startGameActivity() {
        MainActivity.start(this)
        finish()
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        presenter.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}
