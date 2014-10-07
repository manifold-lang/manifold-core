package org.manifold.compiler;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.manifold.compiler.middle.Schematic;

public interface Backend {
  public String getBackendName();
  public void registerArguments(Options options);
  public void invokeBackend(Schematic schematic, CommandLine cmdline) 
      throws Exception;
}
