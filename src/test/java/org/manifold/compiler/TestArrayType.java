package org.manifold.compiler;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestArrayType {

  @Test
  public void testGetElementType() {
    TypeValue elementType = BooleanTypeValue.getInstance();
    
    ArrayTypeValue arrayType = new ArrayTypeValue(elementType);
    
    assertEquals(
        "element type not Boolean",
        elementType,
        arrayType.getElementType()
    );
  }

}
