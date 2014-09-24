package org.manifold.compiler;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class ConnectionType extends TypeValue {

  public ConnectionType(Map<String, TypeValue> attributes) {
    super(attributes);
  }

  public ConnectionType(Map<String, TypeValue> attributes, 
      TypeValue supertype) {
    super(supertype, attributes);
    // supertype must be a ConnectionType for inheritance to work
    if (!(supertype instanceof ConnectionType)) {
      throw new UndefinedBehaviourError(
          "supertype of ConnectionType must be a ConnectionType");
    }
  }

  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }

}
