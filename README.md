READ ME

# Smart Task Manager
Smart Task Manager is a Java-based desktop application built using **Swing** for GUI and **MySQL** for persistent storage. It allows users to create, categorize, filter, and manage tasks with due dates, priorities, and reminders.

---

## Features

- **User Login/Register**
- **Add, Edit, Delete Tasks**
- **Due Date Picker**
- **Category Support** (Work, Study, Personal, etc.)
- **Reminders for Due Today**
- **Filter by Priority**
- **Tabbed View for Today, Overdue, Upcoming, All Tasks**
- **Popup Notifications for Due Tasks**
- **MySQL Integration for Persistent Storage**

---

## Technologies Used
- Java (Swing for GUI)
- MySQL (JDBC)
- IntelliJ IDEA (Recommended IDE)

---

## Project Structure
SmartTaskManager/ 
├── src/ 
│ └── taskmanager/ 
│ ├── SmartTaskManager.java 
│ └── Task.java 
├── README.md

---

## Database Setup (MySQL)
1. Open **MySQL Workbench**.
2. Create a new database:
```sql
  CREATE DATABASE taskdb;
```
3. USE taskdb;
4. Create tables:

```sql
CREATE TABLE users (
id INT AUTO_INCREMENT PRIMARY KEY,
username VARCHAR(100) UNIQUE NOT NULL,
password VARCHAR(100) NOT NULL
);
CREATE TABLE tasks (
id INT AUTO_INCREMENT PRIMARY KEY,
username VARCHAR(100),
title VARCHAR(255),
duedate DATE,
priority VARCHAR(20),
category VARCHAR(50)
);
```
