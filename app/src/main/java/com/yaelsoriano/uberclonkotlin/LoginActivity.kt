package com.yaelsoriano.uberclonkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.yaelsoriano.uberclonkotlin.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        binding.goToRegisterBtn.setOnClickListener { goToRegister() }
        binding.loginBtn.setOnClickListener { login() }
    }

    private fun goToRegister(){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun login(){
        val email = binding.emailEditTxt.text.toString()
        val pass = binding.passEditTxt.text.toString()

        if (!isValidForm(email, pass)){ Toast.makeText(this, "No puedes dejar ningún campo vacío", Toast.LENGTH_LONG).show() }
    }

    private fun isValidForm(email: String, pass: String): Boolean {
        if (email.isEmpty() || pass.isEmpty()) {
            return false
        }

        return true
    }
}