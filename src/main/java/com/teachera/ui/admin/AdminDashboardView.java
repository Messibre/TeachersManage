package com.teachera.ui.admin;

import com.teachera.service.AttendanceService;
import com.teachera.service.LeaveService;
import com.teachera.service.PayrollService;
import com.teachera.service.TeacherService;
import com.teachera.ui.teacher.TeacherManagementView;
import com.teachera.ui.leave.LeaveManagementView;
import com.teachera.ui.payroll.PayrollManagementView;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboardView {

    private final TeacherService teacherService;
    private final AttendanceService attendanceService;
    private final LeaveService leaveService;
    private final PayrollService payrollService;
    private final com.teachera.service.ScheduleService scheduleService;
    private final Runnable onLogout;

    private StackPane contentArea;
    private List<Button> navButtons = new ArrayList<>();

    public AdminDashboardView(TeacherService teacherService,
            AttendanceService attendanceService,
            LeaveService leaveService,
            PayrollService payrollService,
            com.teachera.service.ScheduleService scheduleService,
            Runnable onLogout) {
        this.teacherService = teacherService;
        this.attendanceService = attendanceService;
        this.leaveService = leaveService;
        this.payrollService = payrollService;
        this.scheduleService = scheduleService;
        this.onLogout = onLogout;
    }

    public Node createContent() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-pane");

        VBox sideNav = new VBox();
        sideNav.getStyleClass().add("side-nav");

        Text appTitle = new Text("Teachera");
        appTitle.getStyleClass().add("app-title");

        Text roleTitle = new Text("ADMIN PORTAL");
        roleTitle.setStyle("-fx-fill: white; -fx-font-size: 10px; -fx-font-weight: bold; -fx-opacity: 0.7;");

        VBox header = new VBox(5, appTitle, roleTitle);
        header.setPadding(new Insets(0, 0, 20, 0));

        Button teachersBtn = createNavButton("Teachers");
        Button approvalsBtn = createNavButton("Pending Approvals");
        Button scheduleBtn = createNavButton("Schedule");
        Button attendanceBtn = createNavButton("Attendance");
        Button leavesBtn = createNavButton("Leaves");
        Button payrollBtn = createNavButton("Payroll");
        Button reportsBtn = createNavButton("Reports");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = createNavButton("Logout");
        logoutBtn.getStyleClass().add("nav-button-logout");
        logoutBtn.setOnAction(e -> {
            if (onLogout != null)
                onLogout.run();
        });

        sideNav.getChildren().addAll(header, teachersBtn, approvalsBtn, scheduleBtn, attendanceBtn, leavesBtn,
                payrollBtn, reportsBtn, spacer, logoutBtn);
        root.setLeft(sideNav);

        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");
        Text welcomeText = new Text("Welcome to Teachera Admin Dashboard");
        welcomeText.setStyle("-fx-font-size: 18px; -fx-fill: -text-color;");
        contentArea.getChildren().add(welcomeText);

        root.setCenter(contentArea);

        teachersBtn.setOnAction(e -> {
            setActive(teachersBtn);
            setContent(new TeacherManagementView(teacherService, attendanceService, null, onLogout).createContent());
        });

        approvalsBtn.setOnAction(e -> {
            setActive(approvalsBtn);
            showApprovalsView();
        });

        scheduleBtn.setOnAction(e -> {
            setActive(scheduleBtn);
            setContent(
                    new com.teachera.ui.admin.ScheduleManagementView(scheduleService, teacherService).createContent());
        });

        attendanceBtn.setOnAction(e -> {
            setActive(attendanceBtn);
            setContent(new com.teachera.ui.attendance.AttendanceManagementView(attendanceService, teacherService)
                    .createContent());
        });

        leavesBtn.setOnAction(e -> {
            setActive(leavesBtn);
            setContent(new LeaveManagementView(leaveService).createContent());
        });

        payrollBtn.setOnAction(e -> {
            setActive(payrollBtn);
            setContent(new PayrollManagementView(payrollService, teacherService).createContent());
        });

        reportsBtn.setOnAction(e -> {
            setActive(reportsBtn);
            setContent(new com.teachera.ui.reports.ReportsView(teacherService, attendanceService, payrollService)
                    .createContent());
        });

        return root;
    }

    private Button createNavButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("nav-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        navButtons.add(btn);
        return btn;
    }

    private void setActive(Button activeBtn) {
        for (Button btn : navButtons) {
            btn.getStyleClass().remove("active");
        }
        activeBtn.getStyleClass().add("active");
    }

    private void setContent(Node node) {
        contentArea.getChildren().setAll(node);
    }

    private void showApprovalsView() {
        VBox panel = new VBox(15);
        panel.getStyleClass().add("card");

        HBox headerBox = new HBox();
        Text title = new Text("Pending Teacher Approvals");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: -primary-color;");
        headerBox.getChildren().add(title);

        ListView<com.teachera.model.Teacher> lv = new ListView<>();
        lv.setPrefHeight(400);
        VBox.setVgrow(lv, Priority.ALWAYS);

        Button approveBtn = new Button("Approve / Assign");
        approveBtn.getStyleClass().add("button");

        VBox detailsBox = new VBox(10);
        detailsBox.setStyle("-fx-background-color: -background-color; -fx-padding: 15; -fx-background-radius: 8;");
        Text detailText = new Text("Select a pending teacher to see details.");
        detailText.setWrappingWidth(400);
        detailsBox.getChildren().add(detailText);

        lv.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV == null) {
                detailText.setText("Select a pending teacher to see details.");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Name: ").append(newV.getFullName()).append('\n');
                sb.append("Qualification: ").append(newV.getQualification() == null ? "N/A" : newV.getQualification())
                        .append('\n');
                sb.append("Email: ").append(newV.getContactEmail() == null ? "N/A" : newV.getContactEmail())
                        .append('\n');
                sb.append("Phone: ").append(newV.getContactPhone() == null ? "N/A" : newV.getContactPhone())
                        .append('\n');
                sb.append("Preferred Subjects: ")
                        .append(newV.getSubjectSpecialty() == null ? "N/A" : newV.getSubjectSpecialty()).append('\n');
                detailText.setText(sb.toString());
            }
        });

        approveBtn.setOnAction(ae -> {
            com.teachera.model.Teacher sel = lv.getSelectionModel().getSelectedItem();
            if (sel == null)
                return;

            Dialog<ButtonType> dlg = new Dialog<>();
            dlg.setTitle("Approve Teacher");
            ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dlg.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

            GridPane gp = new GridPane();
            gp.setHgap(10);
            gp.setVgap(10);
            gp.setPadding(new Insets(20));

            TextField codeField = new TextField(sel.getTeacherCode() == null ? "" : sel.getTeacherCode());
            TextField salaryField = new TextField(
                    sel.getBaseSalary() == null ? "" : sel.getBaseSalary().toPlainString());
            TextField coursesField = new TextField(sel.getSubjectSpecialty() == null ? "" : sel.getSubjectSpecialty());
            coursesField.setPromptText("Comma-separated (e.g. MATH,PHYS)");
            TextField classesField = new TextField();
            classesField.setPromptText("Comma-separated (e.g. Sec3,Sec9)");

            gp.addRow(0, new Label("Teacher Code:"), codeField);
            gp.addRow(1, new Label("Base Salary:"), salaryField);
            gp.addRow(2, new Label("Courses:"), coursesField);
            gp.addRow(3, new Label("Classes:"), classesField);

            dlg.getDialogPane().setContent(gp);

            java.util.Optional<ButtonType> res = dlg.showAndWait();
            if (res.isPresent() && res.get() == saveType) {
                try {
                    sel.setApproved(true);
                    sel.setTeacherCode(
                            codeField.getText() == null || codeField.getText().isBlank() ? sel.getTeacherCode()
                                    : codeField.getText().trim());
                    if (salaryField.getText() != null && !salaryField.getText().isBlank()) {
                        sel.setBaseSalary(new BigDecimal(salaryField.getText().trim()));
                    }
                    if (coursesField.getText() != null && !coursesField.getText().isBlank()) {
                        sel.setSubjectSpecialty(coursesField.getText().trim());
                    }

                    teacherService.updateTeacher(sel);

                    String[] courses = coursesField.getText() == null ? new String[0]
                            : coursesField.getText().split(",");
                    String[] classes = classesField.getText() == null ? new String[0]
                            : classesField.getText().split(",");

                    for (String ccode : courses) {
                        String cc = ccode.trim();
                        if (cc.isEmpty())
                            continue;
                        if (classes.length == 0) {
                            teacherService.assignClassToTeacher(sel.getId(), cc, "");
                        } else {
                            for (String cls : classes) {
                                String cl = cls.trim();
                                if (cl.isEmpty())
                                    continue;
                                teacherService.assignClassToTeacher(sel.getId(), cc, cl);
                            }
                        }
                    }
                    lv.getItems().remove(sel);
                } catch (Exception ex) {
                    Alert a = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
                    a.showAndWait();
                }
            }
        });

        try {
            java.util.List<com.teachera.model.Teacher> pending = teacherService.getAllTeachers().stream()
                    .filter(t -> !t.isApproved()).toList();
            lv.getItems().setAll(pending);
        } catch (Exception e) {
            e.printStackTrace();
        }

        panel.getChildren().addAll(headerBox, lv, detailsBox, approveBtn);
        setContent(panel);
    }
}
