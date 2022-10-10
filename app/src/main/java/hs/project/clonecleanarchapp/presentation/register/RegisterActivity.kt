package hs.project.clonecleanarchapp.presentation.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import hs.project.clonecleanarchapp.R
import hs.project.clonecleanarchapp.data.common.utils.WrappedResponse
import hs.project.clonecleanarchapp.data.register.remote.dto.RegisterRequest
import hs.project.clonecleanarchapp.data.register.remote.dto.RegisterResponse
import hs.project.clonecleanarchapp.databinding.ActivityRegisterBinding
import hs.project.clonecleanarchapp.domain.common.register.entity.RegisterEntity
import hs.project.clonecleanarchapp.infra.SharedPrefs
import hs.project.clonecleanarchapp.presentation.common.extension.isEmail
import hs.project.clonecleanarchapp.presentation.common.extension.showGenericAlertDialog
import hs.project.clonecleanarchapp.presentation.common.extension.showToast
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var prefs : SharedPrefs

    private val vm by viewModels<RegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        signUp()
        observe()
        goBack()
    }

    private fun goBack() {
        binding.backButton.setOnClickListener { finish() }
    }

    private fun observe() {
        vm.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleState(state) }
            .launchIn(lifecycleScope)
    }

    private fun handleState(state: RegisterActivityState) {
        when(state) {
            is RegisterActivityState.Init -> Unit
            is RegisterActivityState.IsLoading -> handleLoading(state.isLoading)
            is RegisterActivityState.ShowToast -> showToast(state.message)
            is RegisterActivityState.SuccessRegister -> handleSuccessRegister(state.registerEntity)
            is RegisterActivityState.ErrorRegister -> handleErrorRegister(state.rawResponse)
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        binding.loadingProgressBar.isIndeterminate = isLoading
        binding.registerButton.isEnabled = !isLoading
        binding.backButton.isEnabled = !isLoading
        if (!isLoading){
            binding.loadingProgressBar.progress = 0
        }
    }

    private fun handleSuccessRegister(registerEntity: RegisterEntity) {
        prefs.saveToken(registerEntity.token)
        setResult(RESULT_OK)
        finish()
    }

    private fun handleErrorRegister(rawResponse: WrappedResponse<RegisterResponse>) {
        showGenericAlertDialog(rawResponse.message)
    }

    private fun signUp(){
        binding.registerButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (validate(name, email, password)) {
                vm.register(RegisterRequest(name, email, password))
            }
        }
    }

    private fun validate(name: String, email: String, password: String) : Boolean {
        resetAllError()
        if (name.isEmpty()) {
            setNameError(getString(R.string.error_name_not_valid))
            return false
        }
        if (!email.isEmail()) {
            setEmailError(getString(R.string.error_email_not_valid))
            return false
        }
        if (password.length < 0) {
            setPasswordError(getString(R.string.error_password_not_valid))
            return false
        }
        return true
    }

    private fun setNameError(e: String?) {
        binding.nameInput.error = e
    }

    private fun setEmailError(e: String?) {
        binding.emailInput.error = e
    }

    private fun setPasswordError(e: String?) {
        binding.passwordInput.error = e
    }

    private fun resetAllError() {
        setNameError(null)
        setEmailError(null)
        setPasswordError(null)
    }
}