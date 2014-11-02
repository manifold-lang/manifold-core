package org.manifold.compiler;

import java.util.Map;

public class ConnectionType extends TypeValue {

  public ConnectionType(Map<String, AttributeTypeValue> attributes) {
    super(attributes);
  }

  public ConnectionType(Map<String, AttributeTypeValue> attributes,
      ConnectionType supertype) {
    super(supertype, attributes);
  }

  @Override
  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }

}
