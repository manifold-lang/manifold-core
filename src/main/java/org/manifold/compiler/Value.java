package org.manifold.compiler;

public abstract class Value {

  private TypeValue type = null;
  
  protected Value(Value type) {
    // Allow a "null" type so that TypeTypeValue can escape the circular
    // dependency on itself (it will override getType)
    assert type instanceof TypeValue || type == null;
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
