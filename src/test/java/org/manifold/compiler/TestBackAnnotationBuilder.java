package org.manifold.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.manifold.compiler.middle.BackAnnotationBuilder;
import org.manifold.compiler.middle.Schematic;
import org.manifold.compiler.middle.SchematicException;

import com.google.common.collect.ImmutableMap;

public class TestBackAnnotationBuilder {

  private static final String TEST_SCHEMATIC_NAME = "dogematics";
  private static final String TEST_TYPE_NAME = "very type";
  private static final String TEST_CONSTRAINT_TYPE_NAME = "much constraint";
  private static final String TEST_NODE_TYPE_NAME = "such node";
  private static final String TEST_PORT_TYPE_NAME = "wow port";
  private static final String TEST_PORT_TYPE_ATTRIBUTE_NAME = "much attributes";

  private static final String NODE_ATTR_FOO = "foo";
  private static final String NODE_ATTR_BAR = "bar";

  private static final String IN_PORT_NAME = "in_port_name";
  private static final String OUT_PORT_NAME = "out_port_name";

  private static final String PORT_ATTR_BAZ = "baz";
  private static final String PORT_ATTR_PLUGH = "plugh";

  private static final String DIGITAL_IN = "digital_in";
  private static final String DIGITAL_OUT = "digital_out";

  private static final String IN_NODE_NAME = "in_node_name";
  private static final String OUT_NODE_NAME = "out_node_name";

  private static final String CONNECTION_NAME = "wire";

  private static final String CONN_ATTR_XYZZY = "xyzzy";
  private static final String CONN_ATTR_ASDF = "asdf";

  private static final String CONSTRAINT_NAME = "rope";

  private Schematic originalSchematic;

  @Before
  public void setup() throws SchematicException {

    originalSchematic = new Schematic(TEST_SCHEMATIC_NAME);

    // UDT
    InferredTypeValue maybeBool = new InferredTypeValue(
        originalSchematic.getUserDefinedType("Bool"));
    UserDefinedTypeValue udtMaybe =
        new UserDefinedTypeValue(maybeBool, "MaybeBool");
    originalSchematic.addUserDefinedType(udtMaybe);

    // port type
    HashMap<String, TypeValue> portDinAttrMap = new HashMap<>();
    portDinAttrMap.put(PORT_ATTR_BAZ,
        originalSchematic.getUserDefinedType("Bool"));
    portDinAttrMap.put(PORT_ATTR_PLUGH,
        originalSchematic.getUserDefinedType("Bool"));
    PortTypeValue din = new PortTypeValue(
        originalSchematic.getUserDefinedType("Bool"), portDinAttrMap);
    PortTypeValue dout = new PortTypeValue(
        originalSchematic.getUserDefinedType("Bool"), new HashMap<>());
    originalSchematic.addPortType(DIGITAL_IN, din);
    originalSchematic.addPortType(DIGITAL_OUT, dout);

    // node type
    HashMap<String, TypeValue> dinAttrMap = new HashMap<>();
    dinAttrMap.put(NODE_ATTR_FOO, originalSchematic.getUserDefinedType("Bool"));
    dinAttrMap.put(NODE_ATTR_BAR, maybeBool);

    HashMap<String, PortTypeValue> dinPortMap = new HashMap<>();
    dinPortMap.put(IN_PORT_NAME, din);

    HashMap<String, PortTypeValue> doutPortMap = new HashMap<>();
    doutPortMap.put(OUT_PORT_NAME, dout);

    NodeTypeValue dinNodeType = new NodeTypeValue(dinAttrMap, dinPortMap);
    NodeTypeValue doutNodeType = new NodeTypeValue(new HashMap<>(),
        doutPortMap);

    originalSchematic.addNodeType(IN_NODE_NAME, dinNodeType);
    originalSchematic.addNodeType(OUT_NODE_NAME, doutNodeType);

    // node
    Map<String, Value> inNodeAttrs = new HashMap<>();
    inNodeAttrs.put(NODE_ATTR_FOO, BooleanValue.getInstance(true));
    inNodeAttrs.put(NODE_ATTR_BAR, new InferredValue(
        maybeBool, BooleanValue.getInstance(true)));

    Map<String, Map<String, Value>> inNodePortAttrs = new HashMap<>();
    Map<String, Value> inPortAttrs = new HashMap<>();
    inPortAttrs.put(PORT_ATTR_BAZ, BooleanValue.getInstance(false));
    inPortAttrs.put(PORT_ATTR_PLUGH, BooleanValue.getInstance(false));
    inNodePortAttrs.put(IN_PORT_NAME, inPortAttrs);

    Map<String, Map<String, Value>> outNodePortAttrs = new HashMap<>();
    outNodePortAttrs.put(OUT_PORT_NAME, new HashMap<>());

    NodeValue inNode = new NodeValue(dinNodeType, inNodeAttrs, inNodePortAttrs);
    originalSchematic.addNode("nIN", inNode);

    NodeValue outNode = new NodeValue(doutNodeType, new HashMap<>(),
        outNodePortAttrs);
    originalSchematic.addNode("nOUT", outNode);

    Map<String, Value> connAttrs = new HashMap<>();
    connAttrs.put(CONN_ATTR_ASDF, BooleanValue.getInstance(false));
    connAttrs.put(CONN_ATTR_XYZZY, BooleanValue.getInstance(false));
    ConnectionValue con = new ConnectionValue(inNode
        .getPort(IN_PORT_NAME), outNode.getPort(OUT_PORT_NAME),
        connAttrs);

    originalSchematic.addConnection(CONNECTION_NAME, con);

    // constraint
    TypeValue stringType = originalSchematic.getUserDefinedType("String");
    ConstraintType constraintType = new ConstraintType(ImmutableMap.of(
        "foo", stringType,
        "din_reference", dinNodeType,
        "port_reference", din,
        "conn_reference", ConnectionTypeValue.getInstance()));
    originalSchematic.addConstraintType(TEST_CONSTRAINT_TYPE_NAME, constraintType);

    ConstraintValue constraintValue = new ConstraintValue(constraintType,
        ImmutableMap.of(
            "foo", new StringValue(stringType, "bar"),
            "din_reference", inNode,
            "port_reference", inNode.getPort(IN_PORT_NAME),
            "conn_reference", con));

    originalSchematic.addConstraint("c1", constraintValue);
  }

