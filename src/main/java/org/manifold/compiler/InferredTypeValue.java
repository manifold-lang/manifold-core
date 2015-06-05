package org.manifold.compiler;

import com.google.common.base.Throwables;
import com.google.gson.JsonElement;

public class InferredTypeValue extends TypeValue {

  private final TypeValue elementType;

  public TypeValue getInferredType() {
    return this.elementType;
  }

  public InferredTypeValue(TypeValue elementType) {
    this.elementType = elementType;
  }

  @Override
  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String toString() {
    return "Inferred(" + elementType + ")";
  }

  @Override
  public boolean isSubtypeOf(TypeValue other) {
    if (!(other instanceof InferredTypeValue)) {
      return false;
    }
    InferredTypeValue otherType = (InferredTypeValue) other;
    return (this.getInferredType().isSubtypeOf(otherType.getInferredType()));
  }

  @Override
  public Value instantiate(JsonElement element) {
    if (element.isJsonNull()) {
      return new InferredValue(this);
    }

    Value v = elementType.instantiate(element);
    try {
      return new InferredValue(this, v);
    } catch (TypeMismatchException e) {
      throw Throwables.propagate(e);
    }

  }
}
