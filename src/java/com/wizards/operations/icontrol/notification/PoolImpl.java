/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wizards.operations.icontrol.notification;

import com.wizards.operations.icontrol.PoolPOA;
import com.wizards.operations.icontrol.notification.RealPool.RealMember;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CosNotification.*;
import org.omg.CosNotifyChannelAdmin.*;
import org.omg.CosNotifyComm.InvalidEventType;
import org.omg.CosNotifyComm.StructuredPushSupplierHelper;
import org.omg.CosNotifyComm.StructuredPushSupplierOperations;
import org.omg.CosNotifyComm.StructuredPushSupplierPOATie;
import org.omg.PortableServer.POA;

/**
 *
 * @author hildebj
 */
public class PoolImpl 
  extends PoolPOA 
  implements StructuredPushSupplierOperations {
  
  private EventChannel channel;
  private SupplierAdmin supplierAdmin;
  private ORB orb;
  private POA poa;
  private PoolServer poolServer;
  private Pool pool;
  private RealPool realPool;
  private int eventId;
  private boolean disconnected;
  private String poolName;
  private StructuredPushSupplierPOATie thisTie;
  private ClientType ctype;
  private org.omg.CORBA.IntHolder proxyIdHolder;
  
  public PoolImpl(EventChannel e, ORB orb, POA poa, PoolServer poolServer) 
          throws Exception {
    // set the ORB and event channel
    channel = e;
    this.orb = orb;
    this.poa = poa;
    this.poolServer = poolServer;
    eventId = 0;
    realPool = new RealPool();
  }
  
  public int getEventId() {
    return eventId++;
  }
  
  class Pool {
    /**
      * @return the name
      */
    public String getName() {
      return name;
    }

    private String name;

    public Pool(String name) {
      this.name = name;
    }
    
    public List<RealMember> proxyMembers;
    
    public ArrayList<StructuredProxyPushConsumer> pushConsumers;
  }

  @Override
  public void startPush(final String poolName) {
    // register Pool with PoolServer
    poolServer.registerPool(poolName);
    Thread poolThread = null;
    if (this.poolName == null) {
      this.poolName = poolName;
      pool = new Pool(poolName);
      // start pool thread
      poolThread = new Thread(new PoolRunnable(poolName));
    }
    
    StructuredProxyPushConsumer pushConsumer = null;
    try {
      pushConsumer = StructuredProxyPushConsumerHelper.narrow(
          supplierAdmin.obtain_notification_push_consumer(ctype, proxyIdHolder));
      pool.pushConsumers.add(pushConsumer);
    } catch (AdminLimitExceeded ex) {
        System.err.println("Could not get consumer proxy, maximum number of "
                + "proxies exceeded!");
        System.exit(1);
    }
      
    // connect the push supplier
    try {
      pushConsumer.connect_structured_push_supplier(StructuredPushSupplierHelper
              .narrow( poa.servant_to_reference( thisTie )));
    } catch( Exception e ) {
        e.printStackTrace();
    } 
    
    // push the first event to push all pool members
    try {
      pool.proxyMembers = realPool.getPoolMembersStatus(poolName);
      if ( generateEvents() ) {
        // create a structured event
        StructuredEvent watchingEvent = new StructuredEvent();

        // set the event type and name
        EventType type = new EventType("Pool", "Watching");
        FixedEventHeader fixed = new FixedEventHeader(type, "" + getEventId() );

        // complete header date
        Property variable[] = new Property[0];
        watchingEvent.header = new EventHeader(fixed, variable);

        // set filterable event body data
        watchingEvent.filterable_data = new Property[2];

        Any membersAny = orb.create_any();
        org.omg.CORBA.TypeCode tcInterface = orb.create_interface_tc(
              com.wizards.operations.icontrol.MemberHelper.id (), "Member");
        int memberCount = pool.proxyMembers.size();
        org.omg.CORBA.TypeCode tcArray = orb.
                create_array_tc(memberCount, tcInterface);
        membersAny.insert_Value(pool.proxyMembers.toArray(), tcArray);
        watchingEvent.filterable_data[0] = new Property("members", membersAny);

        Any urgentAny = orb.create_any();
        urgentAny.insert_boolean( true );
        watchingEvent.filterable_data[1] = new Property("urgent", urgentAny);

        watchingEvent.remainder_of_body = orb.create_any();

        try {
          pushConsumer.push_structured_event(watchingEvent);
        } catch ( org.omg.CosEventComm.Disconnected d ) {}
      }
    } catch ( Exception e ) {
      e.printStackTrace();
    }
    
    // now get updates via another thread if not already created
    if (poolThread != null) poolThread.start();
  }
  
  public void connect() {
    thisTie = new StructuredPushSupplierPOATie(this);

    // get admin interface and proxy consumer
    supplierAdmin = channel.default_supplier_admin();

    ctype = ClientType.STRUCTURED_EVENT;
    proxyIdHolder = new org.omg.CORBA.IntHolder();
  }

  @Override
  public void disablePoolMember(String memberName) {
    try {
      realPool.disablePoolMember(poolName, memberName);
    } catch (Exception ex) {
      Logger.getLogger(PoolImpl.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  @Override
  public void enablePoolMember(String memberName) {
    try {
      realPool.enablePoolMember(poolName, memberName);
    } catch (Exception ex) {
      Logger.getLogger(PoolImpl.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
    * Potentially release resources, 
    * from CosNotifyComm.NotifySubscribe
    */
  @Override
  public void disconnect_structured_push_supplier() {
    disconnected = true;
  }
  
  boolean generateEvents() {
    return !disconnected;
  }

  @Override
  public void subscription_change(EventType[] ets, EventType[] ets1) throws 
          InvalidEventType {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  /** Inner class PoolWorker
   *  Monitors an F5 pool and sends notifications
   */
  
  public class PoolRunnable implements Runnable {
    private String poolName;
    public PoolRunnable(String poolName) {
      this.poolName = poolName; 
    }

    @Override
    public void run() {
      
      while (!disconnected) {
        // wait until notified
        try {
          clock();
          synchronized( this ) {
            wait();
          }
        } catch( InterruptedException ie ) {}
        
        try {
          // poll for pool member changes
          List<RealMember> newProxyMembers = realPool.
                  getPoolMembersStatus(poolName);
          for (int i = 0; i < newProxyMembers.size(); i++) {
            RealMember newProxyMember = newProxyMembers.get(i);
            RealMember oldProxyMember = pool.proxyMembers.get(i);
            long cur_connections = newProxyMember.getConnections();
            if (cur_connections != oldProxyMember.getConnections()) {
              // update connections
              pool.proxyMembers.get(i).setConnections(cur_connections);
              // create a structured event
              StructuredEvent connChangeEvent = new StructuredEvent();
        
              // set the event type and name
              EventType type = new EventType("Connection", "Changed");
              FixedEventHeader fixed = new FixedEventHeader(type, "" + 
                      getEventId() );
        
              // complete header date
              Property variable[] = new Property[0];
              connChangeEvent.header = new EventHeader(fixed, variable);
        
              // set filterable event body data
              connChangeEvent.filterable_data = new Property[3];
        
              Any nameAny = orb.create_any();
              nameAny.insert_string(newProxyMember.getName());
              connChangeEvent.filterable_data[0] = 
                      new Property("name", nameAny);
              
              Any connAny = orb.create_any();
              connAny.insert_longlong(cur_connections);
              connChangeEvent.filterable_data[1] = 
                      new Property("conn", connAny);
              
              Any urgentAny = orb.create_any();
              urgentAny.insert_boolean( true );
              connChangeEvent.filterable_data[1] = 
                      new Property("urgent", urgentAny);

              connChangeEvent.remainder_of_body = orb.create_any();
              
              // push event to all consumers
              for (StructuredProxyPushConsumer pushConsumer : 
                      pool.pushConsumers) {
                pushConsumer.push_structured_event(connChangeEvent);
              }
            }
          }
          // all other state changes are rare so will be bundled in one event
          for (int i = 0; i < newProxyMembers.size(); i++) {
            RealMember newProxyMember = newProxyMembers.get(i);
            RealMember oldProxyMember = pool.proxyMembers.get(i);
            String available = newProxyMember.getAvailability();
            String enabled = newProxyMember.getEnabled();
            String description = newProxyMember.getDescription();
            if (!available.equals(oldProxyMember.getAvailability()) || 
                    !enabled.equals(oldProxyMember.getEnabled()) ||
                    !description.equals(oldProxyMember.getDescription())) {
              // update attributes
              pool.proxyMembers.get(i).setAvailability(available);
              pool.proxyMembers.get(i).setEnabled(enabled);
              pool.proxyMembers.get(i).setDescription(description);
              
              // create a structured event
              StructuredEvent otherChangeEvent = new StructuredEvent();
        
              // set the event type and name
              EventType type = new EventType("Other", "Changed");
              FixedEventHeader fixed = new FixedEventHeader(type, "" + 
                      getEventId() );
        
              // complete header date
              Property variable[] = new Property[0];
              otherChangeEvent.header = new EventHeader(fixed, variable);
        
              // set filterable event body data
              otherChangeEvent.filterable_data = new Property[5];
        
              Any nameAny = orb.create_any();
              nameAny.insert_string(newProxyMember.getName());
              otherChangeEvent.filterable_data[0] = 
                      new Property("name", nameAny);
              
              Any availAny = orb.create_any();
              availAny.insert_string(available);
              otherChangeEvent.filterable_data[1] = 
                      new Property("avail", availAny);
              
              Any enabledAny = orb.create_any();
              enabledAny.insert_string(enabled);
              otherChangeEvent.filterable_data[2] = 
                      new Property("enabled", enabledAny);
              
              Any descrAny = orb.create_any();
              descrAny.insert_string(description);
              otherChangeEvent.filterable_data[3] = 
                      new Property("descr", descrAny);
              
              Any urgentAny = orb.create_any();
              urgentAny.insert_boolean( true );
              otherChangeEvent.filterable_data[4] = 
                      new Property("urgent", urgentAny);

              otherChangeEvent.remainder_of_body = orb.create_any();
              
              // push event to all consumers
              for (StructuredProxyPushConsumer pushConsumer : 
                      pool.pushConsumers) {
                pushConsumer.push_structured_event(otherChangeEvent);
              }
            }
          }
        } catch(Exception e) {
          e.printStackTrace();
        }
      }
    }

    /**
     * act as clock management, signaling waiting run thread
     */
	
    public synchronized void clock() throws InterruptedException {
      Thread.currentThread();
      Thread.sleep(5000);
      super.notify();
    }
  }
}