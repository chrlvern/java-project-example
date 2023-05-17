package com.example.quick_loans;

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

public class DBUtils {
    public static void changeScene(ActionEvent actionEvent, String fxmlFile, String title, String fullName)
    {
        Parent root = null;

        if (fullName != null){
            try{
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                root = loader.load();
                HomePageController homePageController = loader.getController();
                homePageController.setUserInformation(fullName);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else {
            try {
                root = FXMLLoader.load(DBUtils.class.getResource(fxmlFile));
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
        stage.show();
    }

    public static void signUpUser(ActionEvent event, String fullName, String password, String accessLevel) {
        Connection connection = null;
        PreparedStatement psInsert = null;
        PreparedStatement psCheckUserExists = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://192.168.1.116:3306/loan_database", "root", "password123");
            psCheckUserExists = connection.prepareStatement("SELECT * FROM users WHERE f_name = ?");
            psCheckUserExists.setString(1, fullName);
            resultSet = psCheckUserExists.executeQuery();

            if(resultSet.isBeforeFirst()) {
                System.out.println("User already exists!");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("You cannot use this username.");
                alert.show();
            }
            else
            {
                psInsert = connection.prepareStatement("INSERT INTO users (f_Name, password, accessLevel) VALUES (?, ?, ?)");
                psInsert.setString(1, fullName);
                psInsert.setString(2, password);
                psInsert.setString(3, accessLevel);
                psInsert.executeUpdate();


                changeScene(event, "homePage.fxml", "Welcome!", fullName);


            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psCheckUserExists != null) {
                try {
                    psCheckUserExists.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(psInsert != null) {
                try {
                    psInsert.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void loginUser(ActionEvent actionEvent, String fullName, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://192.168.1.116:3306/loan_database", "root", "password123");
            preparedStatement = connection.prepareStatement("SELECT password FROM users WHERE f_name = ?");
            preparedStatement.setString(1, fullName);
            resultSet = preparedStatement.executeQuery();

            if(!resultSet.isBeforeFirst()) {
                System.out.println("User not found in the database!");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Provided credentials are incorrect!");
                alert.show();
            }
            else {
                while(resultSet.next()) {
                    String retrievedPassword = resultSet.getString("password");

                    if(retrievedPassword.equals(password)) {
                        DBUtils.changeScene(actionEvent, "homePage.fxml", "Quick Loans", fullName);
                    }
                    else  {
                        System.out.println("Passwords did not match.");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Provided credentials are incorrect!");
                        alert.show();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void populateCbUser(ComboBox cb) {
        Connection connection = null;
        PreparedStatement psInsertCB = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://192.168.1.116:3306/loan_database", "root", "password123");
            psInsertCB = connection.prepareStatement("select * from users");
            resultSet = psInsertCB.executeQuery();

            ObservableList<String> data = FXCollections.observableArrayList();
            while (resultSet.next()) {
                data.add(resultSet.getString(2));
            }
            cb.setItems(data);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psInsertCB != null) {
                try {
                    psInsertCB.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void addCustomer(String firstName, String lastName, LocalDate dob, String address1, String address2, String town, String district, LocalDate atCurrentAddressSince, String residenceIs, String phoneNumber1, String phoneNumber2, String email, String idType, String idNumber, String gender, String nc_firstName, String nc_lastName, String nc_address1, String nc_address2, String nc_town, String nc_district, String nc_phone1, String nc_phone2, String workplace, String title, String salary, String salaryFrequency, LocalDate atCurrentWPSince, LocalDate atCurrentPSince) {
        Connection connection = null;
        PreparedStatement psInsert = null;
        PreparedStatement psCheckUserExists = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://192.168.1.116:3306/loan_database", "root", "password123");
            psCheckUserExists = connection.prepareStatement("SELECT * FROM customer WHERE first_name = ? AND last_name = ?");
            psCheckUserExists.setString(1, firstName);
            psCheckUserExists.setString(2, lastName);
            resultSet = psCheckUserExists.executeQuery();

            if (resultSet.isBeforeFirst()) {
                System.out.println("Customer already exists!");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("This Customer already exists in the database");
                alert.show();
            } else {
                psInsert = connection.prepareStatement("INSERT INTO customer (first_name, last_name, dob, address1, address2, town, district, atCurrentAddressSince, residenceIs, phone_number1, phone_number2, email, id_type, id_number, gender, secondary_first_name, secondary_last_name, secondary_address1, secondary_address2, secondary_town, secondary_district, secondary_phone_number1, secondary_phone_number2, workplace, job_title, salary, salary_frequency, atCurrentWPSince, atCurrentPSince) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                psInsert.setString(1, firstName);
                psInsert.setString(2, lastName);
                psInsert.setDate(3, Date.valueOf(dob));
                psInsert.setString(4, address1);
                psInsert.setString(5, address2);
                psInsert.setString(6, town);
                psInsert.setString(7, district);
                psInsert.setDate(8, Date.valueOf(atCurrentAddressSince));
                psInsert.setString(9, residenceIs);
                psInsert.setString(10, phoneNumber1);
                psInsert.setString(11, phoneNumber2);
                psInsert.setString(12, email);
                psInsert.setString(13, idType);
                psInsert.setString(14, idNumber);
                psInsert.setString(15, gender);
                psInsert.setString(16, nc_firstName);
                psInsert.setString(17, nc_lastName);
                psInsert.setString(18, nc_address1);
                psInsert.setString(19, nc_address2);
                psInsert.setString(20, nc_town);
                psInsert.setString(21, nc_district);
                psInsert.setString(22, nc_phone1);
                psInsert.setString(23, nc_phone2);
                psInsert.setString(24, workplace);
                psInsert.setString(25, title);
                psInsert.setString(26, salary);
                psInsert.setString(27, salaryFrequency);
                psInsert.setDate(28, Date.valueOf(atCurrentWPSince));
                psInsert.setDate(29, Date.valueOf(atCurrentPSince));
                psInsert.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psCheckUserExists != null) {
                try {
                    psCheckUserExists.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psInsert != null) {
                try {
                    psInsert.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void populateCbWorkplace(ComboBox cb) {
        Connection connection = null;
        PreparedStatement psInsertCB = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://192.168.1.116:3306/loan_database", "root", "password123");
            psInsertCB = connection.prepareStatement("select * from workplace");
            resultSet = psInsertCB.executeQuery();

            ObservableList<String> data = FXCollections.observableArrayList();
            while (resultSet.next()) {
                data.add(resultSet.getString(1));
            }
            cb.setItems(data);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psInsertCB != null) {
                try {
                    psInsertCB.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static ObservableList<customer> getData(String name, String number) {
        Connection connection = null;
        PreparedStatement psPopulateTV = null;
        ResultSet resultSet = null;
        ObservableList<customer> list = FXCollections.observableArrayList();

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/loan_database", "root", "password123");
            psPopulateTV = connection.prepareStatement("select * from customer where first_name = ? or last_name = ? or phone_number1 = ?");
            psPopulateTV.setString(1, name);
            psPopulateTV.setString(2, name);
            psPopulateTV.setString(3, number);
            resultSet = psPopulateTV.executeQuery();

            while (resultSet.next()) {
                list.add(new customer((resultSet.getString("first_name") + " " + resultSet.getString("last_name")), resultSet.getString("address1"), resultSet.getString("phone_number1"), resultSet.getString("customerid")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psPopulateTV != null) {
                try {
                    psPopulateTV.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return list;
    }

    public static void addLoan(Integer id, LocalDate date, Double amount, String duration, String payment, String appFee, String insuranceFee, Double total, Double interestP, Double interestAmt, Double processingP, Double processingAmt, Double toPay, Double outstanding, Double arrears) {
        Connection connection = null;
        PreparedStatement psInsert = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://192.168.1.116:3306/loan_database", "root", "password123");
            psInsert = connection.prepareStatement("INSERT INTO loan (custid, disbursedDate, principle_amt, loanDuration, paymentMethod, applicationFee, insuranceFee, totalAmt, interest_percent, interest_fee, processingPercent, processingFee, toPay, outstanding, arrears) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
            psInsert.setInt(1, id);
            psInsert.setDate(2, Date.valueOf(date));
            psInsert.setDouble(3, amount);
            psInsert.setString(4, duration);
            psInsert.setString(5, payment);
            psInsert.setString(6, appFee);
            psInsert.setString(7, insuranceFee);
            psInsert.setDouble(8, total);
            psInsert.setDouble(9, interestP);
            psInsert.setDouble(10, interestAmt);
            psInsert.setDouble(11, processingP);
            psInsert.setDouble(12, processingAmt);
            psInsert.setDouble(13, toPay);
            psInsert.setDouble(14, outstanding);
            psInsert.setDouble(15, arrears);
            psInsert.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (psInsert != null) {
                try {
                    psInsert.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static ObservableList<payment> getDataPayment() {
        Connection connection = null;
        PreparedStatement psPopulateTV = null;
        ResultSet resultSet = null;
        ObservableList<payment> list = FXCollections.observableArrayList();
        try {
            connection = DriverManager.getConnection("jdbc:mysql://192.168.1.116:3306/loan_database", "root", "password123");
            psPopulateTV = connection.prepareStatement("select * from paymentmethods");
            resultSet = psPopulateTV.executeQuery();

            while (resultSet.next()) {
                list.add(new payment(resultSet.getString("description")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psPopulateTV != null) {
                try {
                    psPopulateTV.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    public static void populateCbPayment(ComboBox cb) {
        Connection connection = null;
        PreparedStatement psInsertCB = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://192.168.1.116:3306/loan_database", "root", "password123");
            psInsertCB = connection.prepareStatement("select * from paymentmethods");
            resultSet = psInsertCB.executeQuery();

            ObservableList<String> data = FXCollections.observableArrayList();
            while (resultSet.next()) {
                data.add(resultSet.getString(1));
            }
            cb.setItems(data);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psInsertCB != null) {
                try {
                    psInsertCB.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static ObservableList<loan> getDataLoan(String name, String outstanding, String choice, LocalDate date) {
        Connection connection = null;
        PreparedStatement psPopulateTV = null;
        PreparedStatement psPopulateTVOutstanding;
        ResultSet resultSet = null;
        ObservableList<loan> list = FXCollections.observableArrayList();

        try {
            connection = DriverManager.getConnection("jdbc:mysql://192.168.1.116:3306/loan_database", "root", "password123");
            if (Objects.equals(choice, "By Name")){
                if (Objects.equals(outstanding, "false")) {
                    psPopulateTV = connection.prepareStatement("SELECT customer.first_name, loan.disbursedDate, loan.principle_amt, loan.toPay, loan.outstanding, loan.arrears FROM customer INNER JOIN loan ON customer.customerid = loan.custid WHERE customer.first_name = ?");
                    psPopulateTV.setString(1, name);
                    resultSet = psPopulateTV.executeQuery();
                }
                else {
                    psPopulateTVOutstanding = connection.prepareStatement("SELECT customer.first_name, loan.disbursedDate, loan.principle_amt, loan.toPay, loan.outstanding, loan.arrears FROM customer INNER JOIN loan ON customer.customerid = loan.custid WHERE customer.first_name = ? AND loan.outstanding = 0.0");
                    psPopulateTVOutstanding.setString(1, name);
                    resultSet = psPopulateTVOutstanding.executeQuery();
                }
            }
            else if (Objects.equals(choice, "By Date")) {
                if (Objects.equals(outstanding, "false")) {
                    psPopulateTV = connection.prepareStatement("SELECT customer.first_name, loan.disbursedDate, loan.principle_amt, loan.toPay, loan.outstanding, loan.arrears FROM customer INNER JOIN loan ON customer.customerid = loan.custid WHERE loan.disbursedDate = ?");
                    psPopulateTV.setDate(1, Date.valueOf(date));
                    resultSet = psPopulateTV.executeQuery();
                }
                else {
                    psPopulateTVOutstanding = connection.prepareStatement("SELECT customer.first_name, loan.disbursedDate, loan.principle_amt, loan.toPay, loan.outstanding, loan.arrears FROM customer INNER JOIN loan ON customer.customerid = loan.custid WHERE loan.disbursedDate = ? AND loan.outstanding = 0.0");
                    psPopulateTVOutstanding.setDate(1, Date.valueOf(date));
                    resultSet = psPopulateTVOutstanding.executeQuery();
                }
            }
            else {
                if (Objects.equals(outstanding, "false")) {
                    psPopulateTV = connection.prepareStatement("SELECT customer.first_name, loan.disbursedDate, loan.principle_amt, loan.toPay, loan.outstanding, loan.arrears FROM customer INNER JOIN loan ON customer.customerid = loan.custid");
                    resultSet = psPopulateTV.executeQuery();
                }
                else {
                    psPopulateTVOutstanding = connection.prepareStatement("SELECT customer.first_name, loan.disbursedDate, loan.principle_amt, loan.toPay, loan.outstanding, loan.arrears FROM customer INNER JOIN loan ON customer.customerid = loan.custid WHERE loan.outstanding = 0.0");
                    resultSet = psPopulateTVOutstanding.executeQuery();
                }
            }

            while (resultSet.next()) {
                list.add(new loan(resultSet.getString("customer.first_name"), resultSet.getDate("disbursedDate"), resultSet.getDouble("principle_amt"), resultSet.getDouble("toPay"), resultSet.getDouble("outstanding"), resultSet.getDouble("arrears")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psPopulateTV != null) {
                try {
                    psPopulateTV.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return list;
    }

    public static void addCreditor(String firstName, String lastName, LocalDate dob, String address1, String address2, String town, String district, String number1, String number2, String idType, String idNumber, String workplace, String title, String credit) {
        Connection connection = null;
        PreparedStatement psInsert = null;
        PreparedStatement psCheckUserExists = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://192.168.1.116:3306/loan_database", "root", "password123");
            psCheckUserExists = connection.prepareStatement("SELECT * FROM customer WHERE first_name = ? AND last_name = ?");
            psCheckUserExists.setString(1, firstName);
            psCheckUserExists.setString(2, lastName);
            resultSet = psCheckUserExists.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                System.out.println("User not found in the database!");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Provided details are incorrect!");
                alert.show();
            }
            else {
                psInsert = connection.prepareStatement("INSERT INTO badcreditors (firstName, lastName, dob, address1, address2, town, district, phoneNumber1, phoneNumber2, idType, idNumber, workplace, w_title, credit_source) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                psInsert.setString(1, firstName);
                psInsert.setString(2, lastName);
                psInsert.setDate(3, Date.valueOf(dob));
                psInsert.setString(4, address1);
                psInsert.setString(5, address2);
                psInsert.setString(6, town);
                psInsert.setString(7, district);
                psInsert.setString(8, number1);
                psInsert.setString(9, number2);
                psInsert.setString(10, idType);
                psInsert.setString(11, idNumber);
                psInsert.setString(12, workplace);
                psInsert.setString(13, title);
                psInsert.setString(14, credit);
                psInsert.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psCheckUserExists != null) {
                try {
                    psCheckUserExists.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psInsert != null) {
                try {
                    psInsert.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static ObservableList<creditors> getData(String name) {
        Connection connection = null;
        PreparedStatement psPopulateTV = null;
        ResultSet resultSet = null;
        ObservableList<creditors> list = FXCollections.observableArrayList();

        try {
            connection = DriverManager.getConnection("jdbc:mysql://192.168.1.116:3306/loan_database", "root", "password123");
            psPopulateTV = connection.prepareStatement("select * from badcreditors where firstName = ? or lastName = ?");
            psPopulateTV.setString(1, name);
            resultSet = psPopulateTV.executeQuery();

            while (resultSet.next()) {
                list.add(new creditors((resultSet.getString("firstName") + " " + resultSet.getString("lastName")), resultSet.getString("address1"), resultSet.getString("phoneNumber1")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psPopulateTV != null) {
                try {
                    psPopulateTV.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return list;
    }

    public static ObservableList<users> getDataUsers() {
        Connection connection = null;
        PreparedStatement psPopulateTV = null;
        ResultSet resultSet = null;
        ObservableList<users> list = FXCollections.observableArrayList();

        try {
            connection = DriverManager.getConnection("jdbc:mysql://192.168.1.116:3306/loan_database", "root", "password123");
            psPopulateTV = connection.prepareStatement("select * from users");
            resultSet = psPopulateTV.executeQuery();

            while (resultSet.next()) {
                list.add(new users(resultSet.getString("f_name"), resultSet.getString("accessLevel"), resultSet.getString("password")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psPopulateTV != null) {
                try {
                    psPopulateTV.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return list;
    }

    public static void addWorkplace(String name, String description, String address1, String address2, String town, String district, String number1, String number2, String email) {
        Connection connection = null;
        PreparedStatement psInsert = null;
        PreparedStatement psCheckUserExists = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://192.168.1.116:3306/loan_database", "root", "password123");
            psCheckUserExists = connection.prepareStatement("SELECT * FROM workplace WHERE wName = ? AND address1 = ? AND town = ?");
            psCheckUserExists.setString(1, name);
            psCheckUserExists.setString(2, address1);
            psCheckUserExists.setString(3, town);
            resultSet = psCheckUserExists.executeQuery();

            if (resultSet.isBeforeFirst()) {
                System.out.println("Workplace already exists!");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("This Workplace already exists in the database");
                alert.show();
            } else {
                psInsert = connection.prepareStatement("INSERT INTO workplace (wName, wDescription, address1, address2, town, district, phone_number1, phone_number2, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                psInsert.setString(1, name);
                psInsert.setString(2, description);
                psInsert.setString(3, address1);
                psInsert.setString(4, address2);
                psInsert.setString(5, town);
                psInsert.setString(6, district);
                psInsert.setString(7, number1);
                psInsert.setString(8, number2);
                psInsert.setString(9, email);
                psInsert.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psCheckUserExists != null) {
                try {
                    psCheckUserExists.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psInsert != null) {
                try {
                    psInsert.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static ObservableList<workplace> getDataWorkplace(String name) {
        Connection connection = null;
        PreparedStatement psPopulateTV = null;
        ResultSet resultSet = null;
        ObservableList<workplace> list = FXCollections.observableArrayList();

        try {
            connection = DriverManager.getConnection("jdbc:mysql://192.168.1.116:3306/loan_database", "root", "password123");
            psPopulateTV = connection.prepareStatement("select * from workplace where wName = ?");
            psPopulateTV.setString(1, name);
            resultSet = psPopulateTV.executeQuery();

            while (resultSet.next()) {
                list.add(new workplace(resultSet.getString("wName"), resultSet.getString("address1"), resultSet.getString("phone_number1")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psPopulateTV != null) {
                try {
                    psPopulateTV.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return list;
    }

    public static void addPaymentMethod(String description) {
        Connection connection = null;
        PreparedStatement psInsert = null;
        PreparedStatement psCheckUserExists = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://192.168.1.116:3306/loan_database", "root", "password123");
            psCheckUserExists = connection.prepareStatement("SELECT * FROM paymentmethods WHERE description = ?");
            psCheckUserExists.setString(1, description);
            resultSet = psCheckUserExists.executeQuery();

            if (resultSet.isBeforeFirst()) {
                System.out.println("Method already exists!");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("This Payment Method already exists in the database");
                alert.show();
            }
            else {
                psInsert = connection.prepareStatement("INSERT INTO paymentmethods (description) VALUES (?)");
                psInsert.setString(1, description);
                psInsert.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psInsert != null) {
                try {
                    psInsert.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static ObservableList<customerMatch> getDataCustomerMatch(String name) {
        Connection connection = null;
        PreparedStatement psPopulateTV = null;
        ResultSet resultSet = null;
        ObservableList<customerMatch> list = FXCollections.observableArrayList();

        try {
            connection = DriverManager.getConnection("jdbc:mysql://192.168.1.116:3306/loan_database", "root", "password123");
            psPopulateTV = connection.prepareStatement("select * from customer where first_name = ? or last_name = ?");
            psPopulateTV.setString(1, name);
            psPopulateTV.setString(2, name);
            resultSet = psPopulateTV.executeQuery();

            while (resultSet.next()) {
                list.add(new customerMatch(resultSet.getString("first_name"), resultSet.getString("town")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psPopulateTV != null) {
                try {
                    psPopulateTV.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return list;
    }

    public static void removePayment(String description) {
        Connection connection = null;
        PreparedStatement psRemove = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://192.168.1.116:3306/loan_database", "root", "password123");
            psRemove = connection.prepareStatement("delete from paymentmethods where description = ?");
            psRemove.setString(1, description);
            psRemove.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addAccount(String incomeType, String description, Double balance){
        Connection connection = null;
        PreparedStatement psInsert = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://192.168.1.116:3306/loan_database", "root", "password123");
            psInsert = connection.prepareStatement("insert into accounts (accountType, accountDescription, balance) VALUES (?, ?, ?)");
            psInsert.setString(1, incomeType);
            psInsert.setString(2, description);
            psInsert.setDouble(3, balance);
            psInsert.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<accounts> getDataAccounts(String name) {
        Connection connection = null;
        PreparedStatement psPopulateTV = null;
        ResultSet resultSet = null;
        ObservableList<accounts> list = FXCollections.observableArrayList();

        try {
            connection = DriverManager.getConnection("jdbc:mysql://192.168.1.116:3306/loan_database", "root", "password123");
            psPopulateTV = connection.prepareStatement("select * from accounts where accountDescription = ?");
            psPopulateTV.setString(1, name);
            resultSet = psPopulateTV.executeQuery();

            while (resultSet.next()) {
                list.add(new accounts(resultSet.getInt("id"), resultSet.getString("accountDescription"), resultSet.getString("accountType"), resultSet.getDouble("balance")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psPopulateTV != null) {
                try {
                    psPopulateTV.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return list;
    }

    public static void addTransaction(){
        Connection connection = null;
        PreparedStatement psInsert = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://192.168.1.116:3306/loan_database", "root", "password123");
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

}
