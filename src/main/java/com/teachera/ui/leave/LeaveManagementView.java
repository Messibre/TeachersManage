package com.teachera.ui.leave;

import com.teachera.model.Leave;
import com.teachera.service.LeaveService;
import com.teachera.service.ServiceException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;

public class LeaveManagementView {

    private final LeaveService leaveService;
    private final ObservableList<Leave> list = FXCollections.observableArrayList();

    public LeaveManagementView(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    public Node createContent() {
        VBox root = new VBox(8);
        root.setPadding(new Insets(12));

        Text title = new Text("Leave Management");
        ListView<Leave> lv = new ListView<>(list);
        lv.setCellFactory(l -> new ListCell<>() {
            @Override
            protected void updateItem(Leave item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null)
                    setText(null);
                else
                    setText(item.getId() + " — teacher=" + item.getTeacherId() + " — " + item.getStatus() + " ("
                            + item.getStartDate() + "→" + item.getEndDate() + ")");
            }
        });

        Button approve = new Button("Approve");
        Button reject = new Button("Reject");
        Button refresh = new Button("Refresh");

        HBox actions = new HBox(8, approve, reject, refresh);

        approve.setOnAction(e -> {
            Leave sel = lv.getSelectionModel().getSelectedItem();
            if (sel == null)
                return;
            try {
                leaveService.approveLeave(sel.getId(), /* reviewerUserId */ 1);
                refreshList();
            } catch (ServiceException ex) {
                showAlert(ex.getMessage());
            }
        });

        reject.setOnAction(e -> {
            Leave sel = lv.getSelectionModel().getSelectedItem();
            if (sel == null)
                return;
            try {
                leaveService.rejectLeave(sel.getId(), /* reviewerUserId */ 1, "Rejected by admin");
                refreshList();
            } catch (ServiceException ex) {
                showAlert(ex.getMessage());
            }
        });

        refresh.setOnAction(e -> refreshList());

        root.getChildren().addAll(title, lv, actions);
        VBox.setVgrow(lv, Priority.ALWAYS);
        refreshList();
        return root;
    }

    private void refreshList() {
        List<Leave> all = leaveService.getAllLeaves();
        list.setAll(all);
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.showAndWait();
    }
}
