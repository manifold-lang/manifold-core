package org.manifold.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.junit.Test;
import org.manifold.compiler.middle.SchematicException;

public class TestArrayValue {
  private static final IntegerTypeValue intType =
      IntegerTypeValue.getInstance();
  private static final ArrayTypeValue intArrayType =
      new ArrayTypeValue(intType);
  private static final NodeTypeValue nodeType =
      new NodeTypeValue(ImmutableMap.of(), ImmutableMap.of());
  private static final ArrayTypeValue nodeArrayType =
      new ArrayTypeValue(nodeType);

  private static List<Value> makeIntArray(int... values) {
    List<Value> vals = new ArrayList<>();
    for (int v : values) {
      vals.add(new IntegerValue(v));
    }
    return vals;
  }

  @Test
  public void testIntArray() throws TypeMismatchException {
    List<Value> l = makeIntArray(3, 14);
    ArrayValue arr = new ArrayValue(intArrayType, l);
    assertEquals(intArrayType, arr.getType());
    assertEquals(intType, arr.getElementType());
    assertEquals(2, arr.length());
    assertTrue(arr.isElaborationtimeKnowable());
    assertFalse(arr.isRuntimeKnowable());
    assertEquals(arr.get(0), l.get(0));
    assertEquals(arr.get(1), l.get(1));
  }

  @Test
  public void testNodeArray() throws SchematicException {
    List<Value> l = ImmutableList.of(
        new NodeValue(nodeType, ImmutableMap.of(), ImmutableMap.of()));
    ArrayValue arr = new ArrayValue(nodeArrayType, l);
    assertEquals(nodeArrayType, arr.getType());
    assertEquals(1, arr.length());
    assertFalse(arr.isElaborationtimeKnowable());
    assertTrue(arr.isRuntimeKnowable());
    assertEquals(arr.get(0), l.get(0));
  }

  @Test(expected = org.manifold.compiler.TypeMismatchException.class)
  public void testInvalidElementType() throws TypeMismatchException {
    List<Value> boolList = ImmutableList.of(BooleanValue.getInstance(true));
    new ArrayValue(intArrayType, boolList);
  }
}
