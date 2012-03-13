/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wizards.operations.icontrol.notification;

/**
 *
 * @author hildebj
 */

import com.wizards.operations.icontrol.Pool;
import com.wizards.operations.icontrol.PoolHelper;
import org.omg.CORBA.Any;
import org.omg.CosEventComm.Disconnected;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNotification.*;
import org.omg.CosNotifyChannelAdmin.*;
import org.omg.CosNotifyComm.InvalidEventType;
import org.omg.CosNotifyComm.StructuredPushConsumer;
import org.omg.CosNotifyComm.StructuredPushConsumerPOA;
import org.omg.CosNotifyFilter.*;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

public class PoolClient extends StructuredPushConsumerPOA {
  
  PoolClient(String argv[]) {
    init(argv);
  }
  
  @Override
  public void push_structured_event(StructuredEvent se) throws Disconnected {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  /** 
  * releases any resources, none in this case
  */
  @Override
  public void disconnect_structured_push_consumer() {
    System.out.println("Disconnected!");
  }

  @Override
  public void offer_change(EventType[] ets, EventType[] ets1) 
          throws InvalidEventType {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public void init(String argv[]) {
    EventChannel channel;
    FilterFactory filterFactory;
    Filter filter;
    ConsumerAdmin consumerAdmin;
    StructuredProxyPushSupplier proxyPushSupplier;
    StructuredPushConsumer structuredPushConsumer;
    Pool pool;
    String poolName = argv[0];
    
    // initialize ORB
    org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init(argv, null);
    POA poa;
    
    try {
      // initialize POA
      poa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
      
      // get naming service reference
      NamingContextExt nc = NamingContextExtHelper.
              narrow(orb.resolve_initial_references("NameService"));
      
      // find the event channel reference and the Pool
      channel = EventChannelHelper.narrow(nc.resolve(nc.
              to_name(poolName + "_event.channel")));
      
      pool = PoolHelper.narrow(nc.resolve(nc.to_name("Pool")));
      
      poa.the_POAManager().activate();
      
      // create and implicitly activate the client
      structuredPushConsumer = (StructuredPushConsumer)this._this(orb);
      
      // get the admin interface and the supplier proxy
      consumerAdmin = channel.default_consumer_admin();
      
      proxyPushSupplier = StructuredProxyPushSupplierHelper.narrow(
              consumerAdmin.obtain_notification_push_supplier(
              ClientType.STRUCTURED_EVENT, new org.omg.CORBA.IntHolder() ) );
      
      // connect ourselves to the event channel
      proxyPushSupplier.connect_structured_push_consumer(structuredPushConsumer);
      
      // get the default filter factory
      filterFactory = channel.default_filter_factory();
      if (filterFactory == null) {
        System.err.println("No default filter factory!");
      } else {
        filter = filterFactory.create_filter("EXTENDED_TCL");
        EventType[] eventTypes =
                new EventType[] { new EventType("Pool", "Watching"),
                                  new EventType("Connection", "Changed"),
                                  new EventType("Other", "Changed") };
        ConstraintExp constraint = new ConstraintExp( eventTypes, "TRUE" );
        filter.add_constraints( new ConstraintExp[] { constraint } );
        proxyPushSupplier.add_filter(filter);
      
        Any default_value = orb.create_any();
        default_value.insert_short( (short) 0 );
        MappingFilter mappingFilter = filterFactory.create_mapping_filter(
                "EXTENDED_TCL", default_value);

        MappingConstraintPair[] constraints = new MappingConstraintPair[1];
        Any mappingValue = orb.create_any();
        mappingValue.insert_short( (short) 10 );
        ConstraintExp mappingConstraint = 
                new ConstraintExp(eventTypes, "$urgent == TRUE");
        constraints[0] = 
                new MappingConstraintPair(mappingConstraint, mappingValue);
        
        mappingFilter.add_mapping_constraints(constraints);
        consumerAdmin.priority_filter(mappingFilter);
      }
      
      Property[] qos = new Property[1];
      org.omg.CORBA.Any data = org.omg.CORBA.ORB.init().create_any();
      data.insert_short( PriorityOrder.value );
      qos[0] = new Property( OrderPolicy.value, data );
      
      try {
        consumerAdmin.set_qos(qos);
      } catch (UnsupportedQoS ex) {
        System.err.println("Unsupported QoS");
        System.exit(1);
      }
      
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
