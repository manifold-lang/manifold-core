package org.manifold.compiler;

public class UserDefinedTypeValue extends AttributeTypeValue {

  private AttributeTypeValue typeAlias;

  public AttributeTypeValue getTypeAlias() {
    return typeAlias;
  }

  public UserDefinedTypeValue(AttributeTypeValue typeAlias) {
    this.typeAlias = typeAlias;
  }

  @Override
  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public boolean isSubtypeOf(TypeValue other) {
    if (this == other) {
      return true;
    } else {
      return typeAlias.isSubtypeOf(other);
    }
  }

  @Override
  public Value instantiate(String s) {
    return typeAlias.instantiate(s);
  }

}
