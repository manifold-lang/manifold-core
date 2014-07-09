package org.manifold.compiler;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.HashMap;
import org.manifold.compiler.back.SchematicException;

public class TestValue {

  @Test
  public void testRetrieveType() throws SchematicException {
    NodeTypeValue nDef = new NodeTypeValue(new HashMap<>(), new HashMap<>());
    Value dom = new NodeValue(nDef, new HashMap<>(), new HashMap<>());
    TypeValue expected = nDef;
    TypeValue actual = dom.getType();
    assertEquals(expected, actual);
  }

}
