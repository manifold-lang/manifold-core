package org.manifold.compiler;

import org.manifold.compiler.IntegerTypeValue;

public class IntegerValue extends Value {

  private final Integer val;
  public IntegerValue(Integer val){
    super(IntegerTypeValue.getInstance());
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
