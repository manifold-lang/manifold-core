package org.manifold.compiler;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class NodeTypeValue extends TypeValue {

  private final Map<String, TypeValue> attributes;
  private final Map<String, PortTypeValue> ports;

  public NodeTypeValue(
      Map<String, TypeValue> attributes,
      Map<String, PortTypeValue> ports) {
    this.attributes = ImmutableMap.copyOf(attributes);
    this.ports = ImmutableMap.copyOf(ports);
  }
  
  public Map<String, TypeValue> getAttributes() {
    return this.attributes;
  }

  public Map<String, PortTypeValue> getPorts() {
    return this.ports;
  }
  
  @Override
  public void accept(ValueVisitor visitor) {
    visitor.visit(this);
  }
  
}
