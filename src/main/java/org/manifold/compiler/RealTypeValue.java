package org.manifold.compiler;

public class RealTypeValue extends TypeValue {
  private static final RealTypeValue instance = new RealTypeValue();

  private RealTypeValue() {

  }

  public static RealTypeValue getInstance() {
    return instance;
  }

  @Override
  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String toString() {
    return "Real";
  }

  @Override
  public Value instantiate(String s) {
    return new RealValue(Double.parseDouble(s));
  }
}
