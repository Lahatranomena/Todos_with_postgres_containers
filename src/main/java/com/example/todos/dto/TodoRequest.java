package com.example.todos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TodoRequest {
    private String id;
    private String title;
    private String description;
    private Boolean completed;
    private LocalDateTime createdAt;
}