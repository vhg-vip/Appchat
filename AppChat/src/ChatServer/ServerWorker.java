/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import ChatClient.*;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author vhg
 */
public class ServerWorker extends Thread {

    private final Socket clientSocket;
    private final Server server;
    private String login = null;
    private OutputStream outputStream;
    private HashSet<String> topicSet = new HashSet<>();
    private ArrayList User;
    String File = "src/ChatClient/UserAccount.txt";
    public String username = "";
    public String pass = "";

    public ServerWorker(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
//            e.printStackTrace();
        } catch (InterruptedException e) {
//            e.printStackTrace();
        }
    }

    private void handleClientSocket() throws IOException, InterruptedException {
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] tokens = StringUtils.split(line);
            if (tokens != null && tokens.length > 0) {
                String cmd = tokens[0];
                // logout account
                if ("logout".equals(cmd) || "quit".equalsIgnoreCase(cmd)) {
                    handleLogout();
                    break;
                } // login account
                else if ("login".equalsIgnoreCase(cmd)) {
                    handleLogin(outputStream, tokens);
                } // send direct message
                else if ("msg".equalsIgnoreCase(cmd)) {
                    String[] tokensMessage = StringUtils.split(line, null, 3);
                    handleMessage(tokensMessage);
                } // join a group chat
                else if ("join".equalsIgnoreCase(cmd)) {
                    handleJoin(tokens);
                } // leave a group chat
                else if ("leave".equalsIgnoreCase(cmd)) {
                    handleLeave(tokens);
                } else {
                    String message = "unknown " + cmd + "\n";
                    outputStream.write(message.getBytes());
                }
            }
        }

        clientSocket.close();
    }

    // Leave a group chat
    private void handleLeave(String[] tokens) {
        if (tokens.length > 1) {
            String topic = tokens[1];
            topicSet.remove(topic);
        }
    }

    // check a member of a group chat
    public boolean isMemberOfTopic(String topic) {
        return topicSet.contains(topic);
    }

    // join a group chat
    private void handleJoin(String[] tokens) {
        if (tokens.length > 1) {
            String topic = tokens[1];
            topicSet.add(topic);
        }
    }

    // format: "msg" "login" body...
    // format: "msg" "#topic" body...
    private void handleMessage(String[] tokens) throws IOException {
        String sendTo = tokens[1];  // person who is receive the message
        String body = tokens[2];    // message

        boolean isTopic = sendTo.charAt(0) == '#';

        List<ServerWorker> workerList = server.getWorkerList();
        for (ServerWorker worker : workerList) {
            if (isTopic) {
                if (worker.isMemberOfTopic(sendTo)) {
                    String outMessage = "msg " + sendTo + ": " + login + " " + body + "\n";
                    worker.send(outMessage);
                }
            } else {
                if (sendTo.equalsIgnoreCase(worker.getLogin())) {
                    String outMessage = "msg " + login + " " + body + "\n";
                    worker.send(outMessage);
                }
            }
        }

    }

    // logout account
    private void handleLogout() throws IOException {
        server.removeWorker(this);
        List<ServerWorker> workerList = server.getWorkerList();
        
        String message = "logout\n";
        outputStream.write(message.getBytes());
        
        // send other online users current user's status
        String onlineMessage = "offline " + login + "\n";
        for (ServerWorker worker : workerList) {
            if (!login.equals(worker.getLogin())) {
                worker.send(onlineMessage);
            }
        }
        clientSocket.close();
    }

    public String getLogin() {
        return login;
    }

    // login account
    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
        if (tokens.length == 3) {
            
            String login = tokens[1];
            String password = tokens[2];
            
            ReadFile rf = new ReadFile();
            User = new ArrayList<String>();
            User = rf.readFile();
            for(Object u : User){
                String a = (String) u;
                String[] account = a.split(" ");
                username = account[0];
                pass = account[1];
                if(login.equalsIgnoreCase(username) && password.equalsIgnoreCase(pass)){
//                    System.out.println(username);
//                    System.out.println(pass);
                    break;
                }
            }

            if ((login.equals(username) && password.equals(pass))) {
                String message = "ok login\n";
                outputStream.write(message.getBytes());
                this.login = login;
                System.out.println("User logged in successfully: " + login);

                List<ServerWorker> workerList = server.getWorkerList();

                // send current user all other online user
                for (ServerWorker worker : workerList) {
                    if (worker.getLogin() != null) {
                        if (!login.equals(worker.getLogin())) {
                            String message2 = "online " + worker.getLogin() + "\n";
                            send(message2);
                        }
                    }
                }

                // send other online users current user's status
                String onlineMessage = "online " + login + "\n";
                for (ServerWorker worker : workerList) {
                    if (!login.equals(worker.getLogin())) {
                        worker.send(onlineMessage);
                    }
                }
            } else {
                String message = "error login\n";
                outputStream.write(message.getBytes());
                System.err.println("Login failed for " + login);
            }
        }
    }

    private void send(String message) throws IOException {
        if (login != null) {
            outputStream.write(message.getBytes());
        }
    }
}
