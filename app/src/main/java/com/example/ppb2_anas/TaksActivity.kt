package com.example.ppb2_anas

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ppb2_anas.adaptor.TodoAdaptor
import com.example.ppb2_anas.databinding.ActivityTaksBinding
import com.example.ppb2_anas.entity.Todo
import com.example.ppb2_anas.usecase.TodoUseCase
import kotlinx.coroutines.launch

class TaksActivity : AppCompatActivity() {
    private lateinit var activityTaksBinding: ActivityTaksBinding
    private lateinit var todoAdapter: TodoAdaptor
    private lateinit var todoUseCase: TodoUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        activityTaksBinding = ActivityTaksBinding.inflate(layoutInflater)
        todoUseCase = TodoUseCase()
        setContentView(activityTaksBinding.root)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        initializeData()
        registerEvents()
    }

    fun registerEvents() {
        activityTaksBinding.tombolTambah.setOnClickListener {
            toCreateTodoPage()
        }
    }

    fun setupRecyclerView() {
        todoAdapter = TodoAdaptor(mutableListOf(), object : TodoAdaptor.TodoItemEvents {
            override fun onEdit(todo: Todo) {
                val intent = Intent(this@TaksActivity, EditTodoActivity::class.java)
                intent.putExtra("to_do_item_id", todo.id)
                startActivity(intent)
            }

            override fun onDelete(todo: Todo) {
                val builder = AlertDialog.Builder(this@TaksActivity)
                builder.setTitle("Konfirmasi Hapus Data")
                builder.setMessage("Apakah anda yakin mau menghapus data?")

                builder.setPositiveButton("Yo") {dialog, _ ->
                    lifecycleScope.launch {
                        try {
                           todoUseCase.deleteTodo(todo.id)

                            Toast.makeText(
                                this@TaksActivity,
                                "Data berhasil dihapus",
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (exc: Exception) {
                            displayMessage("Gagal menghapus data : ${exc.message}")
                        }
                        initializeData()
                    }
                }

                builder.setNeutralButton("Ora sido") {dialog, _ ->
                    dialog.dismiss()
                }

                val dialog = builder.create()
                dialog.show()
            }
        })
        activityTaksBinding.container.adapter = todoAdapter
        activityTaksBinding.container.layoutManager = LinearLayoutManager(this)
    }

    fun initializeData() {
        activityTaksBinding.container.visibility = View.GONE
        activityTaksBinding.loading.visibility = View.VISIBLE

        lifecycleScope.launch {
            val data = todoUseCase.getTodo()
            activityTaksBinding.container.visibility = View.VISIBLE
            activityTaksBinding.loading.visibility = View.GONE
            todoAdapter.updateData(data)
        }
    }

    fun toCreateTodoPage() {
        val intent = Intent(this, CreateTodoActivity::class.java)
        startActivity(intent)
        finish()

    }

    fun displayMessage(message: String) {
        Toast.makeText(this@TaksActivity, message, Toast.LENGTH_SHORT).show()
    }
}