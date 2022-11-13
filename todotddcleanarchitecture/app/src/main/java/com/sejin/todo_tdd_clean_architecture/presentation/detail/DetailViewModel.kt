package com.sejin.todo_tdd_clean_architecture.presentation.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sejin.todo_tdd_clean_architecture.data.entity.ToDoEntity
import com.sejin.todo_tdd_clean_architecture.domain.todo.DeleteToDoItemUseCase
import com.sejin.todo_tdd_clean_architecture.domain.todo.GetTodoItemUseCase
import com.sejin.todo_tdd_clean_architecture.domain.todo.InsertToDoUseCase
import com.sejin.todo_tdd_clean_architecture.domain.todo.UpdateToDoUseCase
import com.sejin.todo_tdd_clean_architecture.presentation.BaseViewModel
import com.sejin.todo_tdd_clean_architecture.presentation.list.ToDoListState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class DetailViewModel(
    var detailMode: DetailMode,
    var id: Long = -1,
    private val getTodoItemUseCase: GetTodoItemUseCase,
    private val deleteTodoItemUseCase: DeleteToDoItemUseCase,
    private val updateTodoItemUseCase: UpdateToDoUseCase,
    private val insertToDoUseCase: InsertToDoUseCase
) : BaseViewModel() {
    private val _toDoDetailLiveData =
        MutableLiveData<ToDoDetailState>(ToDoDetailState.UnInitialized)
    val todoDetailLiveData = _toDoDetailLiveData

    override fun fetchData(): Job = viewModelScope.launch {


        when (detailMode) {
            DetailMode.WRITE -> {

            }
            DetailMode.DETAIL -> {
                _toDoDetailLiveData.postValue(ToDoDetailState.Loading)
                try {
                    getTodoItemUseCase(id)?.let {
                        _toDoDetailLiveData.postValue(ToDoDetailState.Success(it))
                    } ?: run {
                        _toDoDetailLiveData.postValue(ToDoDetailState.Error)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _toDoDetailLiveData.postValue(ToDoDetailState.Error)
                }
            }
        }
    }

    fun deleteTodo() = viewModelScope.launch {
        _toDoDetailLiveData.postValue(ToDoDetailState.Loading)
        try {
            val result = deleteTodoItemUseCase(id)
            if (result) {
                _toDoDetailLiveData.postValue(ToDoDetailState.Delete)
            } else {
                _toDoDetailLiveData.postValue(ToDoDetailState.Error)
            }
        } catch (e: Exception) {
            _toDoDetailLiveData.postValue((ToDoDetailState.Error))
        }

    }

    fun writeToDo(title: String, description: String) = viewModelScope.launch {
        _toDoDetailLiveData.postValue(ToDoDetailState.Loading)
        when (detailMode) {
            DetailMode.WRITE -> {
                try {
                    val toDoEntity = ToDoEntity(
                        title = title,
                        description = description
                    )
                    id = insertToDoUseCase(toDoEntity)
                    _toDoDetailLiveData.postValue(ToDoDetailState.Success(toDoEntity))
                    detailMode = DetailMode.DETAIL


                } catch (e: Exception) {
                    _toDoDetailLiveData.postValue(ToDoDetailState.Error)
                }
            }
            DetailMode.DETAIL -> {
                try {
                    getTodoItemUseCase(id)?.let {
                        val updatedToDoEntity = it.copy(title = title, description = description)
                        updateTodoItemUseCase(updatedToDoEntity)
                        _toDoDetailLiveData.postValue(ToDoDetailState.Success(updatedToDoEntity))
                    } ?: run {
                        _toDoDetailLiveData.postValue(ToDoDetailState.Error)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _toDoDetailLiveData.postValue(ToDoDetailState.Error)
                }
            }
        }
    }
}