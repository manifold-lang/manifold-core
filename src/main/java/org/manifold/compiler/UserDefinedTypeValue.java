package org.manifold.compiler;

public class UserDefinedTypeValue extends TypeValue {

  private TypeValue typeAlias;

  public TypeValue getTypeAlias() {
    return typeAlias;
  }

  public UserDefinedTypeValue(TypeValue typeAlias) {
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

  public String toString() {
    return typeAlias.toString();
  }

}
