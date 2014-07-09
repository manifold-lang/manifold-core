package org.manifold.compiler;

public class TypeTypeValue extends TypeValue {

  private static TypeTypeValue instance = new TypeTypeValue();

  public static TypeTypeValue getInstance() {    
    return instance;
  }

  private TypeTypeValue() {}

  // We override the isSubtypeOf method to prevent recursive loops.
  @Override
  public boolean isSubtypeOf(TypeValue type) {
    return this == type;
  }
}
