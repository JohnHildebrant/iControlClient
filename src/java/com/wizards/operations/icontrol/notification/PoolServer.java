/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wizards.operations.icontrol.notification;

/**
 *
 * @author hildebj
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNotification.Property;
import org.omg.CosNotifyChannelAdmin.EventChannel;
import org.omg.CosNotifyChannelAdmin.EventChannelFactory;
import org.omg.CosNotifyChannelAdmin.EventChannelFactoryHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

public class PoolServer {
  /**
   * main
   */
  
  private Map<String, EventChannel> channels;
  private Map<String, PoolImpl> pools;
  private static NamingContextExt nc;
  private static org.omg.CORBA.ORB orb;
  private static POA poa;
  
  PoolServer() {
    channels = new HashMap<String, EventChannel>();
  }
  
  static void main (String argv[]) {
    orb = org.omg.CORBA.ORB.init(argv, null);
    
    try {
      // initialize POA, get naming and event service references
      poa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
      poa.the_POAManager().activate();
      
      nc = NamingContextExtHelper.narrow(orb.
              resolve_initial_references("NameService"));
      
      // wait for requests
      orb.run();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  /*
   * Register an event channel for a Pool's events
   * To be shared among consumers of a Pool's EventChannel
   */
  public void registerPool(final PoolImpl pool) {
    try {
      String poolName = pool.getName();
      if (!(pools.containsKey(poolName) && channels.containsKey(poolName))) {
        EventChannelFactory factory = EventChannelFactoryHelper.
                  narrow(nc.resolve(nc.to_name("NotificationService")));

        if (factory == null) {
          System.err.println("Could not find or narrow EventChannelFactory");
          System.exit(1);
        }

        org.omg.CORBA.IntHolder idHolder = new org.omg.CORBA.IntHolder();

        Property[] qos = new Property[0];
        Property[] adm = new Property[0];

        EventChannel channel = factory.create_channel(qos, adm, idHolder);
        nc.rebind(nc.to_name(poolName + "_event.channel"), channel);

        channels.put(poolName, channel);

        System.out.println("Channel " + idHolder.value + 
          " created and bound to" + " name " + poolName + "_event.channel.");

        // associate a Pool object, implicitly activate it and advertise 
        // its presence
        pool.setChannel(channel);
        pool.setOrb(orb);
        pool.setPoa(poa);
        pool.setPoolServer(this);
        pools.put(poolName, pool);
        pool.connect();
        System.out.println("Pool connected");

        org.omg.CORBA.Object poolObj = poa.servant_to_reference(pool);
        System.out.println("Pool exported");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
