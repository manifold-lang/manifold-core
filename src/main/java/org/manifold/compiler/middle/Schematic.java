package org.manifold.compiler.middle;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.manifold.compiler.BooleanTypeValue;
import org.manifold.compiler.ConnectionType;
import org.manifold.compiler.ConnectionValue;
import org.manifold.compiler.ConstraintType;
import org.manifold.compiler.ConstraintValue;
import org.manifold.compiler.IntegerTypeValue;
import org.manifold.compiler.MultipleAssignmentException;
import org.manifold.compiler.MultipleDefinitionException;
import org.manifold.compiler.NodeTypeValue;
import org.manifold.compiler.NodeValue;
import org.manifold.compiler.PortTypeValue;
import org.manifold.compiler.StringTypeValue;
import org.manifold.compiler.TypeValue;
import org.manifold.compiler.UndeclaredIdentifierException;
import org.manifold.compiler.UndefinedBehaviourError;

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
  private final Map<String, TypeValue> userDefinedTypes;
  private final Map<String, PortTypeValue> portTypes;
  private final Map<String, NodeTypeValue> nodeTypes;
  private final Map<String, ConnectionType> connectionTypes;
  private final Map<String, ConstraintType> constraintTypes;

  // Maps containing instantiated objects for this schematic; they are all
  // indexed by the (string) instance-name of the object.
  private final Map<String, NodeValue> nodes;
  private final Map<NodeValue, String> reverseNodeMap;
  private final Map<String, ConnectionValue> connections;
  private final Map<String, ConstraintValue> constraints;

  public Schematic(String name) {
    this.name = name;

    this.userDefinedTypes = new HashMap<>();
    populateDefaultType();

    this.portTypes = new HashMap<>();
    this.nodeTypes = new HashMap<>();
    this.connectionTypes = new HashMap<>();
    this.constraintTypes = new HashMap<>();

    this.nodes = new HashMap<>();
    this.reverseNodeMap = new HashMap<>();
    this.connections = new HashMap<>();
    this.constraints = new HashMap<>();
  }

  /*
   * Add "library standard" type definitions for basic types such as integer,
   * string, and boolean. Every class in .intermediate.types should be
   * represented in here.
   */
  private void populateDefaultType() {
    TypeValue boolType = BooleanTypeValue.getInstance();
    TypeValue intType = IntegerTypeValue.getInstance();
    TypeValue stringType = StringTypeValue.getInstance();

    try {
      addUserDefinedType("Bool", boolType);
      addUserDefinedType("Int", intType);
      addUserDefinedType("String", stringType);
    } catch (MultipleDefinitionException mde) {
      // this should not actually be possible unless there is something wrong
      // with the compiler itself
      throw new UndefinedBehaviourError(
          "could not create default type definitions (" + mde.getMessage()
              + ")");
    }
  }

  public void addUserDefinedType(String typename, TypeValue td)
      throws MultipleDefinitionException {
    if (userDefinedTypes.containsKey(typename)) {
      throw new MultipleDefinitionException("type-definition", typename);
    }
    userDefinedTypes.put(typename, td);
  }

  public TypeValue getUserDefinedType(String typename)
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

  public void addConnectionType(String typename, ConnectionType cd)
      throws MultipleDefinitionException {
    if (connectionTypes.containsKey(typename)) {
      throw new MultipleDefinitionException("connection-definition", typename);
    }
    connectionTypes.put(typename, cd);
  }

  public ConnectionType getConnectionType(String typename)
      throws UndeclaredIdentifierException {
    if (connectionTypes.containsKey(typename)) {
      return connectionTypes.get(typename);
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
  }

  public ConnectionValue getConnection(String instanceName)
      throws UndeclaredIdentifierException {
    if (connections.containsKey(instanceName)) {
      return connections.get(instanceName);
    } else {
      throw new UndeclaredIdentifierException(instanceName);
    }
  }

  public Map<String, NodeValue> getNodes() {
    return ImmutableMap.copyOf(nodes);
  }
  
  public Map<String, ConnectionValue> getConnections() {
    return ImmutableMap.copyOf(connections);
  }

  public void addConstraint(String instanceName, ConstraintValue constraint)
      throws MultipleAssignmentException {
    if (constraints.containsKey(instanceName)) {
      throw new MultipleAssignmentException("constraint", instanceName);
    }
    constraints.put(instanceName, constraint);
  }

  public ConstraintValue getConstraint(String instanceName)
      throws UndeclaredIdentifierException {
    if (constraints.containsKey(instanceName)) {
      return constraints.get(instanceName);
    } else {
      throw new UndeclaredIdentifierException(instanceName);
    }
  }

  // TODO do we add nodes as a function of their node definition right away, or
  // just record that the node "will" exist with such-and-such definition and
  // elaborate it later?
}
