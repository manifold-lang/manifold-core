package org.manifold.compiler;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class ConnectionType extends TypeValue {
  private final ImmutableMap<String, TypeValue> attributes;
  private final TypeValue supertype;

  @Override
  public TypeValue getSupertype() {
    return this.supertype;
  }

  public ConnectionType(Map<String, TypeValue> attributes) {
    this.attributes = ImmutableMap.copyOf(attributes);
    this.supertype = TypeTypeValue.getInstance();
  }

  public ConnectionType(Map<String, TypeValue> attributes, 
      TypeValue supertype) {
    // supertype must be a ConnectionType for inheritance to work
    if (!(supertype instanceof ConnectionType)) {
      // TODO we could throw a TypeMismatchException here
      throw new UndefinedBehaviourError(
          "supertype of ConnectionType must be a ConnectionType");
    }
    // add specified attributes to inherited supertype attributes
    ConnectionType superConn = (ConnectionType) supertype;
    Map<String, TypeValue> mixedAttrs = new HashMap<>(
        superConn.getAttributes());
    // TODO strategy for dealing with duplicates?
    mixedAttrs.putAll(attributes);
    this.attributes = ImmutableMap.copyOf(mixedAttrs);
    this.supertype = supertype;
  }

  public ImmutableMap<String, TypeValue> getAttributes() {
    return attributes;
  }

  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }

}
