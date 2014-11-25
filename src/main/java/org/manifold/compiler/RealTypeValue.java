package org.manifold.compiler;

public class RealTypeValue extends TypeValue {
  private static final RealTypeValue instance = new RealTypeValue();

  private RealTypeValue() {

  }

  public static RealTypeValue getInstance() {
    return instance;
  }
  
  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }

  public String toString() {
    return "String";
  }
}
