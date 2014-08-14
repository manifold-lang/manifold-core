package org.manifold.compiler;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class PortTypeValue extends TypeValue {
  private final Map<String, TypeValue> attributes;
  
  public PortTypeValue(Map<String, TypeValue> attributes){
    this.attributes = ImmutableMap.copyOf(attributes);
  }
  
  public Map<String, TypeValue> getAttributes() {
    return this.attributes;
  }
  
  @Override
  public void accept(ValueVisitor visitor) {
    visitor.visit(this);
  }
  
}
