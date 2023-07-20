package com.example.schoolsystem.Server;

import com.example.schoolsystem.Testing.Mocker;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ClientHandler implements Runnable {
    private final Socket client;
    private final BufferedReader input;

    private final PrintWriter output;

    public ClientHandler(Socket clientSocket) throws IOException {
        //set up socket, readers and writers per client
        this.client = clientSocket;
        this.input = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
        //autoflush flushes the writer before the buffers gets full
        this.output = new PrintWriter(clientSocket.getOutputStream(), true);


    }

    @Override
    public void run() {
        //try to catch any failures:
        try {
            //while - heartbeat design pattern
            while (true) {
                //get input from client and save it
                String inputLine = this.input.readLine();
                JSONObject object = new JSONObject(inputLine);
                System.out.println(object);
                //get command from object
                String clientCommand = object.getString("command");

                //read command and pass to relevant method - front controller design pattern
                if (clientCommand.equalsIgnoreCase("login")) {
                    this.output.println(login(object));
                    this.output.flush();
                } else if (clientCommand.equalsIgnoreCase("get_messages")) {
                    this.output.println(get_messages(object));
                    this.output.flush();
                } else if (clientCommand.equalsIgnoreCase("send_message")) {
                    this.output.println(send_message(object));
                    this.output.flush();
                } else if (clientCommand.equalsIgnoreCase("add_class")) {
                    this.output.println(add_class(object));
                    this.output.flush();
                } else if (clientCommand.equalsIgnoreCase("delete_class")) {
                    this.output.println(delete_class(object));
                    this.output.flush();
                } else if (clientCommand.equalsIgnoreCase("join_class")) {
                    this.output.println(join_class(object));
                    this.output.flush();
                } else if (clientCommand.equalsIgnoreCase("update_class")) {
                    this.output.println(update_class(object));
                    this.output.flush();
                } else if (clientCommand.equalsIgnoreCase("get_classes_by_size")) {
                    this.output.println(get_classes_by_size(object));
                    this.output.flush();
                    //teacher only
                } else if (clientCommand.equalsIgnoreCase("get_classes_by_name")) {
                    this.output.println(get_classes_by_name(object));
                    this.output.flush();
                    //teacher only
                } else if (clientCommand.equalsIgnoreCase("get_classes_by_date")) {
                    this.output.println(get_classes_by_date(object));
                    this.output.flush();
                    //parent only
                } else if (clientCommand.equalsIgnoreCase("get_classes")) {
                    this.output.println(get_classes(object));
                    this.output.flush();
                }
            }
        } catch (IOException e) {
            //log error
            System.out.println("Exception while connected");
            System.out.println(e.getMessage());
        }
    }

    public static JSONObject login(JSONObject object) {
        //create object to return
        JSONObject response = new JSONObject();
        //get values from object sent by client
        String clientUsername = object.getString("username");
        String clientPassword = object.getString("password");
        //in try to stop crashing, such as if given username does not exist in Mocker
        try {
            //grab data from Mocker
            JSONObject userData = Mocker.JSONusers.getJSONObject(clientUsername);
            //load in user's current messages from Mocker (would've been used for broadcasts)
            JSONArray userMessages = Mocker.JSONclientMessages.getJSONArray(clientUsername);
            //sout just for demo purposes:
            System.out.println(Utils.encrypt(clientPassword));
            //fill in success and relevant values into object to be sent back to client
            response.put("command", "login_success");
            response.put("messages", userMessages);
            //grab user's role from Mocker and add to response
            String role = userData.getString("role");
            response.put("role", role);
            //shouldn't be hit, but just in case:
            //if nothing in given user's userData, log in should fail
            if (userData == null) {
                response.put("command", "login_fail");
            }
            //if given password is wrong, login should fail
            if (!userData.getString("password").equals(Utils.encrypt(clientPassword))) {
                response.put("command", "login_fail");
            }
        } catch (Exception e) {
            //if any part of grabbing data from mocker fails, login should fail
            response.put("command", "login_fail");
        }
        //send response to client
        return response;
    }

    public static JSONObject get_messages(JSONObject object) {
        System.out.println("in get messages command");
        //pull info from received object from client
        String clientUsername = object.getString("username");
        JSONObject response = new JSONObject();
        //try to catch errors:
        try {
            //put list of messages into response object along with success message
            JSONArray userMessages = Mocker.JSONclientMessages.getJSONArray(clientUsername);
            response.put("command", "get_messages_success");
            response.put("messages", userMessages);
        } catch (Exception e) {
            //put in fail message
            //messages is a blank array in case other areas of the program try to
            //process the value under the "messages" key
            response.put("command", "get_messages_fail");
            response.put("messages", new JSONArray());
        }
        System.out.println("response returned");
        return response;
    }

    public JSONObject send_message(JSONObject object) {
        System.out.println("in send_message command");
        //pull values from object received from client
        String To = object.getString("To");
        String Message_Text = object.getString("Message_Text");
        String From = object.getString("From");
        String Sent = object.getString("Sent");
        //type was used to determine whether a messages was a broadcast or direct message
        String Type = object.getString("Type");
        JSONObject response = new JSONObject();
        //if the user being sent to exists:
        if (Mocker.JSONusers.has(To)) {
            System.out.println(To + "has been found");
            //check if sender is a teacher
            //as teachers have permissions to send to all users
            if (Mocker.JSONusers.getJSONObject(From).getString("role").equalsIgnoreCase("teacher")) {
                try {
                    //always try sending when user is teacher
                    Mocker.addClientMessage(To, Message_Text, From, Sent, Type);
                    //add command to object
                    response.put("command", "send_message_success");
                    System.out.println("message sent because sender is teacher");
                } catch (Exception e) {
                    //add a fail message if saving the message faila
                    System.out.println("in first catch for send_message command");
                    response.put("command", "send_message_fail");
                }
            }
            //if user is parent
            else {
                //if message recipient is also a parent
                if (Mocker.JSONusers.getJSONObject(To).getString("role").equalsIgnoreCase("parent")) {
                    System.out.println("parents cant message parents!");
                    //add fail to response
                    response.put("command", "send_message_fail");
                } else {
                    try { //if not, try saving message
                        Mocker.addClientMessage(To, Message_Text, From, Sent, Type);
                        System.out.println("parent successfully send teacher message");
                        response.put("command", "send_message_success");
                    } catch (Exception e) {
                        //add fail message if message can not be saved
                        response.put("command", "send_message_fail");
                        System.out.println("error caught!");
                    }
                }
            }
        } else {
            //fail if message recipient doesn't exist
            System.out.println("that user isn't found!");
            response.put("command", "send_message_fail");
        }
        return response;
    }

    public static JSONObject add_class(JSONObject object) {
        //get values from received object
        String code = object.getString("code");
        String name = object.getString("name");
        String teacher = object.getString("teacher");
        JSONObject response = new JSONObject();
        //check code or class doesn't already exist
        if (!Mocker.JSONclasses.has(name) && !Mocker.codeMap.containsKey(code)) {
            try {
                //try adding class to mocker
                //and add success command to server response if successful
                Mocker.addClass(name, code, teacher);
                response.put("command", "add_class_success");
            } catch (Exception e) {
                //add fail to server response if fails
                response.put("command", "add_class_fail");
            }
        } else {
            //if code or class name already in use, add fail to response
            response.put("command", "add_class_fail");
        }
        return response;
    }

    public static JSONObject delete_class(JSONObject object) {
        //prints just used for demo
        System.out.println("in delete in server");
        //create response object
        JSONObject response = new JSONObject();
        //pull values from object received from client
        String name = object.getString("name");
        String teacher = object.getString("teacher");
        System.out.println(teacher);
        System.out.println("logged in teacher " + teacher + " mocker teacher " + Mocker.JSONclasses.getJSONObject(name).getString("teacher"));
        //if the class doesn't exist, or doesn't belong to this teacher
        if (!Mocker.JSONclasses.has(name) || !Mocker.JSONclasses.getJSONObject(name).getString("teacher").equalsIgnoreCase(teacher)) {
            response.put("command", "delete_class_fail");
            System.out.println("failed because incorrect teacher");
        } else {
            try {
                System.out.println("trying to delete");
                //grab the given class' code
                String code = Mocker.JSONclasses.getJSONObject(name).getString("code");
                //remove both from the mocker
                Mocker.deleteClass(name);
                Mocker.codeMap.remove(code);
                //add success message to server response
                response.put("command", "delete_class_success");
                System.out.println("adding success");
            } catch (Exception e) {
                //add fail message to response if unsuccessful
                response.put("command", "delete_class_fail");
                System.out.println("exception occured, adding fail");
            }
        }
        System.out.println("returning response");
        //send response to client
        return response;
    }

    public static JSONObject join_class(JSONObject object) {
        //create response object
        JSONObject response = new JSONObject();
        System.out.println("join class initiated");
        //grab values from object received from client
        String Code = object.getString("code");
        String name = object.getString("name");
        try {
            //find class name associated with given code
            String className = Mocker.codeMap.get(Code);
            //add student to that class in the mocker
            Mocker.addStudentsToClass(name, className);
            //add success message to server response
            response.put("command", "join_success");
        } catch (Exception e) {
            //add fail message to server response if exception occurs
            response.put("command", "join_fail");
        }
        //prints for demo purposes
        System.out.println(Mocker.JSONclasses);
        System.out.println(response);
        //return to client
        return response;
    }

    public static JSONObject update_class(JSONObject object) {
        //essentially same as above....
        JSONObject response = new JSONObject();
        System.out.println("update class initiated");
        //pull values from object received from client
        String Code = object.getString("code");
        String name = object.getString("name");
        try {
            System.out.println("in try");
            //finds class name with given code and tries to add the student to that class
            String className = Mocker.codeMap.get(Code);
            Mocker.addStudentsToClass(name, className);
            response.put("command", "update_success");
        } catch (Exception e) {
            //sends fail message if unable
            System.out.println("in exception");
            response.put("command", "update_fail");
        }
        System.out.println(Mocker.JSONclasses);
        System.out.println(response);
        System.out.println("returning response to client");
        return response;
    }

    public JSONObject get_classes_by_size(JSONObject object) {
        JSONObject response = new JSONObject();
        //defaults as success
        response.put("command", "get_classes_success_by_size");
        String teacher = object.getString("teacher");
        try {
            //grabs all keys from classes object and uses them to iterate through
            JSONObject filteredClasses = new JSONObject();
            Iterator<String> keys = Mocker.JSONclasses.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                //compares the teacher of the class to the teacher who is logged in
                String currentTeacher = Mocker.JSONclasses.getJSONObject(key).get("teacher").toString();

                System.out.println("Current Teacher:'" + currentTeacher + "'");
                System.out.println("Compared Teacher:'" + teacher + "'");

                if (currentTeacher.equalsIgnoreCase(teacher)) {
                    //stores class if logged in teacher teaches current class
                    System.out.println("We are in the if");
                    JSONObject actualClass = Mocker.JSONclasses.getJSONObject(key);
                    System.out.println(actualClass);
                    filteredClasses.put(key, actualClass);

                }
            }
            System.out.println(filteredClasses);
            //add teacher's classes to response
            response.put("classes", filteredClasses);
        } catch (Exception e) {
            //adds fail message if process fails
            //adds empty object for classes in case other areas try to work with
            //a json object receive from this process
            response.put("command", "get_classes_fail_by_size");
            response.put("classes", new JSONObject());
        }
        return response;
    }

    public JSONObject get_classes_by_name(JSONObject object) {
        //teacher only
        //make response object
        JSONObject response = new JSONObject();
        //grab teacher from client object
        String teacher = object.getString("teacher");
        JSONObject filteredClasses = new JSONObject();
        try {
            //iterates through for only teacher's classes
            Iterator<String> keys = Mocker.JSONclasses.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                String currentTeacher = Mocker.JSONclasses.getJSONObject(key).get("teacher").toString();

                System.out.println("Current Teacher:'" + currentTeacher + "'");
                System.out.println("Compared Teacher:'" + teacher + "'");

                if (currentTeacher.equalsIgnoreCase(teacher)) {
                    System.out.println("We are in the if");
                    JSONObject actualClass = Mocker.JSONclasses.getJSONObject(key);
                    System.out.println(actualClass);
                    filteredClasses.put(key, actualClass);

                }
            }
            //add success command if all goes well
            response.put("command", "get_classes_success_by_class_name");
        } catch (Exception e) {
            //add fail command if fails
            response.put("command", "get_classes_fail_by_class_name");
        }
        System.out.println(filteredClasses);
        //add classes to server response
        response.put("classes", filteredClasses);
        //return response to client
        return response;
    }

    public JSONObject get_classes_by_date(JSONObject object) {
        JSONObject response = new JSONObject();
        response.put("command", "get_classes_success_by_date");
        String student = object.getString("student");
        JSONObject filteredClasses = new JSONObject();
        Iterator<String> keys = Mocker.JSONclasses.keys();

        //iterate through all classes
        while (keys.hasNext()) {
            String key = keys.next();
            //find list of students for current class
            String studentList = Mocker.JSONclasses.getJSONObject(key).get("students").toString().replace("[", "").replace("]", "").replace("\"", "");
            String[] currentStudent = studentList.split(",");
            for (int i = 0; i < currentStudent.length; i++) {
                System.out.println("Current student in array:'" + currentStudent[i] + "'");
                if (currentStudent[i].equalsIgnoreCase(student)) {
                    //add class to response if current student is a student of that class
                    System.out.println("We are in the if");
                    JSONObject actualClass = Mocker.JSONclasses.getJSONObject(key);
                    System.out.println(actualClass);
                    filteredClasses.put(key, actualClass);

                }
            }

            System.out.println("Current student:'" + currentStudent + "'");
            System.out.println("Compared students:'" + student + "'");
        }

        //return classes
        System.out.println(filteredClasses);
        response.put("classes", filteredClasses);
        return response;
    }

    public JSONObject get_classes(JSONObject object) {
        //this command works differently depending on the role of the user
        System.out.println(object);
        JSONObject response = new JSONObject();
        System.out.println("get classes object made on server");
        String role = object.getString("role");
        String name = object.getString("name");
        try {
            //if the user is a parent they are only eligile to see details of classes
            //their student is enrolled in
            if (role.equalsIgnoreCase("parent")) {
                //iterate and collate details of each class in the mocker
                Iterator<String> keys = Mocker.JSONclasses.keys();

                Map<String, Integer> tempMap = new HashMap<>();
                JSONObject tempObject = new JSONObject();
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONObject keyValue = Mocker.JSONclasses.getJSONObject(key);
                    JSONArray listOfStudents = keyValue.getJSONArray("students");
                    if (listOfStudents.toList().contains(name)) {
                        tempObject.put(key, keyValue);
                    }

                }
                //add student's classes to object
                response.put("classes", tempObject);
            } else { //if teacher
                //they are eligible to see all classes
                //so add all classes in the mocker to the response
                response.put("classes", Mocker.JSONclasses);
            }
            //if got this far, add success command to object to be returned
            response.put("command", "get_classes_success");
        } catch (Exception e) {
            //if fails, command is a fail command
            response.put("command", "get_classes_fail");
        }
        //return response to client
        return response;
    }

}

//                switch (clientCommand){
//                    case "login":
//                        this.output.println(login(object));
//                        break;
//                    case "get_messages":
//                        this.output.println(get_messages(object));
//                        break;
//                    case "send_message":
//                        this.output.println(send_message(object));
//                        break;
//                    case "add_class":
//                        this.output.println(add_class(object));
//                        break;
//                    case "delete_class":
//                        this.output.println(delete_class(object));
//                        break;
//                    case "join_class":
//                        this.output.println(join_class(object));
//                        break;
//                    case "update_class":
//                        this.output.println(update_class(object));
//                    case "get_classes_by_size":
//                        //teacher only command
//                        this.output.println(get_classes_by_size(object));
//                        break;
//                    case "get_classes_by_name":
//                        //teacher only command
//                        this.output.println(get_classes_by_name(object));
//                        break;
//                    case "get_classes_by_date":
//                        //parent only command
//                        this.output.println(get_classes_by_date(object));
//                        break;
//                    case "get_classes":
//                        this.output.println(get_classes(object));
//                        break;
//                    default:
//                        JSONObject response = new JSONObject();
//                        response.put("command", "error");
//                        this.output.println(response);
//                }

