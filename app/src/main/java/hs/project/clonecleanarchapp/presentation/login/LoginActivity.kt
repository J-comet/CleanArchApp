package hs.project.clonecleanarchapp.presentation.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import hs.project.clonecleanarchapp.R
import hs.project.clonecleanarchapp.data.common.utils.WrappedResponse
import hs.project.clonecleanarchapp.data.login.remote.dto.LoginRequest
import hs.project.clonecleanarchapp.data.login.remote.dto.LoginResponse
import hs.project.clonecleanarchapp.databinding.ActivityLoginBinding
import hs.project.clonecleanarchapp.domain.common.login.entity.LoginEntity
import hs.project.clonecleanarchapp.infra.SharedPrefs
import hs.project.clonecleanarchapp.presentation.common.extension.isEmail
import hs.project.clonecleanarchapp.presentation.common.extension.showGenericAlertDialog
import hs.project.clonecleanarchapp.presentation.common.extension.showToast
import hs.project.clonecleanarchapp.presentation.main.MainActivity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val vm by viewModels<LoginViewModel>()

    @Inject
    lateinit var pref: SharedPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        login()
        observe()
    }

    private fun observe() {
        vm.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleState(state) }
            .launchIn(lifecycleScope)
    }

    private fun handleState(state: LoginActivityState) {
        when(state) {
            is LoginActivityState.ShowToast -> showToast(state.message)
            is LoginActivityState.IsLoading -> handleLoading(state.isLoading)
            is LoginActivityState.Init -> Unit
            is LoginActivityState.ErrorLogin -> handleErrorLogin(state.rawResponse)
            is LoginActivityState.SuccessLogin -> handleSuccessLogin(state.loginEntity)
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        binding.btnLogin.isEnabled = !isLoading
        binding.progressHorizontal.isIndeterminate = isLoading
        if (!isLoading){
            binding.progressHorizontal.progress = 0
        }
    }

    private fun handleSuccessLogin(loginEntity: LoginEntity) {
        pref.saveToken(loginEntity.token)
        goToMainActivity()
    }

    private fun handleErrorLogin(rawResponse: WrappedResponse<LoginResponse>) {
        showGenericAlertDialog(rawResponse.message)
    }

    private fun goToMainActivity() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }

    private fun login() {
        binding.btnLogin.setOnClickListener {
            val email = binding.tieEmail.text.toString().trim()
            val pw = binding.tiePw.text.toString().trim()

            if (validate(email, pw)) {
                vm.login(LoginRequest(email,pw))
            }
        }
    }

    private fun setEmailError(e: String?) {
        binding.tiEmail.error = e
    }

    private fun setPasswordError(e: String?) {
        binding.tiPw.error = e
    }

    private fun resetAllError(){
        setEmailError(null)
        setPasswordError(null)
    }

    private fun validate(email: String, password: String) : Boolean {
        resetAllError()
        if (!email.isEmail()) {
            setPasswordError(getString(R.string.error_email_not_valid))
            return false
        }
        if (password.length < 0) {
            setPasswordError(getString(R.string.error_password_not_valid))
            return false
        }
        return true
    }

}