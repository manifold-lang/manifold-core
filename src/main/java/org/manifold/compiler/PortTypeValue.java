package org.manifold.compiler;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class PortTypeValue extends TypeValue {
  private final Map<String, TypeValue> attributes;
  
  public PortTypeValue(Map<String, TypeValue> attributes){
    this.attributes = ImmutableMap.copyOf(attributes);
  }
  
  public Map<String, TypeValue> getAttributes() {
    return this.attributes;
  }
}
