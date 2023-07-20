module com.example.schoolsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires json;
    requires org.junit.jupiter.api;


    opens com.example.schoolsystem to javafx.fxml;
    exports com.example.schoolsystem.Server;
    exports com.example.schoolsystem.Client;
    exports com.example.schoolsystem.Client.Controllers;
    exports com.example.schoolsystem.Testing;
    exports com.example.schoolsystem.Client.GUI;
    opens com.example.schoolsystem.Testing to javafx.fxml;
}