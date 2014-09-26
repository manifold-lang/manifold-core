package org.manifold.compiler;

import java.util.Map;

public class PortTypeValue extends TypeValue {
  
  public PortTypeValue(Map<String, TypeValue> attributes){
    super(attributes);
  }
  
  public PortTypeValue(Map<String, TypeValue> attributes, 
      PortTypeValue supertype) {
    super(supertype, attributes);
  }

  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }
  
}
