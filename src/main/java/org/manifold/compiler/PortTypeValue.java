package org.manifold.compiler;

import java.util.Map;

public class PortTypeValue extends TypeValue {
  
  public PortTypeValue(Map<String, TypeValue> attributes){
    super(attributes);
  }
  
  public PortTypeValue(Map<String, TypeValue> attributes, TypeValue supertype) {
    super(supertype, attributes);
    // supertype must be a PortTypeValue for inheritance to work
    if (!(supertype instanceof PortTypeValue)) {
      throw new UndefinedBehaviourError(
          "supertype of PortTypeValue must be a PortTypeValue");
    }
  }

  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }
  
}
