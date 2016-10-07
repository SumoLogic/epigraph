package io.epigraph.service;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class Service {
  @NotNull
  private final String name;

  @NotNull
  private final Map<String, Resource> resources;

  public Service(@NotNull String name, @NotNull Collection<Resource> resources) throws ServiceInitializationException {
    this.name = name;

    this.resources = new HashMap<>();

    for (Resource resource : resources) {
      String resourceName = resource.declaration().fieldName();
      if (this.resources.containsKey(resourceName))
        throw new ServiceInitializationException("Resource '" + resourceName + "' is already registered"); // or merge?
      else
        this.resources.put(resourceName, resource);
    }
  }

  @NotNull
  public String name() { return name; }

  @NotNull
  public Map<String, Resource> resources() { return resources; }
}
