package org.manifold.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.manifold.compiler.middle.SchematicException;

public class TestSchematicExceptions {
  private static TypeValue intType = IntegerTypeValue.getInstance();
  private static TypeValue boolType = BooleanTypeValue.getInstance();

  private static void validateException(
      SchematicException e, String nameType, String cause) {
    String message = e.getMessage();
    assertTrue("Should mention the " + nameType, message.contains("bogus"));
    assertTrue("Should mention cause of '" + cause + "'",
        message.contains(cause));
  }

  @Test
  public void testInvalidAttributeException() {
    SchematicException e = new InvalidAttributeException("bogus");
    validateException(e, "attribute name", "invalid attribute");
  }

  @Test
  public void testUndeclaredAttributeException() {
    SchematicException e = new UndeclaredAttributeException("bogus");
    validateException(e, "attribute name", "undeclared attribute");
  }

  @Test
  public void testInvalidIdentifierException() {
    SchematicException e = new InvalidIdentifierException("bogus");
    validateException(e, "identifier name", "invalid identifier");
  }

  @Test
  public void testUndeclaredIdentifierException() {
    SchematicException e = new UndeclaredIdentifierException("bogus");
    validateException(e, "identifier name", "undeclared identifier");
  }

  @Test
  public void testTypeMismatchException() {
    TypeMismatchException e = new TypeMismatchException(boolType, intType);
    assertEquals(boolType, e.getExpectedType());
    assertEquals(intType, e.getActualType());
    String message = e.getMessage();
    assertTrue(message.contains("type error"));
    assertTrue(message.contains(boolType.toString()));
    assertTrue(message.contains(intType.toString()));
  }

  @Test
  public void testMultipleAssignmentException() {
    SchematicException e =
        new MultipleAssignmentException("BogusType", "bogus");
    validateException(e, "identifier name", "multiple instantiations");
    assertTrue(e.getMessage().contains("BogusType"));
  }

  @Test
  public void testMultipleDefinitionException() {
    SchematicException e =
        new MultipleDefinitionException("BogusType", "bogus");
    validateException(e, "identifier name", "multiple definitions");
    assertTrue(e.getMessage().contains("BogusType"));
  }
}
