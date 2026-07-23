package com.example.todos;

import com.github.dockerjava.api.model.Info;
import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;

class DockerConnectionTest {

    @Test
    void dockerInfo() {
        Info info = DockerClientFactory.instance()
                .client()
                .infoCmd()
                .exec();

        System.out.println(info);
    }
}