package org.manifold.compiler;

public class RealValue extends Value {
  private final Double val;
  public RealValue(Double val){
    super(RealTypeValue.getInstance());
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
  
  public void accept(SchematicValueVisitor visitor) {
    visitor.visit(this);
  }
  
  public double toDouble() {
    return val;
  }
}
