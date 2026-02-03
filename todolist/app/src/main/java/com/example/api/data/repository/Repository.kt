package com.example.api.data.repository

import com.example.api.APIService
import com.example.api.Task
import com.example.api.data.local.TaskDao
import com.example.api.data.local.TaskEntity

class TaskRepository(
    private val api: APIService,
    private val dao: TaskDao
){

    suspend fun getTasks():List<Task>{
        return try{
            val remote=api.getTasks().data

            dao.insertAll(
                remote.map{
                    TaskEntity(
                        it.id, it.title, it.description,
                        it.status, it.priority,
                        it.dueDate, it.category
                    )
                }
            )

            remote
        }catch(e:Exception){
            // offline fallback
            dao.getAll().map{
                Task(
                    it.id,it.title,it.description,
                    it.status,it.priority,
                    it.dueDate,it.category,
                    "", "", emptyList(),
                    emptyList(), emptyList()
                )
            }
        }
    }

    suspend fun addTask(task:TaskEntity){
        dao.insert(task)
    }

    suspend fun deleteTask(id:Int){
        dao.deleteById(id)
        try{ api.deleteTask(id) }catch(_:Exception){}
    }
}
