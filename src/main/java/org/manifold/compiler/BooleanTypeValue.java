package org.manifold.compiler;

import com.google.gson.JsonElement;

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
  public Value instantiate(JsonElement e) {
    // TODO: consider using JSON true, false instead. (also for int).
    String s = e.getAsString();
    if (!(Boolean.TRUE.toString().equals(s) ||
        Boolean.FALSE.toString().equals(s))) {
      throw new IllegalArgumentException(String.format(
          "Expected boolean value of true or false, got %s", s));
    }
    return BooleanValue.getInstance(Boolean.parseBoolean(s));
  }

}
