# 🚆 Train Booking System

A Java-based desktop **Train Booking System** for managing train schedules, routes, trains, coaches, seats, fares, passenger bookings, payments, cancellations, refunds, PNR status, and administrative reports.

The application is built as a **Java Swing desktop application** with a **MySQL relational database** backend. It follows a layered structure separating the user interface, business/service logic, data-access connection, models, and reporting functionality.

> **Project type:** Desktop application  
> **Language:** Java  
> **GUI:** Java Swing  
> **Database:** MySQL  
> **IDE/project format:** Apache NetBeans  
> **Build system:** Apache Ant / NetBeans  
> **Java version configured by the project:** Java 24  
> **Main entry point:** `app.Main`

---

## 📌 Table of Contents

- [Project Overview](#-project-overview)
- [Objectives](#-objectives)
- [Key Features](#-key-features)
- [User Roles](#-user-roles)
- [System Architecture](#-system-architecture)
- [Project Structure](#-project-structure)
- [Technology Stack](#-technology-stack)
- [Database Design](#-database-design)
- [Application Workflow](#-application-workflow)
- [Prerequisites](#-prerequisites)
- [Installation and Setup](#-installation-and-setup)
- [Database Configuration](#-database-configuration)
- [NetBeans Configuration](#-netbeans-configuration)
- [Running the Application](#-running-the-application)
- [Passenger Workflow](#-passenger-workflow)
- [Administrator Workflow](#-administrator-workflow)
- [Booking and Payment Logic](#-booking-and-payment-logic)
- [Cancellation and Refund Workflow](#-cancellation-and-refund-workflow)
- [Reporting](#-reporting)
- [Security](#-security)
- [Important Configuration Notes](#-important-configuration-notes)
- [Troubleshooting](#-troubleshooting)
- [Possible Improvements](#-possible-improvements)
- [Development Guidelines](#-development-guidelines)
- [Future Enhancements](#-future-enhancements)
- [Contributing](#-contributing)
- [License](#-license)
- [Author](#-author)

---

## 🎯 Project Overview

The Train Booking System provides a computerized platform for managing railway operations and passenger ticket reservations.

Instead of handling train information, passenger bookings, seats, payments, and cancellations manually, the system provides a graphical interface through which users can perform these operations.

The application has two major operational areas:

### Passenger side

Passengers can:

- Create an account.
- Log in securely.
- Search available train runs.
- Select a route, train run, seat class, and seat.
- Book a ticket.
- Make a payment record.
- Receive a generated PNR.
- View their bookings.
- View their tickets.
- Check PNR status.
- Request cancellation.
- View booking history.

### Administrator side

Administrators can manage:

- Stations.
- Routes.
- Trains.
- Train runs/schedules.
- Coaches.
- Seats.
- Fares.
- Bookings and cancellation processing.
- Refunds.
- Operational reports.

The system also provides PDF report generation for administrative reporting.

---

## 🎯 Objectives

The main objectives of the system are to:

1. Automate train ticket reservation.
2. Reduce manual management of railway schedules and passenger bookings.
3. Prevent duplicate seat allocation.
4. Maintain passenger and booking records in a relational database.
5. Provide role-based access to passenger and administrator functionality.
6. Support cancellation and refund processing.
7. Generate useful operational reports.
8. Provide a user-friendly desktop interface.
9. Maintain a clear separation between UI, business logic, and database access.

---

## ✨ Key Features

### 🔐 Authentication and Registration

- Passenger registration.
- User login.
- Role-based authentication.
- Password hashing using **BCrypt**.
- Separate passenger and administrator dashboards.

The authentication layer reads the user's role from the database and uses it to determine the appropriate application interface.

---

### 🚉 Station Management

Administrators can:

- Add stations.
- View stations.
- Delete stations.
- Store station names and countries.

Stations form the foundation for route creation.

---

### 🛤️ Route Management

Administrators can create routes using:

- Origin station.
- Destination station.
- Distance in kilometres.

Routes are then associated with train runs and fares.

---

### 🚆 Train Management

Administrators can:

- Add trains.
- View trains.
- Delete trains.
- Activate/deactivate trains.

Each train has information such as:

- Train ID.
- Train number.
- Train name.
- Active status.

---

### 🗓️ Train Run / Schedule Management

A train run represents a scheduled journey for a train.

Administrators can configure:

- Train.
- Route.
- Run date.
- Departure time.
- Arrival time.
- Duration.
- Schedule status.

The application supports searching for train runs based on passenger requirements.

---

### 🚃 Coach Management

Coaches are associated with trains.

Administrators can manage:

- Coach label.
- Coach type.
- Coach capacity.

The system can generate seats for a coach based on the configured capacity.

---

### 💺 Seat Management

The system supports:

- Seat classes.
- Seat availability.
- Seat selection.
- Coach/seat identification.
- Detection of seats already used for a train run.

When a passenger books a seat, the booking logic checks existing tickets before assigning the seat.

---

### 💰 Fare Management

Fares are associated with:

- Routes.
- Seat classes.
- Effective dates.

The application retrieves the appropriate fare when a passenger selects a train run and class.

---

### 🎟️ Ticket Booking

Passengers can:

1. Select a train run.
2. Select a seat class.
3. Select an available seat.
4. View the applicable fare.
5. Enter/select payment information.
6. Confirm the booking.
7. Receive a PNR.

The booking service creates the related booking, ticket, payment, and PNR records.

---

### 💳 Payment Recording

The payment service records:

- Booking.
- Amount.
- Payment method.
- Transaction reference.
- Payment status.
- Payment date.

The current application implements payment as a database transaction/recording mechanism rather than a live external payment gateway.

---

### 🔖 PNR Management

Each confirmed booking receives a PNR in the format:

```text
PNR<timestamp>
```

Example:

```text
PNR1750000000000
```

Passengers can use the PNR status interface to check their booking information.

---

### ❌ Cancellation and Refunds

The system supports a cancellation workflow.

A passenger can submit a cancellation request with a reason.

An administrator can process the request.

Processing can:

1. Mark the booking as cancelled.
2. Mark associated tickets as cancelled.
3. Create a refund record when applicable.
4. Update the payment status to `Refunded`.
5. Mark the cancellation request as processed.

---

### 📊 Reports

The reporting module supports several report types, including:

- Train schedules.
- Booking summaries.
- Top routes.
- Cancellation reports.
- Train utilization.
- Passenger booking history.

Reports are represented internally using `ReportData`.

The system can generate PDF reports using **iText 5**.

---

## 👥 User Roles

### Passenger

Passengers primarily interact with:

- Registration.
- Login.
- Train search.
- Ticket booking.
- Payment recording.
- My bookings.
- My tickets.
- PNR status.
- Cancellation requests.
- Booking history.

### Administrator

Administrators primarily interact with:

- Stations.
- Routes.
- Trains.
- Train runs.
- Coaches.
- Fares.
- Cancellation processing.
- Reports.
- Operational management.

---

# 🏗️ System Architecture

The project follows a layered structure:

```text
┌─────────────────────────────────────┐
│          Java Swing UI              │
│                                     │
│ Login / Signup / Dashboards         │
│ Booking / Search / Tickets          │
│ Management / Reports                │
└──────────────────┬──────────────────┘
                   │
                   ▼
┌─────────────────────────────────────┐
│         Service Layer               │
│                                     │
│ AuthenticationService               │
│ BookingService                      │
│ PaymentService                      │
│ CancellationService                │
│ TrainService                        │
│ RouteService                        │
│ ScheduleService                    │
│ SeatService                         │
│ CoachService                        │
│ FareService                         │
│ ReportService                       │
└──────────────────┬──────────────────┘
                   │
                   ▼
┌─────────────────────────────────────┐
│        Database Connection          │
│                                     │
│       DBConnection.java             │
│             JDBC                    │
└──────────────────┬──────────────────┘
                   │
                   ▼
┌─────────────────────────────────────┐
│             MySQL                   │
│                                     │
│ Users / Passengers / Trains         │
│ Routes / Stations / Coaches         │
│ Seats / Bookings / Tickets          │
│ Payments / Cancellations / Refunds  │
│ Fares / Train Runs / PNRs           │
└─────────────────────────────────────┘
```

---

# 📁 Project Structure

```text
TrainBookingSystem-Main/
│
├── src/
│   ├── app/
│   │   └── Main.java
│   │
│   ├── db/
│   │   ├── DBConnection.java
│   │   └── erd.mwb
│   │
│   ├── models/
│   │   ├── Run.java
│   │   └── User.java
│   │
│   ├── reports/
│   │   └── PdfReportGenerator.java
│   │
│   ├── services/
│   │   ├── AuthenticationService.java
│   │   ├── BookingService.java
│   │   ├── CancelService.java
│   │   ├── CancellationService.java
│   │   ├── CoachService.java
│   │   ├── FareService.java
│   │   ├── PaymentService.java
│   │   ├── ReportData.java
│   │   ├── ReportService.java
│   │   ├── RouteService.java
│   │   ├── ScheduleService.java
│   │   ├── SeatService.java
│   │   └── TrainService.java
│   │
│   ├── ui/
│   │   ├── AdminDashboard.java
│   │   ├── AdminReportsFrame.java
│   │   ├── BookingForm.java
│   │   ├── CancelBookingForm.java
│   │   ├── CancelRequestForm.java
│   │   ├── LoginForm.java
│   │   ├── ManageCoachesUI.java
│   │   ├── ManageFaresUI.java
│   │   ├── ManageRoutesUI.java
│   │   ├── ManageStationsUI.java
│   │   ├── ManageTrainRunsUI.java
│   │   ├── ManageTrainsUI.java
│   │   ├── MyBookingsForm.java
│   │   ├── MyTicketsForm.java
│   │   ├── PassengerBookingHistory.java
│   │   ├── PassengerBookings.java
│   │   ├── PassengerDashboard.java
│   │   ├── PnrStatusForm.java
│   │   ├── RefundForm.java
│   │   ├── RefundRequestForm.java
│   │   ├── ReportGenerator.java
│   │   ├── SearchTrainsForm.java
│   │   ├── SignupForm.java
│   │   ├── TicketBookingForm.java
│   │   ├── TicketReceiptDialog.java
│   │   └── TicketView.java
│   │
│   └── resources/
│       └── images/
│
├── nbproject/
├── build.xml
├── manifest.mf
└── README.md
```

### Package responsibilities

| Package | Responsibility |
|---|---|
| `app` | Application entry point |
| `db` | Database connectivity and ERD |
| `models` | Data/domain objects |
| `services` | Business logic and database operations |
| `ui` | Java Swing user interfaces |
| `reports` | PDF report generation |
| `resources` | Application images and visual assets |

---

# 🧰 Technology Stack

| Technology | Purpose |
|---|---|
| Java 24 | Application programming language/runtime target |
| Java Swing | Desktop graphical user interface |
| MySQL | Relational database |
| JDBC | Java-to-MySQL database communication |
| Apache NetBeans | Project development environment |
| Apache Ant | Build system |
| MySQL Connector/J 9.4.0 | MySQL JDBC driver |
| jBCrypt 0.4 | Password hashing |
| iText 5.5.5 | PDF generation |
| JDatePicker 1.3.4 | Date selection UI |
| Git/GitHub | Version control and collaboration |

---

# 🗄️ Database Design

The application expects a MySQL database named:

```text
train_ticket_db
```

The application code references entities including:

```text
roles
users
passengers
stations
routes
trains
train_runs
coaches
seats
seat_classes
fares
bookings
tickets
payments
pnr_status
cancellations
refunds
```

A simplified relationship model is:

```text
ROLES
  │
  └── USERS
        │
        └── PASSENGERS
              │
              └── BOOKINGS
                    │
                    ├── TICKETS
                    │      └── SEATS
                    │           └── COACHES
                    │                └── TRAINS
                    │
                    ├── PAYMENTS
                    │
                    ├── CANCELLATIONS
                    │
                    └── REFUNDS

STATIONS
   │
   └── ROUTES
          │
          ├── TRAIN_RUNS
          │      └── TRAINS
          │
          └── FARES
                 └── SEAT_CLASSES
```

The project contains a MySQL Workbench model:

```text
src/db/erd.mwb
```

### Important database note

The uploaded project contains the ERD model and Java database-access code, but does **not** include a standalone `.sql` database schema/dump.

Therefore, before running the application on a new computer, the database schema must be created and populated according to the ERD and the SQL statements used by the services.

At minimum, the database must contain the tables and columns expected by the application.

---

# 🔄 Application Workflow

A typical passenger booking follows this process:

```text
Register
   │
   ▼
Login
   │
   ▼
Passenger Dashboard
   │
   ▼
Search Train
   │
   ▼
Select Train Run
   │
   ▼
Select Class
   │
   ▼
Select Available Seat
   │
   ▼
Calculate Fare
   │
   ▼
Enter Payment Details
   │
   ▼
Create Booking
   │
   ├── Create PNR
   ├── Create Booking
   ├── Create Ticket
   └── Create Payment
   │
   ▼
Confirmed Ticket
```

---

# ⚙️ Prerequisites

Before installing the application, ensure that the following are available:

### Required

- Java Development Kit compatible with the configured Java 24 source/target.
- Apache NetBeans.
- MySQL Server.
- MySQL Workbench — recommended for database administration.
- Git — recommended for version control.

### Required Java libraries

The project currently references:

- MySQL Connector/J 9.4.0
- jBCrypt 0.4
- iText 5.5.5
- JDatePicker 1.3.4

---

# 🚀 Installation and Setup

## 1. Clone the repository

```bash
git clone <YOUR_REPOSITORY_URL>
cd TrainBookingSystem-Main
```

Or download the project ZIP and extract it.

---

## 2. Install Java

Verify Java:

```bash
java -version
```

Verify the Java compiler:

```bash
javac -version
```

The NetBeans project is configured with:

```text
javac.source=24
javac.target=24
```

Therefore, use a compatible JDK.

---

## 3. Install MySQL

Make sure MySQL Server is running.

On Linux, for example:

```bash
sudo systemctl status mysql
```

Start it if necessary:

```bash
sudo systemctl start mysql
```

---

## 4. Create the database

Create the expected database:

```sql
CREATE DATABASE train_ticket_db;
```

Then select it:

```sql
USE train_ticket_db;
```

Create/import the required tables according to:

```text
src/db/erd.mwb
```

and the SQL statements contained in the service classes.

> **Recommended improvement:** add a version-controlled `database/schema.sql` and `database/seed.sql` to the repository. This makes the project much easier for other developers to install.

---

# 🔌 Database Configuration

The current connection class is:

```text
src/db/DBConnection.java
```

It currently uses:

```java
private static final String URL =
    "jdbc:mysql://localhost:3306/train_ticket_db?useSSL=false&serverTimezone=UTC";

private static final String USER = "root";

private static final String PASS = "";
```

This means the application expects:

```text
Host: localhost
Port: 3306
Database: train_ticket_db
Username: root
Password: empty
```

### ⚠️ Security recommendation

Do **not** use an empty MySQL root password in a production environment.

A better configuration is:

```text
Application
    ↓
Environment variables / configuration file
    ↓
Dedicated MySQL user
    ↓
train_ticket_db
```

For example:

```text
DB_HOST=localhost
DB_PORT=3306
DB_NAME=train_ticket_db
DB_USER=train_app
DB_PASSWORD=<secure-password>
```

The application should then read these values instead of storing credentials directly in source code.

---

# 🖥️ NetBeans Configuration

Open the project in Apache NetBeans.

Use:

```text
File
  → Open Project
  → TrainBookingSystem-Main
```

The project is a NetBeans/Ant project because it contains:

```text
nbproject/
build.xml
manifest.mf
```

### Add required libraries

The original project configuration contains absolute Windows paths such as:

```text
C:\Users\ssemu\...
```

These paths will not work on another computer.

Update the NetBeans project libraries/classpath so that they point to locally installed copies of:

```text
mysql-connector-j-9.4.0.jar
jbcrypt-0.4.jar
itextpdf-5.5.5.jar
jdatepicker-1.3.4.jar
```

This is especially important when moving the project between:

- Windows and Linux.
- Different developer machines.
- Different NetBeans installations.
- GitHub and fresh clones.

---

# ▶️ Running the Application

The main application class is:

```text
app.Main
```

It launches:

```text
LoginForm
```

The simplest method is to run the project through NetBeans:

```text
Run → Run Project
```

The application should open the login screen.

---

## Running from the command line

The project uses Apache Ant.

After configuring the required dependencies:

```bash
ant clean
ant
```

The generated JAR is configured as:

```text
dist/TrainBookingSystem.jar
```

Depending on the NetBeans configuration and manifest settings, the application can then be launched using the generated distribution.

---

# 👤 Passenger Workflow

## 1. Registration

A new passenger provides registration information such as:

- Username.
- Password.
- Full name.
- Gender.
- Date of birth.

The password is hashed using BCrypt before it is stored.

---

## 2. Login

The login process:

1. Receives username and password.
2. Finds the corresponding user.
3. Retrieves the stored password hash.
4. Checks the password using BCrypt.
5. Retrieves the user's role.
6. Opens the appropriate dashboard.

---

## 3. Search for trains

Passengers can search available train runs based on station and travel requirements.

The system retrieves information including:

- Train name.
- Departure time.
- Arrival time.
- Duration.
- Status.

---

## 4. Select seat

The passenger selects:

```text
Train Run
     ↓
Seat Class
     ↓
Available Seat
```

The system checks tickets already associated with the selected train run so that an occupied seat is not selected again.

---

## 5. Complete booking

The booking service creates:

- Booking.
- PNR.
- Ticket.
- Payment record.

The booking status is initially:

```text
Confirmed
```

and the ticket status is:

```text
Active
```

---

# 👨‍💼 Administrator Workflow

The administrator dashboard provides access to management functions.

Typical setup order is:

```text
1. Create Stations
        ↓
2. Create Routes
        ↓
3. Create Trains
        ↓
4. Create Coaches
        ↓
5. Generate Seats
        ↓
6. Configure Fares
        ↓
7. Create Train Runs
        ↓
8. Accept Passenger Bookings
        ↓
9. Monitor Reports
```

This order is important because later entities depend on earlier entities.

For example:

```text
Route → Train Run
Train → Coach
Coach → Seat
Route + Seat Class → Fare
Train Run + Seat → Booking
```

---

# 💳 Booking and Payment Logic

The central booking logic is implemented by:

```text
services/BookingService.java
```

The system supports booking methods with either:

- Automatically selected available seat.
- A passenger-requested specific seat.

The booking process performs database operations involving:

```text
passengers
      ↓
bookings
      ↓
tickets
      ↓
payments
      ↓
pnr_status
```

The PNR is generated after the booking is validated.

---

# ❌ Cancellation and Refund Workflow

The cancellation system contains two major stages.

### Stage 1 — Passenger request

The passenger submits:

```text
Booking
Reason
```

The request is stored with:

```text
Pending
```

status.

### Stage 2 — Administrator processing

The administrator processes the request.

The system can then:

```text
Cancellation Request
        ↓
Booking = Cancelled
        ↓
Ticket = Cancelled
        ↓
Refund Record
        ↓
Payment = Refunded
        ↓
Cancellation = Processed
```

This separates a passenger's request from administrative approval/processing.

---

# 📊 Reporting

Reporting is implemented through:

```text
services/ReportService.java
```

and:

```text
reports/PdfReportGenerator.java
```

Supported report categories include:

### Train schedules

Provides scheduled train-run information for a selected date range.

### Booking summary

Summarizes booking activity.

### Top routes

Identifies routes with significant booking activity.

### Cancellations

Displays cancellation requests and their statuses.

### Train utilization

Provides information that can be used to evaluate train/seat utilization.

### Passenger booking history

Shows an individual passenger's booking history.

---

## PDF Generation

PDF reports are generated using:

```text
iText 5.5.5
```

The generated PDF can contain:

- Report title.
- Logo.
- Report parameters.
- Column headings.
- Data rows.
- Footer information.

---

# 🔐 Security

The project already contains some useful security practices.

## Password hashing

Passwords are hashed using BCrypt:

```java
BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
```

Login uses:

```java
BCrypt.checkpw(password, hash);
```

This is preferable to storing plain-text passwords.

---

## Prepared statements

The application frequently uses:

```java
PreparedStatement
```

for user-supplied values.

This helps reduce SQL injection risk.

---

## Role-based access

Users have roles stored in the database.

The authentication service retrieves:

```text
role_name
```

and uses the authenticated user's role to determine the appropriate application interface.

---

## Security improvements recommended

For a production deployment, consider:

- Moving database credentials out of source code.
- Using a dedicated database account rather than MySQL `root`.
- Enforcing stronger password policies.
- Adding account lockout/rate limiting.
- Validating all user input.
- Adding authorization checks at the service layer.
- Avoiding sensitive information in application logs.
- Using transactions consistently for multi-step booking operations.
- Adding audit logging for administrator actions.
- Updating legacy dependencies where practical.

---

# ⚠️ Important Configuration Notes

## 1. Absolute dependency paths

The NetBeans project currently contains dependency paths tied to one Windows machine.

For example:

```text
C:\Users\ssemu\...
```

A fresh clone will therefore require classpath correction.

### Recommended solution

Use a dependency manager such as:

- Maven, or
- Gradle.

This would allow dependencies to be declared centrally rather than stored as local machine paths.

---

## 2. Database credentials are hard-coded

Current code contains:

```text
root
empty password
```

This should be changed before production use.

---

## 3. Database schema script is missing

The project contains:

```text
src/db/erd.mwb
```

but no standalone SQL initialization script is included.

A future version should contain:

```text
database/
├── schema.sql
├── seed.sql
└── README.md
```

---

## 4. Payment gateway

The current payment functionality records payment details in the database.

It does not appear to integrate with an external payment provider.

For a production railway booking platform, an actual payment provider would need to be integrated.

---

## 5. PNR generation

The current PNR is based on the system timestamp:

```text
PNR + System.currentTimeMillis()
```

A production implementation should use a stronger unique identifier strategy and enforce uniqueness at the database level.

---

# 🛠️ Troubleshooting

## MySQL connection error

Check:

```bash
sudo systemctl status mysql
```

Then verify:

- MySQL is running.
- Port `3306` is available.
- `train_ticket_db` exists.
- Username/password are correct.
- Required tables exist.

---

## MySQL JDBC driver not found

If you see an error similar to:

```text
MySQL JDBC driver not found
```

verify that:

```text
mysql-connector-j-9.4.0.jar
```

is included in the project classpath.

---

## BCrypt class not found

If you see:

```text
ClassNotFoundException
org.mindrot.jbcrypt.BCrypt
```

add:

```text
jbcrypt-0.4.jar
```

to the classpath.

---

## iText errors

If PDF reporting fails, verify that the iText 5.5.5 libraries are correctly configured.

The main library required for PDF generation is:

```text
itextpdf-5.5.5.jar
```

---

## JDatePicker errors

If date selection components fail to compile, verify:

```text
jdatepicker-1.3.4.jar
```

is included.

---

## Application starts but cannot log in

Check:

1. MySQL is running.
2. The database exists.
3. The `roles` table contains the expected roles.
4. The user exists.
5. The password hash is valid.
6. The user is associated with the correct role.

---

# 📈 Possible Improvements

The project can be improved significantly as a production-quality application.

## Architecture

Move toward:

```text
UI
 ↓
Controllers
 ↓
Services
 ↓
Repositories / DAOs
 ↓
Database
```

Currently, several service classes directly execute SQL. Introducing a repository/DAO layer would improve maintainability and testability.

---

## Dependency management

Migrate from NetBeans local JAR references to Maven or Gradle.

For example:

```text
pom.xml
```

could centrally manage:

- MySQL Connector/J.
- BCrypt.
- iText.
- JDatePicker.
- Testing libraries.

---

## Configuration management

Replace hard-coded database settings with environment variables or an external configuration file.

---

## Database migration

Introduce a migration system such as:

- Flyway.
- Liquibase.

This would make database versioning easier.

---

## Testing

Add:

```text
Unit tests
Integration tests
Database tests
UI tests
```

Important areas to test include:

- Login.
- Registration.
- Fare calculation.
- Seat allocation.
- Duplicate seat prevention.
- Booking creation.
- Cancellation.
- Refund processing.
- Report generation.

---

## Transaction management

Booking is a multi-step operation.

A robust implementation should ensure that:

```text
Booking
Ticket
Payment
PNR
```

are created atomically.

If one operation fails, the entire booking transaction should be rolled back.

---

# 🚀 Future Enhancements

Potential future versions could include:

### Passenger features

- Online payment gateway.
- Email/SMS ticket notifications.
- QR-code tickets.
- Ticket printing.
- Passenger profile management.
- Automatic booking confirmation.
- Travel reminders.

### Administrator features

- Advanced dashboards.
- Real-time train occupancy.
- Revenue analytics.
- User management.
- Audit logs.
- Automated refund management.
- Export to CSV/Excel.

### System features

- REST API.
- Web application.
- Mobile application.
- Cloud database.
- Real-time notifications.
- Multi-language support.
- Automated database backups.
- Containerized deployment with Docker.

---

# 👨‍💻 Development Guidelines

When extending the project:

### UI code

Place Swing forms and visual components under:

```text
src/ui/
```

### Business logic

Place business operations under:

```text
src/services/
```

### Data models

Place domain models under:

```text
src/models/
```

### Database connectivity

Keep connection management under:

```text
src/db/
```

### Reporting

Keep PDF/report-generation functionality under:

```text
src/reports/
```

Avoid putting large amounts of database/business logic directly inside Swing event handlers.

---

# 🌿 Recommended Git Workflow

For team development:

```text
main
 │
 ├── feature/authentication
 ├── feature/booking
 ├── feature/admin-management
 ├── feature/reporting
 └── feature/database
```

Recommended workflow:

```bash
git checkout -b feature/your-feature
```

Make changes, test them, then:

```bash
git add .
git commit -m "feat: implement train search"
git push -u origin feature/your-feature
```

Then create a Pull Request into `main`.

Avoid committing:

```text
database passwords
IDE private configuration
compiled build files
machine-specific paths
crash logs
temporary files
```

---

# 📦 Files That Should Not Normally Be Committed

The current project archive contains generated/runtime files such as:

```text
build/
hs_err_pid*.log
replay_pid*.log
nbproject/private/
```

These should generally be excluded from version control.

A suitable `.gitignore` should include entries such as:

```gitignore
/build/
/dist/
/nbproject/private/
/*.log
/hs_err_pid*.log
/replay_pid*.log
*.class
```

Keep source code and required project configuration under version control.

---

# 🧪 Suggested Test Checklist

Before considering a release, verify:

### Authentication

- [ ] Passenger can register.
- [ ] Password is hashed.
- [ ] Correct password logs in.
- [ ] Incorrect password is rejected.
- [ ] Correct dashboard opens for each role.

### Train management

- [ ] Station can be created.
- [ ] Route can be created.
- [ ] Train can be created.
- [ ] Coach can be created.
- [ ] Seats can be generated.
- [ ] Train run can be scheduled.
- [ ] Fare can be configured.

### Booking

- [ ] Available trains can be searched.
- [ ] Available seats are displayed.
- [ ] Occupied seats cannot be double-booked.
- [ ] Correct fare is calculated.
- [ ] Booking is created.
- [ ] Ticket is created.
- [ ] Payment record is created.
- [ ] PNR is generated.

### Cancellation

- [ ] Passenger can request cancellation.
- [ ] Request is stored as pending.
- [ ] Administrator can process it.
- [ ] Booking becomes cancelled.
- [ ] Ticket becomes cancelled.
- [ ] Refund is recorded.
- [ ] Payment status becomes refunded.

### Reporting

- [ ] Reports can be generated.
- [ ] Date filters work.
- [ ] PDF generation works.
- [ ] Report data matches database records.

---

# 📚 Learning Outcomes

This project demonstrates practical application of:

- Object-oriented programming.
- Java Swing GUI development.
- JDBC database programming.
- Relational database design.
- SQL.
- Authentication.
- Password hashing.
- Role-based access control.
- CRUD operations.
- Transaction-oriented booking workflows.
- Seat allocation.
- Payment record management.
- Cancellation and refund processing.
- PDF report generation.
- Layered software architecture.
- Git-based collaborative development.

It is therefore suitable as an academic software engineering/database project and as a foundation for a larger railway reservation platform.

---

# 🤝 Contributing

Contributions are welcome.

A typical contribution process is:

1. Fork the repository.
2. Create a feature branch.
3. Implement the feature.
4. Test the feature.
5. Commit the changes.
6. Push the branch.
7. Open a Pull Request.

Example:

```bash
git checkout -b feature/improve-seat-selection
git add .
git commit -m "feat: improve seat selection"
git push origin feature/improve-seat-selection
```

---

# 📄 License

No explicit open-source license is currently included in the project.

If this project is intended to be publicly distributed, add an appropriate license file such as:

```text
LICENSE
```

For example:

- MIT License.
- Apache License 2.0.
- GNU GPL v3.

Choose the license based on the project's intended use and ownership requirements.

---

# 👤 Author

**Ssemuli Joseph**

Computer Science / AI Engineering Student

GitHub:

```text
https://github.com/SsemuliJoseph
```

---

# ⭐ Project Summary

The **Train Booking System** is a Java Swing and MySQL desktop application designed to digitize railway ticket reservation and administration.

It brings together:

```text
Authentication
     +
Train Management
     +
Route Management
     +
Schedule Management
     +
Coach & Seat Management
     +
Fare Management
     +
Ticket Booking
     +
Payment Recording
     +
PNR Tracking
     +
Cancellation & Refunds
     +
Operational Reporting
```

The current implementation provides a strong academic foundation for a train reservation system while leaving clear opportunities for further development, particularly around portable dependency management, database initialization, secure configuration, automated testing, real payment integration, and production deployment.
