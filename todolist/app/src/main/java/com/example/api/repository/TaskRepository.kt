package com.example.api.repository

import com.example.api.APIService
import com.example.api.Task
import com.example.api.data.local.TaskDao
import com.example.api.data.local.TaskEntity

// Cập nhật lại class Repository để nhận thêm apiService
class TaskRepository(
    private val dao: TaskDao,
    private val api: APIService
) {
    // Lấy dữ liệu từ Local DB
    suspend fun getAll(): List<TaskEntity> {
        return dao.getAll()
    }
    suspend fun refreshTasksFromApi() {
        try {
            val response = api.getTasks()
            if (response.isSuccess) {

                val entities = response.data.map { task ->
                    TaskEntity(
                        id = task.id, // ID từ API
                        title = task.title,
                        description = task.description,
                        status = task.status,
                        priority = task.priority,
                        dueDate = task.dueDate,
                        category = task.category,
                        subtasks = task.subtasks,
                        attachments = task.attachments
                    )
                }
                dao.insertAll(entities)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun insert(entity: TaskEntity) = dao.insert(entity)
    suspend fun deleteById(id: Int) = dao.deleteById(id)
}


