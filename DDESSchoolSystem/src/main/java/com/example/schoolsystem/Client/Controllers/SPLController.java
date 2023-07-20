package com.example.schoolsystem.Client.Controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class SPLController implements Initializable {
    public Button SPLLoginButton;
    public TextField SPLUsernameBox;
    public PasswordField SPLPasswordBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        SPLLoginButton.setOnAction(event -> {
            System.out.println("You clicked me!");
            System.out.print("Username: ");
            System.out.println(SPLUsernameBox.getCharacters().toString());
            System.out.print("Password: ");
            System.out.println(SPLPasswordBox.getCharacters().toString());
        });
    }
}
