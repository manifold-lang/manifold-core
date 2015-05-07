package org.manifold.compiler.middle;

import java.util.HashMap;
import java.util.Map;

import org.manifold.compiler.NodeTypeValue;
import org.manifold.compiler.NodeValue;
import org.manifold.compiler.TypeMismatchException;
import org.manifold.compiler.TypeValue;
import org.manifold.compiler.UndeclaredAttributeException;
import org.manifold.compiler.UndeclaredIdentifierException;
import org.manifold.compiler.Value;

// Produces a back-annotated schematic, which has the same structure
// as an existing schematic but may have modifications to attributes.
public class BackAnnotationBuilder {

  private Schematic originalSchematic;

  public BackAnnotationBuilder(Schematic originalSchematic) {
    this.originalSchematic = originalSchematic;
  }

  // nodeAttributeAnnotations[nodeName][attrName] = attrValue
  private Map<String, Map<String, Value>> nodeAttributeAnnotations =
      new HashMap<>();

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

  public Schematic build() {
    throw new UnsupportedOperationException("not yet implemented");
  }

}
