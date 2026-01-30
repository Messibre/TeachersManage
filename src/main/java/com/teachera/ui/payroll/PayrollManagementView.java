package com.teachera.ui.payroll;

import com.teachera.model.Payroll;
import com.teachera.service.PayrollService;
import com.teachera.service.ServiceException;
import com.teachera.service.TeacherService;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class PayrollManagementView {

    private final PayrollService payrollService;
    private final TeacherService teacherService;

    public PayrollManagementView(PayrollService payrollService, TeacherService teacherService) {
        this.payrollService = payrollService;
        this.teacherService = teacherService;
    }

    public Node createContent() {
        VBox root = new VBox(8);
        root.setPadding(new Insets(12));

        Text title = new Text("Payroll Management");

        javafx.scene.control.ComboBox<com.teachera.model.Teacher> teacherBox = new javafx.scene.control.ComboBox<>();
        try {
            teacherBox.getItems().addAll(teacherService.getAllTeachers());
        } catch (Exception ex) {
            // ignore populate errors; will validate on generate
        }
        teacherBox.setPromptText("Select Teacher");
        TextField yearField = new TextField();
        yearField.setPromptText("Year (e.g., 2026)");
        TextField monthField = new TextField();
        monthField.setPromptText("Month (1-12)");

        Button generate = new Button("Generate Payroll");

        generate.setOnAction(e -> {
            try {
                com.teachera.model.Teacher selTeacher = teacherBox.getValue();
                if (selTeacher == null) {
                    showAlert("Select a teacher.");
                    return;
                }
                int teacherId = selTeacher.getId();
                int year = Integer.parseInt(yearField.getText().trim());
                int month = Integer.parseInt(monthField.getText().trim());

                teacherService.getTeacherById(teacherId)
                        .orElseThrow(() -> new ServiceException("Teacher not found with id " + teacherId));

                Payroll p = payrollService.generatePayrollForTeacher(teacherId, year, month);
                Alert a = new Alert(Alert.AlertType.INFORMATION, "Payroll generated: Net=" + p.getNetSalary(),
                        ButtonType.OK);
                a.showAndWait();
            } catch (NumberFormatException ex) {
                showAlert("Invalid numeric input.");
            } catch (ServiceException ex) {
                showAlert(ex.getMessage());
            }
        });

        root.getChildren().addAll(title, teacherBox, yearField, monthField, generate);
        return root;
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.showAndWait();
    }
}
