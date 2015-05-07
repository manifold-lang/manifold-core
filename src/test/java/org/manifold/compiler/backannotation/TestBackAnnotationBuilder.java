package org.manifold.compiler.backannotation;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.manifold.compiler.BooleanValue;
import org.manifold.compiler.ConnectionValue;
import org.manifold.compiler.ConstraintType;
import org.manifold.compiler.ConstraintValue;
import org.manifold.compiler.NodeTypeValue;
import org.manifold.compiler.NodeValue;
import org.manifold.compiler.PortTypeValue;
import org.manifold.compiler.StringValue;
import org.manifold.compiler.TypeValue;
import org.manifold.compiler.Value;
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

  private static final String IN_PORT_NAME = "in_port_name";
  private static final String OUT_PORT_NAME = "out_port_name";

  private static final String DIGITAL_IN = "digital_in";
  private static final String DIGITAL_OUT = "digital_out";

  private static final String IN_NODE_NAME = "in_node_name";
  private static final String OUT_NODE_NAME = "out_node_name";

  private static final String CONNECTION_NAME = "wire";

  private static final String CONSTRAINT_NAME = "rope";

  private Schematic originalSchematic;

  @Before
  public void setup() throws SchematicException {

    originalSchematic = new Schematic(TEST_SCHEMATIC_NAME);

    // port type
    PortTypeValue din = new PortTypeValue(
        originalSchematic.getUserDefinedType("Bool"), new HashMap<>());
    PortTypeValue dout = new PortTypeValue(
        originalSchematic.getUserDefinedType("Bool"), new HashMap<>());
    originalSchematic.addPortType(DIGITAL_IN, din);
    originalSchematic.addPortType(DIGITAL_OUT, dout);

    // node type
    HashMap<String, TypeValue> dinAttrMap = new HashMap<>();
    dinAttrMap.put(NODE_ATTR_FOO, originalSchematic.getUserDefinedType("Bool"));

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

    Map<String, Map<String, Value>> inNodePortAttrs = new HashMap<>();
    inNodePortAttrs.put(IN_PORT_NAME, new HashMap<>());

    Map<String, Map<String, Value>> outNodePortAttrs = new HashMap<>();
    outNodePortAttrs.put(OUT_PORT_NAME, new HashMap<>());

    NodeValue inNode = new NodeValue(dinNodeType, inNodeAttrs, inNodePortAttrs);
    originalSchematic.addNode("nIN", inNode);

    NodeValue outNode = new NodeValue(doutNodeType, new HashMap<>(),
        outNodePortAttrs);
    originalSchematic.addNode("nOUT", outNode);

    ConnectionValue con = new ConnectionValue(inNode
        .getPort(IN_PORT_NAME), outNode.getPort(OUT_PORT_NAME),
        new HashMap<>());

    originalSchematic.addConnection(CONNECTION_NAME, con);

    // constraint
    TypeValue stringType = originalSchematic.getUserDefinedType("String");
    ConstraintType constraintType = new ConstraintType(ImmutableMap.of(
        "foo", stringType,
        "din_reference", dinNodeType,
        "port_reference", din));
    originalSchematic.addConstraintType(TEST_CONSTRAINT_TYPE_NAME, constraintType);

    ConstraintValue constraintValue = new ConstraintValue(constraintType,
        ImmutableMap.of(
            "foo", new StringValue(stringType, "bar"),
            "din_reference", inNode,
            "port_reference", inNode.getPort(IN_PORT_NAME)));

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

    NodeValue inOriginal = originalSchematic.getNode("nIN");
    NodeValue inModified = modifiedSchematic.getNode("nIN");

    assertTrue(inOriginal.getAttributes().equals(inModified.getAttributes()));
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

}
