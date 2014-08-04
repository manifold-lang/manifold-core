package org.manifold.compiler;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

public class PortValue extends Value {

  private final Attributes attributes;
  private final transient NodeValue parent;

  public Value getAttribute(String attrName)
      throws UndeclaredAttributeException {
    return attributes.get(attrName);
  }

  public NodeValue getParent() {
    return parent;
  }

  public PortValue(PortTypeValue type, NodeValue parent,
      Map<String, Value> attrMap) throws UndeclaredAttributeException,
      InvalidAttributeException {

    super(type);
    this.attributes = new Attributes(type.getAttributes(), attrMap);
    this.parent = checkNotNull(parent);
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
