package com.deadlinebuddy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application entry point.
 * @SpringBootApplication enables Auto-configuration, Component Scanning, and Configuration.
 */
@SpringBootApplication
public class DeadlineBuddyApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeadlineBuddyApplication.class, args);
    }
}
