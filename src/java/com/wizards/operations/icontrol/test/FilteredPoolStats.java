
package com.wizards.operations.icontrol.test;

/**
 *
 * @author hildebj
 */

import com.wizards.operations.crypto.CryptoUtils;
import iControl.*;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;

public class FilteredPoolStats {
    
  public static void main( String[] args ) throws Exception {
    final String KEY_FILE = "/secure/icontrol.key";
    final String PWD_FILE = "/secure/icontrol.properties";
    final int BATCH_SIZE = 10;
    final String ENDPOINT = "10.1.0.85";
    Properties props = new Properties();
    props.load(new FileReader(PWD_FILE));
    String encryptedUser = props.getProperty("user");
    String encryptedPwd = props.getProperty("pwd");
    
    Interfaces interfaces = new Interfaces();
    String decryptedUser = CryptoUtils.decrypt(encryptedUser, 
            new File(KEY_FILE));
    String decryptedPwd = CryptoUtils.decrypt(encryptedPwd, 
            new File(KEY_FILE));
    
    interfaces.initialize(ENDPOINT, decryptedUser, decryptedPwd);
    
    // Get binding stub for pool
    LocalLBPoolBindingStub m_pool = interfaces.getLocalLBPool();
    
    // Get binding stub for pool member
    LocalLBPoolMemberBindingStub m_poolMember = interfaces.
            getLocalLBPoolMember();
    
    // Get list of pools
    String [] poolList0 = m_pool.get_list();
    String [] poolList = new String[BATCH_SIZE];
    int poolCount = 0;
    
    while (poolCount < poolList0.length) {
      for (int i = 0; i < BATCH_SIZE; i++, poolCount++) {
        if (poolCount == poolList0.length) break;
        poolList[i] = poolList0[poolCount];
      }
      
      // Get LBMethod
      LocalLBLBMethod[] lbMethodList = m_pool.get_lb_method(poolList);

      // Get Min/cur active members
      long[] minMemberList = m_pool.get_minimum_active_member(poolList);

      long[] curMemberList = m_pool.get_active_member_count(poolList);

      // Get statistics
      LocalLBPoolPoolStatistics poolStats = m_pool.get_statistics(poolList);

      // Get member statistics
      LocalLBPoolMemberMemberStatistics[] memberStatList = 
              m_poolMember.get_all_statistics(poolList);

      // print out summary
      for (int i = 0; i < poolList.length; i++) {
        System.out.println("POOL " + poolList[i] + "  LB METHEOD " + 
                lbMethodList[i] + "  MIN/CUR ACTIVE MEMBERS: " + minMemberList[i]
                + "/" + curMemberList[i]);
        for (int j = 0; j < poolStats.getStatistics()[i].getStatistics().
                length; j++) {
          CommonStatisticType type = poolStats.getStatistics()[i].
                  getStatistics()[j].getType();
          CommonULong64 value = poolStats.getStatistics()[i].getStatistics()[j].
                  getValue();
          System.out.println("|    " + type + " : " + value.getLow());
        }
        // loop over members
        LocalLBPoolMemberMemberStatisticEntry[] memberEntryList = 
                memberStatList[i].getStatistics();
        for (int j = 0; j < memberEntryList.length; j++) {
          CommonIPPortDefinition member = memberEntryList[j].getMember();
          CommonStatistic[] memberStats = memberEntryList[j].getStatistics();
          System.out.println("+-> MEMBER " + member.getAddress() + ":" + member.
                  getPort());

          for (int k = 0; k < memberStats.length; k++) {
            CommonStatisticType type = memberStats[k].getType();
            CommonULong64 value = memberStats[k].getValue();
            System.out.println("    |    " + type + " : " + value.getLow());
          }
        }
      }
    }
  }
}

