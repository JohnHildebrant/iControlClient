package icontrol;


/**
* icontrol/member.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from icontrol.idl
* Wednesday, March 7, 2012 9:53:06 PM PST
*/

public final class member implements org.omg.CORBA.portable.IDLEntity
{
  public String name = null;
  public short connections = (short)0;
  public String availability = null;
  public String enabled = null;
  public String description = null;

  public member ()
  {
  } // ctor

  public member (String _name, short _connections, String _availability, String _enabled, String _description)
  {
    name = _name;
    connections = _connections;
    availability = _availability;
    enabled = _enabled;
    description = _description;
  } // ctor

} // class member
