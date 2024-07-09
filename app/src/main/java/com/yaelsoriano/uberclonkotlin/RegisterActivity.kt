package com.yaelsoriano.uberclonkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.yaelsoriano.uberclonkotlin.databinding.ActivityLoginBinding
import com.yaelsoriano.uberclonkotlin.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        binding.goToLoginBtn.setOnClickListener { goToLogin() }
        binding.registerBtn.setOnClickListener { register() }
    }

    private fun goToLogin(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun register(){
        val name = binding.nameEditTxt.text.toString()
        val lastName = binding.lastNameEditTxt.text.toString()
        val phoneNumber = binding.phoneNumberEditTxt.text.toString()
        val email = binding.emailEditTxt.text.toString()
        val pass = binding.passEditTxt.text.toString()
        val confirmPass = binding.confirmPassEditTxt.text.toString()

        if (!isValidForm(name, lastName, phoneNumber, email, pass, confirmPass)) { Toast.makeText(this, "No puedes dejar ningún campo vacío", Toast.LENGTH_LONG).show() }
    }

    private fun isValidForm(name: String, lastName: String, phoneNumber: String, email: String, pass: String, confirmPass: String): Boolean {
        if (name.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
            return false
        }

        if (pass.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_LONG).show()
            return false
        }

        if (pass != confirmPass) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }
}