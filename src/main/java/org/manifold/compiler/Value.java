package org.manifold.compiler;

public abstract class Value {

  protected TypeValue type;
  
  protected Value(Value type) {
    assert type instanceof TypeValue;
    this.type = (TypeValue) type;
  }
  
  public TypeValue getType() {
    return this.type;
  }
  
  /*
   * Executed during formal verification pass. Any errors should result in an
   * exception.
   */
  public void verify() throws Exception {}
  
  /*
   * Returns true if this value can be evaulated at compiletime.
   * Either this or isSynthesizable or both must return true.
   */
  public abstract boolean isCompiletimeEvaluable();
  
  /*
   * Returns true if this value is synthesizable, able to be represented in
   * hardware.
   */
  public abstract boolean isSynthesizable();
}
