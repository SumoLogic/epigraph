package io.epigraph.idl;

import io.epigraph.lang.Qn;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class Idl {
  @NotNull
  private final Qn namespace;
  @NotNull
  private final Map<String, ResourceIdl> resources;

  public Idl(@NotNull Qn namespace, @NotNull Map<String, ResourceIdl> resources) {
    this.namespace = namespace;
    this.resources = resources;
  }

  @NotNull public Qn namespace() { return namespace; }

  public @NotNull Map<String, ResourceIdl> resources() { return resources; }
}