  @Test
  public void testPassthrough() throws SchematicException {
    // If we don't make any annotations, the builder should give us back
    // a schematic whose nodes, ports, connections, and constraints
    // have the same attributes as the one that we started with.

    BackAnnotationBuilder builder =
        new BackAnnotationBuilder(originalSchematic);
    Schematic modifiedSchematic = builder.build();

    NodeValue inNodeOriginal = originalSchematic.getNode("nIN");
    NodeValue inNodeModified = modifiedSchematic.getNode("nIN");
    assertTrue(inNodeOriginal.getAttributes().
        equals(inNodeModified.getAttributes()));

    PortValue inPortOriginal = inNodeOriginal.getPort(IN_PORT_NAME);
    PortValue inPortModified = inNodeModified.getPort(IN_PORT_NAME);
    assertTrue(inPortOriginal.getAttributes().
        equals(inPortModified.getAttributes()));

    ConnectionValue inConnOriginal = originalSchematic.
        getConnection(CONNECTION_NAME);
    ConnectionValue inConnModified = modifiedSchematic.
        getConnection(CONNECTION_NAME);
    assertTrue(inConnOriginal.getAttributes().
        equals(inConnModified.getAttributes()));
  }

  @Test
  public void testModifyNodeAttribute() throws SchematicException {
    // Modify the "foo" attribute on node "nIN" to be False instead of True.

    NodeValue inOriginal = originalSchematic.getNode("nIN");
    assertTrue("precondition failed",
        inOriginal.getAttribute(NODE_ATTR_FOO).equals(
            BooleanValue.getInstance(true)));

    BackAnnotationBuilder builder =
        new BackAnnotationBuilder(originalSchematic);
    builder.annotateNodeAttribute("nIN", NODE_ATTR_FOO,
        BooleanValue.getInstance(false));
    Schematic modifiedSchematic = builder.build();

    NodeValue inModified = modifiedSchematic.getNode("nIN");
    assertTrue("back-annotation failed",
        inModified.getAttribute(NODE_ATTR_FOO).equals(
            BooleanValue.getInstance(false)));
  }

