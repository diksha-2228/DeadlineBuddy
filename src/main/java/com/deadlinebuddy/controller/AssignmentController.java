package com.deadlinebuddy.controller;

import com.deadlinebuddy.model.Assignment;
import com.deadlinebuddy.model.Priority;
import com.deadlinebuddy.model.Status;
import com.deadlinebuddy.service.AssignmentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Spring MVC Controller handling user interface page requests.
 * Uses Thymeleaf templates to render HTML views with server-side data.
 */
@Controller
public class AssignmentController {

    private final AssignmentService assignmentService;

    @Autowired
    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    /**
     * Dashboard view (Home page).
     * Route: GET /
     * Displays summary statistics, completion progress bar, and upcoming deadlines table.
     */
    @GetMapping("/")
    public String dashboard(Model model) {
        // Fetch dashboard metrics (total, pending, completed, overdue, due soon, %)
        Map<String, Object> summary = assignmentService.getSummaryStats();
        model.addAttribute("summary", summary);

        // Fetch upcoming assignments sorted by due date
        List<Assignment> upcoming = assignmentService.getAllAssignments();
        model.addAttribute("assignments", upcoming);

        return "dashboard";
    }

    /**
     * List page with multi-criteria filtering and title search.
     * Route: GET /assignments
     */
    @GetMapping("/assignments")
    public String listAssignments(
            @RequestParam(name = "status", required = false) Status status,
            @RequestParam(name = "priority", required = false) Priority priority,
            @RequestParam(name = "subject", required = false) String subject,
            @RequestParam(name = "search", required = false) String search,
            Model model) {

        List<Assignment> assignments = assignmentService.searchAndFilter(status, priority, subject, search);
        List<String> distinctSubjects = assignmentService.getDistinctSubjects();

        model.addAttribute("assignments", assignments);
        model.addAttribute("subjects", distinctSubjects);
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("statuses", Status.values());

        // Keep current filter selections in view for form binding
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedPriority", priority);
        model.addAttribute("selectedSubject", subject);
        model.addAttribute("searchQuery", search);

        return "list";
    }

    /**
     * Show form to create a new assignment.
     * Route: GET /assignments/new
     */
    @GetMapping("/assignments/new")
    public String showCreateForm(Model model) {
        Assignment assignment = new Assignment();
        assignment.setPriority(Priority.MEDIUM);
        assignment.setStatus(Status.PENDING);

        model.addAttribute("assignment", assignment);
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("statuses", Status.values());
        model.addAttribute("isEdit", false);

        return "form";
    }

    /**
     * Process creation of a new assignment with Bean Validation.
     * Route: POST /assignments
     */
    @PostMapping("/assignments")
    public String createAssignment(
            @Valid @ModelAttribute("assignment") Assignment assignment,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Check for validation errors (e.g. title too short, null date, etc.)
        if (bindingResult.hasErrors()) {
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("statuses", Status.values());
            model.addAttribute("isEdit", false);
            return "form";
        }

        assignmentService.saveAssignment(assignment);
        redirectAttributes.addFlashAttribute("successMessage",
                "Assignment '" + assignment.getTitle() + "' added successfully!");

        return "redirect:/assignments";
    }

    /**
     * Show form to edit an existing assignment.
     * Route: GET /assignments/{id}/edit
     */
    @GetMapping("/assignments/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Assignment> optionalAssignment = assignmentService.getAssignmentById(id);

        if (optionalAssignment.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Assignment not found with ID: " + id);
            return "redirect:/assignments";
        }

        model.addAttribute("assignment", optionalAssignment.get());
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("statuses", Status.values());
        model.addAttribute("isEdit", true);

        return "form";
    }

    /**
     * Process update of an existing assignment.
     * Route: POST /assignments/{id}/edit
     */
    @PostMapping("/assignments/{id}/edit")
    public String updateAssignment(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("assignment") Assignment assignment,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("statuses", Status.values());
            model.addAttribute("isEdit", true);
            return "form";
        }

        assignment.setId(id);
        assignmentService.saveAssignment(assignment);
        redirectAttributes.addFlashAttribute("successMessage",
                "Assignment '" + assignment.getTitle() + "' updated successfully!");

        return "redirect:/assignments";
    }

    /**
     * Delete an assignment.
     * Supports both POST (preferred) and GET for easy browser / link invocation.
     * Route: POST or GET /assignments/{id}/delete
     */
    @RequestMapping(value = "/assignments/{id}/delete", method = {RequestMethod.POST, RequestMethod.GET})
    public String deleteAssignment(@PathVariable("id") Long id,
                                   HttpServletRequest request,
                                   RedirectAttributes redirectAttributes) {
        assignmentService.deleteAssignment(id);
        redirectAttributes.addFlashAttribute("successMessage", "Assignment deleted successfully!");

        // Return to referring page (Dashboard or List) if available
        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("/")) {
            return "redirect:" + referer;
        }
        return "redirect:/assignments";
    }

    /**
     * Mark an assignment as completed.
     * Route: POST or GET /assignments/{id}/complete
     */
    @RequestMapping(value = "/assignments/{id}/complete", method = {RequestMethod.POST, RequestMethod.GET})
    public String completeAssignment(@PathVariable("id") Long id,
                                     HttpServletRequest request,
                                     RedirectAttributes redirectAttributes) {
        boolean success = assignmentService.markAsCompleted(id);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Assignment marked as completed!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Assignment not found with ID: " + id);
        }

        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        }
        return "redirect:/assignments";
    }
}
