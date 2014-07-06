package org.manifold.compiler;

import static org.junit.Assert.*;

import org.junit.Test;
import org.manifold.compiler.front.BooleanTypeValue;
import org.manifold.compiler.front.BooleanValue;
import org.manifold.compiler.front.TypeMismatchException;
import org.manifold.compiler.front.TypeValue;
import org.manifold.compiler.front.Value;

public class TestTypeMismatchException {

  public TypeValue getTypeValueInstance(){
    return BooleanTypeValue.getInstance();
  }
  
  public Value getValueInstance(){
    return BooleanValue.getInstance(false);
  }
  
  public TypeMismatchException getInstance(){
    return new TypeMismatchException(
        getTypeValueInstance(),
        getValueInstance()
    );
  }
  
  @Test
  public void testGetMessage_containsTypeNames() {
    TypeMismatchException instance = getInstance();
    String msg = instance.getMessage();
    assertTrue(msg.contains(getTypeValueInstance().toString()));
    assertTrue(msg.contains(getValueInstance().toString()));
  }

}
