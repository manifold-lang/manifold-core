package org.manifold.compiler.middle.serialization;

import static org.manifold.compiler.middle.serialization.SerializationConsts.ConnectionConsts.FROM;
import static org.manifold.compiler.middle.serialization.SerializationConsts.ConnectionConsts.TO;
import static org.manifold.compiler.middle.serialization.SerializationConsts.GlobalConsts.ATTRIBUTES;
import static org.manifold.compiler.middle.serialization.SerializationConsts.GlobalConsts.SCHEMATIC_NAME;
import static org.manifold.compiler.middle.serialization.SerializationConsts.GlobalConsts.SIGNAL_TYPE;
import static org.manifold.compiler.middle.serialization.SerializationConsts.GlobalConsts.SUPERTYPE;
import static org.manifold.compiler.middle.serialization.SerializationConsts.GlobalConsts.TYPE;
import static org.manifold.compiler.middle.serialization.SerializationConsts.NodeConsts.PORT_ATTRS;
import static org.manifold.compiler.middle.serialization.SerializationConsts.NodeTypeConsts.PORT_MAP;
import static org.manifold.compiler.middle.serialization.SerializationConsts.PrimitiveTypes.PRIMITIVE_TYPES;
import static org.manifold.compiler.middle.serialization.SerializationConsts.SchematicConsts.CONNECTION_DEFS;
import static org.manifold.compiler.middle.serialization.SerializationConsts.SchematicConsts.CONNECTION_TYPES;
import static org.manifold.compiler.middle.serialization.SerializationConsts.SchematicConsts.CONSTRAINT_DEFS;
import static org.manifold.compiler.middle.serialization.SerializationConsts.SchematicConsts.CONSTRAINT_TYPES;
import static org.manifold.compiler.middle.serialization.SerializationConsts.SchematicConsts.NODE_DEFS;
import static org.manifold.compiler.middle.serialization.SerializationConsts.SchematicConsts.NODE_TYPES;
import static org.manifold.compiler.middle.serialization.SerializationConsts.SchematicConsts.PORT_TYPES;
import static org.manifold.compiler.middle.serialization.SerializationConsts.SchematicConsts.USER_DEF_TYPES;
import static org.manifold.compiler.middle.serialization.SerializationConsts.UDTConsts.ARRAY_ELEMENT_TYPE;
import static org.manifold.compiler.middle.serialization.SerializationConsts.UDTConsts.INFERRED_TYPE;

import java.util.HashMap;
import java.util.Map;

