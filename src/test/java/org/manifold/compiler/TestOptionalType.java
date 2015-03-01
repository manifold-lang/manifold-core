package org.manifold.compiler;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestOptionalType {

  @Test
  public void testGetOptionalType() {
    TypeValue elementType = BooleanTypeValue.getInstance();

    OptionalTypeValue optionalType = new OptionalTypeValue(elementType);

    assertEquals(
        "element type not Boolean",
        elementType,
        optionalType.getOptionalType());
  }

}
