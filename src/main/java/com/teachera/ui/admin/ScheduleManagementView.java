package com.teachera.ui.admin;

import com.teachera.model.Schedule;
import com.teachera.model.Subject;
import com.teachera.model.Teacher;
import com.teachera.service.ScheduleService;
import com.teachera.service.ServiceException;
import com.teachera.service.TeacherService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalTime;
import java.util.List;

public class ScheduleManagementView {

    private final ScheduleService scheduleService;
    private final TeacherService teacherService;

    public ScheduleManagementView(ScheduleService scheduleService, TeacherService teacherService) {
        this.scheduleService = scheduleService;
        this.teacherService = teacherService;
    }

    public Node createContent() {
        VBox root = new VBox(8);
        root.setPadding(new Insets(12));

        Label title = new Label("Schedule Management");

        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(8);

        List<Teacher> teachers = teacherService.getAllTeachers();
        ObservableList<Teacher> tlist = FXCollections.observableArrayList(teachers);
        ComboBox<Teacher> teacherBox = new ComboBox<>(tlist);
        teacherBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Teacher item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTeacherCode() + " - " + item.getFullName());
            }
        });
        teacherBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Teacher item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTeacherCode() + " - " + item.getFullName());
            }
        });

        ComboBox<String> dayBox = new ComboBox<>(
                FXCollections.observableArrayList("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"));

        ComboBox<Integer> sectionsCombo = new ComboBox<>(FXCollections.observableArrayList(
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
        sectionsCombo.setPromptText("Select section");
        ListView<Integer> selectedSections = new ListView<>();
        selectedSections.setPrefHeight(80);
        Button addSectionBtn = new Button("Add");
        Button removeSectionBtn = new Button("Remove");

        com.teachera.dao.SubjectDAO subjectDAO = new com.teachera.dao.impl.SubjectDAOImpl();
        java.util.List<Subject> subjects = subjectDAO.findAll();
        javafx.collections.ObservableList<Subject> sl = FXCollections.observableArrayList(subjects);
        ComboBox<Subject> subjectBox = new ComboBox<>(sl);
        subjectBox.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Subject item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getCode() + " - " + item.getName());
            }
        });
        subjectBox.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Subject item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getCode() + " - " + item.getName());
            }
        });

        ObservableList<String> timeSlots = FXCollections.observableArrayList(
                "2:00 - 4:20",
                "4:30 - 6:15",
                "7:30 - 9:00",
                "9:10 - 11:00");
        ComboBox<String> timeSlotBox = new ComboBox<>(timeSlots);
        timeSlotBox.setPromptText("Select time slot");

        form.addRow(0, new Label("Teacher"), teacherBox);
        form.addRow(1, new Label("Day"), dayBox);
        form.addRow(2, new Label("Sections"), sectionsCombo);
        form.addRow(3, new Label("Selected"), selectedSections);
        form.addRow(4, addSectionBtn, removeSectionBtn);
        form.addRow(5, new Label("Subject"), subjectBox);
        form.addRow(6, new Label("Time Slot"), timeSlotBox);

        Button saveBtn = new Button("Save Schedule");

        TableView<TimeRow> timetable = new TableView<>();
        timetable.setPrefHeight(360);

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

        root.getChildren().addAll(title, form, saveBtn, new Label("Weekly Timetable (Mon–Fri)"), timetable);

        saveBtn.setOnAction(e -> {
            try {
                Teacher t = teacherBox.getValue();
                if (t == null) {
                    showAlert("Select a teacher");
                    return;
                }
                String day = dayBox.getValue();
                if (day == null) {
                    showAlert("Select a day");
                    return;
                }
                java.util.List<Integer> secs = new java.util.ArrayList<>(selectedSections.getItems());
                if (secs.isEmpty()) {
                    showAlert("Select at least one section");
                    return;
                }
                Subject subjObj = subjectBox.getValue();
                if (subjObj == null) {
                    showAlert("Select a subject");
                    return;
                }
                String subj = subjObj.getCode();
                String slot = timeSlotBox.getValue();
                if (slot == null) {
                    showAlert("Select a time slot");
                    return;
                }
                LocalTime st;
                LocalTime et;
                switch (slot) {
                    case "2:00 - 4:20":
                        st = LocalTime.of(14, 0);
                        et = LocalTime.of(16, 20);
                        break;
                    case "4:30 - 6:15":
                        st = LocalTime.of(16, 30);
                        et = LocalTime.of(18, 15);
                        break;
                    case "7:30 - 9:00":
                        st = LocalTime.of(19, 30);
                        et = LocalTime.of(21, 0);
                        break;
                    case "9:10 - 11:00":
                        st = LocalTime.of(21, 10);
                        et = LocalTime.of(23, 0);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid time slot");
                }

                Schedule s = new Schedule();
                s.setTeacherId(t.getId());
                s.setDayOfWeek(day);

                s.setClassName(secs.isEmpty() ? "" : String.valueOf(secs.get(0)));
                s.setSections(secs);
                s.setSubjectCode(subj);
                s.setStartTime(st);
                s.setEndTime(et);

                scheduleService.createSchedule(s);
                showAlertInfo("Saved");
                refreshTimetable(timetable);
            } catch (ServiceException ex) {
                ex.printStackTrace();
                showAlert(ex.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Invalid input: " + ex.getMessage());
            }
        });

        addSectionBtn.setOnAction(ae -> {
            Integer v = sectionsCombo.getValue();
            if (v != null && !selectedSections.getItems().contains(v))
                selectedSections.getItems().add(v);
        });
        removeSectionBtn.setOnAction(ae -> {
            Integer sel = selectedSections.getSelectionModel().getSelectedItem();
            if (sel != null)
                selectedSections.getItems().remove(sel);
        });

        refreshTimetable(timetable);

        return root;
    }

    private void refreshTimetable(TableView<TimeRow> timetable) {
        java.util.List<Schedule> schedules;
        try {
            schedules = scheduleService.getAllSchedules();
            if (schedules == null)
                schedules = java.util.Collections.emptyList();
        } catch (Exception ex) {

            showAlert("Failed to load schedules: " + ex.getMessage());
            schedules = java.util.Collections.emptyList();
        }

        java.time.LocalTime[] startTimes = new java.time.LocalTime[] {
                java.time.LocalTime.of(14, 0), // 2:00
                java.time.LocalTime.of(16, 30), // 4:30
                java.time.LocalTime.of(19, 30), // 7:30
                java.time.LocalTime.of(21, 10) // 9:10
        };
        java.time.LocalTime[] endTimes = new java.time.LocalTime[] {
                java.time.LocalTime.of(16, 20),
                java.time.LocalTime.of(18, 15),
                java.time.LocalTime.of(21, 0),
                java.time.LocalTime.of(23, 0)
        };
        String[] labels = new String[] { "2:00 - 4:20", "4:30 - 6:15", "7:30 - 9:00", "9:10 - 11:00" };

        java.util.List<TimeRow> rows = new java.util.ArrayList<>();
        for (int i = 0; i < labels.length; i++)
            rows.add(new TimeRow(labels[i]));

        java.util.Map<Integer, Teacher> teacherMap = new java.util.HashMap<>();
        for (Teacher t : teacherService.getAllTeachers())
            teacherMap.put(t.getId(), t);

        for (Schedule s : schedules) {
            if (s.getDayOfWeek() == null)
                continue;
            int dayIndex = switch (s.getDayOfWeek()) {
                case "MONDAY" -> 0;
                case "TUESDAY" -> 1;
                case "WEDNESDAY" -> 2;
                case "THURSDAY" -> 3;
                case "FRIDAY" -> 4;
                default -> -1;
            };

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

    private java.time.LocalTime parseTime(String txt) {
        if (txt == null)
            throw new IllegalArgumentException("Time is required");
        txt = txt.trim();
        java.time.format.DateTimeFormatter[] fmts = new java.time.format.DateTimeFormatter[] {
                java.time.format.DateTimeFormatter.ofPattern("H:mm"),
                java.time.format.DateTimeFormatter.ofPattern("HH:mm"),
                java.time.format.DateTimeFormatter.ofPattern("H.m"),
                java.time.format.DateTimeFormatter.ISO_LOCAL_TIME
        };
        for (java.time.format.DateTimeFormatter f : fmts) {
            try {
                return java.time.LocalTime.parse(txt, f);
            } catch (Exception ignored) {
            }
        }
        throw new IllegalArgumentException("Unrecognized time format: " + txt);
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.showAndWait();
    }

    private void showAlertInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.showAndWait();
    }
}
