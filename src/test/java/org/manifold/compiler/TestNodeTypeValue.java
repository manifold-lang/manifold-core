package org.manifold.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class TestNodeTypeValue {

  private static final TypeValue boolType = BooleanTypeValue.getInstance();
  private static final TypeValue intType = IntegerTypeValue.getInstance();
  private static final Map<String, TypeValue> noAttributes = new HashMap<>();
  private static final Map<String, PortTypeValue> noPorts = new HashMap<>();

  @Test
  public void testGetSupertype() {
    NodeTypeValue nBase = new NodeTypeValue(noAttributes, noPorts);
    NodeTypeValue nDerived = new NodeTypeValue(noAttributes, noPorts, nBase);
    assertEquals(nBase, nDerived.getSupertype());
  }

  @Test
  public void testInheritedAttributes() {
    NodeTypeValue nBase =
        new NodeTypeValue(ImmutableMap.of("u", boolType), noPorts);
    NodeTypeValue nDerived =
        new NodeTypeValue(ImmutableMap.of("v", intType), noPorts, nBase);
    // nDerived must have both "u" and "v" attributes
    assertTrue(nDerived.getAttributes().containsKey("u"));
    assertTrue(nDerived.getAttributes().containsKey("v"));
  }

  @Test
  public void testInheritedPorts() {
    PortTypeValue pType = new PortTypeValue(boolType, noAttributes);
    NodeTypeValue nBase =
        new NodeTypeValue(noAttributes, ImmutableMap.of("p", pType));
    NodeTypeValue nDerived =
        new NodeTypeValue(noAttributes, ImmutableMap.of("q", pType), nBase);
    // nDerived must have both "p" and "q" ports
    assertTrue(nDerived.getPorts().containsKey("p"));
    assertTrue(nDerived.getPorts().containsKey("q"));
  }

}
