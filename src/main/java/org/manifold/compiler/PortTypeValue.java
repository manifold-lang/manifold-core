package org.manifold.compiler;

import java.util.Map;

public class PortTypeValue extends TypeValue {

  private final TypeValue signalType;
  public TypeValue getSignalType() {
    return signalType;
  }

  public PortTypeValue(TypeValue signalType, Map<String, AttributeTypeValue> attributes){
    super(attributes);
    this.signalType = signalType;
  }

  public PortTypeValue(TypeValue signalType, Map<String, AttributeTypeValue> attributes,
      PortTypeValue supertype) {
    super(supertype, attributes);
    this.signalType = signalType;
  }

  @Override
  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }

}
