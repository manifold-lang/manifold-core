package org.manifold.compiler;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class ConstraintType extends TypeValue {
  private final ImmutableMap<String, TypeValue> attributes;

  public ConstraintType(Map<String, TypeValue> attributes) {
    this.attributes = ImmutableMap.copyOf(attributes);
  }

  public ConstraintType(Map<String, TypeValue> attributes, 
      TypeValue supertype) {
    super(supertype);
    // supertype must be a ConstraintType for inheritance to work
    if (!(supertype instanceof ConstraintType)) {
      throw new UndefinedBehaviourError(
          "supertype of ConstraintType must be a ConstraintType");
    }
    // add specified attributes to inherited supertype attributes
    ConstraintType superConst = (ConstraintType) supertype;
    Map<String, TypeValue> mixedAttrs = new HashMap<>(
        superConst.getAttributes());
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
