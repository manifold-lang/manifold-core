package org.manifold.compiler;


public class InferredValue extends Value {
  private final TypeValue inferredType;
  private final TypeValue unaliasedInferredType;
  private final Value element;

  public InferredValue(InferredTypeValue t) {
    super(t);

    this.inferredType = t.getInferredType();
    this.unaliasedInferredType = t.getUnaliasedElementType();
    this.element = null;
  }

  public InferredValue(InferredTypeValue t, Value element)
      throws TypeMismatchException {
    super(t);

    this.inferredType = t.getInferredType();
    this.unaliasedInferredType = t.getUnaliasedElementType();
    this.element = element;

    // type-check contents -- Value must have type 'elementType'
    if (element != null) {
      TypeValue vt = element.getType();
      if (!vt.equals(unaliasedInferredType)) {
        throw new TypeMismatchException(unaliasedInferredType, vt);
      }
    }
  }

  public TypeValue getInferredType() {
    return this.inferredType;
  }

  public Value get() {
    return element;
  }

  public boolean isSet() {
    return element != null;
  }

  @Override
  public void verify() throws Exception {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isElaborationtimeKnowable() {
    if (element != null) {
      return element.isElaborationtimeKnowable();
    }
    return true;
  }

  @Override
  public boolean isRuntimeKnowable() {
    if (element != null) {
      return element.isRuntimeKnowable();
    }
    return true;
  }

  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }

}
