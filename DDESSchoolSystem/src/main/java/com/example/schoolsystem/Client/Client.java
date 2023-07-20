package com.example.schoolsystem.Client;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class Client {
    //this version of the client is mostly redundant
    //it only exists to demo the progression of the client-side
    //as well as to show functionality that didn't
    //make it to the gui implementation
    //for a more commented, well-structured version
    //please see GUIClient
    private Boolean authenticated = false;

    //give IP address of own machine
    private static final String server_ip_address = "127.0.0.1";
    private static final int server_port = 8080;

    private final Socket socket;

    //had to remove timer
    //private Timer timer = new Timer();

    private String name = null;

    private boolean loggedIn = false;

    private String role;

    private JSONArray messages = null;


    private List<String> helpCommands = new ArrayList<>() {{
        add("login");
        add("help");
        add("send_message");
        add("exit");
    }};

    public BufferedReader input;
    public BufferedReader keyBoardInput;
    public PrintWriter clientOutput;

    //had to purge timertask
    //race condition
    private class getMessages {
        public void run(){
            try{
                JSONObject message = new JSONObject();
                message.put("command", "get_messages");
                message.put("username", name);
                clientOutput.println(message);
                String temp = input.readLine();
                JSONObject serverResponse = new JSONObject(temp);
                String serverCommand = serverResponse.getString("command");
                if(serverCommand.equalsIgnoreCase("get_messages_success")){
                    JSONArray tempMessages = serverResponse.getJSONArray("messages");
                    messages = tempMessages;
                }
            }
            catch(Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Client() throws IOException{
        this.socket = new Socket(server_ip_address, server_port);
        //'input' is input from the server
        input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        //keyBoardInput is input from the keyboard
        keyBoardInput = new BufferedReader(new InputStreamReader(System.in));
        clientOutput = new PrintWriter(socket.getOutputStream(), true);
    }

    public void createClient(){
        try {
            boolean running = true;

            while (running) {
                System.out.print(">>> ");
                String command = keyBoardInput.readLine();

                JSONObject object = new JSONObject();
                //object.put("command", "sendMessage");
                if (command.equalsIgnoreCase("login")) {
                    object.put("command", "login");
                    System.out.println(object);
                    System.out.print("Please enter username: ");
                    String username = keyBoardInput.readLine();
                    object.put("username", username);
                    System.out.println(object);
                    System.out.print("Please enter password: ");
                    String password = keyBoardInput.readLine();
                    object.put("password", password);
                    System.out.println(object);
                    this.name = username;
                } else if (command.equalsIgnoreCase("help")) {
                    for (String x : this.helpCommands){
                        System.out.println("Command " + x);
                    }
                    continue;
                }
                else if (command.equalsIgnoreCase("send_message")){
                    if (!loggedIn){
                        System.out.println("You need to be logged in to do this!");
                        continue;
                    }
                    else {
                        object.put("command", "send_message");
                        System.out.println("Who is the message to?");
                        String messageTo = keyBoardInput.readLine();
                        System.out.println("Please type your message:");
                        String messageText = keyBoardInput.readLine();
                        String timestamp = String.valueOf((System.currentTimeMillis()));
                        String from = this.name;
                        System.out.println("What type of message would you like to send? D/B?");
                        String messageType = keyBoardInput.readLine();
                        if(messageType.equalsIgnoreCase("B") && !this.role.equalsIgnoreCase("teacher")){
                            System.out.println("Only teachers can send broadcast messages");
                            continue;
                        }
                        object.put("To", messageTo);
                        object.put("Message_Text", messageText);
                        object.put("From", from);
                        object.put("Sent", timestamp);
                        object.put("Type", messageType.equalsIgnoreCase("B") ? "Broadcast_Message" : "Direct_message");
                        //need if for checking if user can choose message type


                    }
                }
                else if(command.equalsIgnoreCase("add_class")){
                    if(!role.equalsIgnoreCase("teacher")){
                        System.out.println(("You have to be a teacher to do that"));
                        continue;
                    }
                    System.out.println("Please enter the class' name");
                    String className = keyBoardInput.readLine();
                    System.out.println("Please enter the class' code");
                    String Code = keyBoardInput.readLine();
                    object.put("command", "add_class");
                    object.put("code", Code);
                    object.put("name", className);
                    object.put("teacher", this.name);
                }
                else if(command.equalsIgnoreCase("get_messages")){
                    object.put("command", "get_messages");
                    object.put("username", this.name);
                }
                else if (command.equalsIgnoreCase("join_class")){
                    System.out.println("Enter the code of the class you would like to join:");
                    String classCode = keyBoardInput.readLine();
                    object.put("command", "join_class");
                    object.put("code", classCode);
                    object.put("name", this.name);
                }
                else if(command.equalsIgnoreCase("get_classes_by_size")){
                    object.put("command", "get_classes_by_size");
                    object.put("teacher", this.name);

                }
                else if(command.equalsIgnoreCase("get_classes_by_name")){
                    object.put("command", "get_classes_by_name");
                    object.put("teacher", this.name);
                }
                else if(command.equalsIgnoreCase("get_classes_by_date")){
                    object.put("command", "get_classes_by_date");
                    object.put("student", this.name);
                    System.out.println("making get class command");
                }
//                else if(command.equalsIgnoreCase("get_classes")){
//                    object.put("command", "get_classes");
//                }
                else if(command.equalsIgnoreCase("get_classes")){
                    object.put("command", "get_classes");
                    object.put("role", this.role);
                    object.put("name", this.name);
                    System.out.println(object);
                }
                else {
                    System.out.println("Please refer to help command :)");
                    continue;
                }
                System.out.println("check for client output");
                clientOutput.println(object);
                String temp = input.readLine();
                JSONObject serverResponse = null;
                String serverCommand = null;

                try {
                    serverResponse = new JSONObject(temp);
                    serverCommand = serverResponse.getString("command");
                }
                catch(Exception e) {
                    System.out.println(e);
                    continue;
                }

                if(serverCommand == null || serverResponse == null) {
                    System.out.println("server command or server response was null!");
                    continue;
                }
                if (serverCommand.equalsIgnoreCase("login_success")) {
                    JSONArray tempMessages = serverResponse.getJSONArray("messages");
                    this.messages = tempMessages;
                    System.out.println("Login successful :)");
                    this.loggedIn = true;
                    this.role = serverResponse.getString("role");
                    //timer.scheduleAtFixedRate(new getMessages(),0,5000);
                }
                else if(serverCommand.equalsIgnoreCase("get_classes_success_by_size")){
                    //this sorts classes by num of students
                    Iterator<String> keys = serverResponse.getJSONObject("classes").keys();

                    Map<String, Integer> tempMap = new HashMap<>();
                    while(keys.hasNext()){
                        String key = keys.next();
                        //tempMap.put(key, serverResponse.getJSONObject(key).getInt("class_size"));
                        tempMap.put(key, serverResponse.getJSONObject("classes").getJSONObject(key).getInt("class_size"));


                    }
                    List<Map.Entry<String, Integer>> sortedByValue = new ArrayList<>(tempMap.entrySet());
                    sortedByValue.sort(Map.Entry.comparingByValue());
                    Map<String, Integer> result = new LinkedHashMap<>();
                    for(Map.Entry<String, Integer> Entry:sortedByValue){
                        result.put(Entry.getKey(), Entry.getValue());
                    }
                    System.out.println(result.keySet());
                    //System.out.println(serverResponse.getJSONObject("classes"));
//                    for(String key : result.keySet()){
//                        System.out.println(serverResponse.getJSONObject("classes"));
//                    }
                }
                else if(serverCommand.equalsIgnoreCase("get_classes_success_by_date")){
                    //this sorts classes by date
                    System.out.println("received get class command");
                    Iterator<String> keys = serverResponse.getJSONObject("classes").keys();

                    Map<String, Integer> tempMap = new HashMap<>();
                    while(keys.hasNext()){
                        String key = keys.next();
                        //tempMap.put(key, serverResponse.getJSONObject(key).getInt("class_size"));
                        tempMap.put(key, serverResponse.getJSONObject("classes").getJSONObject(key).getInt("end_time"));


                    }
                    List<Map.Entry<String, Integer>> sortedByValue = new ArrayList<>(tempMap.entrySet());
                    sortedByValue.sort(Map.Entry.comparingByValue());
                    Map<String, Integer> result = new LinkedHashMap<>();
                    for(Map.Entry<String, Integer> Entry:sortedByValue){
                        result.put(Entry.getKey(), Entry.getValue());
                    }
                    System.out.println("Hello");
                    System.out.println(result.keySet());
                    System.out.println(serverResponse.getJSONObject("classes"));
//                    for(String key : result.keySet()){
//                        System.out.println(serverResponse.getJSONObject("classes"));
//                    }
                }
                else if(serverCommand.equalsIgnoreCase("get_messages_success")){
                    JSONArray tempMessages = serverResponse.getJSONArray("messages");
                    this.messages = tempMessages;
                    System.out.println(tempMessages);

                }
                else if(serverCommand.equalsIgnoreCase("get_classes_success_by_class_name")){
                    //this sorts classes by name
//                    List sortedKeys = new ArrayList(serverResponse.getJSONObject("classes").keySet());
//                    Collections.sort(sortedKeys);
                    Iterator<String> keys = serverResponse.getJSONObject("classes").keys();

                    List<String> sortedByValue = new ArrayList<>();
                    while(keys.hasNext()){
                        String key = keys.next();
                        sortedByValue.add(key);
                    }
                    Collections.sort(sortedByValue);
                    for(String className : sortedByValue){
                        System.out.println(className);
                    }
                }
                else if (serverCommand.equalsIgnoreCase("get_classes_success")){
                    System.out.println(serverResponse.getJSONObject("classes"));
                }
                if (command.equalsIgnoreCase("Exit")) {
                    socket.close();
                    running = false;
                }
                //what is sent to the server
            }
            System.exit(0);
        } catch  (Exception e) {
            System.out.println("Something went wrong!");
            e.printStackTrace();
        }
    }
}
