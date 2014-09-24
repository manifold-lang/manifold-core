package org.manifold.compiler;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class PortTypeValue extends TypeValue {
  private final Map<String, TypeValue> attributes;
  
  public PortTypeValue(Map<String, TypeValue> attributes){
    this.attributes = ImmutableMap.copyOf(attributes);
  }
  
  public PortTypeValue(Map<String, TypeValue> attributes, TypeValue supertype) {
    super(supertype);
    // supertype must be a PortTypeValue for inheritance to work
    if (!(supertype instanceof PortTypeValue)) {
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
  }
  
  public Map<String, TypeValue> getAttributes() {
    return this.attributes;
  }
  
  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }
  
}
