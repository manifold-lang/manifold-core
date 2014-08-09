package org.manifold.compiler;


public class IntegerValue extends Value {

  private final Integer val;
  public IntegerValue(TypeValue t, Integer val){
    super(t);
    this.val = val;
  }

  @Override
  public boolean isElaborationtimeKnowable() {
    return true;
  }

  @Override
  public boolean isRuntimeKnowable() {
    return false;
  }
  
}
