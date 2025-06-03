package com.example.todolist

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ConexaoDB(context: Context) : SQLiteOpenHelper(context, "TarefaDB", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE Tarefa(id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT, descricao TEXT, prioridade TEXT, data DATE)")
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Tarefa")
        onCreate(db)
    }
    fun inserir(titulo: String, descricao: String, prioridade: String, data: String): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("titulo", titulo)
            put("descricao", descricao)
            put("prioridade", prioridade)
            put("data", data)
        }
        return db.insert("Tarefa", null, valores) != -1L
    }
    fun listar(): List<String> {
        val lista = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Tarefa", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"))
                val descricao = cursor.getString(cursor.getColumnIndexOrThrow("descricao"))
                val prioridade = cursor.getString(cursor.getColumnIndexOrThrow("prioridade"))
                val data = cursor.getString(cursor.getColumnIndexOrThrow("data"))
                lista.add("ID: $id = Título: $titulo\nDescrição: $descricao\nPrioridade: $prioridade\nData para finalizar: $data")
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }
    fun atualizar(id: Int, novoTitulo: String, novaDescricao: String, novaPrioridade: String, novaData: String): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("titulo", novoTitulo)
            put("descricao", novaDescricao)
            put("prioridade", novaPrioridade)
            put("data", novaData)
        }
        val linhasAfetadas = db.update("Tarefa", valores, "id = ?", arrayOf(id.toString()))
        return linhasAfetadas > 0
    }
    fun excluir(id: Int): Boolean {
        val db = writableDatabase
        val linhasAfetadas = db.delete("Tarefa", "id = ?", arrayOf(id.toString()))
        return linhasAfetadas > 0
    }

    fun buscarPorId(id: Int): Map<String, String>? {
        val db = readableDatabase
        val cursor = db.query(
            "Tarefa", null, "id = ?", arrayOf(id.toString()),
            null, null, null
        )
        return  if (cursor.moveToFirst()) {
            val resultado = mutableMapOf<String, String>()
            resultado["id"] = id.toString()
            resultado["titulo"] = cursor.getString(cursor.getColumnIndexOrThrow("titulo"))
            resultado["descricao"] = cursor.getString(cursor.getColumnIndexOrThrow("descricao"))
            resultado["prioridade"] = cursor.getString(cursor.getColumnIndexOrThrow("prioridade"))
            resultado["data"] = cursor.getString(cursor.getColumnIndexOrThrow("data"))
            cursor.close()
            resultado
        } else {
            cursor.close()
            null
        }
    }
}