  @Test
  public void testModifyNodeAttribute_PreservesOthers()
      throws SchematicException {
    // Check that modifying one node attribute doesn't modify any others.

    NodeValue inOriginal = originalSchematic.getNode("nIN");
    InferredValue vBar = (InferredValue) inOriginal.getAttribute(NODE_ATTR_BAR);
    assertTrue("precondition failed",
        vBar.get().equals(
            BooleanValue.getInstance(true)));

    BackAnnotationBuilder builder =
        new BackAnnotationBuilder(originalSchematic);
    builder.annotateNodeAttribute("nIN", NODE_ATTR_FOO,
        BooleanValue.getInstance(false));
    Schematic modifiedSchematic = builder.build();

    NodeValue inModified = modifiedSchematic.getNode("nIN");
    vBar = (InferredValue) inModified.getAttribute(NODE_ATTR_BAR);
    assertTrue("back-annotation smashed unmodified node attribute",
        vBar.get().equals(
            BooleanValue.getInstance(true)));
  }

  @Test(expected = UndeclaredIdentifierException.class)
  public void testModifyNodeAttribute_NodeDoesNotExist()
      throws SchematicException {
    BackAnnotationBuilder builder =
        new BackAnnotationBuilder(originalSchematic);
    builder.annotateNodeAttribute("nBOGUS", NODE_ATTR_FOO,
        BooleanValue.getInstance(false));
  }

  @Test(expected = UndeclaredAttributeException.class)
  public void testModifyNodeAttribute_AttributeDoesNotExist()
      throws SchematicException {
    BackAnnotationBuilder builder =
        new BackAnnotationBuilder(originalSchematic);
    builder.annotateNodeAttribute("nIN", "Bogus_Attribute",
        BooleanValue.getInstance(false));
  }

  @Test(expected = TypeMismatchException.class)
  public void testModifyNodeAttribute_ValueHasIncompatibleType()
      throws SchematicException {
    BackAnnotationBuilder builder =
        new BackAnnotationBuilder(originalSchematic);
    builder.annotateNodeAttribute("nIN", NODE_ATTR_FOO, new IntegerValue(13));
  }

  @Test
  public void testModifyPortAttribute()
      throws SchematicException {
    // Modify the "baz" attribute on nIN:in_port_name to be True instead of False.

    PortValue inOriginal = originalSchematic.getNode("nIN").
        getPort(IN_PORT_NAME);
    assertTrue("precondition failed",
        inOriginal.getAttribute(PORT_ATTR_BAZ).equals(
            BooleanValue.getInstance(false)));

    BackAnnotationBuilder builder =
        new BackAnnotationBuilder(originalSchematic);
    builder.annotatePortAttribute("nIN", IN_PORT_NAME, PORT_ATTR_BAZ,
        BooleanValue.getInstance(true));
    Schematic modifiedSchematic = builder.build();

    PortValue inModified = modifiedSchematic.getNode("nIN").
        getPort(IN_PORT_NAME);
    assertTrue("back-annotation failed",
        inModified.getAttribute(PORT_ATTR_BAZ).equals(
            BooleanValue.getInstance(true)));
  }

  @Test
  public void testModifyPortAttribute_PreservesOthers()
      throws SchematicException {
    // Check that modifying one port attribute doesn't modify any others.
    PortValue inOriginal = originalSchematic.getNode("nIN").
        getPort(IN_PORT_NAME);
    assertTrue("precondition failed",
        inOriginal.getAttribute(PORT_ATTR_PLUGH).equals(
            BooleanValue.getInstance(false)));

    BackAnnotationBuilder builder =
        new BackAnnotationBuilder(originalSchematic);
    builder.annotatePortAttribute("nIN", IN_PORT_NAME, PORT_ATTR_BAZ,
        BooleanValue.getInstance(true));
    Schematic modifiedSchematic = builder.build();

    PortValue inModified = modifiedSchematic.getNode("nIN").
        getPort(IN_PORT_NAME);
    // > Nothing happens.
    assertTrue("back-annotation smashed unmodified port attribute",
        inModified.getAttribute(PORT_ATTR_PLUGH).equals(
            BooleanValue.getInstance(false)));
  }

  @Test(expected = UndeclaredIdentifierException.class)
  public void testModifyPortAttribute_NodeDoesNotExist()
      throws SchematicException {
    BackAnnotationBuilder builder =
        new BackAnnotationBuilder(originalSchematic);
    builder.annotatePortAttribute("nBOGUS", IN_PORT_NAME, PORT_ATTR_BAZ,
        BooleanValue.getInstance(false));
  }

