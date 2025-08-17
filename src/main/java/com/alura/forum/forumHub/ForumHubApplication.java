package com.alura.forum.forumHub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ForumHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForumHubApplication.class, args);
    }
}