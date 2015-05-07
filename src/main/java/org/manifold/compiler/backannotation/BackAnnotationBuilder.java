package org.manifold.compiler.backannotation;

import org.manifold.compiler.Value;
import org.manifold.compiler.middle.Schematic;

// Produces a back-annotated schematic, which has the same structure
// as an existing schematic but may have modifications to attributes.
public class BackAnnotationBuilder {

  private Schematic originalSchematic;

  public BackAnnotationBuilder(Schematic originalSchematic) {
    this.originalSchematic = originalSchematic;
  }

  public void annotateNodeAttribute(
      String nodeName, String attrName, Value attrValue) {
    throw new UnsupportedOperationException("not yet implemented");
  }

  public Schematic build() {
    throw new UnsupportedOperationException("not yet implemented");
  }

}
