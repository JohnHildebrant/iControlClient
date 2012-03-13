
package com.wizards.operations.icontrol.test;

import com.wizards.operations.crypto.CryptoUtils;
import iControl.*;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 *
 * @author hildebj
 */
public class PoolMemberControl {

  public Interfaces interfaces = new Interfaces();
  
  public void Run(String [] args) throws Exception
  {
    final String KEY_FILE = "/secure/icontrol.key";
    final String PWD_FILE = "/secure/icontrol.properties";
    //final String ENDPOINT = "10.1.32.10";
    final String ENDPOINT = "10.9.14.10";
    Properties props = new Properties();
    props.load(new FileReader(PWD_FILE));
    String encryptedUser = props.getProperty("user");
    String encryptedPwd = props.getProperty("pwd");
    
    String decryptedUser = CryptoUtils.decrypt(encryptedUser, 
            new File(KEY_FILE));
    String decryptedPwd = CryptoUtils.decrypt(encryptedPwd, 
            new File(KEY_FILE));
     
    boolean init = interfaces.initialize(ENDPOINT, decryptedUser, decryptedPwd);
    if ( init )
    {
      if ( args.length == 0)
      {
        getPoolList();
      }
      else if ( args.length == 1)
      {
        getPoolMembers(args[0]);
      }
      else if ( args.length == 2)
      {
        getPoolMemberStatus(args[0], args[1]);
      }
      else if (args.length == 3)
      {
        if ( args[2].equals("enable"))
        {
          enablePoolMember(args[0],args[1]);
        }
        else
        {
          disablePoolMember(args[0],args[1]);
        }
      }
    }
  }
  
  public void getPoolList() throws Exception
  {
    String [] poolList = interfaces.getLocalLBPool().get_list();
    System.out.println("Available Pools");
    for (String pool : poolList) {
      System.out.println("  " + pool);
    }
  }
  
  public void getPoolMembers(String pool) throws Exception
  {
    String [] poolList = { pool };
    
    CommonIPPortDefinition [][] memberDefAofA = 
      interfaces.getLocalLBPool().get_member(poolList);
    
    CommonIPPortDefinition [] memberDefA = memberDefAofA[0];
    
    System.out.println("Pool Member for pool '" + pool + "'");
    
    for(int i=0; i<memberDefA.length; i++)
    {
      String addr = memberDefA[i].getAddress();
      long port = memberDefA[i].getPort();
      System.out.println("  " + addr + ":" + port);
    }
  }
  
  public void getPoolMemberStatus(String pool, String member) throws Exception
  {
    String [] poolList = { pool };
    boolean found = false;
    
    LocalLBPoolMemberMemberObjectStatus [][] objStatusAofA = 
      interfaces.getLocalLBPoolMember().get_object_status(poolList);
    
    LocalLBPoolMemberMemberObjectStatus [] objStatusA = objStatusAofA[0];
    for(int i=0; i<objStatusA.length; i++)
    {
      String a2 = objStatusA[i].getMember().getAddress();
      Long p2 = objStatusA[i].getMember().getPort();
      String m2 = a2 + ":" + p2;
      if ( member.equals(m2) )
      {
        LocalLBObjectStatus objStatus = 
                objStatusA[i].getObject_status();
        LocalLBAvailabilityStatus availability = 
                objStatus.getAvailability_status();
        LocalLBEnabledStatus enabled = objStatus.getEnabled_status();
        String description = objStatus.getStatus_description();
        
        System.out.println("Pool " + pool + ", Member " + member + " status:");
        System.out.println("  Availability : " + availability);
        System.out.println("  Enabled      : " + enabled);
        System.out.println("  Description  : " + description);
        
        found = true;
      }
    }
    if ( ! found )
    {
      System.out.println("Member " + member + " could not be found in pool " 
              + pool);
    }
  }
  
  public void enablePoolMember(String pool, String member) throws Exception
  {
    String [] pool_list = { pool };
    
    String [] memberTokens = member.split(":");
    String memberAddr = memberTokens[0];
    String memberPort = memberTokens[1];
    
    LocalLBPoolMemberMemberMonitorState memberMonitorState = 
            new LocalLBPoolMemberMemberMonitorState();
    memberMonitorState.setMember(new CommonIPPortDefinition());
    memberMonitorState.getMember().setAddress(memberAddr);
    memberMonitorState.getMember().setPort(Long.parseLong(memberPort));
    memberMonitorState.setMonitor_state(CommonEnabledState.STATE_ENABLED);
    
    LocalLBPoolMemberMemberMonitorState [] monStateA = { memberMonitorState };
    LocalLBPoolMemberMemberMonitorState [][] monStateAofA = { monStateA };
    
    System.out.println("Setting Monitor State to Enabled");
    interfaces.getLocalLBPoolMember().set_monitor_state(pool_list, monStateAofA);
    
    LocalLBPoolMemberMemberSessionState memberSessionState = 
            new LocalLBPoolMemberMemberSessionState();
    memberSessionState.setMember(new CommonIPPortDefinition());
    memberSessionState.getMember().setAddress(memberAddr);
    memberSessionState.getMember().setPort(Long.parseLong(memberPort));
    memberSessionState.setSession_state(CommonEnabledState.STATE_ENABLED);
    
    LocalLBPoolMemberMemberSessionState [] sessionStateA = { memberSessionState };
    LocalLBPoolMemberMemberSessionState [][] sessionStateAofA = { sessionStateA };
    
    System.out.println("Setting Session State to Enabled");
    interfaces.getLocalLBPoolMember().
            set_session_enabled_state(pool_list, sessionStateAofA);
    
    getPoolMemberStatus(pool, member);
  }
  
