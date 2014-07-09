package org.manifold.compiler;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

import org.junit.Test;

public class TestBooleanTypeValue {

  private BooleanTypeValue getInstance() {
    return BooleanTypeValue.getInstance();
  }
  
  @Test
  public void testStaticGetInstance() {
    assertThat(
      BooleanTypeValue.getInstance(),
      instanceOf(BooleanTypeValue.class)
    );
  }
  
  @Test
  public void testVerify() throws Exception {
    getInstance().verify();
  }


}
