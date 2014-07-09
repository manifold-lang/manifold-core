package org.manifold.compiler;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import org.manifold.compiler.back.SchematicException;

public class TestNode {

  private static final PortTypeValue defaultPortDefinition =
      new PortTypeValue(new HashMap<>());
  private static final TypeValue boolType = BooleanTypeValue.getInstance();
  private static final String PORT_NAME = "testport";
  private static final String PORT_ATTR_KEY = "the truth will set you free";
  private static final Map<String, Map<String, Value>> PORT_ATTRS =
      ImmutableMap.of(PORT_NAME, ImmutableMap.of());
  
  private NodeTypeValue hasNoAttrs;
  private NodeTypeValue hasABCNodeAttr;
  
  @Before
  public void setup() {
    Map<String, PortTypeValue> portMap =
        ImmutableMap.of(PORT_NAME, defaultPortDefinition);
    hasNoAttrs = new NodeTypeValue(new HashMap<>(), portMap);
    hasABCNodeAttr = new NodeTypeValue(
        ImmutableMap.of("abc", boolType),
        portMap
    );
  }

  @Test(
      expected = org.manifold.compiler.InvalidIdentifierException.class)
  public void testCreateWithMissingPort() throws SchematicException {
    Value v = BooleanValue.getInstance(true);
    new NodeValue(hasABCNodeAttr, ImmutableMap.of("abc", v), new HashMap<>());
  }
  
  @Test(
      expected = org.manifold.compiler.UndeclaredIdentifierException.class)
  public void testCreateWithInvalidPortName() throws SchematicException {
    Value v = BooleanValue.getInstance(true);
    Map<String, Map<String, Value>> portAttrMap = ImmutableMap.of(
        PORT_NAME, new HashMap<>(),
        "bogusPort", new HashMap<>());
    new NodeValue(hasABCNodeAttr, ImmutableMap.of("abc", v), portAttrMap);
  }

  @Test
  public void testGetAttribute() throws SchematicException {
    Value v = BooleanValue.getInstance(true);
    NodeValue n = new NodeValue(
        hasABCNodeAttr,
        ImmutableMap.of("abc", v),
        PORT_ATTRS
    );
    assertEquals(v, n.getAttribute("abc"));
  }

  @Test(expected = org.manifold.compiler.UndeclaredAttributeException.class)
  public void testGetAttribute_nonexistent() throws SchematicException {
    NodeValue n = new NodeValue(hasNoAttrs, new HashMap<>(), PORT_ATTRS);
    n.getAttribute("bogus");
  }

  @Test
  public void testGetPort() throws SchematicException {
    NodeValue n = new NodeValue(hasNoAttrs, new HashMap<>(), PORT_ATTRS);
    PortValue port = n.getPort(PORT_NAME);
    assertEquals(defaultPortDefinition, port.getType());
  }

  @Test(expected = UndeclaredIdentifierException.class)
  public void testGetPort_nonexistent() throws SchematicException {
    NodeValue n = new NodeValue(hasNoAttrs, new HashMap<>(), PORT_ATTRS);
    n.getPort("bogus");
  }

  @Test
  public void testPortsWithValidAttributes() throws SchematicException {
    PortTypeValue portTypeWithAttr =
        new PortTypeValue(ImmutableMap.of(PORT_ATTR_KEY, boolType));
    Map<String, PortTypeValue> portTypeMap =
        ImmutableMap.of(PORT_NAME, portTypeWithAttr);
    NodeTypeValue withPortAttrs = new NodeTypeValue(
        ImmutableMap.of(),
        portTypeMap
    );
    BooleanValue v = BooleanValue.getInstance(false);
    Map<String, Map<String, Value>> portAttrMap = ImmutableMap.of(PORT_NAME,
        ImmutableMap.of(PORT_ATTR_KEY, v));
    NodeValue n = new NodeValue(withPortAttrs, ImmutableMap.of(), portAttrMap);
    assertEquals(v, n.getPort(PORT_NAME).getAttribute(PORT_ATTR_KEY));
  }
}
