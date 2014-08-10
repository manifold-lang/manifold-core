package org.manifold.compiler;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

public class ConnectionValue extends Value {

  private final Attributes attributes;

  public Value getAttribute(String attrName) throws
      UndeclaredAttributeException {
    return attributes.get(attrName);
  }
  
  private PortValue portFrom = null, portTo = null;
  
  public PortValue getFrom() {
    return portFrom;
  }
  
  public PortValue getTo() {
    return portTo;
  }

  public ConnectionValue(ConnectionType type, PortValue from, PortValue to,
      Map<String, Value> attrs)
      throws UndeclaredAttributeException, InvalidAttributeException,
      TypeMismatchException {
    super(type);
    this.attributes = new Attributes(type.getAttributes(), attrs);
    this.portFrom = checkNotNull(from);
    this.portTo = checkNotNull(to);
    
    if (from == to) {
      throw new UndefinedBehaviourError(
        "Cannot create connection from a port to itself"
      );
    }
  }

  @Override
  public boolean isElaborationtimeKnowable() {
    return false;
  }

  @Override
  public boolean isRuntimeKnowable() {
    return true;
  }

}
