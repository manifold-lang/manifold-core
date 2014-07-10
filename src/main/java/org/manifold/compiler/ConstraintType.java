package org.manifold.compiler;

import com.google.common.collect.ImmutableMap;

import java.util.Map;


public class ConstraintType extends TypeValue {
  private final ImmutableMap<String, TypeValue> attributes;
  
  public ConstraintType(Map<String, TypeValue> attributes){
    this.attributes = ImmutableMap.copyOf(attributes);
  }
  
  public ImmutableMap<String, TypeValue> getAttributes() {
    return attributes;
  }
}
