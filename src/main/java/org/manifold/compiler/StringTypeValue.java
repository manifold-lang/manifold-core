package org.manifold.compiler;

public class StringTypeValue extends TypeValue {

  private static final StringTypeValue instance = new StringTypeValue();

  private StringTypeValue() { }

  public static StringTypeValue getInstance() {
    return instance;
  }

  @Override
  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String toString() {
    return "String";
  }

  @Override
  public Value instantiate(String s) {
    return new StringValue(instance, s);
  }
}
