package com.example.schoolsystem.Client.GUI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class GUIClient {
    private Boolean authenticated = false;

    //give IP address of own machine
    private static final String server_ip_address = "127.0.0.1";
    private static final int server_port = 8080;

    private final Socket socket;

    //had to remove timer
    //private Timer timer = new Timer();

    private String name = null;

    private boolean loggedIn = false;

    public String role;

    private JSONArray messages = null;
    public BufferedReader input;
    public BufferedReader keyBoardInput;
    public PrintWriter clientOutput;

    public GUIClient() throws IOException {
        //sets up socket and reader/writer connections
        this.socket = new Socket(server_ip_address, server_port);
        //'input' is input from the server
        input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        //keyBoardInput is input from the keyboard
        keyBoardInput = new BufferedReader(new InputStreamReader(System.in));
        clientOutput = new PrintWriter(socket.getOutputStream(), true);
    }


    public Boolean login(JSONObject object) throws IOException {
        //send object to server
        clientOutput.println(object);
        String temp = input.readLine();
        //store server response
        JSONObject serverResponse = new JSONObject(temp);
        String serverCommand = serverResponse.getString("command");
        if (serverCommand.equalsIgnoreCase("login_success")) {
            //if successful save relevant user information
            this.messages = serverResponse.getJSONArray("messages");
            System.out.println("Login successful :)");
            this.loggedIn = true;
            this.role = serverResponse.getString("role");
            this.name = object.getString("username");
            //timer.scheduleAtFixedRate(new getMessages(),0,5000);
            //return true to gui
            return true;
        } else {
            //return false to gui if unsuccessful
            return false;
        }
    }

    public JSONObject get_messages() throws IOException {
        //send info to server
        JSONObject object = new JSONObject();
        object.put("command", "get_messages");
        object.put("username", this.name);
        clientOutput.println(object);
        //receive messages back from server
        String temp = input.readLine();
        JSONObject serverResponse = new JSONObject(temp);

        return serverResponse;
    }

    public Boolean add_class(JSONObject object) throws IOException {
        //add info to object to send to server
        object.put("teacher", this.name);
        clientOutput.println(object);
        //send object to server
        String temp = input.readLine();
        JSONObject serverResponse = new JSONObject(temp);
        //try catch on server side, check if add was successful
        String serverCommand = serverResponse.getString("command");
        if (serverCommand.equalsIgnoreCase("add_class_success")) {
            //return true to gui if successful
            return true;
        } else {
            //return false to gui if unsuccessful
            return false;
        }
    }

    public Boolean update_class(JSONObject object) throws IOException {
        System.out.println("object sending");
        //send object to server
        clientOutput.println(object);
        String temp = input.readLine();
        //receive and save response from server
        JSONObject serverResponse = new JSONObject(temp);
        System.out.println("response received");
        String serverCommand = serverResponse.getString("command");
        if (serverCommand.equalsIgnoreCase("update_success")) {
            //return true to gui if successful
            System.out.println("should return true");
            return true;
        } else {
            //return false if unsuccessful
            System.out.println("should return false");
            return false;
        }
    }

    public Boolean join_class(JSONObject object) throws IOException {
        //add logged in user's name to object to send
        object.put("name", this.name);
        //send to server
        clientOutput.println(object);
        String temp = input.readLine();
        //save received response
        JSONObject serverResponse = new JSONObject(temp);
        String serverCommand = serverResponse.getString("command");
        if (serverCommand.equalsIgnoreCase("join_success")) {
            //send success to gui
            return true;
        } else {
            //send fail to gui
            return false;
        }
    }

    public Boolean delete_class(JSONObject object) throws IOException {
        System.out.println("adding teacher");
        //add logged in user as teacher to object to send to server
        object.put("teacher", this.name);
        System.out.println("sending " + object + " to server");
        //send object to server
        clientOutput.println(object);
        //receive and save respones
        String temp = input.readLine();
        JSONObject serverResponse = new JSONObject(temp);
        String serverCommand = serverResponse.getString("command");
        System.out.println("server command is " + serverCommand);
        if (serverCommand.equalsIgnoreCase("delete_class_success")) {
            //if successful, return success to gui
            System.out.println("returning success");
            return true;
        } else {
            //if fail, return fail to gui
            System.out.println("returning fail");
            return false;
        }
    }

    public Boolean send_message(JSONObject object) throws IOException {
        //get current time for message timestamp
        String timestamp = String.valueOf((System.currentTimeMillis()));
        //add values to object
        object.put("From", name);
        //only used to check whether person can message a parent
        object.put("role", role);
        object.put("Sent", timestamp);
        //send object to server
        clientOutput.println(object);
        String temp = input.readLine();
        //save server's response
        JSONObject serverResponse = new JSONObject(temp);
        String serverCommand = serverResponse.getString("command");
        if (serverCommand.equalsIgnoreCase("send_message_success")) {
            //return true to gui
            return true;
        } else {
            //return false to gui
            return false;
        }
    }

    public JSONObject get_classes_by_size() throws IOException {
        //teacher
        //initalise object to send to server and array to send back to gui
        JSONObject object = new JSONObject();
        JSONArray returnableArray = new JSONArray();
        object.put("command", "get_classes_by_size");
        object.put("teacher", this.name);
        clientOutput.println(object);
        JSONObject returnToGUI = new JSONObject();
        String temp = input.readLine();
        JSONObject serverResponse = new JSONObject(temp);
        //this sorts classes by num of students
        //uses iterator as cannot use for to iterate through json object
        Iterator<String> keys = serverResponse.getJSONObject("classes").keys();

        Map<String, Integer> tempMap = new HashMap<>();
        while (keys.hasNext()) {
            String key = keys.next();
            //tempMap.put(key, serverResponse.getJSONObject(key).getInt("class_size"));
            tempMap.put(key, serverResponse.getJSONObject("classes").getJSONObject(key).getInt("class_size"));


        }
        List<Map.Entry<String, Integer>> sortedByValue = new ArrayList<>(tempMap.entrySet());
        sortedByValue.sort(Map.Entry.comparingByValue());
        Map<String, Integer> result = new LinkedHashMap<>();
        //for each:
        //add to array in order of class size
        for (Map.Entry<String, Integer> Entry : sortedByValue) {
            returnableArray.put(Entry.getKey());
            System.out.println(returnableArray);
            //result.put(Entry.getKey(), Entry.getValue());
        }
        //put array in object to send to gui, and send to gui
        returnToGUI.put("classes", returnableArray);
        return returnToGUI;
    }

    public JSONObject get_classes_by_name() throws IOException {
        //teacher
        //create object and adds values
        JSONObject object = new JSONObject();
        object.put("command", "get_classes_by_name");
        object.put("teacher", this.name);
        //sends object to server
        clientOutput.println(object);
        //create object to be sent back to gui
        JSONObject returnToGUI = new JSONObject();
        //read and save response from server
        String temp = input.readLine();
        JSONObject serverResponse = new JSONObject(temp);
        //get keys from server response and create array to return to gui
        Iterator<String> keys = serverResponse.getJSONObject("classes").keys();
        JSONArray returnableArray = new JSONArray();

        List<String> sortedByValue = new ArrayList<>();
        while (keys.hasNext()) {
            String key = keys.next();
            sortedByValue.add(key);
            System.out.println(sortedByValue);
        }
        Collections.sort(sortedByValue);
        //for each loop
        //add to array in order of class name
        for (String className : sortedByValue) {
            System.out.println(className);
            returnableArray.put(className);
        }

        System.out.println("The final array is" + returnableArray);
        //add sorted array to object and return to gui
        returnToGUI.put("classes", returnableArray);
        return returnToGUI;
    }

    public JSONObject get_classes_by_date() throws IOException {
        //parent only
        //create and populate object to send to server
        JSONObject object = new JSONObject();
        object.put("command", "get_classes_by_date");
        object.put("student", this.name);
        //send to server
        clientOutput.println(object);
        //save response from server
        String temp = input.readLine();
        //makes object to send back to server
        JSONObject serverResponse = new JSONObject(temp);
        Iterator<String> keys = serverResponse.getJSONObject("classes").keys();
        Map<String, Integer> tempMap = new HashMap<>();
        //for every class (key)
        //grab the end time
        while (keys.hasNext()) {
            String key = keys.next();
            //gets end time of classes
            tempMap.put(key, serverResponse.getJSONObject("classes").getJSONObject(key).getInt("end_time"));


        }
        //orders classes by end time
        List<Map.Entry<String, Integer>> sortedByValue = new ArrayList<>(tempMap.entrySet());
        sortedByValue.sort(Map.Entry.comparingByValue());
        Map<String, Integer> result = new LinkedHashMap<>();
        //add in order of end time
        for (Map.Entry<String, Integer> Entry : sortedByValue) {
            result.put(Entry.getKey(), Entry.getValue());
        }
        //add result to object to send to gui, and then send
        JSONObject backtoGUI = new JSONObject();
        backtoGUI.put("classes", result.keySet());
        return (backtoGUI);
    }

//    public JSONObject get_classes() throws IOException{
//        JSONObject object = new JSONObject();
//        JSONObject returnToGUI = new JSONObject();
//        System.out.println("both objects made");
//        object.put("command", "get_classes");
//        object.put("role", this.role);
//        object.put("name", this.name);
//        System.out.println("sending to server");
//        clientOutput.println(object);
//        String temp = input.readLine();
//        JSONObject serverResponse =  new JSONObject(temp);
//        String serverCommand = serverResponse.getString("command");
//        System.out.println("received" + serverResponse);
//        System.out.println("the server command is" + serverCommand);
//        if (serverCommand.equalsIgnoreCase("get_classes_success")){
//            System.out.println("get classes command successful");
//            System.out.println(serverResponse.get("classes"));
//            System.out.println("trying to make json array");
//            Object classes = serverResponse.get("classes");
//            System.out.println("type is " + classes.getClass());
//            System.out.println("successfully made json array");
//            returnToGUI.put("classes", classes);
//            System.out.println("returning " + returnToGUI + " in if");
//        }
//        else{
//            System.out.println("classes command unsuccessful");
//            returnToGUI.put("classes", new JSONArray());
//        }
//        System.out.println("returning " + returnToGUI);
//        return returnToGUI;
//
//    }

}
