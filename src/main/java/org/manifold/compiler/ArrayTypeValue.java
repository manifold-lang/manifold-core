package org.manifold.compiler;


public class ArrayTypeValue extends TypeValue {
  
  private final TypeValue elementType;
  
  public TypeValue getElementType(){
    return this.elementType;
  }
  
  public ArrayTypeValue(TypeValue elementType){
    this.elementType = elementType;
  }
  
  @Override
  public void accept(ValueVisitor visitor) {
    visitor.visit(this);
  }
  
}
