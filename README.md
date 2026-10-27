# 🎓 DeadlineBuddy - Student Assignment and Deadline Tracker
> **Advanced Internet Programming (AIP) Mini Project**  
> Master of Computer Applications (MCA)

DeadlineBuddy is a lightweight, clean, and beginner-friendly web application built with **Spring Boot 3, Thymeleaf, Spring Data JPA, and Bootstrap 5**. It helps students stay organized by tracking upcoming assignment deadlines, prioritizing tasks, and preventing missed submissions through visual alerts.

---

## 📌 Project Overview & Highlights
- **Single-User Architecture:** Focused and simple, no authentication setup needed.
- **Layered Architecture:** Controller $\rightarrow$ Service $\rightarrow$ Repository.
- **Embedded H2 Database:** Data is stored in a local file (`./data/deadlinebuddydb`), meaning zero database installation is required, and data persists across restarts.
- **Dynamic Visual Cues:** Overdue tasks highlighted in soft red, deadlines within 3 days in amber/orange, and completed tasks in soft green with strikethrough styling.
- **REST API + MVC:** Demonstrates traditional server-rendered MVC views alongside modern REST JSON endpoints.

---

## 🛠️ Tech Stack
| Component | Technology | Description |
|---|---|---|
| **Language** | Java 17+ (tested with Java 17, 21, and 23) | Modern Java features |
| **Framework** | Spring Boot 3.3.5 | Inversion of Control & Auto-configuration |
| **Web / MVC** | Spring MVC + Thymeleaf | Server-side templating engine |
| **Database ORM** | Spring Data JPA / Hibernate | Object-Relational Mapping |
| **Database** | H2 Embedded File Database | File-based storage in `./data/deadlinebuddydb` |
| **Styling** | Bootstrap 5.3 (CDN) & Bootstrap Icons | Responsive, modern user interface |
| **Validation** | Jakarta Bean Validation (Hibernate Validator) | Form input constraints (`@NotBlank`, `@Size`, etc.) |
| **Build Tool** | Apache Maven 3.x (with Maven Wrapper `mvnw`) | Dependency management & compilation |

---

## 🚀 Key Features
1. **Interactive Dashboard (`/`):**
   - 5 Statistical Summary Cards: **Total**, **Pending**, **Completed**, **Overdue**, and **Due in next 3 Days**.
   - Dynamic animated **Completion Progress Bar** (% of completed assignments).
   - Upcoming deadlines table sorted chronologically by due date.
2. **Add Assignment (`/assignments/new`):**
   - Server-side validated form requiring title (3–100 chars), course/subject, due date, and priority.
   - Field-level validation error messages.
3. **Edit Assignment (`/assignments/{id}/edit`):**
   - Form pre-populated with existing details for editing title, subject, notes, due date, priority, or status.
4. **Delete Assignment (`/assignments/{id}/delete`):**
   - Instant deletion with JavaScript confirmation dialog.
5. **Quick Mark as Completed (`/assignments/{id}/complete`):**
   - One-click completion button on each row in the dashboard and list table.
6. **All Assignments & Multi-Criteria Filtering (`/assignments`):**
   - Filter by **Status** (`ALL`, `PENDING`, `COMPLETED`).
   - Filter by **Priority** (`ALL`, `LOW`, `MEDIUM`, `HIGH`).
   - Filter by **Subject** (dynamically populated from existing subjects in the database).
   - **Search Box** for keyword search on assignment titles.
   - One-click "Reset Filters" button.
7. **Visual Deadline Cues:**
   - 🔴 **Overdue:** Soft red background (`.row-overdue`), red badge showing `Overdue by X days`.
   - 🟠 **Due Soon (&le; 3 days):** Soft amber background (`.row-due-soon`), orange badge showing `Due tomorrow` or `X days left`.
   - 🟢 **Completed:** Soft green background (`.row-completed`), green badge, title struck through.
8. **Startup Data Seeding:**
   - Automatically seeds 5 realistic sample assignments relative to today's date on first run.

---

## 🌐 REST API Endpoints
DeadlineBuddy exposes two REST endpoints via `@RestController`:

| Method | Endpoint | Description | Sample Output |
|---|---|---|---|
| `GET` | `/api/assignments` | Returns all assignments as JSON (supports optional `?status=PENDING` or `COMPLETED`) | `[ { "id": 1, "title": "...", "status": "PENDING", ... } ]` |
| `GET` | `/api/assignments/summary` | Returns statistical metrics in JSON | `{"total": 5, "pending": 4, "completed": 1, "overdue": 1, "dueSoon": 2, "completionPercentage": 20}` |

