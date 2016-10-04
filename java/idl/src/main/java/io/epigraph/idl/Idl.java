package io.epigraph.idl;

import io.epigraph.lang.Qn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class Idl {
  @NotNull
  private final Qn namespace;
  @NotNull
  private final List<Resource> resources;

  public Idl(@NotNull Qn namespace, @NotNull List<Resource> resources) {
    this.namespace = namespace;
    this.resources = resources;
  }

  @NotNull public Qn namespace() { return namespace; }

  @NotNull
  public List<Resource> resources() { return resources; }
}
