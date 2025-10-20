package com.example.ppb2_anas.usecase

import com.example.ppb2_anas.entity.Todo
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class TodoUseCase {
    private val db = Firebase.firestore
    private val collectionName = "todo" // pastikan konsisten huruf kecil semua

    suspend fun getTodo(): List<Todo> {
        return try {
            val data = db.collection("to doo")
                .get()
                .await()

            // Cek apakah data kosong
            if (data.isEmpty) {
                throw Exception("Gaono data nek server")
            }

            // Ubah setiap dokumen menjadi objek Todo
            data.documents.map {
                Todo(
                    id = it.id,
                    title = it.getString("title").orEmpty(),
                    description = it.getString("description").orEmpty()
                )
            }

        } catch (exc: Exception) {
            // Bisa juga return emptyList() jika kamu ingin aplikasi tetap jalan
            throw Exception("Error mengambil data: ${exc.message}")
        }
    }

    suspend fun createTodo(todo: Todo): Todo{
        try {
            val payload = hashMapOf(
                "title" to todo.title,
                "description" to todo.description
            )

            val data = db.collection(collectionName)
                .add(payload)
                .await()

            return todo.copy(id = data.id)
        } catch (exc:Exception) {
            throw Exception("Gagal menyimpan data ke firestore")
        }
    }

    suspend fun deleteTodo(id: String) {
        try {
            db.collection("to doo")
                .document(id)
                .delete()
                .await()
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    suspend fun updateTodo(todo: Todo) {
        val payload = hashMapOf(
            "title" to todo.title,
            "description" to todo.description
        )

        try {
            db.collection("to doo")
                .document(todo.id)
                .set(payload)
                .await()
        } catch (exc: Exception) {
            throw Exception("Gagal memperbarui todo: ${exc.message}")
        }
    }

    suspend fun getTodo(id: String): Todo? {
        val data = db.collection("to doo")
            .document(id)
            .get()
            .await()

        if (data.exists()) {
            return Todo(
                id = data.id,
                title = data.getString("title").toString(),
                description = data.getString("description").toString()
            )
        }

        return null
    }
}
