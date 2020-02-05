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
public class SignupAccount implements Serializable{
    private String usernameS;
    private String passwordS;

    public SignupAccount(String usernameS, String passwordS) {
        this.usernameS = usernameS;
        this.passwordS = passwordS;
    }

    public void setUsernameS(String usernameS) {
        this.usernameS = usernameS;
    }

    public void setPasswordS(String passwordS) {
        this.passwordS = passwordS;
    }

    public String getUsername() {
        return usernameS;
    }

    public String getPassword() {
        return passwordS;
    }
    public Object[] toObject(){
        return new Object[]{usernameS, passwordS};
    }
    
}
