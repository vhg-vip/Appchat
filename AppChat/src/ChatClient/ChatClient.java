/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChatClient;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import ChatServer.*;

/**
 *
 * @author vhg
 */
public class ChatClient {

    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private OutputStream serverOut;
    private InputStream serverIn;
    private BufferedReader bufferedIn;
    private BufferedWriter bufferedOut;

    private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
    private ArrayList<MessageListener> messageListeners = new ArrayList<>();
    private ArrayList userLogin = new ArrayList();
    String File = "src/ChatClient/UserAccount.txt";

    public ChatClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    // search account in file to login
    public void search(String login, String password) {
        userLogin = new ArrayList();
        for (Object user : userLogin) {
            SignupAccount a = (SignupAccount) user;
            if (login.equalsIgnoreCase(a.getUsername()) && password.equalsIgnoreCase(a.getPassword())) {
                login = a.getUsername();
                password = a.getPassword();
                break;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ChatClient client = new ChatClient("localhost", 4444);
        client.addUserStatusListener(new UserStatusListener() {
            @Override
            public void online(String login) {
                System.out.println("ONLINE: " + login);
            }

            @Override
            public void offline(String login) {
                System.out.println("OFFLINE: " + login);
            }
        });

        client.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(String fromLogin, String msgBody) {
                System.out.println("You got a message from " + fromLogin + "==> " + msgBody);
            }
        });

        if (!client.connect()) {
            System.err.println("Connect failed");
        } else {
            System.out.println("Connect successful");
            if (client.login("guest", "guest")) {
                System.out.println("Login successful");

            } else {
                System.err.println("Login failed");
            }

            client.logout();
        }
    }

    public void message(String sendTo, String msgBody) throws IOException {
        String cmd = "msg " + sendTo + " " + msgBody + "\n";
        serverOut.write(cmd.getBytes());
    }

    // logout account
    public void logout() throws Exception {
        String cmd = "logout\n";
        serverOut.write(cmd.getBytes());
        
        String response = bufferedIn.readLine();
        System.out.println("Response Line: " + response);
        
    }

    // check login
    public boolean login(String login, String password) throws IOException {
        String cmd = "login " + login + " " + password + "\n";
        serverOut.write(cmd.getBytes());
        
        search(login, password);
        String response = bufferedIn.readLine();
        System.out.println("Response Line: " + response);

        if ("ok login".equalsIgnoreCase(response)) {
            startMessageReader();
            return true;
        } else {
            return false;
        }
    }

    private void startMessageReader() {
        Thread t = new Thread() {
            @Override
            public void run() {
                readMessageLoop();
            }
        };
        t.start();
    }

    // check online or offline
    private void readMessageLoop() {
        try {
            String line;
            while ((line = bufferedIn.readLine()) != null) {
                String[] tokens = StringUtils.split(line);
                if (tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];
                    if ("online".equalsIgnoreCase(cmd)) {
                        handleOnline(tokens);
                    } else if ("offline".equalsIgnoreCase(cmd)) {
                        handleOffline(tokens);
                    } else if ("msg".equalsIgnoreCase(cmd)) {
                        String[] tokensMessage = StringUtils.split(line, null, 3);
                        handleMessage(tokensMessage);
                    } 
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleMessage(String[] tokensMessage) {
        String login = tokensMessage[1];
        String msgBody = tokensMessage[2];

        for (MessageListener listener : messageListeners) {
            listener.onMessage(login, msgBody);
        }
    }

    private void handleOffline(String[] tokens) {
        String login = tokens[1];
        for (UserStatusListener listener : userStatusListeners) {
            listener.offline(login);
        }
    }

    private void handleOnline(String[] tokens) {
        String login = tokens[1];
        for (UserStatusListener listener : userStatusListeners) {
            listener.online(login);
        }
    }

    // connect to server
    public boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            System.out.println("Client port is: " + socket.getLocalPort());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // add online status
    public void addUserStatusListener(UserStatusListener listener) {
        userStatusListeners.add(listener);
    }

    // remove online status
    public void removeUserStatusListener(UserStatusListener listener) {
        userStatusListeners.remove(listener);
    }

    public void addMessageListener(MessageListener listener) {
        messageListeners.add(listener);
    }

    public void removeMessageListener(MessageListener listener) {
        messageListeners.remove(listener);
    }
}
