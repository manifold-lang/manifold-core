package org.manifold.compiler;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

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

  private final TypeValue supertype;
  public TypeValue getSupertype() {
    return supertype;
  }

  private final ImmutableMap<String, AttributeTypeValue> attributes;
  public ImmutableMap<String, AttributeTypeValue> getAttributes() {
    return attributes;
  }

  private ImmutableMap<String, AttributeTypeValue> inheritAttributes(
      Map<String, AttributeTypeValue> derivedAttributes) {
    // add specified attributes to inherited supertype attributes
    ImmutableMap.Builder<String, AttributeTypeValue> b = ImmutableMap.builder();
    if (getSupertype() != null) {
      b.putAll(getSupertype().getAttributes());
    }
    b.putAll(derivedAttributes);
    return b.build();
  }

  public TypeValue(TypeValue supertype,
      Map<String, AttributeTypeValue> attributes) {
    super(null);
    this.supertype = supertype;
    this.attributes = inheritAttributes(attributes);
  }

  public TypeValue(Map<String, AttributeTypeValue> attributes) {
    this(TypeTypeValue.getInstance(), attributes);
  }

  public TypeValue() {
    this(TypeTypeValue.getInstance(), ImmutableMap.of());
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
