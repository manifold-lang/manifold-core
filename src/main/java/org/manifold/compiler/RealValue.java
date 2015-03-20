package org.manifold.compiler;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class RealValue extends Value {
  private final double val;

  public RealValue(double val) {
    super(RealTypeValue.getInstance());
    this.val = val;
  }

  @Override
  public boolean isElaborationtimeKnowable() {
    return true;
  }

  @Override
  public boolean isRuntimeKnowable() {
    return false;
  }

  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }

  public double toDouble() {
    return val;
  }

  @Override
  public String toString() {
    return Double.toString(val);
  }

  @Override
  public JsonElement toJson() {
    return new JsonPrimitive(Double.toString(val));
  }
}
