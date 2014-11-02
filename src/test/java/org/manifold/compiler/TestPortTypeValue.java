package org.manifold.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class TestPortTypeValue {

  private static final AttributeTypeValue boolType = BooleanTypeValue.getInstance();
  private static final AttributeTypeValue intType = IntegerTypeValue.getInstance();
  private static final Map<String, AttributeTypeValue> noAttributes = new HashMap<>();

  @Test
  public void testGetSupertype() {
    PortTypeValue pBase = new PortTypeValue(boolType, noAttributes);
    PortTypeValue pDerived = new PortTypeValue(boolType, noAttributes, pBase);
    assertEquals(pBase, pDerived.getSupertype());
  }

  @Test
  public void testInheritedAttributes() {
    PortTypeValue pBase =
        new PortTypeValue(boolType, ImmutableMap.of("u", boolType));
    PortTypeValue pDerived =
        new PortTypeValue(boolType, ImmutableMap.of("v", intType), pBase);

    // pDerived must have both "u" and "v" attributes
    assertTrue(pDerived.getAttributes().containsKey("u"));
    assertTrue(pDerived.getAttributes().containsKey("v"));
  }

}
