package io.epigraph.idl;

import io.epigraph.lang.Fqn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class Idl {
  @NotNull
  private final Fqn namespace;
  @NotNull
  private final List<Resource> resources;

  public Idl(@NotNull Fqn namespace, @NotNull List<Resource> resources) {
    this.namespace = namespace;
    this.resources = resources;
  }

  @NotNull public Fqn namespace() { return namespace; }

  @NotNull
  public List<Resource> resources() { return resources; }
}
