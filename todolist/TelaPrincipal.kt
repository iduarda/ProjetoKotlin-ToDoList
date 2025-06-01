package com.example.todolist

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todolist.R.id.listaTarefas

class TelaPrincipal : AppCompatActivity() {
    private lateinit var db : ConexaoDB
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_principal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = ConexaoDB(this)
        //val editId = findViewById<EditText>(R.id.editId)
        //val editNome = findViewById<EditText>(R.id.editNome)
        //val editIdade = findViewById<EditText>(R.id.editIdade)
        val lista = findViewById<ListView>(listaTarefas)

        val buttonInserir = findViewById<Button>(R.id.buttonInserir)
        buttonInserir.setOnClickListener {
            val intent = Intent(this, InserirTarefa::class.java)
            startActivity(intent)
        }

        val buttonAtualizar = findViewById<Button>(R.id.buttonAtualizar)
        buttonAtualizar.setOnClickListener {
            Toast.makeText(this, "Tarefa atualizada!", Toast.LENGTH_SHORT).show()
        }

        val buttonExcluir = findViewById<Button>(R.id.buttonExcluir)
        buttonExcluir.setOnClickListener {
            // Cria um EditText dentro do AlertDialog
            val input = EditText(this).apply {
                inputType = android.text.InputType.TYPE_CLASS_NUMBER
                hint = "Digite o ID da tarefa"
            }

            AlertDialog.Builder(this)
                .setTitle("Excluir Tarefa")
                .setMessage("Informe o ID da tarefa que deseja excluir:")
                .setView(input)
                .setPositiveButton("Excluir") { _, _ ->
                    val id = input.text.toString().toIntOrNull()
                    if (id != null) {
                        if (db.excluir(id)) {
                            Toast.makeText(this, "Tarefa excluída com sucesso!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "ID não encontrado.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "ID inválido.", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        val buttonExibir = findViewById<Button>(R.id.buttonExibir)
        buttonExibir.setOnClickListener {
            val dados = db.listar()
            lista.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dados)
        }
    }
}