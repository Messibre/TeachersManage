package com.teachera.ui.teacher;

import com.teachera.model.Attendance;
import com.teachera.model.AttendanceStatus;
import com.teachera.model.EmploymentType;
import com.teachera.model.Teacher;
import com.teachera.model.Schedule;
import com.teachera.model.User;
import com.teachera.service.AttendanceService;
import com.teachera.service.ServiceException;
import com.teachera.service.TeacherService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.util.List;

public class TeacherManagementView {

    private final TeacherService teacherService;
    private final AttendanceService attendanceService;
    private com.teachera.service.LeaveService leaveService;
    private com.teachera.service.PayrollService payrollService;
    private com.teachera.service.ScheduleService scheduleService;
    private final User currentUser;
    private final Runnable onLogout;
    private final ObservableList<Teacher> teacherList = FXCollections.observableArrayList();

    public TeacherManagementView(TeacherService teacherService) {
        this(teacherService, null, null, null, null, null, null);
    }

    public TeacherManagementView(TeacherService teacherService,
            AttendanceService attendanceService,
            User currentUser,
            Runnable onLogout) {
        this(teacherService, attendanceService, null, null, null, currentUser, onLogout);
    }

    public TeacherManagementView(TeacherService teacherService,
            AttendanceService attendanceService,
            com.teachera.service.LeaveService leaveService,
            com.teachera.service.PayrollService payrollService,
            com.teachera.service.ScheduleService scheduleService,
            User currentUser,
            Runnable onLogout) {
        this.teacherService = teacherService;
        this.attendanceService = attendanceService;
        this.leaveService = leaveService;
        this.payrollService = payrollService;
        this.scheduleService = scheduleService;
        this.currentUser = currentUser;
        this.onLogout = onLogout;
    }

