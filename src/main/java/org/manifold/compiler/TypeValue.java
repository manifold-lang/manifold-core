package org.manifold.compiler;

import java.util.HashMap;
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
  
  private final ImmutableMap<String, TypeValue> attributes;
  public ImmutableMap<String, TypeValue> getAttributes() {
    return attributes;
  }
  
  private ImmutableMap<String, TypeValue> inheritAttributes(
      Map<String, TypeValue> derivedAttributes) {
    // add specified attributes to inherited supertype attributes
    Map<String, TypeValue> mixedAttrs = new HashMap<>(
        getSupertype().getAttributes());
    // TODO strategy for dealing with duplicates?
    mixedAttrs.putAll(derivedAttributes);
    return ImmutableMap.copyOf(mixedAttrs);
  }
  
  public TypeValue(TypeValue supertype, Map<String, TypeValue> attributes) {
    super(null);
    this.supertype = supertype;
    this.attributes = inheritAttributes(attributes);
  }
  
  public TypeValue(Map<String, TypeValue> attributes) {
    super(null);
    this.supertype = TypeTypeValue.getInstance();
    this.attributes = inheritAttributes(attributes);
  }
  
  public TypeValue() {
    super(null);
    this.supertype = TypeTypeValue.getInstance();
    this.attributes = ImmutableMap.of();
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
