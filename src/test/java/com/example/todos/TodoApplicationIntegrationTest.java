package com.example.todos;

import com.example.todos.dto.TodoRequest;
import com.example.todos.dto.TodoResponse;
import com.example.todos.repository.TodoRepository;
import com.example.todos.service.TodoService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@Testcontainers
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class TodoApplicationIntegrationTest {


    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16");


    @Autowired
    private TodoService todoService;


    @Autowired
    private TodoRepository todoRepository;


    @LocalServerPort
    private int port;


    private final RestTemplate restTemplate =
            new RestTemplate();



    private String url(String path){
        return "http://localhost:" + port + path;
    }



    @Test
    void shouldWorkWithCompleteApplicationFlow(){

        TodoRequest request = new TodoRequest();
        request.setTitle("Test integration");
        request.setDescription("Avec PostgreSQL");


        List<TodoResponse> saved =
                todoService.saveTodos(List.of(request));


        assertThat(saved)
                .hasSize(1);


        assertThat(todoRepository.count())
                .isEqualTo(1);


        List<Map<String,Object>> body = List.of(
                Map.of(
                        "title","Acheter du lait",
                        "description","Magasin"
                )
        );


        ResponseEntity<TodoResponse[]> response =
                restTemplate.exchange(
                        url("/todos"),
                        HttpMethod.PUT,
                        new HttpEntity<>(body),
                        TodoResponse[].class
                );


        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);


        assertThat(response.getBody())
                .isNotNull();



        String id =
                response.getBody()[0].getId();


        ResponseEntity<TodoResponse> get =
                restTemplate.getForEntity(
                        url("/todos/" + id),
                        TodoResponse.class
                );


        assertThat(get.getStatusCode())
                .isEqualTo(HttpStatus.OK);


        assertThat(get.getBody().getTitle())
                .isEqualTo("Acheter du lait");



        try {

            restTemplate.getForEntity(
                    url("/todos/inexistant"),
                    TodoResponse.class
            );

        } catch(Exception e){

            assertThat(e.getMessage())
                    .contains("404");

        }

    }
}