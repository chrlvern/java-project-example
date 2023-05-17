package com.example.quick_loans;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Objects;
import java.util.ResourceBundle;

public class AddLoanController extends AddUserController implements Initializable {
    ObservableList<String> durationList = FXCollections.observableArrayList("1 Month", "1.5 Months", "2 Months", "2.5 Months", "3 Months", "Other");
    ObservableList<String> feeList = FXCollections.observableArrayList("Has Been Paid", "Has Not Been Paid", "Will Not Be Charged");
    @FXML
    private Button button_save;
    @FXML
    private Button button_exit;
    @FXML
    private Button button_search;
    @FXML
    private Text text_name;
    @FXML
    private Text text_address;
    @FXML
    private Text text_number;
    @FXML
    private Text text_id;
    @FXML
    private ChoiceBox cb_loanDuration;
    @FXML
    private ChoiceBox cb_appFee;
    @FXML
    private ChoiceBox cb_insuranceFee;
    @FXML
    private ComboBox cb_paymentMethod;
    @FXML
    private ToggleGroup input;
    @FXML
    private DatePicker d_date;
    @FXML
    private TextField loan_amount;
    @FXML
    private Label label_total;
    @FXML
    private TextField tf_interestFee;
    @FXML
    private TextField tf_totalInterest;
    @FXML
    private TextField tf_processingFee;
    @FXML
    private TextField tf_totalProcessing;
    @FXML
    private Label label_payments;
    @FXML
    private Label label_newTotal;

    public static Text static_name;
    public static Text static_address;
    public static Text static_number;
    public static Text static_id;

    private String durationChoice;
    private String appFeeChoice;
    private String insuranceFeeChoice;
    private String radioChoice;
    private Double interest;
    private Double processing;
    private Double amount;
    private double toPay = 0;

    private double outstanding = 0;

    private final double arrears = 0;

    private double paymentAmt = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DBUtils.populateCbPayment(cb_paymentMethod);

        static_name = text_name;
        static_address = text_address;
        static_number = text_number;
        static_id = text_id;

        loan_amount.textProperty().addListener((observable, oldValue, newValue) -> {
            label_total.setText(loan_amount.getText());
        });

        cb_loanDuration.setItems(durationList);
        cb_loanDuration.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> {
            durationChoice = (durationList.get(t1.intValue()));
            switch (durationChoice) {
                case "1.5 Months":
                    tf_interestFee.setText("6.0");
                    tf_processingFee.setText("24.0");
                    label_payments.setText("3");
                    break;
                case "2 Months":
                    tf_interestFee.setText("8.0");
                    tf_processingFee.setText("32.0");
                    label_payments.setText("4");;
                case "2.5 Months":
                    tf_interestFee.setText("10.0");
                    tf_processingFee.setText("40.0");
                    label_payments.setText("5");
                    break;
                case "3 Months":
                    tf_interestFee.setText("12.0");
                    tf_processingFee.setText("48.0");
                    label_payments.setText("6");
                    break;
                case "1 Month":
                    tf_interestFee.setText("4.0");
                    tf_processingFee.setText("16.0");
                    label_payments.setText("2");
                    break;
            }
            interest = (Double.parseDouble(loan_amount.getText())) * (Double.parseDouble(tf_interestFee.getText()))/100;
            tf_totalInterest.setText(interest.toString());

            processing = (Double.parseDouble(loan_amount.getText())) * (Double.parseDouble(tf_processingFee.getText()))/100;
            tf_totalProcessing.setText(processing.toString());

            amount = Double.parseDouble(loan_amount.getText()) + interest + processing;
            label_total.setText(amount.toString());
        });

        cb_appFee.setValue("Will Not Be Charged");
        cb_appFee.setItems(feeList);
        cb_appFee.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> appFeeChoice = (feeList.get(t1.intValue())));

        cb_insuranceFee.setValue("Will Not Be Charged");
        cb_insuranceFee.setItems(feeList);
        cb_insuranceFee.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> insuranceFeeChoice = (feeList.get(t1.intValue())));

        tf_interestFee.setDisable(true);
        tf_processingFee.setDisable(true);
        tf_totalInterest.setDisable(true);
        tf_totalProcessing.setDisable(true);

        input.selectedToggleProperty().addListener((observableValue, toggle, t1) -> {
            RadioButton rb = (RadioButton) input.getSelectedToggle();
            if(rb != null) {
                radioChoice = rb.getText();
            }
            if (Objects.equals(radioChoice, "Use Manual Input")) {
                tf_interestFee.setDisable(false);
                tf_processingFee.setDisable(false);
                tf_totalInterest.setDisable(false);
                tf_totalProcessing.setDisable(false);
            }
            else {
                tf_interestFee.setDisable(true);
                tf_processingFee.setDisable(true);
                tf_totalInterest.setDisable(true);
                tf_totalProcessing.setDisable(true);
            }

        });

        button_search.setOnAction(actionEvent -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("selectCustomer.fxml"));
                Parent root1 = fxmlLoader.load();

                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Select Customer");
                stage.setScene(new Scene(root1));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        if (Objects.equals(level, "Payments Clerk") || Objects.equals(level, "Read Only Clerk")){
            button_save.setDisable(true);
        }

        button_save.setOnAction(actionEvent -> {
            paymentAmt = (Double.parseDouble(label_total.getText())/Integer.parseInt(label_payments.getText()));
            toPay = paymentAmt;
            outstanding = (paymentAmt*Integer.parseInt(label_payments.getText()));
            DBUtils.addLoan(Integer.parseInt(static_id.getText()), d_date.getValue(), Double.parseDouble(loan_amount.getText()), durationChoice, cb_paymentMethod.getSelectionModel().getSelectedItem().toString(), appFeeChoice, insuranceFeeChoice, Double.parseDouble(label_total.getText()), Double.parseDouble(tf_interestFee.getText()), Double.parseDouble(tf_totalInterest.getText()), Double.parseDouble(tf_processingFee.getText()), Double.parseDouble(tf_totalProcessing.getText()), Double.parseDouble(String.format("%.2f", toPay)), outstanding, arrears);
        });
        button_exit.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "homePage.fxml", "Welcome!", null));
    }
}
