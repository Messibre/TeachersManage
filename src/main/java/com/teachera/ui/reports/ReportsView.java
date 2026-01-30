package com.teachera.ui.reports;

import com.teachera.model.Attendance;
import com.teachera.model.Payroll;
import com.teachera.model.Teacher;
import com.teachera.service.AttendanceService;
import com.teachera.service.PayrollService;
import com.teachera.service.TeacherService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportsView {

        private final TeacherService teacherService;
        private final AttendanceService attendanceService;
        private final PayrollService payrollService;

        public ReportsView(TeacherService teacherService,
                        AttendanceService attendanceService,
                        PayrollService payrollService) {
                this.teacherService = teacherService;
                this.attendanceService = attendanceService;
                this.payrollService = payrollService;
        }

        public Node createContent() {
                TabPane tabs = new TabPane();

                Tab teachersTab = new Tab("Teacher List");
                teachersTab.setClosable(false);

                ObservableList<String> teachersItems = FXCollections.observableArrayList();
                ListView<String> teachersLv = new ListView<>(teachersItems);
                teachersLv.setPrefHeight(400);
                VBox.setVgrow(teachersLv, Priority.ALWAYS);

                Button refreshTeachers = new Button("Refresh List");
                refreshTeachers.getStyleClass().add("button");
                refreshTeachers.setOnAction(e -> {
                        teachersItems.clear();
                        List<Teacher> all = teacherService.getAllTeachers();
                        all.forEach(t -> teachersItems.add(t.getTeacherCode() + " — " + t.getFullName()));
                });

                VBox tBox = createSection("Registered Teachers", refreshTeachers, teachersLv);
                teachersTab.setContent(tBox);

                Tab attendanceTab = new Tab("Attendance Summary");
                attendanceTab.setClosable(false);

                TableView<Map<String, Object>> attTable = new TableView<>();
                attTable.setPrefHeight(400);
                VBox.setVgrow(attTable, Priority.ALWAYS);
                attTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

                TableColumn<Map<String, Object>, String> tcol = new TableColumn<>("Teacher");
                tcol.setCellValueFactory(cd -> new SimpleStringProperty((String) cd.getValue().get("teacher")));

                TableColumn<Map<String, Object>, Integer> pcol = new TableColumn<>("Present");
                pcol.setCellValueFactory(cd -> new SimpleObjectProperty<>((Integer) cd.getValue().get("present")));

                TableColumn<Map<String, Object>, Integer> acol = new TableColumn<>("Absent");
                acol.setCellValueFactory(cd -> new SimpleObjectProperty<>((Integer) cd.getValue().get("absent")));

                TableColumn<Map<String, Object>, Integer> lcol = new TableColumn<>("Late");
                lcol.setCellValueFactory(cd -> new SimpleObjectProperty<>((Integer) cd.getValue().get("late")));

                TableColumn<Map<String, Object>, Integer> ocol = new TableColumn<>("On Leave");
                ocol.setCellValueFactory(cd -> new SimpleObjectProperty<>((Integer) cd.getValue().get("onleave")));

                attTable.getColumns().addAll(List.of(tcol, pcol, acol, lcol, ocol));

                Button refreshAttendance = new Button("Refresh Stats");
                refreshAttendance.getStyleClass().add("button");
                refreshAttendance.setOnAction(e -> {
                        attTable.getItems().clear();
                        List<Attendance> all = attendanceService.getAllAttendance();
                        Map<Integer, int[]> stats = new HashMap<>();
                        all.forEach(a -> {
                                int[] s = stats.computeIfAbsent(a.getTeacherId(), k -> new int[4]);
                                if (a.getStatus() != null) {
                                        switch (a.getStatus()) {
                                                case PRESENT:
                                                        s[0]++;
                                                        break;
                                                case ABSENT:
                                                        s[1]++;
                                                        break;
                                                case LATE:
                                                        s[2]++;
                                                        break;
                                                case ON_LEAVE:
                                                        s[3]++;
                                                        break;
                                        }
                                }
                        });

                        teacherService.getAllTeachers().forEach(t -> {
                                int[] s = stats.getOrDefault(t.getId(), new int[4]);
                                Map<String, Object> row = new HashMap<>();
                                row.put("teacher", t.getTeacherCode() + " — " + t.getFullName());
                                row.put("present", s[0]);
                                row.put("absent", s[1]);
                                row.put("late", s[2]);
                                row.put("onleave", s[3]);
                                attTable.getItems().add(row);
                        });
                });

                VBox aBox = createSection("Attendance Statistics", refreshAttendance, attTable);
                attendanceTab.setContent(aBox);

                Tab payrollTab = new Tab("Salary Summary");
                payrollTab.setClosable(false);

                TableView<Map<String, Object>> payTable = new TableView<>();
                payTable.setPrefHeight(400);
                VBox.setVgrow(payTable, Priority.ALWAYS);
                payTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

                TableColumn<Map<String, Object>, String> ptcol = new TableColumn<>("Teacher");
                ptcol.setCellValueFactory(cd -> new SimpleStringProperty((String) cd.getValue().get("teacher")));

                TableColumn<Map<String, Object>, Integer> pycol = new TableColumn<>("Year");
                pycol.setCellValueFactory(cd -> new SimpleObjectProperty<>((Integer) cd.getValue().get("year")));

                TableColumn<Map<String, Object>, Integer> pmcol = new TableColumn<>("Month");
                pmcol.setCellValueFactory(cd -> new SimpleObjectProperty<>((Integer) cd.getValue().get("month")));

                TableColumn<Map<String, Object>, String> pncol = new TableColumn<>("Net Salary");
                pncol.setCellValueFactory(cd -> new SimpleStringProperty((String) cd.getValue().get("net")));

                payTable.getColumns().addAll(List.of(ptcol, pycol, pmcol, pncol));

                Button refreshPayroll = new Button("Refresh Payroll");
                refreshPayroll.getStyleClass().add("button");
                refreshPayroll.setOnAction(e -> {
                        payTable.getItems().clear();
                        teacherService.getAllTeachers().forEach(t -> {
                                List<Payroll> pList = payrollService.getPayrollForTeacher(t.getId());
                                Map<String, Object> row = new HashMap<>();
                                row.put("teacher", t.getTeacherCode() + " — " + t.getFullName());
                                if (pList.isEmpty()) {
                                        row.put("year", 0);
                                        row.put("month", 0);
                                        row.put("net", "No records");
                                } else {
                                        Payroll latest = pList.get(0);
                                        row.put("year", latest.getPeriodYear());
                                        row.put("month", latest.getPeriodMonth());
                                        row.put("net", latest.getNetSalary() == null ? "0"
                                                        : latest.getNetSalary().toPlainString());
                                }
                                payTable.getItems().add(row);
                        });
                });

                VBox pBox = createSection("Payroll Overview", refreshPayroll, payTable);
                payrollTab.setContent(pBox);

                tabs.getTabs().addAll(teachersTab, attendanceTab, payrollTab);

                refreshTeachers.fire();
                refreshAttendance.fire();
                refreshPayroll.fire();

                return tabs;
        }

        private VBox createSection(String title, Button action, Node content) {
                VBox card = new VBox(15);
                card.getStyleClass().add("stat-card");
                card.setMaxWidth(900);
                card.setAlignment(Pos.TOP_CENTER);

                Label header = new Label(title);
                header.getStyleClass().add("section-header");

                card.getChildren().addAll(header, action, content);
                VBox.setVgrow(content, Priority.ALWAYS);

                VBox outer = new VBox(card);
                outer.setPadding(new Insets(20));
                outer.setAlignment(Pos.TOP_CENTER);
                return outer;
        }
}
