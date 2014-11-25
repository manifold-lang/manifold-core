package org.manifold.compiler;

public class BooleanTypeValue extends TypeValue {

  private static final BooleanTypeValue instance = new BooleanTypeValue();

  public static BooleanTypeValue getInstance() {
    return instance;
  }

  private BooleanTypeValue() { }

  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }

  public String toString() {
    return "Bool";
  }
  
}