  public void disablePoolMember(String pool, String member) throws Exception
  {
    String [] pool_list = { pool };
    
    String [] memberTokens = member.split(":");
    String memberAddr = memberTokens[0];
    String memberPort = memberTokens[1];
    
    System.out.println("Disabling Session Enabled State...");
    
    LocalLBPoolMemberMemberSessionState memberSessionState = 
            new LocalLBPoolMemberMemberSessionState();
    memberSessionState.setMember(new CommonIPPortDefinition());
    memberSessionState.getMember().setAddress(memberAddr);
    memberSessionState.getMember().setPort(Long.parseLong(memberPort));
    memberSessionState.setSession_state(CommonEnabledState.STATE_DISABLED);
    
    LocalLBPoolMemberMemberSessionState [] sessionStateA = { memberSessionState };
    LocalLBPoolMemberMemberSessionState [][] sessionStateAofA = { sessionStateA };
    
    interfaces.getLocalLBPoolMember().
            set_session_enabled_state(pool_list, sessionStateAofA);
    
    System.out.println("Waiting for current connections to drop to zero...");
    
    CommonIPPortDefinition memberDef = new CommonIPPortDefinition();
    memberDef.setAddress(memberAddr);
    memberDef.setPort(Long.parseLong(memberPort));
    
    CommonIPPortDefinition [] memberDefA = { memberDef };
    CommonIPPortDefinition [][] memberDefAofA = { memberDefA };
    
    long cur_connections = 1;
    
    while ( cur_connections > 0 )
    {
      LocalLBPoolMemberMemberStatistics [] memberStatsA = 
        interfaces.getLocalLBPoolMember().get_statistics(pool_list, memberDefAofA);
      
      LocalLBPoolMemberMemberStatistics memberStats = memberStatsA[0];
      
      LocalLBPoolMemberMemberStatisticEntry [] statsEntryA = 
        memberStats.getStatistics();
      LocalLBPoolMemberMemberStatisticEntry  statsEntry = statsEntryA[0];
      
      CommonStatistic [] statsA = statsEntry.getStatistics();
      
      for(int i=0; i<statsA.length; i++)
      {
        CommonStatisticType type = statsA[i].getType();
        CommonULong64 value64 = statsA[i].getValue();

        if ( type == CommonStatisticType.STATISTIC_SERVER_SIDE_CURRENT_CONNECTIONS )
        {
          cur_connections = value64.getLow();
          System.out.println("Current Connections: " + cur_connections);
        }
      }
      Thread.currentThread();
      Thread.sleep(1000);
    }
    
    System.out.println("Disabling Monitor State...");
    
    LocalLBPoolMemberMemberMonitorState memberMonitorState = 
      new LocalLBPoolMemberMemberMonitorState();
    memberMonitorState.setMember(new CommonIPPortDefinition());
    memberMonitorState.getMember().setAddress(memberAddr);
    memberMonitorState.getMember().setPort(Long.parseLong(memberPort));
    memberMonitorState.setMonitor_state(CommonEnabledState.STATE_DISABLED);
    
    LocalLBPoolMemberMemberMonitorState [] monStateA = { memberMonitorState };
    LocalLBPoolMemberMemberMonitorState [][] monStateAofA = { monStateA };
    
    interfaces.getLocalLBPoolMember().set_monitor_state(pool_list, monStateAofA);
    
    getPoolMemberStatus(pool, member);
  }
  
  public static String[] stringToArgs( String s ) {
    StringTokenizer token = new StringTokenizer( s," " );
    String[] progArgs = new String[ token.countTokens( ) ];
    for ( int i = 0; token.hasMoreTokens( ); i++ ) {
        progArgs[i] = token.nextToken( );
    }
    return progArgs;
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    try
    {
      PoolMemberControl prog = new PoolMemberControl();
      //prog.Run(args);
      //String s = "OPISENDINTAPOOL01_45063 10.1.33.96:45063 enable";
      //String s = "POOL_neverwinterqa.wizards.com_80 10.1.16.155:80 enable";
      //String s = "WWWDNDPOOL01 10.9.16.19:80 enable";
      // String s = "WWWDNDPOOL01 10.9.16.19:80";
      String s = "AccountsPool2";
      prog.Run(stringToArgs(s));
      s = "POOL_opis-prime.onlinegaming.wizards.com_443";
      prog.Run(stringToArgs(s));
      s = "WWWPWPPOOL01";
      prog.Run(stringToArgs(s));
      s = "POOL_pwp.wizards.com_akamai_80";
      prog.Run(stringToArgs(s));
      s = "WWWDNDPOOL01";
      prog.Run(stringToArgs(s));
      //prog.Run(args);
    }
    catch(Exception ex)
    {
      ex.printStackTrace(System.out);
    }
  }
}
