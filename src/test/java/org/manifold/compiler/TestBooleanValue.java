package org.manifold.compiler;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

import org.junit.Test;

public class TestBooleanValue {
  
  private BooleanValue getInstance(boolean value) {
    return BooleanValue.getInstance(value);
  }

  @Test
  public void testStaticGetInstance() {
    assertThat(BooleanValue.getInstance(true), instanceOf(BooleanValue.class));
    assertThat(BooleanValue.getInstance(false), instanceOf(BooleanValue.class));
  }

  @Test
  public void testToBoolean() {
    assertTrue(getInstance(true).toBoolean());
    assertFalse(getInstance(false).toBoolean());
  }

  @Test
  public void testGetType() {
    assertThat(
      getInstance(true).getType(),
      instanceOf(BooleanTypeValue.class)
    );
    assertThat(
      getInstance(false).getType(),
      instanceOf(BooleanTypeValue.class)
    );
  }
  
  @Test
  public void testIsCompiletimeEvaluable() {
    assertTrue(getInstance(false).isElaborationtimeKnowable());
  }
  
  @Test
  public void testIsSynthesizable() {
    assertTrue(getInstance(false).isRuntimeKnowable());
  }
  
  @Test
  public void testVerify() {
    getInstance(true).verify();
  }

}
