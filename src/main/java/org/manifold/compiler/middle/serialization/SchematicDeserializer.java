package org.manifold.compiler.middle.serialization;


import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.manifold.compiler.BooleanValue;
import org.manifold.compiler.ConnectionType;
import org.manifold.compiler.ConnectionValue;
import org.manifold.compiler.ConstraintType;
import org.manifold.compiler.IntegerValue;
import org.manifold.compiler.InvalidAttributeException;
import org.manifold.compiler.MultipleAssignmentException;
import org.manifold.compiler.MultipleDefinitionException;
import org.manifold.compiler.NodeTypeValue;
import org.manifold.compiler.NodeValue;
import org.manifold.compiler.PortTypeValue;
import org.manifold.compiler.PortValue;
import org.manifold.compiler.StringTypeValue;
import org.manifold.compiler.StringValue;
import org.manifold.compiler.TypeMismatchException;
import org.manifold.compiler.TypeValue;
import org.manifold.compiler.UndeclaredAttributeException;
import org.manifold.compiler.UndeclaredIdentifierException;
import org.manifold.compiler.Value;
import org.manifold.compiler.middle.Schematic;
import org.manifold.compiler.middle.SchematicException;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class SchematicDeserializer {

  private static final String NODE_PORT_DELIM = ":";

  private static final String NAME = "name";
  private static final String ATTRIBUTES = "attributes";
  private static final String TYPE = "type";

  private static final String USER_DEF_TYPES = "userDefinedTypes";

  private static final String PORT_TYPES = "portTypes";

  private static final String NODE_TYPES = "nodeTypes";
  private static final String NODE_TYPE_PORT_MAP = "ports";

  private static final String CONNECTION_TYPES = "connectionTypes";
  private static final String CONSTRAINT_TYPES = "constraintTypes";

  private static final String NODES = "nodes";
  private static final String NODE_PORT_ATTRS = "portAttrs";

  private static final String CONNECTIONS = "connections";
  private static final String CONNECTION_FROM = "from";
  private static final String CONNECTION_TO = "to";

  private Gson gson = new GsonBuilder().create();

  private Map<String, TypeValue> getTypeDefAttributes(Schematic sch,
      JsonObject obj) throws UndeclaredIdentifierException {
    JsonObject attributeMapJson = obj.getAsJsonObject(ATTRIBUTES);
    HashMap<String, TypeValue> attributeMap = new HashMap<>();

    if (attributeMapJson == null) {
      return attributeMap;
    }

    for (Entry<String, JsonElement> attrEntry : attributeMapJson.entrySet()) {
      String typeName = attrEntry.getValue().getAsString();
      // global schematic type lookup?
      attributeMap.put(attrEntry.getKey(), sch.getUserDefinedType(typeName));
    }

    return attributeMap;
  }

  private Map<String, Value> getValueAttributes(Schematic sch,
      Map<String, TypeValue> expectedTypes, JsonObject obj)
      throws UndeclaredIdentifierException, UndeclaredAttributeException {
    JsonObject attributeMapJson = obj.getAsJsonObject(ATTRIBUTES);
    HashMap<String, Value> attributeMap = new HashMap<>();

    if (attributeMapJson == null) {
      return attributeMap;
    }

    // Ideally, some kind of createInstance(String) method on each TypeValue?
    TypeValue bool = sch.getUserDefinedType("Bool");
    TypeValue integer = sch.getUserDefinedType("Int");
    TypeValue str = sch.getUserDefinedType("String");

    for (Entry<String, JsonElement> attrEntry : attributeMapJson.entrySet()) {
      TypeValue type = expectedTypes.get(attrEntry.getKey());
      Value attrValue = null;

      String valueString = attrEntry.getValue().getAsString();

      if (type == null) {
        throw new UndeclaredAttributeException(attrEntry.getKey());
      }

      if (type.equals(bool)) {
        if (!(Boolean.TRUE.toString().equals(valueString) ||
              Boolean.FALSE.toString().equals(valueString))) {
          throw new IllegalArgumentException(String.format(
              "Expected boolean value of true or false, got %s", valueString));
        }
        attrValue = BooleanValue.getInstance(Boolean.parseBoolean(valueString));
      } else if (type.equals(integer)) {
        attrValue = new IntegerValue(Integer.valueOf(valueString));
      } else if (type.equals(str)) {
        attrValue = new StringValue(StringTypeValue.getInstance(), valueString);
      } else {
        throw new UndeclaredAttributeException(attrEntry.getKey());
      }

      attributeMap.put(attrEntry.getKey(), attrValue);
    }

    // TODO (max): read these, dependent on IR attribute/type overhaul


    return attributeMap;
  }

  private PortValue getPortValue(Schematic sch, String ref)
      throws UndeclaredIdentifierException {
    int delim = ref.indexOf(NODE_PORT_DELIM);
    NodeValue node = sch.getNode(ref.substring(0, delim));
    return node.getPort(ref.substring(delim + 1));
  }

  private void deserializePortTypes(Schematic sch, JsonObject in)
      throws JsonSyntaxException, MultipleDefinitionException,
      UndeclaredIdentifierException {
    for (Entry<String, JsonElement> entry : in.entrySet()) {
      Map<String, TypeValue> attributeMap = getTypeDefAttributes(sch, entry
          .getValue().getAsJsonObject());
      PortTypeValue portTypeValue = new PortTypeValue(attributeMap);

      sch.addPortType(entry.getKey(), portTypeValue);
    }
  }

  private void deserializeNodeTypes(Schematic sch, JsonObject in)
      throws JsonSyntaxException, MultipleDefinitionException,
      UndeclaredIdentifierException {
    for (Entry<String, JsonElement> entry : in.entrySet()) {

      Map<String, TypeValue> attributeMap = getTypeDefAttributes(sch, entry
          .getValue().getAsJsonObject());

      Map<String, PortTypeValue> portMap = new HashMap<>();
      JsonObject portMapJson = entry.getValue().getAsJsonObject()
          .getAsJsonObject(NODE_TYPE_PORT_MAP);

      for (Entry<String, JsonElement> portEntry : portMapJson.entrySet()) {
        portMap.put(portEntry.getKey(), sch.getPortType(portEntry.getValue()
            .getAsString()));
      }
      NodeTypeValue nodeTypeValue = new NodeTypeValue(attributeMap, portMap);

      sch.addNodeType(entry.getKey(), nodeTypeValue);
    }
  }

  private void deserializeConnectionTypes(Schematic sch, JsonObject in)
      throws MultipleDefinitionException, UndeclaredIdentifierException {
    for (Entry<String, JsonElement> entry : in.entrySet()) {
      Map<String, TypeValue> attributeMap = getTypeDefAttributes(sch, entry
          .getValue().getAsJsonObject());
      ConnectionType connectionType = new ConnectionType(attributeMap);

      sch.addConnectionType(entry.getKey(), connectionType);
    }
  }

  private void deserializeConstraintTypes(Schematic sch, JsonObject in)
      throws MultipleDefinitionException, UndeclaredIdentifierException {
    for (Entry<String, JsonElement> entry : in.entrySet()) {
      Map<String, TypeValue> attributeMap = getTypeDefAttributes(sch, entry
          .getValue().getAsJsonObject());
      ConstraintType constraintType = new ConstraintType(attributeMap);

      sch.addConstraintType(entry.getKey(), constraintType);
    }
  }

  /**
   * Node defn:
   *
   * <pre>
   * nodes: {
   *  node_one: {
   *    type: node_type,
   *    attributes: { ... },
   *    portAttrs: {
   *      port1: { ... },
   *      port2: { ... },
   *      ...
   *    }
   *  },
   *  ...
   * }
   * </pre>
   */
  private void deserializeNodes(Schematic sch, JsonObject in)
      throws SchematicException {
    for (Entry<String, JsonElement> entry : in.entrySet()) {
      JsonObject nodeDef = entry.getValue().getAsJsonObject();

      NodeTypeValue nodeType = sch
          .getNodeType(nodeDef.get(TYPE).getAsString());
      Map<String, Value> attributeMap = getValueAttributes(sch, nodeType
          .getAttributes(), nodeDef);
      Map<String, Map<String, Value>> portAttrMap = new HashMap<>();

      JsonObject portAttrJson = nodeDef.getAsJsonObject(NODE_PORT_ATTRS);

      for (Entry<String, JsonElement> p : portAttrJson.entrySet()) {
        portAttrMap.put(p.getKey(), getValueAttributes(sch, nodeType
            .getAttributes(), p.getValue().getAsJsonObject()));
      }

      NodeValue node = new NodeValue(nodeType, attributeMap, portAttrMap);
      sch.addNode(entry.getKey(), node);
    }
  }

  /**
   * <pre>
   * connections: {
   *  con_one: {
   *    type: connection_type
   *    attributes: { ... }
   *    from: nodeName:portName
   *    to: nodeName:portName
   *  },
   *  ...
   * }
   * </pre>
   */
  private void deserializeConnections(Schematic sch, JsonObject in)
      throws UndeclaredIdentifierException, UndeclaredAttributeException,
      InvalidAttributeException, MultipleAssignmentException,
      TypeMismatchException {

    for (Entry<String, JsonElement> entry : in.entrySet()) {
      JsonObject obj = entry.getValue().getAsJsonObject();

      ConnectionType conType = sch.getConnectionType(obj.get(TYPE)
          .getAsString());
      Map<String, Value> attributeMap = getValueAttributes(sch, conType
          .getAttributes(), obj);
      ConnectionValue conVal = new ConnectionValue(conType,
          getPortValue(sch, obj.get(CONNECTION_FROM).getAsString()),
          getPortValue(sch, obj.get(CONNECTION_TO).getAsString()),
          attributeMap);

      sch.addConnection(entry.getKey(), conVal);
    }
  }

  public Schematic deserialize(JsonObject in) {
    Schematic sch = new Schematic(in.get(NAME).getAsString());

    try {
      // how to do this? should we have these in the IR at all? or should they
      // just be unrolled into the base types?
      // deserializeUserDefinedTypes(sch, in.getAsJsonObject(USER_DEF_TYPES));
      deserializePortTypes(sch, in.getAsJsonObject(PORT_TYPES));
      deserializeNodeTypes(sch, in.getAsJsonObject(NODE_TYPES));
      deserializeConnectionTypes(sch, in.getAsJsonObject(CONNECTION_TYPES));
      deserializeConstraintTypes(sch, in.getAsJsonObject(CONSTRAINT_TYPES));
      deserializeNodes(sch, in.getAsJsonObject(NODES));
      deserializeConnections(sch, in.getAsJsonObject(CONNECTIONS));
      // TODO (max): constraints once they're fleshed out
    } catch (Exception e) {
      Throwables.propagate(e);
    }

    return sch;
  }
}
