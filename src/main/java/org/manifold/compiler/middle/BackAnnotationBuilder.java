package org.manifold.compiler.middle;

import java.util.HashMap;
import java.util.Map;

import org.manifold.compiler.ConnectionValue;
import org.manifold.compiler.ConstraintType;
import org.manifold.compiler.ConstraintValue;
import org.manifold.compiler.NodeTypeValue;
import org.manifold.compiler.NodeValue;
import org.manifold.compiler.PortTypeValue;
import org.manifold.compiler.PortValue;
import org.manifold.compiler.TypeMismatchException;
import org.manifold.compiler.TypeValue;
import org.manifold.compiler.UndeclaredAttributeException;
import org.manifold.compiler.UndeclaredIdentifierException;
import org.manifold.compiler.UndefinedBehaviourError;
import org.manifold.compiler.UserDefinedTypeValue;
import org.manifold.compiler.Value;

// Produces a back-annotated schematic, which has the same structure
// as an existing schematic but may have modifications to attributes.
public class BackAnnotationBuilder {

  private Schematic originalSchematic;

  public BackAnnotationBuilder(Schematic originalSchematic) {
    this.originalSchematic = originalSchematic;
  }

  // nodeAttributeAnnotations[nodeName][attrName] = attrValue
  // This map only contains entries that have been updated;
  // the original values should be pulled from the schematic.
  private Map<String, Map<String, Value>> nodeAttributeAnnotations =
      new HashMap<>();

  // portAttributeAnnotations[nodeName][portName][attrName] = attrValue
  // This map only contains entries that have been updated;
  // the original values should be pulled from the schematic.
  private Map<String, Map<String, Map<String, Value>>> portAttributeAnnotations
      = new HashMap<>();

  // connectionAttributeAnnotations[connectionName][attrName] = attrValue
  // This map only contains entries that have been updated;
  // the original values should be pulled from the schematic.
  private Map<String, Map<String, Value>> connectionAttributeAnnotations =
      new HashMap<>();

  // constraintAttributeAnnotations[constraintName][attrName] = attrValue
  // This map only contains entries that have been updated;
  // the original values should be pulled from the schematic.
  private Map<String, Map<String, Value>> constraintAttributeAnnotations =
      new HashMap<>();

  public void annotateConstraintAttribute(
      String constraintName, String attrName, Value attrValue)
          throws UndeclaredIdentifierException, UndeclaredAttributeException,
          TypeMismatchException {
    // make sure the original schematic contains this constraint
    if (!(originalSchematic.getConstraints().containsKey(constraintName))) {
      throw new UndeclaredIdentifierException(
          "constraint '" + constraintName
          + "' not present on original schematic");
    }
    // make sure the constraint has such an attribute
    ConstraintValue originalConstraint =
        originalSchematic.getConstraint(constraintName);
    ConstraintType originalConstraintType = (ConstraintType)
        originalConstraint.getType();
    if (!(originalConstraintType.getAttributes().containsKey(attrName))) {
      throw new UndeclaredAttributeException(
          "attribute '" + attrName + "' not present on this constraint type");
    }
    // make sure the value has the correct type for the attribute
    TypeValue expectedType = originalConstraintType.getAttributes()
        .get(attrName);
    if (expectedType instanceof UserDefinedTypeValue) {
      expectedType = ((UserDefinedTypeValue) expectedType).getTypeAlias();
    }
    TypeValue actualType = attrValue.getType();
    if (!actualType.isSubtypeOf(expectedType)) {
      throw new TypeMismatchException(expectedType, actualType);
    }
    // record the change
    if (!(constraintAttributeAnnotations.containsKey(constraintName))) {
      constraintAttributeAnnotations.put(
          constraintName, new HashMap<String, Value>());
    }
    constraintAttributeAnnotations.get(constraintName).put(attrName, attrValue);
  }

  public void annotateConnectionAttribute(
      String connectionName, String attrName, Value attrValue)
      throws UndeclaredIdentifierException {
    // make sure the original schematic contains this connection
    if (!(originalSchematic.getConnections().containsKey(connectionName))) {
      throw new UndeclaredIdentifierException(
          "connection '" + connectionName +
          "' not present on original schematic");
    }
    // record the change
    if (!(connectionAttributeAnnotations.containsKey(connectionName))) {
      connectionAttributeAnnotations.put(
          connectionName, new HashMap<String, Value>());
    }
    connectionAttributeAnnotations.get(connectionName).put(attrName, attrValue);
  }

