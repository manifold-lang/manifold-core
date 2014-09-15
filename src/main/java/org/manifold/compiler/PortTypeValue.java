package org.manifold.compiler;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class PortTypeValue extends TypeValue {
  private final Map<String, TypeValue> attributes;
  private final TypeValue supertype;
  
  @Override
  public TypeValue getSupertype() {
    return this.supertype;
  }
  
  public PortTypeValue(Map<String, TypeValue> attributes){
    this.attributes = ImmutableMap.copyOf(attributes);
    this.supertype = TypeTypeValue.getInstance();
  }
  
  public PortTypeValue(Map<String, TypeValue> attributes, TypeValue supertype) {
    // supertype must be a PortTypeValue for inheritance to work
    if (!(supertype instanceof PortTypeValue)) {
      // TODO we could throw a TypeMismatchException here
      throw new UndefinedBehaviourError(
          "supertype of PortTypeValue must be a PortTypeValue");
    }
    // add specified attributes to inherited supertype attributes
    PortTypeValue superPort = (PortTypeValue) supertype;
    Map<String, TypeValue> mixedAttrs = new HashMap<>(
        superPort.getAttributes());
    // TODO strategy for dealing with duplicates?
    mixedAttrs.putAll(attributes);
    this.attributes = ImmutableMap.copyOf(mixedAttrs);
    this.supertype = supertype;
  }
  
  public Map<String, TypeValue> getAttributes() {
    return this.attributes;
  }
  
  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }
  
}
