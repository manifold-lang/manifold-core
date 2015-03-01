package org.manifold.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.manifold.compiler.middle.SchematicException;

import com.google.common.collect.ImmutableMap;

public class TestOptionalValue {
  private static final IntegerTypeValue intType =
      IntegerTypeValue.getInstance();
  private static final UserDefinedTypeValue intAliasType =
      new UserDefinedTypeValue(intType);
  private static final OptionalTypeValue intOptionalType =
      new OptionalTypeValue(intType);
  private static final OptionalTypeValue intAliasOptionalType =
      new OptionalTypeValue(intAliasType);
  private static final NodeTypeValue nodeType =
      new NodeTypeValue(ImmutableMap.of(), ImmutableMap.of());
  private static final OptionalTypeValue nodeOptionalType =
      new OptionalTypeValue(nodeType);

  @Test
  public void testIntOptional() throws TypeMismatchException {
    IntegerValue v = new IntegerValue(14);
    OptionalValue opt = new OptionalValue(intOptionalType, v);
    assertEquals(intOptionalType, opt.getType());
    assertEquals(intType, opt.getOptionalType());
    assertTrue(opt.isElaborationtimeKnowable());
    assertFalse(opt.isRuntimeKnowable());
    assertEquals(v, opt.get());
  }

  @Test
  public void testIntEmpty() {
    OptionalValue opt = new OptionalValue(intOptionalType);
    assertEquals(intOptionalType, opt.getType());
    assertEquals(intType, opt.getOptionalType());
    assertTrue(opt.isElaborationtimeKnowable());
    assertTrue(opt.isRuntimeKnowable());
    assertEquals(null, opt.get());
  }

  @Test
  public void testNodeArray() throws SchematicException {
    NodeValue n = new NodeValue(nodeType, ImmutableMap.of(), ImmutableMap.of());
    OptionalValue opt = new OptionalValue(nodeOptionalType, n);
    assertEquals(nodeOptionalType, opt.getType());
    assertFalse(opt.isElaborationtimeKnowable());
    assertTrue(opt.isRuntimeKnowable());
    assertEquals(n, opt.get());
  }

  @Test(expected = org.manifold.compiler.TypeMismatchException.class)
  public void testInvalidElementType() throws TypeMismatchException {
    new OptionalValue(intOptionalType, BooleanValue.getInstance(true));
  }

  @Test
  public void testIntAlias() throws TypeMismatchException {
    // TODO: Figure out what to do with aliases.
    IntegerValue v = new IntegerValue(14);
    OptionalValue opt = new OptionalValue(intAliasOptionalType, v);
    assertEquals(intAliasOptionalType, opt.getType());
    assertEquals(intAliasType, opt.getOptionalType());
    assertTrue(opt.isElaborationtimeKnowable());
    assertFalse(opt.isRuntimeKnowable());
    assertEquals(v, opt.get());
  }
}
