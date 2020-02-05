/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChatServer;

/**
 *
 * @author vhg
 */
public class ServerMain {
    public static void main(String[] args) {
        int port = 4444;
        Server server = new Server(port);
        server.start();
    }
}
