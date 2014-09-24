package org.manifold.compiler;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class ConnectionType extends TypeValue {
  private final ImmutableMap<String, TypeValue> attributes;

  public ConnectionType(Map<String, TypeValue> attributes) {
    super();
    this.attributes = ImmutableMap.copyOf(attributes);
  }

  public ConnectionType(Map<String, TypeValue> attributes, 
      TypeValue supertype) {
    super(supertype);
    // supertype must be a ConnectionType for inheritance to work
    if (!(supertype instanceof ConnectionType)) {
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
  }

  public ImmutableMap<String, TypeValue> getAttributes() {
    return attributes;
  }

  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }

}
