package com.example.smartchef

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class IngredientesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredientes)

        val addButton = findViewById<Button>(R.id.btn_add_to_list)
        val recipeButton = findViewById<Button>(R.id.btn_random_recipes)
        val closeFormButton = findViewById<Button>(R.id.btn_close_form)
        val saveButton = findViewById<Button>(R.id.btn_save_ingredient)
        val formLayout = findViewById<RelativeLayout>(R.id.form_add_ingredient)

        // Exibir formulário ao clicar no botão "Adicionar à sua lista"
        addButton.setOnClickListener {
            formLayout.visibility = View.VISIBLE
        }

        // Fechar formulário ao clicar no botão "X"
        closeFormButton.setOnClickListener {
            formLayout.visibility = View.GONE
        }

        // Fechar formulário ao salvar ingrediente
        saveButton.setOnClickListener {
            formLayout.visibility = View.GONE
            // Aqui pode ser implementado o salvamento dos dados
        }

        // Navegar para a tela de receitas
        recipeButton.setOnClickListener {
            val intent = Intent(this, ReceitasActivity::class.java)
            startActivity(intent)
        }
    }
}