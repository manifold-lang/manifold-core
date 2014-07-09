package org.manifold.compiler;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableMap;

import org.junit.Test;
import org.manifold.compiler.back.SchematicException;

public class TestConstraint {

  private static final TypeValue boolType = BooleanTypeValue.getInstance();
  private static final ConstraintType defaultConstraintDefinition =
      new ConstraintType(ImmutableMap.of("v", boolType));

  @Test
  public void testGetAttribute() throws SchematicException {
    Value v = BooleanValue.getInstance(true);
    ConstraintValue ept = new ConstraintValue(defaultConstraintDefinition,
        ImmutableMap.of("v", v));
    Value vActual = ept.getAttribute("v");
    assertEquals(v, vActual);
  }

  @Test(expected = org.manifold.compiler.UndeclaredAttributeException.class)
  public void testGetAttribute_nonexistent() throws SchematicException {
    Value v = BooleanValue.getInstance(true);
    ConstraintValue ept = new ConstraintValue(defaultConstraintDefinition,
        ImmutableMap.of("v", v));
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
