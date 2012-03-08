package com.wizards.operations.icontrol.data;

import com.wizards.operations.icontrol.data.User;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.3.0.v20110604-r9504", date="2012-03-05T07:56:52")
@StaticMetamodel(Pool.class)
public class Pool_ { 

    public static volatile SingularAttribute<Pool, Integer> id;
    public static volatile SingularAttribute<Pool, String> description;
    public static volatile SingularAttribute<Pool, String> name;
    public static volatile SingularAttribute<Pool, Short> connections;
    public static volatile SingularAttribute<Pool, Short> threshold;
    public static volatile CollectionAttribute<Pool, User> userCollection;

}