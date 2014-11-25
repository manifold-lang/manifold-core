package org.manifold.compiler;

public class StringTypeValue extends TypeValue {

  private static final StringTypeValue instance = new StringTypeValue();

  private StringTypeValue() { }

  public static StringTypeValue getInstance() {
    return instance;
  }
  
  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }

  public String toString() {
    return "String";
  }
    
}
