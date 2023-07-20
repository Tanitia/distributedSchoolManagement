package com.example.schoolsystem;

import com.example.schoolsystem.Server.ClientHandler;
import com.example.schoolsystem.Testing.Mocker;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.assertEquals;

//22 server unit tests:
//4 login tests
//2 get message tests
//5 send message tests
//3 add class tests
//3 delete class tests
//2 join class tests
//3 update class tests
class ClientHandlerTest {

    @BeforeAll
    static void setup() {
        Mocker.createUsers();
        Mocker.initialiseMessages();
        Mocker.createClasses();
    }

    @org.junit.jupiter.api.Test
    void loginSuccessTeacher() {
        JSONObject object = new JSONObject();
        object.put("command", "login");
        object.put("username", "Tanitia");
        object.put("password", "password123");

        JSONObject response = ClientHandler.login(object);

        assertEquals("login_success", response.getString("command"));
        assertEquals("teacher", response.getString("role"));
        assertEquals(2, response.getJSONArray("messages").length());
    }

    @org.junit.jupiter.api.Test
    void loginSuccessParent() {
        JSONObject object = new JSONObject();
        object.put("command", "login");
        object.put("username", "Lucy");
        object.put("password", "password123");

        JSONObject response = ClientHandler.login(object);

        assertEquals("login_success", response.getString("command"));
        assertEquals("parent", response.getString("role"));
        assertEquals(2, response.getJSONArray("messages").length());
    }

    @org.junit.jupiter.api.Test
    void loginFailTeacher() {
        JSONObject object = new JSONObject();
        object.put("command", "login");
        object.put("username", "Tanitia");
        object.put("password", "wrongpassword");

        JSONObject response = ClientHandler.login(object);

        assertEquals("login_fail", response.getString("command"));
    }

    @org.junit.jupiter.api.Test
    void loginFailParent() {
        JSONObject object = new JSONObject();
        object.put("command", "login");
        object.put("username", "Levi");
        object.put("password", "wrongpassword");

        JSONObject response = ClientHandler.login(object);

        assertEquals("login_fail", response.getString("command"));
    }

    @org.junit.jupiter.api.Test
    void getMessageSuccess() {
        JSONObject object = new JSONObject();
        object.put("command", "get_messages");
        object.put("username", "Tanitia");

        JSONObject response = ClientHandler.get_messages(object);

        //can be successful even if 0 messages returned
        assertEquals("get_messages_success", response.getString("command"));
    }

    @org.junit.jupiter.api.Test
    void getMessageFail() {
        JSONObject object = new JSONObject();
        object.put("command", "get_messages");
        object.put("username", "fakeName");

        JSONObject response = ClientHandler.get_messages(object);

        //should output fail message if user does not exist
        //this probably would never happen, but good to test anyway
        assertEquals("get_messages_fail", response.getString("command"));
    }

    @org.junit.jupiter.api.Test
    void sendMessageTeacherSuccess() {
        JSONObject object = new JSONObject();
        object.put("command", "send_message");
        object.put("To", "Lucy");
        object.put("Message_Text", "UNIT TEST");
        object.put("From", "Tanitia");
        object.put("Sent", "1679234781");
        object.put("Type", "D");

        JSONObject response = ClientHandler.get_messages(object);

        assertEquals("send_message_success", response.getString("command"));
    }

    @org.junit.jupiter.api.Test
    void sendMessageTeacherFail() {
        JSONObject object = new JSONObject();
        object.put("command", "send_message");
        object.put("To", "Fake_user");
        object.put("Message_Text", "UNIT TEST");
        object.put("From", "Tanitia");
        object.put("Sent", "1679234781");
        object.put("Type", "D");

        JSONObject response = ClientHandler.get_messages(object);

        assertEquals("send_message_fail", response.getString("command"));
    }

    @org.junit.jupiter.api.Test
    void sendMessageParentToParent() {
        JSONObject object = new JSONObject();
        object.put("command", "send_message");
        object.put("To", "Levi");
        object.put("Message_Text", "UNIT TEST");
        object.put("From", "Lucy");
        object.put("Sent", "1679234781");
        object.put("Type", "D");

        JSONObject response = ClientHandler.get_messages(object);

        assertEquals("send_message_fail", response.getString("command"));
    }

    @org.junit.jupiter.api.Test
    void sendMessageParentToInvalidUser() {
        JSONObject object = new JSONObject();
        object.put("command", "send_message");
        object.put("To", "FAKE_USER");
        object.put("Message_Text", "UNIT TEST");
        object.put("From", "Lucy");
        object.put("Sent", "1679234781");
        object.put("Type", "D");

        JSONObject response = ClientHandler.get_messages(object);

        assertEquals("send_message_fail", response.getString("command"));
    }

    @org.junit.jupiter.api.Test
    void sendMessageParentSuccess() {
        JSONObject object = new JSONObject();
        object.put("command", "send_message");
        object.put("To", "Tanitia");
        object.put("Message_Text", "UNIT TEST");
        object.put("From", "Lucy");
        object.put("Sent", "1679234781");
        object.put("Type", "D");

        JSONObject response = ClientHandler.get_messages(object);

        assertEquals("send_message_success", response.getString("command"));
    }

