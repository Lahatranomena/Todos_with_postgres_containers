package com.example.todos.controller;

import com.example.todos.dto.TodoRequest;
import com.example.todos.dto.TodoResponse;
import com.example.todos.service.TodoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public List<TodoResponse> getAllTodos(
            @RequestParam(required = false) Boolean completed) {
        return todoService.getAllTodos(completed);
    }

    @GetMapping("/{id}")
    public TodoResponse getTodoById(@PathVariable String id) {
        return todoService.getTodoById(id);
    }

    @PutMapping
    public List<TodoResponse> saveTodos(@RequestBody List<TodoRequest> requests) {
        return todoService.saveTodos(requests);
    }
}