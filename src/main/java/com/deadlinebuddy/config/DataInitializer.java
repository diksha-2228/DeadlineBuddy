package com.deadlinebuddy.config;

import com.deadlinebuddy.model.Assignment;
import com.deadlinebuddy.model.Priority;
import com.deadlinebuddy.model.Status;
import com.deadlinebuddy.repository.AssignmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;

/**
 * Seeds initial demo data on application startup using Spring Boot's CommandLineRunner.
 *
 * Demonstrates:
 * 1. An overdue assignment (due 3 days ago, Pending)
 * 2. An urgent assignment due tomorrow (due in 1 day, Pending)
 * 3. An assignment due in 2 days (Pending)
 * 4. An upcoming assignment (due in 7 days, Pending)
 * 5. A finished assignment (Completed)
 *
 * Dates are calculated dynamically relative to LocalDate.now() so the sample data
 * always renders accurate overdue / due soon / upcoming visual cues whenever evaluated!
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final AssignmentRepository assignmentRepository;

    public DataInitializer(AssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    @Override
    public void run(String... args) {
        // Only seed if database is currently empty
        if (assignmentRepository.count() == 0) {
            LocalDate today = LocalDate.now();

            Assignment a1 = new Assignment(
                    "Cloud Computing Lab Report 2",
                    "Cloud Computing",
                    "Configure AWS EC2 instance and write deployment steps with VPC security groups.",
                    today.minusDays(3), // OVERDUE
                    Priority.HIGH,
                    Status.PENDING
            );

            Assignment a2 = new Assignment(
                    "AIP Mini Project Submission",
                    "Advanced Internet Programming",
                    "Submit Spring Boot + Thymeleaf DeadlineBuddy source code and documentation.",
                    today.plusDays(1), // DUE SOON (Tomorrow)
                    Priority.HIGH,
                    Status.PENDING
            );

            Assignment a3 = new Assignment(
                    "Machine Learning Assignment 3",
                    "Machine Learning",
                    "Implement Linear and Logistic Regression using Python scikit-learn on diabetes dataset.",
                    today.plusDays(2), // DUE SOON (2 days left)
                    Priority.MEDIUM,
                    Status.PENDING
            );

            Assignment a4 = new Assignment(
                    "Software Engineering Case Study",
                    "Software Engineering",
                    "Write Agile Sprint Retrospective and prepare Scrum burndown chart analysis.",
                    today.plusDays(7), // UPCOMING (7 days left)
                    Priority.LOW,
                    Status.PENDING
            );

            Assignment a5 = new Assignment(
                    "Database Systems Quiz Prep",
                    "Database Management",
                    "Solved B+ tree indexing exercises and normalization practice questions up to BCNF.",
                    today.minusDays(1), // COMPLETED
                    Priority.MEDIUM,
                    Status.COMPLETED
            );

            assignmentRepository.saveAll(Arrays.asList(a1, a2, a3, a4, a5));
            logger.info(">>> Seeded 5 sample assignments into database successfully.");
        } else {
            logger.info(">>> Database already contains {} assignments; skipping sample seeding.", assignmentRepository.count());
        }
    }
}
