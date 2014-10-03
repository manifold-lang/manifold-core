package org.manifold.compiler.middle.serialization;

import static org.manifold.compiler.middle.serialization.SerializationConsts.GlobalConsts.ATTRIBUTES;
import static org.manifold.compiler.middle.serialization.SerializationConsts.GlobalConsts.SCHEMATIC_NAME;
import static org.manifold.compiler.middle.serialization.SerializationConsts.GlobalConsts.SUPERTYPE;
import static org.manifold.compiler.middle.serialization.SerializationConsts.NodeConsts.PORT_ATTRS;
import static org.manifold.compiler.middle.serialization.SerializationConsts.NodeTypeConsts.PORT_MAP;
import static org.manifold.compiler.middle.serialization.SerializationConsts.SchematicConsts.CONNECTION_TYPES;
import static org.manifold.compiler.middle.serialization.SerializationConsts.SchematicConsts.CONSTRAINT_TYPES;
import static org.manifold.compiler.middle.serialization.SerializationConsts.SchematicConsts.NODE_DEFS;
import static org.manifold.compiler.middle.serialization.SerializationConsts.SchematicConsts.NODE_TYPES;
import static org.manifold.compiler.middle.serialization.SerializationConsts.SchematicConsts.PORT_TYPES;
import static org.manifold.compiler.middle.serialization.SerializationConsts.SchematicConsts.USER_DEF_TYPES;


import java.util.HashMap;
import java.util.Map;


import org.manifold.compiler.ConnectionType;
import org.manifold.compiler.ConnectionValue;
import org.manifold.compiler.ConstraintType;
import org.manifold.compiler.ConstraintValue;
import org.manifold.compiler.NodeTypeValue;
import org.manifold.compiler.NodeValue;
import org.manifold.compiler.PortTypeValue;
import org.manifold.compiler.TypeDependencyTree;
import org.manifold.compiler.TypeTypeValue;
import org.manifold.compiler.TypeValue;
import org.manifold.compiler.Value;
import org.manifold.compiler.middle.Schematic;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class SchematicSerializer {
  private JsonObject schJson;

  // reverse maps of java obj -> name in schematics
  private Map<TypeValue, String> rUserDefTypeMap;
  private Map<PortTypeValue, String> rPortTypeMap;
  private Map<NodeTypeValue, String> rNodeTypeMap;
  private Map<ConnectionType, String> rConnectionTypeMap;
  private Map<ConstraintType, String> rConstraintTypeMap;

  private Gson gson;

  private SchematicSerializer(Schematic sch) {
    schJson = new JsonObject();
    schJson.addProperty(SCHEMATIC_NAME, sch.getName());
    rUserDefTypeMap = new HashMap<>();
    rPortTypeMap = new HashMap<>();
    rNodeTypeMap = new HashMap<>();
    rConnectionTypeMap = new HashMap<>();
    rConstraintTypeMap = new HashMap<>();

    gson = new GsonBuilder().create();
  }

  private JsonObject serializeTypeAttr(Map<String, TypeValue> typeAttr) {
    JsonObject typeAttrJson = new JsonObject();

    typeAttr.forEach((key, val) -> typeAttrJson.addProperty(key,
        rUserDefTypeMap.get(val)));

    return typeAttrJson;
  }

  private JsonObject serializeValueAttr(Map<String, Value> valueAttr) {
    JsonObject attrs = new JsonObject();
    valueAttr.forEach((key, val) -> attrs.addProperty(key, val.toString()));
    return attrs;
  }

  public void addUserDefType(Map<String, TypeValue> userDefTypes) {
    JsonObject collection = new JsonObject();

    userDefTypes.forEach((key, val) -> {
      rUserDefTypeMap.put(val, key);
      if (key.equals("Bool") || key.equals("Int") || key.equals("String")) {
        return;
      }
    });
    schJson.add(USER_DEF_TYPES, collection);
  }

  public void addPortTypes(Map<String, PortTypeValue> portTypes) {
    JsonObject collection = new JsonObject();

    portTypes.forEach((key, val) -> {
      rPortTypeMap.put(val, key);

      JsonObject single = new JsonObject();
      single.add(ATTRIBUTES, serializeTypeAttr(val.getAttributes()));
      collection.add(key, single);
    });

    schJson.add(PORT_TYPES, collection);
  }

  public void addNodeTypes(Map<String, NodeTypeValue> nodeTypes) {
    JsonObject collection = new JsonObject();

    /*
    nodeTypes.forEach((key, val) -> {
      rNodeTypeMap.put(val, key);

      JsonObject single = new JsonObject();
      single.add(ATTRIBUTES, serializeTypeAttr(val.getAttributes()));

      JsonObject ports = new JsonObject();
      val.getPorts().forEach((pkey, pval) -> {
        ports.addProperty(pkey, rPortTypeMap.get(pval));
      });

      single.add(PORT_MAP, ports);
      collection.add(key, single);
    });
    */
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

  public void addConnectionTypes(Map<String, ConnectionType> conTypes) {
    JsonObject collection = new JsonObject();

    conTypes.forEach((key, val) -> {
      rConnectionTypeMap.put(val, key);

      JsonObject single = new JsonObject();
      single.add(ATTRIBUTES, serializeTypeAttr(val.getAttributes()));
      collection.add(key, single);
    });

    schJson.add(CONNECTION_TYPES, collection);
  }

  public void addConstraintTypes(Map<String, ConstraintType> constraintTypes) {
    JsonObject collection = new JsonObject();

    constraintTypes.forEach((key, val) -> {
      rConstraintTypeMap.put(val, key);

      JsonObject single = new JsonObject();
      single.add(ATTRIBUTES, serializeTypeAttr(val.getAttributes()));
      collection.add(key, single);
    });

    schJson.add(CONSTRAINT_TYPES, collection);
  }

  public void addNodes(Map<String, NodeValue> nodes) {
    JsonObject collection = new JsonObject();

    nodes.forEach((key, val) -> {
      JsonObject single = new JsonObject();
      single.add(ATTRIBUTES, serializeValueAttr(val.getAttributes().getAll()));

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

  }

  public void addConstraints(Map<String, ConstraintValue> constraints) {

  }

  public JsonObject getJson() {
    return schJson;
  }

  public static JsonObject serialize(Schematic sch) {
    SchematicSerializer serializer = new SchematicSerializer(sch);
    serializer.addUserDefType(sch.getUserDefinedTypes());
    serializer.addPortTypes(sch.getPortTypes());
    serializer.addNodeTypes(sch.getNodeTypes());
    serializer.addConnectionTypes(sch.getConnectionTypes());
    serializer.addConstraintTypes(sch.getConstraintTypes());

    serializer.addNodes(sch.getNodes());
    serializer.addConnections(sch.getConnections());
    serializer.addConstraints(sch.getConstraints());
    return serializer.getJson();
  }
}
