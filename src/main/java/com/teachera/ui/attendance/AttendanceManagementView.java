package com.teachera.ui.attendance;

import com.teachera.model.Attendance;
import com.teachera.model.Teacher;
import com.teachera.service.AttendanceService;
import com.teachera.service.TeacherService;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;

public class AttendanceManagementView {

    private final AttendanceService attendanceService;
    private final TeacherService teacherService;

    public AttendanceManagementView(AttendanceService attendanceService, TeacherService teacherService) {
        this.attendanceService = attendanceService;
        this.teacherService = teacherService;
    }

    public Node createContent() {
        VBox root = new VBox(8);
        root.setPadding(new Insets(12));

        Text title = new Text("Attendance Management");

        TableView<Attendance> table = new TableView<>();
        TableColumn<Attendance, Integer> colTeacherId = new TableColumn<>("Teacher ID");
        colTeacherId.setCellValueFactory(
                cd -> new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().getTeacherId()));

        TableColumn<Attendance, String> colTeacher = new TableColumn<>("Teacher");
        colTeacher.setCellValueFactory(cell -> {
            Integer tid = cell.getValue().getTeacherId();
            Teacher t = teacherService.getTeacherById(tid).orElse(null);
            String display = t == null ? String.valueOf(tid) : (t.getTeacherCode() + " — " + t.getFullName());
            return new javafx.beans.property.SimpleStringProperty(display);
        });

        TableColumn<Attendance, java.time.LocalDate> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(
                cd -> new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().getAttendanceDate()));

        TableColumn<Attendance, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getStatus().name()));
        TableColumn<Attendance, String> colHours = new TableColumn<>("Hours");
        colHours.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getHoursWorked() == null ? "" : cell.getValue().getHoursWorked().toString()));
        TableColumn<Attendance, String> colRemarks = new TableColumn<>("Remarks");
        colRemarks.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getRemarks() == null ? "" : cell.getValue().getRemarks()));

        table.getColumns().addAll(colTeacherId, colTeacher, colDate, colStatus, colHours, colRemarks);
        table.setPrefHeight(500);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button refresh = new Button("Refresh");
        refresh.setOnAction(e -> refreshList(table));

        root.getChildren().addAll(title, table, refresh);
        VBox.setVgrow(table, Priority.ALWAYS);
        refreshList(table);
        return root;
    }

    private void refreshList(TableView<Attendance> table) {
        List<Attendance> all = attendanceService.getAllAttendance();
        table.getItems().setAll(all);
    }
}
