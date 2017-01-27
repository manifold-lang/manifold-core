package org.manifold.compiler.middle;

import static org.manifold.compiler.middle.serialization.SerializationConsts.PrimitiveTypes.PRIMITIVE_TYPES;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.manifold.compiler.ConnectionTypeValue;
import org.manifold.compiler.ConnectionValue;
import org.manifold.compiler.ConstraintType;
import org.manifold.compiler.ConstraintValue;
import org.manifold.compiler.MultipleAssignmentException;
import org.manifold.compiler.MultipleDefinitionException;
import org.manifold.compiler.NodeTypeValue;
import org.manifold.compiler.NodeValue;
import org.manifold.compiler.PortTypeValue;
import org.manifold.compiler.UndeclaredIdentifierException;
import org.manifold.compiler.UndefinedBehaviourError;
import org.manifold.compiler.UserDefinedTypeValue;

import com.google.common.collect.ImmutableMap;

/**
 * A Schematic contains all the information needed by the intermediate
 * representation. This includes type definitions, node/connection definitions,
 * node/connection instantiations, and constraint definitions/instantiations.
 */
public class Schematic {
  private final String name;

  public String getName() {
    return name;
  }

  // Maps containing object definitions for this schematic; they are all
  // indexed by the (string) type-name of the object.
  private final Map<String, UserDefinedTypeValue> userDefinedTypes;
  private final Map<String, PortTypeValue> portTypes;
  private final Map<String, NodeTypeValue> nodeTypes;
  private final Map<String, ConstraintType> constraintTypes;
  private final Map<String, ConnectionTypeValue> connectionTypes;

  // Maps containing instantiated objects for this schematic; they are all
  // indexed by the (string) instance-name of the object.
  private final Map<String, NodeValue> nodes;
  private final Map<NodeValue, String> reverseNodeMap;
  private final Map<String, ConnectionValue> connections;
  private final Map<ConnectionValue, String> reverseConnectionMap;
  private final Map<String, ConstraintValue> constraints;
  private final Map<ConstraintValue, String> reverseConstraintMap;

  public Schematic(String name) {
    this.name = name;

    this.userDefinedTypes = new HashMap<>();
    this.connectionTypes = new HashMap<>();
    populateDefaultType();

    this.portTypes = new HashMap<>();
    this.nodeTypes = new HashMap<>();
    this.constraintTypes = new HashMap<>();

    this.nodes = new HashMap<>();
    this.reverseNodeMap = new HashMap<>();
    this.connections = new HashMap<>();
    this.reverseConnectionMap = new HashMap<>();
    this.constraints = new HashMap<>();
    this.reverseConstraintMap = new HashMap<>();
  }

  /*
   * Add "library standard" type definitions for basic types such as integer,
   * string, and boolean.
   */
  private void populateDefaultType() {
    PRIMITIVE_TYPES.forEach((name, type) -> {
        try {
          addUserDefinedType(new UserDefinedTypeValue(type, name));
        } catch (MultipleDefinitionException mde) {
          // this should not actually be possible unless there is something
          // wrong with the compiler itself
          throw new UndefinedBehaviourError(
             "could not create default type definitions ("
              + mde.getMessage() + ")");
        }
      });

    // TODO: Should this be a Primitive type as well?
    try {
      addConnectionType("Connection", ConnectionTypeValue.getInstance());
    } catch (MultipleDefinitionException mde) {
      // this should never happen
      throw new UndefinedBehaviourError(
          "could not create default connection type definitions ("
          + mde.getMessage() + ")");
    }
  }

  public void addUserDefinedType(UserDefinedTypeValue td)
      throws MultipleDefinitionException {
    String typename = td.getName();
    if (userDefinedTypes.containsKey(typename)) {
      throw new MultipleDefinitionException(
          "user-defined-type-definition", typename);
    }
    userDefinedTypes.put(typename, td);
  }

  public UserDefinedTypeValue getUserDefinedType(String typename)
      throws UndeclaredIdentifierException {
    if (userDefinedTypes.containsKey(typename)) {
      return userDefinedTypes.get(typename);
    } else {
      throw new UndeclaredIdentifierException(typename);
    }
  }

  public void addPortType(String typename, PortTypeValue portType)
      throws MultipleDefinitionException {
    if (portTypes.containsKey(typename)) {
      throw new MultipleDefinitionException("port-definition", typename);
    }
    portTypes.put(typename, portType);
  }

  public PortTypeValue getPortType(String typename)
      throws UndeclaredIdentifierException {
    if (portTypes.containsKey(typename)) {
      return portTypes.get(typename);
    } else {
      throw new UndeclaredIdentifierException(typename);
    }
  }

  public void addNodeType(String typename, NodeTypeValue nd)
      throws MultipleDefinitionException {
    if (nodeTypes.containsKey(typename)) {
      throw new MultipleDefinitionException("node-definition", typename);
    }
    nodeTypes.put(typename, nd);
  }

