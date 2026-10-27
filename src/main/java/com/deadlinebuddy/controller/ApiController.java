package com.deadlinebuddy.controller;

import com.deadlinebuddy.model.Assignment;
import com.deadlinebuddy.model.Status;
import com.deadlinebuddy.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for DeadlineBuddy API.
 * Demonstrates the difference between @Controller (returns HTML views via Thymeleaf)
 * and @RestController (returns raw JSON payloads directly in the HTTP response body).
 *
 * Endpoints:
 * - GET /api/assignments : Lists assignments in JSON (optional ?status=PENDING/COMPLETED)
 * - GET /api/assignments/summary : Returns count summary {total, pending, completed, overdue}
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allows cross-origin testing if needed
public class ApiController {

    private final AssignmentService assignmentService;

    @Autowired
    public ApiController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    /**
     * Endpoint 1: Retrieve all assignments as JSON, optionally filtered by status.
     * Example: GET /api/assignments OR GET /api/assignments?status=PENDING
     */
    @GetMapping("/assignments")
    public ResponseEntity<List<Assignment>> getAllAssignments(
            @RequestParam(name = "status", required = false) Status status) {
        List<Assignment> assignments = assignmentService.getAssignmentsByStatus(status);
        return ResponseEntity.ok(assignments);
    }

    /**
     * Endpoint 2: Retrieve statistical summary counts as JSON.
     * Returns: { "total": N, "pending": N, "completed": N, "overdue": N }
     * Example: GET /api/assignments/summary
     */
    @GetMapping("/assignments/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        Map<String, Object> stats = assignmentService.getSummaryStats();
        return ResponseEntity.ok(stats);
    }
}
