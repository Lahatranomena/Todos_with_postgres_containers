package com.example.todos.service;

import com.example.todos.dto.TodoRequest;
import com.example.todos.dto.TodoResponse;
import com.example.todos.mapper.TodoMapper;
import com.example.todos.model.Todo;
import com.example.todos.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<TodoResponse> getAllTodos(Boolean completed) {
        List<Todo> todos = completed == null
                ? todoRepository.findAll()
                : todoRepository.findByCompleted(completed);

        return todos.stream()
                .map(TodoMapper::toResponse)
                .toList();
    }

    public TodoResponse getTodoById(String id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Todo not found with id: " + id));
        return TodoMapper.toResponse(todo);
    }

    public List<TodoResponse> saveTodos(List<TodoRequest> requests) {
        List<Todo> todos = requests.stream()
                .map(TodoMapper::toEntity)
                .toList();

        List<Todo> saved = todoRepository.saveAll(todos);

        return saved.stream()
                .map(TodoMapper::toResponse)
                .toList();
    }
}