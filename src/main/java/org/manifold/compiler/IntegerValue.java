package org.manifold.compiler;


public class IntegerValue extends Value {

  private final Integer val;
  public IntegerValue(Type t, Integer val){
    super(t);
    this.val = val;
  }
  
}
