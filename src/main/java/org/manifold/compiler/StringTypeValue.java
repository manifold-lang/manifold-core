package org.manifold.compiler;

import com.google.gson.JsonElement;

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
  public Value instantiate(JsonElement e) {
    return new StringValue(instance, e.getAsString());
  }
}