import org.manifold.compiler.ArrayTypeValue;
import org.manifold.compiler.BooleanTypeValue;
import org.manifold.compiler.ConnectionTypeValue;
import org.manifold.compiler.ConnectionValue;
import org.manifold.compiler.ConstraintType;
import org.manifold.compiler.ConstraintValue;
import org.manifold.compiler.InferredTypeValue;
import org.manifold.compiler.NodeTypeValue;
import org.manifold.compiler.NodeValue;
import org.manifold.compiler.PortTypeValue;
import org.manifold.compiler.PortValue;
import org.manifold.compiler.TypeDependencyTree;
import org.manifold.compiler.TypeTypeValue;
import org.manifold.compiler.TypeValue;
import org.manifold.compiler.UndefinedBehaviourError;
import org.manifold.compiler.UserDefinedTypeValue;
import org.manifold.compiler.Value;
import org.manifold.compiler.middle.Schematic;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class SchematicSerializer {
  private Schematic schematic;
  private JsonObject schJson;

  // This is actually duplicated (though less generally) in schematic itself
  // TODO: perhaps merge the 2?
  // reverse maps of java obj -> name in schematics
  private Map<Value, String> rValueMap;

  private SchematicSerializer(Schematic sch) {
    schematic = sch;
    schJson = new JsonObject();
    schJson.addProperty(SCHEMATIC_NAME, sch.getName());
    rValueMap = new HashMap<>();
  }

  private JsonObject serializeTypeAttr(
      Map<String, TypeValue> typeAttr) {
    JsonObject typeAttrJson = new JsonObject();

    typeAttr.forEach((key, val) -> {
        // ideally these might want to be in schematic as well just so we
        // don't have to do this special case here
        JsonElement type = null;
        if (val instanceof InferredTypeValue) {
          InferredTypeValue inferredVal = (InferredTypeValue) val;
          JsonObject inferredType = new JsonObject();
          inferredType.add(TYPE, new JsonPrimitive("Inferred"));
          inferredType.add(INFERRED_TYPE,
              new JsonPrimitive(rValueMap.get(inferredVal.getInferredType())));
          type = inferredType;
        } else {
          type = new JsonPrimitive(rValueMap.get(val));
        }
        typeAttrJson.add(key, type);
      });

    return typeAttrJson;
  }

  private JsonObject serializeValueAttr(Map<String, Value> valueAttr) {
    JsonObject attrs = new JsonObject();
    // unlike types which are always back references, values come in 2 flavours
    // if they exist in the rValueMap, then they are a reference
    // otherwise they are a primitive and we call toString
    valueAttr.forEach((key, val) -> {
        JsonElement elem = rValueMap.containsKey(val) ?
            new JsonPrimitive(rValueMap.get(val)) :
            val.toJson();
        attrs.add(key, elem);
      });
    return attrs;
  }

  // serializeAsAttr from/to ports in the form "nodeName:portName"
  private JsonPrimitive serializeConnectedPort(PortValue port) {
    String nodeName = schematic.getNodeName(port.getParent());
    String portName = null;
    for (Map.Entry<String, PortValue> entry
        : port.getParent().getPorts().entrySet()) {
      if (entry.getValue().equals(port)) {
        portName = entry.getKey();
        break;
      }
    }
    if (portName == null) {
      throw new UndefinedBehaviourError("node '" + nodeName
          + "' does not contain serialized port");
    }
    String portDesc = nodeName + ":" + portName;
    return new JsonPrimitive(portDesc);
  }

  public void addUserDefinedTypes(
      Map<String, UserDefinedTypeValue> userDefTypes) {
    JsonObject collection = new JsonObject();

    PRIMITIVE_TYPES.forEach((key, val) -> rValueMap.put(val, key));

    userDefTypes.forEach((key, val) -> {
        rValueMap.put(val, key);
        // do not serializeAsAttr primitive types
        if (PRIMITIVE_TYPES.keySet().contains(key)) {
          return;
        }

        // inspect the aliased value
        TypeValue aliasedVal = val.getTypeAlias();
        collection.add(key, serializeTypeValue(aliasedVal));
      });

    schJson.add(USER_DEF_TYPES, collection);
  }

  // Convert a TypeValue to a string/object that can be deserialized
  // to the same TypeValue.
  // (val is not a UserDefinedTypeValue)
  private JsonElement serializeTypeValue(TypeValue val) {

    if (val instanceof UserDefinedTypeValue) {
      return new JsonPrimitive(((UserDefinedTypeValue) val).getName());
    } else if (val instanceof BooleanTypeValue) {
      // TODO: Rewrite? It's possible for Inferred and possibly Array to be
      // serialized without a UserDefinedType (from frontend code),
      // but trying to serialize a raw BooleanTypeValue is probably only
      // possible in unit tests.
      // Same for String, Integer, Real, etc...
      return new JsonPrimitive("Bool");
    } else if (val instanceof ArrayTypeValue) {
      // TODO use a type dependency tree/graph
      // to ensure we don't have a cycle
      ArrayTypeValue arrayVal = (ArrayTypeValue) val;
      JsonObject arrayJson = new JsonObject();
      arrayJson.add(TYPE, new JsonPrimitive("Array"));
      arrayJson.add(ARRAY_ELEMENT_TYPE,
          serializeTypeValue(arrayVal.getElementType()));
      return arrayJson;
    } else if (val instanceof InferredTypeValue) {
      // TODO use a type dependency tree/graph
      // to ensure we don't have a cycle here as well
      InferredTypeValue inferredVal = (InferredTypeValue) val;
      JsonObject inferredJson = new JsonObject();
      inferredJson.add(TYPE, new JsonPrimitive("Inferred"));
      inferredJson.add(INFERRED_TYPE,
          serializeTypeValue(inferredVal.getInferredType()));
      return inferredJson;
    } else {
      throw new UndefinedBehaviourError(
          "don't know how to serialize TypeValue '" + val.toString() + "'");
    }
  }

  public void addPortTypes(Map<String, PortTypeValue> portTypes) {
    JsonObject collection = new JsonObject();

    TypeDependencyTree typeDeps = new TypeDependencyTree();
    portTypes.forEach((key, val) -> {
        typeDeps.addType(val);
        rValueMap.put(val, key);
      });
    // now add each PortTypeValue to the collection
    typeDeps.forEachDFS((t) -> {
        PortTypeValue val = (PortTypeValue) t;
        String key = rValueMap.get(val);

        JsonObject single = new JsonObject();

        TypeValue supertype = t.getSupertype();
        if (!(supertype.equals(TypeTypeValue.getInstance()))) {
          single.addProperty(SUPERTYPE, rValueMap.get(supertype));
        }

        TypeValue signalType = val.getSignalType();
        single.addProperty(SIGNAL_TYPE, rValueMap.get(signalType));

        single.add(ATTRIBUTES, serializeTypeAttr(val.getAttributes()));
        collection.add(key, single);

      });

    schJson.add(PORT_TYPES, collection);
  }

  public void addNodeTypes(Map<String, NodeTypeValue> nodeTypes) {
    JsonObject collection = new JsonObject();

    TypeDependencyTree typeDeps = new TypeDependencyTree();
    nodeTypes.forEach((key, val) -> {
        typeDeps.addType(val);
        rValueMap.put(val, key);
      });
    // now add each NodeTypeValue to the collection
    typeDeps.forEachDFS((t) -> {
        NodeTypeValue val = (NodeTypeValue) t;
        String key = rValueMap.get(val);

        JsonObject single = new JsonObject();
        single.add(ATTRIBUTES, serializeTypeAttr(val.getAttributes()));

        JsonObject ports = new JsonObject();
        val.getPorts().forEach((pkey, pval) -> {
            ports.addProperty(pkey, rValueMap.get(pval));
          });

        TypeValue supertype = t.getSupertype();
        if (!(supertype.equals(TypeTypeValue.getInstance()))) {
          single.addProperty(SUPERTYPE, rValueMap.get(supertype));
        }

        single.add(PORT_MAP, ports);
        collection.add(key, single);
      });

    schJson.add(NODE_TYPES, collection);
  }

  public void addConstraintTypes(Map<String, ConstraintType> constraintTypes) {
    JsonObject collection = new JsonObject();

    TypeDependencyTree typeDeps = new TypeDependencyTree();
    constraintTypes.forEach((key, val) -> {
        typeDeps.addType(val);
        rValueMap.put(val, key);
      });
    // now add each ConstraintType to the collection
    typeDeps.forEachDFS((t) -> {
        ConstraintType val = (ConstraintType) t;
        String key = rValueMap.get(val);

        JsonObject single = new JsonObject();
        single.add(ATTRIBUTES, serializeTypeAttr(val.getAttributes()));

        TypeValue supertype = t.getSupertype();
        if (!(supertype.equals(TypeTypeValue.getInstance()))) {
          single.addProperty(SUPERTYPE, rValueMap.get(supertype));
        }

        collection.add(key, single);
      });

    schJson.add(CONSTRAINT_TYPES, collection);
  }

  public void addConnectionTypes(
          Map<String, ConnectionTypeValue> connectionTypes) {
    JsonObject collection = new JsonObject();

    connectionTypes.forEach((key, val) -> rValueMap.put(val, key));

    schJson.add(CONNECTION_TYPES, collection);
  }

  public void addNodes(Map<String, NodeValue> nodes) {
    JsonObject collection = new JsonObject();

    nodes.forEach((key, val) -> {
        rValueMap.put(val, key);
        JsonObject single = new JsonObject();
        single.add(TYPE, new JsonPrimitive(rValueMap.get(val.getType())));
        single.add(ATTRIBUTES, serializeValueAttr(
            val.getAttributes().getAll()));
        JsonObject portAttrs = new JsonObject();
        val.getPorts().forEach((pkey, pval) -> {
            rValueMap.put(pval, pkey);
            portAttrs.add(
                pkey, serializeValueAttr(pval.getAttributes().getAll()));
          });
        single.add(PORT_ATTRS, portAttrs);
        collection.add(key, single);
      });

    schJson.add(NODE_DEFS, collection);
  }

  public void addConnections(Map<String, ConnectionValue> connections) {
    JsonObject collection = new JsonObject();

    connections.forEach((key, val) -> {
        rValueMap.put(val, key);
        JsonObject single = new JsonObject();
        single.add(ATTRIBUTES, serializeValueAttr(
            val.getAttributes().getAll()));
        single.add(FROM, serializeConnectedPort(val.getFrom()));
        single.add(TO, serializeConnectedPort(val.getTo()));
        collection.add(key, single);
      });

    schJson.add(CONNECTION_DEFS, collection);
  }

  public void addConstraints(Map<String, ConstraintValue> constraints) {
    JsonObject collection = new JsonObject();
    constraints.forEach((key, val) -> {
        rValueMap.put(val, key);
        JsonObject single = new JsonObject();
        single.add(TYPE, new JsonPrimitive(rValueMap.get(
            val.getType())));
        single.add(ATTRIBUTES, serializeValueAttr(
            val.getAttributes().getAll()));
        collection.add(key, single);
      });
    schJson.add(CONSTRAINT_DEFS, collection);
  }

  public JsonObject getJson() {
    return schJson;
  }

  public static JsonObject serialize(Schematic sch) {
    SchematicSerializer serializer = new SchematicSerializer(sch);
    serializer.addUserDefinedTypes(sch.getUserDefinedTypes());
    serializer.addConnectionTypes(sch.getConnectionTypes());
    serializer.addPortTypes(sch.getPortTypes());
    serializer.addNodeTypes(sch.getNodeTypes());
    serializer.addConstraintTypes(sch.getConstraintTypes());

    serializer.addNodes(sch.getNodes());
    serializer.addConnections(sch.getConnections());
    serializer.addConstraints(sch.getConstraints());
    return serializer.getJson();
  }
}
