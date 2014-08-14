package org.manifold.compiler.middle.serialization;

public interface SeralizationConsts {
  public interface GlobalConsts {
    String NODE_PORT_DELIM = ":";
    String SCHEMATIC_NAME = "name";
    String ATTRIBUTES = "attributes";
    String TYPE = "type";
  }

  public interface SchematicConsts {
    String USER_DEF_TYPES = "userDefinedTypes";
    String PORT_TYPES = "portTypes";
    String NODE_TYPES = "nodeTypes";
    String CONNECTION_TYPES = "connectionTypes";
    String CONSTRAINT_TYPES = "constraintTypes";

    String NODE_DEFS = "nodes";
    String CONNECTION_DEFS = "connections";
    String CONSTRAINT_DEFS = "constraints";
  }

  public interface NodeTypeConsts {
    String PORT_MAP = "ports";
  }

  public interface NodeConsts {
    String PORT_ATTRS = "portAttrs";
  }

  public interface ConnectionConsts {
    String FROM = "from";
    String TO = "to";
  }
}
