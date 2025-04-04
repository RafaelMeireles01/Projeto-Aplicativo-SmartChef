package com.example.smartchef

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ReceitasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receitas)

        val backButton = findViewById<Button>(R.id.btn_back)

        // Voltar para a tela de ingredientes
        backButton.setOnClickListener {
            finish()
        }
    }
}