/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChatClient;

import java.io.Serializable;

/**
 *
 * @author vhg
 */
public class LoginAccount implements Serializable{
    private String username;
    private String password;

    public LoginAccount(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Object[] toObject() {
        return new Object[]{username, password};
    }
    
    
    
}
