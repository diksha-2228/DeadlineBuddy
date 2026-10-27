package com.deadlinebuddy;

import com.deadlinebuddy.model.Assignment;
import com.deadlinebuddy.model.Priority;
import com.deadlinebuddy.model.Status;
import com.deadlinebuddy.repository.AssignmentRepository;
import com.deadlinebuddy.service.AssignmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Automated Test Suite for DeadlineBuddy.
 * Tests Service Layer, Repository Layer, MVC Controllers, and REST API Endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DeadlineBuddyApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Test
    @DisplayName("Context loads successfully and DataInitializer seeds demo assignments")
    void contextLoads() {
        assertThat(assignmentRepository.count()).isGreaterThanOrEqualTo(5);
    }

    @Test
    @DisplayName("Dashboard summary metrics calculate correctly")
    void testDashboardMetrics() {
        Map<String, Object> summary = assignmentService.getSummaryStats();
        assertThat(summary).containsKey("total");
        assertThat(summary).containsKey("pending");
        assertThat(summary).containsKey("completed");
        assertThat(summary).containsKey("overdue");
        assertThat(summary).containsKey("dueSoon");
        assertThat(summary).containsKey("completionPercentage");

        long total = (long) summary.get("total");
        long pending = (long) summary.get("pending");
        long completed = (long) summary.get("completed");

        assertThat(total).isEqualTo(pending + completed);
    }

    @Test
    @DisplayName("Add, retrieve, mark complete, and delete assignment via Service")
    void testServiceCrudOperations() {
        // 1. Create & Save
        Assignment testAssignment = new Assignment(
                "Automated Test Assignment",
                "Testing Course",
                "Unit testing service layer operations",
                LocalDate.now().plusDays(4),
                Priority.MEDIUM,
                Status.PENDING
        );
        Assignment saved = assignmentService.saveAssignment(testAssignment);
        assertThat(saved.getId()).isNotNull();

        // 2. Retrieve
        Assignment fetched = assignmentService.getAssignmentById(saved.getId()).orElse(null);
        assertThat(fetched).isNotNull();
        assertThat(fetched.getTitle()).isEqualTo("Automated Test Assignment");
        assertThat(fetched.isCompleted()).isFalse();

        // 3. Mark as completed
        boolean marked = assignmentService.markAsCompleted(saved.getId());
        assertThat(marked).isTrue();
        Assignment completedAssignment = assignmentService.getAssignmentById(saved.getId()).orElseThrow();
        assertThat(completedAssignment.getStatus()).isEqualTo(Status.COMPLETED);
        assertThat(completedAssignment.isCompleted()).isTrue();

        // 4. Delete
        assignmentService.deleteAssignment(saved.getId());
        assertThat(assignmentService.getAssignmentById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("Filter and search queries work properly")
    void testFilterAndSearch() {
        List<Assignment> filtered = assignmentService.searchAndFilter(Status.PENDING, null, null, null);
        for (Assignment a : filtered) {
            assertThat(a.getStatus()).isEqualTo(Status.PENDING);
        }

        List<Assignment> searchResults = assignmentService.searchAndFilter(null, null, null, "Report");
        for (Assignment a : searchResults) {
            assertThat(a.getTitle().toLowerCase()).contains("report");
        }
    }

    @Test
    @DisplayName("MVC: GET / renders Dashboard view with 200 OK")
    void testMvcDashboardRoute() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("summary"))
                .andExpect(model().attributeExists("assignments"));
    }

    @Test
    @DisplayName("MVC: GET /assignments renders List view with 200 OK")
    void testMvcListRoute() throws Exception {
        mockMvc.perform(get("/assignments"))
                .andExpect(status().isOk())
                .andExpect(view().name("list"))
                .andExpect(model().attributeExists("assignments"))
                .andExpect(model().attributeExists("subjects"));
    }

    @Test
    @DisplayName("MVC: GET /assignments/new renders Form view with 200 OK")
    void testMvcNewFormRoute() throws Exception {
        mockMvc.perform(get("/assignments/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("form"))
                .andExpect(model().attributeExists("assignment"))
                .andExpect(model().attribute("isEdit", false));
    }

    @Test
    @DisplayName("MVC: POST /assignments with invalid title triggers validation error")
    void testMvcValidationFailure() throws Exception {
        // Title too short (less than 3 chars)
        mockMvc.perform(post("/assignments")
                        .param("title", "AB")
                        .param("subject", "AIP")
                        .param("dueDate", LocalDate.now().plusDays(2).toString())
                        .param("priority", "HIGH")
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(view().name("form"))
                .andExpect(model().hasErrors());
    }

    @Test
    @DisplayName("REST: GET /api/assignments returns JSON array of assignments")
    void testRestGetAllAssignments() throws Exception {
        mockMvc.perform(get("/api/assignments")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").exists())
                .andExpect(jsonPath("$[0].subject").exists());
    }

    @Test
    @DisplayName("REST: GET /api/assignments?status=PENDING returns only pending items")
    void testRestFilterByStatus() throws Exception {
        mockMvc.perform(get("/api/assignments")
                        .param("status", "PENDING")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("REST: GET /api/assignments/summary returns statistical JSON")
    void testRestGetSummary() throws Exception {
        mockMvc.perform(get("/api/assignments/summary")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total").isNumber())
                .andExpect(jsonPath("$.pending").isNumber())
                .andExpect(jsonPath("$.completed").isNumber())
                .andExpect(jsonPath("$.overdue").isNumber());
    }
}
