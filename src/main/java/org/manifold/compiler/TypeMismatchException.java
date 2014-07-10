package org.manifold.compiler;

import org.manifold.compiler.middle.SchematicException;

public class TypeMismatchException extends SchematicException {
  private static final long serialVersionUID = 7730618233489002412L;
  private final TypeValue expected;
  private final TypeValue actual;

  public TypeMismatchException(TypeValue expected, TypeValue actual){
    this.expected = expected;
    this.actual = actual;
  }

  public TypeValue getExpectedType(){
    return this.expected;
  }
  public TypeValue getActualType(){
    return this.actual;
  }

  @Override
  public String getMessage(){
    return "type error: expected '" + expected + "', actual '" + actual + "'";
  }
}
