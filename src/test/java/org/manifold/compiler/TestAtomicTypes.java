package org.manifold.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class TestAtomicTypes {
  
  @Test
  public void testEquality() {
    BooleanTypeValue p1 = BooleanTypeValue.getInstance();
    IntegerTypeValue p2 = IntegerTypeValue.getInstance();
    StringTypeValue p3 = StringTypeValue.getInstance();
    RealTypeValue p4 = RealTypeValue.getInstance();
    
    // Primitive types are singletons.
    assertEquals(p1, BooleanTypeValue.getInstance());
    assertEquals(p2, IntegerTypeValue.getInstance());
    assertEquals(p3, StringTypeValue.getInstance());
    assertEquals(p4, RealTypeValue.getInstance());
    
    // Two types are equal iff they are the same object
    assertNotEquals(p1, p2);
    assertNotEquals(p1, p3);
    assertNotEquals(p2, p3);
    assertNotEquals(p3, p4);
    
    // Equality function doesn't fail for null.
    assertFalse(p1.equals(null));
  }

}
