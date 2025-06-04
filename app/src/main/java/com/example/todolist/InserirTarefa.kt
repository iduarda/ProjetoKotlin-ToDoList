package com.example.todolist

import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.chip.ChipGroup

class InserirTarefa : AppCompatActivity() {
    private lateinit var db : ConexaoDB
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inserir_tarefa)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = ConexaoDB(this)
        val editTitulo = findViewById<EditText>(R.id.editTitulo)
        val editDescricao = findViewById<EditText>(R.id.editDescricao)
        val chipGroupPrioridade = findViewById<ChipGroup>(R.id.chipGroupPrioridade)
        val calendarioView = findViewById<CalendarView>(R.id.calendarioView)

        var dataSelecionadaMillis = calendarioView.date

        calendarioView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val cal = java.util.Calendar.getInstance()
            cal.set(year, month, dayOfMonth, 0, 0, 0)
            cal.set(java.util.Calendar.MILLISECOND, 0)
            dataSelecionadaMillis = cal.timeInMillis
        }

        val btnAdicionar = findViewById<Button>(R.id.btnAdicionar)
        btnAdicionar.setOnClickListener {
            val titulo = editTitulo.text.toString().trim()
            val descricao = editDescricao.text.toString().trim()

            // Obtem o ID do chip selecionado
            val chipIdSelecionado = chipGroupPrioridade.checkedChipId
            if (chipIdSelecionado == -1) {
                AlertDialog.Builder(this)
                    .setTitle("Erro")
                    .setMessage("Por favor, selecione uma prioridade.")
                    .setPositiveButton("OK", null)
                    .show()
                return@setOnClickListener
            }
            val chipSelecionado = findViewById<com.google.android.material.chip.Chip>(chipIdSelecionado)
            val prioridade = chipSelecionado.text.toString()

            // Pega a data selecionada no CalendarView
            val data = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale("pt", "BR")).format(java.util.Date(dataSelecionadaMillis))

            // Verifica se os campos obrigatórios estão preenchidos
            if (titulo.isEmpty() || descricao.isEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle("Erro")
                    .setMessage("Preencha todos os campos.")
                    .setPositiveButton("OK", null)
                    .show()
                return@setOnClickListener
            }

            // Insere no banco
            val sucesso = db.inserir(titulo, descricao, prioridade, data)

            if (sucesso) {
                // Exibe o Toast com a mensagem de sucesso
                Toast.makeText(this, "Tarefa adicionada com sucesso!", Toast.LENGTH_SHORT).show()
            } else {
                // Caso a inserção não tenha sido bem-sucedida
                Toast.makeText(this, "Erro ao adicionar a tarefa.", Toast.LENGTH_SHORT).show()
            }

            finish()
        }

        val btnVoltar = findViewById<Button>(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Cancelar")
                .setMessage("A tarefa não será salva. Deseja continuar?")
                .setPositiveButton("Sim") { dialog, which ->
                    finish()
                }
                .setNegativeButton("Não", null)
                .show()
        }
    }
}