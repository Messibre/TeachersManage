package com.teachera.ui;

import com.teachera.dao.AttendanceDAO;
import com.teachera.dao.LeaveDAO;
import com.teachera.dao.PayrollDAO;
import com.teachera.dao.TeacherDAO;
import com.teachera.dao.UserDAO;
import com.teachera.dao.impl.AttendanceDAOImpl;
import com.teachera.dao.impl.LeaveDAOImpl;
import com.teachera.dao.impl.PayrollDAOImpl;
import com.teachera.dao.impl.TeacherDAOImpl;
import com.teachera.dao.impl.UserDAOImpl;
import com.teachera.service.AttendanceService;
import com.teachera.service.AuthService;
import com.teachera.service.LeaveService;
import com.teachera.service.PayrollService;
import com.teachera.service.TeacherService;
import com.teachera.service.impl.AttendanceServiceImpl;
import com.teachera.service.impl.AuthServiceImpl;
import com.teachera.service.impl.LeaveServiceImpl;
import com.teachera.service.impl.PayrollServiceImpl;
import com.teachera.service.impl.TeacherServiceImpl;
import com.teachera.ui.admin.AdminDashboardView;
import com.teachera.ui.teacher.TeacherManagementView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TeacheraApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Teachera - Teacher Management System");

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-pane");

        VBox headerBox = new VBox(6);
        headerBox.setPadding(new Insets(24));
        headerBox.getStyleClass().add("center-box");

        Label title = new Label("Teachera");
        title.getStyleClass().add("app-title");

        Label subtitle = new Label("Teacher Management System");
        subtitle.getStyleClass().add("app-subtitle");

        headerBox.getChildren().addAll(title, subtitle);
        root.setTop(headerBox);

        UserDAO userDAO = new UserDAOImpl();
        TeacherDAO teacherDAO = new TeacherDAOImpl();
        AttendanceDAO attendanceDAO = new AttendanceDAOImpl();
        LeaveDAO leaveDAO = new LeaveDAOImpl();
        PayrollDAO payrollDAO = new PayrollDAOImpl();

        AuthService authService = new AuthServiceImpl(userDAO);
        com.teachera.dao.TeacherAssignmentDAO assignmentDAO = new com.teachera.dao.impl.TeacherAssignmentDAOImpl();
        TeacherService teacherService = new TeacherServiceImpl(teacherDAO, assignmentDAO);
        AttendanceService attendanceService = new AttendanceServiceImpl(attendanceDAO, teacherDAO);
        LeaveService leaveService = new LeaveServiceImpl(leaveDAO, teacherDAO);
        PayrollService payrollService = new PayrollServiceImpl(payrollDAO, teacherDAO, attendanceDAO);
        com.teachera.dao.ScheduleDAO scheduleDAO = new com.teachera.dao.impl.ScheduleDAOImpl();
        com.teachera.service.ScheduleService scheduleService = new com.teachera.service.impl.ScheduleServiceImpl(
                scheduleDAO, teacherDAO);

        final LoginView[] loginViewRef = new LoginView[1];
        loginViewRef[0] = new LoginView(authService, user -> {

            Runnable logoutAction = () -> {
                authService.logout();
                VBox loginCenter = new VBox(10);
                loginCenter.setPadding(new Insets(24));
                loginCenter.getStyleClass().add("center-box");
                loginCenter.getChildren().add(loginViewRef[0].createContent());
                root.setCenter(loginCenter);
            };

            if (user.getRole().name().equals("ADMIN")) {
                AdminDashboardView adminView = new com.teachera.ui.admin.AdminDashboardView(teacherService,
                        attendanceService, leaveService, payrollService, scheduleService, logoutAction);
                root.setCenter(adminView.createContent());
            } else {

                com.teachera.ui.teacher.TeacherManagementView teacherView = new com.teachera.ui.teacher.TeacherManagementView(
                        teacherService, attendanceService, leaveService, payrollService, scheduleService, user,
                        logoutAction);
                root.setCenter(teacherView.createContent());
            }
        });

        VBox centerBox = new VBox(10);
        centerBox.setPadding(new Insets(24));
        centerBox.getStyleClass().add("center-box");
        centerBox.getChildren().add(loginViewRef[0].createContent());
        root.setCenter(centerBox);

        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
