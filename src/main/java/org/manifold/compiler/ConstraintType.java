package org.manifold.compiler;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class ConstraintType extends TypeValue {

  public ConstraintType(Map<String, TypeValue> attributes) {
    super(attributes);
  }

  public ConstraintType(Map<String, TypeValue> attributes, 
      TypeValue supertype) {
    super(supertype, attributes);
    // supertype must be a ConstraintType for inheritance to work
    if (!(supertype instanceof ConstraintType)) {
      throw new UndefinedBehaviourError(
          "supertype of ConstraintType must be a ConstraintType");
    }
  }

  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }

}
