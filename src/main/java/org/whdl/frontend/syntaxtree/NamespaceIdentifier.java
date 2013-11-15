package org.whdl.frontend.syntaxtree;

import java.util.List;

public class NamespaceIdentifier {

  private List<String> name;

  public NamespaceIdentifier(List<String> name) {
    this.name = java.util.Collections.unmodifiableList(name);
  }

  public List<String> getName() {
    return name;
  }
}