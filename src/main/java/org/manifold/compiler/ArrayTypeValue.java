package org.manifold.compiler;


public class ArrayTypeValue extends TypeValue {
  
  private final TypeValue elementType;
  
  public TypeValue getElementType() {
    return this.elementType;
  }
  
  public ArrayTypeValue(TypeValue elementType) {
    this.elementType = elementType;
  }
  
  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }

  public String toString() {
    return "Array("+elementType+")";
  }
   
}
