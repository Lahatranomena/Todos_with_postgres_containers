package com.example.todos.mapper;

import com.example.todos.dto.TodoRequest;
import com.example.todos.dto.TodoResponse;
import com.example.todos.model.Todo;

import java.util.UUID;

public class TodoMapper {

    public static Todo toEntity(TodoRequest request) {
        Todo todo = new Todo();
        todo.setId(request.getId() != null ? request.getId() : UUID.randomUUID().toString());
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setCompleted(request.getCompleted() != null ? request.getCompleted() : false);
        todo.setCreatedAt(request.getCreatedAt() != null ? request.getCreatedAt() : java.time.LocalDateTime.now());
        return todo;
    }

    public static TodoResponse toResponse(Todo todo) {
        TodoResponse response = new TodoResponse();
        response.setId(todo.getId());
        response.setTitle(todo.getTitle());
        response.setDescription(todo.getDescription());
        response.setCompleted(todo.isCompleted());
        response.setCreatedAt(todo.getCreatedAt());
        response.setUpdatedAt(todo.getUpdatedAt());
        return response;
    }
}