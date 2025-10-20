package com.example.ppb2_anas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.ppb2_anas.databinding.ActivityEditTodoBinding
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
    }

    fun loadData(){
        lifecycleScope.launch {
            val todo = todoUseCase.getTodo(todoItemId)
            if (todo == null) {
                val intent = Intent(this@EditTodoActivity,TaksActivity::class.java)
                startActivity(intent)
                finish()
            }

            if (todo != null) {
                Log.d("TITLE", todo.title)
            }

            binding.title.setText(todo?.title)
            binding.description.setText(todo?.description)
        }
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }
}