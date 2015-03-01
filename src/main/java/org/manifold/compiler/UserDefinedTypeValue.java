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

  @Override
  public String toString() {
    return typeAlias.toString();
  }

  @Override
  public Value instantiate(String s) {
    return typeAlias.instantiate(s);
  }

  public static TypeValue getUnaliasedType(TypeValue tv) {
    // TODO: account for infinite loops
    while (tv instanceof UserDefinedTypeValue) {
      tv = ((UserDefinedTypeValue) tv).getTypeAlias();
    }
    return tv;
  }
}
