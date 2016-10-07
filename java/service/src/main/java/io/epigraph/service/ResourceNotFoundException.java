package io.epigraph.service;

import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ResourceNotFoundException extends Exception {
  @NotNull
  private final String resourceName;

  public ResourceNotFoundException(@NotNull String resourceName) {
    super("Resource '" + resourceName + "' not found");
    this.resourceName = resourceName;
  }

  @NotNull
  public String resourceName() { return resourceName; }
}
