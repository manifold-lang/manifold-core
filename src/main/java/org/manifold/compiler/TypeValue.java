package org.manifold.compiler;

public abstract class TypeValue extends Value {

 /*
  * Java doesn't let us express this but every `TypeValue`, by convention,
  * ought to have a `public abstract final getInstance()` singleton accessor
  * method.
  *
  *   public abstract final TypeValue getInstance();
  *
  * and a private constructor
  *
  *   private abstract TypeValue();
  */
  
  private TypeValue supertype;
  public TypeValue getSupertype() {
    return supertype;
  }
  
  public TypeValue(TypeValue supertype) {
    super(null);
    this.supertype = supertype;
  }
  
  public TypeValue() {
    super(null);
    this.supertype = TypeTypeValue.getInstance();
  }

  @Override
  public TypeValue getType() {
    return TypeTypeValue.getInstance();
  }

  public boolean isSubtypeOf(TypeValue type) {
    if (this == type) {
      return true;
    } else {
      // TypeTypeValue overrides this method for base case behaviour.
      return getSupertype().isSubtypeOf(type);
    }
  }

  @Override
  public boolean isElaborationtimeKnowable() {
    return true;
  }

  @Override
  public boolean isRuntimeKnowable() {
    return false;
  }
}
