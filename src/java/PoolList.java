/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author hildebj
 */

import iControl.Interfaces;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;

public class PoolList {
  
  public static void main( String[] args ) throws Exception {
    final String KEY_FILE = "/secure/howto.key";
    final String PWD_FILE = "/secure/howto.properties";
    Properties props = new Properties();
    props.load(new FileReader(PWD_FILE));
    String encryptedUser = props.getProperty("user");
    String encryptedPwd = props.getProperty("pwd");
    
    Interfaces interfaces = new Interfaces();
    String decryptedUser = CryptoUtils.decrypt(encryptedUser, new File(KEY_FILE));
    String decryptedPwd = CryptoUtils.decrypt(encryptedPwd, new File(KEY_FILE));
    
    interfaces.initialize("10.1.0.85", decryptedUser, decryptedPwd);
    String [] poolList = interfaces.getLocalLBPool().get_list();
    for (String pool : poolList) {
      System.out.println(pool);
    }
  }
}
