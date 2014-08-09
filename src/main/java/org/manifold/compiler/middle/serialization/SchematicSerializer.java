package org.manifold.compiler.middle.serialization;

import java.io.BufferedWriter;
import java.io.IOException;

import org.manifold.compiler.middle.Schematic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SchematicSerializer {
  public void serialize(Schematic sch, BufferedWriter out, boolean pretty)
      throws IOException {
    Gson gson;
    GsonBuilder builder = new GsonBuilder();

    if (pretty) {
      builder.setPrettyPrinting();
    }
    gson = builder.create();
    out.write(gson.toJson(sch));
    out.flush();
  }

  public void serialize(Schematic sch, BufferedWriter out) throws IOException {
    serialize(sch, out, false);
  }

}
