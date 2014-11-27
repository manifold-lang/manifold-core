package org.manifold.compiler;

public class BooleanTypeValue extends TypeValue {

  private static final BooleanTypeValue instance = new BooleanTypeValue();

  public static BooleanTypeValue getInstance() {
    return instance;
  }

  private BooleanTypeValue() { }

  @Override
  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String toString() {
    return "Bool";
  }

  @Override
  public Value instantiate(String s) {
    if (!(Boolean.TRUE.toString().equals(s) ||
        Boolean.FALSE.toString().equals(s))) {
      throw new IllegalArgumentException(String.format(
          "Expected boolean value of true or false, got %s", s));
    }
    return BooleanValue.getInstance(Boolean.parseBoolean(s));
  }

}
