package org.manifold.compiler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;

public class TypeDependencyTree {

  class Node {
    private TypeValue type;
    public TypeValue getType() {
      return type;
    }
    
    private Set<Node> children;
    public Set<Node> getChildren() {
      return ImmutableSet.copyOf(children);
    }
    public void addChild(Node c) {
      children.add(c);
    }
    
    private Node parent;
    public Node getParent() {
      return parent;
    }
    
    public Node(TypeValue type) {
      this.type = checkNotNull(type);
      this.children = new HashSet<>();
      this.parent = null;
    }
    
    public Node(TypeValue type, Node parent) {
      this.type = checkNotNull(type);
      this.children = new HashSet<>();
      this.parent = parent;
      this.parent.addChild(this);
    }
    
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + getOuterType().hashCode();
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Node other = (Node) obj;
      if (!getOuterType().equals(other.getOuterType()))
        return false;
      if (type == null) {
        if (other.type != null)
          return false;
      } else if (!type.equals(other.type))
        return false;
      return true;
    }

    private TypeDependencyTree getOuterType() {
      return TypeDependencyTree.this;
    }
  }
  
  private Map<TypeValue, Node> nodes = new HashMap<>();
  private Node root;
  
  public TypeDependencyTree() {
    // all TypeValues should eventually inherit from TypeTypeValue
    Node ttv = new Node(TypeTypeValue.getInstance());
    nodes.put(TypeTypeValue.getInstance(), ttv);
    root = ttv;
  }
  
  public void addType(TypeValue type) {
    // each type appears in the graph exactly once
    if (nodes.containsKey(type)) {
      return;
    }
    TypeValue supertype = type.getSupertype();
    // TODO cycle detection
    addType(supertype);
    Node parent = nodes.get(supertype);
    Node child = new Node(type, parent);
    nodes.put(type, child);
  }
  
  private void dfsVisit(Consumer<TypeValue> f, Node n) {
    f.accept(n.getType());
    for (Node child : n.getChildren()) {
      dfsVisit(f, child);
    }
  }
  
  public void forEachDFS(Consumer<TypeValue> f) {
    // don't visit the root since TypeTypeValue is internal,
    // but hit everything else on the way down
    for (Node n : root.getChildren()) {
      dfsVisit(f, n);
    }
  }
  
}
