package com.example.todos;

import com.example.todos.dto.TodoResponse;
import com.example.todos.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
class TodoControllerIntegrationTest {


    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16");


    @LocalServerPort
    private int port;


    private RestTemplate restTemplate;


    @Autowired
    TodoService todoService;


    @BeforeEach
    void setup() {
        restTemplate = new RestTemplate();

        restTemplate.setErrorHandler(new org.springframework.web.client.DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(org.springframework.http.client.ClientHttpResponse response) {
                return false;
            }
        });
    }


    private String url(String path) {
        return "http://localhost:" + port + path;
    }



    @Test
    void shouldCreateAndRetrieveTodo() {

        List<Map<String, Object>> body = List.of(
                Map.of(
                        "title", "Faire les courses",
                        "description", "Lait, pain"
                )
        );


        ResponseEntity<TodoResponse[]> createResponse =
                restTemplate.exchange(
                        url("/todos"),
                        HttpMethod.PUT,
                        new HttpEntity<>(body),
                        TodoResponse[].class
                );


        assertThat(createResponse.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        assertThat(createResponse.getBody())
                .isNotNull();

        assertThat(createResponse.getBody()[0].getTitle())
                .isEqualTo("Faire les courses");


        String id = createResponse.getBody()[0].getId();


        ResponseEntity<TodoResponse> getResponse =
                restTemplate.getForEntity(
                        url("/todos/" + id),
                        TodoResponse.class
                );


        assertThat(getResponse.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        assertThat(getResponse.getBody())
                .isNotNull();

        assertThat(getResponse.getBody().getTitle())
                .isEqualTo("Faire les courses");

        assertThat(getResponse.getBody().getDescription())
                .isEqualTo("Lait, pain");
    }

    @Test
    void shouldFilterByCompletedStatus() {


        List<Map<String, Object>> body = List.of(
                Map.of(
                        "title", "Tache terminee",
                        "completed", true
                ),
                Map.of(
                        "title", "Tache en cours",
                        "completed", false
                )
        );


        restTemplate.exchange(
                url("/todos"),
                HttpMethod.PUT,
                new HttpEntity<>(body),
                TodoResponse[].class
        );



        ResponseEntity<TodoResponse[]> completedResponse =
                restTemplate.getForEntity(
                        url("/todos?completed=true"),
                        TodoResponse[].class
                );


        assertThat(completedResponse.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        assertThat(completedResponse.getBody())
                .isNotNull();

        assertThat(completedResponse.getBody())
                .allMatch(TodoResponse::isCompleted);




        ResponseEntity<TodoResponse[]> notCompletedResponse =
                restTemplate.getForEntity(
                        url("/todos?completed=false"),
                        TodoResponse[].class
                );


        assertThat(notCompletedResponse.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        assertThat(notCompletedResponse.getBody())
                .isNotNull();

        assertThat(notCompletedResponse.getBody())
                .allMatch(todo -> !todo.isCompleted());

    }

    @Test
    void shouldReturnNotFoundForUnknownId() {

        ResponseEntity<Map> response =
                restTemplate.exchange(
                        url("/todos/id-inexistant"),
                        HttpMethod.GET,
                        null,
                        Map.class
                );


        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldGenerateIdAndCreatedAtWhenNotProvided() {


        List<Map<String, Object>> body = List.of(
                Map.of(
                        "title", "Todo sans id"
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


        assertThat(response.getBody()[0].getId())
                .isNotNull()
                .isNotBlank();


        assertThat(response.getBody()[0].getCreatedAt())
                .isNotNull();
    }
}