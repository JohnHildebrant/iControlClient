/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wizards.operations.icontrol.test;

import com.wizards.operations.icontrol.notification.RealPool;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hildebj
 */
public class PoolClient {
  /**
   * @param args
   */
  public static void main(String[] args) {
    try
    {
      RealPool pool = new RealPool();
      List<String> poolNames = new ArrayList<String>();
      poolNames.add("AccountsPool2");
      poolNames.add("POOL_opis-prime.onlinegaming.wizards.com_443");
      poolNames.add("WWWPWPPOOL01");
      poolNames.add("POOL_pwp.wizards.com_akamai_80");
      poolNames.add("WWWDNDPOOL01");
      
      while (true) {
        for (String poolName : poolNames) {
          pool.getPoolMembersStatus(poolName);
        }
        Thread.sleep(5000);
      }
    }
    catch(Exception ex)
    {
      ex.printStackTrace(System.out);
    }
  }
}
