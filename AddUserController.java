package com.example.quick_loans;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AddUserController implements Initializable {
    ObservableList<String> accessLevelList = FXCollections.observableArrayList("Director", "Loans Clerk", "Payments Clerk", "Read Only Clerk");
    @FXML
    private Button button_addUser;
    @FXML
    private Button button_cancel;
    @FXML
    private TextField tf_fullName;
    @FXML
    private TextField tf_password;
    @FXML
    private ComboBox<String> cb_accessLevel;
    String level = "Director";
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cb_accessLevel.setValue("Director");
        cb_accessLevel.setItems(accessLevelList);
        cb_accessLevel.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> level = (accessLevelList.get(t1.intValue())));
        button_addUser.setOnAction(actionEvent -> {
            if(!tf_fullName.getText().trim().isEmpty() && !tf_password.getText().trim().isEmpty()) {
                DBUtils.signUpUser(actionEvent, tf_fullName.getText(), tf_password.getText(), level);
            }
            else {
                System.out.println("PLease fill out all information");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Please fill in all information to sign up!");
                alert.show();
            }
        });
        button_cancel.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "login.fxml", "Login", null));
    }
}