---

## 📂 Project Structure
```text
deadlinebuddy/
├── .mvn/wrapper/                  # Maven Wrapper files
├── mvnw                           # Linux/macOS Maven wrapper script
├── mvnw.cmd                       # Windows Maven wrapper script
├── pom.xml                        # Maven dependencies & build configuration
├── README.md                      # Project documentation and viva guide
├── data/                          # Persistent H2 database storage
│   └── deadlinebuddydb.mv.db
└── src/
    ├── main/
    │   ├── java/com/deadlinebuddy/
    │   │   ├── DeadlineBuddyApplication.java   # Main Spring Boot entry point
    │   │   ├── config/
    │   │   │   └── DataInitializer.java        # Seeds 5 demo assignments on startup
    │   │   ├── controller/
    │   │   │   ├── AssignmentController.java   # Spring MVC Controller (Thymeleaf views)
    │   │   │   └── ApiController.java          # REST API Controller (JSON)
    │   │   ├── model/
    │   │   │   ├── Assignment.java             # JPA Entity mapped to 'assignments' table
    │   │   │   ├── Priority.java               # Enum: LOW, MEDIUM, HIGH
    │   │   │   └── Status.java                 # Enum: PENDING, COMPLETED
    │   │   ├── repository/
    │   │   │   └── AssignmentRepository.java   # Spring Data JPA Repository
    │   │   └── service/
    │   │       └── AssignmentService.java      # Service Layer (Business logic & stats)
    │   └── resources/
    │       ├── application.properties          # H2 and server configurations
    │       ├── static/css/
    │       │   └── style.css                   # Custom CSS (visual cues & theme)
    │       └── templates/
    │           ├── dashboard.html              # Home page: stats, progress, table
    │           ├── list.html                   # All assignments + multi-filters
    │           ├── form.html                   # Add / Edit validated form
    │           └── fragments/
    │               └── layout.html             # Reusable navbar, footer & flash alerts
    └── test/
        └── java/com/deadlinebuddy/
            └── DeadlineBuddyApplicationTests.java  # 11 Automated integration & unit tests
```

---

## ⚙️ How to Run the Project

### Prerequisites
- Java 17 or higher (`java -version`)
- *(Optional)* Maven (Not required, as the included Maven Wrapper handles everything!)

### Step-by-Step Execution

1. **Clone or Open the Project Folder:**
   ```bash
   cd AIP_dik
   ```

2. **Run using Maven Wrapper:**
   - **On Windows (Command Prompt or PowerShell):**
     ```cmd
     .\mvnw.cmd spring-boot:run
     ```
   - **On Linux / macOS:**
     ```bash
     chmod +x mvnw
     ./mvnw spring-boot:run
     ```
   - **Or using the pre-built standalone JAR:**
     ```bash
     java -jar target/deadlinebuddy-1.0.0.jar
     ```
   - **Or using local Maven if installed:**
     ```bash
     mvn spring-boot:run
     ```

