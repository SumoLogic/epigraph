/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.epigraph.service;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class Service {
  private final @NotNull String name;

  private final @NotNull Map<String, Resource> resources;

  public Service(@NotNull String name, @NotNull Iterable<Resource> resources) throws ServiceInitializationException {
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

  public @NotNull String name() { return name; }

  public @NotNull Map<String, Resource> resources() { return resources; }
}
