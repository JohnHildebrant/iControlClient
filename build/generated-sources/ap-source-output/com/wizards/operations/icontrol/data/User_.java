package com.wizards.operations.icontrol.data;

import com.wizards.operations.icontrol.data.Pool;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.3.0.v20110604-r9504", date="2012-03-05T07:56:52")
@StaticMetamodel(User.class)
public class User_ { 

    public static volatile SingularAttribute<User, Integer> id;
    public static volatile CollectionAttribute<User, Pool> poolCollection;
    public static volatile SingularAttribute<User, String> samaccountname;
    public static volatile SingularAttribute<User, String> fullname;

}