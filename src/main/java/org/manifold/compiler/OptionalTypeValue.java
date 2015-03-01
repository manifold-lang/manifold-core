package org.manifold.compiler;

import com.google.common.base.Throwables;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class OptionalTypeValue extends TypeValue {

  private final TypeValue elementType;
  private final TypeValue unaliasedType;

  public TypeValue getOptionalType() {
    return this.elementType;
  }

  public TypeValue getUnaliasedElementType() {
    return this.unaliasedType;
  }

  public OptionalTypeValue(TypeValue elementType) {
    this.elementType = elementType;
    this.unaliasedType = UserDefinedTypeValue.getUnaliasedType(elementType);
  }

  @Override
  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String toString() {
    return "Optional(" + elementType + ")";
  }

  @Override
  public Value instantiate(String s) {
    JsonElement element = new JsonParser().parse(s);
    if (element.isJsonNull()) {
      return new OptionalValue(this);
    }

    Value v = elementType.instantiate(
        element.getAsJsonPrimitive().getAsString());
    try {
      return new OptionalValue(this, v);
    } catch (TypeMismatchException e) {
      throw Throwables.propagate(e);
    }

  }
}
