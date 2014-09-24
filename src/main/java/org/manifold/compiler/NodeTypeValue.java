package org.manifold.compiler;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class NodeTypeValue extends TypeValue {

  private final Map<String, PortTypeValue> ports;

  public NodeTypeValue(
      Map<String, TypeValue> attributes,
      Map<String, PortTypeValue> ports) {
    super(attributes);
    this.ports = ImmutableMap.copyOf(ports);
  }
  
  public NodeTypeValue(
      Map<String, TypeValue> attributes,
      Map<String, PortTypeValue> ports,
      TypeValue supertype) {
    super(supertype, attributes);
    // supertype must be a NodeType for inheritance to work
    if (!(supertype instanceof NodeTypeValue)) {
      throw new UndefinedBehaviourError(
          "supertype of NodeTypeValue must be a NodeTypeValue");
    }
    
    NodeTypeValue superNode = (NodeTypeValue) supertype;
    // add derived ports to inherited ports
    Map<String, PortTypeValue> mixedPorts = new HashMap<>(superNode.getPorts());
    mixedPorts.putAll(ports);
    this.ports = ImmutableMap.copyOf(mixedPorts);
  }

  public Map<String, PortTypeValue> getPorts() {
    return this.ports;
  }
  
  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }
  
}
