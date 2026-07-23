package com.example.todos.repository;

import com.example.todos.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, String> {
    List<Todo> findByCompleted(boolean completed);
}