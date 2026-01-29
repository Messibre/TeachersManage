# Teachera – Teacher Management System (JavaFX + MySQL)

Teachera is a desktop Teacher Management System (JavaFX + MySQL) implemented as a layered
monolith. It demonstrates separation of concerns: UI (JavaFX) → Service → DAO → Model → DB.

Quick links
- Schema: [db/schema.sql](db/schema.sql)
- DB config: [src/main/resources/db.properties](src/main/resources/db.properties)

Prerequisites
- Java 17 or later installed and `JAVA_HOME` set
- Maven 3.6+ (for building)
- IntelliJ IDEA (recommended for running JavaFX apps)
- A MySQL-compatible database (local or cloud)

Database setup
1. Edit `src/main/resources/db.properties` and set your JDBC URL, username and password. Example fields are present there.
2. Initialize the schema using the provided SQL file:

```bash
mysql -h <host> -P <port> -u <user> -p <database> < db/schema.sql
```

3. Create an initial admin user. The application stores SHA-256 hex of passwords. Compute SHA-256 in PowerShell:

```powershell
$pw='Admin@123'; [System.BitConverter]::ToString((New-Object System.Security.Cryptography.SHA256Managed).ComputeHash([System.Text.Encoding]::UTF8.GetBytes($pw))).Replace('-','').ToLower()
```

Use the resulting hex string to insert an admin record (replace `<sha256>`):

```sql
INSERT INTO users (username, password_hash, role, active) VALUES ('admin','<sha256>','ADMIN',1);
```

Files
- Database schema: [db/schema.sql](db/schema.sql)
- DB config (edit before running): [src/main/resources/db.properties](src/main/resources/db.properties)

Running the app (recommended: IntelliJ)
1. Open the project in IntelliJ (File → Open... → select project folder).
2. Let Maven import dependencies.
3. Run the Java class `com.teachera.ui.TeacheraApp` as a Java application. IntelliJ will handle JavaFX classpath when project SDK is configured.

Running with Maven (alternative)
- You can `mvn clean compile` to build. Running JavaFX apps from Maven requires proper module/configuration; using the IDE is simpler.

Manual tests checklist
- Login: verify admin login redirects to Admin Dashboard
- Teacher CRUD: add, update, delete teacher records
- Attendance: create attendance, prevent duplicate date records
- Leave: request leave, approve/reject as admin
- Payroll: generate payroll for a teacher and month; verify net salary
- Reports: open Reports and verify data



