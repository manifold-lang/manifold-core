package org.manifold.compiler;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestInferredType {

  @Test
  public void testGetInferredType() {
    TypeValue boolType = BooleanTypeValue.getInstance();

    InferredTypeValue inferredType = new InferredTypeValue(boolType);

    assertEquals(
        "inferred type not Boolean",
        boolType,
        inferredType.getInferredType());
  }

}
