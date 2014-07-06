package org.manifold.compiler;

public class StringValue extends Value {

  private final String val;

  public StringValue(Type t, String val) {
    super(t);
    this.val = val;
  }

}