  public void annotatePortAttribute(
      String nodeName, String portName, String attrName, Value attrValue)
          throws UndeclaredIdentifierException, UndeclaredAttributeException,
          TypeMismatchException {
    // make sure the original schematic contains this node
    if (!(originalSchematic.getNodes().containsKey(nodeName))) {
      throw new UndeclaredIdentifierException(
          "node '" + nodeName + "' not present on original schematic");
    }
    // make sure the node has such a port
    NodeValue originalNode = originalSchematic.getNode(nodeName);
    if (!(originalNode.getPorts().containsKey(portName))) {
      throw new UndeclaredIdentifierException(
          "port '" + portName + "' not present on original node");
    }
    // make sure the port has such an attribute
    PortValue originalPort = originalNode.getPort(portName);
    PortTypeValue originalPortType = (PortTypeValue) originalPort.getType();
    if (!(originalPortType.getAttributes().containsKey(attrName))) {
      throw new UndeclaredAttributeException(
          "attribute '" + attrName + "' not present on this port type");
    }
    // make sure the value has the correct type for the attribute
    TypeValue expectedType = originalPortType.getAttributes().get(attrName);
    if (expectedType instanceof UserDefinedTypeValue) {
      expectedType = ((UserDefinedTypeValue) expectedType).getTypeAlias();
    }
    TypeValue actualType = attrValue.getType();
    if (!actualType.isSubtypeOf(expectedType)) {
      throw new TypeMismatchException(expectedType, actualType);
    }
    // record the change
    if (!(portAttributeAnnotations.containsKey(nodeName))) {
      portAttributeAnnotations.put(nodeName, new HashMap<>());
    }
    if (!(portAttributeAnnotations.get(nodeName).containsKey(portName))) {
      portAttributeAnnotations.get(nodeName).put(portName, new HashMap<>());
    }
    portAttributeAnnotations.get(nodeName).get(portName)
      .put(attrName, attrValue);
  }

  public void annotateNodeAttribute(
      String nodeName, String attrName, Value attrValue)
          throws UndeclaredIdentifierException, UndeclaredAttributeException,
          TypeMismatchException {
    // make sure the original schematic contains this node
    if (!(originalSchematic.getNodes().containsKey(nodeName))) {
      throw new UndeclaredIdentifierException(
          "node '" + nodeName + "' not present on original schematic");
    }
    // make sure the node has such an attribute
    NodeValue originalNode = originalSchematic.getNode(nodeName);
    NodeTypeValue originalNodeType = (NodeTypeValue) originalNode.getType();
    if (!(originalNodeType.getAttributes().containsKey(attrName))) {
      throw new UndeclaredAttributeException(
          "attribute '" + attrName + "' not present on this node type");
    }
    // make sure the value has the correct type for the attribute
    TypeValue expectedType = originalNodeType.getAttributes().get(attrName);
    if (expectedType instanceof UserDefinedTypeValue) {
      expectedType = ((UserDefinedTypeValue) expectedType).getTypeAlias();
    }
    TypeValue actualType = attrValue.getType();
    if (!actualType.isSubtypeOf(expectedType)) {
      throw new TypeMismatchException(expectedType, actualType);
    }

    // record the change
    if (!(nodeAttributeAnnotations.containsKey(nodeName))) {
      nodeAttributeAnnotations.put(nodeName, new HashMap<String, Value>());
    }
    nodeAttributeAnnotations.get(nodeName).put(attrName, attrValue);
  }

  private String getPortName(PortValue port) {
    String portName = null;
    for (Map.Entry<String, PortValue> entry
        : port.getParent().getPorts().entrySet()) {
      if (entry.getValue().equals(port)) {
        portName = entry.getKey();
        break;
      }
    }
    if (portName == null) {
      throw new UndefinedBehaviourError("cannot find name of port");
    }
    return portName;
  }

