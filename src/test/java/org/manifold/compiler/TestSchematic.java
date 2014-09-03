package org.manifold.compiler;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;
import org.manifold.compiler.middle.Schematic;
import org.manifold.compiler.middle.SchematicException;

public class TestSchematic {

  @Test(expected = UndeclaredIdentifierException.class)
  public void testGetUserDefinedType_Undeclared_ThrowsException()
      throws SchematicException {
    Schematic sch = new Schematic("test");
    TypeValue tv = sch.getUserDefinedType("bogus");
  }

  @Test(expected = UndeclaredIdentifierException.class)
  public void testGetPortType_Undeclared_ThrowsException()
      throws SchematicException {
    Schematic sch = new Schematic("test");
    PortTypeValue tv = sch.getPortType("bogus");
  }
  
  @Test(expected = UndeclaredIdentifierException.class)
  public void testGetNodeType_Undeclared_ThrowsException()
      throws SchematicException {
    Schematic sch = new Schematic("test");
    NodeTypeValue tv = sch.getNodeType("bogus");
  }
  
  @Test(expected = UndeclaredIdentifierException.class)
  public void testGetConnectionType_Undeclared_ThrowsException()
      throws SchematicException {
    Schematic sch = new Schematic("test");
    ConnectionType tv = sch.getConnectionType("bogus");
  }
  
  @Test(expected = UndeclaredIdentifierException.class)
  public void testGetNode_Undeclared_ThrowsException()
      throws SchematicException {
    Schematic sch = new Schematic("test");
    NodeValue nv = sch.getNode("bogus");
  }
  
  @Test(expected = UndeclaredIdentifierException.class)
  public void testGetConnection_Undeclared_ThrowsException()
      throws SchematicException {
    Schematic sch = new Schematic("test");
    ConnectionValue cv = sch.getConnection("bogus");
  }
  
  @Test
  public void testGetNodes_InitiallyEmpty() {
    Schematic sch = new Schematic("test");
    Map<String, NodeValue> nodes = sch.getNodes();
    assertTrue("schematic node map not initially empty", nodes.isEmpty());
  }
  
  @Test
  public void testGetConnections_InitiallyEmpty() {
    Schematic sch = new Schematic("test");
    Map<String, ConnectionValue> connections = sch.getConnections();
    assertTrue("schematic connection map not initially empty", 
        connections.isEmpty());
  }
  
}
