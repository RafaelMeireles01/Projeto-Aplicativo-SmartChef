package com.example.smartchef

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CadastroActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        // Inicializa o Firebase Auth
        auth = FirebaseAuth.getInstance()

        val btnSalvar = findViewById<Button>(R.id.btnSalvar_cadastro)
        val nomeInput = findViewById<EditText>(R.id.nomeInput)
        val telefoneInput = findViewById<EditText>(R.id.telefoneInput)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val senhaInput = findViewById<EditText>(R.id.senhaInput)
        val preferenciaGroup = findViewById<RadioGroup>(R.id.preferenciaAlimentar)
        val experienciaGroup = findViewById<RadioGroup>(R.id.nivelExperiencia)

        btnSalvar.setOnClickListener {
            val nome = nomeInput.text.toString().trim()
            val telefone = telefoneInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val senha = senhaInput.text.toString().trim()

            // Obter seleção dos RadioGroups
            val preferenciaId = preferenciaGroup.checkedRadioButtonId
            val experienciaId = experienciaGroup.checkedRadioButtonId

            val preferencia = when(preferenciaId) {
                R.id.vegano -> "Vegano"
                R.id.vegetariano -> "Vegetariano"
                R.id.Carnivoro -> "Carnívoro"
                R.id.onivoro -> "Onívoro"
                else -> ""
            }

            val experiencia = when(experienciaId) {
                R.id.novato -> "Novato"
                R.id.intermediario -> "Intermediário"
                R.id.expert -> "Expert"
                else -> ""
            }

            if (validarCampos(nome, telefone, email, senha, preferencia, experiencia)) {
                cadastrarUsuario(nome, telefone, email, senha, preferencia, experiencia)
            }
        }
    }

    private fun validarCampos(
        nome: String,
        telefone: String,
        email: String,
        senha: String,
        preferencia: String,
        experiencia: String
    ): Boolean {
        if (nome.isEmpty()) {
            Toast.makeText(this, "Por favor, insira seu nome", Toast.LENGTH_SHORT).show()
            return false
        }

        if (telefone.isEmpty()) {
            Toast.makeText(this, "Por favor, insira seu telefone", Toast.LENGTH_SHORT).show()
            return false
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Por favor, insira seu e-mail", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Por favor, insira um e-mail válido", Toast.LENGTH_SHORT).show()
            return false
        }

        if (senha.isEmpty()) {
            Toast.makeText(this, "Por favor, insira sua senha", Toast.LENGTH_SHORT).show()
            return false
        }

        if (senha.length < 6) {
            Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return false
        }

        if (preferencia.isEmpty()) {
            Toast.makeText(this, "Por favor, selecione uma preferência alimentar", Toast.LENGTH_SHORT).show()
            return false
        }

        if (experiencia.isEmpty()) {
            Toast.makeText(this, "Por favor, selecione seu nível de experiência", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun cadastrarUsuario(
        nome: String,
        telefone: String,
        email: String,
        senha: String,
        preferencia: String,
        experiencia: String
    ) {
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Cadastro no Authentication bem-sucedido, agora salvar no Firestore
                    val userId = auth.currentUser?.uid ?: ""

                    val usuario = hashMapOf(
                        "nome" to nome,
                        "telefone" to telefone,
                        "email" to email,
                        "preferencia" to preferencia,
                        "experiencia" to experiencia
                        // Não salvamos a senha no Firestore por segurança
                    )

                    db.collection("usuarios")
                        .document(userId)
                        .set(usuario)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Erro ao salvar dados: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Falha no cadastro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}