3. **Access the Application in your Browser:**
   - **Dashboard (Home Page):** [http://localhost:8080/](http://localhost:8080/)
   - **All Assignments (with filters):** [http://localhost:8080/assignments](http://localhost:8080/assignments)
   - **Add New Assignment:** [http://localhost:8080/assignments/new](http://localhost:8080/assignments/new)
   - **REST API - All Assignments:** [http://localhost:8080/api/assignments](http://localhost:8080/api/assignments)
   - **REST API - Summary Counts:** [http://localhost:8080/api/assignments/summary](http://localhost:8080/api/assignments/summary)
   - **H2 Database Console:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)  
     *(JDBC URL: `jdbc:h2:file:./data/deadlinebuddydb`, User Name: `sa`, Password: leave blank)*

4. **Running Automated Tests:**
   ```bash
   .\mvnw.cmd test
   ```

---

## 🗄️ Database Note: Switching to MySQL (Optional)
The project uses H2 file database by default so it runs instantly on any machine without installing or configuring MySQL. If your college viva specifically requires MySQL, you can switch in 2 simple steps:

1. In `pom.xml`, add the MySQL connector dependency:
   ```xml
   <dependency>
       <groupId>com.mysql</groupId>
       <artifactId>mysql-connector-j</artifactId>
       <scope>runtime</scope>
   </dependency>
   ```

2. In `src/main/resources/application.properties`, comment out the H2 settings and add:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/deadlinebuddy_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=your_mysql_password
   spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
   spring.jpa.hibernate.ddl-auto=update
   ```

---

## 📸 Screenshots Section (Placeholders for Viva Report)
| Dashboard with Metrics & Progress | Filter & Search List Page | Validated Assignment Form |
|:---:|:---:|:---:|
| *(Add Dashboard Screenshot)* | *(Add List Page Screenshot)* | *(Add Form Error Screenshot)* |

---

## 🎓 8 Likely Viva Questions & Short Answers

### Q1: What is the layered architecture used in this project and why is it beneficial?
> **Answer:**  
> The project follows a 3-tier layered architecture:  
> 1. **Controller (`AssignmentController`, `ApiController`):** Handles incoming HTTP requests, binds parameters, and returns views or JSON.  
> 2. **Service (`AssignmentService`):** Contains core business logic, statistics calculations, and manages transactions (`@Transactional`).  
> 3. **Repository (`AssignmentRepository`):** Directly communicates with the database using Spring Data JPA.  
> **Benefit:** Separation of concerns (SoC), high modularity, easier maintenance, and simple unit testing.

---

### Q2: How does Spring Data JPA simplify database operations?
> **Answer:**  
> Instead of writing verbose boilerplate JDBC code (connections, statements, result sets), `AssignmentRepository` extends `JpaRepository<Assignment, Long>`. Spring dynamically generates implementations for standard CRUD methods (`save()`, `findAll()`, `findById()`, `deleteById()`) and enables custom queries using method names or `@Query` JPQL without writing vendor-specific SQL.

---

### Q3: What is the difference between `@Controller` and `@RestController`?
> **Answer:**  
> - `@Controller` is used for traditional MVC web applications where handler methods return a `String` representing a view template name (e.g. `"dashboard"`, `"form"`), which Thymeleaf resolves to render an HTML page.  
> - `@RestController` is a convenience annotation combining `@Controller` and `@ResponseBody`. Its methods serialize Java objects directly into JSON/XML payloads written straight into the HTTP response body.

---

### Q4: How is form validation implemented and handled in this application?
> **Answer:**  
> We use **Jakarta Bean Validation** annotations directly on the `Assignment` entity:  
> - `@NotBlank(message = "Title is required")` and `@Size(min = 3, max = 100)`  
> - `@NotBlank(message = "Subject is required")`  
> - `@NotNull(message = "Due date is required")`  
> In the controller method, `@Valid @ModelAttribute("assignment")` triggers validation, and the `BindingResult` parameter captures any violations. If `bindingResult.hasErrors()` is true, the controller returns the form view, and Thymeleaf displays field-specific error feedback using `th:errors="*{field}"` and Bootstrap's `is-invalid` class.

---

### Q5: Why did you choose H2 file database over MySQL for this mini project?
> **Answer:**  
> 1. **Zero Configuration:** H2 is an embedded database running inside the JVM; it does not require running an external database server like MySQL or setting up system services.  
> 2. **Persistence:** By using `jdbc:h2:file:./data/deadlinebuddydb`, data is saved to disk and persists across application restarts.  
> 3. **Portability:** Anyone can clone the repository, run `./mvnw spring-boot:run`, and test the project immediately on any operating system.

---

### Q6: How are the deadline visual cues (Red, Orange, Green) implemented?
> **Answer:**  
> The `Assignment` model contains helper methods evaluated against `LocalDate.now()`:  
> - `isOverdue()`: checks if status is `PENDING` and `dueDate.isBefore(LocalDate.now())`.  
> - `isDueSoon()`: checks if status is `PENDING` and `dueDate` is between today and `today + 3 days`.  
> - `isCompleted()`: checks if status is `COMPLETED`.  
> In the Thymeleaf template, `th:classappend` dynamically assigns CSS classes (`row-overdue`, `row-due-soon`, `row-completed`) to table rows and badges, providing instant visual feedback.

---

### Q7: What role does Thymeleaf play in this project?
> **Answer:**  
> Thymeleaf is a server-side Java template engine for web environments. It processes HTML files containing `th:*` attributes (like `th:text`, `th:each`, `th:if`, `th:replace`) on the server, substitutes dynamic model data provided by Spring MVC controllers, and sends pure standard HTML5 to the user's browser. It also supports fragment reusability (`fragments/layout.html`).

---

### Q8: What is Inversion of Control (IoC) and Dependency Injection (DI) in Spring Boot?
> **Answer:**  
> - **Inversion of Control (IoC):** The framework manages the lifecycle and creation of objects (beans) rather than the developer manually creating them with `new`.  
> - **Dependency Injection (DI):** Spring injects dependencies where needed (for example, injecting `AssignmentRepository` into `AssignmentService`, and injecting `AssignmentService` into `AssignmentController` via constructor injection with `@Autowired`).
