package org.manifold.compiler;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class BooleanValue extends Value {

  private static final BooleanValue highInstance = new BooleanValue(true);
  private static final BooleanValue lowInstance = new BooleanValue(false);

  public static BooleanValue getInstance(boolean value) {
    if (value) {
      return highInstance;
    } else {
      return lowInstance;
    }
  }

  private final boolean value;

  private BooleanValue(boolean value) {
    super(BooleanTypeValue.getInstance());
    this.value = value;
  }

  public boolean toBoolean() {
    return value;
  }

  @Override
  public void verify() { }

  @Override
  public boolean isElaborationtimeKnowable() {
    return true;
  }

  @Override
  public boolean isRuntimeKnowable() {
    return true;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @Override
  public JsonElement toJson() {
    return new JsonPrimitive(String.valueOf(value));
  }

  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }

}
