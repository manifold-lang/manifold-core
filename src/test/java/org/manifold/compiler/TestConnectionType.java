package org.manifold.compiler;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class TestConnectionType {

  private static final TypeValue boolType = BooleanTypeValue.getInstance();
  private static final TypeValue intType = IntegerTypeValue.getInstance();
  private static final Map<String, TypeValue> noAttributes = new HashMap<>();
  
  @Test
  public void testGetSupertype() {
    ConnectionType cBase = new ConnectionType(noAttributes);
    ConnectionType cDerived = new ConnectionType(noAttributes, cBase);
    assertEquals(cBase, cDerived.getSupertype());
  }
  
  @Test
  public void testInheritedAttributes() {
    ConnectionType cBase =
        new ConnectionType(ImmutableMap.of("u", boolType));
    ConnectionType cDerived =
        new ConnectionType(ImmutableMap.of("v", intType), cBase);
    // pDerived must have both "u" and "v" attributes
    assertTrue(cDerived.getAttributes().containsKey("u"));
    assertTrue(cDerived.getAttributes().containsKey("v"));
  }
  
  @Test(expected = UndefinedBehaviourError.class)
  public void testInheritance_FromWrongSupertype() {
    new ConnectionType(noAttributes, boolType);
  }

}
