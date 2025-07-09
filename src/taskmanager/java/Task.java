package taskmanager.java;

import java.time.LocalDate;

public class Task {
    private final String title;
    private final String dueDate;
    private final String priority;
    private final String category;

    public Task(String title, String dueDate, String priority, String category) {
        this.title = title;
        this.dueDate = dueDate;
        this.priority = priority;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public String getCategory() {
        return category;
    }

    public boolean isDueToday() {
        return dueDate.equals(LocalDate.now().toString());
    }

    @Override
    public String toString() {
        return "Title: " + title + ", Due: " + dueDate + ", Priority: " + priority + ", Category: " + category;
    }
}
