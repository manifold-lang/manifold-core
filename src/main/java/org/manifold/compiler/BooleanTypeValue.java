package org.manifold.compiler;

public class BooleanTypeValue extends TypeValue {

  private static final BooleanTypeValue instance = new BooleanTypeValue();

  public static BooleanTypeValue getInstance() {
    return instance;
  }

  private BooleanTypeValue() { }

  @Override
  public void accept(ValueVisitor visitor) {
    visitor.visit(this);
  }
  
}
