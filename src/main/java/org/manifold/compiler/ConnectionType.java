package org.manifold.compiler;

import java.util.Map;

public class ConnectionType extends TypeValue {

  public ConnectionType(Map<String, TypeValue> attributes) {
    super(attributes);
  }

  public ConnectionType(Map<String, TypeValue> attributes,
      ConnectionType supertype) {
    super(supertype, attributes);
  }

  @Override
  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }
}
