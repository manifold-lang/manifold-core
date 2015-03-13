package org.manifold.compiler;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;


public class ArrayTypeValue extends TypeValue {

  private final TypeValue elementType;

  public TypeValue getElementType() {
    return this.elementType;
  }

  public ArrayTypeValue(TypeValue elementType) {
    this.elementType = elementType;
  }

  @Override
  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String toString() {
    return "Array(" + elementType + ")";
  }

  @Override
  public Value instantiate(JsonElement element) {
    JsonArray jsonArray = element.getAsJsonArray();
    ImmutableList.Builder<Value> valueList = ImmutableList.builder();
    jsonArray.forEach(value -> valueList.add(
        elementType.instantiate(value)));
    try {
      return new ArrayValue(this, valueList.build());
    } catch (TypeMismatchException e) {
      throw Throwables.propagate(e);
    }
  }
}
