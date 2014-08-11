package org.manifold.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.ImmutableMap;

import org.junit.Test;
import org.manifold.compiler.middle.SchematicException;

public class TestConstraint {

  private static final TypeValue boolType = BooleanTypeValue.getInstance();
  private static final ConstraintType defaultConstraintDefinition =
      new ConstraintType(ImmutableMap.of("v", boolType));
  private static final Value vTrue = BooleanValue.getInstance(true);

  @Test
  public void testProperties() throws SchematicException {
    ConstraintValue ept = new ConstraintValue(defaultConstraintDefinition,
        ImmutableMap.of("v", vTrue));
    assertTrue(ept.isRuntimeKnowable());
    assertFalse(ept.isElaborationtimeKnowable());
  }

  @Test
  public void testGetAttribute() throws SchematicException {
    ConstraintValue ept = new ConstraintValue(defaultConstraintDefinition,
        ImmutableMap.of("v", vTrue));
    assertEquals(vTrue, ept.getAttribute("v"));
  }

  @Test(expected = org.manifold.compiler.UndeclaredAttributeException.class)
  public void testGetAttribute_nonexistent() throws SchematicException {
    ConstraintValue ept = new ConstraintValue(defaultConstraintDefinition,
        ImmutableMap.of("v", vTrue));
    ept.getAttribute("bogus");
  }

  @Test(expected = org.manifold.compiler.UndeclaredAttributeException.class)
  public void testMissingAttribute() throws SchematicException {
    new ConstraintValue(defaultConstraintDefinition, ImmutableMap.of());
  }

  @Test(expected = org.manifold.compiler.InvalidAttributeException.class)
  public void testExtraAttribute() throws Exception {
    Value v = BooleanValue.getInstance(true);
    new ConstraintValue(defaultConstraintDefinition,
        ImmutableMap.of("v", v, "vBogus", v));
  }
}
