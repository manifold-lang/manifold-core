package org.manifold.compiler;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestAtomicTypes {
  
  @Test
  public void testEquality() {
    BooleanTypeValue p1 = BooleanTypeValue.getInstance();
    IntegerTypeValue p2 = IntegerTypeValue.getInstance();
    StringTypeValue p3 = StringTypeValue.getInstance();
    
    // Primitive types are singletons.
    assertEquals(p1, BooleanTypeValue.getInstance());
    assertEquals(p2, IntegerTypeValue.getInstance());
    assertEquals(p3, StringTypeValue.getInstance());
    
    // Two types are equal iff they are the same object
    assertNotEquals(p1, p2);
    assertNotEquals(p1, p3);
    assertNotEquals(p2, p3);
    
    // Equality function doesn't fail for null.
    assertFalse(p1.equals(null));
  }

}
