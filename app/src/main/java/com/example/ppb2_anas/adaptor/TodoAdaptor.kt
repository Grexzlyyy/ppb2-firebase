package com.example.ppb2_anas.adaptor

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ppb2_anas.databinding.KotakBinding
import com.example.ppb2_anas.entity.Todo

class TodoAdaptor (
    private val dataset: MutableList<Todo>,
    private val events: TodoItemEvents,

): RecyclerView.Adapter<TodoAdaptor.CustomViewHolder>() {

    interface TodoItemEvents {
        fun onDelete(todo: Todo)
        fun onEdit(todo: Todo)
    }

    inner class CustomViewHolder(
        val view: KotakBinding
    ): RecyclerView.ViewHolder(view.root) {

            fun binData(item: Todo) {
                view.title.text = item.title
                view.description.text = item.description

                view.root.setOnLongClickListener {
                    events.onDelete(todo =item)
                    true
                }
                view.root.setOnClickListener {
                    events.onEdit()
                }
            }
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomViewHolder {
        val binding = KotakBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CustomViewHolder,
        position: Int
    ){
        val data = dataset[position]
        holder.binData(data)
    }


    override fun getItemCount(): Int {
        return dataset.size
    }
    @SuppressLint("notifyDataSetChanged")
    fun updateData(newData: List<Todo>) {
        dataset.clear()
        dataset.addAll(newData)
        notifyDataSetChanged()


    }
}