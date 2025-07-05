READ ME

# Smart Task Manager
Smart Task Manager is a Java-based desktop application built using **Swing** for GUI and **MySQL** for persistent storage. It allows users to create, categorize, filter, and manage tasks with due dates, priorities, and reminders.

---

## ![image](https://github.com/user-attachments/assets/a1e9431d-9973-442f-968b-28276d478bb9) Features

- ![image](https://github.com/user-attachments/assets/3c04208f-2942-4d9b-80ae-737f4d82131b) **User Login/Register**
- ![image](https://github.com/user-attachments/assets/7b3ebe79-1ed7-4b4a-b8f0-24942f8bc5d1) **Add, Edit, Delete Tasks**
- ![image](https://github.com/user-attachments/assets/899adb9a-ce9c-436a-adb8-cb3bf60d8467) **Due Date Picker**
- ![image](https://github.com/user-attachments/assets/443ac375-9960-44b2-8fcb-98ed1d5b94be) **Category Support** (Work, Study, Personal, etc.)
- ![image](https://github.com/user-attachments/assets/1f0c1853-ec24-445f-b20f-f7855e8d0136) **Reminders for Due Today**
- ![image](https://github.com/user-attachments/assets/4273085b-e736-4001-8dbf-99abf242352e) **Filter by Priority**
- ![image](https://github.com/user-attachments/assets/baf3b138-a2e2-457d-8db8-af5a3c5f9c38) **Tabbed View for Today, Overdue, Upcoming, All Tasks**
- ![image](https://github.com/user-attachments/assets/863b926c-bc19-46b9-b5cf-c6f3b281167a) **Popup Notifications for Due Tasks**
- ![image](https://github.com/user-attachments/assets/8ccf548e-95ae-4ca9-ac5a-01b59f8effe9) **MySQL Integration for Persistent Storage**

---

## ![image](https://github.com/user-attachments/assets/6224c66b-dceb-4c19-bfd6-4485886f7303) Technologies Used
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
