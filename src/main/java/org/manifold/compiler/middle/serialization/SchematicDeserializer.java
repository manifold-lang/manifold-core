package org.manifold.compiler.middle.serialization;

import java.util.HashMap;
import java.util.Map.Entry;

import org.manifold.compiler.ConnectionType;
import org.manifold.compiler.ConstraintType;
import org.manifold.compiler.MultipleDefinitionException;
import org.manifold.compiler.NodeTypeValue;
import org.manifold.compiler.PortTypeValue;
import org.manifold.compiler.TypeValue;
import org.manifold.compiler.UndeclaredIdentifierException;
import org.manifold.compiler.middle.Schematic;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class SchematicDeserializer {

  private static final String NAME = "name";
  private static final String ATTRIBUTES = "attributes";
  private static final String USER_DEF_TYPES = "userDefinedTypes";
  private static final String PORT_TYPES = "portTypes";
  private static final String NODE_TYPES = "nodeTypes";
  private static final String NODE_TYPE_PORT_MAP = "ports";
  private static final String CONNECTION_TYPES = "connectionTypes";
  private static final String CONSTRAINT_TYPES = "constraintTypes";

  private Gson gson = new GsonBuilder().create();

  private HashMap<String, TypeValue> getAttributes(Schematic sch, JsonObject obj)
      throws UndeclaredIdentifierException {
    JsonObject attributeMapJson = obj.getAsJsonObject(ATTRIBUTES);
    HashMap<String, TypeValue> attributeMap = new HashMap<>();

    for (Entry<String, JsonElement> attrEntry : attributeMapJson.entrySet()) {
      String typeName = attrEntry.getValue().getAsString();
      // global schematic type lookup?
      attributeMap.put(attrEntry.getKey(), sch.getUserDefinedType(typeName));
    }

    return attributeMap;
  }

  private void deserializePortTypes(Schematic sch, JsonObject in)
      throws JsonSyntaxException, MultipleDefinitionException,
      UndeclaredIdentifierException {
    for (Entry<String, JsonElement> entry : in.entrySet()) {
      HashMap<String, TypeValue> attributeMap = getAttributes(sch, entry
          .getValue().getAsJsonObject());
      PortTypeValue portTypeValue = new PortTypeValue(attributeMap);

      sch.addPortType(entry.getKey(), portTypeValue);
    }
  }

  private void deserializeNodeTypes(Schematic sch, JsonObject in)
      throws JsonSyntaxException, MultipleDefinitionException,
      UndeclaredIdentifierException {
    for (Entry<String, JsonElement> entry : in.entrySet()) {

      HashMap<String, TypeValue> attributeMap = getAttributes(sch, entry
          .getValue().getAsJsonObject());

      HashMap<String, PortTypeValue> portMap = new HashMap<>();
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
      HashMap<String, TypeValue> attributeMap = getAttributes(sch, entry
          .getValue().getAsJsonObject());
      ConnectionType connectionType = new ConnectionType(attributeMap);

      sch.addConnectionType(entry.getKey(), connectionType);
    }
  }

  private void deserializeConstraintTypes(Schematic sch, JsonObject in)
      throws MultipleDefinitionException, UndeclaredIdentifierException {
    for (Entry<String, JsonElement> entry : in.entrySet()) {
      HashMap<String, TypeValue> attributeMap = getAttributes(sch, entry
          .getValue().getAsJsonObject());
      ConstraintType constraintType = new ConstraintType(attributeMap);

      sch.addConstraintType(entry.getKey(), constraintType);
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
    } catch (Exception e) {
      Throwables.propagate(e);
    }

    return sch;
  }
}
