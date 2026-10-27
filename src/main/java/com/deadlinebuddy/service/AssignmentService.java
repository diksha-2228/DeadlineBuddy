package com.deadlinebuddy.service;

import com.deadlinebuddy.model.Assignment;
import com.deadlinebuddy.model.Priority;
import com.deadlinebuddy.model.Status;
import com.deadlinebuddy.repository.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service Layer for Assignment business logic.
 * Acts as the bridge between Controllers and the Repository layer.
 * Marked with @Service and @Transactional for database transaction management.
 */
@Service
@Transactional
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;

    @Autowired
    public AssignmentService(AssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    /**
     * Retrieves all assignments sorted by due date ascending (earliest first).
     */
    @Transactional(readOnly = true)
    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAllByOrderByDueDateAsc();
    }

    /**
     * Retrieves assignments by status (e.g. for REST API filter).
     */
    @Transactional(readOnly = true)
    public List<Assignment> getAssignmentsByStatus(Status status) {
        if (status == null) {
            return getAllAssignments();
        }
        return assignmentRepository.findByStatusOrderByDueDateAsc(status);
    }

    /**
     * Finds a single assignment by its ID.
     */
    @Transactional(readOnly = true)
    public Optional<Assignment> getAssignmentById(Long id) {
        return assignmentRepository.findById(id);
    }

    /**
     * Saves or updates an assignment in the database.
     */
    public Assignment saveAssignment(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }

    /**
     * Deletes an assignment by ID.
     */
    public void deleteAssignment(Long id) {
        assignmentRepository.deleteById(id);
    }

    /**
     * Marks an assignment as completed.
     */
    public boolean markAsCompleted(Long id) {
        Optional<Assignment> optionalAssignment = assignmentRepository.findById(id);
        if (optionalAssignment.isPresent()) {
            Assignment assignment = optionalAssignment.get();
            assignment.setStatus(Status.COMPLETED);
            assignmentRepository.save(assignment);
            return true;
        }
        return false;
    }

    /**
     * Toggles an assignment status between PENDING and COMPLETED.
     */
    public boolean toggleStatus(Long id) {
        Optional<Assignment> optionalAssignment = assignmentRepository.findById(id);
        if (optionalAssignment.isPresent()) {
            Assignment assignment = optionalAssignment.get();
            if (assignment.getStatus() == Status.COMPLETED) {
                assignment.setStatus(Status.PENDING);
            } else {
                assignment.setStatus(Status.COMPLETED);
            }
            assignmentRepository.save(assignment);
            return true;
        }
        return false;
    }

    /**
     * Filters assignments by status, priority, subject, and title search text.
     */
    @Transactional(readOnly = true)
    public List<Assignment> searchAndFilter(Status status, Priority priority, String subject, String search) {
        // Sanitize empty strings to null for clean query execution
        String cleanSubject = (subject != null && !subject.trim().isEmpty()) ? subject.trim() : null;
        String cleanSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;

        return assignmentRepository.searchAndFilter(status, priority, cleanSubject, cleanSearch);
    }

    /**
     * Returns a distinct list of subjects for dropdown filtering.
     */
    @Transactional(readOnly = true)
    public List<String> getDistinctSubjects() {
        return assignmentRepository.findDistinctSubjects();
    }

    /**
     * Computes dashboard summary metrics:
     * - Total assignments
     * - Pending assignments
     * - Completed assignments
     * - Overdue assignments (pending and due before today)
     * - Due in next 3 days (pending and due between today and today+3)
     * - Completion percentage
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getSummaryStats() {
        long total = assignmentRepository.count();
        long pending = assignmentRepository.countByStatus(Status.PENDING);
        long completed = assignmentRepository.countByStatus(Status.COMPLETED);

        LocalDate today = LocalDate.now();
        LocalDate threeDaysLater = today.plusDays(3);

        long overdue = assignmentRepository.countByStatusAndDueDateBefore(Status.PENDING, today);
        long dueSoon = assignmentRepository.countByStatusAndDueDateBetween(Status.PENDING, today, threeDaysLater);

        int completionPercentage = 0;
        if (total > 0) {
            completionPercentage = (int) Math.round(((double) completed / total) * 100);
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("total", total);
        summary.put("pending", pending);
        summary.put("completed", completed);
        summary.put("overdue", overdue);
        summary.put("dueSoon", dueSoon);
        summary.put("completionPercentage", completionPercentage);

        return summary;
    }
}
