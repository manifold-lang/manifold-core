package org.manifold.compiler.middle.serialization;

import static org.manifold.compiler.middle.serialization.SerializationConsts.GlobalConsts.SIGNAL_TYPE;
import static org.manifold.compiler.middle.serialization.SerializationConsts.GlobalConsts.SUPERTYPE;
import static org.manifold.compiler.middle.serialization.SerializationConsts.GlobalConsts.TYPE;
import static org.manifold.compiler.middle.serialization.SerializationConsts.UDTConsts.ARRAY_ELEMENT_TYPE;
import static org.manifold.compiler.middle.serialization.SerializationConsts.UDTConsts.INFERRED_TYPE;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.manifold.compiler.ArrayTypeValue;
import org.manifold.compiler.ConnectionValue;
import org.manifold.compiler.ConstraintType;
import org.manifold.compiler.ConstraintValue;
import org.manifold.compiler.InferredTypeValue;
import org.manifold.compiler.InvalidAttributeException;
import org.manifold.compiler.MultipleAssignmentException;
import org.manifold.compiler.MultipleDefinitionException;
import org.manifold.compiler.NodeTypeValue;
import org.manifold.compiler.NodeValue;
import org.manifold.compiler.PortTypeValue;
import org.manifold.compiler.PortValue;
import org.manifold.compiler.TypeMismatchException;
import org.manifold.compiler.TypeValue;
import org.manifold.compiler.UndeclaredAttributeException;
import org.manifold.compiler.UndeclaredIdentifierException;
import org.manifold.compiler.UserDefinedTypeValue;
import org.manifold.compiler.Value;
import org.manifold.compiler.middle.Schematic;
import org.manifold.compiler.middle.SchematicException;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class SchematicDeserializer implements SerializationConsts {

  private Gson gson = new GsonBuilder().create();

  private Map<String, TypeValue> getTypeDefAttributes(Schematic sch,
      JsonObject obj) throws UndeclaredIdentifierException {
    JsonObject attributeMapJson = obj.getAsJsonObject(GlobalConsts.ATTRIBUTES);
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
    JsonObject attributeMapJson = obj.getAsJsonObject(GlobalConsts.ATTRIBUTES);
    HashMap<String, Value> attributeMap = new HashMap<>();

    if (attributeMapJson == null) {
      return attributeMap;
    }

    for (Entry<String, JsonElement> attrEntry : attributeMapJson.entrySet()) {
      TypeValue type = expectedTypes.get(attrEntry.getKey());
      String valueString = attrEntry.getValue().getAsString();

      if (type == null) {
        throw new UndeclaredAttributeException(attrEntry.getKey());
      }

      Value attrValue = type.instantiate(valueString);
      attributeMap.put(attrEntry.getKey(), attrValue);
    }

    // TODO (max): read these, dependent on IR attribute/type overhaul


    return attributeMap;
  }

  private PortValue getPortValue(Schematic sch, String ref)
      throws UndeclaredIdentifierException {
    int delim = ref.indexOf(GlobalConsts.NODE_PORT_DELIM);
    NodeValue node = sch.getNode(ref.substring(0, delim));
    return node.getPort(ref.substring(delim + 1));
  }

  private TypeValue deserializeTypeValue(Schematic sch, JsonElement el)
      throws UndeclaredIdentifierException {
    if (el.isJsonPrimitive()) {
      String typename = el.getAsString();
      return sch.getUserDefinedType(typename);
    } else if (el.isJsonObject()) {
      JsonObject eObj = el.getAsJsonObject();
      if (eObj.has(TYPE)) {
        String typename = eObj.get(TYPE).getAsString();
        if (typename.equals("Array")) {
          // deserialize element type
          if (eObj.has(ARRAY_ELEMENT_TYPE)) {
            TypeValue elementType = deserializeTypeValue(
                sch, eObj.get(ARRAY_ELEMENT_TYPE));
            return new ArrayTypeValue(elementType);
          } else {
            throw new JsonSyntaxException("array type value '"
                + el.toString() + "' missing required element '"
                + ARRAY_ELEMENT_TYPE + "'");
          }
        } else if (typename.equals("Inferred")) {
          // deserialize element type
          if (eObj.has(INFERRED_TYPE)) {
            TypeValue elementType = deserializeTypeValue(
                sch, eObj.get(INFERRED_TYPE));
            return new InferredTypeValue(elementType);
          } else {
            throw new JsonSyntaxException("inferred type value '"
                + el.toString() + "' missing required element '"
                + ARRAY_ELEMENT_TYPE + "'");
          }
        } else {
          throw new JsonSyntaxException("unknown structured type '"
              + typename + "'");
        }
      } else {
        throw new JsonSyntaxException("structured type value '" + el.toString()
            + "' missing required attribute '" + TYPE + "'");
      }
    } else {
      throw new JsonSyntaxException("cannot deserialize type value '"
          + el.toString() + "'");
    }
  }

  private void deserializeUserDefinedTypes(Schematic sch, JsonObject in)
      throws UndeclaredIdentifierException, MultipleDefinitionException {
    if (in == null) {
      // TODO warning?
      return;
    }

    for (Entry<String, JsonElement> entry : in.entrySet()) {
      TypeValue udt = deserializeTypeValue(sch, entry.getValue());
      sch.addUserDefinedType(entry.getKey(), new UserDefinedTypeValue(udt));
    }
  }

  private void deserializePortTypes(Schematic sch, JsonObject in)
      throws JsonSyntaxException, MultipleDefinitionException,
      UndeclaredIdentifierException {

    if (in == null) {
      // TODO warning?
      return;
    }

    for (Entry<String, JsonElement> entry : in.entrySet()) {
      Map<String, TypeValue> attributeMap = getTypeDefAttributes(sch, entry
          .getValue().getAsJsonObject());

      // get signal type
      if (!(entry.getValue().getAsJsonObject().has(SIGNAL_TYPE))) {
        throw new JsonSyntaxException("port type '" + entry.getKey() + "'"
            + " does not define a signal type;"
            + " possible schematic version mismatch");
      }
      String signalTypeName = entry.getValue().getAsJsonObject()
          .get(SIGNAL_TYPE).getAsString();
      TypeValue signalType = sch.getUserDefinedType(signalTypeName);

      // get supertype if it exists
      PortTypeValue supertype = null;
      if (entry.getValue().getAsJsonObject().has(SUPERTYPE)) {
        String supertypeName = entry.getValue().getAsJsonObject()
            .get(SUPERTYPE).getAsString();
        supertype = sch.getPortType(supertypeName);
      }

      PortTypeValue portTypeValue = null;
      if (supertype == null) {
        portTypeValue = new PortTypeValue(signalType, attributeMap);
      } else {
        portTypeValue = new PortTypeValue(signalType, attributeMap, supertype);
      }

      sch.addPortType(entry.getKey(), portTypeValue);
    }
  }

  private void deserializeNodeTypes(Schematic sch, JsonObject in)
      throws JsonSyntaxException, MultipleDefinitionException,
      UndeclaredIdentifierException {

    if (in == null) {
      // TODO warning?
      return;
    }

    for (Entry<String, JsonElement> entry : in.entrySet()) {

      Map<String, TypeValue> attributeMap = getTypeDefAttributes(sch,
          entry.getValue().getAsJsonObject());

      Map<String, PortTypeValue> portMap = new HashMap<>();
      JsonObject portMapJson = entry.getValue().getAsJsonObject()
          .getAsJsonObject(NodeTypeConsts.PORT_MAP);

      for (Entry<String, JsonElement> portEntry : portMapJson.entrySet()) {
        portMap.put(portEntry.getKey(), sch.getPortType(portEntry.getValue()
            .getAsString()));
      }

      // get supertype if it exists
      NodeTypeValue supertype = null;
      if (entry.getValue().getAsJsonObject().has(SUPERTYPE)) {
        String supertypeName = entry.getValue().getAsJsonObject()
            .get(SUPERTYPE).getAsString();
        supertype = sch.getNodeType(supertypeName);
      }

      NodeTypeValue nodeTypeValue;
      if (supertype == null) {
        nodeTypeValue = new NodeTypeValue(attributeMap, portMap);
      } else {
        nodeTypeValue = new NodeTypeValue(attributeMap, portMap, supertype);
      }

      sch.addNodeType(entry.getKey(), nodeTypeValue);
    }
  }

  private void deserializeConstraintTypes(Schematic sch, JsonObject in)
      throws MultipleDefinitionException, UndeclaredIdentifierException {

    if (in == null) {
      // TODO warning?
      return;
    }

    for (Entry<String, JsonElement> entry : in.entrySet()) {
      Map<String, TypeValue> attributeMap = getTypeDefAttributes(sch,
          entry.getValue().getAsJsonObject());

      ConstraintType supertype = null;
      if (entry.getValue().getAsJsonObject().has(SUPERTYPE)) {
        String supertypeName = entry.getValue().getAsJsonObject()
            .get(SUPERTYPE).getAsString();
        supertype = sch.getConstraintType(supertypeName);
      }

      ConstraintType constraintType = null;
      if (supertype == null) {
        constraintType = new ConstraintType(attributeMap);
      } else {
        constraintType = new ConstraintType(attributeMap, supertype);
      }

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

    if (in == null) {
      // TODO warning?
      return;
    }

    for (Entry<String, JsonElement> entry : in.entrySet()) {
      JsonObject nodeDef = entry.getValue().getAsJsonObject();

      NodeTypeValue nodeType = sch
          .getNodeType(nodeDef.get(GlobalConsts.TYPE).getAsString());
      Map<String, Value> attributeMap = getValueAttributes(sch, nodeType
          .getAttributes(), nodeDef);
      Map<String, Map<String, Value>> portAttrMap = new HashMap<>();

      JsonObject portAttrJson = nodeDef.getAsJsonObject(NodeConsts.PORT_ATTRS);

      for (Entry<String, JsonElement> p : portAttrJson.entrySet()) {
        if (!(nodeType.getPorts().containsKey(p.getKey()))) {
          throw new UndeclaredIdentifierException(p.getKey());
        }
        Map<String, TypeValue> expectedPortAttributes =
            nodeType.getPorts().get(p.getKey()).getAttributes();
        portAttrMap.put(p.getKey(), getValueAttributes(
            sch,
            // here we want the port attribute map
            expectedPortAttributes,
            p.getValue().getAsJsonObject()));
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

    if (in == null) {
      // TODO warning?
      return;
    }

    for (Entry<String, JsonElement> entry : in.entrySet()) {
      JsonObject obj = entry.getValue().getAsJsonObject();

      // TODO: read attributes; non-trivial since we no longer have their type
      Map<String, Value> attributeMap = new HashMap<>();
      ConnectionValue conVal = new ConnectionValue(
          getPortValue(sch, obj.get(ConnectionConsts.FROM).getAsString()),
          getPortValue(sch, obj.get(ConnectionConsts.TO).getAsString()),
          attributeMap);

      sch.addConnection(entry.getKey(), conVal);
    }
  }

  /**
   * <pre>
   * constraints: {
   *  con_one: {
   *    type: constraint_type
   *    attributes: { ... }
   *  },
   *  ...
   * }
   * </pre>
   */
  private void deserializeConstraints(Schematic sch, JsonObject in)
      throws UndeclaredIdentifierException, UndeclaredAttributeException,
      InvalidAttributeException, MultipleAssignmentException,
      TypeMismatchException {

    if (in == null) {
      // TODO warning?
      return;
    }

    for (Entry<String, JsonElement> entry : in.entrySet()) {
      JsonObject obj = entry.getValue().getAsJsonObject();

      ConstraintType conType = sch.getConstraintType(obj.get(GlobalConsts.TYPE)
          .getAsString());
      Map<String, Value> attributeMap = getValueAttributes(sch, conType
          .getAttributes(), obj);
      ConstraintValue conVal = new ConstraintValue(conType,
          attributeMap);

      sch.addConstraint(entry.getKey(), conVal);
    }
  }

  public Schematic deserialize(JsonObject in) {
    Schematic sch = new Schematic(
        in.get(GlobalConsts.SCHEMATIC_NAME).getAsString());

    try {
      deserializeUserDefinedTypes(sch, in.getAsJsonObject(
          SchematicConsts.USER_DEF_TYPES));
      deserializePortTypes(sch, in.getAsJsonObject(SchematicConsts.PORT_TYPES));
      deserializeNodeTypes(sch, in.getAsJsonObject(SchematicConsts.NODE_TYPES));
      deserializeConstraintTypes(sch,
          in.getAsJsonObject(SchematicConsts.CONSTRAINT_TYPES));
      deserializeNodes(sch, in.getAsJsonObject(SchematicConsts.NODE_DEFS));
      deserializeConnections(sch,
          in.getAsJsonObject(SchematicConsts.CONNECTION_DEFS));
      deserializeConstraints(sch,
          in.getAsJsonObject(SchematicConsts.CONSTRAINT_DEFS));
    } catch (Exception e) {
      Throwables.propagate(e);
    }

    return sch;
  }
}