    public Node createContent() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));

        if (currentUser != null && currentUser.getRole() != null && currentUser.getRole().name().equals("TEACHER")) {
            return createTeacherSelfView();
        }

        ListView<Teacher> listView = new ListView<>(teacherList);
        listView.setPrefWidth(320);
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Teacher item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTeacherCode() + " — " + item.getFullName());
            }
        });

        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(8);

        TextField codeField = new TextField();
        TextField nameField = new TextField();
        ComboBox<EmploymentType> employmentBox = new ComboBox<>();
        employmentBox.getItems().addAll(EmploymentType.values());
        TextField baseSalaryField = new TextField();

        form.addRow(0, new Label("Code"), codeField);
        form.addRow(1, new Label("Full name"), nameField);
        form.addRow(2, new Label("Employment"), employmentBox);
        form.addRow(3, new Label("Base salary"), baseSalaryField);

        HBox actions = new HBox(8);
        Button addBtn = new Button("Add");
        Button updateBtn = new Button("Update");
        Button deleteBtn = new Button("Delete");
        Button refreshBtn = new Button("Refresh");
        Button logoutBtn = new Button("Logout");
        actions.getChildren().addAll(addBtn, updateBtn, deleteBtn, refreshBtn, logoutBtn);

        VBox right = new VBox(10, form, actions);

        root.setLeft(listView);
        root.setCenter(right);

        refreshList();

        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                codeField.setText(newV.getTeacherCode());
                nameField.setText(newV.getFullName());
                employmentBox.setValue(newV.getEmploymentType());
                baseSalaryField.setText(newV.getBaseSalary() == null ? "" : newV.getBaseSalary().toPlainString());
            }
        });

        addBtn.setOnAction(e -> {
            try {
                Teacher t = new Teacher();
                t.setTeacherCode(codeField.getText());
                t.setFullName(nameField.getText());
                t.setEmploymentType(employmentBox.getValue());
                t.setBaseSalary(parseMoney(baseSalaryField.getText()));
                teacherService.createTeacher(t);
                refreshList();
            } catch (ServiceException ex) {
                showAlert(ex.getMessage());
            }
        });

        updateBtn.setOnAction(e -> {
            Teacher sel = listView.getSelectionModel().getSelectedItem();
            if (sel == null) {
                showAlert("Select a teacher to update.");
                return;
            }
            try {
                sel.setTeacherCode(codeField.getText());
                sel.setFullName(nameField.getText());
                sel.setEmploymentType(employmentBox.getValue());
                sel.setBaseSalary(parseMoney(baseSalaryField.getText()));
                teacherService.updateTeacher(sel);
                refreshList();
            } catch (ServiceException ex) {
                showAlert(ex.getMessage());
            }
        });

        deleteBtn.setOnAction(e -> {
            Teacher sel = listView.getSelectionModel().getSelectedItem();
            if (sel == null) {
                showAlert("Select a teacher to delete.");
                return;
            }
            try {
                teacherService.deleteTeacher(sel.getId());
                refreshList();
            } catch (ServiceException ex) {
                showAlert(ex.getMessage());
            }
        });

        refreshBtn.setOnAction(e -> refreshList());

        logoutBtn.setOnAction(e -> {
            if (onLogout != null)
                onLogout.run();
        });

        return root;
    }

    private Node createTeacherSelfView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));

        Teacher me = teacherService.getAllTeachers().stream()
                .filter(t -> t.getUserId() != null && currentUser.getId() != null
                        && t.getUserId().equals(currentUser.getId()))
                .findFirst().orElse(null);

        VBox box = new VBox(10);
        box.setPadding(new Insets(8));

        if (me == null) {

            GridPane form = new GridPane();
            form.setHgap(8);
            form.setVgap(8);

            TextField fullNameField = new TextField();
            TextField emailField = new TextField();
            TextField phoneField = new TextField();
            TextField qualificationField = new TextField();
            qualificationField.setPromptText("Highest qualification");
            TextField yearsExpField = new TextField();
            yearsExpField.setPromptText("Years of experience");
            TextField preferredSubjectsField = new TextField();
            preferredSubjectsField.setPromptText("e.g. MATH, PHYS");

            form.addRow(0, new Label("Full Name:"), fullNameField);
            form.addRow(1, new Label("Email:"), emailField);
            form.addRow(2, new Label("Phone:"), phoneField);
            form.addRow(3, new Label("Qualification:"), qualificationField);
            form.addRow(4, new Label("Years Experience:"), yearsExpField);
            form.addRow(5, new Label("Preferred Subject(s):"), preferredSubjectsField);

            Button submitBtn = new Button("Submit Profile");
            Button logoutBtn = new Button("Logout");
            form.add(submitBtn, 1, 6);
            form.add(logoutBtn, 0, 6);

            submitBtn.setOnAction(e -> {
                try {
                    com.teachera.model.Teacher t = new com.teachera.model.Teacher();
                    t.setUserId(currentUser.getId());
                    t.setTeacherCode(null);
                    t.setFullName(fullNameField.getText());
                    t.setContactEmail(emailField.getText());
                    t.setContactPhone(phoneField.getText());

                    String qual = qualificationField.getText();
                    if (yearsExpField.getText() != null && !yearsExpField.getText().trim().isEmpty()) {
                        qual = qual + " | Exp:" + yearsExpField.getText().trim();
                    }
                    t.setQualification(qual);
                    t.setSubjectSpecialty(preferredSubjectsField.getText());
                    t.setBaseSalary(java.math.BigDecimal.ZERO);
                    t.setApproved(false);

                    teacherService.createTeacher(t);

                    Label ok = new Label("Profile submitted — pending admin approval.");
                    box.getChildren().clear();
                    box.getChildren().add(ok);
                } catch (ServiceException ex) {
                    showAlert(ex.getMessage());
                }
            });

            logoutBtn.setOnAction(e -> {
                if (onLogout != null)
                    onLogout.run();
            });

            box.getChildren().addAll(new Label("Complete your profile"), form);
            root.setCenter(box);
            return root;
        }

        TabPane tabs = new TabPane();

        VBox profileCard = new VBox(20);
        profileCard.getStyleClass().add("stat-card");
        profileCard.setPadding(new Insets(30));
        profileCard.setMaxWidth(600);
        profileCard.setAlignment(javafx.geometry.Pos.CENTER);

        Label nameLabel = new Label(me.getFullName());
        nameLabel.getStyleClass().add("profile-header");

        GridPane details = new GridPane();
        details.setHgap(20);
        details.setVgap(15);
        details.setAlignment(javafx.geometry.Pos.CENTER);

        addRow(details, 0, "Teacher Code:", me.getTeacherCode());
        addRow(details, 1, "Employment:", me.getEmploymentType() == null ? "N/A" : me.getEmploymentType().name());
        addRow(details, 2, "Base Salary:", me.getBaseSalary() == null ? "0.00" : me.getBaseSalary().toPlainString());

        profileCard.getChildren().addAll(nameLabel, details);

        StackPane profileContainer = new StackPane(profileCard);
        profileContainer.setPadding(new Insets(20));

        Tab profileTab = new Tab("Profile", profileContainer);
        profileTab.setClosable(false);

        TableView<Attendance> attTable = new TableView<>();
        TableColumn<Attendance, java.time.LocalDate> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(
                cd -> new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().getAttendanceDate()));
        TableColumn<Attendance, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(
                cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getStatus().name()));
        TableColumn<Attendance, String> colHours = new TableColumn<>("Hours");
        colHours.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getHoursWorked() == null ? "" : cd.getValue().getHoursWorked().toString()));
        TableColumn<Attendance, String> colRemarks = new TableColumn<>("Remarks");
        colRemarks.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getRemarks() == null ? "" : cd.getValue().getRemarks()));
        @SuppressWarnings("unchecked")
        TableColumn<Attendance, ?>[] cols = new TableColumn[] { colDate, colStatus, colHours, colRemarks };
        attTable.getColumns().addAll(cols);

        // Timeslot selector replaces free-text hours; class list is filtered by
        // timeslot/schedule
        String[] slotLabels = new String[] { "2:00 - 4:20", "4:30 - 6:15", "7:30 - 9:00", "9:10 - 11:00" };
        java.time.LocalTime[] slotStarts = new java.time.LocalTime[] {
                java.time.LocalTime.of(14, 0),
                java.time.LocalTime.of(16, 30),
                java.time.LocalTime.of(19, 30),
                java.time.LocalTime.of(21, 10)
        };
        java.time.LocalTime[] slotEnds = new java.time.LocalTime[] {
                java.time.LocalTime.of(16, 20),
                java.time.LocalTime.of(18, 15),
                java.time.LocalTime.of(21, 0),
                java.time.LocalTime.of(23, 0)
        };

        ComboBox<String> timeSlotBox = new ComboBox<>();
        timeSlotBox.getItems().addAll(slotLabels);
        timeSlotBox.setValue(slotLabels[0]);

        ComboBox<String> classSelect = new ComboBox<>();
        java.util.List<com.teachera.model.TeacherAssignment> assignments = teacherService
                .getAssignmentsForTeacher(me.getId());

        ComboBox<AttendanceStatus> statusBox = new ComboBox<>();
        statusBox.getItems().addAll(AttendanceStatus.values());
        statusBox.setValue(AttendanceStatus.PRESENT);

        TextField remarksField = new TextField();
        remarksField.setPromptText("Remarks");
        Button markBtn = new Button("Mark Attendance");
        // Populate classSelect based on selected timeslot and teacher schedules (falls
        // back to assignments)
        Runnable populateClasses = () -> {
            classSelect.getItems().clear();
            if (scheduleService != null) {
                try {
                    java.util.List<Schedule> schedules = scheduleService.getSchedulesForTeacher(me.getId());
                    int idx = java.util.Arrays.asList(slotLabels).indexOf(timeSlotBox.getValue());
                    if (idx < 0)
                        idx = 0;
                    java.time.LocalTime sStart = slotStarts[idx];
                    java.time.LocalTime sEnd = slotEnds[idx];
                    java.util.Set<String> found = new java.util.LinkedHashSet<>();
                    for (Schedule sch : schedules) {
                        if (overlaps(sStart, sEnd, sch.getStartTime(), sch.getEndTime())) {
                            found.add(sch.getClassName() + " — " + sch.getSubjectCode());
                        }
                    }
                    if (found.isEmpty()) {
                        for (com.teachera.model.TeacherAssignment a : assignments) {
                            found.add(a.getClassName() + " — " + a.getSubjectCode());
                        }
                    }
                    classSelect.getItems().addAll(found);
                    if (!classSelect.getItems().isEmpty())
                        classSelect.setValue(classSelect.getItems().get(0));
                } catch (Exception ex) {
                    for (com.teachera.model.TeacherAssignment a : assignments) {
                        classSelect.getItems().add(a.getClassName() + " — " + a.getSubjectCode());
                    }
                }
            } else {
                for (com.teachera.model.TeacherAssignment a : assignments) {
                    classSelect.getItems().add(a.getClassName() + " — " + a.getSubjectCode());
                }
            }
        };

        timeSlotBox.valueProperty().addListener((obs, ov, nv) -> populateClasses.run());
        populateClasses.run();

        markBtn.setOnAction(e -> {
            try {
                // Convert selected timeslot to hours decimal (e.g. 2:20 -> 2.33)
                int idx = java.util.Arrays.asList(slotLabels).indexOf(timeSlotBox.getValue());
                if (idx < 0)
                    idx = 0;
                java.time.LocalTime sStart = slotStarts[idx];
                java.time.LocalTime sEnd = slotEnds[idx];
                long mins = java.time.Duration.between(sStart, sEnd).toMinutes();
                java.math.BigDecimal hours = java.math.BigDecimal.valueOf(mins)
                        .divide(java.math.BigDecimal.valueOf(60), 2, java.math.RoundingMode.HALF_UP);

                attendanceService.recordAttendance(me.getId(), java.time.LocalDate.now(), statusBox.getValue(),
                        hours.toPlainString(), remarksField.getText());
                refreshAttendanceTable(me.getId(), attTable);
            } catch (ServiceException ex) {
                showAlert(ex.getMessage());
            }
        });
        HBox attControls = new HBox(8, new Label("Class:"), classSelect, new Label("Status:"), statusBox,
                new Label("Time Slot:"), timeSlotBox, remarksField, markBtn);

        VBox attBox = new VBox(8, attTable, attControls);
        attTable.setPrefHeight(400);
        attTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(attTable, Priority.ALWAYS);
        Tab attTab = new Tab("Attendance", attBox);
        attTab.setClosable(false);

        TableView<TimeRow> timetable = new TableView<>();
        timetable.setPrefHeight(320);

        TableColumn<TimeRow, String> timeLabelCol = new TableColumn<>("Time Range");
        timeLabelCol.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().label));
        timeLabelCol.setPrefWidth(120);
        timetable.getColumns().add(timeLabelCol);

        String[] weekdays = new String[] { "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY" };
        for (int i = 0; i < weekdays.length; i++) {
            final int idx = i;
            TableColumn<TimeRow, String> col = new TableColumn<>(
                    weekdays[i].substring(0, 1).toUpperCase() + weekdays[i].substring(1).toLowerCase());
            col.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getCell(idx)));
            col.setPrefWidth(180);
            timetable.getColumns().add(col);
        }

        if (scheduleService != null) {
            refreshTeacherTimetable(timetable, me.getId());
        } else {

            java.util.List<TimeRow> rows = new java.util.ArrayList<>();
            String[] labels = new String[] { "2:00 - 4:20", "4:30 - 6:15", "7:30 - 9:00", "9:10 - 11:00" };
            for (String lab : labels)
                rows.add(new TimeRow(lab));
            int colIndex = 0;
            for (com.teachera.model.TeacherAssignment a : assignments) {
                String txt = a.getClassName() + "\n" + a.getSubjectCode();
                rows.get(0).putCell(colIndex % 5, txt);
                colIndex++;
            }
            timetable.getItems().setAll(rows);
        }
        Tab schedTab = new Tab("Schedule", timetable);
        schedTab.setClosable(false);

        VBox leaveBox = new VBox(8);
        if (leaveService != null) {
            DatePicker start = new DatePicker();
            DatePicker end = new DatePicker();
            ComboBox<com.teachera.model.LeaveType> typeBox = new ComboBox<>();
            typeBox.getItems().addAll(com.teachera.model.LeaveType.values());
            TextField reason = new TextField();
            Button reqBtn = new Button("Request Leave");
            ListView<com.teachera.model.Leave> leaveLv = new ListView<>();
            reqBtn.setOnAction(ev -> {
                try {
                    com.teachera.model.Leave l = leaveService.requestLeave(me.getId(), start.getValue(), end.getValue(),
                            typeBox.getValue(), reason.getText());
                    leaveLv.getItems().add(l);
                } catch (ServiceException ex) {
                    showAlert(ex.getMessage());
                }
            });
            leaveBox.getChildren().addAll(new Label("Request Leave"), new HBox(8, new Label("Start"), start,
                    new Label("End"), end, new Label("Type"), typeBox, reason, reqBtn), new Label("Your Leaves"),
                    leaveLv);
            leaveLv.getItems().setAll(leaveService.getLeavesForTeacher(me.getId()));
        } else {
            leaveBox.getChildren().add(new Label("Leave feature not available."));
        }
        Tab leaveTab = new Tab("Leave", leaveBox);
        leaveTab.setClosable(false);

        VBox reportsBox = new VBox(8);

        java.util.List<Attendance> allAtt = attendanceService == null ? java.util.List.of()
                : attendanceService.getAttendanceForTeacher(me.getId());
        long present = allAtt.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
        long absent = allAtt.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count();
        reportsBox.getChildren().addAll(new Label("Attendance Summary:"),
                new Label("Present: " + present + "  Absent: " + absent));
        if (payrollService != null) {
            java.util.List<com.teachera.model.Payroll> pays = payrollService.getPayrollForTeacher(me.getId());
            reportsBox.getChildren().add(new Label("Payroll records: " + pays.size()));
        }
        Tab reportsTab = new Tab("Reports", reportsBox);
        reportsTab.setClosable(false);

        tabs.getTabs().addAll(profileTab, attTab, schedTab, leaveTab, reportsTab);

        if (attendanceService != null)
            refreshAttendanceTable(me.getId(), attTable);

        Button logoutBtnSelf = new Button("Logout");
        logoutBtnSelf.setOnAction(e -> {
            if (onLogout != null)
                onLogout.run();
        });

        VBox content = new VBox(10, tabs, logoutBtnSelf);
        root.setCenter(content);
        return root;
    }

    private void refreshAttendanceList(int teacherId, ListView<Attendance> lv) {
        if (attendanceService == null)
            return;
        java.util.List<Attendance> list = attendanceService.getAttendanceForTeacher(teacherId);
        lv.getItems().setAll(list);
    }

    private void refreshAttendanceTable(int teacherId, TableView<Attendance> tv) {
        if (attendanceService == null)
            return;
        java.util.List<Attendance> list = attendanceService.getAttendanceForTeacher(teacherId);
        tv.getItems().setAll(list);
    }

    private void refreshTeacherTimetable(TableView<TimeRow> timetable, int teacherId) {
        java.util.List<Schedule> schedules;
        try {
            schedules = scheduleService.getSchedulesForTeacher(teacherId);
            if (schedules == null)
                schedules = java.util.Collections.emptyList();
        } catch (Exception ex) {
            ex.printStackTrace();
            schedules = java.util.Collections.emptyList();
        }

        java.time.LocalTime[] startTimes = new java.time.LocalTime[] {
                java.time.LocalTime.of(14, 0),
                java.time.LocalTime.of(16, 30),
                java.time.LocalTime.of(19, 30),
                java.time.LocalTime.of(21, 10)
        };
        java.time.LocalTime[] endTimes = new java.time.LocalTime[] {
                java.time.LocalTime.of(16, 20),
                java.time.LocalTime.of(18, 15),
                java.time.LocalTime.of(21, 0),
                java.time.LocalTime.of(23, 0)
        };
        String[] labels = new String[] { "2:00 - 4:20", "4:30 - 6:15", "7:30 - 9:00", "9:10 - 11:00" };

        java.util.List<TimeRow> rows = new java.util.ArrayList<>();
        for (String lab : labels)
            rows.add(new TimeRow(lab));

        java.util.Map<Integer, Teacher> teacherMap = new java.util.HashMap<>();
        for (Teacher t : teacherService.getAllTeachers())
            teacherMap.put(t.getId(), t);

        for (Schedule s : schedules) {
            int dayIndex = switch (s.getDayOfWeek()) {
                case "MONDAY" -> 0;
                case "TUESDAY" -> 1;
                case "WEDNESDAY" -> 2;
                case "THURSDAY" -> 3;
                case "FRIDAY" -> 4;
                default -> -1;
            };
            if (dayIndex < 0)
                continue;

            for (int slot = 0; slot < startTimes.length; slot++) {
                if (overlaps(startTimes[slot], endTimes[slot], s.getStartTime(), s.getEndTime())) {
                    StringBuilder sb = new StringBuilder();
                    Teacher tt = teacherMap.get(s.getTeacherId());
                    if (tt != null)
                        sb.append(tt.getTeacherCode()).append(" - ").append(tt.getFullName()).append("\n");
                    if (s.getSections() != null && !s.getSections().isEmpty())
                        sb.append("Section:").append(s.getSections().toString()).append("\n");
                    sb.append(s.getSubjectCode());
                    rows.get(slot).putCell(dayIndex, sb.toString());
                }
            }
        }

        timetable.getItems().setAll(rows);
    }

    private static boolean overlaps(java.time.LocalTime aStart, java.time.LocalTime aEnd, java.time.LocalTime bStart,
            java.time.LocalTime bEnd) {
        if (aStart == null || aEnd == null || bStart == null || bEnd == null)
            return false;
        return bStart.isBefore(aEnd) && aStart.isBefore(bEnd);
    }

    private static class TimeRow {
        final String label;
        final java.util.Map<Integer, String> cells = new java.util.HashMap<>();

        TimeRow(String label) {
            this.label = label;
        }

        void putCell(int dayIndex, String text) {
            cells.put(dayIndex, text);
        }

        String getCell(int dayIndex) {
            return cells.getOrDefault(dayIndex, "");
        }
    }

    private void refreshList() {
        List<Teacher> all = teacherService.getAllTeachers();
        teacherList.setAll(all);
    }

    private BigDecimal parseMoney(String txt) {
        if (txt == null || txt.trim().isEmpty())
            return BigDecimal.ZERO;
        try {
            return new BigDecimal(txt.trim());
        } catch (NumberFormatException ex) {
            throw new ServiceException("Invalid money value: " + txt);
        }
    }

    private void addRow(GridPane grid, int row, String label, String value) {
        Label l = new Label(label);
        l.getStyleClass().add("data-label");
        Label v = new Label(value);
        v.getStyleClass().add("data-value");
        grid.addRow(row, l, v);
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.showAndWait();
    }
}
