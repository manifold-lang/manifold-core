package org.manifold.compiler.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.manifold.compiler.BooleanValue;
import org.manifold.compiler.ConnectionType;
import org.manifold.compiler.ConnectionValue;
import org.manifold.compiler.ConstraintType;
import org.manifold.compiler.NodeTypeValue;
import org.manifold.compiler.NodeValue;
import org.manifold.compiler.PortTypeValue;
import org.manifold.compiler.UndeclaredAttributeException;
import org.manifold.compiler.UndeclaredIdentifierException;
import org.manifold.compiler.Value;
import org.manifold.compiler.middle.Schematic;
import org.manifold.compiler.middle.SchematicException;
import org.manifold.compiler.middle.serialization.SchematicDeserializer;
import org.manifold.compiler.middle.serialization.SchematicSerializer;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class TestSerialization {

  private static final String TEST_SCHEMATIC_NAME = "dogematics";
  private static final String TEST_TYPE_NAME = "very type";
  private static final String TEST_CONSTRAINT_TYPE_NAME = "much constraint";
  private static final String TEST_NODE_TYPE_NAME = "such node";
  private static final String TEST_PORT_TYPE_NAME = "wow port";
  private static final String TEST_PORT_TYPE_ATTRIBUTE_NAME = "much attributes";

  private static final String IN_PORT_NAME = "in_port_name";
  private static final String OUT_PORT_NAME = "out_port_name";

  private static final String DIGITAL_IN = "digital_in";
  private static final String DIGITAL_OUT = "digital_out";

  private static final String IN_NODE_NAME = "in_node_name";
  private static final String OUT_NODE_NAME = "out_node_name";

  private static final String CONNECTION_NAME = "wire";

  private Schematic testSchematic;

  @Before
  public void setup() throws SchematicException {

    testSchematic = new Schematic(TEST_SCHEMATIC_NAME);

    // port type
    PortTypeValue din = new PortTypeValue(new HashMap<>());
    PortTypeValue dout = new PortTypeValue(new HashMap<>());
    testSchematic.addPortType(DIGITAL_IN, din);
    testSchematic.addPortType(DIGITAL_OUT, dout);

    // node type
    HashMap<String, PortTypeValue> dinPortMap = new HashMap<>();
    dinPortMap.put(IN_PORT_NAME, din);

    HashMap<String, PortTypeValue> doutPortMap = new HashMap<>();
    doutPortMap.put(OUT_PORT_NAME, dout);

    NodeTypeValue dinNodeType = new NodeTypeValue(new HashMap<>(), dinPortMap);
    NodeTypeValue doutNodeType = new NodeTypeValue(new HashMap<>(),
        doutPortMap);

    testSchematic.addNodeType(IN_NODE_NAME, dinNodeType);
    testSchematic.addNodeType(OUT_NODE_NAME, doutNodeType);

    // node
    Map<String, Map<String, Value>> inNodeAttr = new HashMap<>();
    inNodeAttr.put(IN_PORT_NAME, new HashMap<>());

    Map<String, Map<String, Value>> outNodeAttr = new HashMap<>();
    outNodeAttr.put(OUT_PORT_NAME, new HashMap<>());

    NodeValue inNode = new NodeValue(dinNodeType, new HashMap<>(), inNodeAttr);

    NodeValue outNode = new NodeValue(doutNodeType, new HashMap<>(),
        outNodeAttr);

    // connection
    ConnectionType conType = new ConnectionType(new HashMap<>());
    ConnectionValue con = new ConnectionValue(conType, inNode
        .getPort(IN_PORT_NAME), outNode.getPort(OUT_PORT_NAME),
        new HashMap<>());

    testSchematic.addConnection(CONNECTION_NAME, con);
  }

  @Test
  public void testSerialize() throws IOException {
    URL url = Resources
        .getResource("org/manifold/compiler/serialization/data/"
            + "deserialization-types-test.json");

    JsonObject json = new JsonParser().parse(
        Resources.toString(url, Charsets.UTF_8)).getAsJsonObject();
    Schematic sch = new SchematicDeserializer().deserialize(json);

    JsonObject result = SchematicSerializer.serialize(sch);

    // fuck this pretty printing
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    JsonParser jp = new JsonParser();
    JsonElement je = jp.parse(result.toString());
    String prettyJsonString = gson.toJson(je);

    System.out.println(prettyJsonString);
  }

  @Test
  public void testDeserialize() throws IOException,
      UndeclaredIdentifierException, UndeclaredAttributeException {
    final String IN_NODE_NAME = "in_node";
    final String OUT_NODE_NAME = "out_node";
    final String DIGITAL_IN_PORT_NAME = "digital_in";
    final String DIGITAL_OUT_PORT_NAME = "digital_out";

    URL url = Resources
        .getResource("org/manifold/compiler/serialization/data/"
            + "deserialization-types-test.json");

    JsonObject json = new JsonParser().parse(
        Resources.toString(url, Charsets.UTF_8)).getAsJsonObject();
    Schematic sch = new SchematicDeserializer().deserialize(json);

    Map<String, PortTypeValue> outNodePorts = sch.getNodeType(OUT_NODE_NAME)
        .getPorts();
    Map<String, PortTypeValue> inNodePorts = sch.getNodeType(IN_NODE_NAME)
        .getPorts();

    PortTypeValue digitalIn = sch.getPortType(DIGITAL_IN_PORT_NAME);
    PortTypeValue digitalOut = sch.getPortType(DIGITAL_OUT_PORT_NAME);

    assertEquals(TEST_SCHEMATIC_NAME, sch.getName());
    assertEquals(digitalIn, inNodePorts.get("in1"));
    assertEquals(digitalIn, inNodePorts.get("in2"));
    assertEquals(digitalOut, outNodePorts.get("out2"));
    assertEquals(digitalOut, outNodePorts.get("out2"));

    NodeValue andNode = sch.getNode("and_node");
    NodeValue andNode2 = sch.getNode("and_node2");

    assertEquals(sch.getNodeType("and"), andNode.getType());
    assertEquals(andNode.getType(), andNode2.getType());
    assertEquals(digitalIn, andNode.getPort("in1").getType());
    assertFalse(((BooleanValue) andNode.getAttribute("is_awesome"))
        .toBoolean());
    assertTrue(((BooleanValue) andNode2.getAttribute("is_awesome"))
        .toBoolean());

    ConnectionValue conVal = sch.getConnection("con1");
    assertEquals(andNode.getPort("out1"), conVal.getFrom());
    assertEquals(andNode2.getPort("in2"), conVal.getTo());
  }
  
  @Test
  public void testSerialize_DerivedPort() {
    fail("not implemented");
  }
  
  @Test
  public void testDeserialize_DerivedPort() 
      throws JsonSyntaxException, IOException, UndeclaredIdentifierException {
    URL url = Resources
        .getResource("org/manifold/compiler/serialization/data/"
            + "deserialization-derived-port-test.json");

    JsonObject json = new JsonParser().parse(
        Resources.toString(url, Charsets.UTF_8)).getAsJsonObject();
    Schematic sch = new SchematicDeserializer().deserialize(json);
    
    final String BASE_PORT_NAME = "basePort";
    final String DERIVED_PORT_NAME = "derivedPort";
    
    PortTypeValue basePort = sch.getPortType(BASE_PORT_NAME);
    PortTypeValue derivedPort = sch.getPortType(DERIVED_PORT_NAME);
    assertTrue(derivedPort.isSubtypeOf(basePort));
  }
  
  @Test
  public void testSerialize_DerivedNode() throws SchematicException {
    // add a derived type to the test schematic
    NodeTypeValue dInDerived = new NodeTypeValue(
        new HashMap<>(), new HashMap<>(), 
        testSchematic.getNodeType(IN_NODE_NAME));
    String derivedNodeName = IN_NODE_NAME + "Derived"; 
    testSchematic.addNodeType(derivedNodeName, dInDerived);
    // serialize, deserialize, check that the type looks okay
    JsonObject result = SchematicSerializer.serialize(testSchematic);
    
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    JsonParser jp = new JsonParser();
    JsonElement je = jp.parse(result.toString());
    String prettyJsonString = gson.toJson(je);

    System.out.println(prettyJsonString);
    
    Schematic sch = new SchematicDeserializer().deserialize(result);
    NodeTypeValue tBase = sch.getNodeType(IN_NODE_NAME);
    NodeTypeValue tDerived = sch.getNodeType(derivedNodeName);
    assertTrue(tDerived.isSubtypeOf(tBase));
  }
  
  @Test
  public void testDeserialize_DerivedNode() 
      throws JsonSyntaxException, IOException, UndeclaredIdentifierException {
    URL url = Resources
        .getResource("org/manifold/compiler/serialization/data/"
            + "deserialization-derived-node-test.json");

    JsonObject json = new JsonParser().parse(
        Resources.toString(url, Charsets.UTF_8)).getAsJsonObject();
    Schematic sch = new SchematicDeserializer().deserialize(json);
    
    final String BASE_NODE_NAME = "baseNode";
    final String DERIVED_NODE_NAME = "derivedNode";
    
    NodeTypeValue baseNode = sch.getNodeType(BASE_NODE_NAME);
    NodeTypeValue derivedNode = sch.getNodeType(DERIVED_NODE_NAME);
    assertTrue(derivedNode.isSubtypeOf(baseNode));
    
  }
  
  @Test
  public void testSerialize_DerivedConnection() {
    fail("not implemented");
  }
  
  @Test
  public void testDeserialize_DerivedConnection() 
      throws JsonSyntaxException, IOException, UndeclaredIdentifierException {
    URL url = Resources
        .getResource("org/manifold/compiler/serialization/data/"
            + "deserialization-derived-connection-test.json");

    JsonObject json = new JsonParser().parse(
        Resources.toString(url, Charsets.UTF_8)).getAsJsonObject();
    Schematic sch = new SchematicDeserializer().deserialize(json);
    
    final String BASE_CONNECTION_NAME = "baseConnection";
    final String DERIVED_CONNECTION_NAME = "derivedConnection";
    
    ConnectionType baseConnection = 
        sch.getConnectionType(BASE_CONNECTION_NAME);
    ConnectionType derivedConnection = 
        sch.getConnectionType(DERIVED_CONNECTION_NAME);
    assertTrue(derivedConnection.isSubtypeOf(baseConnection));
  }
  
  @Test
  public void testSerialize_DerivedConstraint() {
    fail("not implemented");
  }
  
  @Test
  public void testDeserialize_DerivedConstraint() 
      throws JsonSyntaxException, IOException, UndeclaredIdentifierException {
    URL url = Resources
        .getResource("org/manifold/compiler/serialization/data/"
            + "deserialization-derived-constraint-test.json");

    JsonObject json = new JsonParser().parse(
        Resources.toString(url, Charsets.UTF_8)).getAsJsonObject();
    Schematic sch = new SchematicDeserializer().deserialize(json);
    
    final String BASE_CONSTRAINT_NAME = "baseConstraint";
    final String DERIVED_CONSTRAINT_NAME = "derivedConstraint";
    
    ConstraintType baseConstraint = 
        sch.getConstraintType(BASE_CONSTRAINT_NAME);
    ConstraintType derivedConstraint = 
        sch.getConstraintType(DERIVED_CONSTRAINT_NAME);
    assertTrue(derivedConstraint.isSubtypeOf(baseConstraint));
  }
  
}
