package org.manifold.compiler;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

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
  public void testGetConstraintType_Undeclared_ThrowsException()
      throws SchematicException {
    Schematic sch = new Schematic("test");
    ConstraintType tv = sch.getConstraintType("bogus");
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
  
  @Test(expected = UndeclaredIdentifierException.class)
  public void testGetConstraint_Undeclared_ThrowsException()
      throws SchematicException {
    Schematic sch = new Schematic("test");
    ConstraintValue cv = sch.getConstraint("bogus");
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
  
  @Test
  public void testGetNodeName()
      throws SchematicException {
    Schematic sch = new Schematic("test");
    // create a test node type
    String nodeTypeName = "TestNode";
    Map<String, TypeValue> attributes = new HashMap<>();
    Map<String, PortTypeValue> ports = new HashMap<>();
    NodeTypeValue nodeType = new NodeTypeValue(attributes, ports);
    sch.addNodeType(nodeTypeName, nodeType);
    // create a node based on this type
    String nodeName = "testNode";
    Map<String, Value> nodeAttrs = new HashMap<>();
    Map<String, Map<String, Value>> nodePortAttrs = new HashMap<>();
    NodeValue node1 = new NodeValue(nodeType, nodeAttrs, nodePortAttrs);
    sch.addNode(nodeName, node1);
    String retrievedNodeName = sch.getNodeName(node1);
    assertEquals(nodeName, retrievedNodeName);
  }
  
  @Test(expected = NoSuchElementException.class)
  public void testGetNodeName_Undeclared_ThrowsException()
      throws NoSuchElementException, SchematicException {
    Schematic sch = new Schematic("test");
    // create a test node type
    String nodeTypeName = "TestNode";
    Map<String, TypeValue> attributes = new HashMap<>();
    Map<String, PortTypeValue> ports = new HashMap<>();
    NodeTypeValue nodeType = new NodeTypeValue(attributes, ports);
    sch.addNodeType(nodeTypeName, nodeType);
    // create a node based on this type
    String nodeName = "testNode";
    Map<String, Value> nodeAttrs = new HashMap<>();
    Map<String, Map<String, Value>> nodePortAttrs = new HashMap<>();
    NodeValue node1 = new NodeValue(nodeType, nodeAttrs, nodePortAttrs);
    // don't add this node to the schematic, but instead try to get its name
    String retrievedName = sch.getNodeName(node1);
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
  
  @Test(expected = MultipleAssignmentException.class)
  public void testAddNode_AlreadyDeclared_ThrowsException()
      throws SchematicException {
    Schematic sch = new Schematic("test");
    // create a test node type
    String nodeTypeName = "TestNode";
    Map<String, TypeValue> attributes = new HashMap<>();
    Map<String, PortTypeValue> ports = new HashMap<>();
    NodeTypeValue nodeType = new NodeTypeValue(attributes, ports);
    sch.addNodeType(nodeTypeName, nodeType);
    // create a node based on this type
    String nodeName = "testNode";
    Map<String, Value> nodeAttrs = new HashMap<>();
    Map<String, Map<String, Value>> nodePortAttrs = new HashMap<>();
    try {
      NodeValue node1 = new NodeValue(nodeType, nodeAttrs, nodePortAttrs);
      sch.addNode(nodeName, node1);
    } catch (MultipleAssignmentException e) {
      fail("exception thrown too early");
    }
    // try to add another node with the same name
    NodeValue node2 = new NodeValue(nodeType, nodeAttrs, nodePortAttrs);
    sch.addNode(nodeName, node2);
  }
  
  @Test(expected = MultipleAssignmentException.class)
  public void testAddConnection_AlreadyDeclared_ThrowsException()
      throws SchematicException {
    Schematic sch = new Schematic("test");
    ConnectionType connType = null;
    NodeValue node1 = null, node2 = null;
    Map<String, Value> connAttrs = null;
    String connName = "conn1";
    try {
      // create a test port type
      String portTypeName = "TestPort";
      Map<String, TypeValue> portAttributes = new HashMap<>();
      PortTypeValue portType = new PortTypeValue(portAttributes);
      sch.addPortType(portTypeName, portType);
      // create a test node type
      String nodeTypeName = "TestNode";
      Map<String, TypeValue> attributes = new HashMap<>();
      Map<String, PortTypeValue> ports = new HashMap<>();
      ports.put("p", portType);
      NodeTypeValue nodeType = new NodeTypeValue(attributes, ports);
      sch.addNodeType(nodeTypeName, nodeType);
      // create two nodes based on this type
      Map<String, Value> nodeAttrs = new HashMap<>();
      Map<String, Map<String, Value>> nodePortAttrs = new HashMap<>();
      nodePortAttrs.put("p", new HashMap<>());
      node1 = new NodeValue(nodeType, nodeAttrs, nodePortAttrs);
      sch.addNode("node1", node1);
      node2 = new NodeValue(nodeType, nodeAttrs, nodePortAttrs);
      sch.addNode("node2", node2);
      // create a test connection type
      String connTypeName = "TestConn";
      connType = new ConnectionType(attributes);
      sch.addConnectionType(connTypeName, connType);
      // create the first connection
      connAttrs = new HashMap<>();
      ConnectionValue conn1 = new ConnectionValue(
          connType, node1.getPort("p"), node2.getPort("p"), connAttrs);
      sch.addConnection(connName, conn1);
    } catch (MultipleAssignmentException e) {
      fail("exception thrown too early");
    }
    ConnectionValue conn2 = new ConnectionValue(
        connType, node1.getPort("p"), node2.getPort("p"), connAttrs);
    sch.addConnection(connName, conn2);
  }
  
  @Test(expected = MultipleAssignmentException.class)
  public void testAddConstraint_AlreadyDeclared_ThrowsException()
      throws SchematicException {
    Schematic sch = new Schematic("test");
    // create a test constraint type
    Map<String, TypeValue> typeAttrs = new HashMap<>();
    ConstraintType cxtType = new ConstraintType(typeAttrs);
    String cxtName = "cxt1";
    Map<String, Value> attrs = new HashMap<>();
    try {
      ConstraintValue cxt1 = new ConstraintValue(cxtType, attrs);
      sch.addConstraint(cxtName, cxt1);
    } catch (MultipleAssignmentException e) {
      fail("exception thrown too early");
    }
    ConstraintValue cxt2 = new ConstraintValue(cxtType, attrs);
    sch.addConstraint(cxtName, cxt2);
  }
  
}
