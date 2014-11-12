package org.manifold.compiler;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestArrayType {

  @Test
  public void testGetElementType() {
    AttributeTypeValue elementType = BooleanTypeValue.getInstance();

    ArrayTypeValue arrayType = new ArrayTypeValue(elementType);

    assertEquals(
        "element type not Boolean",
        elementType,
        arrayType.getElementType()
    );
  }

}
