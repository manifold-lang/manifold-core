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
import org.manifold.compiler.ArrayTypeValue;
import org.manifold.compiler.BooleanTypeValue;
import org.manifold.compiler.BooleanValue;
import org.manifold.compiler.ConnectionValue;
import org.manifold.compiler.ConstraintType;
import org.manifold.compiler.InferredValue;
import org.manifold.compiler.MultipleDefinitionException;
import org.manifold.compiler.NodeTypeValue;
import org.manifold.compiler.NodeValue;
import org.manifold.compiler.PortTypeValue;
import org.manifold.compiler.TypeValue;
import org.manifold.compiler.UndeclaredAttributeException;
import org.manifold.compiler.UndeclaredIdentifierException;
import org.manifold.compiler.UserDefinedTypeValue;
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

  private static final String CONSTRAINT_NAME = "rope";

  private Schematic testSchematic;

  @Before
  public void setup() throws SchematicException {

    testSchematic = new Schematic(TEST_SCHEMATIC_NAME);

    // port type
    PortTypeValue din = new PortTypeValue(
        testSchematic.getUserDefinedType("Bool"), new HashMap<>());
    PortTypeValue dout = new PortTypeValue(
        testSchematic.getUserDefinedType("Bool"), new HashMap<>());
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
    testSchematic.addNode("nIN", inNode);

    NodeValue outNode = new NodeValue(doutNodeType, new HashMap<>(),
        outNodeAttr);
    testSchematic.addNode("nOUT", outNode);

    ConnectionValue con = new ConnectionValue(inNode
        .getPort(IN_PORT_NAME), outNode.getPort(OUT_PORT_NAME),
        new HashMap<>());

    testSchematic.addConnection(CONNECTION_NAME, con);

    // constraint
    ConstraintType constraintType = new ConstraintType(new HashMap<>());
    testSchematic.addConstraintType(TEST_CONSTRAINT_TYPE_NAME, constraintType);

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

    assertTrue(sch.getUserDefinedType("Flag")
        .isSubtypeOf(BooleanTypeValue.getInstance()));

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
  public void testSerialize_DerivedPort()
      throws UndeclaredIdentifierException, MultipleDefinitionException {
    // add a derived port type to the test schematic
    PortTypeValue dPortDerived = new PortTypeValue(
        testSchematic.getUserDefinedType("Bool"), new HashMap<>(),
        testSchematic.getPortType(DIGITAL_IN));
    String derivedPortName = DIGITAL_IN + "Derived";
    testSchematic.addPortType(derivedPortName, dPortDerived);
    // serialize, deserialize, check that the type looks okay
    JsonObject result = SchematicSerializer.serialize(testSchematic);

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    JsonParser jp = new JsonParser();
    JsonElement je = jp.parse(result.toString());
    String prettyJsonString = gson.toJson(je);

    System.out.println(prettyJsonString);

    Schematic sch = new SchematicDeserializer().deserialize(result);
    PortTypeValue tBase = sch.getPortType(DIGITAL_IN);
    PortTypeValue tDerived = sch.getPortType(derivedPortName);
    assertTrue(tDerived.isSubtypeOf(tBase));
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
    NodeTypeValue dNodeDerived = new NodeTypeValue(
        new HashMap<>(), new HashMap<>(),
        testSchematic.getNodeType(IN_NODE_NAME));
    String derivedNodeName = IN_NODE_NAME + "Derived";
    testSchematic.addNodeType(derivedNodeName, dNodeDerived);
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
  public void testSerialize_DerivedConstraint()
      throws UndeclaredIdentifierException, MultipleDefinitionException {
    // add a derived constraint type to the test schematic
    ConstraintType dConDerived = new ConstraintType(new HashMap<>(),
        testSchematic.getConstraintType(TEST_CONSTRAINT_TYPE_NAME));
    String derivedConstraintName = TEST_CONSTRAINT_TYPE_NAME + "Derived";
    testSchematic.addConstraintType(derivedConstraintName, dConDerived);
    // serialize, deserialize, check that the type looks okay
    JsonObject result = SchematicSerializer.serialize(testSchematic);

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    JsonParser jp = new JsonParser();
    JsonElement je = jp.parse(result.toString());
    String prettyJsonString = gson.toJson(je);

    System.out.println(prettyJsonString);

    Schematic sch = new SchematicDeserializer().deserialize(result);
    ConstraintType tBase = sch.getConstraintType(TEST_CONSTRAINT_TYPE_NAME);
    ConstraintType tDerived = sch.getConstraintType(derivedConstraintName);
    assertTrue(tDerived.isSubtypeOf(tBase));
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

  @Test
  public void testSerialize_UserDefinedArray()
      throws SchematicException {
    // add an array UDT to the test schematic
    TypeValue bitvectorType = new ArrayTypeValue(
        testSchematic.getUserDefinedType("Bool"));
    String typename = "Bitvector";
    testSchematic.addUserDefinedType(typename,
        new UserDefinedTypeValue(bitvectorType));
    // serialize, deserialize, check that the type looks okay
    JsonObject result = SchematicSerializer.serialize(testSchematic);

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    JsonParser jp = new JsonParser();
    JsonElement je = jp.parse(result.toString());
    String prettyJsonString = gson.toJson(je);

    System.out.println(prettyJsonString);

    Schematic sch = new SchematicDeserializer().deserialize(result);
    try {
      UserDefinedTypeValue udt = sch.getUserDefinedType(typename);
      assertTrue(udt.getTypeAlias() instanceof ArrayTypeValue);
      ArrayTypeValue arrayType = (ArrayTypeValue) udt.getTypeAlias();
      assertTrue(arrayType.getElementType()
          .isSubtypeOf(BooleanTypeValue.getInstance()));
    } catch (UndeclaredIdentifierException e) {
      fail("undeclared identifier '" + e.getIdentifier() + "'; "
          + "the user-defined type may not have been serialized");
    }
  }

  @Test
  public void testDeserialize_UserDefinedArray()
      throws JsonSyntaxException, IOException, UndeclaredIdentifierException {
    URL url = Resources
        .getResource("org/manifold/compiler/serialization/data/"
            + "deserialization-udt-array.json");

    JsonObject json = new JsonParser().parse(
        Resources.toString(url, Charsets.UTF_8)).getAsJsonObject();
    Schematic sch = new SchematicDeserializer().deserialize(json);

    final String UDT_TYPENAME = "Bitvector";
    UserDefinedTypeValue udt = sch.getUserDefinedType(UDT_TYPENAME);
    assertTrue(udt.getTypeAlias() instanceof ArrayTypeValue);
    ArrayTypeValue arrayType = (ArrayTypeValue) udt.getTypeAlias();
    assertTrue(arrayType.getElementType()
        .isSubtypeOf(BooleanTypeValue.getInstance()));
  }

  @Test
  public void testDeserializeInferredAttributes() throws IOException,
      UndeclaredIdentifierException, UndeclaredAttributeException {
    final String IN_NODE_NAME = "in_node";
    final String OUT_NODE_NAME = "out_node";
    final String DIGITAL_IN_PORT_NAME = "digital_in";
    final String DIGITAL_OUT_PORT_NAME = "digital_out";

    URL url = Resources
        .getResource("org/manifold/compiler/serialization/data/"
            + "deserialization-inferred-attributes-test.json");

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

    assertEquals(BooleanValue.getInstance(false),
        ((InferredValue) andNode.getAttribute("is_awesome")).get());
    assertEquals(null,
        ((InferredValue) andNode2.getAttribute("is_awesome")).get());

    ConnectionValue conVal = sch.getConnection("con1");
    assertEquals(andNode.getPort("out1"), conVal.getFrom());
    assertEquals(andNode2.getPort("in2"), conVal.getTo());

    InferredValue foom = (InferredValue) andNode.getPort("out1")
        .getAttributes().get("foom");
    InferredValue foom2 = (InferredValue) andNode2.getPort("out1")
        .getAttributes().get("foom");
    assertEquals(null, foom.get());
    assertEquals(BooleanValue.getInstance(true), foom2.get());
  }

  @Test
  public void regressionTestDeserialize_UndeclaredNodeAttributeInst_correct()
      throws JsonSyntaxException, IOException {
    URL url = Resources
        .getResource("org/manifold/compiler/serialization/data/"
            + "node_attribute_undeclared_instantiation_positive.json");

    JsonObject json = new JsonParser().parse(
        Resources.toString(url, Charsets.UTF_8)).getAsJsonObject();
    Schematic sch = new SchematicDeserializer().deserialize(json);
  }

  @Test
  public void regressionTestDeserialize_UndeclaredNodeAttributeInst_incorrect()
    throws JsonSyntaxException, IOException {
    URL url = Resources
        .getResource("org/manifold/compiler/serialization/data/"
            + "node_attribute_undeclared_instantiation_negative.json");

    JsonObject json = new JsonParser().parse(
        Resources.toString(url, Charsets.UTF_8)).getAsJsonObject();
    try {
      Schematic sch = new SchematicDeserializer().deserialize(json);
      fail("deserialization failed to detect incorrect schematic");
    } catch (RuntimeException e) {
      if (e.getCause() instanceof UndeclaredAttributeException) {
        UndeclaredAttributeException uae =
            (UndeclaredAttributeException) e.getCause();
        assertEquals("initialValue", uae.name);
      } else {
        fail(e.getMessage());
      }
    }
  }

  @Test
  public void regressionTestDeserialize_UndeclaredNodeAttributeType_correct()
      throws JsonSyntaxException, IOException {
    URL url = Resources
        .getResource("org/manifold/compiler/serialization/data/"
            + "node_attribute_undeclared_type_positive.json");

    JsonObject json = new JsonParser().parse(
        Resources.toString(url, Charsets.UTF_8)).getAsJsonObject();
    Schematic sch = new SchematicDeserializer().deserialize(json);
  }

  @Test
  public void regressionTestDeserialize_UndeclaredNodeAttributeType_incorrect()
    throws JsonSyntaxException, IOException {
    URL url = Resources
        .getResource("org/manifold/compiler/serialization/data/"
            + "node_attribute_undeclared_type_negative.json");

    JsonObject json = new JsonParser().parse(
        Resources.toString(url, Charsets.UTF_8)).getAsJsonObject();
    try {
      Schematic sch = new SchematicDeserializer().deserialize(json);
      fail("deserialization failed to detect incorrect schematic");
    } catch (RuntimeException e) {
      if (e.getCause() instanceof UndeclaredAttributeException) {
        UndeclaredAttributeException uae =
            (UndeclaredAttributeException) e.getCause();
        assertEquals("test1", uae.name);
      } else {
        fail(e.getMessage());
      }
    }
  }

  @Test
  public void regressionTestDeserialize_UndeclaredPortAttributeInst_correct()
      throws JsonSyntaxException, IOException {
    URL url = Resources
        .getResource("org/manifold/compiler/serialization/data/"
            + "port_attribute_undeclared_instantiation_positive.json");

    JsonObject json = new JsonParser().parse(
        Resources.toString(url, Charsets.UTF_8)).getAsJsonObject();
    Schematic sch = new SchematicDeserializer().deserialize(json);
  }

  @Test
  public void regressionTestDeserialize_UndeclaredPortAttributeInst_incorrect()
    throws JsonSyntaxException, IOException {
    URL url = Resources
        .getResource("org/manifold/compiler/serialization/data/"
            + "port_attribute_undeclared_instantiation_negative.json");

    JsonObject json = new JsonParser().parse(
        Resources.toString(url, Charsets.UTF_8)).getAsJsonObject();
    try {
      Schematic sch = new SchematicDeserializer().deserialize(json);
      fail("deserialization failed to detect incorrect schematic");
    } catch (RuntimeException e) {
      if (e.getCause() instanceof UndeclaredAttributeException) {
        UndeclaredAttributeException uae =
            (UndeclaredAttributeException) e.getCause();
        assertEquals("extra", uae.name);
      } else {
        fail(e.getMessage());
      }
    }
  }

  @Test
  public void regressionTestDeserialize_UndeclaredPortAttributeType()
      throws JsonSyntaxException, IOException {
    URL url = Resources
        .getResource("org/manifold/compiler/serialization/data/"
            + "port_attribute_undeclared_type_positive.json");

    JsonObject json = new JsonParser().parse(
        Resources.toString(url, Charsets.UTF_8)).getAsJsonObject();
    Schematic sch = new SchematicDeserializer().deserialize(json);
  }

}
