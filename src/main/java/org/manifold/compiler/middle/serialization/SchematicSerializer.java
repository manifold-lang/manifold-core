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
import static org.manifold.compiler.middle.serialization.SerializationConsts.SchematicConsts.CONSTRAINT_DEFS;
import static org.manifold.compiler.middle.serialization.SerializationConsts.SchematicConsts.CONSTRAINT_TYPES;
import static org.manifold.compiler.middle.serialization.SerializationConsts.SchematicConsts.NODE_DEFS;
import static org.manifold.compiler.middle.serialization.SerializationConsts.SchematicConsts.NODE_TYPES;
import static org.manifold.compiler.middle.serialization.SerializationConsts.SchematicConsts.PORT_TYPES;
import static org.manifold.compiler.middle.serialization.SerializationConsts.SchematicConsts.USER_DEF_TYPES;
import static org.manifold.compiler.middle.serialization.SerializationConsts.UDTConsts.ARRAY_ELEMENT_TYPE;

import java.util.HashMap;
import java.util.Map;

import org.manifold.compiler.ArrayTypeValue;
import org.manifold.compiler.BooleanTypeValue;
import org.manifold.compiler.ConnectionTypeValue;
import org.manifold.compiler.ConnectionValue;
import org.manifold.compiler.ConstraintType;
import org.manifold.compiler.ConstraintValue;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class SchematicSerializer {
  private Schematic schematic;
  private JsonObject schJson;

  // reverse maps of java obj -> name in schematics
  private Map<UserDefinedTypeValue, String> rUserDefTypeMap;
  private Map<PortTypeValue, String> rPortTypeMap;
  private Map<NodeTypeValue, String> rNodeTypeMap;
  private Map<ConnectionTypeValue, String> rConnectionTypeMap;
  private Map<ConstraintType, String> rConstraintTypeMap;

  private Gson gson;

  private SchematicSerializer(Schematic sch) {
    schematic = sch;
    schJson = new JsonObject();
    schJson.addProperty(SCHEMATIC_NAME, sch.getName());
    rUserDefTypeMap = new HashMap<>();
    rPortTypeMap = new HashMap<>();
    rNodeTypeMap = new HashMap<>();
    rConnectionTypeMap = new HashMap<>();
    rConstraintTypeMap = new HashMap<>();
    gson = new GsonBuilder().create();
  }

  private JsonObject serializeTypeAttr(
      Map<String, TypeValue> typeAttr) {
    JsonObject typeAttrJson = new JsonObject();

    typeAttr.forEach((key, val) -> {
        typeAttrJson.addProperty(key, rUserDefTypeMap.get(val));
      });

    return typeAttrJson;
  }

  private JsonObject serializeValueAttr(Map<String, Value> valueAttr) {
    JsonObject attrs = new JsonObject();
    valueAttr.forEach((key, val) -> attrs.addProperty(key, val.toString()));
    return attrs;
  }

  // serialize from/to ports in the form "nodeName:portName"
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

    userDefTypes.forEach((key, val) -> {
        rUserDefTypeMap.put(val, key);
        // do not serialize primitive types
        if (PRIMITIVE_TYPES.keySet().contains(key)) {
          return;
        }

        // inspect the aliased value
        TypeValue aliasedVal = val.getTypeAlias();

        if (aliasedVal.isSubtypeOf(BooleanTypeValue.getInstance())) {
          collection.add(key, new JsonPrimitive("Bool"));

        } else if (aliasedVal instanceof ArrayTypeValue) {
          // TODO use a type dependency tree/graph
          // to ensure we don't have a cycle
          ArrayTypeValue arrayVal = (ArrayTypeValue) aliasedVal;
          JsonObject arrayJson = new JsonObject();
          arrayJson.add(TYPE, new JsonPrimitive("Array"));
          arrayJson.add(ARRAY_ELEMENT_TYPE, new JsonPrimitive(
              rUserDefTypeMap.get(arrayVal.getElementType())));
          collection.add(key, arrayJson);
        } else {
          throw new UndefinedBehaviourError(
              "don't know how to serialize TypeValue '" + val.toString() + "'");
        }
      });

    schJson.add(USER_DEF_TYPES, collection);
  }

  public void addPortTypes(Map<String, PortTypeValue> portTypes) {
    JsonObject collection = new JsonObject();

    TypeDependencyTree typeDeps = new TypeDependencyTree();
    portTypes.forEach((key, val) -> {
        typeDeps.addType(val);
        rPortTypeMap.put(val, key);
      });
    // now add each PortTypeValue to the collection
    typeDeps.forEachDFS((t) -> {
        PortTypeValue val = (PortTypeValue) t;
        String key = rPortTypeMap.get(val);

        JsonObject single = new JsonObject();

        TypeValue supertype = t.getSupertype();
        if (!(supertype.equals(TypeTypeValue.getInstance()))) {
          single.addProperty(SUPERTYPE, rPortTypeMap.get(supertype));
        }

        TypeValue signalType = val.getSignalType();
        single.addProperty(SIGNAL_TYPE, rUserDefTypeMap.get(signalType));

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
        rNodeTypeMap.put(val, key);
      });
    // now add each NodeTypeValue to the collection
    typeDeps.forEachDFS((t) -> {
        NodeTypeValue val = (NodeTypeValue) t;
        String key = rNodeTypeMap.get(val);

        JsonObject single = new JsonObject();
        single.add(ATTRIBUTES, serializeTypeAttr(val.getAttributes()));

        JsonObject ports = new JsonObject();
        val.getPorts().forEach((pkey, pval) -> {
            ports.addProperty(pkey, rPortTypeMap.get(pval));
          });

        TypeValue supertype = t.getSupertype();
        if (!(supertype.equals(TypeTypeValue.getInstance()))) {
          single.addProperty(SUPERTYPE, rNodeTypeMap.get(supertype));
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
        rConstraintTypeMap.put(val, key);
      });
    // now add each ConstraintType to the collection
    typeDeps.forEachDFS((t) -> {
        ConstraintType val = (ConstraintType) t;
        String key = rConstraintTypeMap.get(val);

        JsonObject single = new JsonObject();
        single.add(ATTRIBUTES, serializeTypeAttr(val.getAttributes()));

        TypeValue supertype = t.getSupertype();
        if (!(supertype.equals(TypeTypeValue.getInstance()))) {
          single.addProperty(SUPERTYPE, rConstraintTypeMap.get(supertype));
        }

        collection.add(key, single);
      });

    schJson.add(CONSTRAINT_TYPES, collection);
  }

  public void addNodes(Map<String, NodeValue> nodes) {
    JsonObject collection = new JsonObject();

    nodes.forEach((key, val) -> {
        JsonObject single = new JsonObject();
        single.add(TYPE, new JsonPrimitive(rNodeTypeMap.get(val.getType())));
        single.add(ATTRIBUTES, serializeValueAttr(
            val.getAttributes().getAll()));
        JsonObject portAttrs = new JsonObject();
        val.getPorts().forEach(
              (pkey, pval) -> portAttrs.add(
                  pkey, serializeValueAttr(pval.getAttributes().getAll())));
        single.add(PORT_ATTRS, portAttrs);
        collection.add(key, single);
      });

    schJson.add(NODE_DEFS, collection);
  }

  public void addConnections(Map<String, ConnectionValue> connections) {
    JsonObject collection = new JsonObject();

    connections.forEach((key, val) -> {
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
        JsonObject single = new JsonObject();
        single.add(TYPE, new JsonPrimitive(rConstraintTypeMap.get(
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
    serializer.addPortTypes(sch.getPortTypes());
    serializer.addNodeTypes(sch.getNodeTypes());
    serializer.addConstraintTypes(sch.getConstraintTypes());

    serializer.addNodes(sch.getNodes());
    serializer.addConnections(sch.getConnections());
    serializer.addConstraints(sch.getConstraints());
    return serializer.getJson();
  }
}
