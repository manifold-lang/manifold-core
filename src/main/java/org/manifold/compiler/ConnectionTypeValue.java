package org.manifold.compiler;

import com.google.common.collect.ImmutableMap;

public class ConnectionTypeValue extends TypeValue {

  private static ConnectionTypeValue instance = new ConnectionTypeValue();

  public static ConnectionTypeValue getInstance() {
    return instance;
  }

  private ConnectionTypeValue() {
    super(null, ImmutableMap.of());
  }

  @Override
  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String toString() {
    return "Connection";
  }
}
