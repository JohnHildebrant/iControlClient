/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author hildebj
 */
import java.io.File;
import java.io.FileWriter;
//import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
//import java.util.Properties;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec; 

public class CryptoUtils {
  
  public static final String AES = "AES";
  
  /**
   * encrypt a value and generate a keyfile 
   * if the keyfile is not found then a new one is created
   * @throws GeneralSecurityException 
   * @throws IOException 
   */
  public static String encrypt(String value, File keyFile)
  throws GeneralSecurityException, IOException 
  {
    if (!keyFile.exists()) {
      KeyGenerator keyGen = KeyGenerator.getInstance(CryptoUtils.AES);
      keyGen.init(128);
      SecretKey sk = keyGen.generateKey();
      FileWriter fw = new FileWriter(keyFile);
      fw.write(byteArrayToHexString(sk.getEncoded()));
      fw.flush();
      fw.close();
    }
    
   SecretKeySpec sks = getSecretKeySpec(keyFile);
   Cipher cipher = Cipher.getInstance(CryptoUtils.AES);
   cipher.init(Cipher.ENCRYPT_MODE, sks, cipher.getParameters());
   byte[] encrypted = cipher.doFinal(value.getBytes());
   return byteArrayToHexString(encrypted);
  }
  
  /**
   * decrypt a value  
   * @throws GeneralSecurityException 
   * @throws IOException 
   */
  public static String decrypt(String message, File keyFile) 
  throws GeneralSecurityException, IOException 
  {
   SecretKeySpec sks = getSecretKeySpec(keyFile);
   Cipher cipher = Cipher.getInstance(CryptoUtils.AES);
   cipher.init(Cipher.DECRYPT_MODE, sks);
   byte[] decrypted = cipher.doFinal(hexStringToByteArray(message));
   return new String(decrypted);
  }
  
  
  
  private static SecretKeySpec getSecretKeySpec(File keyFile) 
  throws NoSuchAlgorithmException, IOException 
  {
    byte [] key = readKeyFile(keyFile);
    SecretKeySpec sks = new SecretKeySpec(key, CryptoUtils.AES);
    return sks;
  }

  private static byte [] readKeyFile(File keyFile) 
  throws FileNotFoundException 
  {
    Scanner scanner = 
      new Scanner(keyFile).useDelimiter("\\Z");
    String keyValue = scanner.next();
    scanner.close();
    return hexStringToByteArray(keyValue);
  }

  
  private static String byteArrayToHexString(byte[] b){
    StringBuilder sb = new StringBuilder(b.length * 2);
    for (int i = 0; i < b.length; i++){
      int v = b[i] & 0xff;
      if (v < 16) {
        sb.append('0');
      }
      sb.append(Integer.toHexString(v));
    }
    return sb.toString().toUpperCase();
}

  private static byte[] hexStringToByteArray(String s) {
    byte[] b = new byte[s.length() / 2];
    for (int i = 0; i < b.length; i++){
      int index = i * 2;
      int v = Integer.parseInt(s.substring(index, index + 2), 16);
      b[i] = (byte)v;
    }
    return b;
}
  
//  public static void main(String[] args) throws Exception {
//    final String KEY_FILE = "/secure/howto.key";
//    final String PWD_FILE = "/secure/howto.properties";
//    
//    String clearPwd = "xxxxxxxxx";
//    String clearUser = "xxxxxxxxx";
//    
//    Properties p1 = new Properties();
//    
//    String encryptedUser = CryptoUtils.encrypt(clearUser, new File(KEY_FILE));
//    p1.put("user", encryptedUser);
//    String encryptedPwd = CryptoUtils.encrypt(clearPwd, new File(KEY_FILE));
//    p1.put("pwd", encryptedPwd);
//    p1.store(new FileWriter(PWD_FILE), "");
    
    // ==================
//    Properties p2 = new Properties();
    
//    p2.load(new FileReader(PWD_FILE));
//    encryptedPwd = p2.getProperty("pwd");
//    System.out.println(encryptedPwd);
//    System.out.println
//        (CryptoUtils.decrypt(encryptedPwd, new File(KEY_FILE)));
//        encryptedPwd = p2.getProperty("pwd");
//    System.out.println(encryptedUser);
//    System.out.println
//        (CryptoUtils.decrypt(encryptedUser, new File(KEY_FILE)));
//  }
}