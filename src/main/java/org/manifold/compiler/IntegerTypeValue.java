package org.manifold.compiler;

import com.google.gson.JsonElement;

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
  public Value instantiate(JsonElement e) {
    return new IntegerValue(Integer.parseInt(e.getAsString()));
  }

}
