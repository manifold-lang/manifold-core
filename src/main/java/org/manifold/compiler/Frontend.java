package org.manifold.compiler;

import org.manifold.compiler.middle.Schematic;

public interface Frontend {
  public String getFrontendName();
  public Schematic invokeFrontend(String args[]) throws Exception;
}
