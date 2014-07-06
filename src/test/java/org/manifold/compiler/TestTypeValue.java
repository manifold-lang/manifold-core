package org.manifold.compiler;

import static org.junit.Assert.*;

import org.junit.Test;
import org.manifold.compiler.front.BooleanTypeValue;
import org.manifold.compiler.front.TypeTypeValue;
import org.manifold.compiler.front.TypeValue;

public class TestTypeValue {

  // TODO(lucas) Replace this with another real type value class as soon as
  // we have one other than BitTypeValue and TypeTypeValue
  private static class FacadeTypeValue extends TypeValue {

    private static final FacadeTypeValue instance = new FacadeTypeValue();

    public static FacadeTypeValue getInstance() {
      return instance;
    }

    private FacadeTypeValue() {}

    @Override
    public void verify() {}
  }

  private TypeValue getInstance() {
    // TypeValue is abstract so we use a simple implementation.
    return BooleanTypeValue.getInstance();
  }

  @Test
  public void testGetType() {
    assertSame(getInstance().getType(), TypeTypeValue.getInstance());
  }

  @Test
  public void testGetSupertype() {
    assertSame(getInstance().getSupertype(), TypeTypeValue.getInstance());
  }

  @Test
  public void isSubtypeOf_equal() {
    assertTrue(getInstance().isSubtypeOf(getInstance()));
  }

  @Test
  public void isSubtypeOf_subtype() {
    assertTrue(BooleanTypeValue.getInstance().isSubtypeOf(
        TypeTypeValue.getInstance()));
  }

  @Test
  public void isSubtypeOf_false() {
    assertFalse(BooleanTypeValue.getInstance().isSubtypeOf(
        FacadeTypeValue.getInstance()));
  }

  @Test
  public void testIsCompiletimeEvaluable() {
    assertTrue(getInstance().isCompiletimeEvaluable());
  }

  @Test
  public void testIsSynthesizable() {
    assertFalse(getInstance().isSynthesizable());
  }

}