  @Test(expected = UndeclaredIdentifierException.class)
  public void testModifyPortAttribute_PortDoesNotExist()
      throws SchematicException {
    BackAnnotationBuilder builder =
        new BackAnnotationBuilder(originalSchematic);
    builder.annotatePortAttribute("nIN", "pBOGUS", PORT_ATTR_BAZ,
        BooleanValue.getInstance(false));
  }

  @Test(expected = UndeclaredAttributeException.class)
  public void testModifyPortAttribute_AttributeDoesNotExist()
      throws SchematicException {
    BackAnnotationBuilder builder =
        new BackAnnotationBuilder(originalSchematic);
    builder.annotatePortAttribute("nIN", IN_PORT_NAME, "attrBOGUS",
        BooleanValue.getInstance(false));
  }

  @Test(expected = TypeMismatchException.class)
  public void testModifyPortAttribute_ValueHasIncompatibleType()
      throws SchematicException {
    BackAnnotationBuilder builder =
        new BackAnnotationBuilder(originalSchematic);
    builder.annotatePortAttribute("nIN", IN_PORT_NAME, PORT_ATTR_BAZ,
        new IntegerValue(13));
  }

  @Test
  public void testModifyConnectionAttribute()
      throws SchematicException {
    // Modify the "xyzzy" attribute on connection "wire" to be False instead of True.

    ConnectionValue cOriginal = originalSchematic.getConnection(
        CONNECTION_NAME);
    assertTrue("precondition failed",
        cOriginal.getAttribute(CONN_ATTR_XYZZY).equals(
            BooleanValue.getInstance(false)));

    BackAnnotationBuilder builder =
        new BackAnnotationBuilder(originalSchematic);
    builder.annotateConnectionAttribute(CONNECTION_NAME, CONN_ATTR_XYZZY,
        BooleanValue.getInstance(true));
    Schematic modifiedSchematic = builder.build();

    ConnectionValue cModified = modifiedSchematic.getConnection(
        CONNECTION_NAME);
    assertTrue("back-annotation failed",
        cModified.getAttribute(CONN_ATTR_XYZZY).equals(
            BooleanValue.getInstance(true)));
  }

  @Test
  public void testModifyConnectionAttribute_PreservesOthers()
      throws SchematicException {
    // Check that modifying one connection attribute doesn't modify any others.

    ConnectionValue cOriginal = originalSchematic.getConnection(
        CONNECTION_NAME);
    assertTrue("precondition failed",
        cOriginal.getAttribute(CONN_ATTR_ASDF).equals(
            BooleanValue.getInstance(false)));

    BackAnnotationBuilder builder =
        new BackAnnotationBuilder(originalSchematic);
    builder.annotateConnectionAttribute(CONNECTION_NAME, CONN_ATTR_XYZZY,
        BooleanValue.getInstance(true));
    Schematic modifiedSchematic = builder.build();

    ConnectionValue cModified = modifiedSchematic.getConnection(
        CONNECTION_NAME);
    assertTrue("back-annotation smashed unmodified connection attribute",
        cModified.getAttribute(CONN_ATTR_ASDF).equals(
            BooleanValue.getInstance(false)));
  }

  @Test(expected = UndeclaredIdentifierException.class)
  public void testModifyConnectionAttribute_ConnectionDoesNotExist()
      throws SchematicException {
    BackAnnotationBuilder builder =
        new BackAnnotationBuilder(originalSchematic);
    builder.annotateConnectionAttribute("cBOGUS", CONN_ATTR_XYZZY,
        BooleanValue.getInstance(false));
  }

  @Test
  public void testModifyConstraintAttribute()
      throws SchematicException {
    // Modify the "foo" attribute on constraint "c1" to be the string "xyzzy".
    TypeValue stringType = originalSchematic.getUserDefinedType("String");

    StringValue strOriginal = (StringValue) originalSchematic
        .getConstraint("c1").getAttribute("foo");
    assertEquals("precondition failed", "bar", strOriginal.toString());

    BackAnnotationBuilder builder =
        new BackAnnotationBuilder(originalSchematic);
    builder.annotateConstraintAttribute("c1", "foo",
        new StringValue(stringType, "xyzzy"));
    Schematic modifiedSchematic = builder.build();

    StringValue strNew = (StringValue) modifiedSchematic
        .getConstraint("c1").getAttribute("foo");
    assertEquals("back-annotation failed", "xyzzy", strNew.toString());

  }

