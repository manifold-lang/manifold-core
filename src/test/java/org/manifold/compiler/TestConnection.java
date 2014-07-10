package org.manifold.compiler;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import org.manifold.compiler.middle.SchematicException;


public class TestConnection {
  private static final PortTypeValue defaultPortDefinition =
      new PortTypeValue(new HashMap<>());
  private static final TypeValue boolType = BooleanTypeValue.getInstance();
  private static final String PORT_NAME1 = "testport";
  private static final String PORT_NAME2 = "another port";
  
  private NodeTypeValue nType;
  private NodeValue n;
  private ConnectionType conType;
  private ConnectionValue ept;
  private Value v;
  
  @Before
  public void setup() throws SchematicException {
    Map<String, PortTypeValue> portMap = ImmutableMap.of(
        PORT_NAME1, defaultPortDefinition,
        PORT_NAME2, defaultPortDefinition);
    Map<String, Map<String, Value>> portAttrMap = ImmutableMap.of(
        PORT_NAME1, ImmutableMap.of(),
        PORT_NAME2, ImmutableMap.of());
    nType = new NodeTypeValue(new HashMap<>(), portMap);
    n = new NodeValue(nType, new HashMap<>(), portAttrMap);
    v = BooleanValue.getInstance(true);
    conType = new ConnectionType(ImmutableMap.of("v", boolType));
    
    ept = new ConnectionValue(
        conType,
        n.getPort(PORT_NAME1),
        n.getPort(PORT_NAME2),
        ImmutableMap.of("v", v)
    );
  }

  @Test(expected = UndefinedBehaviourError.class)
  public void testIncorrectPortConnection() throws SchematicException {
    
    new ConnectionValue(
      conType,
      n.getPort(PORT_NAME1),
      n.getPort(PORT_NAME1),
      ImmutableMap.of("v", v)
    );
  }

  @Test
  public void testGetAttribute() throws UndeclaredAttributeException {
    assertEquals(v, ept.getAttribute("v"));
  }

  @Test(expected = org.manifold.compiler.UndeclaredAttributeException.class)
  public void testGetAttribute_nonexistent()
      throws UndeclaredAttributeException {
    ept.getAttribute("bogus");
  }

  @Test(expected = org.manifold.compiler.UndeclaredAttributeException.class)
  public void testCreateWithUndeclaredAttribute() throws SchematicException {
    new ConnectionValue(conType, n.getPort(PORT_NAME1), n.getPort(PORT_NAME2),
        ImmutableMap.of());
  }

  @Test(expected = org.manifold.compiler.InvalidAttributeException.class)
  public void testCreateWithInvalidAttribute() throws SchematicException {
    Value v = BooleanValue.getInstance(true);
    new ConnectionValue(conType, n.getPort(PORT_NAME1), n.getPort(PORT_NAME2), 
        ImmutableMap.of("v", v, "bogus", v));
  }
  
  @Test
  public void testGetPort() throws UndeclaredIdentifierException {
    assertEquals(n.getPort(PORT_NAME1), ept.getFrom());
    assertEquals(n.getPort(PORT_NAME2), ept.getTo());
  }
}
