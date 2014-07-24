package org.manifold.compiler;

import java.util.HashMap;
import java.util.Map;

import org.manifold.compiler.middle.SchematicException;

import com.google.common.collect.ImmutableMap;

public class NodeValue extends Value {

  private final Attributes attributes;
  private final Map<String, PortValue> ports;

  public Map<String, PortValue> getPorts() {
    return ImmutableMap.copyOf(ports);
  }
  
  public Value getAttribute(String attrName)
      throws UndeclaredAttributeException {
    return attributes.get(attrName);
  }

  public PortValue getPort(String portName)
      throws UndeclaredIdentifierException {
    if (ports.containsKey(portName)){
      return ports.get(portName);
    } else {
      throw new UndeclaredIdentifierException(portName);
    }
  }

  public NodeValue(NodeTypeValue type, Map<String, Value> attrs,
      Map<String, Map<String, Value>> portAttrMaps) throws SchematicException {
    super(type);
    this.attributes = new Attributes(type.getAttributes(), attrs);
    this.ports = new HashMap<>();

    final Map<String, PortTypeValue> portTypes = type.getPorts();
    if (portTypes != null) {
      for (String portName : portAttrMaps.keySet()) {
        if (!portTypes.containsKey(portName)) {
          throw new UndeclaredIdentifierException(portName);
        }
      }
      
      Map<String, PortTypeValue> ports = type.getPorts();
      for (Map.Entry<String, PortTypeValue> portEntry : ports.entrySet()) {
        String portName = portEntry.getKey();
        PortTypeValue portType = portEntry.getValue();
        Map<String, Value> portAttrs = portAttrMaps.get(portName);
        if (portAttrs == null) {
          throw new InvalidIdentifierException(portName);
        }
        this.ports.put(portName, new PortValue(portType, this, portAttrs));
      }
    }
  }

  @Override
  public boolean isCompiletimeEvaluable() {
    return false;
  }

  @Override
  public boolean isSynthesizable() {
    return true;
  }
}
