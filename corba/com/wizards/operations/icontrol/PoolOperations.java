package com.wizards.operations.icontrol;


/**
* com/wizards/operations/icontrol/PoolOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from icontrol.idl
* Monday, March 12, 2012 7:09:13 PM PDT
*/

public interface PoolOperations 
{
  void startPush (String poolName);
  void disablePoolMember (String memberName);
  void enablePoolMember (String memberName);
} // interface PoolOperations
