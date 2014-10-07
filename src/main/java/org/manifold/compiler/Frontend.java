package org.manifold.compiler;

import org.manifold.compiler.middle.Schematic;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public interface Frontend {
  public String getFrontendName();
  public void registerArguments(Options options);
  public Schematic invokeFrontend(CommandLine cmdline) throws Exception;
}
