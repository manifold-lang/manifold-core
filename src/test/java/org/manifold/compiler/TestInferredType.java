package org.manifold.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

public class TestInferredType {
  private static BooleanTypeValue boolType = BooleanTypeValue.getInstance();
  private static InferredTypeValue inferredBoolType = new InferredTypeValue(
      boolType);
  @Test
  public void testGetInferredType() {
    assertEquals(
        "inferred type not Boolean",
        boolType,
        inferredBoolType.getInferredType());
  }

  @Test
  public void testInstantiateNull() {
    Value v = inferredBoolType.instantiate(JsonNull.INSTANCE);
    assertEquals(null, ((InferredValue) v).get());
  }

  @Test
  public void testInstantiateNonNull() {
    Value v = inferredBoolType.instantiate(new JsonPrimitive("true"));
    assertEquals(BooleanValue.getInstance(true), ((InferredValue) v).get());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInstantiateInvalidType() {
    inferredBoolType.instantiate(new JsonPrimitive("truedat"));
  }

  @Test
  public void testInstantiateAlias() {
    UserDefinedTypeValue bitType = new UserDefinedTypeValue(boolType, "Bit");
    InferredTypeValue inferredBitType = new InferredTypeValue(bitType);

    Value v = inferredBitType.instantiate(new JsonPrimitive("false"));
    assertEquals(inferredBitType, v.getType());
    Value withinV = ((InferredValue) v).get();
    assertEquals(BooleanValue.getInstance(false), withinV);
  }

  @Test
  public void testSubTypeOfInferredType() {
    UserDefinedTypeValue bitType = new UserDefinedTypeValue(boolType, "Bit");
    InferredTypeValue inferredBitType = new InferredTypeValue(bitType);

    assertTrue("inferred is subtype", inferredBitType.isSubtypeOf(bitType));
  }
}
