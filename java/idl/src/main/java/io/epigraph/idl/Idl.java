package io.epigraph.idl;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class Idl {
  @NotNull
  private final List<Resource> resources;

  public Idl(@NotNull List<Resource> resources) {this.resources = resources;}

  @NotNull
  public List<Resource> resources() { return resources; }
}
