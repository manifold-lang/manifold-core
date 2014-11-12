package org.manifold.compiler;

public class RealTypeValue extends AttributeTypeValue {
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
  public Value instantiate(String s) {
    return new RealValue(Double.parseDouble(s));
  }
}
