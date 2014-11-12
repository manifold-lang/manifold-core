package org.manifold.compiler;

public class IntegerTypeValue extends AttributeTypeValue {
  private static final IntegerTypeValue instance = new IntegerTypeValue();

  private IntegerTypeValue() {

  }

  public static IntegerTypeValue getInstance() {
    return instance;
  }

  @Override
  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public Value instantiate(String s) {
    return new IntegerValue(Integer.valueOf(s));
  }

}
