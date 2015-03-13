package org.manifold.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.manifold.compiler.middle.SchematicException;

import com.google.common.collect.ImmutableMap;

public class TestInferredValue {
  private static final IntegerTypeValue intType =
      IntegerTypeValue.getInstance();
  private static final UserDefinedTypeValue intAliasType =
      new UserDefinedTypeValue(intType, "intAlias");
  private static final InferredTypeValue intInferredType =
      new InferredTypeValue(intType);
  private static final InferredTypeValue intAliasInferredType =
      new InferredTypeValue(intAliasType);
  private static final NodeTypeValue nodeType =
      new NodeTypeValue(ImmutableMap.of(), ImmutableMap.of());
  private static final InferredTypeValue nodeInferredType =
      new InferredTypeValue(nodeType);

  @Test
  public void testIntInferred() throws TypeMismatchException {
    IntegerValue v = new IntegerValue(14);
    InferredValue inferred = new InferredValue(intInferredType, v);
    assertEquals(intInferredType, inferred.getType());
    assertEquals(intType, inferred.getInferredType());
    assertTrue(inferred.isElaborationtimeKnowable());
    assertFalse(inferred.isRuntimeKnowable());
    assertEquals(v, inferred.get());
  }

  @Test
  public void testIntEmpty() {
    InferredValue inferred = new InferredValue(intInferredType);
    assertEquals(intInferredType, inferred.getType());
    assertEquals(intType, inferred.getInferredType());
    assertTrue(inferred.isElaborationtimeKnowable());
    assertTrue(inferred.isRuntimeKnowable());
    assertEquals(null, inferred.get());
  }

  @Test
  public void testNodeArray() throws SchematicException {
    NodeValue n = new NodeValue(nodeType, ImmutableMap.of(), ImmutableMap.of());
    InferredValue inferred = new InferredValue(nodeInferredType, n);
    assertEquals(nodeInferredType, inferred.getType());
    assertFalse(inferred.isElaborationtimeKnowable());
    assertTrue(inferred.isRuntimeKnowable());
    assertEquals(n, inferred.get());
  }

  @Test(expected = org.manifold.compiler.TypeMismatchException.class)
  public void testInvalidElementType() throws TypeMismatchException {
    new InferredValue(intInferredType, BooleanValue.getInstance(true));
  }

  @Test
  public void testIntAlias() throws TypeMismatchException {
    // TODO: Figure out what to do with aliases.
    IntegerValue v = new IntegerValue(14);
    InferredValue inferred = new InferredValue(intAliasInferredType, v);
    assertEquals(intAliasInferredType, inferred.getType());
    assertEquals(intAliasType, inferred.getInferredType());
    assertTrue(inferred.isElaborationtimeKnowable());
    assertFalse(inferred.isRuntimeKnowable());
    assertEquals(v, inferred.get());
  }
}
