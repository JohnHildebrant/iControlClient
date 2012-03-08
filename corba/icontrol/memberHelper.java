package icontrol;


/**
* icontrol/memberHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from icontrol.idl
* Wednesday, March 7, 2012 9:53:06 PM PST
*/

abstract public class memberHelper
{
  private static String  _id = "IDL:icontrol/member:1.0";

  public static void insert (org.omg.CORBA.Any a, icontrol.member that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static icontrol.member extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  private static boolean __active = false;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      synchronized (org.omg.CORBA.TypeCode.class)
      {
        if (__typeCode == null)
        {
          if (__active)
          {
            return org.omg.CORBA.ORB.init().create_recursive_tc ( _id );
          }
          __active = true;
          org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [5];
          org.omg.CORBA.TypeCode _tcOf_members0 = null;
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _members0[0] = new org.omg.CORBA.StructMember (
            "name",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_ushort);
          _members0[1] = new org.omg.CORBA.StructMember (
            "connections",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _members0[2] = new org.omg.CORBA.StructMember (
            "availability",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _members0[3] = new org.omg.CORBA.StructMember (
            "enabled",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _members0[4] = new org.omg.CORBA.StructMember (
            "description",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (icontrol.memberHelper.id (), "member", _members0);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static icontrol.member read (org.omg.CORBA.portable.InputStream istream)
  {
    icontrol.member value = new icontrol.member ();
    value.name = istream.read_string ();
    value.connections = istream.read_ushort ();
    value.availability = istream.read_string ();
    value.enabled = istream.read_string ();
    value.description = istream.read_string ();
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, icontrol.member value)
  {
    ostream.write_string (value.name);
    ostream.write_ushort (value.connections);
    ostream.write_string (value.availability);
    ostream.write_string (value.enabled);
    ostream.write_string (value.description);
  }

}