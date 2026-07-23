package com.example.todos;

import com.example.todos.dto.TodoRequest;
import com.example.todos.dto.TodoResponse;
import com.example.todos.repository.TodoRepository;
import com.example.todos.service.TodoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class TodoServiceTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16");

    @Autowired
    private TodoService todoService;

    @Autowired
    private TodoRepository todoRepository;


    @Test
    void shouldCreateTodo() {

        TodoRequest request = new TodoRequest();
        request.setTitle("Test todo");
        request.setDescription("Description test");

        List<TodoResponse> responses =
                todoService.saveTodos(List.of(request));

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isNotBlank();
        assertThat(responses.get(0).getTitle())
                .isEqualTo("Test todo");

        assertThat(todoRepository.count())
                .isEqualTo(1);
    }
}