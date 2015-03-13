package org.manifold.compiler;

import com.google.gson.JsonElement;

public class UserDefinedTypeValue extends TypeValue {

  private final TypeValue typeAlias;
  private final String udtName;

  /**
   * Gets the type that this aliases
   *
   * @return a subclass of TypeValue that isn't a UserDefinedTypeValue
   */
  public TypeValue getTypeAlias() {
    return typeAlias;
  }

  public UserDefinedTypeValue(TypeValue typeAlias, String udtName) {
    this.typeAlias = UserDefinedTypeValue.getUnaliasedType(typeAlias);
    this.udtName = udtName;
  }

  @Override
  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public boolean isSubtypeOf(TypeValue other) {
    if (this == other) {
      return true;
    }
    TypeValue otherTypeAlias = getUnaliasedType(other);
    if (typeAlias == otherTypeAlias) {
      return true;
    }
    return typeAlias.isSubtypeOf(other);
  }

  @Override
  public String toString() {
    return "Type " + udtName + "(" + typeAlias.toString() + ")";
  }

  @Override
  public Value instantiate(JsonElement e) {
    return typeAlias.instantiate(e);
  }

  public static TypeValue getUnaliasedType(TypeValue tv) {
    // TODO: account for infinite loops
    if (tv instanceof UserDefinedTypeValue) {
      return ((UserDefinedTypeValue) tv).getTypeAlias();
    }
    return tv;
  }

  public String getName() {
    return udtName;
  }
}
