package org.manifold.compiler;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;


public class ArrayTypeValue extends AttributeTypeValue {

  private final AttributeTypeValue elementType;

  public AttributeTypeValue getElementType() {
    return this.elementType;
  }

  public ArrayTypeValue(AttributeTypeValue elementType) {
    this.elementType = elementType;
  }

  @Override
  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public Value instantiate(String s) {
    JsonArray jsonArray = new JsonParser().parse(s).getAsJsonArray();
    ImmutableList.Builder<Value> valueList = ImmutableList.builder();
    jsonArray.forEach(value -> valueList.add(
        elementType.instantiate(value.getAsJsonPrimitive().getAsString())));
    try {
      return new ArrayValue(this, valueList.build());
    } catch (TypeMismatchException e) {
      throw Throwables.propagate(e);
    }
  }

}
