package org.manifold.compiler;

public class BooleanValue extends Value {

  private final Boolean val;
  public BooleanValue(Type t, Boolean val){
    super(t);
    this.val = val;
  }

}