    @org.junit.jupiter.api.Test
    void add_class_success() {
        JSONObject object = new JSONObject();
        object.put("command", "add_class");
        object.put("code", "Z1");
        object.put("name", "Zoology_1");
        object.put("teacher", "Tanitia");

        JSONObject response = ClientHandler.add_class(object);
        String name = object.getString("name");
        String code = object.getString("code");

        //check class has been added
        assertEquals(true, Mocker.JSONclasses.has(name));
        //check code has been added
        assertEquals(true, Mocker.codeMap.containsKey(code));
        //check server has registered success
        assertEquals("add_class_success", response.get("command"));
    }

    @org.junit.jupiter.api.Test
    void add_class_fail_name() {
        JSONObject object = new JSONObject();
        object.put("command", "add_class");
        object.put("code", "Z1");
        object.put("name", "English_1");
        object.put("teacher", "Tanitia");

        JSONObject response = ClientHandler.add_class(object);
        String name = object.getString("name");
        String code = object.getString("code");

        //check class has not been added
        assertEquals(false, Mocker.JSONclasses.has(name));
        //check code has not been added
        assertEquals(false, Mocker.codeMap.containsKey(code));
        //check server has registered fail
        assertEquals("add_class_fail", response.get("command"));
    }

    @org.junit.jupiter.api.Test
    void add_class_fail_code() {
        JSONObject object = new JSONObject();
        object.put("command", "add_class");
        object.put("code", "H1");
        object.put("name", "New_1");
        object.put("teacher", "Tanitia");

        JSONObject response = ClientHandler.add_class(object);
        String name = object.getString("name");
        String code = object.getString("code");

        //check class has not been added
        assertEquals(false, Mocker.JSONclasses.has(name));
        //check code has not been added
        assertEquals(false, Mocker.codeMap.containsKey(code));
        //check server has registered fail
        assertEquals("add_class_fail", response.get("command"));
    }

    @org.junit.jupiter.api.Test
    void delete_class_success() {
        JSONObject object = new JSONObject();
        object.put("command", "delete_class");
        object.put("name", "English_1");
        object.put("teacher", "Tanitia");

        JSONObject response = ClientHandler.delete_class(object);
        String name = object.getString("name");

        //check server has registered success
        assertEquals("delete_class_success", response.get("command"));
    }

    @org.junit.jupiter.api.Test
    void delete_class_wrong_teacher() {
        JSONObject object = new JSONObject();
        object.put("command", "delete_class");
        object.put("name", "Maths_1");
        object.put("teacher", "Tanitia");

        JSONObject response = ClientHandler.delete_class(object);
        String name = object.getString("name");

        //check server has registered fail
        assertEquals("delete_class_fail", response.get("command"));
    }

    //write test for fake class name
    @org.junit.jupiter.api.Test
    void delete_class_wrong_name() {
        JSONObject object = new JSONObject();
        object.put("command", "delete_class");
        object.put("name", "NO_CLASS");
        object.put("teacher", "Tanitia");

        JSONObject response = ClientHandler.delete_class(object);

        //check server has registered fail
        assertEquals("delete_class_fail", response.get("command"));
    }

    @org.junit.jupiter.api.Test
    void join_class_success() {
        JSONObject object = new JSONObject();
        object.put("command", "join_class");
        object.put("name", "Levi");
        object.put("code", "E1");

        JSONObject response = ClientHandler.join_class(object);

        //check server has registered success
        assertEquals("join_success", response.get("command"));
    }

    @org.junit.jupiter.api.Test
    void join_class_fail() {
        JSONObject object = new JSONObject();
        object.put("command", "join_class");
        object.put("name", "Levi");
        object.put("code", "FAKE_CODE");

        JSONObject response = ClientHandler.join_class(object);

        //check server has registered fail
        assertEquals("join_fail", response.get("command"));
    }


    @org.junit.jupiter.api.Test
    void update_class_success() {
        JSONObject object = new JSONObject();
        object.put("command", "update_class");
        object.put("code", "H1");
        object.put("name", "Lucy");

        JSONObject response = ClientHandler.update_class(object);

        //check server has registered success
        assertEquals("update_success", response.get("command"));
    }

    @org.junit.jupiter.api.Test
    void update_class_code_fail() {
        JSONObject object = new JSONObject();
        object.put("command", "update_class");
        object.put("code", "FAKECODE");
        object.put("name", "Lucy");

        JSONObject response = ClientHandler.update_class(object);

        //check server has registered success
        assertEquals("update_fail", response.get("command"));
    }

    @org.junit.jupiter.api.Test
    void update_class_name_fail() {
        JSONObject object = new JSONObject();
        object.put("command", "update_class");
        object.put("code", "H1");
        object.put("name", "FAKENAME");

        JSONObject response = ClientHandler.update_class(object);

        //check server has registered success
        assertEquals("update_fail", response.get("command"));
    }

}