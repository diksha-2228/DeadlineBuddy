package com.deadlinebuddy.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * JPA Entity representing an Assignment.
 * Maps to the 'assignments' table in the H2 database.
 * Includes Bean Validation annotations for form input validation.
 */
@Entity
@Table(name = "assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Title validation: non-blank and between 3 and 100 characters
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    @Column(nullable = false, length = 100)
    private String title;

    // Subject validation: non-blank
    @NotBlank(message = "Subject is required")
    @Size(max = 50, message = "Subject cannot exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String subject;

    // Description is optional but limited in length
    @Column(length = 1000)
    private String description;

    // Due Date validation: required
    @NotNull(message = "Due date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    // Priority enum (LOW, MEDIUM, HIGH)
    @NotNull(message = "Priority is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Priority priority = Priority.MEDIUM;

    // Status enum (PENDING, COMPLETED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private Status status = Status.PENDING;

    // Timestamp when the record was created
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Default constructor required by JPA
    public Assignment() {
    }

    // Convenience constructor
    public Assignment(String title, String subject, String description, LocalDate dueDate, Priority priority, Status status) {
        this.title = title;
        this.subject = subject;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
    }

    // Automatically set createdAt and default status before persisting
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = Status.PENDING;
        }
        if (this.priority == null) {
            this.priority = Priority.MEDIUM;
        }
    }

    // ==========================================
    // HELPER METHODS FOR UI VISUAL CUES & STATUS
    // ==========================================

    /**
     * Checks if this assignment is overdue (Pending and dueDate before today).
     */
    public boolean isOverdue() {
        if (status == Status.COMPLETED || dueDate == null) {
            return false;
        }
        return dueDate.isBefore(LocalDate.now());
    }

    /**
     * Checks if this assignment is due within the next 3 days (and still Pending).
     */
    public boolean isDueSoon() {
        if (status == Status.COMPLETED || dueDate == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        return !dueDate.isBefore(today) && !dueDate.isAfter(today.plusDays(3));
    }

    /**
     * Checks if assignment is completed.
     */
    public boolean isCompleted() {
        return this.status == Status.COMPLETED;
    }

    /**
     * Calculates days difference between today and due date.
     */
    public long getDaysRemaining() {
        if (dueDate == null) return 0;
        return ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }

    /**
     * User-friendly text for deadline cue: "Overdue by X days", "Due today", "X days left".
     */
    public String getDeadlineText() {
        if (isCompleted()) {
            return "Completed";
        }
        if (dueDate == null) {
            return "No date";
        }
        long days = getDaysRemaining();
        if (days < 0) {
            long overdueDays = Math.abs(days);
            return overdueDays == 1 ? "Overdue by 1 day" : "Overdue by " + overdueDays + " days";
        } else if (days == 0) {
            return "Due today";
        } else if (days == 1) {
            return "Due tomorrow (1 day left)";
        } else {
            return days + " days left";
        }
    }

    // ==========================================
    // GETTERS AND SETTERS
    // ==========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
