/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author hildebj
 */

import iControl.CommonIPPortDefinition;
import iControl.CommonStatistic;
import iControl.CommonStatisticType;
import iControl.CommonULong64;
import iControl.Interfaces;
import iControl.LocalLBLBMethod;
import iControl.LocalLBPoolBindingStub;
import iControl.LocalLBPoolMemberBindingStub;
import iControl.LocalLBPoolMemberMemberStatisticEntry;
import iControl.LocalLBPoolMemberMemberStatistics;
import iControl.LocalLBPoolPoolStatistics;
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
    String decryptedUser = CryptoUtils.decrypt(encryptedUser, 
            new File(KEY_FILE));
    String decryptedPwd = CryptoUtils.decrypt(encryptedPwd, 
            new File(KEY_FILE));
    
    interfaces.initialize("10.1.0.85", decryptedUser, decryptedPwd);
    
    // Get binding stub for pool
    LocalLBPoolBindingStub m_pool = interfaces.getLocalLBPool();
    
    // Get binding stub for pool member
    LocalLBPoolMemberBindingStub m_poolMember = interfaces.
            getLocalLBPoolMember();
    
    // Get list of pools
    String [] poolList0 = m_pool.get_list();
    String [] poolList = new String[10];
    int poolCount = 0;
    
    while (poolCount < poolList0.length) {
      for (int i = 0; i < 10; i++, poolCount++) {
        if (poolCount < poolList0.length)
          poolList[i] = poolList0[poolCount];
      }
  //    for (String pool : poolList) {
  //      System.out.println(pool);
  //    }

      // Get LBMethod
      LocalLBLBMethod[] lbMethodList = null;
      lbMethodList = m_pool.get_lb_method(poolList);

      // Get Min/cur active members
      long[] minMemberList = null;
      minMemberList = m_pool.get_minimum_active_member(poolList);

      long[] curMemberList = null;
      curMemberList = m_pool.get_active_member_count(poolList);

      // Get statistics
      LocalLBPoolPoolStatistics poolStats = null;
      poolStats = m_pool.get_statistics(poolList);

      // Get member statistics
      LocalLBPoolMemberMemberStatistics[] memberStatList = null;
      memberStatList = m_poolMember.get_all_statistics(poolList);

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
