package org.manifold.compiler;

import static org.junit.Assert.*;

import java.util.HashMap;
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
  
  @Test(expected = MultipleDefinitionException.class)
  public void testAddUserDefinedType_AlreadyDefined_ThrowsException()
      throws SchematicException {
    Schematic sch = new Schematic("test");
    String udtName = "TestUDT";
    TypeValue tv = BooleanTypeValue.getInstance();
    try {
      sch.addUserDefinedType(udtName, tv);
    } catch (MultipleDefinitionException e) {
      fail("exception thrown too early");
    }
    // try to add another UDT with the same name
    sch.addUserDefinedType(udtName, tv);
  }
  
  @Test(expected = MultipleDefinitionException.class)
  public void testAddPortType_AlreadyDefined_ThrowsException()
      throws SchematicException {
    Schematic sch = new Schematic("test");
    // add the first port type
    String portTypeName = "TestPort";
    Map<String, TypeValue> portAttributes = new HashMap<>();
    try {
      PortTypeValue portType1 = new PortTypeValue(portAttributes);
      sch.addPortType(portTypeName, portType1);
    } catch (MultipleDefinitionException e) {
      fail("exception thrown too early");
    }
    // try to add another port type with the same name
    PortTypeValue portType2 = new PortTypeValue(portAttributes);
    sch.addPortType(portTypeName, portType2);
  }
  
  @Test(expected = MultipleDefinitionException.class)
  public void testAddNodeType_AlreadyDefined_ThrowsException()
      throws SchematicException {
    Schematic sch = new Schematic("test");
    // add the first node type
    String nodeTypeName = "TestNode";
    Map<String, TypeValue> attributes = new HashMap<>();
    Map<String, PortTypeValue> ports = new HashMap<>();
    try {
      NodeTypeValue nv1 = new NodeTypeValue(attributes, ports);
      sch.addNodeType(nodeTypeName, nv1);
    } catch (MultipleDefinitionException e) {
      fail("exception thrown too early");
    }
    // try to add another node type with the same name
    NodeTypeValue nv2 = new NodeTypeValue(attributes, ports);
    sch.addNodeType(nodeTypeName, nv2);
  }
  
  // testAddConnectionType
  @Test(expected = MultipleDefinitionException.class)
  public void testAddConnectionType_AlreadyDefined_ThrowsException()
      throws SchematicException {
    Schematic sch = new Schematic("test");
    // add the first connection type
    String connTypeName = "TestConnection";
    Map<String, TypeValue> attributes = new HashMap<>();
    try {
      ConnectionType cv1 = new ConnectionType(attributes);
      sch.addConnectionType(connTypeName, cv1);
    } catch (MultipleDefinitionException e) {
      fail("exception thrown too early");
    }
    // try to add another connection type with the same name
    ConnectionType cv2 = new ConnectionType(attributes);
    sch.addConnectionType(connTypeName, cv2);
  }
  
  @Test(expected = MultipleDefinitionException.class)
  public void testAddConstraintType_AlreadyDefined_ThrowsException()
      throws SchematicException {
    Schematic sch = new Schematic("test");
    // add the first constraint type
    String constraintTypeName = "TestConstraint";
    Map<String, TypeValue> attributes = new HashMap<>();
    try {
      ConstraintType cv1 = new ConstraintType(attributes);
      sch.addConstraintType(constraintTypeName, cv1);
    } catch (MultipleDefinitionException e) {
      fail("exception thrown too early");
    }
    // try to add another constraint type with the same name
    ConstraintType cv2 = new ConstraintType(attributes);
    sch.addConstraintType(constraintTypeName, cv2);
  }
  
}
