package com.deadlinebuddy.model;

/**
 * Priority enum representing the urgency levels of an assignment.
 * Useful for color-coded visual cues (Bootstrap badge classes).
 */
public enum Priority {
    LOW("Low", "info"),
    MEDIUM("Medium", "primary"),
    HIGH("High", "danger");

    private final String displayName;
    private final String badgeClass;

    Priority(String displayName, String badgeClass) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}
