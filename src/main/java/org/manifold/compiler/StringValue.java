package org.manifold.compiler;

public class StringValue extends Value {

  private final String val;

  public StringValue(TypeValue t, String val) {
    super(t);
    this.val = val;
  }

  @Override
  public boolean isCompiletimeEvaluable() {
    return true;
  }

  @Override
  public boolean isSynthesizable() {
    return false;
  }

}
