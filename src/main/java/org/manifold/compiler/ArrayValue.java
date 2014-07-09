package org.manifold.compiler;

import java.util.ArrayList;
import java.util.List;

public class ArrayValue extends Value {

  private final TypeValue elementType;
  private List<Value> elements = null;

  public ArrayValue(ArrayTypeValue t, List<Value> elements)
      throws TypeMismatchException {
    super(t);
    this.elementType = t.getElementType();
    // type-check contents -- every Value must have type 'elementType'
    for (Value element : elements){
      TypeValue vt = element.getType();
      if (!vt.equals(elementType)){
        throw new TypeMismatchException(elementType, vt);
      }
    }
    // now we can copy the new list into our object
    this.elements = new ArrayList<Value>(elements);
  }

  public TypeValue getElementType(){
    return this.elementType;
  }

  public Value get(Integer i){
    return elements.get(i);
  }

  public Integer length(){
    return elements.size();
  }

  @Override
  public void verify() throws Exception {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isCompiletimeEvaluable() {
    for (Value element : elements) {
      if (!element.isCompiletimeEvaluable()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean isSynthesizable() {
    for (Value element : elements) {
      if (!element.isSynthesizable()) {
        return false;
      }
    }
    return true;
  }

}
