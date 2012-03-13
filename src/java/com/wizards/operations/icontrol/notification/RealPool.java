/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wizards.operations.icontrol.notification;

import com.wizards.operations.crypto.CryptoUtils;
import iControl.*;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author hildebj
 */
public class RealPool {
  private Interfaces interfaces = new Interfaces();
  private List<RealMember> realMembers;
  
  public class RealMember {
    private String name;
    private long connections;
    private String availability;
    private String enabled;
    private String description;
    
    RealMember(String name) {
      this.name = name;
    }

    /**
     * @param connections the connections to set
     */
    public void setConnections(Long connections) {
      this.connections = connections;
    }

    /**
     * @param availability the availability to set
     */
    public void setAvailability(String availability) {
      this.availability = availability;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(String enabled) {
      this.enabled = enabled;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
      this.description = description;
    }

    /**
     * @return the connections
     */
    public long getConnections() {
      return connections;
    }

    /**
     * @return the availability
     */
    public String getAvailability() {
      return availability;
    }

    /**
     * @return the enabled
     */
    public String getEnabled() {
      return enabled;
    }

    /**
     * @return the description
     */
    public String getDescription() {
      return description;
    }

    /**
     * @return the name
     */
    public String getName() {
      return name;
    }
  }
  
  public RealPool() throws Exception {
    final String KEY_FILE = "/secure/icontrol.key";
    final String PWD_FILE = "/secure/icontrol.properties";
    //final String ENDPOINT = "10.1.32.10";
    final String ENDPOINT = "10.9.14.10";
    Properties props = new Properties();
    props.load(new FileReader(PWD_FILE));
    final String encryptedUser = props.getProperty("user");
    final String encryptedPwd = props.getProperty("pwd");
    final String decryptedUser = CryptoUtils.decrypt(encryptedUser, 
            new File(KEY_FILE));
    final String decryptedPwd = CryptoUtils.decrypt(encryptedPwd, 
            new File(KEY_FILE));
    interfaces.initialize(ENDPOINT, decryptedUser, decryptedPwd);
  }
  
  public void getPoolList() throws Exception
  {
    String [] poolList = interfaces.getLocalLBPool().get_list();
    System.out.println("Available Pools");
    for (String pool : poolList) {
      System.out.println("  " + pool);
    }
  }
  
  public String[] getPoolMembers(String pool) throws Exception {
    String [] poolList = { pool };
    
    CommonIPPortDefinition [][] memberDefAofA = 
      interfaces.getLocalLBPool().get_member(poolList);
    
    CommonIPPortDefinition [] memberDefA = memberDefAofA[0];
    
    String[] members = new String[memberDefA.length];
    for(int i = 0; i < memberDefA.length; i++)
    {
      String addr = memberDefA[i].getAddress();
      long port = memberDefA[i].getPort();
      members[i] = addr + ":" + port;
      //System.out.println("  " + addr + ":" + port);
    }
    return members;
  }
  
  public List<RealMember> getPoolMembersStatus(String pool) throws Exception {
    String [] poolList = { pool };
    String [] members = getPoolMembers(pool);
    
    realMembers = new ArrayList<RealMember>();
    for (String member : members) {
      RealMember realMember = new RealMember(member);
      realMembers.add(realMember);
    
      LocalLBPoolMemberMemberObjectStatus [][] objStatusAofA = 
      interfaces.getLocalLBPoolMember().get_object_status(poolList);
    
      LocalLBPoolMemberMemberObjectStatus [] objStatusA = objStatusAofA[0];
      for (int i = 0; i < objStatusA.length; i++) {
        String a2 = objStatusA[i].getMember().getAddress();
        Long p2 = objStatusA[i].getMember().getPort();
        String m2 = a2 + ":" + p2;
        if ( member.equals(m2) ) {
          LocalLBObjectStatus objStatus = 
                objStatusA[i].getObject_status();
          LocalLBAvailabilityStatus availability = 
                objStatus.getAvailability_status();
          LocalLBEnabledStatus enabled = objStatus.getEnabled_status();
          String description = objStatus.getStatus_description();
        
          String [] memberTokens = member.split(":");
          String memberAddr = memberTokens[0];
          String memberPort = memberTokens[1];
        
          CommonIPPortDefinition memberDef = new CommonIPPortDefinition();
          memberDef.setAddress(memberAddr);
          memberDef.setPort(Long.parseLong(memberPort));
    
          CommonIPPortDefinition [] memberDefA = { memberDef };
          CommonIPPortDefinition [][] memberDefAofA = { memberDefA };
        
          LocalLBPoolMemberMemberStatistics [] memberStatsA = interfaces.
                getLocalLBPoolMember().get_statistics(poolList, memberDefAofA);
      
          LocalLBPoolMemberMemberStatistics memberStats = memberStatsA[0];
      
          LocalLBPoolMemberMemberStatisticEntry [] statsEntryA = 
                memberStats.getStatistics();
          LocalLBPoolMemberMemberStatisticEntry  statsEntry = statsEntryA[0];
      
          CommonStatistic [] statsA = statsEntry.getStatistics();
      
          for (int j = 0; j < statsA.length; j++) {
            CommonStatisticType type = statsA[j].getType();
            CommonULong64 value64 = statsA[j].getValue();

            if ( type == CommonStatisticType.
                    STATISTIC_SERVER_SIDE_CURRENT_CONNECTIONS ) {
              long cur_connections = value64.getLow();
              realMember.setAvailability(availability.toString());
              realMember.setEnabled(enabled.toString());
              realMember.setDescription(description);
              realMember.setConnections(cur_connections);
            }
          }
        }
      }
    }
    return realMembers;
  }
  
  public void enablePoolMember(String pool, String member) throws Exception {
    String [] poolList = { pool };
    
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
    interfaces.getLocalLBPoolMember().set_monitor_state(poolList, monStateAofA);
    
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
            set_session_enabled_state(poolList, sessionStateAofA);
    
    //getPoolMemberStatus(pool, member);
  }
  
  public void disablePoolMember(String pool, String member) throws Exception {
    String [] poolList = { pool };
    
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
            set_session_enabled_state(poolList, sessionStateAofA);
    
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
        interfaces.getLocalLBPoolMember().get_statistics(poolList, memberDefAofA);
      
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
      Thread.sleep(5000);
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
    
    interfaces.getLocalLBPoolMember().set_monitor_state(poolList, monStateAofA);
    
    //getPoolMemberStatus(pool, member);
  }
}
