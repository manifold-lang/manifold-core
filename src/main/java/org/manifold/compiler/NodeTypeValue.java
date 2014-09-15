package org.manifold.compiler;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class NodeTypeValue extends TypeValue {

  private final Map<String, TypeValue> attributes;
  private final Map<String, PortTypeValue> ports;
  private final TypeValue supertype;
  
  @Override
  public TypeValue getSupertype() {
    return this.supertype;
  }

  public NodeTypeValue(
      Map<String, TypeValue> attributes,
      Map<String, PortTypeValue> ports) {
    this.attributes = ImmutableMap.copyOf(attributes);
    this.ports = ImmutableMap.copyOf(ports);
    this.supertype = TypeTypeValue.getInstance();
  }
  
  public NodeTypeValue(
      Map<String, TypeValue> attributes,
      Map<String, PortTypeValue> ports,
      TypeValue supertype) {
    // supertype must be a NodeType for inheritance to work
    if (!(supertype instanceof NodeTypeValue)) {
      // TODO we could throw a TypeMismatchException here
      throw new UndefinedBehaviourError(
          "supertype of NodeTypeValue must be a NodeTypeValue");
    }
    
    NodeTypeValue superNode = (NodeTypeValue) supertype;
    // add specified attributes to inherited supertype attributes
    Map<String, TypeValue> mixedAttrs = new HashMap<>(
        superNode.getAttributes());
    // TODO strategy for dealing with duplicates?
    mixedAttrs.putAll(attributes);
    this.attributes = ImmutableMap.copyOf(mixedAttrs);
    // do the same for ports
    Map<String, PortTypeValue> mixedPorts = new HashMap<>(superNode.getPorts());
    mixedPorts.putAll(ports);
    this.ports = ImmutableMap.copyOf(mixedPorts);
    
    this.supertype = supertype;
  }
  
  public Map<String, TypeValue> getAttributes() {
    return this.attributes;
  }

  public Map<String, PortTypeValue> getPorts() {
    return this.ports;
  }
  
  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }
  
}
