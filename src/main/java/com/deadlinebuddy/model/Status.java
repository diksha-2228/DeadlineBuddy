package com.deadlinebuddy.model;

/**
 * Status enum representing whether an assignment is Pending or Completed.
 * Also supplies Bootstrap badge style classes for visual cues.
 */
public enum Status {
    PENDING("Pending", "warning"),
    COMPLETED("Completed", "success");

    private final String displayName;
    private final String badgeClass;

    Status(String displayName, String badgeClass) {
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
