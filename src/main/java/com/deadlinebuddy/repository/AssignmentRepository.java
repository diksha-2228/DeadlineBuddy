package com.deadlinebuddy.repository;

import com.deadlinebuddy.model.Assignment;
import com.deadlinebuddy.model.Priority;
import com.deadlinebuddy.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data JPA Repository for Assignment entity.
 * Provides standard CRUD operations plus custom JPQL queries for dashboard counts and filtering.
 */
@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    // Fetch all assignments sorted by due date (earliest deadline first)
    List<Assignment> findAllByOrderByDueDateAsc();

    // Fetch assignments by specific status (e.g. for REST API ?status=PENDING)
    List<Assignment> findByStatusOrderByDueDateAsc(Status status);

    // Count helpers for Dashboard metrics
    long countByStatus(Status status);

    // Overdue count: Status is PENDING and due date is strictly before today
    long countByStatusAndDueDateBefore(Status status, LocalDate date);

    // Due soon count: Status is PENDING and due date is between today and today + 3 days
    long countByStatusAndDueDateBetween(Status status, LocalDate startDate, LocalDate endDate);

    // Get list of distinct subjects for the filter dropdown
    @Query("SELECT DISTINCT a.subject FROM Assignment a ORDER BY a.subject ASC")
    List<String> findDistinctSubjects();

    // Search and filter query: allows filtering by status, priority, subject, and keyword in title
    @Query("SELECT a FROM Assignment a WHERE " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:priority IS NULL OR a.priority = :priority) AND " +
           "(:subject IS NULL OR :subject = '' OR LOWER(a.subject) = LOWER(:subject)) AND " +
           "(:search IS NULL OR :search = '' OR LOWER(a.title) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY a.dueDate ASC")
    List<Assignment> searchAndFilter(
            @Param("status") Status status,
            @Param("priority") Priority priority,
            @Param("subject") String subject,
            @Param("search") String search
    );
}