  public Schematic build() throws SchematicException {
    Schematic newSchematic = new Schematic(originalSchematic.getName());
    // start by copying all the type declarations from the old schematic
    for (UserDefinedTypeValue udt : originalSchematic
        .getUserDefinedTypes().values()) {
      // badly cheating
      if (!(newSchematic.getUserDefinedTypes().containsKey(udt.getName()))) {
        newSchematic.addUserDefinedType(udt);
      }
    }
    for (Map.Entry<String, PortTypeValue> portType : originalSchematic
        .getPortTypes().entrySet()) {
      newSchematic.addPortType(portType.getKey(), portType.getValue());
    }
    for (Map.Entry<String, NodeTypeValue> nodeType : originalSchematic
        .getNodeTypes().entrySet()) {
      newSchematic.addNodeType(nodeType.getKey(), nodeType.getValue());
    }
    for (Map.Entry<String, ConstraintType> constraintType : originalSchematic
        .getConstraintTypes().entrySet()) {
      newSchematic.addConstraintType(constraintType.getKey(),
          constraintType.getValue());
    }
    Map<NodeValue, NodeValue> nodeIsomorphisms = new HashMap<>();
    for (Map.Entry<String, NodeValue> originalEntry : originalSchematic
        .getNodes().entrySet()) {
      String nodeName = originalEntry.getKey();
      NodeValue originalNode = originalEntry.getValue();

      boolean modifiedNode;
      boolean modifiedPorts;
      modifiedNode = (nodeAttributeAnnotations.containsKey(nodeName));
      modifiedPorts = (portAttributeAnnotations.containsKey(nodeName));
      if (modifiedNode || modifiedPorts) {
        NodeTypeValue nodeType = (NodeTypeValue) originalNode.getType();
        // apply all node attribute edits
        Map<String, Value> originalNodeAttributes = originalNode.getAttributes()
            .getAll();
        Map<String, Value> nodeAttributes = new HashMap<>();
        for (String attrName : originalNodeAttributes.keySet()) {
          if (nodeAttributeAnnotations.containsKey(nodeName)
              && nodeAttributeAnnotations.get(nodeName).containsKey(attrName)) {
            // use new value
            nodeAttributes.put(attrName, nodeAttributeAnnotations.get(nodeName)
                .get(attrName));
          } else {
            // use original value
            nodeAttributes.put(attrName, originalNodeAttributes.get(attrName));
          }
        }
        // apply all port attribute edits
        Map<String, Map<String, Value>> portAttributes = new HashMap<>();
        for (String portName : originalNode.getPorts().keySet()) {
          portAttributes.put(portName, new HashMap<>());
          PortValue originalPort = originalNode.getPort(portName);
          Map<String, Value> originalPortAttributes = originalPort
              .getAttributes().getAll();
          for (String attrName : originalPortAttributes.keySet()) {
            if (portAttributeAnnotations.containsKey(nodeName)
                && portAttributeAnnotations.get(nodeName).containsKey(portName)
                && portAttributeAnnotations.get(nodeName).get(portName).
                    containsKey(attrName)) {
              // use new value
              portAttributes.get(portName).put(attrName,
                  portAttributeAnnotations.get(nodeName).get(portName)
                  .get(attrName));
            } else {
              // use original value
              portAttributes.get(portName).put(attrName,
                  originalPortAttributes.get(attrName));
            }
          }
        }

        // create a new node with the new attributes
        NodeValue newNode = new NodeValue(
            nodeType, nodeAttributes, portAttributes);
        nodeIsomorphisms.put(originalNode, newNode);
        newSchematic.addNode(nodeName, newNode);
      } else {
        // no edits; pass it through
        nodeIsomorphisms.put(originalNode, originalNode);
        newSchematic.addNode(nodeName, originalNode);
      }
    }
    Map<ConnectionValue, ConnectionValue> connectionIsomorphisms =
        new HashMap<>();
    for (Map.Entry<String, ConnectionValue> originalEntry : originalSchematic
        .getConnections().entrySet()) {
      String connectionName = originalEntry.getKey();
      ConnectionValue originalConnection = originalEntry.getValue();
      // the easiest thing to do is recreate every connection
      // with respect to the node isomorphisms
      PortValue originalPortFrom = originalConnection.getFrom();
      PortValue newPortFrom = nodeIsomorphisms.get(originalPortFrom.getParent())
          .getPort(getPortName(originalPortFrom));
      PortValue originalPortTo = originalConnection.getTo();
      PortValue newPortTo = nodeIsomorphisms.get(originalPortTo.getParent())
          .getPort(getPortName(originalPortTo));
      Map<String, Value> newAttributes = new HashMap<>();
      // do this in two steps:
      // first, copy all old attributes, then overwrite them with new ones
      // this also adds new attributes that didn't exist before
      for (Map.Entry<String, Value> entry :
          originalConnection.getAttributes().getAll().entrySet()) {
        newAttributes.put(entry.getKey(), entry.getValue());
      }
      if (connectionAttributeAnnotations.containsKey(connectionName)) {
        for (Map.Entry<String, Value> entry :
            connectionAttributeAnnotations.get(connectionName).entrySet()) {
          newAttributes.put(entry.getKey(), entry.getValue());
        }
      }
      ConnectionValue newConnection = new ConnectionValue(
          newPortFrom, newPortTo, newAttributes);
      newSchematic.addConnection(connectionName, newConnection);
      connectionIsomorphisms.put(originalConnection, newConnection);
    }

    // constraints are tricky
    // we iterate over each one and inspect its attributes
    // in particular, we check the type of each value

    for (Map.Entry<String, ConstraintValue> originalEntry : originalSchematic
        .getConstraints().entrySet()) {
      String constraintName = originalEntry.getKey();
      ConstraintValue originalConstraint = originalEntry.getValue();
      ConstraintType constraintType = (ConstraintType) originalConstraint
          .getType();

      Map<String, Value> newAttributes = new HashMap<>();
      for (Map.Entry<String, Value> originalAttribute : originalConstraint
          .getAttributes().getAll().entrySet()) {
        String attrName = originalAttribute.getKey();
        Value originalAttrValue = originalAttribute.getValue();
        TypeValue originalAttrType = constraintType.getAttributes()
            .get(attrName);
        if (originalAttrType instanceof NodeTypeValue) {
          // pull the new node from the isomorphism table
          newAttributes.put(attrName, nodeIsomorphisms.get(originalAttrValue));
        } else if (originalAttrValue instanceof ConnectionValue) {
          // TODO ...instanceof ConnectionTypeValue?
          // no connection types, so we have to test the value's type
          // pull the new connection from the isomorphism table
          newAttributes.put(attrName, connectionIsomorphisms.get(
              originalAttrValue));
        } else if (originalAttrType instanceof PortTypeValue) {
          // find the port's parent node, pull a node from the
          // node isomorphism table, find the corresponding port on *that* node,
          // and use that port instead
          PortValue originalPort = (PortValue) originalAttrValue;
          String portName = getPortName(originalPort);
          NodeValue originalParent = originalPort.getParent();
          NodeValue newParent = nodeIsomorphisms.get(originalParent);
          PortValue newPort = newParent.getPort(portName);
          newAttributes.put(attrName, newPort);
        } else if (originalAttrType instanceof ConstraintType) {
          // throw an exception because there is a difficult
          // case where the order in which constraints are built matters
          // that I don't want to handle yet
          throw new UnsupportedOperationException(
              "backannotation does not yet support dependent constraints");
        } else {
          // assume the value doesn't depend on any schematic objects
          // that might have been recreated;
          // in this case, we pull the value from the
          // constraint annotation table if it exists and use the
          // original value otherwise
          if (constraintAttributeAnnotations.containsKey(constraintName)
              && constraintAttributeAnnotations.get(constraintName)
              .containsKey(attrName)) {
            // use new value
            newAttributes.put(attrName, constraintAttributeAnnotations
                .get(constraintName).get(attrName));
          } else {
            // use original value
            newAttributes.put(attrName, originalAttrValue);
          }
        }
      }

      ConstraintValue newConstraint = new ConstraintValue(
          constraintType, newAttributes);
      newSchematic.addConstraint(constraintName, newConstraint);
    }
    return newSchematic;
  }

}
