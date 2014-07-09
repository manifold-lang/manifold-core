package org.manifold.compiler;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Set;
import org.manifold.compiler.front.EnumIdentifierNotDefined;
import org.manifold.compiler.front.EnumTypeValue;
import org.manifold.compiler.front.EnumValue;
import org.manifold.compiler.front.TypeMismatchException;

public class TestEnumTypeValue {

  private final HashMap<String, Value> enumMap = new HashMap<>();

  @Before
  public void setup() {
    enumMap.clear();
    enumMap.put("foo", BooleanValue.getInstance(true));
    enumMap.put("bar", BooleanValue.getInstance(false));
  }

  @Test(expected = TypeMismatchException.class)
  @SuppressWarnings("empty-statement")
  public void testIncorrectType() throws Exception {
    new EnumTypeValue(TypeTypeValue.getInstance(), enumMap).verify();;
  }

  @Test(expected = EnumIdentifierNotDefined.class)
  public void testIncorrectIdentifier() throws Exception {
    EnumTypeValue enumType = new EnumTypeValue(
      BooleanTypeValue.getInstance(),
      enumMap
    );
    enumType.verify();
    EnumValue enumValue = new EnumValue(enumType, "boop");
  }

  @Test
  public void testGetters() throws TypeMismatchException {
    EnumTypeValue enumType = new EnumTypeValue(
      BooleanTypeValue.getInstance(),
      enumMap
    );
    Set<String> enumNames = enumType.getIdentifiers();
    assertTrue(enumNames.containsAll(enumMap.keySet()));
    assertEquals(BooleanTypeValue.getInstance(), enumType.getEnumsType());
  }
}
