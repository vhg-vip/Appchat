/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChatClient;

import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 *
 * @author vhg
 */

// read file
public class ReadFile {

    public ArrayList<String> readFile() {
        ArrayList<String> list = new ArrayList<>();
        try {
            File f = new File("src/ChatClient/UserAccount.txt");
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                list.add(line);
//                System.out.println(line);
            }
            fr.close();
            br.close();
        } catch (Exception ex) {
            System.out.println("Loi doc file: " + ex);
        }
        return list;
    }

    // write flie
    public void writeFile(SignupAccount s) {
        try {
            File f = new File("src/ChatClient/UserAccount.txt");
            FileWriter fw = new FileWriter(f, true);
            fw.write(s.getUsername() + " ");
            fw.write(s.getPassword() + "\n");
            fw.close();
        } catch (IOException ex) {
            System.out.println("Loi ghi file: " + ex);
        }
    }
    
    public static void main(String[] args) {
        ReadFile rf = new ReadFile();
//        rf.writeFile();
//        rf.readFile();
//                
    }
}
