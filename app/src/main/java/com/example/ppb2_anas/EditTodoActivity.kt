package com.example.ppb2_anas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.ppb2_anas.databinding.ActivityEditTodoBinding
import com.example.ppb2_anas.entity.Todo
import com.example.ppb2_anas.usecase.TodoUseCase
import kotlinx.coroutines.launch

class EditTodoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditTodoBinding
    private lateinit var todoItemId: String
    private lateinit var todoUseCase: TodoUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEditTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        todoItemId = intent.getStringExtra("to_do_item_id").toString()
        todoUseCase = TodoUseCase()
        registerEvents()
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }

    fun registerEvents() {
        binding.tombolEdit.setOnClickListener {
            lifecycleScope.launch {
                val title = binding.title.text.toString()
                val description = binding.description.text.toString()
                val payload = Todo(
                    id = todoItemId,
                    title = title,
                    description = description,
                )

                try {
                    todoUseCase.updateTodo(payload)
                    displayMessage("Berhasil memperbarui Data")
                    back()
                } catch (exc: Exception) {
                    displayMessage("Gagal memperbarui data task : ${exc.message}")
                }
            }
        }
    }

    fun loadData(){
        lifecycleScope.launch {
            val data = todoUseCase.getTodo(todoItemId)
            if (data == null) {
                displayMessage("Data Task yang akan di edit tidak tersedia di server")
                back()

            }

            binding.title.setText(data?.title)
            binding.description.setText(data?.description)
        }
    }

    fun back() {
        val intent = Intent(this,TaksActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun displayMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}