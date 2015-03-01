package org.manifold.compiler;


public class OptionalValue extends Value {
  private final TypeValue optionalType;
  private final TypeValue unaliasedOptionalType;
  private final Value element;

  public OptionalValue(OptionalTypeValue t) {
    super(t);

    this.optionalType = t.getOptionalType();
    this.unaliasedOptionalType = t.getUnaliasedElementType();
    this.element = null;
  }

  public OptionalValue(OptionalTypeValue t, Value element)
      throws TypeMismatchException {
    super(t);

    this.optionalType = t.getOptionalType();
    this.unaliasedOptionalType = t.getUnaliasedElementType();
    this.element = element;

    // type-check contents -- Value must have type 'elementType'
    if (element != null) {
      TypeValue vt = element.getType();
      if (!vt.equals(unaliasedOptionalType)) {
        throw new TypeMismatchException(unaliasedOptionalType, vt);
      }
    }
  }

  public TypeValue getOptionalType() {
    return this.optionalType;
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
