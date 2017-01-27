package org.manifold.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;
import org.manifold.compiler.middle.Schematic;
import org.manifold.compiler.middle.SchematicException;

public class TestSchematic {
  Map<String, TypeValue> attributes;
  Map<String, TypeValue> portAttributes;


  @Before
  public void setup() {
    attributes = new HashMap<>();
    portAttributes = new HashMap<>();
  }

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
  public void testGetConstraintType_Undeclared_ThrowsException()
      throws SchematicException {
    Schematic sch = new Schematic("test");
    ConstraintType tv = sch.getConstraintType("bogus");
  }

  @Test(expected = UndeclaredIdentifierException.class)
  public void testGetConnectionType_Undeclared_ThrowsException()
          throws SchematicException {
    Schematic sch = new Schematic("test");
    ConnectionTypeValue tv = sch.getConnectionType("bogus");
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
  public void testGetConstraints_InitiallyEmpty() {
    Schematic sch = new Schematic("test");
    Map<String, ConstraintValue> constraints = sch.getConstraints();
    assertTrue("schematic constraint map not initially empty",
        constraints.isEmpty());
  }

  @Test
  public void testGetUserDefinedType() throws SchematicException {
    Schematic sch = new Schematic("test");
    String udtName = "TestUDT";
    UserDefinedTypeValue tv = new UserDefinedTypeValue(
        BooleanTypeValue.getInstance(), udtName);
    sch.addUserDefinedType(tv);
    TypeValue actual = sch.getUserDefinedType(udtName);
    assertEquals(tv, actual);
  }

  @Test
  public void testGetPortType() throws SchematicException {
    Schematic sch = new Schematic("test");
    // add the first port type
    String portTypeName = "TestPort";
    PortTypeValue portType1 = new PortTypeValue(
        BooleanTypeValue.getInstance(), portAttributes);

    sch.addPortType(portTypeName, portType1);
    PortTypeValue actual = sch.getPortType(portTypeName);
    assertEquals(portType1, actual);
  }

  @Test
  public void testGetNodeType() throws SchematicException {
    Schematic sch = new Schematic("test");
    // add the first node type
    String nodeTypeName = "TestNode";
    Map<String, PortTypeValue> ports = new HashMap<>();
    NodeTypeValue nv1 = new NodeTypeValue(attributes, ports);
    sch.addNodeType(nodeTypeName, nv1);
    NodeTypeValue actual = sch.getNodeType(nodeTypeName);
    assertEquals(nv1, actual);
  }

  @Test
  public void testGetConstraintType() throws SchematicException {
    Schematic sch = new Schematic("test");
    // add the first constraint type
    String constraintTypeName = "TestConstraint";
    ConstraintType cv1 = new ConstraintType(attributes);
    sch.addConstraintType(constraintTypeName, cv1);
    ConstraintType actual = sch.getConstraintType(constraintTypeName);
    assertEquals(cv1, actual);
  }

  @Test
  public void testGetConnectionType() throws SchematicException {
    Schematic sch = new Schematic("test");
    // add the first constraint type
    String connectionTypeName = "TestConstraint";
    ConnectionTypeValue cv1 = ConnectionTypeValue.getInstance();
    sch.addConnectionType(connectionTypeName, cv1);
    ConnectionTypeValue actual = sch.getConnectionType(connectionTypeName);
    assertEquals(cv1, actual);
  }

  @Test
  public void testGetNode() throws SchematicException {
    Schematic sch = new Schematic("test");
    // create a test node type
    String nodeTypeName = "TestNode";
    Map<String, PortTypeValue> ports = new HashMap<>();
    NodeTypeValue nodeType = new NodeTypeValue(attributes, ports);
    sch.addNodeType(nodeTypeName, nodeType);
    // create a node based on this type
    String nodeName = "testNode";
    Map<String, Value> nodeAttrs = new HashMap<>();
    Map<String, Map<String, Value>> nodePortAttrs = new HashMap<>();
    NodeValue node1 = new NodeValue(nodeType, nodeAttrs, nodePortAttrs);
    sch.addNode(nodeName, node1);
    NodeValue actual = sch.getNode(nodeName);
    assertEquals(node1, actual);
  }

  @Test
  public void testGetConnection() throws SchematicException {
    Schematic sch = new Schematic("test");
    ConnectionTypeValue connType = null;
    NodeValue node1 = null, node2 = null;
    Map<String, Value> connAttrs = null;
    String connName = "conn1";
    // create a test port type
    String portTypeName = "TestPort";
    PortTypeValue portType = new PortTypeValue(
        BooleanTypeValue.getInstance(), portAttributes);
    sch.addPortType(portTypeName, portType);
    // create a test node type
    String nodeTypeName = "TestNode";
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

    // create the first connection
    connAttrs = new HashMap<>();
    ConnectionValue conn1 = new ConnectionValue(
        node1.getPort("p"), node2.getPort("p"), connAttrs);
    sch.addConnection(connName, conn1);
    ConnectionValue actual = sch.getConnection(connName);
    assertEquals(conn1, actual);
  }

  @Test
  public void testGetConstraint() throws SchematicException {
    Schematic sch = new Schematic("test");
    // create a test constraint type
    ConstraintType cxtType = new ConstraintType(attributes);
    String cxtName = "cxt1";
    Map<String, Value> attrs = new HashMap<>();
    ConstraintValue cxt1 = new ConstraintValue(cxtType, attrs);
    sch.addConstraint(cxtName, cxt1);
    ConstraintValue actual = sch.getConstraint(cxtName);
    assertEquals(cxt1, actual);
  }

  @Test
  public void testGetNodeName()
      throws SchematicException {
    Schematic sch = new Schematic("test");
    // create a test node type
    String nodeTypeName = "TestNode";
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

  @Test
  public void testGetConnectionName()
      throws SchematicException {
    Schematic sch = new Schematic("test");
    // create a test port type
    String portTypeName = "TestPort";
    PortTypeValue portType1 = new PortTypeValue(
        BooleanTypeValue.getInstance(), portAttributes);
    sch.addPortType(portTypeName, portType1);

    // create a test node type
    String nodeTypeName = "TestNode";
    Map<String, PortTypeValue> ports = new HashMap<>();
    ports.put("p0", portType1);

    NodeTypeValue nodeType = new NodeTypeValue(attributes, ports);
    sch.addNodeType(nodeTypeName, nodeType);
    // create two nodes based on this type
    Map<String, Value> nodeAttrs = new HashMap<>();
    Map<String, Map<String, Value>> nodePortAttrs = new HashMap<>();
    nodePortAttrs.put("p0", new HashMap<>());
    NodeValue node1 = new NodeValue(nodeType, nodeAttrs, nodePortAttrs);
    sch.addNode("testNode1", node1);
    NodeValue node2 = new NodeValue(nodeType, nodeAttrs, nodePortAttrs);
    sch.addNode("testNode2", node2);

    String connName = "testConn1";
    ConnectionValue conn = new ConnectionValue(
        node1.getPort("p0"), node2.getPort("p0"), nodeAttrs);
    sch.addConnection(connName, conn);
    String retrievedName = sch.getConnectionName(conn);
    assertEquals(connName, retrievedName);
  }

  @Test(expected = NoSuchElementException.class)
  public void testGetConnectionName_Undeclared_ThrowsException()
      throws SchematicException {
    Schematic sch = new Schematic("test");
    // create a test port type
    String portTypeName = "TestPort";
    PortTypeValue portType1 = new PortTypeValue(
        BooleanTypeValue.getInstance(), portAttributes);
    sch.addPortType(portTypeName, portType1);

    // create a test node type
    String nodeTypeName = "TestNode";
    Map<String, PortTypeValue> ports = new HashMap<>();
    ports.put("p0", portType1);

    NodeTypeValue nodeType = new NodeTypeValue(attributes, ports);
    sch.addNodeType(nodeTypeName, nodeType);
    // create two nodes based on this type
    Map<String, Value> nodeAttrs = new HashMap<>();
    Map<String, Map<String, Value>> nodePortAttrs = new HashMap<>();
    nodePortAttrs.put("p0", new HashMap<>());
    NodeValue node1 = new NodeValue(nodeType, nodeAttrs, nodePortAttrs);
    sch.addNode("testNode1", node1);
    NodeValue node2 = new NodeValue(nodeType, nodeAttrs, nodePortAttrs);
    sch.addNode("testNode2", node2);

    String connName = "testConn1";
    ConnectionValue conn = new ConnectionValue(
        node1.getPort("p0"), node2.getPort("p0"), nodeAttrs);
    //sch.addConnection(connName, conn);
    String retrievedName = sch.getConnectionName(conn);
  }

  @Test
  public void testGetConstraintName() throws SchematicException {
    Schematic sch = new Schematic("test");
    ConstraintType cxtType1 = new ConstraintType(attributes);
    sch.addConstraintType("TestConstraint", cxtType1);

    String cxtName = "constraint1";
    Map<String, Value> attrs = new HashMap<>();
    ConstraintValue cxt1 = new ConstraintValue(cxtType1, attrs);
    sch.addConstraint(cxtName, cxt1);
    String retrievedName = sch.getConstraintName(cxt1);
    assertEquals(cxtName, retrievedName);
  }

  @Test(expected = NoSuchElementException.class)
  public void testGetConstraintName_Undeclared_ThrowsException()
      throws SchematicException {
    Schematic sch = new Schematic("test");
    ConstraintType cxtType1 = new ConstraintType(attributes);
    sch.addConstraintType("TestConstraint", cxtType1);

    String cxtName = "constraint1";
    Map<String, Value> attrs = new HashMap<>();
    ConstraintValue cxt1 = new ConstraintValue(cxtType1, attrs);
    //sch.addConstraint(cxtName, cxt1);
    String retrievedName = sch.getConstraintName(cxt1);
  }

  @Test(expected = MultipleDefinitionException.class)
  public void testAddUserDefinedType_AlreadyDefined_ThrowsException()
      throws SchematicException {
    Schematic sch = new Schematic("test");
    String udtName = "TestUDT";
    TypeValue tv = BooleanTypeValue.getInstance();
    try {
      sch.addUserDefinedType(new UserDefinedTypeValue(tv, udtName));
    } catch (MultipleDefinitionException e) {
      fail("exception thrown too early");
    }
    // try to add another UDT with the same name
    sch.addUserDefinedType(new UserDefinedTypeValue(tv, udtName));
  }

  @Test(expected = MultipleDefinitionException.class)
  public void testAddPortType_AlreadyDefined_ThrowsException()
      throws SchematicException {
    Schematic sch = new Schematic("test");
    // add the first port type
    String portTypeName = "TestPort";
    try {
      PortTypeValue portType1 = new PortTypeValue(
          BooleanTypeValue.getInstance(), portAttributes);
      sch.addPortType(portTypeName, portType1);
    } catch (MultipleDefinitionException e) {
      fail("exception thrown too early");
    }
    // try to add another port type with the same name
    PortTypeValue portType2 = new PortTypeValue(
        BooleanTypeValue.getInstance(), portAttributes);
    sch.addPortType(portTypeName, portType2);
  }

  @Test(expected = MultipleDefinitionException.class)
  public void testAddNodeType_AlreadyDefined_ThrowsException()
      throws SchematicException {
    Schematic sch = new Schematic("test");
    // add the first node type
    String nodeTypeName = "TestNode";
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

  @Test(expected = MultipleDefinitionException.class)
  public void testAddConstraintType_AlreadyDefined_ThrowsException()
      throws SchematicException {
    Schematic sch = new Schematic("test");
    // add the first constraint type
    String constraintTypeName = "TestConstraint";
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

  @Test(expected = MultipleDefinitionException.class)
  public void testAddConnectionType_AlreadyDefined_ThrowsException()
      throws SchematicException {
    Schematic sch = new Schematic("test");
    // add the first constraint type
    String connectionTypeName = "TestConnection";
    try {
      ConnectionTypeValue cv1 = ConnectionTypeValue.getInstance();
      sch.addConnectionType(connectionTypeName, cv1);
    } catch (MultipleDefinitionException e) {
      fail("exception thrown too early");
    }
    // try to add another constraint type with the same name
    ConnectionTypeValue cv2 = ConnectionTypeValue.getInstance();
    sch.addConnectionType(connectionTypeName, cv2);
  }

  @Test(expected = MultipleAssignmentException.class)
  public void testAddNode_AlreadyDeclared_ThrowsException()
      throws SchematicException {
    Schematic sch = new Schematic("test");
    // create a test node type
    String nodeTypeName = "TestNode";
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
    ConnectionTypeValue connType = null;
    NodeValue node1 = null, node2 = null;
    Map<String, Value> connAttrs = null;
    String connName = "conn1";
    try {
      // create a test port type
      String portTypeName = "TestPort";
      PortTypeValue portType = new PortTypeValue(
          BooleanTypeValue.getInstance(), portAttributes);
      sch.addPortType(portTypeName, portType);
      // create a test node type
      String nodeTypeName = "TestNode";
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

      // create the first connection
      connAttrs = new HashMap<>();
      ConnectionValue conn1 = new ConnectionValue(
          node1.getPort("p"), node2.getPort("p"), connAttrs);
      sch.addConnection(connName, conn1);
    } catch (MultipleAssignmentException e) {
      fail("exception thrown too early");
    }
    ConnectionValue conn2 = new ConnectionValue(
        node1.getPort("p"), node2.getPort("p"), connAttrs);
    sch.addConnection(connName, conn2);
  }

  @Test(expected = MultipleAssignmentException.class)
  public void testAddConstraint_AlreadyDeclared_ThrowsException()
      throws SchematicException {
    Schematic sch = new Schematic("test");
    // create a test constraint type
    ConstraintType cxtType = new ConstraintType(attributes);
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
