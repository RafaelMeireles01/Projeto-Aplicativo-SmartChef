package com.example.smartchef

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.smartchef.databinding.ActivityMainBinding
import model.Usuario
import manager.UsuarioManager
import java.io.IOException
import java.security.GeneralSecurityException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var encryptedSharedPreferences: EncryptedSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura SharedPreferences criptografado
        try {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

            encryptedSharedPreferences = EncryptedSharedPreferences.create(
                "auth_prefs",
                masterKeyAlias,
                this,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            ) as EncryptedSharedPreferences
        } catch (e: GeneralSecurityException) {
            showErrorAndFinish("Erro de segurança ao configurar armazenamento")
        } catch (e: IOException) {
            showErrorAndFinish("Erro de IO ao configurar armazenamento")
        }

        checkUserLoggedIn()

        binding.btnCadastrar.setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }

        binding.btnEntrar.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val senha = binding.senha.text.toString().trim()

            if (validarCampos(email, senha)) {
                autenticarUsuario(email, senha)
            }
        }
    }

    private fun showErrorAndFinish(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    }

    private fun checkUserLoggedIn() {
        val userEmail = encryptedSharedPreferences.getString("user_email", null)
        if (userEmail != null) {
            startActivity(Intent(this, IngredientesActivity::class.java))

        }
    }

    private fun validarCampos(email: String, senha: String): Boolean {
        if (email.isEmpty()) {
            binding.email.error = "Por favor, insira seu e-mail"
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.email.error = "Por favor, insira um e-mail válido"
            return false
        }

        if (senha.isEmpty()) {
            binding.senha.error = "Por favor, insira sua senha"
            return false
        }

        if (senha.length < 6) {
            binding.senha.error = "A senha deve ter pelo menos 6 caracteres"
            return false
        }

        return true
    }

    private fun autenticarUsuario(email: String, senha: String) {
        val usuario = UsuarioManager.autenticar(email, senha)

        if (usuario != null) {
            encryptedSharedPreferences.edit()
                .putString("user_email", email)
                .apply()

            startActivity(Intent(this, IngredientesActivity::class.java))

        } else {
            Toast.makeText(
                this,
                "Autenticação falhou: E-mail ou senha incorretos",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}