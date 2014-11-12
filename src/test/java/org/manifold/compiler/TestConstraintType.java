package org.manifold.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class TestConstraintType {

  private static final AttributeTypeValue boolType = BooleanTypeValue
      .getInstance();
  private static final AttributeTypeValue intType = IntegerTypeValue
      .getInstance();
  private static final Map<String, AttributeTypeValue> noAttributes
      = new HashMap<>();

  @Test
  public void testGetSupertype() {
    ConstraintType cBase = new ConstraintType(noAttributes);
    ConstraintType cDerived = new ConstraintType(noAttributes, cBase);
    assertEquals(cBase, cDerived.getSupertype());
  }

  @Test
  public void testInheritedAttributes() {
    ConstraintType cBase = new ConstraintType(ImmutableMap.of("u", boolType));
    ConstraintType cDerived = new ConstraintType(
        ImmutableMap.of("v", intType), cBase);
    // pDerived must have both "u" and "v" attributes
    assertTrue(cDerived.getAttributes().containsKey("u"));
    assertTrue(cDerived.getAttributes().containsKey("v"));
  }

}
