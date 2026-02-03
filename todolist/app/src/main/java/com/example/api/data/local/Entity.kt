package com.example.api.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.api.Attachment
import com.example.api.Subtask
import com.example.api.Task

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val status: String,
    val priority: String,
    val dueDate: String,
    val category: String,

    // Thêm 2 dòng này để Database lưu được list
    val subtasks: List<Subtask> = emptyList(),
    val attachments: List<Attachment> = emptyList()
)

fun TaskEntity.toTask() = Task(
    id = this.id,
    title = this.title,
    description = this.description,
    status = this.status,
    priority = this.priority,
    dueDate = this.dueDate,
    category = this.category,
    createdAt = "",
    updatedAt = "",

    subtasks = this.subtasks,
    attachments = this.attachments,
    reminders = emptyList()
)