  public NodeTypeValue getNodeType(String typename)
      throws UndeclaredIdentifierException {

    if (nodeTypes.containsKey(typename)) {
      return nodeTypes.get(typename);
    } else {
      throw new UndeclaredIdentifierException(typename);
    }
  }

  public void addConstraintType(String typename, ConstraintType cd)
      throws MultipleDefinitionException {
    if (constraintTypes.containsKey(typename)) {
      throw new MultipleDefinitionException("constraint-definition", typename);
    }
    constraintTypes.put(typename, cd);
  }

  public ConstraintType getConstraintType(String typename)
      throws UndeclaredIdentifierException {
    if (constraintTypes.containsKey(typename)) {
      return constraintTypes.get(typename);
    } else {
      throw new UndeclaredIdentifierException(typename);
    }
  }

  public void addConnectionType(String typename,
                                ConnectionTypeValue connectionType)
      throws MultipleDefinitionException {
    if (connectionTypes.containsKey(typename)) {
      throw new MultipleDefinitionException("connection-definition", typename);
    }
    connectionTypes.put(typename, connectionType);
  }

  public ConnectionTypeValue getConnectionType(String typename)
          throws UndeclaredIdentifierException {
    if (connectionTypes.containsKey(typename)) {
      return connectionTypes.get(typename);
    } else {
      throw new UndeclaredIdentifierException(typename);
    }
  }

  public void addNode(String instanceName, NodeValue node)
      throws MultipleAssignmentException {
    if (nodes.containsKey(instanceName) || reverseNodeMap.containsKey(node)) {
      throw new MultipleAssignmentException("node", instanceName);
    }
    nodes.put(instanceName, node);
    reverseNodeMap.put(node, instanceName);
  }

  public NodeValue getNode(String instanceName)
      throws UndeclaredIdentifierException {
    if (nodes.containsKey(instanceName)) {
      return nodes.get(instanceName);
    } else {
      throw new UndeclaredIdentifierException(instanceName);
    }
  }

  public String getNodeName(NodeValue instance) {
    if (reverseNodeMap.containsKey(instance)) {
      return reverseNodeMap.get(instance);
    }
    throw new NoSuchElementException();
  }

  public void addConnection(String instanceName, ConnectionValue conn)
      throws MultipleAssignmentException {
    if (connections.containsKey(instanceName)) {
      throw new MultipleAssignmentException("connection", instanceName);
    }
    connections.put(instanceName, conn);
    reverseConnectionMap.put(conn, instanceName);
  }

  public ConnectionValue getConnection(String instanceName)
      throws UndeclaredIdentifierException {
    if (connections.containsKey(instanceName)) {
      return connections.get(instanceName);
    } else {
      throw new UndeclaredIdentifierException(instanceName);
    }
  }

  public String getConnectionName(ConnectionValue instance) {
    if (reverseConnectionMap.containsKey(instance)) {
      return reverseConnectionMap.get(instance);
    }
    throw new NoSuchElementException();
  }

  public void addConstraint(String instanceName, ConstraintValue constraint)
      throws MultipleAssignmentException {
    if (constraints.containsKey(instanceName)) {
      throw new MultipleAssignmentException("constraint", instanceName);
    }
    constraints.put(instanceName, constraint);
    reverseConstraintMap.put(constraint, instanceName);
  }

  public ConstraintValue getConstraint(String instanceName)
      throws UndeclaredIdentifierException {
    if (constraints.containsKey(instanceName)) {
      return constraints.get(instanceName);
    } else {
      throw new UndeclaredIdentifierException(instanceName);
    }
  }

  public String getConstraintName(ConstraintValue instance) {
    if (reverseConstraintMap.containsKey(instance)) {
      return reverseConstraintMap.get(instance);
    }
    throw new NoSuchElementException();
  }

  public Map<String, UserDefinedTypeValue> getUserDefinedTypes() {
    return ImmutableMap.copyOf(userDefinedTypes);
  }

  public Map<String, PortTypeValue> getPortTypes() {
    return ImmutableMap.copyOf(portTypes);
  }

  public Map<String, NodeTypeValue> getNodeTypes() {
    return ImmutableMap.copyOf(nodeTypes);
  }

  public Map<String, ConstraintType> getConstraintTypes() {
    return ImmutableMap.copyOf(constraintTypes);
  }

  public Map<String, ConnectionTypeValue> getConnectionTypes() {
    return ImmutableMap.copyOf(connectionTypes);
  }

  public Map<String, NodeValue> getNodes() {
    return ImmutableMap.copyOf(nodes);
  }

  public Map<String, ConnectionValue> getConnections() {
    return ImmutableMap.copyOf(connections);
  }

  public Map<String, ConstraintValue> getConstraints() {
    return ImmutableMap.copyOf(constraints);
  }

}
