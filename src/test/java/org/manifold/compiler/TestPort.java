package org.manifold.compiler;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import org.manifold.compiler.back.SchematicException;

public class TestPort {

  private static final TypeValue boolType = BooleanTypeValue.getInstance();
  private static final PortTypeValue defaultPortType =
      new PortTypeValue(ImmutableMap.of("v", boolType));
  private static final String PORT_NAME = "testport";
  private static final Value v = BooleanValue.getInstance(true);
  
  private NodeValue parent;
  private PortValue port;
  
  @Before
  public void setup() throws SchematicException {
    Map<String, PortTypeValue> portTypeMap =
        ImmutableMap.of(PORT_NAME, defaultPortType);
    Map<String, Map<String, Value>> portMap =
        ImmutableMap.of(PORT_NAME, ImmutableMap.of("v", v));
    NodeTypeValue parentType = new NodeTypeValue(new HashMap<>(), portTypeMap);
    parent = new NodeValue(parentType, new HashMap<>(), portMap);
    port = parent.getPort(PORT_NAME);
  }

  @Test
  public void testGetAttribute() throws UndeclaredAttributeException {
    assertEquals(v, port.getAttribute("v"));
  }

  @Test(expected = org.manifold.compiler.UndeclaredAttributeException.class)
  public void testGetAttribute_nonexistent()
      throws UndeclaredAttributeException {
    port.getAttribute("bogus");
  }
  
  @Test
  public void testGetParent() throws UndeclaredIdentifierException {
    assertEquals(parent, parent.getPort(PORT_NAME).getParent());
  }
}
