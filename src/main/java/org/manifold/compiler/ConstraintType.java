package org.manifold.compiler;

import java.util.Map;

public class ConstraintType extends TypeValue {

  public ConstraintType(Map<String, AttributeTypeValue> attributes) {
    super(attributes);
  }

  public ConstraintType(Map<String, AttributeTypeValue> attributes,
      ConstraintType supertype) {
    super(supertype, attributes);
  }

  @Override
  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }

}
