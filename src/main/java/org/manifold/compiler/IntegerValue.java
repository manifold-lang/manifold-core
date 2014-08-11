package org.manifold.compiler;

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
  
  @Override
  public void accept(ValueVisitor visitor) {
    visitor.visit(this);
  }

}
