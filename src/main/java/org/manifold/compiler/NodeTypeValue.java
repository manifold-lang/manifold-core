package org.manifold.compiler;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class NodeTypeValue extends TypeValue {

  private final Map<String, PortTypeValue> ports;

  public NodeTypeValue(
      Map<String, AttributeTypeValue> attributes,
      Map<String, PortTypeValue> ports) {
    super(attributes);
    this.ports = ImmutableMap.copyOf(ports);
  }

  public NodeTypeValue(
      Map<String, AttributeTypeValue> attributes,
      Map<String, PortTypeValue> ports,
      NodeTypeValue supertype) {
    super(supertype, attributes);
    // add derived ports to inherited ports
    Map<String, PortTypeValue> mixedPorts = new HashMap<>(supertype.getPorts());
    mixedPorts.putAll(ports);
    this.ports = ImmutableMap.copyOf(mixedPorts);
  }

  public Map<String, PortTypeValue> getPorts() {
    return this.ports;
  }

  @Override
  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }

}
