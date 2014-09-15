package org.manifold.compiler;

import org.manifold.compiler.middle.Schematic;

public interface Backend {
  public String getBackendName();
  public void invokeBackend(Schematic schematic, String args[]) 
      throws Exception;
}
