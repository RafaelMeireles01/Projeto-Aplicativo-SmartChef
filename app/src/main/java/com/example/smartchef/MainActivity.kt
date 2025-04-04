package com.example.smartchef

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.smartchef.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var encryptedSharedPreferences: EncryptedSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa o Firebase Auth
        auth = Firebase.auth

        // Configura SharedPreferences criptografado
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        encryptedSharedPreferences = EncryptedSharedPreferences.create(
            "auth_prefs",
            masterKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as EncryptedSharedPreferences

        // Verifica se usuário já está logado
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
            // Implemente a recuperação de senha se necessário
            startActivity(Intent(this, RecuperarSenhaActivity::class.java))
        }
    }

    private fun checkUserLoggedIn() {
        val userToken = encryptedSharedPreferences.getString("user_token", null)
        if (userToken != null) {
            startActivity(Intent(this, IngredientesActivity::class.java))
            finish()
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
        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login bem-sucedido
                    val user = auth.currentUser
                    user?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
                        if (tokenTask.isSuccessful) {
                            val token = tokenTask.result?.token
                            // Armazena o token JWT de forma segura
                            encryptedSharedPreferences.edit()
                                .putString("user_token", token)
                                .putString("user_email", email)
                                .apply()

                            // Redireciona para a tela principal
                            startActivity(Intent(this, IngredientesActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Falha ao obter token: ${tokenTask.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    // Se o login falhar, exibe uma mensagem para o usuário
                    Toast.makeText(
                        this,
                        "Autenticação falhou: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}