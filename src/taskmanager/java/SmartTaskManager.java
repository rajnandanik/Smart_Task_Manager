package taskmanager.java;



import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;

public class SmartTaskManager extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/taskdb";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Nandani@2001";

    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> taskList = new JList<>(listModel);
    private final List<Task> tasks = new ArrayList<>();

    private final DefaultListModel<String> allTasksModel = new DefaultListModel<>();
    private final JList<String> allTasksList = new JList<>(allTasksModel);

    private String loggedInUser = null;
    private JLabel notificationLabel;

    public SmartTaskManager() {
        setTitle("Smart Task Manager");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        showLoginScreen();
    }

    private void showLoginScreen() {
        JTextField usernameField = new JTextField(10);
        JPasswordField passwordField = new JPasswordField(10);

        JPanel loginPanel = new JPanel();
        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(null, loginPanel,
                "Login/Register", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            loggedInUser = usernameField.getText();
            String pass = new String(passwordField.getPassword());
            if (authenticateUser(loggedInUser, pass)) {
                loadTasksFromDB();
                initUI();
            } else {
                JOptionPane.showMessageDialog(this, "Login failed.");
                showLoginScreen();
            }
        } else {
            System.exit(0);
        }
    }

    private boolean authenticateUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return true;

            stmt = conn.prepareStatement("INSERT INTO users(username, password) VALUES(?, ?)");
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void initUI() {
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel taskPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel();

        JButton addButton = new JButton("Add Task");
        JButton deleteButton = new JButton("Delete Task");
        JButton editButton = new JButton("Edit Task");
        JButton filterButton = new JButton("Filter by Priority");
        JButton categoryButton = new JButton("Filter by Category");

        addButton.addActionListener(e -> {
            addTask();
            loadTasksFromDB();
        });
        deleteButton.addActionListener(e -> {
            deleteTask();
            loadTasksFromDB();
        });
        editButton.addActionListener(e -> {
            editTask();
            loadTasksFromDB();
        });
        filterButton.addActionListener(e -> filterTasks());
        categoryButton.addActionListener(e -> filterTasksByCategory());

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(filterButton);
        buttonPanel.add(categoryButton);

        taskPanel.add(new JScrollPane(taskList), BorderLayout.CENTER);
        taskPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel allTasksPanel = new JPanel(new BorderLayout());
        JButton refreshButton = new JButton("Refresh");
        notificationLabel = new JLabel("Showing all tasks", SwingConstants.CENTER);

        refreshButton.addActionListener(e -> loadAllTasksFromDB());

        allTasksPanel.add(notificationLabel, BorderLayout.NORTH);
        allTasksPanel.add(new JScrollPane(allTasksList), BorderLayout.CENTER);
        allTasksPanel.add(refreshButton, BorderLayout.SOUTH);

        tabbedPane.addTab("My Tasks", taskPanel);
        tabbedPane.addTab("All Tasks", allTasksPanel);

        add(tabbedPane);
        setVisible(true);

        startReminderThread();
    }

    private void logToFile(String message) {
        try (java.io.FileWriter fw = new java.io.FileWriter("task_log.txt", true)) {
            fw.write(LocalDate.now() + " - " + message + "\n");
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }


    private void startReminderThread() {
      java.util.concurrent.ScheduledExecutorService scheduler = java.util.concurrent.Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            LocalDate today = LocalDate.now();
            for (Task task : tasks) {
                LocalDate dueDate = LocalDate.parse(task.getDueDate());
                if (dueDate.isEqual(today)) {
                    SwingUtilities.invokeLater(() -> {
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(null, "Reminder: Task due today! " + task);
                    } );
                } else if (dueDate.isBefore(today)) {
                    System.out.println("Overdue: " + task);
                    logToFile("Overdue: " + task);
                } else if (dueDate.isAfter(today)) {
                    System.out.println("Upcoming: " + task);
                    logToFile("Upcoming: " + task);
                }
            }
        }, 0, 60, java.util.concurrent.TimeUnit.SECONDS);
    }

    private void addTask() {
        String title = JOptionPane.showInputDialog("Enter task title:");
        SpinnerModel dateModel = new SpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));

        String priority = JOptionPane.showInputDialog("Enter priority (High/Medium/Low):");
        String category = JOptionPane.showInputDialog("Enter category (Work/Study/Personal/etc):");

        JPanel inputPanel = new JPanel(new GridLayout(0, 1));
        inputPanel.add(new JLabel("Select Due Date:"));
        inputPanel.add(dateSpinner);

        int result = JOptionPane.showConfirmDialog(null, inputPanel, "Due Date",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) return;

        java.util.Date selectedDate = (java.util.Date) dateSpinner.getValue();
        String dueDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(selectedDate);

        try {
            LocalDate.parse(dueDate, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format or value.");
            return;
        }

        Task task = new Task(title, dueDate, priority, category);
        tasks.add(task);
        listModel.addElement(task.toString());

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO tasks(username, title, duedate, priority, category) VALUES (?, ?, ?, ?, ?)");
            stmt.setString(1, loggedInUser);
            stmt.setString(2, title);
            stmt.setString(3, dueDate);
            stmt.setString(4, priority);
            stmt.setString(5, category);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void editTask() {
        int index = taskList.getSelectedIndex();
        if (index >= 0 && index < tasks.size()) {
            Task oldTask = tasks.get(index);
            String newTitle = JOptionPane.showInputDialog("Edit Title:", oldTask.getTitle());

            SpinnerModel dateModel = new SpinnerDateModel();
            JSpinner dateSpinner = new JSpinner(dateModel);
            dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));

            String newPriority = JOptionPane.showInputDialog("Edit Priority (High/Medium/Low):", oldTask.getPriority());
            String newCategory = JOptionPane.showInputDialog("Edit Category:", oldTask.getCategory());

            JPanel inputPanel = new JPanel(new GridLayout(0, 1));
            inputPanel.add(new JLabel("Edit Due Date:"));
            inputPanel.add(dateSpinner);

            int result = JOptionPane.showConfirmDialog(null, inputPanel, "Edit Due Date",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result != JOptionPane.OK_OPTION) return;

            String newDueDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format((java.util.Date) dateSpinner.getValue());

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                PreparedStatement updateStmt = conn.prepareStatement("UPDATE tasks SET title=?, duedate=?, priority=?, category=? WHERE username=? AND title=? AND duedate=?");
                updateStmt.setString(1, newTitle);
                updateStmt.setString(2, newDueDate);
                updateStmt.setString(3, newPriority);
                updateStmt.setString(4, newCategory);
                updateStmt.setString(5, loggedInUser);
                updateStmt.setString(6, oldTask.getTitle());
                updateStmt.setString(7, oldTask.getDueDate());
                updateStmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            tasks.set(index, new Task(newTitle, newDueDate, newPriority, newCategory));
        } else {
            JOptionPane.showMessageDialog(this, "Please select a task to edit.");
        }
    }

    private void deleteTask() {
        int index = taskList.getSelectedIndex();
        if (index >= 0 && index < tasks.size()) {
            Task task = tasks.get(index);
            listModel.remove(index);
            tasks.remove(index);

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM tasks WHERE username=? AND title=? AND duedate=?");
                stmt.setString(1, loggedInUser);
                stmt.setString(2, task.getTitle());
                stmt.setString(3, task.getDueDate());
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a valid task to delete.");
        }
    }

    private void filterTasks() {
        String filter = JOptionPane.showInputDialog("Enter priority to filter (High/Medium/Low):");
        listModel.clear();
        for (Task task : tasks) {
            if (task.getPriority().equalsIgnoreCase(filter)) {
                listModel.addElement(task.toString());
            }
        }
    }

    private void filterTasksByCategory() {
        String filter = JOptionPane.showInputDialog("Enter category to filter:");
        listModel.clear();
        for (Task task : tasks) {
            if (task.getCategory().equalsIgnoreCase(filter)) {
                listModel.addElement(task.toString());
            }
        }
    }

    private void loadTasksFromDB() {
        tasks.clear();
        listModel.clear();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tasks WHERE username=?");
            stmt.setString(1, loggedInUser);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Task task = new Task(
                        rs.getString("title"),
                        rs.getString("duedate"),
                        rs.getString("priority"),
                        rs.getString("category")
                );
                tasks.add(task);
                listModel.addElement(task.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAllTasksFromDB() {
        allTasksModel.clear();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM tasks");
            while (rs.next()) {
                String taskStr = rs.getString("username") + " | " + rs.getString("title") + " | Due: " + rs.getString("duedate") +
                        " | Priority: " + rs.getString("priority") + " | Category: " + rs.getString("category");
                allTasksModel.addElement(taskStr);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL Driver not found");
            return;
        }
        SwingUtilities.invokeLater(SmartTaskManager::new);
    }
}