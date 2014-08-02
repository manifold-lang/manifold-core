package org.manifold.compiler.serialization;

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.manifold.compiler.ConnectionType;
import org.manifold.compiler.ConnectionValue;
import org.manifold.compiler.NodeTypeValue;
import org.manifold.compiler.NodeValue;
import org.manifold.compiler.PortTypeValue;
import org.manifold.compiler.UndeclaredIdentifierException;
import org.manifold.compiler.Value;
import org.manifold.compiler.middle.Schematic;
import org.manifold.compiler.middle.SchematicException;
import org.manifold.compiler.middle.serialization.SchematicDeserializer;
import org.manifold.compiler.middle.serialization.SchematicSerializer;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
    NodeTypeValue doutNodeType = new NodeTypeValue(new HashMap<>(), doutPortMap);

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
        .getPort(IN_PORT_NAME), outNode.getPort(OUT_PORT_NAME), new HashMap<>());

    testSchematic.addConnection(CONNECTION_NAME, con);
  }

  @Ignore
  @Test
  public void testSerialize() throws IOException {
    StringWriter outbuffer = new StringWriter();
    new SchematicSerializer().serialize(testSchematic, new BufferedWriter(
        outbuffer), true);
    String result = outbuffer.toString();
  }

  @Test
  public void testDeserialize() throws IOException,
      UndeclaredIdentifierException {
    final String IN_NODE_NAME = "in_node";
    final String OUT_NODE_NAME = "out_node";
    final String DIGITAL_IN_PORT_NAME = "digital_in";
    final String DIGITAL_OUT_PORT_NAME = "digital_out";

    URL url = this.getClass().getResource(
        "data/deserialization-types-test.json");
    JsonObject json = new JsonParser().parse(
        Resources.toString(url, Charsets.UTF_8)).getAsJsonObject();
    Schematic sch = new SchematicDeserializer().deserialize(json);

    Map<String, PortTypeValue> outNodePorts = sch.getNodeType(OUT_NODE_NAME)
        .getPorts();
    Map<String, PortTypeValue> inNodePorts = sch.getNodeType(IN_NODE_NAME)
        .getPorts();

    PortTypeValue digitalIn = sch.getPortType(DIGITAL_IN_PORT_NAME);
    PortTypeValue digitalOut = sch.getPortType(DIGITAL_OUT_PORT_NAME);

    assertEquals("dogematics", sch.getName());
    assertEquals(sch.getUserDefinedType("Int"), digitalIn.getAttributes().get(
        "width"));

    assertEquals(digitalIn, inNodePorts.get("in1"));
    assertEquals(digitalIn, inNodePorts.get("in2"));
    assertEquals(digitalOut, outNodePorts.get("out2"));
    assertEquals(digitalOut, outNodePorts.get("out2"));
  }
}
