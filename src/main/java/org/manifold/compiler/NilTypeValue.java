package org.manifold.compiler;

public class NilTypeValue extends TypeValue {
  private static final NilTypeValue instance = new NilTypeValue();

  public static NilTypeValue getInstance() {
    return instance;
  }

  private NilTypeValue() { }
  
  @Override
  public void accept(ValueVisitor visitor) {
    visitor.visit(this);
  }
  
}