  @Test
  public void testCopyConstraint_RecreatedNode()
      throws SchematicException {
    // Check that a constraint referencing a node actually refers
    // to the correct node in the backannotated copy.
    BackAnnotationBuilder builder =
        new BackAnnotationBuilder(originalSchematic);
    Schematic modifiedSchematic = builder.build();

    NodeValue expectedNode = modifiedSchematic.getNode("nIN");
    ConstraintValue cxt = modifiedSchematic.getConstraint("c1");
    NodeValue actualNode = (NodeValue) cxt.getAttribute("din_reference");
    assertEquals(expectedNode, actualNode);
  }

  @Test
  public void testCopyConstraint_RecreatedPort()
      throws SchematicException {
    // Check that a constraint referencing a port actually refers
    // to the correct port in the backannotated copy.
    BackAnnotationBuilder builder =
        new BackAnnotationBuilder(originalSchematic);
    Schematic modifiedSchematic = builder.build();

    PortValue expectedPort = modifiedSchematic.getNode("nIN")
        .getPort(IN_PORT_NAME);
    ConstraintValue cxt = modifiedSchematic.getConstraint("c1");
    PortValue actualPort = (PortValue) cxt.getAttribute("port_reference");
    assertEquals(expectedPort, actualPort);
  }

  @Test
  public void testCopyConstraint_RecreatedConnection()
      throws SchematicException {
    // Check that a constraint referencing a connection actually refers
    // to the correct connection in the backannotated copy.
    BackAnnotationBuilder builder =
        new BackAnnotationBuilder(originalSchematic);
    Schematic modifiedSchematic = builder.build();

    ConnectionValue expectedConn = modifiedSchematic
        .getConnection(CONNECTION_NAME);
    ConstraintValue cxt = modifiedSchematic.getConstraint("c1");
    ConnectionValue actualConn = (ConnectionValue)
        cxt.getAttribute("conn_reference");
    assertEquals(expectedConn, actualConn);
  }

  @Test(expected = UndeclaredIdentifierException.class)
  public void testModifyConstraintAttribute_ConstraintDoesNotExist()
      throws SchematicException {
    BackAnnotationBuilder builder =
        new BackAnnotationBuilder(originalSchematic);
    builder.annotateConstraintAttribute("nBOGUS", NODE_ATTR_FOO,
        BooleanValue.getInstance(false));
  }

  @Test(expected = UndeclaredAttributeException.class)
  public void testModifyConstraintAttribute_AttributeDoesNotExist()
      throws SchematicException {
    BackAnnotationBuilder builder =
        new BackAnnotationBuilder(originalSchematic);
    builder.annotateConstraintAttribute("c1", "Bogus_Attribute",
        BooleanValue.getInstance(false));
  }

  @Test(expected = TypeMismatchException.class)
  public void testModifyConstraintAttribute_ValueHasIncompatibleType()
      throws SchematicException {
    BackAnnotationBuilder builder =
        new BackAnnotationBuilder(originalSchematic);
    builder.annotateConstraintAttribute("c1", "foo", new IntegerValue(13));
  }

  @Test
  public void testModifyNodeAttribute_ToInferred()
      throws SchematicException {
    // Modify the "iBaz" attribute on node "nIN" to be <Inferred> instead of True

    NodeValue inOriginal = originalSchematic.getNode("nIN");
    InferredValue vBar = (InferredValue) inOriginal.getAttribute(NODE_ATTR_BAR);
    assertTrue("precondition failed",
        vBar.get().equals(
            BooleanValue.getInstance(true)));

    InferredTypeValue maybeBool = new InferredTypeValue(
        originalSchematic.getUserDefinedType("Bool"));
    Value newValue = new InferredValue(maybeBool);

    BackAnnotationBuilder builder =
        new BackAnnotationBuilder(originalSchematic);
    builder.annotateNodeAttribute("nIN", NODE_ATTR_BAR, newValue);
    Schematic modifiedSchematic = builder.build();

    NodeValue inModified = modifiedSchematic.getNode("nIN");
    vBar = (InferredValue) inModified.getAttribute(NODE_ATTR_BAR);
    assertFalse("back-annotation failed", vBar.get() == null);
  }

}
