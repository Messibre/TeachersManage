

CREATE DATABASE IF NOT EXISTS teacheradb
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE teacheradb;


CREATE TABLE IF NOT EXISTS users (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          ENUM('ADMIN', 'TEACHER') NOT NULL,
    active        TINYINT(1)   NOT NULL DEFAULT 1,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);



CREATE TABLE IF NOT EXISTS teachers (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    user_id          INT NULL UNIQUE,
    teacher_code     VARCHAR(20)  NOT NULL UNIQUE,
    full_name        VARCHAR(100) NOT NULL,
    qualification    VARCHAR(100) NULL,
    employment_type  ENUM('FULL_TIME', 'PART_TIME', 'CONTRACT') NOT NULL,
    subject_specialty VARCHAR(100) NULL,
    contact_email    VARCHAR(100) NULL,
    contact_phone    VARCHAR(30)  NULL,
    base_salary      DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    hire_date        DATE NULL,
    approved         TINYINT(1)   NOT NULL DEFAULT 0,
    CONSTRAINT fk_teachers_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);



CREATE TABLE IF NOT EXISTS subjects (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    code        VARCHAR(20)  NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(255) NULL
);

CREATE TABLE IF NOT EXISTS teacher_assignments (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    teacher_id  INT         NOT NULL,
    subject_code VARCHAR(20) NOT NULL,
    class_name  VARCHAR(50) NOT NULL,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_assignment_teacher FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE IF NOT EXISTS schedules (
    id INT AUTO_INCREMENT PRIMARY KEY,
    teacher_id INT NOT NULL,
    day_of_week VARCHAR(10) NOT NULL,
    class_name VARCHAR(50) NOT NULL,
    subject_code VARCHAR(20) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_schedules_teacher FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE IF NOT EXISTS schedule_sections (
    id INT AUTO_INCREMENT PRIMARY KEY,
    schedule_id INT NOT NULL,
    section TINYINT NOT NULL,
    CONSTRAINT fk_schedule_sections_schedule FOREIGN KEY (schedule_id) REFERENCES schedules(id) ON DELETE CASCADE ON UPDATE CASCADE
);



CREATE TABLE IF NOT EXISTS attendance (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    teacher_id   INT         NOT NULL,
    attendance_date DATE     NOT NULL,
    status       ENUM('PRESENT', 'ABSENT', 'LATE', 'ON_LEAVE') NOT NULL,
    hours_worked DECIMAL(4, 2) NULL,
    remarks      VARCHAR(255) NULL,
    created_at   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_attendance_teacher
        FOREIGN KEY (teacher_id) REFERENCES teachers (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT uq_attendance_teacher_date
        UNIQUE (teacher_id, attendance_date)
);



CREATE TABLE IF NOT EXISTS leaves (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    teacher_id    INT         NOT NULL,
    start_date    DATE        NOT NULL,
    end_date      DATE        NOT NULL,
    leave_type    ENUM('SICK', 'CASUAL', 'ANNUAL', 'UNPAID', 'OTHER') NOT NULL,
    status        ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    reason        VARCHAR(255) NULL,
    requested_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reviewed_by   INT NULL,
    reviewed_at   TIMESTAMP NULL,
    CONSTRAINT fk_leaves_teacher
        FOREIGN KEY (teacher_id) REFERENCES teachers (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_leaves_reviewer
        FOREIGN KEY (reviewed_by) REFERENCES users (id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);



CREATE TABLE IF NOT EXISTS payroll (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    teacher_id       INT         NOT NULL,
    period_year      INT         NOT NULL,
    period_month     TINYINT     NOT NULL, -- 1 to 12
    base_salary      DECIMAL(10, 2) NOT NULL,
    overtime_hours   DECIMAL(6, 2)  NOT NULL DEFAULT 0.00,
    overtime_amount  DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    deductions       DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    net_salary       DECIMAL(10, 2) NOT NULL,
    generated_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payroll_teacher
        FOREIGN KEY (teacher_id) REFERENCES teachers (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT uq_payroll_teacher_period
        UNIQUE (teacher_id, period_year, period_month)
);

