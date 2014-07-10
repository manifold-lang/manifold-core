package org.manifold.compiler;

import com.google.common.collect.ImmutableMap;

import java.util.Map;


public class ConnectionType extends TypeValue {
  private final ImmutableMap<String, TypeValue> attributes;
  
  public ConnectionType(Map<String, TypeValue> attributes){
    this.attributes = ImmutableMap.copyOf(attributes);
  }
  
  public ImmutableMap<String, TypeValue> getAttributes() {
    return attributes;
  }
}
