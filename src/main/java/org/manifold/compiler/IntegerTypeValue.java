package org.manifold.compiler;

public class IntegerTypeValue extends TypeValue {
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
  public String toString() {
    return "Int";
  }

  @Override
  public Value instantiate(String s) {
    return new IntegerValue(Integer.valueOf(s));
  }

}
