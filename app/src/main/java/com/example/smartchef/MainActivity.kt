package com.example.smartchef

import android.content.Intent
import android.view.View
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

        binding.btnEsqueciASenha.setOnClickListener {
            showRecoveryForm()
        }
    }

    private fun showRecoveryForm() {
        binding.recoveryForm.visibility = View.VISIBLE

        binding.btnRecoverySubmit.setOnClickListener {
            val email = binding.recoveryEmail.text.toString().trim()
            val telefone = binding.recoveryPhone.text.toString().trim()

            if (email.isEmpty()) {
                binding.recoveryEmail.error = "Por favor, insira seu e-mail"
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.recoveryEmail.error = "Por favor, insira um e-mail válido"
                return@setOnClickListener
            }

            processRecoveryRequest(email, telefone)
        }

        binding.btnRecoveryCancel.setOnClickListener {
            binding.recoveryForm.visibility = View.GONE
            binding.recoveryEmail.text.clear()
            binding.recoveryPhone.text.clear()
        }
    }

    private fun processRecoveryRequest(email: String, telefone: String) {
        val usuario = UsuarioManager.getUsuarios().find { it.email == email }

        if (usuario == null) {
            Toast.makeText(this, "E-mail não encontrado em nosso cadastro", Toast.LENGTH_SHORT).show()
            return
        }

        val telefoneCorresponde = telefone.isNotEmpty() && usuario.telefone == telefone

        if (telefoneCorresponde) {
            sendRecoveryEmail(usuario.email)
            sendRecoverySMS(usuario.telefone)
            Toast.makeText(this, "Instruções enviadas para seu e-mail e telefone", Toast.LENGTH_SHORT).show()
        } else {
            sendRecoveryEmail(usuario.email)
            Toast.makeText(this, "Instruções enviadas para seu e-mail", Toast.LENGTH_SHORT).show()
        }

        binding.recoveryForm.visibility = View.GONE
        binding.recoveryEmail.text.clear()
        binding.recoveryPhone.text.clear()
    }

    private fun sendRecoveryEmail(email: String) {
        println("Email enviado para $email com instruções para redefinir senha")
        // Implementação real iria aqui
    }

    private fun sendRecoverySMS(telefone: String) {
        println("SMS enviado para $telefone com instruções para redefinir senha")
        // Implementação real iria aqui
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