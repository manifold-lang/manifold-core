package org.manifold.compiler;

public interface SchematicValueVisitor {

  void visit(ArrayTypeValue arrayTypeValue);

  void visit(TypeTypeValue typeTypeValue);

  void visit(StringValue stringValue);

  void visit(StringTypeValue stringTypeValue);

  void visit(PortValue portValue);

  void visit(PortTypeValue portTypeValue);

  void visit(NodeValue nodeValue);

  void visit(NodeTypeValue nodeTypeValue);

  void visit(NilTypeValue nilTypeValue);

  void visit(IntegerValue integerValue);

  void visit(ConstraintValue constraintValue);

  void visit(IntegerTypeValue integerTypeValue);

  void visit(ConstraintType constraintType);

  void visit(ConnectionValue connectionValue);

  void visit(ConnectionTypeValue connectionType);

  void visit(BooleanValue booleanValue);

  void visit(BooleanTypeValue booleanTypeValue);

  void visit(ArrayValue arrayValue);

  void visit(RealTypeValue realTypeValue);

  void visit(RealValue realValue);

  void visit(UserDefinedTypeValue userDefinedTypeValue);

}
