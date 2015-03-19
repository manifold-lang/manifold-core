package org.manifold.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestUserDefinedType {
  private static BooleanTypeValue boolType = BooleanTypeValue.getInstance();
  private static UserDefinedTypeValue bitType = new UserDefinedTypeValue(
      boolType, "Bit");
  private static UserDefinedTypeValue udtBitType = new UserDefinedTypeValue(
      bitType, "UDTBit");

  @Test
  public void testGetUnaliasedType() {
    assertEquals(boolType,
        UserDefinedTypeValue.getUnaliasedType(bitType));
    assertEquals(boolType,
        UserDefinedTypeValue.getUnaliasedType(udtBitType));
  }

  @Test
  public void testSubclass() {
    // UDTs are currently similar to typedefs in C.

    // You can check if udts are subtypes of udts and non-udts properly.
    // Note that boolType.isSubTypeOf(udt) would fail, so you would
    // have to unalias udt in code.
    assertTrue(udtBitType.isSubtypeOf(boolType));
    assertTrue(udtBitType.isSubtypeOf(bitType));
    assertTrue(bitType.isSubtypeOf(udtBitType));
  }
}
