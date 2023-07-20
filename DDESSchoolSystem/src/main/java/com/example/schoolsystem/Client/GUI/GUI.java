package com.example.schoolsystem.Client.GUI;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GUI extends Application {
    //the GUI holds the code to
    //create and draw the objects in the window
    //that the user sees on screen.
    //some logic is abstratcted here
    //such as checking for blank inputs
    //as there is no point in sending blank inputs to the server.

    private Stage window;

    private GUIClient client;

    private int width = 720;

    private int height = 405;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        window = stage;
        window.setResizable(false);
        this.client = new GUIClient();
        window.setOnCloseRequest(e -> {
                    //process close command differently to other commands
                    e.consume();
                    try {
                        this.onClose();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
        );
        //show window
        window.setScene(new Scene(this.login(), width, height));
        window.show();
    }

    private void onClose() throws IOException {
        //close window and exit
        window.close();
        System.exit(0);
    }

    private VBox login() {
        //creates objects for this window
        window.setTitle("Login");
        Label login = new Label("Login");
        Label username = new Label("Username");
        Label password = new Label("Password");
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        Button submit = new Button("Submit");

        final Text errorMessage = new Text();

        //hboxes define the horizontal positioning
        //of elements
        //for login elements:
        HBox alignLoginLabel = new HBox();
        alignLoginLabel.setAlignment(Pos.CENTER);
        login.setPadding(new Insets(20));
        login.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        alignLoginLabel.getChildren().add(login);

        //for username elements:
        HBox alignUsernameLabel = new HBox();
        alignUsernameLabel.setAlignment(Pos.CENTER);
        username.setPadding(new Insets(15));
        username.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        alignUsernameLabel.getChildren().addAll(username, usernameField);
        alignUsernameLabel.setSpacing(10);

        //for password elements
        HBox alignPasswordLabel = new HBox();
        alignPasswordLabel.setAlignment(Pos.CENTER);
        password.setPadding(new Insets(15));
        password.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        alignPasswordLabel.getChildren().addAll(password, passwordField);
        alignPasswordLabel.setSpacing(10);

        //for submitting elements:
        HBox alignSubmit = new HBox();
        submit.setPadding(new Insets(10));
        alignSubmit.setAlignment(Pos.CENTER);
        alignSubmit.getChildren().add(submit);

        //for the error message:
        HBox alignError = new HBox();
        alignError.setAlignment(Pos.CENTER);
        alignError.getChildren().add(errorMessage);

        //the vbox positions objects vertically
        VBox vb = new VBox();
        //add all the horizontal elements to the vbox
        vb.getChildren().addAll(alignLoginLabel, alignUsernameLabel, alignPasswordLabel, alignError, alignSubmit);
        //when the user clicks submit:
        submit.setOnAction(e -> {
            //get the inputs
            String inputtedUsername = usernameField.getText().strip();
            String inputtedPassword = passwordField.getText().strip();
            //check they aren't blank
            if (usernameField.getText().isEmpty() || inputtedUsername.equalsIgnoreCase("") || passwordField.getText().isEmpty() || inputtedPassword.equalsIgnoreCase("")) {
                errorMessage.setText("Fields can't be blank!");
            } else if (inputtedUsername.split(" ").length > 1) {
                //check the username doesn't have spaces
                errorMessage.setText("Username can't have spaces");
            } else {
                //loggedin refers to whether logging in was successful or not
                Boolean loggedIn = false;
                JSONObject object = new JSONObject();
                //prepare the object to send to the server
                object.put("command", "login");
                object.put("username", inputtedUsername);
                object.put("password", inputtedPassword);
                //put in try loop in case anything goes wrong...
                try {
                    //loggedin is the value that is returned by the login method
                    loggedIn = client.login(object);
                } catch (Exception ex) {
                    //if it fails, show an error message and redraw the window:
                    errorMessage.setText("Login failed!");
                    window.setScene(new Scene(this.parentDashboard(), width, height));
                    //throw new RuntimeException(ex);
                }
                //if logging in was successful:
                if (loggedIn) {
                    if (this.client.role.equalsIgnoreCase("parent")) {
                        window.setScene(new Scene(this.parentDashboard(), width, height));
                        //show parent dashboard if user is a parent
                    } else {
                        window.setScene(new Scene(this.teacherDashboard(), width, height));
                        //show teacher dashboard if user is a teacher
                    }
                } else {
                    //if unsuccessful, show fail message
                    errorMessage.setText("Login failed!");
                }
            }

        });
        //return the vb so it can be shown to the user
        return vb;
    }

    private VBox parentDashboard() {
        //set up objects...
        window.setTitle("Parent Dashboard");
        Label titleLabel = new Label("Parent Dashboard");
        Button manageClassesButton = new Button("Manage Classes");
        Button viewNotificationsButton = new Button("View Notifications");
        Button sendPMButton = new Button("Send Private Message");
        Button viewPMButton = new Button("View Private Messages");
        Button logOutButton = new Button("Log Out");

        //align things horizontally...
        HBox alignTitleLabel = new HBox();
        alignTitleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPadding(new Insets(20));
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        alignTitleLabel.getChildren().add(titleLabel);
        alignTitleLabel.setSpacing(20);

        HBox alignTopButtons = new HBox();
        alignTopButtons.setAlignment(Pos.CENTER);
        manageClassesButton.setPadding(new Insets(15));
        manageClassesButton.setPrefWidth(200);
        manageClassesButton.setPrefHeight(50);
        manageClassesButton.setFont(Font.font("Arial", 15));
        viewNotificationsButton.setPadding(new Insets(15));
        viewNotificationsButton.setFont(Font.font("Arial", 15));
        viewNotificationsButton.setPrefWidth(200);
        viewNotificationsButton.setPrefHeight(50);
        alignTopButtons.getChildren().addAll(manageClassesButton, viewNotificationsButton);
        alignTopButtons.setSpacing(20);

        HBox alignBottomButtons = new HBox();
        alignBottomButtons.setAlignment(Pos.CENTER);
        sendPMButton.setPadding(new Insets(15));
        sendPMButton.setFont(Font.font("Arial", 15));
        sendPMButton.setPrefWidth(200);
        sendPMButton.setPrefHeight(50);
        viewPMButton.setPadding(new Insets(15));
        viewPMButton.setFont(Font.font("Arial", 15));
        viewPMButton.setPrefWidth(200);
        viewPMButton.setPrefHeight(50);
        alignBottomButtons.getChildren().addAll(sendPMButton, viewPMButton);
        alignBottomButtons.setSpacing(20);

        HBox alignLogOutButton = new HBox();
        alignLogOutButton.setAlignment(Pos.CENTER);
        logOutButton.setPadding(new Insets(15));
        alignLogOutButton.getChildren().addAll(logOutButton);
        alignLogOutButton.setSpacing(20);

        VBox vb = new VBox();
        //add items to vertical box
        vb.getChildren().addAll(alignTitleLabel, alignTopButtons, alignBottomButtons, alignLogOutButton);
        //set action of log out button
        //send back to login screen on press:
        logOutButton.setOnAction(e -> {
            window.setScene(new Scene(this.login(), width, height));
        });
        //set action of sendpm button:
        //send to parent's sendpm screen on press:
        sendPMButton.setOnAction(e -> {
            window.setScene(new Scene(this.parentSendPrivateMessage(), width, height));
        });
        //set action of viewpm button:
        //send to parent's view pm screen on press:
        viewPMButton.setOnAction((e -> {
            window.setScene(new Scene(this.parentViewPM(), width, height));
        }));
        //set action of manage classes button:
        //send to parent's manage classes screen on press:
        manageClassesButton.setOnAction((e -> {
            window.setScene(new Scene(this.parentManageClasses(), width, height));
        }));
        //return the vbox to be drawn on screen:
        return vb;
    }

    private VBox parentManageClasses() {
        //set up objects....
        window.setTitle("Parent Manage Classes");
        Label titleLabel = new Label("Parent Manage Classes");
        Button joinClassesButton = new Button("Join Classes");
        Button viewClassesButton = new Button("View Classes");
        Button backButton = new Button("Back");

        //align elements horizontally
        HBox alignTitleLabel = new HBox();
        alignTitleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPadding(new Insets(20));
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        alignTitleLabel.getChildren().add(titleLabel);
        alignTitleLabel.setSpacing(20);

        HBox alignTopButtons = new HBox();
        alignTopButtons.setAlignment(Pos.CENTER);
        joinClassesButton.setPadding(new Insets(15));
        joinClassesButton.setPrefWidth(200);
        joinClassesButton.setPrefHeight(50);
        joinClassesButton.setFont(Font.font("Arial", 15));
        viewClassesButton.setPadding(new Insets(15));
        viewClassesButton.setFont(Font.font("Arial", 15));
        viewClassesButton.setPrefWidth(200);
        viewClassesButton.setPrefHeight(50);
        alignTopButtons.getChildren().addAll(joinClassesButton, viewClassesButton);
        alignTopButtons.setSpacing(20);

        HBox alignBackButton = new HBox();
        alignBackButton.setAlignment(Pos.CENTER);
        backButton.setPadding(new Insets(15));
        alignBackButton.getChildren().addAll(backButton);
        alignBackButton.setSpacing(20);

        //set back button action:
        //send back to parent dashboard on click
        backButton.setOnAction(e -> {
            window.setScene(new Scene(this.parentDashboard(), width, height));
        });
        //set join classes action:
        //send to join class screen on click
        joinClassesButton.setOnAction(e -> {
            window.setScene(new Scene(this.parentJoinClass(), width, height));
        });
        //set view classes action:
        //send to view class dashboard on click
        viewClassesButton.setOnAction(e -> {
            window.setScene(new Scene(this.parentViewClassesDash(), width, height));
        });

        VBox vb = new VBox();
        //place horizontal elements into vbox to be drawn
        vb.getChildren().addAll(alignTitleLabel, alignTopButtons, alignBackButton);
        //return the final screen to be drawn
        return vb;
    }

    private VBox teacherDashboard() {
        //set up objects to be drawn...
        window.setTitle("Teacher Dashboard");
        Label titleLabel = new Label("Teacher Dashboard");
        Button manageClassesButton = new Button("Manage Classes");
        Button sendNotificationsButton = new Button("Send Notifications");
        Button sendPMButton = new Button("Send Private Message");
        Button viewPMButton = new Button("View Private Messages");
        Button logOutButton = new Button("Log Out");

        //align elements horizontally
        HBox alignTitleLabel = new HBox();
        alignTitleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPadding(new Insets(20));
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        alignTitleLabel.getChildren().add(titleLabel);
        alignTitleLabel.setSpacing(20);

        HBox alignTopButtons = new HBox();
        alignTopButtons.setAlignment(Pos.CENTER);
        manageClassesButton.setPadding(new Insets(15));
        manageClassesButton.setPrefWidth(200);
        manageClassesButton.setPrefHeight(50);
        manageClassesButton.setFont(Font.font("Arial", 15));
        sendNotificationsButton.setPadding(new Insets(15));
        sendNotificationsButton.setFont(Font.font("Arial", 15));
        sendNotificationsButton.setPrefWidth(200);
        sendNotificationsButton.setPrefHeight(50);
        alignTopButtons.getChildren().addAll(manageClassesButton, sendNotificationsButton);
        alignTopButtons.setSpacing(20);

        HBox alignBottomButtons = new HBox();
        alignBottomButtons.setAlignment(Pos.CENTER);
        sendPMButton.setPadding(new Insets(15));
        sendPMButton.setFont(Font.font("Arial", 15));
        sendPMButton.setPrefWidth(200);
        sendPMButton.setPrefHeight(50);
        viewPMButton.setPadding(new Insets(15));
        viewPMButton.setFont(Font.font("Arial", 15));
        viewPMButton.setPrefWidth(200);
        viewPMButton.setPrefHeight(50);
        alignBottomButtons.getChildren().addAll(sendPMButton, viewPMButton);
        alignBottomButtons.setSpacing(20);

        HBox alignLogOutButton = new HBox();
        alignLogOutButton.setAlignment(Pos.CENTER);
        logOutButton.setPadding(new Insets(15));
        alignLogOutButton.getChildren().addAll(logOutButton);
        alignLogOutButton.setSpacing(20);

        VBox vb = new VBox();
        //add horizontal elements to vertical elements:
        vb.getChildren().addAll(alignTitleLabel, alignTopButtons, alignBottomButtons, alignLogOutButton);
        //set logout button action:
        logOutButton.setOnAction(e -> {
            window.setScene(new Scene(this.login(), width, height));
        });
        //redirect to manage classes dashboard on click:
        manageClassesButton.setOnAction(e -> {
            window.setScene(new Scene(this.teacherManageClasses(), width, height));
        });
        //redirect to send private message window on click:
        sendPMButton.setOnAction(e -> {
            window.setScene(new Scene(this.teacherSendPrivateMessage(), width, height));
        });
        //redirect to viewing pm on click:
        viewPMButton.setOnAction(e -> {
            window.setScene(new Scene(this.teacherViewPM(), width, height));
        });
        //return window to draw and show
        return vb;
    }

    private VBox teacherManageClasses() {
        //define objects to show
        window.setTitle("Manage Classes Dashboard");
        Label titleLabel = new Label("Manage Classes");
        Button createClassesButton = new Button("Create Class");
        Button readClassesButton = new Button("Read Classes");
        Button updateClassesButton = new Button("Update Classes");
        Button deleteClassesButton = new Button("Delete Class");
        Button backButton = new Button("Back");

        //align objects horizontally
        HBox alignTitleLabel = new HBox();
        alignTitleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPadding(new Insets(20));
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        alignTitleLabel.getChildren().add(titleLabel);
        alignTitleLabel.setSpacing(20);

        HBox alignTopButtons = new HBox();
        alignTopButtons.setAlignment(Pos.CENTER);
        createClassesButton.setPadding(new Insets(15));
        createClassesButton.setPrefWidth(200);
        createClassesButton.setPrefHeight(50);
        createClassesButton.setFont(Font.font("Arial", 15));
        readClassesButton.setPadding(new Insets(15));
        readClassesButton.setFont(Font.font("Arial", 15));
        readClassesButton.setPrefWidth(200);
        readClassesButton.setPrefHeight(50);
        alignTopButtons.getChildren().addAll(createClassesButton, readClassesButton);
        alignTopButtons.setSpacing(20);

        HBox alignBottomButtons = new HBox();
        alignBottomButtons.setAlignment(Pos.CENTER);
        updateClassesButton.setPadding(new Insets(15));
        updateClassesButton.setFont(Font.font("Arial", 15));
        updateClassesButton.setPrefWidth(200);
        updateClassesButton.setPrefHeight(50);
        deleteClassesButton.setPadding(new Insets(15));
        deleteClassesButton.setFont(Font.font("Arial", 15));
        deleteClassesButton.setPrefWidth(200);
        deleteClassesButton.setPrefHeight(50);
        alignBottomButtons.getChildren().addAll(updateClassesButton, deleteClassesButton);
        alignBottomButtons.setSpacing(20);

        HBox alignBackButton = new HBox();
        alignBackButton.setAlignment(Pos.CENTER);
        backButton.setPadding(new Insets(15));
        alignBackButton.getChildren().addAll(backButton);
        alignBackButton.setSpacing(20);

        //assign vertical positions to items
        VBox vb = new VBox();
        vb.getChildren().addAll(alignTitleLabel, alignTopButtons, alignBottomButtons, alignBackButton);

        //send to dashboard on click:
        backButton.setOnAction((e -> {
            window.setScene(new Scene(this.teacherDashboard(), width, height));
        }));
        //send to view classes menu on click:
        readClassesButton.setOnAction(e -> {
            window.setScene(new Scene(this.teacherViewClasses(), width, height));
        });
        //send to create class menu on click
        createClassesButton.setOnAction(e -> {
            window.setScene(new Scene(this.teacherCreateClass(), width, height));
        });
        //send to update class menu of click
        updateClassesButton.setOnAction(e -> {
            window.setScene(new Scene(this.teacherUpdateClass(), width, height));
        });
        //send to delete class menu on click
        deleteClassesButton.setOnAction(e -> {
            window.setScene(new Scene(this.teacherDeleteClass(), width, height));
        });

        //return object to draw on screen
        return vb;
    }

    private VBox teacherUpdateClass() {
        //define objects to draw
        window.setTitle("Teacher Update Class");
        Label title = new Label("Teacher Update Class");
        Label name = new Label("Student");
        Label code = new Label("Code");
        TextField nameField = new TextField();
        TextField codeField = new TextField();
        Button submit = new Button("Submit");
        Button back = new Button("Back");

        final Text errorMessage = new Text();

        //align items horizontally
        HBox alignTitleLabel = new HBox();
        alignTitleLabel.setAlignment(Pos.CENTER);
        title.setPadding(new Insets(20));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        alignTitleLabel.getChildren().add(title);

        HBox alignNameLabel = new HBox();
        alignNameLabel.setAlignment(Pos.CENTER);
        name.setPadding(new Insets(15));
        name.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        alignNameLabel.getChildren().addAll(name, nameField);
        alignNameLabel.setSpacing(10);

        HBox alignCodeLabel = new HBox();
        alignCodeLabel.setAlignment(Pos.CENTER);
        code.setPadding(new Insets(15));
        code.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        alignCodeLabel.getChildren().addAll(code, codeField);
        alignCodeLabel.setSpacing(10);

        HBox alignSubmit = new HBox();
        submit.setPadding(new Insets(10));
        alignSubmit.setAlignment(Pos.CENTER);
        alignSubmit.getChildren().add(submit);

        HBox alignBack = new HBox();
        alignBack.setAlignment(Pos.CENTER);
        back.setPadding(new Insets(15));
        alignBack.getChildren().addAll(back);
        alignBack.setSpacing(20);

        HBox alignError = new HBox();
        alignError.setAlignment(Pos.CENTER);
        alignError.getChildren().add(errorMessage);

        //add horizontal values to vertical box
        VBox vb = new VBox();
        vb.getChildren().addAll(alignTitleLabel, alignNameLabel, alignCodeLabel, alignSubmit, alignBack, alignError);
        //go to manage classes dashboard on click
        back.setOnAction((e -> {
            window.setScene(new Scene(this.teacherManageClasses(), width, height));
        }));
        //on submit button click:
        submit.setOnAction((e -> {
            //classUpdated used to determine the success of the process
            Boolean classUpdated = false;
            //make object to send to server
            JSONObject object = new JSONObject();
            String inputtedName = nameField.getText();
            String inputtedCode = codeField.getText();
            //checks inputted values aren't blank before adding
            if (nameField.getText().isEmpty() || inputtedName.equalsIgnoreCase("") || codeField.getText().isEmpty() || inputtedCode.equalsIgnoreCase("")) {
                errorMessage.setText("Fields can't be blank!");
            } else {
                //add relevant elements to object
                object.put("command", "update_class");
                object.put("name", inputtedName);
                object.put("code", inputtedCode);
                //in try catch to allow program to carry on without errors
                try {
                    //send object to client to send to server
                    classUpdated = client.update_class(object);
                } catch (IOException ex) {
                    //prevent failing in sending to crash the application
                    errorMessage.setText("Something went wrong!");
                }
                //if success:
                if (classUpdated) {
                    //return to manage classes dash
                    window.setScene(new Scene(this.teacherManageClasses(), width, height));
                } //if not successful:
                else {
                    //show error
                    errorMessage.setText("Please check student and code!");
                }
            }
        }));

        //return to be drawn to screen
        return vb;

    }

    private VBox teacherSendPrivateMessage() {
        //define visual objects
        window.setTitle("Teacher Send Message");
        Label titleLabel = new Label("Teacher Send Message");
        Label toLabel = new Label("To");
        Label messageLabel = new Label("Message:");
        TextField to = new TextField();
        TextField messageText = new TextField();
        Button sendButton = new Button("Send");
        Button backButton = new Button("Back");
        final Text errorMessage = new Text();

        //align horizontally
        HBox alignTitleLabel = new HBox();
        alignTitleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPadding(new Insets(20));
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        alignTitleLabel.getChildren().add(titleLabel);

        HBox alignTo = new HBox();
        alignTo.setAlignment(Pos.CENTER);
        toLabel.setPadding(new Insets(30));
        toLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        alignTo.getChildren().addAll(toLabel, to);
        alignTo.setSpacing(10);

        HBox alignMessage = new HBox();
        alignMessage.setAlignment(Pos.CENTER);
        messageLabel.setPadding(new Insets(15));
        messageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        messageText.setPrefHeight(100);
        messageText.setPrefWidth(150);
        alignMessage.getChildren().addAll(messageLabel, messageText);
        alignMessage.setSpacing(10);

        HBox alignSend = new HBox();
        alignSend.setAlignment(Pos.CENTER);
        sendButton.setPadding(new Insets(15));
        alignSend.getChildren().addAll(sendButton);
        alignSend.setSpacing(20);

        HBox alignBack = new HBox();
        alignBack.setAlignment(Pos.CENTER);
        backButton.setPadding(new Insets(15));
        alignBack.getChildren().addAll(backButton);
        alignBack.setSpacing(20);

        HBox alignError = new HBox();
        alignError.setAlignment(Pos.CENTER);
        alignError.getChildren().add(errorMessage);

        //add horizontal elements to vertical elements
        VBox vb = new VBox();
        vb.getChildren().addAll(alignTitleLabel, alignTo, alignMessage, alignSend, alignBack, alignError);
        //send back to dashboard on click
        backButton.setOnAction((e -> {
            window.setScene(new Scene(this.teacherDashboard(), width, height));
        }));
        //on send:
        sendButton.setOnAction((e -> {
            //get inputted values
            String inputtedTo = to.getText().strip();
            String inputtedMessage = messageText.getText().strip();
            //check inputted fields aren't blank
            if (to.getText().isEmpty() || inputtedTo.equalsIgnoreCase("") || messageText.getText().isEmpty() || inputtedMessage.equalsIgnoreCase("")) {
                //display error
                errorMessage.setText("Fields can't be blank!");
            } else {
                //messageSent used to track success of command
                Boolean messageSent = false;
                //make an object to send to server
                JSONObject object = new JSONObject();
                //add required values to object
                object.put("command", "send_message");
                object.put("To", inputtedTo);
                object.put("Message_Text", inputtedMessage);
                object.put("Type", "D");
                //in try to prevent errors crashing the application
                try {
                    System.out.println("in try");
                    //call client's command to send to server
                    messageSent = client.send_message(object);
                } catch (Exception ex) {
                    //display error if method fails
                    errorMessage.setText("Message failed!");
                    System.out.println("failed");
                }
                //if success:
                if (messageSent) {
                    //send back to teacher dashboard
                    window.setScene(new Scene(this.teacherDashboard(), width, height));
                    System.out.println("success");
                } //if unsuccessful:
                else {
                    //display error
                    errorMessage.setText("Message not sent! Please check the name given!");
                    System.out.println("failed because bad input");
                }
            }
        }));
        //return elements to be shown
        return vb;
    }

    private VBox parentSendPrivateMessage() {
        //set up visual objects
        window.setTitle("Parent Send Message");
        Label titleLabel = new Label("Parent Send Message");
        Label toLabel = new Label("To");
        Label messageLabel = new Label("Message:");
        TextField to = new TextField();
        TextField messageText = new TextField();
        Button sendButton = new Button("Send");
        Button backButton = new Button("Back");
        final Text errorMessage = new Text();

        //align elements horizontally
        HBox alignTitleLabel = new HBox();
        alignTitleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPadding(new Insets(20));
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        alignTitleLabel.getChildren().add(titleLabel);

        HBox alignTo = new HBox();
        alignTo.setAlignment(Pos.CENTER);
        toLabel.setPadding(new Insets(30));
        toLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        alignTo.getChildren().addAll(toLabel, to);
        alignTo.setSpacing(10);

        HBox alignMessage = new HBox();
        alignMessage.setAlignment(Pos.CENTER);
        messageLabel.setPadding(new Insets(15));
        messageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        messageText.setPrefHeight(100);
        messageText.setPrefWidth(150);
        alignMessage.getChildren().addAll(messageLabel, messageText);
        alignMessage.setSpacing(10);

        HBox alignSend = new HBox();
        alignSend.setAlignment(Pos.CENTER);
        sendButton.setPadding(new Insets(15));
        alignSend.getChildren().addAll(sendButton);
        alignSend.setSpacing(20);

        HBox alignBack = new HBox();
        alignBack.setAlignment(Pos.CENTER);
        backButton.setPadding(new Insets(15));
        alignBack.getChildren().addAll(backButton);
        alignBack.setSpacing(20);

        HBox alignError = new HBox();
        alignError.setAlignment(Pos.CENTER);
        alignError.getChildren().add(errorMessage);

        //align horizontal elements vertically
        VBox vb = new VBox();
        vb.getChildren().addAll(alignTitleLabel, alignTo, alignMessage, alignSend, alignBack, alignError);
        //on click, send to dashboard:
        backButton.setOnAction((e -> {
            window.setScene(new Scene(this.parentDashboard(), width, height));
        }));
        //on click:
        sendButton.setOnAction((e -> {
            //get values
            String inputtedTo = to.getText().strip();
            String inputtedMessage = messageText.getText().strip();
            //check that they aren't blank
            if (to.getText().isEmpty() || inputtedTo.equalsIgnoreCase("") || messageText.getText().isEmpty() || inputtedMessage.equalsIgnoreCase("")) {
                //display error if they are
                errorMessage.setText("Fields can't be blank!");
            } else {
                //messageSent used to track action success
                Boolean messageSent = false;
                //make object to be sent to server
                JSONObject object = new JSONObject();
                //add values
                object.put("command", "send_message");
                object.put("To", inputtedTo);
                object.put("Message_Text", inputtedMessage);
                object.put("Type", "D");
                try {
                    //try calling messageSent
                    System.out.println("in try");
                    messageSent = client.send_message(object);
                } catch (Exception ex) {
                    //display error message if fails
                    errorMessage.setText("Message failed!");
                    System.out.println("failed");
                }
                //if success:
                if (messageSent) {
                    //back to dashboard
                    window.setScene(new Scene(this.parentDashboard(), width, height));
                    System.out.println("success");
                } //if unsuccessful:
                else {
                    //display error
                    errorMessage.setText("Message not sent! Please check the name given!");
                    System.out.println("failed because bad input");
                }
            }
        }));

        //return items to draw
        return vb;
    }

    private VBox teacherViewPM() {
        //create GUI objects
        window.setTitle("Teacher View Messages");
        Label titleLabel = new Label("Teacher View Messages");
        ListView listOfMessages = new ListView();
        Button backButton = new Button("Back");
        final Text errorMessage = new Text();

        //align horizontal elements
        HBox alignTitleLabel = new HBox();
        alignTitleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPadding(new Insets(20));
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        alignTitleLabel.getChildren().add(titleLabel);

        HBox alignMessages = new HBox();
        alignMessages.setAlignment(Pos.CENTER);
        alignMessages.setPadding(new Insets(20));
        alignMessages.getChildren().add(listOfMessages);

        HBox alignBack = new HBox();
        alignBack.setAlignment(Pos.CENTER);
        backButton.setPadding(new Insets(15));
        alignBack.getChildren().addAll(backButton);
        alignBack.setSpacing(20);

        HBox alignError = new HBox();
        alignError.setAlignment(Pos.CENTER);
        alignError.setPadding(new Insets(15));
        alignError.getChildren().add(errorMessage);
        alignError.setSpacing(20);

        //on click send to teacher dashboard
        backButton.setOnAction((e -> {
            window.setScene(new Scene(this.teacherDashboard(), width, height));
        }));

        try {
            System.out.println("in try catch for viewing messages");
            //make an object to store the sorted messages
            JSONObject storedMessages = client.get_messages();
            System.out.println(storedMessages);
            //pull array of messages from object
            JSONArray messageArray = storedMessages.getJSONArray("messages");
            System.out.println("array is " + messageArray);

            //gui element ListView must be filled using ObservableList
            ObservableList<String> messageItems = FXCollections.observableArrayList();

            List<String> messagesList = new ArrayList<String>();
            System.out.println("approaching for");
            System.out.println("message array length" + messageArray.length());
            //iterates through jsonarray and adds items to ObservableList
            for (int i = 0; i < messageArray.length(); i++) {
                System.out.println("in loop");
                String stringToAdd = "";
                messageItems.add(messageArray.get(i).toString());
                System.out.println("item added");
            }
            //show items in gui
            listOfMessages.setItems((messageItems));

        } catch (Exception e) {
            //display error message if any part unsuccessful
            errorMessage.setText("Messages could not be loaded");
        }

        //assign vertical alignment to horizontal elements
        VBox vb = new VBox();
        vb.getChildren().addAll(alignTitleLabel, alignMessages, alignBack);
        //return elements to be drawn
        return vb;
    }

    private VBox parentViewPM() {
        //set up GUI objects
        window.setTitle("Parent View Messages");
        Label titleLabel = new Label("Parent View Messages");
        ListView listOfMessages = new ListView();
        Button backButton = new Button("Back");
        final Text errorMessage = new Text();

        //align elements horizontally
        HBox alignTitleLabel = new HBox();
        alignTitleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPadding(new Insets(20));
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        alignTitleLabel.getChildren().add(titleLabel);

        HBox alignMessages = new HBox();
        alignMessages.setAlignment(Pos.CENTER);
        alignMessages.setPadding(new Insets(20));
        alignMessages.getChildren().add(listOfMessages);

        HBox alignBack = new HBox();
        alignBack.setAlignment(Pos.CENTER);
        backButton.setPadding(new Insets(15));
        alignBack.getChildren().addAll(backButton);
        alignBack.setSpacing(20);

        HBox alignError = new HBox();
        alignError.setAlignment(Pos.CENTER);
        alignError.setPadding(new Insets(15));
        alignError.getChildren().add(errorMessage);
        alignError.setSpacing(20);

        //on click send back to parent dashboard
        backButton.setOnAction((e -> {
            window.setScene(new Scene(this.parentDashboard(), width, height));
        }));

        try {
            System.out.println("in try catch for viewing messages");
            //store local version of JSON object received from client logic
            JSONObject storedMessages = client.get_messages();
            System.out.println(storedMessages);
            //store local version of JSON array from received JSON Object
            JSONArray messageArray = storedMessages.getJSONArray("messages");
            System.out.println("array is " + messageArray);

            //ListView items must come from ObservableList
            ObservableList<String> messageItems = FXCollections.observableArrayList();

            List<String> messagesList = new ArrayList<String>();
            System.out.println("approaching for");
            System.out.println("message array length" + messageArray.length());
            //takes elements from JSON Array and adds to ObservableList
            for (int i = 0; i < messageArray.length(); i++) {
                System.out.println("in loop");
                String stringToAdd = "";
                messageItems.add(messageArray.get(i).toString());
                System.out.println("item added");
            }
            //displays received messages
            listOfMessages.setItems((messageItems));

        } catch (Exception e) {
            //display error if fail
            errorMessage.setText("Messages could not be loaded");
        }
        //assign vertical position to horizontal elements
        VBox vb = new VBox();
        vb.getChildren().addAll(alignTitleLabel, alignMessages, alignBack);
        //return elements to be drawn
        return vb;
    }

    private VBox parentViewClassesDash() {
        window.setTitle("Parent View Classes");
        Label titleLabel = new Label("Parent View Classes Dashboard");
//        Button viewAll = new Button("View All Classes");
        Button viewByDate = new Button("View By Date");
        Button backButton = new Button("Back");

        HBox alignTitleLabel = new HBox();
        alignTitleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPadding(new Insets(20));
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        alignTitleLabel.getChildren().add(titleLabel);

        HBox alignViewButtons = new HBox();
        alignViewButtons.setAlignment(Pos.CENTER);
//        viewAll.setPadding(new Insets(15));
//        viewAll.setPrefWidth(200);
//        viewAll.setPrefHeight(50);
//        viewAll.setFont(Font.font("Arial",15));
        viewByDate.setPadding(new Insets(15));
        viewByDate.setFont(Font.font("Arial", 15));
        viewByDate.setPrefWidth(200);
        viewByDate.setPrefHeight(50);
//        alignViewButtons.getChildren().addAll(viewAll, viewByDate);
        alignViewButtons.getChildren().add(viewByDate);
        alignViewButtons.setSpacing(20);

        HBox alignBack = new HBox();
        alignBack.setAlignment(Pos.CENTER);
        backButton.setPadding(new Insets(15));
        alignBack.getChildren().addAll(backButton);
        alignBack.setSpacing(20);

        //go back to parent dashboard on click:
        backButton.setOnAction((e -> {
            window.setScene(new Scene(this.parentDashboard(), width, height));
        }));
        //go to view screen on click:
        viewByDate.setOnAction((e -> {
            window.setScene(new Scene(this.parentViewClasses(), width, height));
        }));
//        viewAll.setOnAction((e ->{
//            window.setScene(new Scene(this.parentViewAllClasses(), width, height));
//        }));

        //assign vertical position to horizontal elements
        VBox vb = new VBox();
        vb.getChildren().addAll(alignTitleLabel, alignViewButtons, alignBack);
        //return items to be drawn
        return vb;
    }

    //    private VBox parentViewAllClasses(){
//        Label titleLabel = new Label("Parent - View All Classes");
//        ListView classes = new ListView();
//        Button backButton = new Button("Back");
//        Text errorMessage = new Text("Error");
//
//        HBox alignTitleLabel = new HBox();
//        alignTitleLabel.setAlignment(Pos.CENTER);
//        titleLabel.setPadding(new Insets(20));
//        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
//        alignTitleLabel.getChildren().add(titleLabel);
//
//        HBox alignClassShow = new HBox();
//        alignClassShow.setAlignment(Pos.CENTER);
//        classes.setPadding(new Insets(15));
//        classes.setPrefWidth(150);
//        classes.setPrefHeight(200);
//        alignClassShow.getChildren().add(classes);
//        alignClassShow.setSpacing(20);
//
//        HBox alignBack = new HBox();
//        alignBack.setAlignment(Pos.CENTER);
//        backButton.setPadding(new Insets(15));
//        alignBack.getChildren().addAll(backButton);
//        alignBack.setSpacing(20);
//
//        HBox alignError = new HBox();
//        alignError.setAlignment(Pos.CENTER);
//        alignError.getChildren().add(errorMessage);
//
//        backButton.setOnAction((e ->{
//            window.setScene(new Scene(this.parentViewClassesDash(), width, height));
//        }));
//
//        //try to get and fill listview here
//
//        try {
//            System.out.println("in gui try");
//            JSONObject storedClasses = client.get_classes();
//            System.out.println("gui 916 " + storedClasses);
//            JSONArray classArray = storedClasses.getJSONArray("classes");
//            System.out.println("json array " + classArray);
//
//            ObservableList<String> classItems = FXCollections.observableArrayList();
//
//            List<String> classesList = new ArrayList<String>();
//            System.out.println("approaching for");
//            for (int i = 0; i < classArray.length(); i++) {
//                System.out.println("in for");
//                //classesList.add(classArray.getString(i) );
//                classItems.add(classArray.getString(i));
//                System.out.println(classItems);
//            }
//
//            classes.setItems(classItems);
//        }
//        catch(Exception e){
//            System.out.println("exception hit");
//            errorMessage.setText("Something went wrong");
//        }
//
//        VBox vb = new VBox();
//        vb.getChildren().addAll(alignTitleLabel, alignClassShow, alignBack, alignError);
//        return vb;
//    }
    private VBox parentViewClasses() {
        //set up gui elements
        window.setTitle("Parent View Classes");
        Label titleLabel = new Label("Parent View Classes - Date");
        ListView listOfClasses = new ListView();
        Text sortMessage = new Text("Classes sorted by nearest end date");
        Button backButton = new Button("Back");
        final Text errorMessage = new Text();

        //align elements horizontally
        HBox alignTitleLabel = new HBox();
        alignTitleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPadding(new Insets(20));
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        alignTitleLabel.getChildren().add(titleLabel);

        HBox alignSortLabel = new HBox();
        alignSortLabel.setAlignment(Pos.CENTER);
        alignSortLabel.getChildren().add(sortMessage);

        HBox alignClassShow = new HBox();
        alignClassShow.setAlignment(Pos.CENTER);
        listOfClasses.setPadding(new Insets(15));
        listOfClasses.setPrefWidth(150);
        listOfClasses.setPrefHeight(200);
        alignClassShow.getChildren().add(listOfClasses);
        alignClassShow.setSpacing(20);

        HBox alignBack = new HBox();
        alignBack.setAlignment(Pos.CENTER);
        backButton.setPadding(new Insets(15));
        alignBack.getChildren().addAll(backButton);
        alignBack.setSpacing(20);

        HBox alignError = new HBox();
        alignError.setAlignment(Pos.CENTER);
        alignError.getChildren().add(errorMessage);

        //send back to manage classes page
        backButton.setOnAction((e -> {
            window.setScene(new Scene(this.parentManageClasses(), width, height));
        }));

        try {
            //store object received from client command
            JSONObject storedClasses = client.get_classes_by_date();
            System.out.println(storedClasses);
            //store array from that object
            JSONArray classArray = storedClasses.getJSONArray("classes");
            System.out.println("json array " + classArray);

            //ListView only takes observable list
            ObservableList<String> classItems = FXCollections.observableArrayList();

            List<String> classesList = new ArrayList<String>();
            for (int i = 0; i < classArray.length(); i++) {
                //add items to observable list from json array
                classItems.add(classArray.getString(i));
            }
            //display elements in listview
            listOfClasses.setItems(classItems);

        } catch (Exception e) {
            //display error if fails
            errorMessage.setText("Classes could not be sorted");
        }

        //add vertical position to horizontal elements
        VBox vb = new VBox();
        vb.getChildren().addAll(alignTitleLabel, alignSortLabel, alignClassShow, alignBack, alignError);
        //return item to be drawn on screen
        return vb;
    }

    private VBox teacherViewClasses() {
        //create gui objects
        window.setTitle("Teacher View Classes");
        Label titleLabel = new Label("Teacher View Classes");
        ListView listOfClasses = new ListView();
        Button sortButtonByName = new Button("Sort By Name");
        Button sortButtonByNum = new Button("Sort By Num");
        Button backButton = new Button("Back");
        final Text errorMessage = new Text();

        //align horizontally
        HBox alignTitleLabel = new HBox();
        alignTitleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPadding(new Insets(20));
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        alignTitleLabel.getChildren().add(titleLabel);

        HBox alignClassSort = new HBox();
        alignClassSort.setAlignment(Pos.CENTER);
        sortButtonByName.setPadding(new Insets(15));
        sortButtonByName.setPrefWidth(100);
        sortButtonByName.setPrefHeight(50);
        sortButtonByName.setFont(Font.font("Arial", 10));
        sortButtonByNum.setPadding(new Insets(15));
        sortButtonByNum.setPrefWidth(100);
        sortButtonByNum.setPrefHeight(50);
        sortButtonByNum.setFont(Font.font("Arial", 10));
        alignClassSort.getChildren().addAll(sortButtonByName, sortButtonByNum);
        alignClassSort.setSpacing(20);

        HBox alignClassShow = new HBox();
        alignClassShow.setAlignment(Pos.CENTER);
        listOfClasses.setPadding(new Insets(15));
        listOfClasses.setPrefWidth(150);
        listOfClasses.setPrefHeight(200);
        alignClassSort.getChildren().add(listOfClasses);
        alignClassSort.setSpacing(20);

        HBox alignBack = new HBox();
        alignBack.setAlignment(Pos.CENTER);
        backButton.setPadding(new Insets(15));
        alignBack.getChildren().addAll(backButton);
        alignBack.setSpacing(20);

        HBox alignError = new HBox();
        alignError.setAlignment(Pos.CENTER);
        alignError.getChildren().add(errorMessage);

        //send back to teacher's manage classes page on click
        backButton.setOnAction((e -> {
            window.setScene(new Scene(this.teacherManageClasses(), width, height));
        }));

        //give horizontal elements a vertical position
        VBox vb = new VBox();
        vb.getChildren().addAll(alignTitleLabel, alignClassSort, alignClassShow, alignBack, alignError);
        //on click:
        sortButtonByName.setOnAction((e -> {
            try {
                //store object received from client's command
                JSONObject storedClasses = client.get_classes_by_name();
                //store array from that object
                JSONArray classArray = storedClasses.getJSONArray("classes");
                System.out.println("json array " + classArray);

                //create observable list to use for listview
                ObservableList<String> classItems = FXCollections.observableArrayList();

                List<String> classesList = new ArrayList<String>();
                for (int i = 0; i < classArray.length(); i++) {
                    //add items from json array to listview list
                    classItems.add(classArray.getString(i));
                }
                //place items from list into listview
                listOfClasses.setItems(classItems);

            } catch (Exception ex) {
                //display an error if anything goes wrong
                errorMessage.setText("Something went wrong!");
            }
        }
        ));
        //on click:
        sortButtonByNum.setOnAction((e -> {
            try {
                //store object received from client's command
                JSONObject storedClasses = client.get_classes_by_size();
                //store array from that object
                JSONArray classArray = storedClasses.getJSONArray("classes");
                System.out.println("json array " + classArray);
                //initalise observable list to use for listview
                ObservableList<String> classItems = FXCollections.observableArrayList();

                List<String> classesList = new ArrayList<String>();
                //iterate through json array and place items in observable list
                for (int i = 0; i < classArray.length(); i++) {
                    //classesList.add(classArray.getString(i) );
                    classItems.add(classArray.getString(i));
                }
                //load observable list into listview
                listOfClasses.setItems(classItems);
            } catch (Exception ex) {
                //display error if anything goes wrong
                errorMessage.setText("Something went wrong!");
            }
        }));
        //return items to draw
        return vb;
    }

    private VBox parentJoinClass() {
        //initialise gui elements
        window.setTitle("Parent Join Classes");
        Label titleLabel = new Label("Parent Join Classes");
        Label codeLabel = new Label("Code:");
        TextField codeBox = new TextField();
        Button submitButton = new Button("Submit");
        Button backButton = new Button("Back");

        final Text errorMessage = new Text();

        //align horizontally
        HBox alignTitleLabel = new HBox();
        alignTitleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPadding(new Insets(20));
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        alignTitleLabel.getChildren().add(titleLabel);

        HBox alignCodes = new HBox();
        alignCodes.setAlignment(Pos.CENTER);
        codeLabel.setPadding(new Insets(15));
        codeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        alignCodes.getChildren().addAll(codeLabel, codeBox);
        alignCodes.setSpacing(10);

        HBox alignSubmit = new HBox();
        alignSubmit.setAlignment(Pos.CENTER);
        submitButton.setPadding(new Insets(15));
        alignSubmit.getChildren().addAll(submitButton);
        alignSubmit.setSpacing(20);

        HBox alignBack = new HBox();
        alignBack.setAlignment(Pos.CENTER);
        backButton.setPadding(new Insets(15));
        alignBack.getChildren().addAll(backButton);
        alignBack.setSpacing(20);

        //send back to parent manage classes dashboard
        backButton.setOnAction((e -> {
            window.setScene(new Scene(this.parentManageClasses(), width, height));
        }));

        HBox alignError = new HBox();
        alignError.setAlignment(Pos.CENTER);
        alignError.getChildren().add(errorMessage);

        //give vertical position to horizontal elements
        VBox vb = new VBox();
        vb.getChildren().addAll(alignTitleLabel, alignCodes, alignSubmit, alignBack, alignError);

        submitButton.setOnAction(e -> {
            //classjoined used to track success of action
            Boolean classJoined = false;
            //make object to send to server
            JSONObject object = new JSONObject();
            String inputtedCode = codeBox.getText();
            //display error if code box is empty
            if (codeBox.getText().isEmpty() || inputtedCode.equalsIgnoreCase("")) {
                errorMessage.setText("Code blank!");
            } else {
                //add values to send
                object.put("command", "join_class");
                object.put("code", inputtedCode);
                try {
                    //send object to client method to be sent to server
                    classJoined = client.join_class(object);
                } catch (IOException ex) {
                    errorMessage.setText("Something went wrong!");
                }
                //send back to dashboard if successful
                if (classJoined) {
                    window.setScene(new Scene(this.parentManageClasses(), width, height));
                } else {
                    //display error if fail
                    errorMessage.setText("Please check the code!");
                }
            }
        });
        //return object to draw
        return vb;
    }

    private VBox teacherCreateClass() {
        //intialise gui objects
        window.setTitle("Teacher Create Classes");
        Label titleLabel = new Label("Teacher Create Classes");
        Label nameLabel = new Label("Class name:");
        Label codeLabel = new Label("Class code:");
        TextField nameField = new TextField();
        TextField codeField = new TextField();
        Button submit = new Button("Submit");
        Button back = new Button("Back");
        final Text errorMessage = new Text();

        //align elements horizontally
        HBox alignTitleLabel = new HBox();
        alignTitleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPadding(new Insets(20));
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        alignTitleLabel.getChildren().add(titleLabel);

        HBox alignNames = new HBox();
        alignNames.setAlignment(Pos.CENTER);
        nameLabel.setPadding(new Insets(15));
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        alignNames.getChildren().addAll(nameLabel, nameField);
        alignNames.setSpacing(10);

        HBox alignCodes = new HBox();
        alignCodes.setAlignment(Pos.CENTER);
        codeLabel.setPadding(new Insets(15));
        codeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        alignCodes.getChildren().addAll(codeLabel, codeField);
        alignCodes.setSpacing(10);

        HBox alignSubmit = new HBox();
        alignSubmit.setAlignment(Pos.CENTER);
        submit.setPadding(new Insets(15));
        alignSubmit.getChildren().addAll(submit);
        alignSubmit.setSpacing(20);

        HBox alignBack = new HBox();
        alignBack.setAlignment(Pos.CENTER);
        back.setPadding(new Insets(15));
        alignBack.getChildren().addAll(back);
        alignBack.setSpacing(20);

        HBox alignError = new HBox();
        alignError.setAlignment(Pos.CENTER);
        alignError.getChildren().add(errorMessage);

        //on press send back to teacher manage class dashboard
        back.setOnAction((e -> {
            window.setScene(new Scene(this.teacherManageClasses(), width, height));
        }));
        //on submit:
        submit.setOnAction((e -> {
            String inputtedName = nameField.getText().strip();
            String inputtedCode = codeField.getText().strip();
            //check fields aren't empty
            if (nameField.getText().isEmpty() || inputtedName.equalsIgnoreCase("") || codeField.getText().isEmpty() || inputtedCode.equalsIgnoreCase("")) {
                errorMessage.setText("Fields can't be blank!");
                //check inputs don't have spaces
            } else if (inputtedName.split(" ").length > 1 || inputtedCode.split(" ").length > 1) {
                errorMessage.setText("Fields can't have spaces");
            } else {
                //classCreated used to track success of process
                Boolean classCreated = false;
                //create object to send and add inputted values
                JSONObject object = new JSONObject();
                object.put("command", "add_class");
                object.put("code", inputtedCode);
                object.put("name", inputtedName);
                try {
                    //send to client logic, to send to server
                    classCreated = client.add_class(object);
                } catch (IOException ex) {
                    //display error if fails
                    errorMessage.setText("Something went wrong!");
                }
                //go back to manage classes dashboard on success
                if (classCreated) {
                    window.setScene(new Scene(teacherManageClasses(), width, height));
                } else {
                    //display error on fail
                    errorMessage.setText("Something went wrong! Is that code/name already in use?");
                }
            }
        }));

        //assign vertical positioning to horizontal elements
        VBox vb = new VBox();
        vb.getChildren().addAll(alignTitleLabel, alignNames, alignCodes, alignSubmit, alignBack, alignError);
        //return drawable object
        return vb;

    }

    private VBox teacherDeleteClass() {
        //set up gui objects
        window.setTitle("Teacher Delete Classes");
        Label titleLabel = new Label("Teacher Delete Classes");
        Label nameLabel = new Label("Class name:");
        TextField nameField = new TextField();
        Button submit = new Button("Submit");
        Button back = new Button("Back");
        final Text errorMessage = new Text();

        //align objects horizontally
        HBox alignTitleLabel = new HBox();
        alignTitleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPadding(new Insets(20));
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        alignTitleLabel.getChildren().add(titleLabel);

        HBox alignNames = new HBox();
        alignNames.setAlignment(Pos.CENTER);
        nameLabel.setPadding(new Insets(15));
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        alignNames.getChildren().addAll(nameLabel, nameField);
        alignNames.setSpacing(10);

        HBox alignSubmit = new HBox();
        alignSubmit.setAlignment(Pos.CENTER);
        submit.setPadding(new Insets(15));
        alignSubmit.getChildren().addAll(submit);
        alignSubmit.setSpacing(20);

        HBox alignBack = new HBox();
        alignBack.setAlignment(Pos.CENTER);
        back.setPadding(new Insets(15));
        alignBack.getChildren().addAll(back);
        alignBack.setSpacing(20);

        HBox alignError = new HBox();
        alignError.setAlignment(Pos.CENTER);
        alignError.getChildren().add(errorMessage);

        //assign vertical values to horizontal elements
        VBox vb = new VBox();
        vb.getChildren().addAll(alignTitleLabel, alignNames, alignSubmit, alignBack, alignError);
        //go back to manage classes on back button press
        back.setOnAction((e -> {
            window.setScene(new Scene(this.teacherManageClasses(), width, height));
        }));
        //on submit button click:
        submit.setOnAction(((e -> {
            String inputtedName = nameField.getText().strip();
            //check inputteed value isnt blank
            if (nameField.getText().isEmpty() || inputtedName.equalsIgnoreCase("")) {
                errorMessage.setText("Please enter a name!");
            } else {
                //class delete success tracked in boolean value
                Boolean classDeleted = false;
                //create and prepare object to send to server
                JSONObject object = new JSONObject();
                object.put("command", "delete_class");
                object.put("name", inputtedName);
                try {
                    //call client command with the object
                    classDeleted = client.delete_class(object);
                } catch (IOException ex) {
                    //display error message if anything goes wrong
                    errorMessage.setText("Something went wrong!");
                }
                //if success send back to manage classes dashboard
                if (classDeleted) {
                    window.setScene(new Scene(teacherManageClasses(), width, height));
                } else {
                    //display error if unsuccessful
                    errorMessage.setText("Something went wrong! Check the class exists and you are the teacher!");
                }
            }
        })));

        //return values to be drawn
        return vb;

    }

}

