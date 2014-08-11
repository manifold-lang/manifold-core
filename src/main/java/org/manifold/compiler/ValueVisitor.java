package org.manifold.compiler;

import org.manifold.compiler.front.EnumTypeValue;
import org.manifold.compiler.front.EnumValue;
import org.manifold.compiler.front.FunctionTypeValue;
import org.manifold.compiler.front.FunctionValue;
import org.manifold.compiler.front.PrimitiveFunctionValue;
import org.manifold.compiler.front.TupleTypeValue;
import org.manifold.compiler.front.TupleValue;

public interface ValueVisitor {

  void visit(ArrayTypeValue arrayTypeValue);

  void visit(PrimitiveFunctionValue primitiveFunctionValue);

  void visit(TupleValue tupleValue);

  void visit(TupleTypeValue tupleTypeValue);

  void visit(FunctionValue functionValue);

  void visit(FunctionTypeValue functionTypeValue);

  void visit(EnumValue enumValue);

  void visit(EnumTypeValue enumTypeValue);

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

  void visit(ConnectionType connectionType);

  void visit(BooleanValue booleanValue);

  void visit(BooleanTypeValue booleanTypeValue);

  void visit(ArrayValue arrayValue);

}
