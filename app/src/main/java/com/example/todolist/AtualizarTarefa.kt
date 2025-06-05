package com.example.todolist

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class AtualizarTarefa : AppCompatActivity() {
    private lateinit var db: ConexaoDB
    private var dataSelecionadaMillis: Long = 0L
    private var tarefaId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_atualizar_tarefa)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = ConexaoDB(this)

        // Referências dos componentes
        val editId = findViewById<EditText>(R.id.editId)
        val editTitulo = findViewById<EditText>(R.id.editTitulo)
        val editDescricao = findViewById<EditText>(R.id.editDescricao)
        val chipGroupPrioridade = findViewById<ChipGroup>(R.id.chipGroupPrioridade)
        val calendarioView = findViewById<CalendarView>(R.id.calendarioView)

        // Recebe o ID vindo da intent
        tarefaId = intent.getIntExtra("id", -1)
        if (tarefaId == -1) {
            Toast.makeText(this, "Erro ao carregar a tarefa.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Busca os dados no banco
        val tarefa = db.buscarPorId(tarefaId)
        if (tarefa != null) {
            editId.setText(tarefa["id"])
            editTitulo.setText(tarefa["titulo"])
            editDescricao.setText(tarefa["descricao"])

            when (tarefa["prioridade"]) {
                "Alta" -> chipGroupPrioridade.check(R.id.chipAlta)
                "Média" -> chipGroupPrioridade.check(R.id.chipMedia)
                "Baixa" -> chipGroupPrioridade.check(R.id.chipBaixa)
            }

            val formato = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale("pt", "BR"))
            val data = formato.parse(tarefa["data"])
            dataSelecionadaMillis = data?.time ?: calendarioView.date
            calendarioView.date = dataSelecionadaMillis
        }

        calendarioView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val cal = java.util.Calendar.getInstance()
            cal.set(year, month, dayOfMonth, 0, 0, 0)
            cal.set(java.util.Calendar.MILLISECOND, 0)
            dataSelecionadaMillis = cal.timeInMillis
        }

        val btnAtualizar = findViewById<Button>(R.id.btnAtualizar)
        btnAtualizar.setOnClickListener {
            val novoTitulo = editTitulo.text.toString().trim()
            val novaDescricao = editDescricao.text.toString().trim()

            val chipIdSelecionado = chipGroupPrioridade.checkedChipId
            if (chipIdSelecionado == -1) {
                AlertDialog.Builder(this)
                    .setTitle("Erro")
                    .setMessage("Por favor, selecione uma prioridade.")
                    .setPositiveButton("OK", null)
                    .show()
                return@setOnClickListener
            }

            val chipSelecionado = findViewById<Chip>(chipIdSelecionado)
            val novaPrioridade = chipSelecionado.text.toString()

            val novaData = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale("pt", "BR")).format(java.util.Date(dataSelecionadaMillis))

            if (novoTitulo.isEmpty() || novaDescricao.isEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle("Erro")
                    .setMessage("Preencha todos os campos.")
                    .setPositiveButton("OK", null)
                    .show()
                return@setOnClickListener
            }

            val sucesso = db.atualizar(tarefaId, novoTitulo, novaDescricao, novaPrioridade, novaData)
            val mediaPlayer = MediaPlayer.create(this, R.raw.caneta_escrevendo)

            if (sucesso) {
                mediaPlayer.start()
                mediaPlayer.setOnCompletionListener {
                    it.release() // Libera o recurso após tocar
                }
                Toast.makeText(this, "Tarefa atualizada com sucesso!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Erro ao atualizar a tarefa.", Toast.LENGTH_SHORT).show()
            }
            finish()
        }

        val btnCancelar = findViewById<Button>(R.id.btnCancelar)
        btnCancelar.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Cancelar")
                .setMessage("As alterações não serão salvas. Deseja continuar?")
                .setPositiveButton("Sim") { _, _ -> finish() }
                .setNegativeButton("Não", null)
                .show()
        }
    }
}