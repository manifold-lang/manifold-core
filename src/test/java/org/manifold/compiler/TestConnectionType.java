package org.manifold.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class TestConnectionType {

  private static final TypeValue boolType = BooleanTypeValue
      .getInstance();
  private static final TypeValue intType = IntegerTypeValue
      .getInstance();
  private static final Map<String, TypeValue> noAttributes
      = new HashMap<>();

  @Test
  public void testGetSupertype() {
    ConnectionTypeValue cBase = new ConnectionTypeValue(noAttributes);
    ConnectionTypeValue cDerived = new ConnectionTypeValue(noAttributes, cBase);
    assertEquals(cBase, cDerived.getSupertype());
  }

  @Test
  public void testInheritedAttributes() {
    ConnectionTypeValue cBase = new ConnectionTypeValue(ImmutableMap.of("u", boolType));
    ConnectionTypeValue cDerived = new ConnectionTypeValue(
        ImmutableMap.of("v", intType), cBase);
    // pDerived must have both "u" and "v" attributes
    assertTrue(cDerived.getAttributes().containsKey("u"));
    assertTrue(cDerived.getAttributes().containsKey("v"));
  }

}
